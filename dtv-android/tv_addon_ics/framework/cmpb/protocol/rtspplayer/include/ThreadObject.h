#ifndef _THREADOBJECT_H_
#define _THREADOBJECT_H_
#include "Semaphore.h"

typedef void *(*ThreadRun)(void* pv_data);

void * runner(void * arg);      // the arg should be ThreadObject * .
class ThreadObject : public Semaphore
{
public:
    ThreadObject(ThreadRun threadrun);
    bool start(void * arg);
    void stop();
    bool needExit();
    void haveDestoryed();
    
    ThreadRun pf_run;    
    void * pv_data;
private:
    bool alive;
    
    pthread_t t_thread;
    
    int rc;
};

#endif /* _THREADOBJECT_H_ */
