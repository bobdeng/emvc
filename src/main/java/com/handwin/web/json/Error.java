package com.handwin.web.json;

import com.google.gson.annotations.Expose;

public class Error implements ErrorInterface{

	@Expose
	private int rlt=101;
	@Expose
	private String txt=null;
	public int getRlt() {
		return rlt;
	}
	public void setRlt(int rlt) {
		this.rlt = rlt;
	}
	public String getTxt() {
		return txt;
	}
	public void setTxt(String txt) {
		this.txt = txt;
	}
	@Override
	public void setMessage(String msg) {
		// TODO Auto-generated method stub
		txt=msg;
		
	}
	public Error(String txt) {
		super();
		this.txt = txt;
	}
}
