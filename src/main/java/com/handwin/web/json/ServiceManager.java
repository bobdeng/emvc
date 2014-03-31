package com.handwin.web.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.handwin.web.Service;

public class ServiceManager {
	static final Logger logger = Logger.getLogger(ServiceManager.class);
	private static HashMap<String,Service> services = new HashMap<String, Service>();
	public synchronized static void startService(String serviceName){
		stopService(serviceName);
		addService(serviceName);
	}
	public synchronized static void stopService(String serviceName){
		Service service = services.get(serviceName);
		if(service!=null){
			service.stopService();
			services.remove(serviceName);
			logger.info("Stop service:" + service.getName());
		}
	}
	public synchronized static void addService(String serviceName){
		try {
			if(serviceName.startsWith("#")) return;
			Class<?> clz = Class.forName(serviceName);
			Service service = ((Service) clz.newInstance());
			service.startService();
			services.put(serviceName,service);
			System.out.println("Start Service:"+serviceName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			logger.warn("服务没有找到:" + serviceName);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized static void stopAllService(){
		Iterator<String> keys = services.keySet().iterator();
		while(keys.hasNext()){
			Service service = services.get(keys.next());
			service.stopService();
			logger.info("Stop service:" + service.getName());
		}
		services.clear();
	}
	public synchronized static List<ServiceInfo> getAllService(){
		List<ServiceInfo> result=new ArrayList<ServiceInfo>();
		Iterator<String> keys = services.keySet().iterator();
		while(keys.hasNext()){
			ServiceInfo info=new ServiceInfo();
			info.setClazzName(keys.next());
			Service service = services.get(info.getClazzName());
			info.setServiceName(service.getName());
			result.add(info);
		}
		return result;
	}
}
