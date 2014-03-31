package com.handwin.db;

import java.util.List;

public interface FlipResult {

	public void setTotalPage(int page);
	public int getTotalPage();
	public void setTotalRecord(int total);
	public int getTotalRecord();
	public void setData(List data);
	public List getData();
	public void setCurPage(int page);
	public int getCurPage();

}
