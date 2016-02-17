#ifndef _WFDCLIENTLISTENER_H_
#define _WFDCLIENTLISTENER_H_

class WFDClientListener
{
    virtual void notifyWFDClientEvent(int event) = 0;
};

#endif /* _WFDCLIENTLISTENER_H_ */
