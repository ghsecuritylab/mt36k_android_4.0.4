#include "ThreadObject.h"

ThreadObject::ThreadObject(ThreadRun threadrun): pf_run(threadrun), alive(false)
{
}

bool ThreadObject::start(void * arg)
{
    pv_data = arg;

    rc = pthread_create(&t_thread, NULL, runner, this);
    if (rc)
    {
        return false;
    }
#if 0    
	struct sched_param sched;
	sched.__sched_priority = 45;
	pthread_setschedparam(t_thread, SCHED_RR, &sched);
#endif	
    alive = true;

    return true;
}

void ThreadObject::stop()
{
    syncBegin();
    if (!alive)
    {
        // have already stopped
        syncEnd();
        return;
    }
    alive = false;
    wait();                     // waiting for the thread to terminate itself.
    // have terminated
    // stop OK.
    syncEnd();
}

bool ThreadObject::needExit()
{
    syncBegin();
    bool exit = ! alive;

    syncEnd();
    return (exit);
}

void ThreadObject::haveDestoryed()
{
    alive = false;
}

void * runner(void * arg)
{
    ThreadObject* obj = (ThreadObject*) arg;
    obj->pf_run(obj->pv_data);
    // thread has finsihed.
    obj->syncBegin();
    obj->haveDestoryed();
    obj->notifyAll();                // wake up the caller of stop.
    obj->syncEnd();
    return arg;
}

