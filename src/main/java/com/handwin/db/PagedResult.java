package com.handwin.db;

public interface PagedResult {

	public void setPage(int page);
	public void setTotal(int total);
	public void setPageSize(int pageSize);
}
