#ifndef _LISTENER_H_
#define _LISTENER_H_

#include <iostream>

namespace rtsp
{
enum State 
{
	OPENING = 0,
	OPENED ,
	PLAYING ,
	PAUSED ,
	STOPPED ,
	CLOSED ,
	SEEKING 
};

enum EventId
{
    Unknown = -1,
    /**
     * param is State
     */
    StateChanged ,
    /**
     * param is no use
     */
    EndOfStream ,
    /**
     * param is current time position
     */
    UpdatePts ,
    /**
     * param is BesTVStatus
     */
    PlayerError ,
    /**
     * param is 0-100 progress
     */
    Buffering ,
};


struct Event
{
    Event(EventId id = Unknown, int param = 0):id_(id), param_(param)
	{
    }
	
    Event(Event const& event)
	{
        this->id_ = event.id_;
        this->param_ = event.param_;
    }
	
    Event operator=(Event const& event)
	{
        Event tmp(event.id_, event.param_);
        return tmp;
    }
    EventId id_;
    int param_;
};

class Listener
{
    public:
        virtual ~Listener() {}
        virtual void notify(const Event &event) = 0;
};

typedef std::deque<:Event> EventQueue;
typedef std::vector<Listener*> ListenerList;



}

