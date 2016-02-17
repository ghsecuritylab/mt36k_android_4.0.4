#include "os/Thread.h"
#include "os/Semaphore.h"

#include <stdlib.h>
#include <time.h>
#include <signal.h>
#include <stdio.h>

#ifdef _USE_LINUX
#include <sys/prctl.h>
#endif

#ifndef _USE_LINUX
#include <windows.h>
#endif

static const int ThreadSchedPriority = 45;

static const int SIGCANCEL = 32; /* pthread cancel or kill */

void os::Thread::sleep(const int & ms)
{
#ifndef _USE_LINUX
    Sleep(ms);
#else
    struct timespec req;
    req.tv_sec  = ms/1000;
    int mod     = ms%1000;
    req.tv_nsec = mod*1000*1000;
    nanosleep(&req, NULL);
#endif
}

class os::Thread::Implmentation
{
public:
    Implmentation();
    ~Implmentation();
    int wait();
    int notify();
private:
    os::Semaphore sema;
};

os::Thread::Implmentation::Implmentation()
    :sema(os::Semaphore(0))
{
}

os::Thread::Implmentation::~Implmentation()
{
}

int os::Thread::Implmentation::wait()
{
    return this->sema.acquire();
}

int os::Thread::Implmentation::notify()
{
    return this->sema.release();
}

os::Thread::Thread(std::string const& threadName) : name(threadName)
{
    this->impl = new os::Thread::Implmentation();
    attr = NULL;
}

os::Thread::~Thread()
{
    if(this->impl != NULL)
    {
        delete this->impl;
    }

    if (attr != NULL)
    {
        pthread_attr_destroy(attr);
        delete attr;
    }
}

void * os::Thread::start(void * tag)
{
#ifdef _USE_LINUX
    // Mask out all the signals
    sigset_t maskedSignals;
    sigemptyset(&maskedSignals);
    sigaddset(&maskedSignals, SIGTERM);
    sigprocmask(SIG_BLOCK, &maskedSignals, NULL);
#endif

#ifdef _USE_LINUX
    // Disable SIGCANCEL so the application will not abort.
    struct sigaction pipeaction;
    pipeaction.sa_handler = SIG_IGN;
    ::sigemptyset(&pipeaction.sa_mask);
    pipeaction.sa_flags = 0;
    struct sigaction oldPipeaction;
    ::sigaction(SIGCANCEL, &pipeaction, &oldPipeaction);
#endif // _USE_LINUX

    os::Thread * thread = static_cast<os::Thread*>(tag);
#ifdef _USE_LINUX
    prctl(PR_SET_NAME, (unsigned long)thread->name.c_str(), (unsigned long)0, (unsigned long)0, (unsigned long)0);
#endif
    thread->Run();
    thread->impl->notify();

#ifdef _USE_LINUX
    // Restore SIGCANCEL.
    ::sigaction(SIGCANCEL, &oldPipeaction, NULL);
#endif
    return tag;
}

void os::Thread::Start()
{
    attr = new pthread_attr_t;
    int ret = pthread_attr_init(attr);
    if (ret != 0)
    {
        printf("Error pthread_attr_init %d \n", ret);
        return ;
    }
#ifdef _USE_LINUX
    struct sched_param schedparam;
    ret = pthread_attr_setdetachstate(attr, PTHREAD_CREATE_DETACHED);
    if (ret != 0)
    {
        printf("Error pthread_attr_setdetachstate %d \n", ret);
        return ;
    }
    ret = pthread_attr_setschedpolicy(attr, SCHED_RR);
    if (ret != 0)
    {
        printf("Error pthread_attr_setschedpolicy %d \n", ret);
        return ;
    }
#if 0
    ret = pthread_attr_setinheritsched(attr, PTHREAD_EXPLICIT_SCHED);
    if (ret != 0)
    {
        printf("Error pthread_attr_setinheritsched %d \n", ret);
        return ;
    }
#endif

    schedparam.sched_priority = ThreadSchedPriority;
    ret = pthread_attr_setschedparam(attr, &schedparam);
    if (ret != 0)
    {
        printf("Error pthread_attr_setschedparam %d \n", ret);
        return ;
    }
#endif

    ret = pthread_create( &handle, attr, start, this);
    if (ret != 0)
    {
        printf("Error pthread_create %d \n", ret);
        return ;
    }
}

int os::Thread::Wait()
{
    if(this->impl != NULL)
    {
        return this->impl->wait();
    }
    return -1;
}