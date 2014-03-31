package com.handwin.db;

import java.util.ArrayList;
import java.util.List;

public class KeyGroup {

	public List<KeyColumn> keys;
	public KeyGroup(){
		keys=new ArrayList<KeyColumn>();
	}
	public KeyGroup addKeyColumn(String name,Object value){
		keys.add(new KeyColumn(name,value));
		return this;
	}
	public List<KeyColumn> getKeys() {
		return keys;
	}
	public void setKeys(List<KeyColumn> keys) {
		this.keys = keys;
	}
	
}
