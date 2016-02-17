#include "WFDDeviceMentorImpl.h"
#include <stdio.h>
#include <stdlib.h>
#include <memory.h>

typedef	unsigned int		UINT32;
typedef char                CHAR ;
typedef int                 INT32;
typedef unsigned char       UINT8;
#ifndef TRUE
#define TRUE 1
#endif
#ifndef FALSE
#define FALSE 0
#endif

typedef unsigned short		        UINT16;
typedef short		        INT16;
typedef void		        VOID;
typedef bool		        BOOL;

extern "C"
{
#include "c_net_wlan_ctl.h"
}
static WFDDeviceListener * _listener = NULL ; 
static NetDevice * _net_device = NULL;
static bool _wfd_started = false;


static INT32 _wifi_direct_notify_entry(INT32 i4CallbackId, VOID *pParam);

bool _do_init_wfd_step()
{
   
     INT32 ret = 0;
     fprintf(stderr,">>>>>>>>>%s,-> open connection!\n",__FUNCTION__);
     ret = c_net_wlan_open_connection("");
     if(ret != 0)
     {
         fprintf(stderr,">>>>>>>>>%s,%d return error!\n",__FUNCTION__,__LINE__);
         return false ;
     }
     fprintf(stderr,">>>>>>>>>%s,-> enable wifi direct for wfd!\n",__FUNCTION__);
     ret = c_net_wlan_enable_wifi_direct();
     if(ret != 0)
     {
         fprintf(stderr,">>>>>>>>>%s,%d return error!\n",__FUNCTION__,__LINE__);
         return false ;
     }
     fprintf(stderr,">>>>>>>>>%s,-> register callback function !\n",__FUNCTION__);
     ret = c_net_wlan_wpa_reg_cbk(_wifi_direct_notify_entry);
     if(ret == -1)
     {
         fprintf(stderr,">>>>>>>>>%s,%d return error!\n",__FUNCTION__,__LINE__);
         return false ;
     }
     fprintf(stderr,">>>>>>>>>%s,-> set wifi direct as listen mode!\n",__FUNCTION__);
     ret = c_net_wlan_p2p_find(_wifi_direct_notify_entry);
     if(ret != 0)
     {
         fprintf(stderr,">>>>>>>>>%s,%d return error!\n",__FUNCTION__,__LINE__);
         return false ;
     }
     fprintf(stderr,">>>>>>>>>%s,-> set wfd mode!\n",__FUNCTION__);
	 c_net_wlan_p2p_set_as_wfd_mode(TRUE);
	  
	 fprintf(stderr,">>>>>>>>>%s,-> set p2p device name!\n",__FUNCTION__);
     ret = c_net_wlan_p2p_set_dev_name("Mediatek-DTV");
     if(ret != 0)
         fprintf(stderr,"########### wfd init p2p set dev name return error!\n");

     return true;
}


static INT32 _wifi_direct_notify_entry(INT32 i4CallbackId, VOID *pParam)
{
    fprintf(stderr,"_wifi_direct_notify_entry be called ,i4CallbackId = %d\n",i4CallbackId);
    if( ! _listener ) //no any notify function been registered .
        return -1;
        
    switch (i4CallbackId) 
    {
        case WLAN_NOTIFY_P2P_DEVICE_FOUND:
        {
            if( !_listener )
                break ;
            
            fprintf(stderr,"_wifi_direct_notify_entry be called ,i4CallbackId = %d\n",i4CallbackId);
            BasicDevice _b_dev;
            NET_802_11_P2P_DATA _p2p_info ;
            memset(&_p2p_info , 0 ,sizeof(NET_802_11_P2P_DATA));
            memcpy(&_p2p_info ,(NET_802_11_P2P_DATA *)pParam , sizeof(NET_802_11_P2P_DATA));
            _b_dev.mac = _p2p_info._mac;
            _b_dev.name = _p2p_info._name;
            fprintf(stderr,"_wifi_direct_notify_entry _p2p_info._mac = %s\n",_p2p_info._mac);
            if(_p2p_info.b_Is_WFD)
            {
	    	    _b_dev.is_wfd= _p2p_info.b_Is_WFD;
			    _b_dev.config_methods = _p2p_info.config_methods;
			    _b_dev.dev_capab = _p2p_info.dev_capab;
			    _b_dev.group_capab = _p2p_info.group_capab;
			    _b_dev.wfd_dev_info = _p2p_info.wfd_dev_info;
			    _b_dev.rtsp_port = _p2p_info.rtsp_port;
			    _b_dev.max_throughput = _p2p_info.max_throughput;
            }
            _listener->notifyDeviceStatusChange(WFD_DEVICE_FOUND,(void *)&_b_dev);
    	    break ;
        }
    
        case WLAN_NOTIFY_P2P_GROUP_STARTED:
        {   
            if( !_listener )
                break ;
            fprintf(stderr,">>>>>> wfdDeviceMentorImpl->");   
            BasicDevice _b_dev;
            NET_802_11_P2P_DATA _p2p_info ;
            memset(&_p2p_info , 0 ,sizeof(NET_802_11_P2P_DATA));
            memcpy(&_p2p_info ,(NET_802_11_P2P_DATA *)pParam , sizeof(NET_802_11_P2P_DATA));
            _b_dev.mac = _p2p_info._mac;
            _b_dev.name = _p2p_info._name;
            _b_dev.is_go = _p2p_info.b_Is_GO;
            
    	    _listener->notifyDeviceStatusChange(WFD_DEVICE_CONNECTED,(void *)&_b_dev);

    	    break ;
        }

        case WLAN_NOTIFY_WFD_IP_GET:
        {
    	    if( !_net_device )
    	        break ;
    	    NET_802_11_WFD_DATA _wfd_info ;
            memset(&_wfd_info , 0 ,sizeof(NET_802_11_WFD_DATA));
            memcpy(&_wfd_info ,(NET_802_11_WFD_DATA *)pParam , sizeof(NET_802_11_WFD_DATA));
    	    _net_device->set(_wfd_info._port , _wfd_info._ip_addr);
    	    //_net_device->port = _wfd_info._port;
			fprintf(stderr,">>>> WFD_IP_GET -> IP=%s,port = %d\n",_wfd_info._ip_addr,_wfd_info._port);
			_listener->notifyDeviceStatusChange(WFD_DEVICE_IP_GET, NULL);

    	    break ;
        }
        case WLAN_NOTIFY_DRIVER_START_IND:
        {
    	    if(_do_init_wfd_step() != true)
			{			    
		        _listener->notifyDeviceStatusChange(WFD_DEVICE_START_FAILED, NULL); 

			}
			_wfd_started = true;
			_listener->notifyDeviceStatusChange(WFD_DEVICE_START_OK, NULL); 

    	    break ;
        }
        case WLAN_NOTIFY_DRIVER_START_FAILED:
        {
    	   _listener->notifyDeviceStatusChange(WFD_DEVICE_START_FAILED, NULL); 
    	   break ;
        }
        default :
            break;
    
    }
    return 0;
}

bool WFDDeviceMentorImpl::start(WFDDeviceListener * listnener)  // discovery device (include found alive and byebye.)
{ 

     if( listnener == NULL )
           return false;
         
     if(_wfd_started == true )
     {
         fprintf(stderr,">>>>>>WFDDeviceMentorImpl::start-> device has already started!\n");
   	     listnener->notifyDeviceStatusChange(WFD_DEVICE_START_OK, NULL);
         return true;
     }
	 INT32 _i4ret ;
   
     _i4ret = c_net_wlan_start_driver(_wifi_direct_notify_entry);
	 if(_i4ret == 1)
	 {
		 fprintf(stderr,"WFDDeviceMentorImpl::start - > already started!, ret = %d\n",_i4ret);	
         if( _do_init_wfd_step() == false)
         {
              fprintf(stderr,"WFDDeviceMentorImpl::start init step failed!\n");
              listnener->notifyDeviceStatusChange(WFD_DEVICE_START_FAILED, NULL);     

         }
        listnener->notifyDeviceStatusChange(WFD_DEVICE_START_OK, NULL);
	 }	
	 if(_i4ret < 0)
	 {	     
         listnener->notifyDeviceStatusChange(WFD_DEVICE_START_FAILED, NULL);	 
		 return false;
	 }
	 _listener = listnener;
	 _wfd_started = true;
     return true;
}

bool WFDDeviceMentorImpl::stop()
{
    INT32 ret ;
     _wfd_started = false;
    c_net_wlan_p2p_set_as_wfd_mode(FALSE);
    ret = c_net_wlan_p2p_stop_find();
    ret = c_net_wlan_wpa_unreg_cbk(_wifi_direct_notify_entry);
	ret = c_net_wlan_disable_wifi_direct();
	if(ret != 0)
	{
	    fprintf(stderr,">>>>>>>>>%s,%d return error!\n",__FUNCTION__,__LINE__);
	    _listener = NULL;
	    return false ;
	}
	_listener = NULL;

    return true;	
}

// BasicDeviceList * WFDDeviceMentorImpl::get()
// {
//      return NULL;
// }
		
bool WFDDeviceMentorImpl::connect(BasicDevice	*dev, NetDevice  * connectedDev) // can connect more than one device meantime?
{
     
     int ret  = c_net_wlan_p2p_connect(P2P_CONNECT_MODE_PBC , dev->mac.c_str(),NULL);
     if(ret !=  0)
     {
         fprintf(stderr,">>>>>>>>>%s,%d->c_net_wlan_p2p_connect return error! ret = %d\n",__FUNCTION__,__LINE__,ret);
         return false;
     
     }
     _net_device = connectedDev;
     return true;
}

bool WFDDeviceMentorImpl::disconnect()
{ 

    int ret = c_net_wlan_p2p_disconnect();
	if(ret !=  0)
    {
	    fprintf(stderr,">>>>>>>>>%s,%d->c_net_wlan_p2p_disconnect return error! ret = %d\n",__FUNCTION__,__LINE__,ret);
		return false;
			 
	}
	_net_device = NULL;
	return true;
}

bool WFDDeviceMentorImpl::find()
{
     int ret = c_net_wlan_p2p_find(_wifi_direct_notify_entry);
     if(ret != 0)
     {
         fprintf(stderr,">>>>>>>>>%s,%d return error!\n",__FUNCTION__,__LINE__);
         return false ;
     }
     fprintf(stderr,">>>>>>>>>%s,%d return ok!\n",__FUNCTION__,__LINE__);
	 return true;
}

WFDDeviceCap * WFDDeviceMentorImpl::getCapabilities(BasicDevice *dev)
{
    return NULL;
}

