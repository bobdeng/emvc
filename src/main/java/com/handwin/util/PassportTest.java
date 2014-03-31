package com.handwin.util;

public class PassportTest {

	private String name;
	private int age;
	private long page;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public long getPage() {
		return page;
	}
	public void setPage(long page) {
		this.page = page;
	}
	public PassportTest(String name, int age, long page) {
		super();
		this.name = name;
		this.age = age;
		this.page = page;
	}
	public PassportTest() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
