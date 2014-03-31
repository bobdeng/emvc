package com.handwin.web.json;

public class DefaultActionCreator implements ActionCreator {

	@Override
	public JsonAction createInstance(Class clz) {
		// TODO Auto-generated method stub
		try{
			return (JsonAction)clz.newInstance();
		}catch(Exception e){
			return null;
		}
	}

}
