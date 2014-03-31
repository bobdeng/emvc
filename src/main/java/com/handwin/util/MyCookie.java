package com.handwin.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.http.cookie.Cookie;

public class MyCookie implements Cookie {

	private String comment;
	private String commentURL;
	private String domain;
	private String name;
	private String value;
	private String path;
	private int[] ports;
	private int version;
	private Date expiryDate;
	
	

	public MyCookie(String domain, String name, String value, String path,
			int[] ports) {
		super();
		this.domain = domain;
		this.name = name;
		this.value = value;
		this.path = path;
		this.ports = ports;
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		this.expiryDate=cal.getTime();
	}

	public MyCookie(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCommentURL() {
		return commentURL;
	}

	public void setCommentURL(String commentURL) {
		this.commentURL = commentURL;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	

	public int[] getPorts() {
		return ports;
	}

	public void setPorts(int[] ports) {
		this.ports = ports;
	}

	public int getVersion() {
		return 1;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public boolean isExpired(Date arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPersistent() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return true;
	}

}
