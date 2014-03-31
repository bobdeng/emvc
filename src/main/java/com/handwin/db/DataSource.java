package com.handwin.db;

import java.util.List;
/**
 * 
 * @author Administrator
 * @deprecated
 */
public class DataSource {

	private List<?> data;
	private int index;
	private int total;
	public List<?> getData() {
		return data;
	}
	public void setData(List<?> data) {
		this.data = data;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
}
