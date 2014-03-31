package com.handwin.util;

import com.google.gson.annotations.Expose;

public class DirtyWordResult{
	@Expose
	private String[] dirtyWords;
	@Expose
	private int rlt;
	@Expose
	private String txt;
	public String[] getDirtyWords() {
		return dirtyWords;
	}
	public void setDirtyWords(String[] dirtyWords) {
		this.dirtyWords = dirtyWords;
	}
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
	
}
