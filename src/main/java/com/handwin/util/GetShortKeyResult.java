package com.handwin.util;

import com.google.gson.annotations.Expose;

public class GetShortKeyResult {
	@Expose
	private int rlt;
	@Expose
	private String txt;
	@Expose
	private long key;
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
	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
	}
	
}
