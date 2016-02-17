#ifndef _WFD_CLIENT_EXPORT_H_
#define _WFD_CLIENT_EXPORT_H_
#include "WFD_common.h"

#ifdef _cplusplus
extern "C"
{
#endif
    extern int wfd_client_start(WFD_Client_callback pf_callback);
#if 1
    extern int wfd_client_get_deviceNumber();

    extern const char* wfd_client_get_deviceName(int index); // begin from 0
    
    extern const char* wfd_client_get_deviceMac(int index); // begin from 0

    extern const char* wfd_client_get_deviceIp(int index); // begin from 0

    extern int wfd_client_get_devicePort(int index); // begin from 0

    extern int  wfd_client_connect_device(int index);

    extern int  wfd_client_disconnect_device();

    extern int wfd_client_is_connected(int index);
    
    extern void wfd_client_stop();
    
    extern int wfd_client_find();

    extern int wfd_client_debug_rtsp(char* ip, int port);

    extern int wfd_client_media_play();
    
    extern int wfd_client_media_pause();
    
    extern int wfd_client_media_teardown();

#endif
#ifdef _cplusplus
}
#endif


#endif /* _WFD_CLIENT_EXPORT_H_ */
