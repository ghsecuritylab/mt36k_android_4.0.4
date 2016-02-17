/*
 * Berkeley Lab Checkpoint/Restart (BLCR) for Linux is Copyright (c)
 * 2008, The Regents of the University of California, through Lawrence
 * Berkeley National Laboratory (subject to receipt of any required
 * approvals from the U.S. Dept. of Energy).  All rights reserved.
 *
 * Portions may be copyrighted by others, as may be noted in specific
 * copyright notices within specific files.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: //DTV/MP_BR/DTV_X_IDTV0801_002158_10_001_158_001/android/ics-4.x/blcr-0.8.4/cr_module/cr_kcompat.h#1 $
 *
 * This file tries to hide as much as practical the differences among Linux
 * kernel versions.  Preferably this is done by back-porting new features, but
 * sometimes by providing the "least common denominator"
 */

#ifndef _CR_KCOMPAT_H
#define _CR_KCOMPAT_H        1

#ifndef _IN_CR_MODULE_H
  #error "This file is only for inclusion from cr_module.h"
#endif

// Not defined in all kernels
#ifndef TASK_COMM_LEN
  #define TASK_COMM_LEN 16
#endif

// Provide uniform lockdep support
#if !defined(DECLARE_WAIT_QUEUE_HEAD_ONSTACK)
  #if defined(CONFIG_LOCKDEP)
    #define __WAIT_QUEUE_HEAD_INIT_ONSTACK(name) \
	({ init_waitqueue_head(&name); name; })
    #define DECLARE_WAIT_QUEUE_HEAD_ONSTACK(name) \
	wait_queue_head_t name = __WAIT_QUEUE_HEAD_INIT_ONSTACK(name)
  #else
    #define DECLARE_WAIT_QUEUE_HEAD_ONSTACK DECLARE_WAIT_QUEUE_HEAD
  #endif
#endif

#if defined(CONFIG_LOCKDEP)
  static __inline__ void CR_NO_LOCKS(void) {
    if (current->lockdep_depth) {
      debug_show_held_locks(current);
      dump_stack();
    }
  }
#else
  #define CR_NO_LOCKS() ((void)0)
#endif 

// MOD_{INC,DEC}_USE_COUNT were removed prior to 2.6.x
#define CR_MODULE_GET()	try_module_get(THIS_MODULE)
#define CR_MODULE_PUT()	module_put(THIS_MODULE)
#undef MOD_INC_USE_COUNT
#undef MOD_DEC_USE_COUNT
#define MOD_INC_USE_COUNT	%%%%ERROR%%%% use CR_MODULE_GET()
#define MOD_DEC_USE_COUNT	%%%%ERROR%%%% use CR_MODULE_PUT()

#ifdef CAP_KILL
  #define cr_capable(X) capable(X)
#else
  #define cr_capable(X) suser()
#endif

#define cr_permission(I,M,N)        inode_permission((I),(M))

#define CR_RLIM(task)	(task)->signal->rlim

#ifndef rcu_assign_pointer
  #define rcu_assign_pointer(A,B)	((A) = (B))
#endif
#ifndef rcu_read_lock
  #define rcu_read_lock()		do {} while (0)
  #define rcu_read_unlock()		do {} while (0)
#endif

typedef struct fdtable	cr_fdtable_t;
#define cr_fdtable(files)	files_fdtable(files)

#define CR_NEXT_FD(_files, _fdt) ((_files)->next_fd)

#define CR_MAX_FDS(_fdt) ((_fdt)->max_fds)

#ifndef thread_group_leader
  #define thread_group_leader(p) ((p)->pid == (p)->tgid)
#endif

#define CR_SIGNAL_LOCK(_task)		spin_lock_irq(&(_task)->sighand->siglock)
#define CR_SIGNAL_UNLOCK(_task)		spin_unlock_irq(&(_task)->sighand->siglock)
#define CR_SIGACTION(_task,_num)	((_task)->sighand->action[(_num)-1])
#define CR_SIGNAL_HAND(_task,_num)	(CR_SIGACTION((_task),(_num)).sa.sa_handler)

typedef struct kmem_cache *cr_kmem_cache_ptr;

#ifndef KMEM_CACHE
  #define KMEM_CACHE(__struct, __flags) \
        kmem_cache_create(#__struct,\
			  sizeof(struct __struct),\
			  __alignof__(struct __struct),\
			  (__flags), NULL, NULL)
#endif  

#define cr_kzalloc kzalloc

#define cr_kmem_cache_zalloc(_szof_arg, _cachep, _flags) kmem_cache_zalloc(_cachep, _flags)

#define cr_kmemdup kmemdup

// Task accessor macros
#define cr_task_pgrp(_t)	task_pgrp_vnr(_t)
#define cr_task_tty_old_pgrp(_t)	((_t)->signal->tty_old_pgrp)
#define cr_task_session(_t)	task_session_vnr(_t)
#define cr_task_leader(_t)	((_t)->signal->leader)
#define cr_task_tty(_t)	((_t)->signal->tty)
#define cr_set_pgrp(_t,_id)	((void)0)
#define cr_set_sid(_t,_id)	((void)0)

#define cr_init_pid_ns init_pid_ns

// XXX: should move to vpid
#define cr_find_pid(P) find_pid_ns((P),&cr_init_pid_ns)

// XXX: should move to by_vpid
#define cr_find_task_by_pid(P) find_task_by_pid_ns((P),&cr_init_pid_ns)

#define cr_have_pid(T,P) (pid_task(find_vpid(P),(T)) != NULL)

// Process table iterators
#define do_each_task_pid(ID, TYPE, T)          \
	do {                                         \
		struct pid *the_pid = cr_find_pid(ID);        \
		do_each_pid_task(the_pid, (TYPE), (T))

#define while_each_task_pid(ID, TYPE, T)       \
		while_each_pid_task(the_pid, (TYPE), (T)); \
	} while (0)

#define CR_DO_EACH_TASK_PGID(ID, T) do_each_task_pid((ID), PIDTYPE_PGID, (T))
#define CR_WHILE_EACH_TASK_PGID(ID, T) while_each_task_pid((ID), PIDTYPE_PGID, (T))

#define CR_DO_EACH_TASK_SID(ID, T) do_each_task_pid((ID), PIDTYPE_SID, (T))
#define CR_WHILE_EACH_TASK_SID(ID, T) while_each_task_pid((ID), PIDTYPE_SID, (T))

#define CR_DO_EACH_TASK_TGID(ID, T) \
	{ \
		struct task_struct *_leader = cr_find_task_by_pid(ID); \
		if (_leader) { \
			/* "extra" iteration for thread group leader (the list head): */ \
			(T) = _leader; goto _label; \
			list_for_each_entry((T), &(_leader->thread_group), thread_group) { \
			_label:

#define CR_WHILE_EACH_TASK_TGID(ID, T) \
			} \
		} \
	}

#define CR_DO_EACH_TASK_PROC(P, T) \
	if ((P)->mm) { \
		pid_t _id = cr_task_pgrp(P); \
		CR_DO_EACH_TASK_PGID(_id, (T)) \
        if ((T)->mm == (P)->mm)

#define CR_WHILE_EACH_TASK_PROC(ID, T) \
		while_each_task_pid(_id, PIDTYPE_PGID, (T)); \
	}

#define CR_DO_EACH_CHILD(C, T) \
    list_for_each_entry((C), &(T)->children, sibling) {

#define CR_WHILE_EACH_CHILD(C, T) \
    }

/* How do we manipulate the lock on a inode */
#define cr_inode_lock(_i)			mutex_lock(&(_i)->i_mutex)
#define cr_inode_lock_interruptible(_i)	mutex_lock_interruptible(&(_i)->i_mutex)
#define cr_inode_unlock(_i)			mutex_unlock(&(_i)->i_mutex)

#ifndef wait_event_interruptible_timeout
/* from 2.6.8 */
  #define __wait_event_interruptible_timeout(wq, condition, ret) \
    do {                                                         \
      DECLARE_WAITQUEUE(__wait, current);                        \
      add_wait_queue(&(wq), &__wait);                            \
      for (;;) {                                                 \
        set_current_state(TASK_INTERRUPTIBLE);                   \
        if (condition) break;                                    \
        if (!signal_pending(current)) {                          \
	  ret = schedule_timeout(ret);                           \
	  if (!ret) break;                                       \
	  continue;                                              \
        }                                                        \
        ret = -ERESTARTSYS;                                      \
        break;                                                   \
      }                                                          \
      current->state = TASK_RUNNING;                             \
      remove_wait_queue(&(wq), &__wait);                         \
    } while(0)
  #define wait_event_interruptible_timeout(wq, condition, timeout) \
    ({ long __ret = timeout;                                       \
       if (!(condition))                                           \
         __wait_event_interruptible_timeout(wq, condition, __ret); \
       __ret;                                                      \
    })
#endif

#if defined(EXIT_ZOMBIE)
  #define cri_task_zombie(task)	 ((task)->exit_state & EXIT_ZOMBIE)
#elif defined(TASK_DEAD)
  #define cri_task_zombie(task)	 ((task)->state & TASK_ZOMBIE)
#else
  #define cri_task_zombie(task)	 ((task)->state == TASK_ZOMBIE)
#endif
#define cri_task_dead(task)	 ((task)->flags & PF_EXITING)

#ifdef DEFINE_SPINLOCK
  #define CR_DEFINE_SPINLOCK DEFINE_SPINLOCK
#else
  #define CR_DEFINE_SPINLOCK(_l) spinlock_t _l = SPIN_LOCK_UNLOCKED
#endif
#ifdef DEFINE_RWLOCK
  #define CR_DEFINE_RWLOCK DEFINE_RWLOCK
#else
  #define CR_DEFINE_RWLOCK(_l) rwlock_t _l = RW_LOCK_UNLOCKED
#endif

/* Implement "struct path" in terms of dentry & vfsmnt */
static __inline__ void cr_set_pwd_file(struct fs_struct *fs, struct file *filp) {
	set_fs_pwd(fs, &filp->f_path);
}
static __inline__ void cr_set_pwd_nd(struct fs_struct *fs, struct nameidata *nd) {
	set_fs_pwd(fs, &nd->path);
}
#define nd_dentry	path.dentry
#define nd_mnt	path.mnt
#define cr_path_release(_nd) path_put(&((_nd)->path))
#define CR_PATH_DECL(_name) struct path *_name /* NO semicolon */
#define CR_PATH_GET_FS(_name,_arg) path_get(((_name) = &(_arg)))
#define CR_PATH_GET_FILE(_name,_arg) path_get(((_name) = &(_arg)->f_path))

#define cr_proc_root NULL

#define cr_set_dumpable(_mm,_val)	set_dumpable((_mm),(_val))

#define cr_suid_dumpable suid_dumpable

// wait_event_timeout() first appears in 2.6.9
// This is reproduced from linux-2.6.9/include/linux/wait.h
#ifndef wait_event_timeout
  #define __wait_event_timeout(wq, condition, ret)                      \
  do {                                                                  \
        DEFINE_WAIT(__wait);                                            \
                                                                        \
        for (;;) {                                                      \
                prepare_to_wait(&wq, &__wait, TASK_UNINTERRUPTIBLE);    \
                if (condition)                                          \
                        break;                                          \
                ret = schedule_timeout(ret);                            \
                if (!ret)                                               \
                        break;                                          \
        }                                                               \
        finish_wait(&wq, &__wait);                                      \
  } while (0)

  #define wait_event_timeout(wq, condition, timeout)                    \
  ({                                                                    \
        long __ret = timeout;                                           \
        if (!(condition))                                               \
                __wait_event_timeout(wq, condition, __ret);             \
        __ret;                                                          \
  })
#endif

#define cr_kill_process(task, sig)	kill_pid(task_tgid(task), sig, 0)

typedef const struct cred *cr_cred_t;
#define cr_current_cred()	current_cred()
#define cr_task_cred(_t)	__task_cred(_t)

#define cr_free_fs_struct free_fs_struct

#define cr_do_pipe(_fds) do_pipe_flags((_fds),0)

#if !defined(DECLARE_MUTEX)
  #define DECLARE_MUTEX(m) DEFINE_SEMAPHORE(m)
  #define init_MUTEX(m) sema_init(m, 1)
#endif

#define cr_read_lock_fs   spin_lock
#define cr_read_unlock_fs spin_unlock

#endif /* _CR_KCOMPAT_H */
