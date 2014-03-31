package com.handwin.db;

import java.util.ArrayList;
import java.util.List;

public class Row {

	private int id;
	private Object data;
	private boolean deleted;
	
	public Row(Object data, boolean deleted) {
		super();
		this.data = data;
		this.deleted = deleted;
	}
	public Row()
	{
		
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public Row(Object data) {
		super();
		this.data = data;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
//	public void addField(Object field)
//	{
//		fields.add(field);
//	}
//	public String asString(int id)
//	{
//		Object value=fields.get(id);
//		if(value==null) return null;
//		return value.toString();
//	}
//	public int asInteger(int id)
//	{
//		Object value=fields.get(id);
//		if(value instanceof String)
//		{
//			return Integer.parseInt(value.toString());
//		}
//		if(value instanceof Long)
//		{
//			return ((Long)value).intValue();
//		}
//		if(value instanceof Byte)
//			return ((Byte)value).intValue();
//		if(value instanceof Short)
//			return ((Short)value).intValue();
//		if(value instanceof Integer)
//			return ((Integer)value);
//		return 0;
//	}
//	public long asLong(int id)
//	{
//		Object value=fields.get(id);
//		if(value instanceof String)
//		{
//			return Long.parseLong(value.toString());
//		}
//		if(value instanceof Long)
//		{
//			return ((Long)value);
//		}
//		if(value instanceof Byte)
//			return ((Byte)value).intValue();
//		if(value instanceof Short)
//			return ((Short)value).intValue();
//		if(value instanceof Integer)
//			return ((Integer)value).intValue();
//		return 0;
//		
//	}
}
