/*
 * �������� 2005-8-18
 *
 * TODO Ҫ��Ĵ���ɵ��ļ���ģ�壬��ת��
 * ���� �� ��ѡ�� �� Java �� ������ʽ �� ����ģ��
 */
package com.handwin.db;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 
 */
public class DataService implements Dao{
	private static Logger logger = Logger.getLogger(DataService.class);
	public DataService() {
		HibernateTool.registerInterceptor(null);
		HibernateTool.beginTransaction();
	}
	public DataService(Interceptor i)
	{
		HibernateTool.registerInterceptor(i);
		HibernateTool.beginTransaction();
	}

	public <T>T delete(T po) throws HException{
		try {
			HibernateTool.getSession().delete(po);
		} catch (HibernateException ex) {
			throw new HException(ex);
		}
		return po;
	}

	public <T>T insert(T dpo) throws HException{
		try {
			HibernateTool.getSession().save(dpo);
		} catch (HibernateException ex) {
			ex.printStackTrace();
			throw new HException(ex);
		}
		return dpo;
	}
	public <T>T save(T dpo) throws HException{

		try {
			HibernateTool.getSession().saveOrUpdate(dpo);
		} catch (HibernateException ex) {
			throw new HException(ex);
		}
		return dpo;

	}

	public void begin() {
		HibernateTool.beginTransaction();
	}
	public Criteria createQuery(Class clz){
		return this.getSession().createCriteria(clz);
	}
	public void commit() {
		// logger.info("DataService.commit begin");
		HibernateTool.commitTransaction();
		clear();
		// logger.info("DataService.commit end");
	}

	public void rollBack() {
		// logger.info("DataService.rollBack begin");
		HibernateTool.rollbackTransaction();
		// logger.info("DataService.rollBack end");
	}

	public void close() {
		// logger.info("DataService.close begin");
		
		HibernateTool.closeSession();
		// logger.info("DataService.close end");
	}

	public void close(ResultSet result, PreparedStatement pstmt) {
		try {
			if (result != null)
				result.close();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}
		try {
			if (pstmt != null)
				pstmt.close();
		} catch (SQLException e2) {

			e2.printStackTrace();
		}
	}
	/**
	 * 根据多个字段条件，获取唯一数据，一般可以用在逻辑主键是联合主键的场景。
	 * @param clz  Hibernate类
	 * @param value 查询条件数据数组
	 * @param column 查询条件列数组
	 * @return 唯一对象，如果没有找到，返回Null
	 */
	public <T> T locate(Class<T> clz,Object value[],String column[])
	{
		Session session = HibernateTool.getSession();
		try
		{
			Criteria query=session.createCriteria(clz);
			
			for(int i=0;i<value.length;i++)
				query.add(Restrictions.eq(column[i], value[i]));
			query.setMaxResults(1);
			if(query.list().size()>0)
				return (T)query.list().get(0);
		}catch(HibernateException e)
		{
			e.printStackTrace();
			return null;
		}
		return null;
	}
	public <T> T locate(Class<T> clz,KeyGroup group)
	{
		Session session = HibernateTool.getSession();
		try
		{
			Criteria query=session.createCriteria(clz);
			
			for(int i=0;i<group.getKeys().size();i++)
				query.add(Restrictions.eq(group.getKeys().get(i).getColumn(), group.getKeys().get(i).getValue()));
			query.setMaxResults(1);
			if(query.list().size()>0)
				return (T)query.list().get(0);
		}catch(HibernateException e)
		{
			//e.printStackTrace();
			return null;
		}
		return null;
	}
	public <T> T locateLock(Class<T> clz,Object value[],String column[])
	{
		Session session = HibernateTool.getSession();
		try
		{
			Criteria query=session.createCriteria(clz);
			
			for(int i=0;i<value.length;i++)
				query.add(Restrictions.eq(column[i], value[i]));
			query.setMaxResults(1);
			if(query.list().size()>0)
			{
				Object rlt= query.list().get(0);
				session.buildLockRequest(LockOptions.UPGRADE).lock(rlt);
				return (T)rlt;
			}
		}catch(HibernateException e)
		{
			return null;
		}
		return null;
	}
	public void flush()
	{
		Session session = HibernateTool.getSession();
		try
		{
			session.flush();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void clear()
	{
		Session session = HibernateTool.getSession();
		try
		{
			session.clear();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public <T> T locate(Class<T> clz, Serializable id) {
		if(id==null)
			return null;
		Session session = HibernateTool.getSession();
		Object bpo = null;

		try {
			bpo = session.get(clz, id);

		} catch (HibernateException e) {
			e.printStackTrace();
			return null;
		}
		return (T)bpo;
	}
	public <T> T locateLock(Class<T> clz, Serializable id) {
		if(id==null)
			return null;
		Session session = HibernateTool.getSession();
		Object bpo = null;

		try {
			bpo = session.get(clz, id, LockOptions.UPGRADE);
			//bpo.toString();
		} catch (HibernateException e) {
			//e.printStackTrace();
			return null;
			// throw new HException(e);
		}
		return (T)bpo;
	}

	

	public Session getSession() {
		return HibernateTool.getSession();
	}

	public static void main(String[] args) {

	}
}