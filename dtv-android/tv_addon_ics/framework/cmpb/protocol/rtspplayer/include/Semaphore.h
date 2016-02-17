#ifndef _SEMAPHORE_H_
#define _SEMAPHORE_H_
#include <pthread.h>

//#define RECURSIVE_LOCK_SUPPORT

#ifdef RECURSIVE_LOCK_SUPPORT
#include <sys/types.h>
#endif

class Semaphore{
public:
    /**
     * Creates a new semaphore. Optionally specify an initial count,
     * default is 0. 
     */
    Semaphore();     // cond number: the Semaphore can maintain more than one cond(s).

    /**
     * Destroys this semaphore.
     */
    ~Semaphore();

    void syncBegin();
    void syncEnd();
    
    void wait(int condInex = 0);
    bool timedWait(unsigned int relTimeOutMs);
    void notify(int condInex = 0);
    void notifyAll(int condInex = 0);
    
private:
    pthread_mutex_t mutex_;
    pthread_cond_t  cond;
#ifdef RECURSIVE_LOCK_SUPPORT
    pid_t t_owner;
    int lock_count;
    pthread_mutex_t ext_protector;
#define EXT_BEGIN()                             \
    pthread_mutex_lock(&ext_protector)

#define EXT_END()                               \
    pthread_mutex_unlock(&ext_protector)

#endif
    

    // pthread_cond_t * conds;
    // int condnum;
    
};


#endif /* _SEMAPHORE_H_ */
