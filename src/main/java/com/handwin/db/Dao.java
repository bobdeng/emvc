package com.handwin.db;

import java.io.Serializable;
import java.sql.Connection;

import org.hibernate.Session;


public interface Dao {

	public void commit()throws HException;
	public void close()throws HException;
	public void rollBack()throws HException;
	public <T>T insert(T dpo) throws HException;
	public <T>T delete(T dpo) throws HException;
	public <T>T save(T dpo) throws HException;
	public <T>T locate(Class<T> clz, Serializable id)throws HException;
	public  <T>T locate(Class<T> clz, Object value[], String column[])throws HException;
	public  <T>T locateLock(Class<T> clz, Object value[], String column[])throws HException;
	public  <T>T locateLock(Class<T> clz, Serializable id)throws HException;
	public Session getSession()throws HException;
	public void flush()throws HException;
	public void clear()throws HException;
    
}
