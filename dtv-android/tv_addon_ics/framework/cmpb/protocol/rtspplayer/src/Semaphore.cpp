#include "Semaphore.h"
#include <stdio.h>
#include <assert.h>
#include <sys/time.h>
#include <errno.h>

Semaphore::Semaphore()
{
    // condnum = condNum;
//     conds = new pthread_cond_t[condNum];
//     for(int i=0;i<condNum; ++i){
//         pthread_cond_init(&(conds[i]), NULL);
//     }
    pthread_cond_init(&cond, NULL);
    pthread_mutex_init(&mutex_, NULL);
#ifdef RECURSIVE_LOCK_SUPPORT
    t_owner = 0;
    lock_count = 0;
    pthread_mutex_init(&ext_protector, NULL);
#endif
}

Semaphore::~Semaphore()
{
//     for(int i=0; i<condnum; ++i){
//         pthread_cond_destroy(&(conds[i]));
//     }
    pthread_cond_destroy(&cond);
    pthread_mutex_destroy(&mutex_);
}

void Semaphore::syncBegin()
{
#ifdef RECURSIVE_LOCK_SUPPORT
    // because recursive mutex won't release the lock, when wait....
    EXT_BEGIN();  // following block of data also needs to be protected.
    if (t_owner == gettid())
    {
        // the current thread had owned the lock. Don't lock again, but just go ahead.
        lock_count ++;
        fprintf(stderr, "%d begin lock count=%d\n", gettid(),lock_count);
        EXT_END();

        return;
    }
    else
    {
        EXT_END();
#endif


        pthread_mutex_lock(&mutex_);
        // THE current thread has got the lock,
#ifdef RECURSIVE_LOCK_SUPPORT
        t_owner = gettid();
        lock_count = 1;
        fprintf(stderr, "%d init lock count=%d\n",pthread_self(),  lock_count);
    }
#endif
}

void Semaphore::syncEnd()
{
#ifdef RECURSIVE_LOCK_SUPPORT
    EXT_BEGIN();
    // following condition should always be true.
    // if (t_owner == pthread_self())
    // {
    //     lock_count --;
    // }
    lock_count --;
    fprintf(stderr, "%d end lock count=%d\n", pthread_self(), lock_count);
    if (lock_count != 0)
    {
        // not the last unlock, so ignore to unlock.
        EXT_END();

        return;
    }
    // means the last unlock.
    EXT_END();
#endif
    pthread_mutex_unlock(&mutex_);
}

void Semaphore::notifyAll(int index)
{
    int ret;
    if((ret = pthread_cond_broadcast(&cond)) != 0)
    {
        printf("notifyAll fail, %d\n", ret);
    }
}

void Semaphore::notify(int index)
{
    int ret;
    if((ret = pthread_cond_signal(&cond)) != 0)
    {
        printf("notify fail, %d\n", ret);
    }
}

void Semaphore::wait(int index)
{
    int ret;

    if((ret = pthread_cond_wait(&cond, &mutex_)) != 0)
    {
        // wait fail.
        printf("Wait fail, %d\n", ret);
    }
}

bool Semaphore::timedWait(unsigned int relTimeOutMs)
{
   //pthread_mutex_lock(&mutex_);
#if 0
    timespec now;
    clock_gettime(CLOCK_REALTIME, &now);
    timespec waitUntil;
    waitUntil.tv_sec  = now.tv_sec + relTimeOutMs/ 1000;
    waitUntil.tv_nsec = now.tv_nsec + (relTimeOutMs % 1000)*1000*1000ull;
#else
    timespec waitUntil;
    waitUntil.tv_sec  = time(0) + relTimeOutMs/ 1000;
    waitUntil.tv_nsec = 0;
#endif
#ifdef RECURSIVE_LOCK_SUPPORT
    while (lock_count == 0) 
#endif        
    {        
        int result = pthread_cond_timedwait(&cond, &mutex_, &waitUntil);
        // catch a timeout
        if (result == ETIMEDOUT) 
        {
            //pthread_mutex_unlock(&mutex_);
            return false;
        }
        // any other failure is fatal
        if (result != 0) 
        {
            //pthread_mutex_unlock(&mutex_);
            printf("timedWait fail\n");
            assert(0);
            return false;
        }
    }
#ifdef RECURSIVE_LOCK_SUPPORT
    lock_count--;
#endif
    //pthread_mutex_unlock(&mutex_);
    return true;
}

