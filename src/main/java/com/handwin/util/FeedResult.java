package com.handwin.util;

import com.google.gson.annotations.Expose;

public class FeedResult {
	@Expose
	private int code;
	@Expose
	private String txt;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getTxt() {
		return txt;
	}
	public void setTxt(String txt) {
		this.txt = txt;
	}
	
}
