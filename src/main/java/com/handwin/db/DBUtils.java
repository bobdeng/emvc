package com.handwin.db;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

/**
 * 数据库常用工具类，如分页
 * @author 邓志国
 *
 */
public class DBUtils {

	
	private static void setPage(int total,int pageSize,int curPage,FlipResult rlt)
	{
		if(curPage==0) curPage=1;
		int totalPage=(total/pageSize)+(total%pageSize==0?0:1);
		rlt.setTotalPage(totalPage);
		if(curPage>totalPage)curPage=totalPage;
		rlt.setCurPage(curPage);
		rlt.setTotalRecord(total);
	}
	public static void doFlipSearch(Criteria query,FlipResult rlt,int curPage,int pageSize)
	{
		query.setProjection(Projections.rowCount());
		int count=(Integer)query.uniqueResult();
		DBUtils.setPage(count, pageSize, curPage, rlt);
		query.setProjection(null);
		query.setMaxResults(pageSize);
		query.setFirstResult((rlt.getCurPage()-1)*pageSize);
		rlt.setData(query.list());		
	}
	public static void main(String[] args) {
		int total=20;
		int pageSize=10;
		System.out.println((total/pageSize)+(total%pageSize==0?0:1));
	}
	
}

