#ifndef _WFD_COMMON_H_
#define _WFD_COMMON_H_

typedef enum
{
    WFD_DEVICE_FOUND, 
    WFD_DEVICE_BYEBYE,
    WFD_DEVICE_CONNECTED,
    WFD_DEVICE_DISCONNECTED,
    WFD_DEVICE_IP_GET ,
    WFD_DEVICE_START_OK ,
    WFD_DEVICE_START_FAILED 

} WFDClient_Event_e;

typedef void (*WFD_Client_callback)(WFDClient_Event_e e);

#endif /* _WFD_COMMON_H_ */
