#include "ThreadEngine.h"
#include <iostream>
#include <stdio.h>
#include <algorithm>

using namespace TEngine;

void * TaskQueue::tmpObject = NULL;

ThreadEngine ThreadEngine::_INS_;

Task::Task(TEPerform fct, void *vtag, void * sd)
    :perform_fct(fct), arg(vtag), sender(sd)
{
}

void*  Executor::run(void *arg)
{
    Executor * exe = (Executor*)arg;
    exe->execute();
    return NULL;
}

Executor::Executor(TaskQueue * queue, const std::string & nm)
    :jobs(queue), alive(true), job(NULL), name(nm)
{
}

void Executor::execute()
{
    while(alive){
        Task *t = jobs->popTask(job);
        if (t == NULL)
        {
            // means should be exit that task queue have returned without any task.
            alive = false;
            break;
        }
        //std::cout<<name<<" perform";
        t->perform_fct(t->arg, TEngine_EVENT_NORMAL);
    }
}

void TaskQueue::putTask(TEPerform fct, void * arg, void * sender)
{ 
    syncBegin();
    Task *t;
    
    if (freeq.empty())
    {
        t = new Task(fct, arg, sender);
    }
    else{
        t = freeq.front();
        freeq.pop_front();
        t->perform_fct = fct;
        t->arg = arg;
        t->sender = sender;
    }
    pendq.push_back(t);
    // notify consumers
    notifyAll();
    
    syncEnd();
}

Task* TaskQueue::popTask(Task * finishedTask)
{
    syncBegin();
    if (finishedTask != NULL)
    {
        // finishedTask should be reused
        // 1. poped from busyq
        busyq.remove(finishedTask);
        // 2. put in freeq
        freeq.push_back(finishedTask);
        
    }
    
    while (pendq.empty())
    {
        // wait for wake up when something is produced
        wait();
#if 0                           // now needn't this feature
        if (!alive)
        {
            syncEnd();
            return NULL;        // so to terminal executor
        }
#endif
    }

    // now, it isn't empty
    Task *t = pendq.front();
    pendq.pop_front();
    busyq.push_back(t);
    
    syncEnd();
    // have got job
    return t;
}

int TaskQueue::cancelAll()
{
    syncBegin();
    // only can cencel tasks in pendq, busy tasks are running now and can't be interrupted.
    int size = pendq.size();
    while (!pendq.empty()){
        freeq.push_back(pendq.front());
        pendq.pop_front();
    }
    std::cout<<"Cancel command can't cancel task num:"<<busyq.size()<<std::endl
             <<"Have interupted task num is:"<<size<<std::endl;
    
    syncEnd();
    return size;
}

bool TaskQueue::isTaskOfArg(void * task)
{
    Task * t = (Task*) task;
    if (t->arg == TaskQueue::tmpObject)
    {
        return true;
    }
    return false;
}

bool TaskQueue::isTaskOfSender(void *task)
{
    Task * t = (Task*) task;
    if (t->sender == TaskQueue::tmpObject)
    {
        return true;
    }
    return false;
}

bool TaskQueue::cancel(void * arg)
{
    syncBegin();
    Queue::iterator it;
    TaskQueue::tmpObject = arg;
    it = std::find_if(pendq.begin(), pendq.end(), isTaskOfArg);
    if (it != pendq.end())
    {
        // have found, move it from pendq to freeq for reusing in future
        freeq.push_back(*it);
        pendq.erase(it);
        Task * t = *it;
        t->perform_fct(t->arg, TEngine_EVENT_CANCEL);
        syncEnd();
        return true;
    }
    syncEnd();
    return false;               // can't cancel specific task
}

int TaskQueue::cancelAll(void * sender)
{
    syncBegin();
    int num = 0;
    Queue::iterator it;
    Queue::iterator fromit = pendq.begin();
    tmpObject = sender;
    while((it = std::find_if(fromit, pendq.end(), isTaskOfSender)) != pendq.end())
    {
        // have found one, move it from pendq to freeq for reusing in future
        freeq.push_back(*it);
        pendq.erase(it);
        Task * t = *it;
        t->perform_fct(t->arg, TEngine_EVENT_CANCEL);
        
        num ++;
    }
    syncEnd();
    return num;
}

void ThreadEngine::initialize()
{
    // setup workers
    if (inited)
    {
        return;
    }
    inited = true;
    for(int i = 0; i<WORKER_NUM; ++i)
    {
        char buf[10] = {0};
        sprintf(buf, "ThreadEngine %d", i);
        Executor * worker = new Executor(&engineQueue, buf);
        int err = pthread_create(&worker->id, NULL, Executor::run, worker);
        if (err != 0)
        {
            // error. todo ..
            std::cout<<"pthread_create error in thread engine initialize"<<std::endl;
            return;
        }
        workers.push_back(worker);
    }
}

ThreadEngine::~ThreadEngine()
{
    if (inited)
    {
        std::vector<Executor*>::iterator it;
        for (it = workers.begin(); it != workers.end(); ++it)
        {
            Executor * worker = *it;
            delete worker;
        }
    }
    workers.clear();
}


void ThreadEngine::requestExecute(TEPerform fct, void * tag, void * sender)
{
    engineQueue.putTask(fct, tag, sender);
}

void ThreadEngine::requestExecuteOrderly(TEPerform fct, void * tag, void *sender)
{
    engineQueue.putTask(fct, tag, sender); // todo ... orderly.
}

bool ThreadEngine::cancel(void * tag)
{
    return engineQueue.cancel(tag);
}

int ThreadEngine::cancelAll(void * sender)
{
    return engineQueue.cancelAll(sender);
}
