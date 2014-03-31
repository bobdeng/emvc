package com.handwin.web.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.handwin.web.BytesUtils;

public class JsonObject {
	public static final int JSON_TYPE_MASK = 0X0F;
	public static final int JSON_TYPE_STRING = 0X01;
	public static final int JSON_TYPE_INTEGER = 0X02;
	public static final int JSON_TYPE_SHORT = 0X03;
	public static final int JSON_TYPE_BYTE = 0X04;
	public static final int JSON_TYPE_LONG = 0X0A;
	public static final int JSON_TYPE_STRING_ARRAY = 0X05;
	public static final int JSON_TYPE_INTEGER_ARRAY = 0X06;
	public static final int JSON_TYPE_SHORT_ARRAY = 0X07;
	public static final int JSON_TYPE_BYTE_ARRAY = 0X08;
	public static final int JSON_TYPE_OBJ_ARRAY = 0X09;
	public static final int JSON_TYPE_LONG_ARRAY = 0X0B;
	public static final int JSON_TYPE_OBJ=0X0C;
	private Map<String, Object> values = new HashMap<String, Object>();
	private int version;
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public byte[] toByteArray() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			out.write("JSONBIN".getBytes());
			writeValus(values, out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out.toByteArray();
	}
	private void writeString(String name,OutputStream out)throws IOException
	{
		if(this.version==0)
			BytesUtils.writeUTF(out, name);
		else
			BytesUtils.writeUTF8(out, name);
		
	}
	private void writeValus(Map<String, Object> values, OutputStream out)
			throws IOException {
		BytesUtils.writeShort(out, values.keySet().size());
		Iterator<String> its = values.keySet().iterator();
		while (its.hasNext()) {
			String name = its.next();
			Object obj = values.get(name);
			if (obj instanceof Integer) {
				out.write(JSON_TYPE_INTEGER);
				writeString(name,out);
				BytesUtils.writeInt(out, (Integer) obj);
				continue;
			}
			if (obj instanceof Long) {
				out.write(JSON_TYPE_LONG);
				writeString(name,out);
				BytesUtils.writeLong(out, (Long) obj);
				continue;
			}
			if (obj instanceof Byte) {
				out.write(JSON_TYPE_BYTE);
				writeString(name,out);
				out.write((Byte) obj);
				continue;
			}
			if (obj instanceof Short) {
				out.write(JSON_TYPE_SHORT);
				writeString(name,out);
				BytesUtils.writeShort(out, (Short) obj);
				continue;
			}
			if (obj instanceof String) {
				out.write(JSON_TYPE_STRING);
				writeString(name,out);
				if(this.version==0)
					BytesUtils.writeUTF(out, (String) obj);
				else
					BytesUtils.writeUTF8(out, (String) obj);
				continue;
			}
			if (obj instanceof int[]) {
				out.write(JSON_TYPE_INTEGER_ARRAY);
				writeString(name,out);
				writeIntArray(out, (int[]) obj);
				continue;
			}
			if (obj instanceof long[]) {
				out.write(JSON_TYPE_LONG_ARRAY);
				writeString(name,out);
				writeLongArray(out, (long[]) obj);
				continue;
			}
			if (obj instanceof short[]) {
				out.write(JSON_TYPE_SHORT_ARRAY);
				writeString(name,out);
				writeShortArray(out, (short[]) obj);
				continue;
			}
			if (obj instanceof byte[]) {
				out.write(JSON_TYPE_BYTE_ARRAY);
				writeString(name,out);
				writeByteArray(out, (byte[]) obj);
				continue;
			}
			if (obj instanceof String[]) {
				out.write(JSON_TYPE_STRING_ARRAY);
				writeString(name,out);
				writeStringArray(out, (String[]) obj);
				continue;
			}
			if (obj instanceof JsonObject[]) {
				out.write(JSON_TYPE_OBJ_ARRAY);
				writeString(name,out);
				JsonObject[] data = (JsonObject[]) obj;
				BytesUtils.writeShort(out, data.length);
				for (int i = 0; i < data.length; i++) {
					writeValus(data[i].getValues(), out);
				}
				continue;
			}
			if(obj instanceof JsonObject)
			{
				out.write(JSON_TYPE_OBJ);
				writeString(name,out);
				writeValus(((JsonObject)obj).getValues(), out);
				continue;
			}
			
		}

	}

	public void addParams(String name, Object object) {
		values.put(name, object);
	}

	private static void writeIntArray(OutputStream out, int[] data)
			throws IOException {
		BytesUtils.writeShort(out, data.length);
		for (int i = 0; i < data.length; i++) {
			BytesUtils.writeInt(out, data[i]);
		}
	}

	private static void writeLongArray(OutputStream out, long[] data)
			throws IOException {
		BytesUtils.writeShort(out, data.length);
		for (int i = 0; i < data.length; i++) {
			BytesUtils.writeLong(out, data[i]);
		}
	}

	private static void writeShortArray(OutputStream out, short[] data)
			throws IOException {
		BytesUtils.writeShort(out, data.length);
		for (int i = 0; i < data.length; i++) {
			BytesUtils.writeShort(out, data[i]);
		}
	}

	private static void writeByteArray(OutputStream out, byte[] data)
			throws IOException {
		BytesUtils.writeShort(out, data.length);
		out.write(data);
	}

	private  void writeStringArray(OutputStream out, String[] data)
			throws IOException {
		BytesUtils.writeShort(out, data.length);
		for (int i = 0; i < data.length; i++) {
			if(version==0)
				BytesUtils.writeUTF(out, data[i]);
			else
				BytesUtils.writeUTF8(out, data[i]);
			
		}
	}

	public Map<String, Object> getValues() {
		return values;
	}

//	public static JsonObject ObjToJsonObject(Object obj) {
//		JsonObject result = new JsonObject();
//		Class<? extends Object> ownerClass = obj.getClass();
//		Method[] methods = ownerClass.getDeclaredMethods();// 得到某类的所有方法
//		try {
//			for (Method method : methods) {
//				String name = method.getName().substring(3).toLowerCase();
//				if (method.getName().startsWith("get")) {// 取得方法的名称，判断方法名称是否以get开头！
//					Object value = method.invoke(obj);
//					if (value instanceof List) {
//						Type type = method.getGenericReturnType();
//						if (type instanceof ParameterizedType)/**//* 如果是泛型类型 */{
//							Type[] types = ((ParameterizedType) type)
//									.getActualTypeArguments();
//							Class c = (Class) types[0];
//							List list = (List) value;
//							if (c.isPrimitive()
//									|| c.getName().trim().equals(
//											"java.lang.String")) {
//								result.addParams(name, list.toArray());
//							} else {
//								JsonObject[] objects = new JsonObject[list
//										.size()];
//								for (int i = 0; i < list.size(); i++) {
//									objects[i] = ObjToJsonObject(list.get(i));
//								}
//								result.addParams(name, objects);
//							}
//						}
//
//					} else if (value instanceof Integer
//							|| value instanceof Long || value instanceof Byte
//							|| value instanceof Short
//							|| value instanceof String
//							|| value instanceof int[]
//							|| value instanceof long[]
//							|| value instanceof short[]
//							|| value instanceof byte[]
//							|| value instanceof String[]) {
//						result.addParams(name, value);
//					} else {
//						result.addParams(name, ObjToJsonObject(value));
//					}
//				}
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return result;
//	}

	public static Object parseData(byte[] data, Class clz) throws Exception {

		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		byte[] head=new byte[7];
		stream.read(head);
		if(!new String(head).equals("JSONBIN"))
		{
			return null;
		}
		return read(stream, clz);

	}

	public static JsonObject ObjToJsonObject(Object obj) {
		if (obj == null)
			return null;
		JsonObject result = new JsonObject();
		Class<? extends Object> ownerClass = obj.getClass();
		Field[] fields = ownerClass.getDeclaredFields();
		for (Field f : fields) {
			if (f.isAnnotationPresent(Expose.class)) {
				String name = f.getName();
				f.setAccessible(true);
				try {
					Object value = f.get(obj);
					if(value==null){
						continue;
					}else if (value instanceof List) {
						Type type = f.getGenericType();
						if (type instanceof ParameterizedType)/**//* 如果是泛型类型 */{
							Type[] types = ((ParameterizedType) type)
									.getActualTypeArguments();
							Class c = (Class) types[0];
							List list = (List) value;
							if (c.isPrimitive()
									|| c.getName().trim().equals(
											"java.lang.String")) {
								result.addParams(name, list.toArray());
							} else {
								JsonObject[] objects = new JsonObject[list
										.size()];
								for (int i = 0; i < list.size(); i++) {
									objects[i] = ObjToJsonObject(list.get(i));
								}
								result.addParams(name, objects);
							}
						}

					} else if (value instanceof Integer
							|| value instanceof Long || value instanceof Byte
							|| value instanceof Short
							|| value instanceof String
							|| value instanceof int[]
							|| value instanceof long[]
							|| value instanceof short[]
							|| value instanceof byte[]
							|| value instanceof String[]) {
						result.addParams(name, value);
					} else {
						result.addParams(name, ObjToJsonObject(value));
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	private static Object read(InputStream stream, Class clz) throws Exception {
		int count = BytesUtils.ReadInt(stream, 2);
		if (count == 0)
			return null;
		Object rlt = clz.newInstance();
		for (int i = 0; i < count; i++) {
			int type = BytesUtils.ReadInt(stream, 1);
			String name = BytesUtils.ReadUTF(stream);
			// System.out.println("name==" + name);
			Field field = clz.getDeclaredField(name);
			if (field == null)
				continue;
			field.setAccessible(true);
			switch (type & JsonObject.JSON_TYPE_MASK) {
			case JsonObject.JSON_TYPE_BYTE:
				field.set(rlt, BytesUtils.ReadInt(stream, 1));
				break;
			case JsonObject.JSON_TYPE_BYTE_ARRAY:
				field.set(rlt, readByteArray(stream));
				break;
			case JsonObject.JSON_TYPE_INTEGER:
				field.set(rlt, BytesUtils.ReadInt(stream, 4));
				break;
			case JsonObject.JSON_TYPE_INTEGER_ARRAY:
				field.set(rlt, readIntArray(stream, 4));
				break;
			case JsonObject.JSON_TYPE_OBJ_ARRAY:
				field.set(rlt, readObjectArray(stream, field.getClass()));
				break;
			case JsonObject.JSON_TYPE_SHORT:
				field.set(rlt, BytesUtils.ReadInt(stream, 2));
				break;
			case JsonObject.JSON_TYPE_SHORT_ARRAY:
				field.set(rlt, readIntArray(stream, 2));
				break;
			case JsonObject.JSON_TYPE_STRING:
				field.set(rlt, BytesUtils.ReadUTF(stream));
				break;
			case JsonObject.JSON_TYPE_STRING_ARRAY:
				field.set(rlt, readStringArray(stream));
				break;
			case JsonObject.JSON_TYPE_LONG:
				field.set(rlt, BytesUtils.readLong(stream, 8));
				break;
			case JsonObject.JSON_TYPE_LONG_ARRAY:
				field.set(rlt, readLongArray(stream, 8));
				break;
			}
		}
		return rlt;
	}

	private static int[] readIntArray(InputStream stream, int size)
			throws Exception {
		int len = BytesUtils.ReadInt(stream, 2);
		if (len == 0)
			return null;
		int[] rlt = new int[len];
		for (int i = 0; i < len; i++) {
			rlt[i] = BytesUtils.ReadInt(stream, size);
		}
		return rlt;
	}

	private static byte[] readByteArray(InputStream stream) throws Exception {
		int len = BytesUtils.ReadInt(stream, 2);
		if (len == 0)
			return null;
		byte[] rlt = new byte[len];
		stream.read(rlt);
		return rlt;
	}

	private static long[] readLongArray(InputStream stream, int size)
			throws Exception {
		int len = BytesUtils.ReadInt(stream, 2);
		if (len == 0)
			return null;
		long[] rlt = new long[len];
		for (int i = 0; i < len; i++) {
			rlt[i] = BytesUtils.readLong(stream, size);
		}
		return rlt;
	}

	private static String[] readStringArray(InputStream stream)
			throws Exception {
		int len = BytesUtils.ReadInt(stream, 2);
		if (len == 0)
			return null;
		String[] rlt = new String[len];
		for (int i = 0; i < len; i++) {
			rlt[i] = BytesUtils.ReadUTF(stream);
		}
		return rlt;
	}

	private static Object[] readObjectArray(InputStream stream, Class clz)
			throws Exception {
		int len = BytesUtils.ReadInt(stream, 2);
		if (len == 0)
			return null;
		Object[] rlt = new Object[len];
		for (int i = 0; i < len; i++) {
			rlt[i] = read(stream, clz);
		}
		return rlt;
	}
}
