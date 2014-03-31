package com.handwin.web.json;

import com.google.gson.annotations.Expose;

public class ServiceInfo {
	@Expose
	private String clazzName;
	@Expose
	private String serviceName;
	@Expose
	private boolean running;
	public String getClazzName() {
		return clazzName;
	}
	public void setClazzName(String clazzName) {
		this.clazzName = clazzName;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	
}
