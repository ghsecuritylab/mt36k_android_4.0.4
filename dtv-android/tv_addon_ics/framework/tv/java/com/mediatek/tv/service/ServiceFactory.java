package com.mediatek.tv.service;

import java.util.HashMap;
import java.util.Map;

import com.mediatek.tv.TVManager;

public class ServiceFactory {
    private static ServiceFactory serviceManager = null;

    private Map<String, IService> serviceMap = new HashMap<String, IService>();

    private ServiceFactory() {

    }

    public IService getService(String serviceName) {
        if (serviceMap != null && serviceMap.containsKey(serviceName)) {
            return serviceMap.get(serviceName);
        } else {
            System.out.println("Can not get service by name " + serviceName);
            return null;
        }
    }

    public static ServiceFactory getInstance() {
        if (serviceManager == null) {
            synchronized (TVManager.class) {
                if (serviceManager == null) {
                    serviceManager = new ServiceFactory();
                }
            }
        }
        return serviceManager;
    }

    public void createServices(String[] serviceNames, Class<?>[] services) {
        if (serviceNames == null) {
            System.out.println("createServices fail serviceNames is null");
            return;
        }

        try {
            for (int i = 0; i < serviceNames.length; i++) {
                if (serviceNames[i].length() > 0) {
                    if (!serviceMap.containsKey(serviceNames[i])) {
                        IService iService = (IService) services[i]
                                .newInstance();
                        if (iService != null) {
                            // iService.setServiceName(serviceNames[i]);
                            serviceMap.put(serviceNames[i], iService);
                        }
                    }
                } else {
                    System.out.println("createServices fail index=" + i);
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
