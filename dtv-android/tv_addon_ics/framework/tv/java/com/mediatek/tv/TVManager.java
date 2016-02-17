package com.mediatek.tv;

import android.content.Context;
/*import android.content.IntentFilter;
import android.media.AudioManager;*/
import android.os.IBinder;
import android.os.RemoteException;

import com.mediatek.tv.service.BroadcastService;
import com.mediatek.tv.service.CIService;
import com.mediatek.tv.service.ChannelService;
import com.mediatek.tv.service.ConfigService;
import com.mediatek.tv.service.EventService;
import com.mediatek.tv.service.IService;
import com.mediatek.tv.service.ITVCallBack;
import com.mediatek.tv.service.ITVRemoteService;
import com.mediatek.tv.service.InputService;
import com.mediatek.tv.service.Logger;
import com.mediatek.tv.service.OSDService;
import com.mediatek.tv.service.ScanService;
import com.mediatek.tv.service.ServiceFactory;
import com.mediatek.tv.service.TVClientHandler;
import com.mediatek.tv.service.TVRemoteService;
import com.mediatek.tv.service.TVRemoteServiceHandler;
import com.mediatek.tv.service.ComponentService;

/**
 * This class provides the primary API for managing all aspects of TV services.
 * Get an instance of this class by calling
 * {@link android.content.Context#getSystemService(String)
 * Context.getSystemService(Context.TV_SERVICE)} . It deals with several
 * categories of items:
 * <ul>
 * <li>The list of channels.</li>
 * <li>The list of physical inputs and outputs (e.g. HDMI inputs).</li>
 * <li>PIP/POP.</li>
 * <li>The binding of inputs with outputs.</li>
 * <li>Parental control.</li>
 * </ul>
 */
public class TVManager {
    private static final String TAG = "TVManager";
    public static final String TV_REMOTE_SERVICE_NAME = "com.mediatek.tv.service.TVRemoteService";
    public static boolean IPC = true;// TODO

    private static TVManager manager;
    private static ITVRemoteService tvRemoteService = null;
	
    private ITVCallBack clientHandler = new TVClientHandler();

    public static ITVRemoteService getRemoteTvService() {
        if (null != TVManager.manager) {
            return TVManager.manager.getTvRemoteService();
        }
        return null;
    }

    /**
     * @return the tvRemoteService
     */
    public ITVRemoteService getTvRemoteService() {
        return tvRemoteService;
    }

    /**
     * Get TVManager singleton instance.
     * 
     * @param context
     *            the Application context
     * @return the TVManager singleton instance
     */
    public static TVManager getInstance(Context context) {
        if (manager == null) {
            synchronized (TVManager.class) {
                if (manager == null) {
                    Logger.d(TAG, "Create TVManager");
                    manager = new TVManager(context);
                    manager.postInit();
                }

                if (IPC) {

                } else {
                    tvRemoteService = new TVRemoteServiceHandler(null);
            }
        }
        }
        Logger.d(TAG, "return TVManager instance " + manager);

        return manager;
    }

    private void postInit() {
        // for input service get data from MW
        InputService inputService = (InputService) this.getService(InputService.InputServiceName);
        if (inputService != null) {
            inputService.inputServiceInit();
        }
    }

    /**
     * Get TVManager singleton instance.
     * 
     * @return the TVManager singleton instance
     */
    public static TVManager getInstance() {
        return manager;
    }

    /**
     * Create a new TVManager instance. Applications will almost always want to
     * use {@link #getInstance} to retrieve the singleton instance
     * 
     * @param context
     *            the Application context
     * @hide - hide private constructor.
     */
    private TVManager(Context context) {
        String[] serviceNames = new String[] {
                //
        ScanService.ScanServiceName,//
                ChannelService.ChannelServiceName,//
                ConfigService.ConfigServiceName,//
                InputService.InputServiceName,//
                OSDService.OSDServiceName, //
                BroadcastService.BrdcstServiceName, //
                EventService.EventServiceName,   //
                CIService.CIServiceName,
                ComponentService.CompServiceName,
        };

        Class<?>[] services = new Class<?>[] {
                //
        ScanService.class,//
                ChannelService.class,//
                ConfigService.class,//
                InputService.class,//
                OSDService.class,//
                BroadcastService.class,//
                EventService.class,   //
                CIService.class,
                ComponentService.class,
        };

        ServiceFactory serviceManager = ServiceFactory.getInstance();
        serviceManager.createServices(serviceNames, services);

        try {
            IBinder binder = android.os.ServiceManager.getService(TVRemoteService.SERVICE_NAME);
            Logger.d(TAG, binder.toString());
            if (binder != null) {
                tvRemoteService = ITVRemoteService.Stub.asInterface(binder);
                if (tvRemoteService == null) {
                    Logger.e(TAG, "Can not get TV Service!");
                } else {
                    tvRemoteService.registerCallback(clientHandler);
                }
    }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * Get service by service name
     * 
     * <pre>
     * ------------------------------------------------------------------------------------------------
     *                     simple code to get Configuration service 
     * ------------------------------------------------------------------------------------------------
     * TVManager tvManager = TVManager.getInstance(null);
     * ConfigService configService = (ConfigService)tvManager.getService(ConfigService.ConfigServiceName);
     * </pre>
     * 
     * @param serviceName
     *            The service name,to indicator a service
     * @return the IService interface
     */
    public IService getService(String serviceName) {
        ServiceFactory serviceManager = ServiceFactory.getInstance();
        return serviceManager.getService(serviceName);
    }

}
