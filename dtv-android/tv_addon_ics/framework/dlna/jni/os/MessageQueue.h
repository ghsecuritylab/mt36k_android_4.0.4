#ifndef _OS_MESSAGE_QUEUE_H
#define _OS_MESSAGE_QUEUE_H

#include "pthread.h"
#include <string>
#include <queue>
#include <list>

#include "os/Mutex.h"
#include "os/ScopedMutex.h"
#include "os/Semaphore.h"

namespace os {
    template <class T> class MessageQueue{
    public:
        MessageQueue();
        ~MessageQueue();

        void send(const T&t);
        T& recv();
        std::list<T> recvAll();
        std::list<T> recvAllAsync();
        bool empty();
    protected:
    private:
        std::queue<T> queue;
        os::Mutex     mutex;
        os::Semaphore semaphore;
    };

    template <class T> os::MessageQueue<T>::MessageQueue():semaphore(Semaphore(0))
    {
    }

    template <class T> os::MessageQueue<T>::~MessageQueue()
    {
    }

    template <class T> void  os::MessageQueue<T>::send(const T & t)
    {
        os::ScopedMutex scoped(mutex);
        if (queue.empty())
        {
            queue.push(t);
            semaphore.release();
        }
        else
        {
            queue.push(t);
        }
    }

    template <class T> T&  os::MessageQueue<T>::recv()
    {
        while (queue.empty())
        {
            semaphore.acquire();
        }
        os::ScopedMutex scoped(mutex);
        T & t = queue.front();
        queue.pop();
        return t;
    }

    template <class T> std::list<T>  os::MessageQueue<T>::recvAll()
    {
        while (queue.empty())
        {
            semaphore.acquire();
        }
        os::ScopedMutex scoped(mutex);
        std::list<T> list;
        while (!queue.empty())
        {
            T & t = queue.front();
            queue.pop();
            list.push_back(t);
        }
        return list;
    }

    template <class T> std::list<T>  os::MessageQueue<T>::recvAllAsync()
    {        
        os::ScopedMutex scoped(mutex);
        std::list<T> list;
        while (!queue.empty())
        {
            T & t = queue.front();
            queue.pop();
            list.push_back(t);
        }
        return list;
    }

    template <class T> bool os::MessageQueue<T>::empty()
    {
        os::ScopedMutex scoped(mutex);
        return queue.empty();
    }
}

#endif