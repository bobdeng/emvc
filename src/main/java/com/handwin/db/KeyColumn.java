package com.handwin.db;

public class KeyColumn {

	private String column;
	private Object value;
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public KeyColumn(String column, Object value) {
		super();
		this.column = column;
		this.value = value;
	}
}
