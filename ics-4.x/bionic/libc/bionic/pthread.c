/*
 * Copyright (C) 2008 The Android Open Source Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
#include <sys/types.h>
#include <unistd.h>
#include <signal.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/atomics.h>
#include <bionic_tls.h>
#include <sys/mman.h>
#include <pthread.h>
#include <time.h>
#include "pthread_internal.h"
#include "thread_private.h"
#include <limits.h>
#include <memory.h>
#include <assert.h>
#include <malloc.h>
#include <bionic_futex.h>
#include <bionic_atomic_inline.h>
#include <sys/prctl.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <bionic_pthread.h>

extern int  __pthread_clone(int (*fn)(void*), void *child_stack, int flags, void *arg);
extern void _exit_with_stack_teardown(void * stackBase, int stackSize, int retCode);
extern void _exit_thread(int  retCode);
extern int  __set_errno(int);

int  __futex_wake_ex(volatile void *ftx, int pshared, int val)
{
    return __futex_syscall3(ftx, pshared ? FUTEX_WAKE : FUTEX_WAKE_PRIVATE, val);
}

int  __futex_wait_ex(volatile void *ftx, int pshared, int val, const struct timespec *timeout)
{
    return __futex_syscall4(ftx, pshared ? FUTEX_WAIT : FUTEX_WAIT_PRIVATE, val, timeout);
}

#define  __likely(cond)    __builtin_expect(!!(cond), 1)
#define  __unlikely(cond)  __builtin_expect(!!(cond), 0)

#ifdef __i386__
#define ATTRIBUTES __attribute__((noinline)) __attribute__((fastcall))
#else
#define ATTRIBUTES __attribute__((noinline))
#endif

void ATTRIBUTES _thread_created_hook(pid_t thread_id);

#define PTHREAD_ATTR_FLAG_DETACHED      0x00000001
#define PTHREAD_ATTR_FLAG_USER_STACK    0x00000002

#define DEFAULT_STACKSIZE (1024 * 1024)
#define STACKBASE 0x10000000

static uint8_t * gStackBase = (uint8_t *)STACKBASE;

static pthread_mutex_t mmap_lock = PTHREAD_MUTEX_INITIALIZER;


static const pthread_attr_t gDefaultPthreadAttr = {
    .flags = 0,
    .stack_base = NULL,
    .stack_size = DEFAULT_STACKSIZE,
    .guard_size = PAGE_SIZE,
    .sched_policy = SCHED_NORMAL,
    .sched_priority = 0
};

#define  INIT_THREADS  1

static pthread_internal_t*  gThreadList = NULL;
static pthread_mutex_t gThreadListLock = PTHREAD_MUTEX_INITIALIZER;
static pthread_mutex_t gDebuggerNotificationLock = PTHREAD_MUTEX_INITIALIZER;


/* we simply malloc/free the internal pthread_internal_t structures. we may
 * want to use a different allocation scheme in the future, but this one should
 * be largely enough
 */
static pthread_internal_t*
_pthread_internal_alloc(void)
{
    pthread_internal_t*   thread;

    thread = calloc( sizeof(*thread), 1 );
    if (thread)
        thread->intern = 1;

    return thread;
}

static void
_pthread_internal_free( pthread_internal_t*  thread )
{
    if (thread && thread->intern) {
        thread->intern = 0;  /* just in case */
        free (thread);
    }
}


static void
_pthread_internal_remove_locked( pthread_internal_t*  thread )
{
    thread->next->pref = thread->pref;
    thread->pref[0]    = thread->next;
}

static void
_pthread_internal_remove( pthread_internal_t*  thread )
{
    pthread_mutex_lock(&gThreadListLock);
    _pthread_internal_remove_locked(thread);
    pthread_mutex_unlock(&gThreadListLock);
}

static void
_pthread_internal_add( pthread_internal_t*  thread )
{
    pthread_mutex_lock(&gThreadListLock);
    thread->pref = &gThreadList;
    thread->next = thread->pref[0];
    if (thread->next)
        thread->next->pref = &thread->next;
    thread->pref[0] = thread;
    pthread_mutex_unlock(&gThreadListLock);
}

pthread_internal_t*
__get_thread(void)
{
    void**  tls = (void**)__get_tls();

    return  (pthread_internal_t*) tls[TLS_SLOT_THREAD_ID];
}


void*
__get_stack_base(int  *p_stack_size)
{
    pthread_internal_t*  thread = __get_thread();

    *p_stack_size = thread->attr.stack_size;
    return thread->attr.stack_base;
}


void  __init_tls(void**  tls, void*  thread)
{
    int  nn;

    ((pthread_internal_t*)thread)->tls = tls;

    // slot 0 must point to the tls area, this is required by the implementation
    // of the x86 Linux kernel thread-local-storage
    tls[TLS_SLOT_SELF]      = (void*)tls;
    tls[TLS_SLOT_THREAD_ID] = thread;
    for (nn = TLS_SLOT_ERRNO; nn < BIONIC_TLS_SLOTS; nn++)
       tls[nn] = 0;

    __set_tls( (void*)tls );
}


/*
 * This trampoline is called from the assembly clone() function
 */
void __thread_entry(int (*func)(void*), void *arg, void **tls)
{
    int retValue;
    pthread_internal_t * thrInfo;

    // Wait for our creating thread to release us. This lets it have time to
    // notify gdb about this thread before it starts doing anything.
    //
    // This also provides the memory barrier needed to ensure that all memory
    // accesses previously made by the creating thread are visible to us.
    pthread_mutex_t * start_mutex = (pthread_mutex_t *)&tls[TLS_SLOT_SELF];
    pthread_mutex_lock(start_mutex);
    pthread_mutex_destroy(start_mutex);

    thrInfo = (pthread_internal_t *) tls[TLS_SLOT_THREAD_ID];

    __init_tls( tls, thrInfo );

    pthread_exit( (void*)func(arg) );
}

void _init_thread(pthread_internal_t * thread, pid_t kernel_id, pthread_attr_t * attr, void * stack_base)
{
    if (attr == NULL) {
        thread->attr = gDefaultPthreadAttr;
    } else {
        thread->attr = *attr;
    }
    thread->attr.stack_base = stack_base;
    thread->kernel_id       = kernel_id;

    // set the scheduling policy/priority of the thread
    if (thread->attr.sched_policy != SCHED_NORMAL) {
        struct sched_param param;
        param.sched_priority = thread->attr.sched_priority;
        sched_setscheduler(kernel_id, thread->attr.sched_policy, &param);
    }

    pthread_cond_init(&thread->join_cond, NULL);
    thread->join_count = 0;

    thread->cleanup_stack = NULL;

    _pthread_internal_add(thread);
}


/* XXX stacks not reclaimed if thread spawn fails */
/* XXX stacks address spaces should be reused if available again */

static void *mkstack(size_t size, size_t guard_size)
{
    void * stack;

    pthread_mutex_lock(&mmap_lock);

    stack = mmap((void *)gStackBase, size,
                 PROT_READ | PROT_WRITE,
                 MAP_PRIVATE | MAP_ANONYMOUS | MAP_NORESERVE,
                 -1, 0);

    if(stack == MAP_FAILED) {
        stack = NULL;
        goto done;
    }

    if(mprotect(stack, guard_size, PROT_NONE)){
        munmap(stack, size);
        stack = NULL;
        goto done;
    }

done:
    pthread_mutex_unlock(&mmap_lock);
    return stack;
}

/*
 * Create a new thread. The thread's stack is laid out like so:
 *
 * +---------------------------+
 * |     pthread_internal_t    |
 * +---------------------------+
 * |                           |
 * |          TLS area         |
 * |                           |
 * +---------------------------+
 * |                           |
 * .                           .
 * .         stack area        .
 * .                           .
 * |                           |
 * +---------------------------+
 * |         guard page        |
 * +---------------------------+
 *
 *  note that TLS[0] must be a pointer to itself, this is required
 *  by the thread-local storage implementation of the x86 Linux
 *  kernel, where the TLS pointer is read by reading fs:[0]
 */
int pthread_create(pthread_t *thread_out, pthread_attr_t const * attr,
                   void *(*start_routine)(void *), void * arg)
{
    char*   stack;
    void**  tls;
    int tid;
    pthread_mutex_t * start_mutex;
    pthread_internal_t * thread;
    int                  madestack = 0;
    int     old_errno = errno;

    /* this will inform the rest of the C library that at least one thread
     * was created. this will enforce certain functions to acquire/release
     * locks (e.g. atexit()) to protect shared global structures.
     *
     * this works because pthread_create() is not called by the C library
     * initialization routine that sets up the main thread's data structures.
     */
    __isthreaded = 1;

    thread = _pthread_internal_alloc();
    if (thread == NULL)
        return ENOMEM;

    if (attr == NULL) {
        attr = &gDefaultPthreadAttr;
    }

    // make sure the stack is PAGE_SIZE aligned
    size_t stackSize = (attr->stack_size +
                        (PAGE_SIZE-1)) & ~(PAGE_SIZE-1);

    if (!attr->stack_base) {
        stack = mkstack(stackSize, attr->guard_size);
        if(stack == NULL) {
            _pthread_internal_free(thread);
            return ENOMEM;
        }
        madestack = 1;
    } else {
        stack = attr->stack_base;
    }

    // Make room for TLS
    tls = (void**)(stack + stackSize - BIONIC_TLS_SLOTS*sizeof(void*));

    // Create a mutex for the thread in TLS_SLOT_SELF to wait on once it starts so we can keep
    // it from doing anything until after we notify the debugger about it
    //
    // This also provides the memory barrier we need to ensure that all
    // memory accesses previously performed by this thread are visible to
    // the new thread.
    start_mutex = (pthread_mutex_t *) &tls[TLS_SLOT_SELF];
    pthread_mutex_init(start_mutex, NULL);
    pthread_mutex_lock(start_mutex);

    tls[TLS_SLOT_THREAD_ID] = thread;

    tid = __pthread_clone((int(*)(void*))start_routine, tls,
                CLONE_FILES | CLONE_FS | CLONE_VM | CLONE_SIGHAND
                | CLONE_THREAD | CLONE_SYSVSEM | CLONE_DETACHED,
                arg);

    if(tid < 0) {
        int  result;
        if (madestack)
            munmap(stack, stackSize);
        _pthread_internal_free(thread);
        result = errno;
        errno = old_errno;
        return result;
    }

    _init_thread(thread, tid, (pthread_attr_t*)attr, stack);

    if (!madestack)
        thread->attr.flags |= PTHREAD_ATTR_FLAG_USER_STACK;

    // Notify any debuggers about the new thread
    pthread_mutex_lock(&gDebuggerNotificationLock);
    _thread_created_hook(tid);
    pthread_mutex_unlock(&gDebuggerNotificationLock);

    // Let the thread do it's thing
    pthread_mutex_unlock(start_mutex);

    *thread_out = (pthread_t)thread;
    return 0;
}


int pthread_attr_init(pthread_attr_t * attr)
{
    *attr = gDefaultPthreadAttr;
    return 0;
}

int pthread_attr_destroy(pthread_attr_t * attr)
{
    memset(attr, 0x42, sizeof(pthread_attr_t));
    return 0;
}

int pthread_attr_setdetachstate(pthread_attr_t * attr, int state)
{
    if (state == PTHREAD_CREATE_DETACHED) {
        attr->flags |= PTHREAD_ATTR_FLAG_DETACHED;
    } else if (state == PTHREAD_CREATE_JOINABLE) {
        attr->flags &= ~PTHREAD_ATTR_FLAG_DETACHED;
    } else {
        return EINVAL;
    }
    return 0;
}

int pthread_attr_getdetachstate(pthread_attr_t const * attr, int * state)
{
    *state = (attr->flags & PTHREAD_ATTR_FLAG_DETACHED)
           ? PTHREAD_CREATE_DETACHED
           : PTHREAD_CREATE_JOINABLE;
    return 0;
}

int pthread_attr_setschedpolicy(pthread_attr_t * attr, int policy)
{
    attr->sched_policy = policy;
    return 0;
}

int pthread_attr_getschedpolicy(pthread_attr_t const * attr, int * policy)
{
    *policy = attr->sched_policy;
    return 0;
}

int pthread_attr_setschedparam(pthread_attr_t * attr, struct sched_param const * param)
{
    attr->sched_priority = param->sched_priority;
    return 0;
}

int pthread_attr_getschedparam(pthread_attr_t const * attr, struct sched_param * param)
{
    param->sched_priority = attr->sched_priority;
    return 0;
}

int pthread_attr_setstacksize(pthread_attr_t * attr, size_t stack_size)
{
    if ((stack_size & (PAGE_SIZE - 1) || stack_size < PTHREAD_STACK_MIN)) {
        return EINVAL;
    }
    attr->stack_size = stack_size;
    return 0;
}

int pthread_attr_getstacksize(pthread_attr_t const * attr, size_t * stack_size)
{
    *stack_size = attr->stack_size;
    return 0;
}

int pthread_attr_setstackaddr(pthread_attr_t * attr, void * stack_addr)
{
#if 1
    // It's not clear if this is setting the top or bottom of the stack, so don't handle it for now.
    return ENOSYS;
#else
    if ((uint32_t)stack_addr & (PAGE_SIZE - 1)) {
        return EINVAL;
    }
    attr->stack_base = stack_addr;
    return 0;
#endif
}

int pthread_attr_getstackaddr(pthread_attr_t const * attr, void ** stack_addr)
{
    *stack_addr = (char*)attr->stack_base + attr->stack_size;
    return 0;
}

int pthread_attr_setstack(pthread_attr_t * attr, void * stack_base, size_t stack_size)
{
    if ((stack_size & (PAGE_SIZE - 1) || stack_size < PTHREAD_STACK_MIN)) {
        return EINVAL;
    }
    if ((uint32_t)stack_base & (PAGE_SIZE - 1)) {
        return EINVAL;
    }
    attr->stack_base = stack_base;
    attr->stack_size = stack_size;
    return 0;
}

int pthread_attr_getstack(pthread_attr_t const * attr, void ** stack_base, size_t * stack_size)
{
    *stack_base = attr->stack_base;
    *stack_size = attr->stack_size;
    return 0;
}

int pthread_attr_setguardsize(pthread_attr_t * attr, size_t guard_size)
{
    if (guard_size & (PAGE_SIZE - 1) || guard_size < PAGE_SIZE) {
        return EINVAL;
    }

    attr->guard_size = guard_size;
    return 0;
}

int pthread_attr_getguardsize(pthread_attr_t const * attr, size_t * guard_size)
{
    *guard_size = attr->guard_size;
    return 0;
}

int pthread_getattr_np(pthread_t thid, pthread_attr_t * attr)
{
    pthread_internal_t * thread = (pthread_internal_t *)thid;
    *attr = thread->attr;
    return 0;
}

int pthread_attr_setscope(pthread_attr_t *attr, int  scope)
{
    if (scope == PTHREAD_SCOPE_SYSTEM)
        return 0;
    if (scope == PTHREAD_SCOPE_PROCESS)
        return ENOTSUP;

    return EINVAL;
}

int pthread_attr_getscope(pthread_attr_t const *attr)
{
    return PTHREAD_SCOPE_SYSTEM;
}


/* CAVEAT: our implementation of pthread_cleanup_push/pop doesn't support C++ exceptions
 *         and thread cancelation
 */

void __pthread_cleanup_push( __pthread_cleanup_t*      c,
                             __pthread_cleanup_func_t  routine,
                             void*                     arg )
{
    pthread_internal_t*  thread = __get_thread();

    c->__cleanup_routine  = routine;
    c->__cleanup_arg      = arg;
    c->__cleanup_prev     = thread->cleanup_stack;
    thread->cleanup_stack = c;
}

void __pthread_cleanup_pop( __pthread_cleanup_t*  c, int  execute )
{
    pthread_internal_t*  thread = __get_thread();

    thread->cleanup_stack = c->__cleanup_prev;
    if (execute)
        c->__cleanup_routine(c->__cleanup_arg);
}

#ifdef UCLIBC_LINUXTHREAD_OLD_EXTENSTION /* uClibc linuxthread.old extension define */


int pthread_setcancelstate(int state, int * oldstate)
{
    
    pthread_internal_t*  thread     = __get_thread();
    int *retval;

    if (state < PTHREAD_CANCEL_ENABLE || state > PTHREAD_CANCEL_DISABLE)
       return EINVAL;
    if (oldstate != NULL) *oldstate = thread->p_cancelstate;
    thread->p_cancelstate = state;
    retval = PTHREAD_CANCELED;
    if (thread->p_canceled &&
        thread->p_cancelstate == PTHREAD_CANCEL_ENABLE &&
        thread->p_canceltype == PTHREAD_CANCEL_ASYNCHRONOUS) {
        /*__pthread_do_exit(PTHREAD_CANCELED, CURRENT_STACK_FRAME); */
        pthread_exit(retval);
    }

    return 0;
}

int pthread_setcanceltype(int type, int * oldtype)
{

    pthread_internal_t*  thread     = __get_thread();
    int *retval;

    if (type < PTHREAD_CANCEL_DEFERRED || type > PTHREAD_CANCEL_ASYNCHRONOUS)
       return EINVAL;
    if (oldtype != NULL) *oldtype = thread->p_canceltype;
    thread->p_cancelstate = type;
    retval = PTHREAD_CANCELED;
    if (thread->p_canceled &&
        thread->p_cancelstate == PTHREAD_CANCEL_ENABLE &&
        thread->p_canceltype == PTHREAD_CANCEL_ASYNCHRONOUS) {
        /*__pthread_do_exit(PTHREAD_CANCELED, CURRENT_STACK_FRAME); */
        pthread_exit(retval);
    }

    return 0;
}


int pthread_cancel(pthread_t thid)
{
    pthread_internal_t*  thread;
    int                  result = 0;
    int                  flags;
    int                  already_canceled;
    int                  __pthread_sig_cancel = SIGUSR2;

    pthread_mutex_lock(&gThreadListLock);
    for (thread = gThreadList; thread != NULL; thread = thread->next)
        if (thread == (pthread_internal_t*)thid)
            goto FoundIt;

    result = ESRCH;
    goto Exit;

FoundIt:
    already_canceled = thread->p_canceled;
    thread->p_canceled = 1;
    
  if (thread->p_cancelstate == PTHREAD_CANCEL_DISABLE || already_canceled) {
    pthread_mutex_unlock(&gThreadListLock);
    return 0;
  }

Exit:
    pthread_mutex_unlock(&gThreadListLock);
  /* If the thread has suspended or is about to, then we unblock it by
     issuing a restart, instead of a cancel signal. Otherwise we send
     the cancel signal to unblock the thread from a cancellation point,
     or to initiate asynchronous cancellation. The restart is needed so
     we have proper accounting of restarts; suspend decrements the thread's
     resume count, and restart() increments it.  This also means that suspend's
     handling of the cancel signal is obsolete. */

    pthread_kill(thid, __pthread_sig_cancel);

    return result;
}

void pthread_testcancel(void)
{
    pthread_internal_t*  thread     = __get_thread();
    int *retval;
  
    retval = PTHREAD_CANCELED;
    if (thread->p_canceled &&
        thread->p_cancelstate == PTHREAD_CANCEL_ENABLE ) {
        /*__pthread_do_exit(PTHREAD_CANCELED, CURRENT_STACK_FRAME); */
        pthread_exit(retval);
    }

    return;
}


/*static pthread_mutex_t pthread_atfork_lock = PTHREAD_MUTEX_INITIALIZER;
static struct handler_list * pthread_atfork_prepare = NULL;
static struct handler_list * pthread_atfork_parent = NULL;
static struct handler_list * pthread_atfork_child = NULL;

struct handler_list {
  void (*handler)(void);
  struct handler_list * next;
};

static void pthread_insert_list(struct handler_list ** list,
                                void (*handler)(void),
                                struct handler_list * newlist,
                                int at_end)
{
  if (handler == NULL) return;
  if (at_end) {
    while(*list != NULL) list = &((*list)->next);
  }
  newlist->handler = handler;
  newlist->next = *list;
  *list = newlist;
}

struct handler_list_block {
  struct handler_list prepare, parent, child;
};

int pthread_atfork(void (*prepare)(void),
		     void (*parent)(void),
		     void (*child)(void))
{
  struct handler_list_block * block =
    (struct handler_list_block *) malloc(sizeof(struct handler_list_block));
  if (block == NULL) return ENOMEM;
  pthread_mutex_lock(&pthread_atfork_lock);
  // "prepare" handlers are called in LIFO 
  pthread_insert_list(&pthread_atfork_prepare, prepare, &block->prepare, 0);
  // "parent" handlers are called in FIFO
  pthread_insert_list(&pthread_atfork_parent, parent, &block->parent, 1);
  // "child" handlers are called in FIFO
  pthread_insert_list(&pthread_atfork_child, child, &block->child, 1);
  pthread_mutex_unlock(&pthread_atfork_lock);
  return 0;
}

static __inline__ void pthread_call_handlers(struct handler_list * list)
{
  for (; list != NULL; list = list->next) (list->handler)();
}

void __pthread_once_fork_prepare(void);
void __pthread_once_fork_child(void);
void __pthread_once_fork_parent(void);

void __pthread_kill_other_threads_np(void)
{
     // FIXME
}
*/

#endif // uClibc linuxthread.old extension define

/* used by pthread_exit() to clean all TLS keys of the current thread */
static void pthread_key_clean_all(void);

void pthread_exit(void * retval)
{
    pthread_internal_t*  thread     = __get_thread();
    void*                stack_base = thread->attr.stack_base;
    int                  stack_size = thread->attr.stack_size;
    int                  user_stack = (thread->attr.flags & PTHREAD_ATTR_FLAG_USER_STACK) != 0;

    // call the cleanup handlers first
    while (thread->cleanup_stack) {
        __pthread_cleanup_t*  c = thread->cleanup_stack;
        thread->cleanup_stack   = c->__cleanup_prev;
        c->__cleanup_routine(c->__cleanup_arg);
    }

    // call the TLS destructors, it is important to do that before removing this
    // thread from the global list. this will ensure that if someone else deletes
    // a TLS key, the corresponding value will be set to NULL in this thread's TLS
    // space (see pthread_key_delete)
    pthread_key_clean_all();

    // if the thread is detached, destroy the pthread_internal_t
    // otherwise, keep it in memory and signal any joiners
    if (thread->attr.flags & PTHREAD_ATTR_FLAG_DETACHED) {
        _pthread_internal_remove(thread);
        _pthread_internal_free(thread);
    } else {
       /* the join_count field is used to store the number of threads waiting for
        * the termination of this thread with pthread_join(),
        *
        * if it is positive we need to signal the waiters, and we do not touch
        * the count (it will be decremented by the waiters, the last one will
        * also remove/free the thread structure
        *
        * if it is zero, we set the count value to -1 to indicate that the
        * thread is in 'zombie' state: it has stopped executing, and its stack
        * is gone (as well as its TLS area). when another thread calls pthread_join()
        * on it, it will immediately free the thread and return.
        */
        pthread_mutex_lock(&gThreadListLock);
        thread->return_value = retval;
        if (thread->join_count > 0) {
            pthread_cond_broadcast(&thread->join_cond);
        } else {
            thread->join_count = -1;  /* zombie thread */
        }
        pthread_mutex_unlock(&gThreadListLock);
    }

    // destroy the thread stack
    if (user_stack)
        _exit_thread((int)retval);
    else
        _exit_with_stack_teardown(stack_base, stack_size, (int)retval);
}

int pthread_join(pthread_t thid, void ** ret_val)
{
    pthread_internal_t*  thread = (pthread_internal_t*)thid;
    int                  count;

    // check that the thread still exists and is not detached
    pthread_mutex_lock(&gThreadListLock);

    for (thread = gThreadList; thread != NULL; thread = thread->next)
        if (thread == (pthread_internal_t*)thid)
            break;

    if (!thread) {
        pthread_mutex_unlock(&gThreadListLock);
        return ESRCH;
    }

    if (thread->attr.flags & PTHREAD_ATTR_FLAG_DETACHED) {
        pthread_mutex_unlock(&gThreadListLock);
        return EINVAL;
    }

   /* wait for thread death when needed
    *
    * if the 'join_count' is negative, this is a 'zombie' thread that
    * is already dead and without stack/TLS
    *
    * otherwise, we need to increment 'join-count' and wait to be signaled
    */
   count = thread->join_count;
    if (count >= 0) {
        thread->join_count += 1;
        pthread_cond_wait( &thread->join_cond, &gThreadListLock );
        count = --thread->join_count;
    }
    if (ret_val)
        *ret_val = thread->return_value;

    /* remove thread descriptor when we're the last joiner or when the
     * thread was already a zombie.
     */
    if (count <= 0) {
        _pthread_internal_remove_locked(thread);
        _pthread_internal_free(thread);
    }
    pthread_mutex_unlock(&gThreadListLock);
    return 0;
}

int  pthread_detach( pthread_t  thid )
{
    pthread_internal_t*  thread;
    int                  result = 0;
    int                  flags;

    pthread_mutex_lock(&gThreadListLock);
    for (thread = gThreadList; thread != NULL; thread = thread->next)
        if (thread == (pthread_internal_t*)thid)
            goto FoundIt;

    result = ESRCH;
    goto Exit;

FoundIt:
    do {
        flags = thread->attr.flags;

        if ( flags & PTHREAD_ATTR_FLAG_DETACHED ) {
            /* thread is not joinable ! */
            result = EINVAL;
            goto Exit;
        }
    }
    while ( __atomic_cmpxchg( flags, flags | PTHREAD_ATTR_FLAG_DETACHED,
                              (volatile int*)&thread->attr.flags ) != 0 );
Exit:
    pthread_mutex_unlock(&gThreadListLock);
    return result;
}

pthread_t pthread_self(void)
{
    return (pthread_t)__get_thread();
}

int pthread_equal(pthread_t one, pthread_t two)
{
    return (one == two ? 1 : 0);
}

int pthread_getschedparam(pthread_t thid, int * policy,
                          struct sched_param * param)
{
    int  old_errno = errno;

    pthread_internal_t * thread = (pthread_internal_t *)thid;
    int err = sched_getparam(thread->kernel_id, param);
    if (!err) {
        *policy = sched_getscheduler(thread->kernel_id);
    } else {
        err = errno;
        errno = old_errno;
    }
    return err;
}

int pthread_setschedparam(pthread_t thid, int policy,
                          struct sched_param const * param)
{
    pthread_internal_t * thread = (pthread_internal_t *)thid;
    int                  old_errno = errno;
    int                  ret;

    ret = sched_setscheduler(thread->kernel_id, policy, param);
    if (ret < 0) {
        ret = errno;
        errno = old_errno;
    }
    return ret;
}


//int __futex_wait(volatile void *ftx, int val, const struct timespec *timeout);
//int __futex_wake(volatile void *ftx, int count);

// mutex lock states
//
// 0: unlocked
// 1: locked, no waiters
// 2: locked, maybe waiters

/* a mutex is implemented as a 32-bit integer holding the following fields
 *
 * bits:     name     description
 * 31-16     tid      owner thread's kernel id (recursive and errorcheck only)
 * 15-14     type     mutex type
 * 13-2      counter  counter of recursive mutexes
 * 1-0       state    lock state (0, 1 or 2)
 */


#define  MUTEX_OWNER(m)  (((m)->value >> 16) & 0xffff)
#define  MUTEX_COUNTER(m) (((m)->value >> 2) & 0xfff)

#define  MUTEX_TYPE_MASK       0xc000
#define  MUTEX_TYPE_NORMAL     0x0000
#define  MUTEX_TYPE_RECURSIVE  0x4000
#define  MUTEX_TYPE_ERRORCHECK 0x8000

#define  MUTEX_COUNTER_SHIFT  2
#define  MUTEX_COUNTER_MASK   0x3ffc




int pthread_mutexattr_init(pthread_mutexattr_t *attr)
{
    if (attr) {
        *attr = PTHREAD_MUTEX_DEFAULT;
        return 0;
    } else {
        return EINVAL;
    }
}

int pthread_mutexattr_destroy(pthread_mutexattr_t *attr)
{
    if (attr) {
        *attr = -1;
        return 0;
    } else {
        return EINVAL;
    }
}

int pthread_mutexattr_gettype(const pthread_mutexattr_t *attr, int *type)
{
    if (attr && *attr >= PTHREAD_MUTEX_NORMAL &&
                *attr <= PTHREAD_MUTEX_ERRORCHECK ) {
        *type = *attr;
        return 0;
    }
    return EINVAL;
}

int pthread_mutexattr_settype(pthread_mutexattr_t *attr, int type)
{
    if (attr && type >= PTHREAD_MUTEX_NORMAL &&
                type <= PTHREAD_MUTEX_ERRORCHECK ) {
        *attr = type;
        return 0;
    }
    return EINVAL;
}

/* process-shared mutexes are not supported at the moment */

int pthread_mutexattr_setpshared(pthread_mutexattr_t *attr, int  pshared)
{
    if (!attr)
        return EINVAL;

    switch (pshared) {
    case PTHREAD_PROCESS_PRIVATE:
    case PTHREAD_PROCESS_SHARED:
        /* our current implementation of pthread actually supports shared
         * mutexes but won't cleanup if a process dies with the mutex held.
         * Nevertheless, it's better than nothing. Shared mutexes are used
         * by surfaceflinger and audioflinger.
         */
        return 0;
    }

    return ENOTSUP;
}

int pthread_mutexattr_getpshared(pthread_mutexattr_t *attr, int *pshared)
{
    if (!attr)
        return EINVAL;

    *pshared = PTHREAD_PROCESS_PRIVATE;
    return 0;
}

int pthread_mutex_init(pthread_mutex_t *mutex,
                       const pthread_mutexattr_t *attr)
{
    if ( mutex ) {
        if (attr == NULL) {
            mutex->value = MUTEX_TYPE_NORMAL;
            return 0;
        }
        switch ( *attr ) {
        case PTHREAD_MUTEX_NORMAL:
            mutex->value = MUTEX_TYPE_NORMAL;
            return 0;

        case PTHREAD_MUTEX_RECURSIVE:
            mutex->value = MUTEX_TYPE_RECURSIVE;
            return 0;

        case PTHREAD_MUTEX_ERRORCHECK:
            mutex->value = MUTEX_TYPE_ERRORCHECK;
            return 0;
        }
    }
    return EINVAL;
}

int pthread_mutex_destroy(pthread_mutex_t *mutex)
{
    int ret;

    /* use trylock to ensure that the mutex value is
     * valid and is not already locked. */
    ret = pthread_mutex_trylock(mutex);
    if (ret != 0)
        return ret;

    mutex->value = 0xdead10cc;
    return 0;
}


/*
 * Lock a non-recursive mutex.
 *
 * As noted above, there are three states:
 *   0 (unlocked, no contention)
 *   1 (locked, no contention)
 *   2 (locked, contention)
 *
 * Non-recursive mutexes don't use the thread-id or counter fields, and the
 * "type" value is zero, so the only bits that will be set are the ones in
 * the lock state field.
 */
static __inline__ void
_normal_lock(pthread_mutex_t*  mutex)
{
    /*
     * The common case is an unlocked mutex, so we begin by trying to
     * change the lock's state from 0 to 1.  __atomic_cmpxchg() returns 0
     * if it made the swap successfully.  If the result is nonzero, this
     * lock is already held by another thread.
     */
    if (__atomic_cmpxchg(0, 1, &mutex->value ) != 0) {
        /*
         * We want to go to sleep until the mutex is available, which
         * requires promoting it to state 2.  We need to swap in the new
         * state value and then wait until somebody wakes us up.
         *
         * __atomic_swap() returns the previous value.  We swap 2 in and
         * see if we got zero back; if so, we have acquired the lock.  If
         * not, another thread still holds the lock and we wait again.
         *
         * The second argument to the __futex_wait() call is compared
         * against the current value.  If it doesn't match, __futex_wait()
         * returns immediately (otherwise, it sleeps for a time specified
         * by the third argument; 0 means sleep forever).  This ensures
         * that the mutex is in state 2 when we go to sleep on it, which
         * guarantees a wake-up call.
         */
        while (__atomic_swap(2, &mutex->value ) != 0)
            __futex_wait(&mutex->value, 2, 0);
    }
    ANDROID_MEMBAR_FULL();
}

/*
 * Release a non-recursive mutex.  The caller is responsible for determining
 * that we are in fact the owner of this lock.
 */
static __inline__ void
_normal_unlock(pthread_mutex_t*  mutex)
{
    ANDROID_MEMBAR_FULL();

    /*
     * The mutex state will be 1 or (rarely) 2.  We use an atomic decrement
     * to release the lock.  __atomic_dec() returns the previous value;
     * if it wasn't 1 we have to do some additional work.
     */
    if (__atomic_dec(&mutex->value) != 1) {
        /*
         * Start by releasing the lock.  The decrement changed it from
         * "contended lock" to "uncontended lock", which means we still
         * hold it, and anybody who tries to sneak in will push it back
         * to state 2.
         *
         * Once we set it to zero the lock is up for grabs.  We follow
         * this with a __futex_wake() to ensure that one of the waiting
         * threads has a chance to grab it.
         *
         * This doesn't cause a race with the swap/wait pair in
         * _normal_lock(), because the __futex_wait() call there will
         * return immediately if the mutex value isn't 2.
         */
        mutex->value = 0;

        /*
         * Wake up one waiting thread.  We don't know which thread will be
         * woken or when it'll start executing -- futexes make no guarantees
         * here.  There may not even be a thread waiting.
         *
         * The newly-woken thread will replace the 0 we just set above
         * with 2, which means that when it eventually releases the mutex
         * it will also call FUTEX_WAKE.  This results in one extra wake
         * call whenever a lock is contended, but lets us avoid forgetting
         * anyone without requiring us to track the number of sleepers.
         *
         * It's possible for another thread to sneak in and grab the lock
         * between the zero assignment above and the wake call below.  If
         * the new thread is "slow" and holds the lock for a while, we'll
         * wake up a sleeper, which will swap in a 2 and then go back to
         * sleep since the lock is still held.  If the new thread is "fast",
         * running to completion before we call wake, the thread we
         * eventually wake will find an unlocked mutex and will execute.
         * Either way we have correct behavior and nobody is orphaned on
         * the wait queue.
         */
        __futex_wake(&mutex->value, 1);
    }
}

static pthread_mutex_t  __recursive_lock = PTHREAD_MUTEX_INITIALIZER;

static void
_recursive_lock(void)
{
    _normal_lock( &__recursive_lock);
}

static void
_recursive_unlock(void)
{
    _normal_unlock( &__recursive_lock );
}

int pthread_mutex_lock(pthread_mutex_t *mutex)
{
    if (__likely(mutex != NULL))
    {
        int  mtype = (mutex->value & MUTEX_TYPE_MASK);

        if ( __likely(mtype == MUTEX_TYPE_NORMAL) ) {
            _normal_lock(mutex);
        }
        else
        {
            int  tid = __get_thread()->kernel_id;

            if ( tid == MUTEX_OWNER(mutex) )
            {
                int  oldv, counter;

                if (mtype == MUTEX_TYPE_ERRORCHECK) {
                    /* trying to re-lock a mutex we already acquired */
                    return EDEADLK;
                }
                /*
                 * We own the mutex, but other threads are able to change
                 * the contents (e.g. promoting it to "contended"), so we
                 * need to hold the global lock.
                 */
                _recursive_lock();
                oldv         = mutex->value;
                counter      = (oldv + (1 << MUTEX_COUNTER_SHIFT)) & MUTEX_COUNTER_MASK;
                mutex->value = (oldv & ~MUTEX_COUNTER_MASK) | counter;
                _recursive_unlock();
            }
            else
            {
                /*
                 * If the new lock is available immediately, we grab it in
                 * the "uncontended" state.
                 */
                int new_lock_type = 1;

                for (;;) {
                    int  oldv;

                    _recursive_lock();
                    oldv = mutex->value;
                    if (oldv == mtype) { /* uncontended released lock => 1 or 2 */
                        mutex->value = ((tid << 16) | mtype | new_lock_type);
                    } else if ((oldv & 3) == 1) { /* locked state 1 => state 2 */
                        oldv ^= 3;
                        mutex->value = oldv;
                    }
                    _recursive_unlock();

                    if (oldv == mtype)
                        break;

                    /*
                     * The lock was held, possibly contended by others.  From
                     * now on, if we manage to acquire the lock, we have to
                     * assume that others are still contending for it so that
                     * we'll wake them when we unlock it.
                     */
                    new_lock_type = 2;

                    __futex_wait( &mutex->value, oldv, 0 );
                }
            }
        }
        return 0;
    }
    return EINVAL;
}


int pthread_mutex_unlock(pthread_mutex_t *mutex)
{
    if (__likely(mutex != NULL))
    {
        int  mtype = (mutex->value & MUTEX_TYPE_MASK);

        if (__likely(mtype == MUTEX_TYPE_NORMAL)) {
            _normal_unlock(mutex);
        }
        else
        {
            int  tid = __get_thread()->kernel_id;

            if ( tid == MUTEX_OWNER(mutex) )
            {
                int  oldv;

                _recursive_lock();
                oldv = mutex->value;
                if (oldv & MUTEX_COUNTER_MASK) {
                    mutex->value = oldv - (1 << MUTEX_COUNTER_SHIFT);
                    oldv = 0;
                } else {
                    mutex->value = mtype;
                }
                _recursive_unlock();

                if ((oldv & 3) == 2)
                    __futex_wake( &mutex->value, 1 );
            }
            else {
                /* trying to unlock a lock we do not own */
                return EPERM;
            }
        }
        return 0;
    }
    return EINVAL;
}


int pthread_mutex_trylock(pthread_mutex_t *mutex)
{
    if (__likely(mutex != NULL))
    {
        int  mtype = (mutex->value & MUTEX_TYPE_MASK);

        if ( __likely(mtype == MUTEX_TYPE_NORMAL) )
        {
            if (__atomic_cmpxchg(0, 1, &mutex->value) == 0) {
                ANDROID_MEMBAR_FULL();
                return 0;
            }

            return EBUSY;
        }
        else
        {
            int  tid = __get_thread()->kernel_id;
            int  oldv;

            if ( tid == MUTEX_OWNER(mutex) )
            {
                int  oldv, counter;

                if (mtype == MUTEX_TYPE_ERRORCHECK) {
                    /* already locked by ourselves */
                    return EDEADLK;
                }

                _recursive_lock();
                oldv = mutex->value;
                counter = (oldv + (1 << MUTEX_COUNTER_SHIFT)) & MUTEX_COUNTER_MASK;
                mutex->value = (oldv & ~MUTEX_COUNTER_MASK) | counter;
                _recursive_unlock();
                return 0;
            }

            /* try to lock it */
            _recursive_lock();
            oldv = mutex->value;
            if (oldv == mtype)  /* uncontended released lock => state 1 */
                mutex->value = ((tid << 16) | mtype | 1);
            _recursive_unlock();

            if (oldv != mtype)
                return EBUSY;

            return 0;
        }
    }
    return EINVAL;
}


/* initialize 'ts' with the difference between 'abstime' and the current time
 * according to 'clock'. Returns -1 if abstime already expired, or 0 otherwise.
 */
static int
__timespec_to_absolute(struct timespec*  ts, const struct timespec*  abstime, clockid_t  clock)
{
    clock_gettime(clock, ts);
    ts->tv_sec  = abstime->tv_sec - ts->tv_sec;
    ts->tv_nsec = abstime->tv_nsec - ts->tv_nsec;
    if (ts->tv_nsec < 0) {
        ts->tv_sec--;
        ts->tv_nsec += 1000000000;
    }
    if ((ts->tv_nsec < 0) || (ts->tv_sec < 0))
        return -1;

    return 0;
}

/* initialize 'abstime' to the current time according to 'clock' plus 'msecs'
 * milliseconds.
 */
static void
__timespec_to_relative_msec(struct timespec*  abstime, unsigned  msecs, clockid_t  clock)
{
    clock_gettime(clock, abstime);
    abstime->tv_sec  += msecs/1000;
    abstime->tv_nsec += (msecs%1000)*1000000;
    if (abstime->tv_nsec >= 1000000000) {
        abstime->tv_sec++;
        abstime->tv_nsec -= 1000000000;
    }
}

int pthread_mutex_lock_timeout_np(pthread_mutex_t *mutex, unsigned msecs)
{
    clockid_t        clock = CLOCK_MONOTONIC;
    struct timespec  abstime;
    struct timespec  ts;

    /* compute absolute expiration time */
    __timespec_to_relative_msec(&abstime, msecs, clock);

    if (__likely(mutex != NULL))
    {
        int  mtype = (mutex->value & MUTEX_TYPE_MASK);

        if ( __likely(mtype == MUTEX_TYPE_NORMAL) )
        {
            /* fast path for unconteded lock */
            if (__atomic_cmpxchg(0, 1, &mutex->value) == 0) {
                ANDROID_MEMBAR_FULL();
                return 0;
            }

            /* loop while needed */
            while (__atomic_swap(2, &mutex->value) != 0) {
                if (__timespec_to_absolute(&ts, &abstime, clock) < 0)
                    return EBUSY;

                __futex_wait(&mutex->value, 2, &ts);
            }
            ANDROID_MEMBAR_FULL();
            return 0;
        }
        else
        {
            int  tid = __get_thread()->kernel_id;
            int  oldv;

            if ( tid == MUTEX_OWNER(mutex) )
            {
                int  oldv, counter;

                if (mtype == MUTEX_TYPE_ERRORCHECK) {
                    /* already locked by ourselves */
                    return EDEADLK;
                }

                _recursive_lock();
                oldv = mutex->value;
                counter = (oldv + (1 << MUTEX_COUNTER_SHIFT)) & MUTEX_COUNTER_MASK;
                mutex->value = (oldv & ~MUTEX_COUNTER_MASK) | counter;
                _recursive_unlock();
                return 0;
            }
            else
            {
                /*
                 * If the new lock is available immediately, we grab it in
                 * the "uncontended" state.
                 */
                int new_lock_type = 1;

                for (;;) {
                    int  oldv;
                    struct timespec  ts;

                    _recursive_lock();
                    oldv = mutex->value;
                    if (oldv == mtype) { /* uncontended released lock => 1 or 2 */
                        mutex->value = ((tid << 16) | mtype | new_lock_type);
                    } else if ((oldv & 3) == 1) { /* locked state 1 => state 2 */
                        oldv ^= 3;
                        mutex->value = oldv;
                    }
                    _recursive_unlock();

                    if (oldv == mtype)
                        break;

                    /*
                     * The lock was held, possibly contended by others.  From
                     * now on, if we manage to acquire the lock, we have to
                     * assume that others are still contending for it so that
                     * we'll wake them when we unlock it.
                     */
                    new_lock_type = 2;

                    if (__timespec_to_absolute(&ts, &abstime, clock) < 0)
                        return EBUSY;

                    __futex_wait( &mutex->value, oldv, &ts );
                }
                return 0;
            }
        }
    }
    return EINVAL;
}

int pthread_condattr_init(pthread_condattr_t *attr)
{
    if (attr == NULL)
        return EINVAL;

    *attr = PTHREAD_PROCESS_PRIVATE;
    return 0;
}

int pthread_condattr_getpshared(pthread_condattr_t *attr, int *pshared)
{
    if (attr == NULL || pshared == NULL)
        return EINVAL;

    *pshared = *attr;
    return 0;
}

int pthread_condattr_setpshared(pthread_condattr_t *attr, int pshared)
{
    if (attr == NULL)
        return EINVAL;

    if (pshared != PTHREAD_PROCESS_SHARED &&
        pshared != PTHREAD_PROCESS_PRIVATE)
        return EINVAL;

    *attr = pshared;
    return 0;
}

int pthread_condattr_destroy(pthread_condattr_t *attr)
{
    if (attr == NULL)
        return EINVAL;

    *attr = 0xdeada11d;
    return 0;
}

/* XXX *technically* there is a race condition that could allow
 * XXX a signal to be missed.  If thread A is preempted in _wait()
 * XXX after unlocking the mutex and before waiting, and if other
 * XXX threads call signal or broadcast UINT_MAX times (exactly),
 * XXX before thread A is scheduled again and calls futex_wait(),
 * XXX then the signal will be lost.
 */

int pthread_cond_init(pthread_cond_t *cond,
                      const pthread_condattr_t *attr)
{
    if (cond == NULL)
        return EINVAL;

    cond->value = 0;
    return 0;
}

int pthread_cond_destroy(pthread_cond_t *cond)
{
    if (cond == NULL)
        return EINVAL;

    cond->value = 0xdeadc04d;
    return 0;
}

int pthread_cond_broadcast(pthread_cond_t *cond)
{
    __atomic_dec(&cond->value);

    /*
     * Ensure that all memory accesses previously made by this thread are
     * visible to the woken thread(s).  On the other side, the "wait"
     * code will issue any necessary barriers when locking the mutex.
     *
     * This may not strictly be necessary -- if the caller follows
     * recommended practice and holds the mutex before signaling the cond
     * var, the mutex ops will provide correct semantics.  If they don't
     * hold the mutex, they're subject to race conditions anyway.
     */
    ANDROID_MEMBAR_FULL();

    __futex_wake(&cond->value, INT_MAX);
    return 0;
}

int pthread_cond_signal(pthread_cond_t *cond)
{
    __atomic_dec(&cond->value);
    __futex_wake(&cond->value, 1);
    return 0;
}

int pthread_cond_wait(pthread_cond_t *cond, pthread_mutex_t *mutex)
{
    return pthread_cond_timedwait(cond, mutex, NULL);
}

int __pthread_cond_timedwait_relative(pthread_cond_t *cond,
                                      pthread_mutex_t * mutex,
                                      const struct timespec *reltime)
{
    int  status;
    int  oldvalue = cond->value;

    pthread_mutex_unlock(mutex);
    status = __futex_wait(&cond->value, oldvalue, reltime);
    pthread_mutex_lock(mutex);

    if (status == (-ETIMEDOUT)) return ETIMEDOUT;
    return 0;
}

int __pthread_cond_timedwait(pthread_cond_t *cond,
                             pthread_mutex_t * mutex,
                             const struct timespec *abstime,
                             clockid_t clock)
{
    struct timespec ts;
    struct timespec * tsp;

    if (abstime != NULL) {
        if (__timespec_to_absolute(&ts, abstime, clock) < 0)
            return ETIMEDOUT;
        tsp = &ts;
    } else {
        tsp = NULL;
    }

    return __pthread_cond_timedwait_relative(cond, mutex, tsp);
}

int pthread_cond_timedwait(pthread_cond_t *cond,
                           pthread_mutex_t * mutex,
                           const struct timespec *abstime)
{
    return __pthread_cond_timedwait(cond, mutex, abstime, CLOCK_REALTIME);
}


/* this one exists only for backward binary compatibility */
int pthread_cond_timedwait_monotonic(pthread_cond_t *cond,
                                     pthread_mutex_t * mutex,
                                     const struct timespec *abstime)
{
    return __pthread_cond_timedwait(cond, mutex, abstime, CLOCK_MONOTONIC);
}

int pthread_cond_timedwait_monotonic_np(pthread_cond_t *cond,
                                     pthread_mutex_t * mutex,
                                     const struct timespec *abstime)
{
    return __pthread_cond_timedwait(cond, mutex, abstime, CLOCK_MONOTONIC);
}

int pthread_cond_timedwait_relative_np(pthread_cond_t *cond,
                                      pthread_mutex_t * mutex,
                                      const struct timespec *reltime)
{
    return __pthread_cond_timedwait_relative(cond, mutex, reltime);
}

int pthread_cond_timeout_np(pthread_cond_t *cond,
                            pthread_mutex_t * mutex,
                            unsigned msecs)
{
    struct timespec ts;

    ts.tv_sec = msecs / 1000;
    ts.tv_nsec = (msecs % 1000) * 1000000;

    return __pthread_cond_timedwait_relative(cond, mutex, &ts);
}



/* A technical note regarding our thread-local-storage (TLS) implementation:
 *
 * There can be up to TLSMAP_SIZE independent TLS keys in a given process,
 * though the first TLSMAP_START keys are reserved for Bionic to hold
 * special thread-specific variables like errno or a pointer to
 * the current thread's descriptor.
 *
 * while stored in the TLS area, these entries cannot be accessed through
 * pthread_getspecific() / pthread_setspecific() and pthread_key_delete()
 *
 * also, some entries in the key table are pre-allocated (see tlsmap_lock)
 * to greatly simplify and speedup some OpenGL-related operations. though the
 * initialy value will be NULL on all threads.
 *
 * you can use pthread_getspecific()/setspecific() on these, and in theory
 * you could also call pthread_key_delete() as well, though this would
 * probably break some apps.
 *
 * The 'tlsmap_t' type defined below implements a shared global map of
 * currently created/allocated TLS keys and the destructors associated
 * with them. You should use tlsmap_lock/unlock to access it to avoid
 * any race condition.
 *
 * the global TLS map simply contains a bitmap of allocated keys, and
 * an array of destructors.
 *
 * each thread has a TLS area that is a simple array of TLSMAP_SIZE void*
 * pointers. the TLS area of the main thread is stack-allocated in
 * __libc_init_common, while the TLS area of other threads is placed at
 * the top of their stack in pthread_create.
 *
 * when pthread_key_create() is called, it finds the first free key in the
 * bitmap, then set it to 1, saving the destructor altogether
 *
 * when pthread_key_delete() is called. it will erase the key's bitmap bit
 * and its destructor, and will also clear the key data in the TLS area of
 * all created threads. As mandated by Posix, it is the responsability of
 * the caller of pthread_key_delete() to properly reclaim the objects that
 * were pointed to by these data fields (either before or after the call).
 *
 */

/* TLS Map implementation
 */

#define TLSMAP_START      (TLS_SLOT_MAX_WELL_KNOWN+1)
#define TLSMAP_SIZE       BIONIC_TLS_SLOTS
#define TLSMAP_BITS       32
#define TLSMAP_WORDS      ((TLSMAP_SIZE+TLSMAP_BITS-1)/TLSMAP_BITS)
#define TLSMAP_WORD(m,k)  (m)->map[(k)/TLSMAP_BITS]
#define TLSMAP_MASK(k)    (1U << ((k)&(TLSMAP_BITS-1)))

/* this macro is used to quickly check that a key belongs to a reasonable range */
#define TLSMAP_VALIDATE_KEY(key)  \
    ((key) >= TLSMAP_START && (key) < TLSMAP_SIZE)

/* the type of tls key destructor functions */
typedef void (*tls_dtor_t)(void*);

typedef struct {
    int         init;                  /* see comment in tlsmap_lock() */
    uint32_t    map[TLSMAP_WORDS];     /* bitmap of allocated keys */
    tls_dtor_t  dtors[TLSMAP_SIZE];    /* key destructors */
} tlsmap_t;

static pthread_mutex_t  _tlsmap_lock = PTHREAD_MUTEX_INITIALIZER;
static tlsmap_t         _tlsmap;

/* lock the global TLS map lock and return a handle to it */
static __inline__ tlsmap_t* tlsmap_lock(void)
{
    tlsmap_t*   m = &_tlsmap;

    pthread_mutex_lock(&_tlsmap_lock);
    /* we need to initialize the first entry of the 'map' array
     * with the value TLS_DEFAULT_ALLOC_MAP. doing it statically
     * when declaring _tlsmap is a bit awkward and is going to
     * produce warnings, so do it the first time we use the map
     * instead
     */
    if (__unlikely(!m->init)) {
        TLSMAP_WORD(m,0) = TLS_DEFAULT_ALLOC_MAP;
        m->init          = 1;
    }
    return m;
}

/* unlock the global TLS map */
static __inline__ void tlsmap_unlock(tlsmap_t*  m)
{
    pthread_mutex_unlock(&_tlsmap_lock);
    (void)m;  /* a good compiler is a happy compiler */
}

/* test to see wether a key is allocated */
static __inline__ int tlsmap_test(tlsmap_t*  m, int  key)
{
    return (TLSMAP_WORD(m,key) & TLSMAP_MASK(key)) != 0;
}

/* set the destructor and bit flag on a newly allocated key */
static __inline__ void tlsmap_set(tlsmap_t*  m, int  key, tls_dtor_t  dtor)
{
    TLSMAP_WORD(m,key) |= TLSMAP_MASK(key);
    m->dtors[key]       = dtor;
}

/* clear the destructor and bit flag on an existing key */
static __inline__ void  tlsmap_clear(tlsmap_t*  m, int  key)
{
    TLSMAP_WORD(m,key) &= ~TLSMAP_MASK(key);
    m->dtors[key]       = NULL;
}

/* allocate a new TLS key, return -1 if no room left */
static int tlsmap_alloc(tlsmap_t*  m, tls_dtor_t  dtor)
{
    int  key;

    for ( key = TLSMAP_START; key < TLSMAP_SIZE; key++ ) {
        if ( !tlsmap_test(m, key) ) {
            tlsmap_set(m, key, dtor);
            return key;
        }
    }
    return -1;
}


int pthread_key_create(pthread_key_t *key, void (*destructor_function)(void *))
{
    uint32_t   err = ENOMEM;
    tlsmap_t*  map = tlsmap_lock();
    int        k   = tlsmap_alloc(map, destructor_function);

    if (k >= 0) {
        *key = k;
        err  = 0;
    }
    tlsmap_unlock(map);
    return err;
}


/* This deletes a pthread_key_t. note that the standard mandates that this does
 * not call the destructor of non-NULL key values. Instead, it is the
 * responsability of the caller to properly dispose of the corresponding data
 * and resources, using any mean it finds suitable.
 *
 * On the other hand, this function will clear the corresponding key data
 * values in all known threads. this prevents later (invalid) calls to
 * pthread_getspecific() to receive invalid/stale values.
 */
int pthread_key_delete(pthread_key_t key)
{
    uint32_t             err;
    pthread_internal_t*  thr;
    tlsmap_t*            map;

    if (!TLSMAP_VALIDATE_KEY(key)) {
        return EINVAL;
    }

    map = tlsmap_lock();

    if (!tlsmap_test(map, key)) {
        err = EINVAL;
        goto err1;
    }

    /* clear value in all threads */
    pthread_mutex_lock(&gThreadListLock);
    for ( thr = gThreadList; thr != NULL; thr = thr->next ) {
        /* avoid zombie threads with a negative 'join_count'. these are really
         * already dead and don't have a TLS area anymore.
         *
         * similarly, it is possible to have thr->tls == NULL for threads that
         * were just recently created through pthread_create() but whose
         * startup trampoline (__thread_entry) hasn't been run yet by the
         * scheduler. so check for this too.
         */
        if (thr->join_count < 0 || !thr->tls)
            continue;

        thr->tls[key] = NULL;
    }
    tlsmap_clear(map, key);

    pthread_mutex_unlock(&gThreadListLock);
    err = 0;

err1:
    tlsmap_unlock(map);
    return err;
}


int pthread_setspecific(pthread_key_t key, const void *ptr)
{
    int        err = EINVAL;
    tlsmap_t*  map;

    if (TLSMAP_VALIDATE_KEY(key)) {
        /* check that we're trying to set data for an allocated key */
        map = tlsmap_lock();
        if (tlsmap_test(map, key)) {
            ((uint32_t *)__get_tls())[key] = (uint32_t)ptr;
            err = 0;
        }
        tlsmap_unlock(map);
    }
    return err;
}

void * pthread_getspecific(pthread_key_t key)
{
    if (!TLSMAP_VALIDATE_KEY(key)) {
        return NULL;
    }

    /* for performance reason, we do not lock/unlock the global TLS map
     * to check that the key is properly allocated. if the key was not
     * allocated, the value read from the TLS should always be NULL
     * due to pthread_key_delete() clearing the values for all threads.
     */
    return (void *)(((unsigned *)__get_tls())[key]);
}

/* Posix mandates that this be defined in <limits.h> but we don't have
 * it just yet.
 */
#ifndef PTHREAD_DESTRUCTOR_ITERATIONS
#  define PTHREAD_DESTRUCTOR_ITERATIONS  4
#endif

/* this function is called from pthread_exit() to remove all TLS key data
 * from this thread's TLS area. this must call the destructor of all keys
 * that have a non-NULL data value (and a non-NULL destructor).
 *
 * because destructors can do funky things like deleting/creating other
 * keys, we need to implement this in a loop
 */
static void pthread_key_clean_all(void)
{
    tlsmap_t*    map;
    void**       tls = (void**)__get_tls();
    int          rounds = PTHREAD_DESTRUCTOR_ITERATIONS;

    map = tlsmap_lock();

    for (rounds = PTHREAD_DESTRUCTOR_ITERATIONS; rounds > 0; rounds--)
    {
        int  kk, count = 0;

        for (kk = TLSMAP_START; kk < TLSMAP_SIZE; kk++) {
            if ( tlsmap_test(map, kk) )
            {
                void*       data = tls[kk];
                tls_dtor_t  dtor = map->dtors[kk];

                if (data != NULL && dtor != NULL)
                {
                   /* we need to clear the key data now, this will prevent the
                    * destructor (or a later one) from seeing the old value if
                    * it calls pthread_getspecific() for some odd reason
                    *
                    * we do not do this if 'dtor == NULL' just in case another
                    * destructor function might be responsible for manually
                    * releasing the corresponding data.
                    */
                    tls[kk] = NULL;

                   /* because the destructor is free to call pthread_key_create
                    * and/or pthread_key_delete, we need to temporarily unlock
                    * the TLS map
                    */
                    tlsmap_unlock(map);
                    (*dtor)(data);
                    map = tlsmap_lock();

                    count += 1;
                }
            }
        }

        /* if we didn't call any destructor, there is no need to check the
         * TLS data again
         */
        if (count == 0)
            break;
    }
    tlsmap_unlock(map);
}

// man says this should be in <linux/unistd.h>, but it isn't
extern int tkill(int tid, int sig);

int pthread_kill(pthread_t tid, int sig)
{
    int  ret;
    int  old_errno = errno;
    pthread_internal_t * thread = (pthread_internal_t *)tid;

    ret = tkill(thread->kernel_id, sig);
    if (ret < 0) {
        ret = errno;
        errno = old_errno;
    }

    return ret;
}

extern int __rt_sigprocmask(int, const sigset_t *, sigset_t *, size_t);

int pthread_sigmask(int how, const sigset_t *set, sigset_t *oset)
{
    return __rt_sigprocmask(how, set, oset, NSIG / 8);
}


int pthread_getcpuclockid(pthread_t  tid, clockid_t  *clockid)
{
    const int            CLOCK_IDTYPE_BITS = 3;
    pthread_internal_t*  thread = (pthread_internal_t*)tid;

    if (!thread)
        return ESRCH;

    *clockid = CLOCK_THREAD_CPUTIME_ID | (thread->kernel_id << CLOCK_IDTYPE_BITS);
    return 0;
}


/* NOTE: this implementation doesn't support a init function that throws a C++ exception
 *       or calls fork()
 */
int  pthread_once( pthread_once_t*  once_control,  void (*init_routine)(void) )
{
    static pthread_mutex_t   once_lock = PTHREAD_RECURSIVE_MUTEX_INITIALIZER;
    volatile pthread_once_t* ocptr = once_control;

    pthread_once_t tmp = *ocptr;
    ANDROID_MEMBAR_FULL();
    if (tmp == PTHREAD_ONCE_INIT) {
        pthread_mutex_lock( &once_lock );
        if (*ocptr == PTHREAD_ONCE_INIT) {
            (*init_routine)();
            ANDROID_MEMBAR_FULL();
            *ocptr = ~PTHREAD_ONCE_INIT;
        }
        pthread_mutex_unlock( &once_lock );
    }
    return 0;
}

/* This value is not exported by kernel headers, so hardcode it here */
#define MAX_TASK_COMM_LEN	16
#define TASK_COMM_FMT 		"/proc/self/task/%u/comm"

int pthread_setname_np(pthread_t thid, const char *thname)
{
    size_t thname_len;
    int saved_errno, ret;

    if (thid == 0 || thname == NULL)
        return EINVAL;

    thname_len = strlen(thname);
    if (thname_len >= MAX_TASK_COMM_LEN)
        return ERANGE;

    saved_errno = errno;
    if (thid == pthread_self())
    {
        ret = prctl(PR_SET_NAME, (unsigned long)thname, 0, 0, 0) ? errno : 0;
    }
    else
    {
        /* Have to change another thread's name */
        pthread_internal_t *thread = (pthread_internal_t *)thid;
        char comm_name[sizeof(TASK_COMM_FMT) + 8];
        ssize_t n;
        int fd;

        snprintf(comm_name, sizeof(comm_name), TASK_COMM_FMT, (unsigned int)thread->kernel_id);
        fd = open(comm_name, O_RDWR);
        if (fd == -1)
        {
            ret = errno;
            goto exit;
        }
        n = TEMP_FAILURE_RETRY(write(fd, thname, thname_len));
        close(fd);

        if (n < 0)
            ret = errno;
        else if ((size_t)n != thname_len)
            ret = EIO;
        else
            ret = 0;
    }
exit:
    errno = saved_errno;
    return ret;
}

/* Return the kernel thread ID for a pthread.
 * This is only defined for implementations where pthread <-> kernel is 1:1, which this is.
 * Not the same as pthread_getthreadid_np, which is commonly defined to be opaque.
 * Internal, not an NDK API.
 */

pid_t __pthread_gettid(pthread_t thid)
{
    pthread_internal_t* thread = (pthread_internal_t*)thid;
    return thread->kernel_id;
}






#ifdef UCLIBC_LINUXTHREAD_OLD_EXTENSTION /* uClibc linuxthread.old extension define */

/*#define __LT_SPINLOCK_INIT 0

static void __pthread_init_lock(struct _pthread_fastlock * lock)
{
  lock->__status = 0;
  lock->__spinlock = __LT_SPINLOCK_INIT;
}


static void __pthread_lock(struct _pthread_fastlock * lock,
				      pthread_internal_t* self)
{
       // FIXME
}

static int __pthread_unlock(struct _pthread_fastlock * lock)
{
       // FIXME 
       return 0;
}


static __inline__ void enqueue(pthread_internal_t ** q, pthread_internal_t* th)
{
  int prio = th->p_priority;
  for (; *q != NULL; q = &((*q)->next)) {
    if (prio > (*q)->p_priority) {
      th->next = *q;
      *q = th;
      return;
    }
  }
  *q = th;
}

static pthread_internal_t * dequeue(pthread_internal_t ** q)
{
  pthread_internal_t *th;
  th = *q;
  if (th != NULL) {
    *q = th->next;
    th->next = NULL;
  }
  return th;
}

static int remove_from_queue(pthread_internal_t ** q, pthread_internal_t *th)
{
  for (; *q != NULL; q = &((*q)->next)) {
    if (*q == th) {
      *q = th->next;
      th->next = NULL;
      return 1;
    }
  }
  return 0;
}

static int queue_is_empty(pthread_internal_t ** q)
{
    return *q == NULL;
}


#define __ASSUME_REALTIME_SIGNALS defined(__NR_rt_sigaction)

// Primitives for controlling thread execution

static void restart(pthread_internal_t* th)
{
  // See pthread.c

}

static void suspend(pthread_internal_t* self)
{
  // See pthread.c

}

static int timedsuspend(pthread_internal_t* self,
		const struct timespec *abstime)
{
  // See pthread.c
  return 0;
}



// Check whether the calling thread already owns one or more read locks on the
// specified lock. If so, return a pointer to the read lock info structure
// corresponding to that lock.

static pthread_readlock_info *
rwlock_is_in_list(pthread_internal_t *self, pthread_rwlock_t *rwlock)
{
  pthread_readlock_info *info;

  for (info = self->p_readlock_list; info != NULL; info = info->pr_next)
    {
      if (info->pr_lock == rwlock)
	return info;
    }

  return NULL;
}

// Add a new lock to the thread's list of locks for which it has a read lock.
// A new info node must be allocated for this, which is taken from the thread's
// free list, or by calling malloc. If malloc fails, a null pointer is
// returned. Otherwise the lock info structure is initialized and pushed
// onto the thread's list.

static pthread_readlock_info *
rwlock_add_to_list(pthread_internal_t *self, pthread_rwlock_t *rwlock)
{
  pthread_readlock_info *info = self->p_readlock_free;

  if (info != NULL)
    self->p_readlock_free = info->pr_next;
  else
    info = malloc(sizeof *info);

  if (info == NULL)
    return NULL;

  info->pr_lock_count = 1;
  info->pr_lock = rwlock;
  info->pr_next = self->p_readlock_list;
  self->p_readlock_list = info;

  return info;
}

// If the thread owns a read lock over the given pthread_rwlock_t,
// and this read lock is tracked in the thread's lock list,
// this function returns a pointer to the info node in that list.
// It also decrements the lock count within that node, and if
// it reaches zero, it removes the node from the list.
// If nothing is found, it returns a null pointer.

static pthread_readlock_info *
rwlock_remove_from_list(pthread_internal_t *self, pthread_rwlock_t *rwlock)
{
  pthread_readlock_info **pinfo;

  for (pinfo = &self->p_readlock_list; *pinfo != NULL; pinfo = &(*pinfo)->pr_next)
    {
      if ((*pinfo)->pr_lock == rwlock)
	{
	  pthread_readlock_info *info = *pinfo;
	  if (--info->pr_lock_count == 0)
	    *pinfo = info->pr_next;
	  return info;
	}
    }

  return NULL;
}


// This function checks whether the conditions are right to place a read lock.
// It returns 1 if so, otherwise zero. The rwlock's internal lock must be
// locked upon entry.

static int
rwlock_can_rdlock(pthread_rwlock_t *rwlock, int have_lock_already)
{
  // Can't readlock; it is write locked.
  if (rwlock->__rw_writer != NULL)
    return 0;

  // Lock prefers readers; get it.
  if (rwlock->__rw_kind == PTHREAD_RWLOCK_PREFER_READER_NP)
    return 1;

  // Lock prefers writers, but none are waiting.
  if (queue_is_empty((pthread_internal_t **)&rwlock->__rw_write_waiting))
    return 1;

  // Writers are waiting, but this thread already has a read lock
  if (have_lock_already)
    return 1;

  // Writers are waiting, and this is a new lock
  return 0;
}

// This function helps support brain-damaged recursive read locking
// semantics required by Unix 98, while maintaining write priority.
// This basically determines whether this thread already holds a read lock
// already. It returns 1 if so, otherwise it returns 0.
//
// If the thread has any ``untracked read locks'' then it just assumes
// that this lock is among them, just to be safe, and returns 1.
//
// Also, if it finds the thread's lock in the list, it sets the pointer
// referenced by pexisting to refer to the list entry.
//
// If the thread has no untracked locks, and the lock is not found
// in its list, then it is added to the list. If this fails,
// then *pout_of_mem is set to 1.

static int
rwlock_have_already(pthread_internal_t *pself, pthread_rwlock_t *rwlock,
    pthread_readlock_info **pexisting, int *pout_of_mem)
{
  pthread_readlock_info *existing = NULL;
  int out_of_mem = 0, have_lock_already = 0;
  pthread_internal_t *self = pself;

  if (rwlock->__rw_kind == PTHREAD_RWLOCK_PREFER_WRITER_NP)
    {
      if (!self)
	self = __get_thread();

      existing = rwlock_is_in_list(self, rwlock);

      if (existing != NULL || self->p_untracked_readlock_count > 0)
	have_lock_already = 1;
      else
	{
	  existing = rwlock_add_to_list(self, rwlock);
	  if (existing == NULL)
	    out_of_mem = 1;
	}
    }

  *pout_of_mem = out_of_mem;
  *pexisting = existing;
  pself = self;

  return have_lock_already;
}

int
pthread_rwlock_init (pthread_rwlock_t *rwlock,
		     const pthread_rwlockattr_t *attr)
{
  __pthread_init_lock(&rwlock->__rw_lock);
  rwlock->__rw_readers = 0;
  rwlock->__rw_writer = NULL;
  rwlock->__rw_read_waiting = NULL;
  rwlock->__rw_write_waiting = NULL;

  if (attr == NULL)
    {
      rwlock->__rw_kind = PTHREAD_RWLOCK_DEFAULT_NP;
      rwlock->__rw_pshared = PTHREAD_PROCESS_PRIVATE;
    }
  else
    {
      rwlock->__rw_kind = attr->__lockkind;
      rwlock->__rw_pshared = attr->__pshared;
    }

  return 0;
}


int
pthread_rwlock_destroy (pthread_rwlock_t *rwlock)
{
  int readers;
  pthread_internal_t *writer;

  __pthread_lock (&rwlock->__rw_lock, NULL);
  readers = rwlock->__rw_readers;
  writer = rwlock->__rw_writer;
  __pthread_unlock (&rwlock->__rw_lock);

  if (readers > 0 || writer != NULL)
    return EBUSY;

  return 0;
}

int
pthread_rwlock_rdlock (pthread_rwlock_t *rwlock)
{
  pthread_internal_t* self = NULL;
  pthread_readlock_info *existing;
  int out_of_mem, have_lock_already;

  have_lock_already = rwlock_have_already(self, rwlock,
      &existing, &out_of_mem);

  for (;;)
    {
      if (self == NULL)
	self = __get_thread();

      __pthread_lock (&rwlock->__rw_lock, self);

      if (rwlock_can_rdlock(rwlock, have_lock_already))
	break;

      enqueue ((pthread_internal_t **)&rwlock->__rw_read_waiting, self);
      __pthread_unlock (&rwlock->__rw_lock);
      suspend (self); // This is not a cancellation point
    }

  ++rwlock->__rw_readers;
  __pthread_unlock (&rwlock->__rw_lock);

  if (have_lock_already || out_of_mem)
    {
      if (existing != NULL)
	existing->pr_lock_count++;
      else
	self->p_untracked_readlock_count++;
    }

  return 0;
}

int
pthread_rwlock_tryrdlock (pthread_rwlock_t *rwlock)
{
  pthread_internal_t* self = __get_thread();
  pthread_readlock_info *existing;
  int out_of_mem, have_lock_already;
  int retval = EBUSY;

  have_lock_already = rwlock_have_already(self, rwlock,
      &existing, &out_of_mem);

  __pthread_lock (&rwlock->__rw_lock, self);

  // 0 is passed to here instead of have_lock_already.
  // This is to meet Single Unix Spec requirements:
  // if writers are waiting, pthread_rwlock_tryrdlock
  // does not acquire a read lock, even if the caller has
  // one or more read locks already.

  if (rwlock_can_rdlock(rwlock, 0))
    {
      ++rwlock->__rw_readers;
      retval = 0;
    }

  __pthread_unlock (&rwlock->__rw_lock);

  if (retval == 0)
    {
      if (have_lock_already || out_of_mem)
	{
	  if (existing != NULL)
	    existing->pr_lock_count++;
	  else
	    self->p_untracked_readlock_count++;
	}
    }

  return retval;
}


int
pthread_rwlock_wrlock (pthread_rwlock_t *rwlock)
{
  pthread_internal_t *self = __get_thread();

  while(1)
    {
      __pthread_lock (&rwlock->__rw_lock, self);
      if (rwlock->__rw_readers == 0 && rwlock->__rw_writer == NULL)
	{
	  rwlock->__rw_writer = self;
	  __pthread_unlock (&rwlock->__rw_lock);
	  return 0;
	}

      // Suspend ourselves, then try again
      enqueue ((pthread_internal_t **)&rwlock->__rw_write_waiting, self);
      __pthread_unlock (&rwlock->__rw_lock);
      suspend (self); // This is not a cancellation point
    }
}


int
pthread_rwlock_trywrlock (pthread_rwlock_t *rwlock)
{
  int result = EBUSY;

  __pthread_lock (&rwlock->__rw_lock, NULL);
  if (rwlock->__rw_readers == 0 && rwlock->__rw_writer == NULL)
    {
      rwlock->__rw_writer = __get_thread();
      result = 0;
    }
  __pthread_unlock (&rwlock->__rw_lock);

  return result;
}


int
pthread_rwlock_unlock (pthread_rwlock_t *rwlock)
{
  pthread_internal_t *torestart;
  pthread_internal_t *th;

  __pthread_lock (&rwlock->__rw_lock, NULL);
  if (rwlock->__rw_writer != NULL)
    {
      // Unlocking a write lock.
      if (rwlock->__rw_writer != __get_thread())
	{
	  __pthread_unlock (&rwlock->__rw_lock);
	  return EPERM;
	}
      rwlock->__rw_writer = NULL;

      if (rwlock->__rw_kind == PTHREAD_RWLOCK_PREFER_READER_NP
	  || (th = dequeue ((pthread_internal_t **)&rwlock->__rw_write_waiting)) == NULL)
	{
	  // Restart all waiting readers.
	  torestart = rwlock->__rw_read_waiting;
	  rwlock->__rw_read_waiting = NULL;
	  __pthread_unlock (&rwlock->__rw_lock);
	  while ((th = dequeue ((pthread_internal_t **)&torestart)) != NULL)
	    restart (th);
	}
      else
	{
	  // Restart one waiting writer.
	  __pthread_unlock (&rwlock->__rw_lock);
	  restart (th);
	}
    }
  else
    {
      // Unlocking a read lock.
      if (rwlock->__rw_readers == 0)
	{
	  __pthread_unlock (&rwlock->__rw_lock);
	  return EPERM;
	}

      --rwlock->__rw_readers;
      if (rwlock->__rw_readers == 0)
	// Restart one waiting writer, if any.
	th = dequeue ((pthread_internal_t **)&rwlock->__rw_write_waiting);
      else
	th = NULL;

      __pthread_unlock (&rwlock->__rw_lock);
      if (th != NULL)
	restart (th);

      // Recursive lock fixup

      if (rwlock->__rw_kind == PTHREAD_RWLOCK_PREFER_WRITER_NP)
	{
	  pthread_internal_t *self = __get_thread();
	  pthread_readlock_info *victim = rwlock_remove_from_list(self, rwlock);

	  if (victim != NULL)
	    {
	      if (victim->pr_lock_count == 0)
		{
		  victim->pr_next = self->p_readlock_free;
		  self->p_readlock_free = victim;
		}
	    }
	  else
	    {
	      if (self->p_untracked_readlock_count > 0)
		self->p_untracked_readlock_count--;
	    }
	}
    }

  return 0;
}


int
pthread_rwlockattr_init (pthread_rwlockattr_t *attr)
{
  attr->__lockkind = 0;
  attr->__pshared = 0;

  return 0;
}


int
pthread_rwlockattr_destroy (pthread_rwlockattr_t *attr)
{
  return 0;
}


int
pthread_rwlockattr_getpshared (const pthread_rwlockattr_t *attr, int *pshared)
{
  *pshared = attr->__pshared;
  return 0;
}


int
pthread_rwlockattr_setpshared (pthread_rwlockattr_t *attr, int pshared)
{
  if (pshared != PTHREAD_PROCESS_PRIVATE && pshared != PTHREAD_PROCESS_SHARED)
    return EINVAL;

  attr->__pshared = pshared;

  return 0;
}


int
pthread_rwlockattr_getkind_np (const pthread_rwlockattr_t *attr, int *pref)
{
  *pref = attr->__lockkind;
  return 0;
}


int
pthread_rwlockattr_setkind_np (pthread_rwlockattr_t *attr, int pref)
{
  if (pref != PTHREAD_RWLOCK_PREFER_READER_NP
      && pref != PTHREAD_RWLOCK_PREFER_WRITER_NP
      && pref != PTHREAD_RWLOCK_DEFAULT_NP)
    return EINVAL;

  attr->__lockkind = pref;

  return 0;
}
*/

#endif


