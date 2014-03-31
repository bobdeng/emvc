package com.handwin.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class MuiltDao implements Dao {

	private static Logger log = Logger.getLogger(DataService.class);

	private static Configuration config;

	private static Map<String,SessionFactory> sessionFactories;

	private static final ThreadLocal<Session> threadSession = new ThreadLocal<Session>();

	private static final ThreadLocal<Transaction> threadTransaction = new ThreadLocal<Transaction>();

	private static final ThreadLocal<Interceptor> threadInterceptor = new ThreadLocal<Interceptor>();

	public static void initConfig(List<Database> databases,String configXml)
	{
		sessionFactories=new HashMap<String,SessionFactory>();
		for(Database db:databases)
		try {
			log.info("初始化数据库配置："+db.getName());
			config = new Configuration();
			config.configure(configXml);
			log.info("--用户名："+db.getUserName());
			config.setProperty("hibernate.connection.username",db.getUserName());
			log.info("--密码："+db.getPassword());
			config.setProperty("hibernate.connection.password",db.getPassword());
			log.info("--URL："+db.getUrl());
			config.setProperty("hibernate.connection.url",db.getUrl());
			log.info("--驱动："+db.getDriver());
			config.setProperty("hibernate.connection.driver_class",db.getDriver());
			log.info("--Dialect："+db.getDialect());
			config.setProperty("dialect",db.getDialect());
			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(config.getProperties()).build(); 
			SessionFactory sf=config.buildSessionFactory(serviceRegistry);
			sessionFactories.put(db.getName(), sf);
			log.info("初始化完毕");
		} catch (Throwable ex) {
			log.error("Building SessionFactory failed.", ex);
			ex.printStackTrace();
			throw new ExceptionInInitializerError(ex);
		}
	}
	private String dbName;
	public MuiltDao(String dbName)throws HException
	{
		this.dbName=dbName;
		Transaction tx = (Transaction) threadTransaction.get();
		if(tx==null)
		{
			Session session=this.getSession();
			if(session!=null)
			{
				tx=session.beginTransaction();
				this.threadTransaction.set(tx);
			}else
				throw new HException("Database open fail:"+dbName);
		}
	
	}
	
	public void commit() throws HException{
		// TODO Auto-generated method stub
		Transaction tx = threadTransaction.get();
		try {
			if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
				tx.commit();
			}
			threadTransaction.set(null);
		} catch (HibernateException ex) {
			ex.printStackTrace();
			throw new HException(ex);
		}
	}

	
	public void close() throws HException{

		try {		
			threadInterceptor.set(null);
			Session s = (Session) threadSession.get();
			//s.setCacheMode(CacheMode.)
			threadSession.set(null);
			threadTransaction.set(null);
			if (s != null && s.isOpen()) {
				s.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
	public void rollBack() throws HException{
		// TODO Auto-generated method stub
		Transaction tx = (Transaction) threadTransaction.get();
		try {
			threadTransaction.set(null);
			if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
				tx.rollback();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			//closeSession();
		}
	}

	
	public <T>T insert(T dpo) throws HException {
		// TODO Auto-generated method stub
		this.getSession().save(dpo);
		return dpo;
	}

	
	public <T>T delete(T dpo) throws HException {
		// TODO Auto-generated method stub
		this.getSession().delete(dpo);
		return dpo;
	}

	
	public <T>T save(T dpo) throws HException {
		// TODO Auto-generated method stub
		this.getSession().saveOrUpdate(dpo);
		return dpo;
	}

	
	public <T> T locate(Class<T> clz, Serializable id) throws HException{
		if(id==null)
			return null;
		Session session = getSession();
		Object bpo = null;

		try {
			bpo = session.get(clz, id);

		} catch (HibernateException e) {
			e.printStackTrace();
			return null;
		}
		return (T)bpo;
	}

	
	public <T> T locate(Class<T> clz, Object[] value, String[] column)throws HException {
		Session session = getSession();
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
			//e.printStackTrace();
			return null;
		}
		return null;
	}

	
	public <T> T locateLock(Class<T> clz, Object[] value, String[] column)throws HException {
		Session session = getSession();
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

	
	public <T> T locateLock(Class<T> clz, Serializable id)throws HException {
		
		if(id==null)
			return null;
		Session session = getSession();
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

	
	public Session getSession()throws HException {
		if(threadSession.get()!=null)
			return threadSession.get();
		else
		{
			SessionFactory sf=sessionFactories.get(dbName);
			if(sf!=null)
			{
				Session session=sf.openSession();
				threadSession.set(session);
				return session;
			}
		}
		return null;
	}

	
	public void flush() throws HException{
		// TODO Auto-generated method stub
		this.getSession().flush();
	}

	
	public void clear()throws HException {
		// TODO Auto-generated method stub
		this.getSession().clear();
	}
	



}
