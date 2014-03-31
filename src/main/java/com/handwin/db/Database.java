package com.handwin.db;

public class Database {

	private String name;
	private String url;
	private String userName;
	private String password;
	private String dialect;
	private String driver;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getDialect() {
		return dialect;
	}
	public void setDialect(String dialect) {
		this.dialect = dialect;
	}
	public Database(String name, String url, String userName, String password,
			String dialect, String driver) {
		super();
		this.name = name;
		this.url = url;
		this.userName = userName;
		this.password = password;
		this.dialect = dialect;
		this.driver = driver;
	}
	
}
