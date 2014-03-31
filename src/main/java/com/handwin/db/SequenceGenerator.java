package com.handwin.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.ReentrantLock;

public class SequenceGenerator {

	private static String rootPath="d:\\phonebook\\";
	private static String path = "d:\\phonebook\\sequence.dat";
	private static String backPath = "d:\\phonebook\\sequence_bak.dat";
	private ReentrantLock lock = new ReentrantLock();
	private static final int ID_COUNT = 100;
	private RandomAccessFile raFile = null;
	private MappedByteBuffer mbb = null;
	private FileChannel fc = null;
	private static SequenceGenerator instance = null;
	public static final int SEQ_PHONE_BOOK = 0;
	public static final int SEQ_APPLY = 1;

	public static void setRootPath(String root)
	{
		path=new File(root,"sequence.dat").getPath();
		backPath=new File(root,"sequence_bak.dat").getPath();
	}
	public static void init(String pathFile) {
		path = pathFile;
	}
	
	private SequenceGenerator() throws IOException {

		init();

	}
	
	public void setId(int id,long value)
	{
		lock.lock();
		try {
			mbb.position(id * Long.SIZE/8);
			mbb.putLong(value);
			mbb.force();
			return;
		} finally {
			lock.unlock();
		}
		
	}
	public static SequenceGenerator getInstance() throws IOException {
		if (instance == null)
			instance = new SequenceGenerator();
		return instance;
	}

	public static void main(String[] args) throws IOException {
		SequenceGenerator.setRootPath("c:\\");
		getInstance();
		backup();
		restore();
	}

	private static void copyFile(String from, String to) throws IOException {
		File fromFile=new File(from);
		if(!fromFile.exists())
			return;
		FileOutputStream out = new FileOutputStream(new File(to));
		FileInputStream in = new FileInputStream(fromFile);
		byte[] buffer = new byte[8];
		while (true) {
			int read = in.read(buffer);
			if (read <= 0)
				break;
			out.write(buffer, 0, read);
			out.flush();

		}
		
		out.close();
		in.close();
		fromFile.delete();

	}

	public static void  backup() throws IOException {
		copyFile(path, backPath);
	}

	public static void restore()throws IOException
	{
		getInstance().close();
		copyFile(backPath, path);
	}
	
	public void init() throws IOException {
		File file = new File(path);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			for (int i = 0; i < ID_COUNT; i++) {
				out.write(0);
				out.write(0);
				out.write(0);
				out.write(0);
				out.write(0);
				out.write(0);
				out.write(0);
				out.write(0);
			}
			out.flush();
			out.close();
		}
		raFile = new RandomAccessFile(file, "rw");
		fc = raFile.getChannel();
		mbb = fc.map(FileChannel.MapMode.READ_WRITE, 0, fc.size());
	}

	public long getNext(int field) throws IOException {
		if (field >= ID_COUNT)
			throw new IOException("");
		lock.lock();
		try {
			mbb.position(field * Long.SIZE/8);
			long rlt = mbb.getLong() + 1;
			mbb.position(field * Long.SIZE/8);
			mbb.putLong(rlt);
			mbb.force();
			return rlt;
		} finally {
			lock.unlock();
		}
	}

	public void close() throws IOException {
		
		if(fc!=null)
		{
			mbb=null;
			fc.close();
			
		}
		if(raFile!=null)
		{
			raFile.close();
			
		}
		System.gc();
	}
}
