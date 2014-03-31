/*
 * �������� 2005-8-18
 *
 * TODO Ҫ��Ĵ���ɵ��ļ���ģ�壬��ת��
 * ���� �� ��ѡ�� �� Java �� ������ʽ �� ����ģ��
 */
package com.handwin.db;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;



/**
 * Basic Hibernate helper class, handles SessionFactory, Session and
 * Transaction.
 * <p>
 * Uses a static initializer for the initial SessionFactory creation and holds
 * Session and Transactions in thread local variables. All exceptions are
 * wrapped in an unchecked InfrastructureException.
 * 
 * @author christian@hibernate.org
 */
public class HibernateTool {

	private static Logger log = Logger.getLogger(HibernateTool.class);

	private static Configuration config;

	private static SessionFactory sessionFactory;

	private static final ThreadLocal threadSession = new ThreadLocal();

	private static final ThreadLocal threadTransaction = new ThreadLocal();

	private static final ThreadLocal threadInterceptor = new ThreadLocal();
	private static Map<String,Integer> connections=new HashMap<String,Integer>();
	
	public static Map<String, Integer> getConnections() {
		return connections;
	}

	// Create the initial SessionFactory from the default configuration files
	static {
		try {
			config = new Configuration();
			Configuration cf=config.configure();
			// Properties prop = new Properties();
			// prop.setProperty("hibernate.connection.username",Configure.dbUser);
			// prop.setProperty("hibernate.connection.password",Configure.dbPassword);
			// prop.setProperty("hibernate.connection.url",Configure.dbURL);
			// config.addProperties(prop);
			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(cf.getProperties()).build(); 
			sessionFactory = cf.buildSessionFactory(serviceRegistry);
			// We could also let Hibernate bind it to JNDI:
			// configuration.configure().buildSessionFactory()
		} catch (Throwable ex) {
			// We have to catch Throwable, otherwise we will miss
			// NoClassDefFoundError and other subclasses of Error
			log.error("Building SessionFactory failed.", ex);
			ex.printStackTrace();
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * Returns the SessionFactory used for this static class.
	 *
	 * @return SessionFactory
	 */
	public static SessionFactory getSessionFactory() {
		/*
		 * Instead of a static variable, use JNDI: SessionFactory sessions =
		 * null; try { Context ctx = new InitialContext(); String jndiName =
		 * "java:hibernate/HibernateFactory"; sessions =
		 * (SessionFactory)ctx.lookup(jndiName); } catch (NamingException ex) {
		 * throw new InfrastructureException(ex); } return sessions;
		 */
		return sessionFactory;
	}

	/**
	 * Returns the original Hibernate configuration.
	 *
	 * @return Configuration
	 */
	public static Configuration getConfiguration() {
		return config;
	}

	/**
	 * Rebuild the SessionFactory with the static Configuration.
	 *
	 */
//	public static void rebuildSessionFactory() throws HException {
//		synchronized (sessionFactory) {
//			try {
//				sessionFactory = getConfiguration().buildSessionFactory();
//			} catch (Exception ex) {
//				throw new HException(ex);
//			}
//		}
//	}

	/**
	 * Rebuild the SessionFactory with the given Hibernate Configuration.
	 *
	 * @param cfg
	 */
	public static void rebuildSessionFactory(Configuration cfg)
			throws HException {
		synchronized (sessionFactory) {
			try {
				sessionFactory = cfg.buildSessionFactory();
				config = cfg;
			} catch (Exception ex) {
				throw new HException(ex);
			}
		}
	}

	/**
	 * Retrieves the current Session local to the thread. <p/>If no Session is
	 * open, opens a new Session for the running thread.
	 *
	 * @return Session
	 */
	public static Session getSession() throws HException {
		Session s = (Session) threadSession.get();
		try {
			
			if (s == null) {
				log.debug("Opening new Session for this thread.");
				if (getInterceptor() != null) {
					
					s = getSessionFactory().withOptions().interceptor(getInterceptor()).openSession();
				} else {
					s = getSessionFactory().openSession();
				}
				
				threadSession.set(s);
				setConnections(1);
			}
		} catch (HibernateException ex) {
			throw new HException(ex);
		}
		return s;
	}

	/**
	 * Closes the Session local to the thread.
	 */
	public static void closeSession() throws HException {
		try {
			commitTransaction();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			rollbackTransaction();
			e.printStackTrace();
		}
		try {
			
			Session s = (Session) threadSession.get();
			threadSession.set(null);
			threadTransaction.set(null);
			if (s != null && s.isOpen()) {
				s.close();
				setConnections(-1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	private static String getCurrentClassName(){
		Field f;
		try {
			f = Thread.class.getDeclaredField("target");
			f.setAccessible(true);
			if(f.get(Thread.currentThread())==null) return "null";
			return (f.get(Thread.currentThread()).getClass().getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	private static void setConnections(int i)
	{
		synchronized (connections) {
			Integer count=connections.get(getCurrentClassName());
			if(count==null){
				count=i;
				
			}
			else count+=i;
				connections.put(getCurrentClassName(), count);
		}
	}
	/**
	 * Start a new database transaction.
	 */
	public static void beginTransaction() throws HException {
		
		Transaction tx = (Transaction) threadTransaction.get();
		try {
			
			if (tx == null) {
				// log.debug("Starting new database transaction in this
				// thread.");
				//Session =getSession();
				tx = getSession().beginTransaction();
				threadTransaction.set(tx);
				
			}
		} catch (HibernateException ex) {
			throw new HException(ex);
		}
	}

	/**
	 * Commit the database transaction.
	 */
	public static void commitTransaction() throws HException {
		
		Transaction tx = (Transaction) threadTransaction.get();
		try {
			if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
				// log.debug("Committing database transaction of this thread.");
				tx.commit();
			}
			threadTransaction.set(null);
		} catch (HibernateException ex) {
			ex.printStackTrace();
			throw new HException(ex);
		}
	}

	/**
	 * Commit the database transaction.
	 */
	public static void rollbackTransaction() throws HException {
		
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

	
	/**
	 * Register a Hibernate interceptor with the current thread.
	 * <p>
	 * Every Session opened is opened with this interceptor after registration.
	 * Has no effect if the current Session of the thread is already open,
	 * effective on next close()/getSession().
	 */
	public static void registerInterceptor(Interceptor interceptor) {
		threadInterceptor.set(interceptor);
	}

	private static Interceptor getInterceptor() {
		Interceptor interceptor = (Interceptor) threadInterceptor.get();
		return interceptor;
	}

	
	public static Object locate(Class hibernateClass, String key)
			throws HibernateException {
		Session session = getSession(); // �õ�Hibernate Session
		if (session == null) {

		}
		try {

			return session.load(hibernateClass, key);

		} catch (HibernateException e) {
			return null;
		} finally {
			try {
				// session.close(); // �ر�Hibernate �Ự
				//HibernateTool.closeSession();
			} catch (Exception e) {

			}
		}
	}

	
	public static void main(String[] args) {
		
	}
}
