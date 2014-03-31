package com.handwin.util;

import java.util.List;

import com.google.gson.annotations.Expose;

public class GetShortKeyNewResult {
	@Expose
	private int rlt;
	@Expose
	private List<String> keys;
	@Expose
	private String txt;
	public int getRlt() {
		return rlt;
	}
	public void setRlt(int rlt) {
		this.rlt = rlt;
	}
	public List<String> getKeys() {
		return keys;
	}
	public void setKeys(List<String> keys) {
		this.keys = keys;
	}
	public String getTxt() {
		return txt;
	}
	public void setTxt(String txt) {
		this.txt = txt;
	}
	
}
