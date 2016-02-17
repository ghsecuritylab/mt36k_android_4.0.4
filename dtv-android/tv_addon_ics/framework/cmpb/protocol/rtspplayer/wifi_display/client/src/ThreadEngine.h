#ifndef _THREADENGINE_H_
#define _THREADENGINE_H_
#include <vector>
#include <list>
#include "Semaphore.h"
#include <string>

 
namespace TEngine
{
    typedef enum {
        TEngine_EVENT_NORMAL,
        TEngine_EVENT_CANCEL
    } TEngineEvent_E;
    
    typedef void (*TEPerform)(void * arg, TEngineEvent_E event);
    
    class Task
    {
    public:
        Task(TEPerform fct, void * vtag, void * sender);
        
        TEPerform perform_fct;
        void * arg;
        void * sender;
    };
    
    typedef std::list<Task*> Queue;
    class TaskQueue:public Semaphore
    {
        Queue pendq;
        Queue busyq;
        Queue freeq;
        bool alive;
        
    public:
        void putTask(TEPerform fct, void * arg, void * sender);
        Task* popTask(Task * finishedTask);
        int cancelAll();        // cancel all
        int cancelAll(void * sender); // more than one task belongs to the same sender will be all canceled.
                                   // return the number of tasks being canceled.
        bool cancel(void * arg);   // only cancel one, if more than one has the same arg, the first one
                                   // which is th oldest will be canceled. 
        static bool isTaskOfArg(void * task);
        static bool isTaskOfSender(void *task);
        static void * tmpObject;
    };
    
    class Executor
    {
    public:
        void execute();
        pthread_t id;
        
        Executor(TaskQueue * queue, const std::string& nm);
        
        static void * run(void * arg);
    private:
        TaskQueue *jobs;
        bool alive;
        std::string name;
        Task * job;
    };
    
        
    class ThreadEngine
    {
    public:
        static ThreadEngine * getInstance()
            {
                return &_INS_;
            }
        void initialize();
        
        void requestExecute(TEPerform fct, void * tag, void * sender = NULL); // NULL sender will be ignored will user wants to cancel all tasks
                                                                              // belongs to the sender.

        void requestExecuteOrderly(TEPerform fct, void * tag, void * sender); // more request of the same fct will be performed in order.
        
        // if cancel successful return true, or return false
        bool cancel(void * tag); // cancel the task which related with this specific tag 
        int cancelAll(void * sender);
        ~ThreadEngine();
        
    private:
        ThreadEngine() : inited(false)
            {
            }
        static ThreadEngine _INS_;
        TaskQueue engineQueue;
        static const int WORKER_NUM = 2; // If only one app use it.
        std::vector<Executor*> workers;
        bool inited;
    };
}

    

#endif /* _THREADENGINE_H_ */
