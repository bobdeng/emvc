package com.handwin.util;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 常用工具类
 * 
 * @author Administrator
 * 
 */
public class Utils {

	public static boolean isBlankString(String src) {
		return src == null || src.equals("");
	}

	static String[] pinyin = null;
	private static Gson gson = null;
	static {
		try {
			InputStream stream = Utils.class.getClassLoader()
					.getResourceAsStream("com/handwin/util/quanpin.txt");
			byte[] data = new byte[stream.available()];
			int r = 0;
			while (r < data.length) {
				r += stream.read(data, r, data.length - r);
			}

			pinyin = (new String(data)).split(",");
		} catch (Exception e) {
			e.printStackTrace();
		}
		GsonBuilder builder = new GsonBuilder();
		builder.excludeFieldsWithoutExposeAnnotation();
		gson = builder.create();
	}
	static final int pystart = 19968;

	private static boolean isChar(char c) {
		return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')
				|| (c >= 'A' && c <= 'Z');
	}

	/**
	 * 中文转拼音（全拼）
	 * 
	 * @param cn
	 *            中文字符串
	 * @return
	 */
	public static String cn2SiglePy(String cn) {
		StringBuffer rlt = new StringBuffer();
		for (int i = 0; i < cn.length(); i++) {
			if (cn.charAt(i) > pystart + pinyin.length) {
				rlt.append("#");
				continue;
			}
			if (cn.charAt(i) < 128) {
				if (isChar(cn.charAt(i))) {
					rlt.append(cn.charAt(i));
					continue;
				} else {
					continue;
				}
			} else {
				if (cn.charAt(i) >= pystart)
					rlt.append(pinyin[cn.charAt(i) - pystart]);
				else {
					rlt.append("#");
					continue;
				}
			}
			if (i < cn.length() - 1)
				rlt.append(",");
		}
		return rlt.toString();

	}

	/**
	 * 中文转拼音头，如“中国”转为ZG
	 * 
	 * @param cn
	 *            中文
	 * @return
	 */
	public static String cn2SiglePyh(String cn) {
		StringBuffer rlt = new StringBuffer();
		for (int i = 0; i < cn.length(); i++) {
			if (cn.charAt(i) > pystart + pinyin.length)
				continue;
			if (cn.charAt(i) < pystart) {
				rlt.append(cn.charAt(i));
			} else
				rlt.append(pinyin[cn.charAt(i) - pystart].charAt(0));
		}
		return rlt.toString();

	}

	public String cn2py(String cn) {
		StringBuffer rlt = new StringBuffer();
		String[] py = new String[cn.length()];
		for (int i = 0; i < cn.length(); i++) {
			py[i] = pinyin[cn.charAt(i) - pystart];
		}
		doNext(rlt, py, 0);
		return rlt.toString();
	}

	private StringBuffer stack = new StringBuffer();

	private void push(char c) {
		stack.append(c);
	}

	private void pop() {
		// stack.deleteCharAt(stack.length()-1);
		stack.setLength(stack.length() - 1);
	}

	private void doNext(StringBuffer rlt, String[] py, int cur) {
		for (int i = 0; i < py[cur].length(); i++) {
			if (cur == 0)
				stack.setLength(0);
			push(py[cur].charAt(i));

			if (cur == py.length - 1) {
				rlt.append(stack.toString());
				rlt.append(",");
				// pop();
			} else {
				doNext(rlt, py, cur + 1);

			}
			pop();

		}
	}

	private static final String seed = "xzPLMOKNI01234asdfgtrewqJBUHVYGhjklmnbvc56789poiuyCTFXRDESZWQA";
	private static String[] minganci = new String[] { "10159", "118114",
			"12590", "12593", "13133685511", "1315897", "13158973797",
			"13725516608", "13875448369", "18box", "23079009", "5d6d",
			"9160011", "av", "baidugoogle", "bignews", "bitch", "boxun",
			"cdma", "chinaliberal", "chinamz", "chinesenewsnet", "cnd", "cpiu",
			"creaders", "dafa", "dajiyuan", "dfdz", "dick", "dpp", "falu",
			"falun", "falundafa", "flamesky", "flg", "freechina", "freedom",
			"freenet", "fuck", "gcd", "hongzhi", "hrichina", "huanet", "hxzi",
			"hypermart", "incest", "ip17908", "jb", "jiangdongriji", "jtyl",
			"keyword", "lei8", "lihongzhi", "making", "minghui", "minghuinews",
			"mmxdd", "mofile", "nacb", "naive", "net", "nmis", "paper64",
			"peacehall", "piao", "place", "playboy", "renminbao", "renmingbao",
			"rfa", "rjzj", "s3x6", "safeweb", "sex", "shit", "sim", "simple",
			"sm", "soccer01", "svdc", "taip", "tibetalk", "triangle",
			"triangleboy", "ultrasurf", "unixbox", "ustibet", "voa",
			"voachinese", "wangce", "webng", "woe", "wstaiji", "xinsheng",
			"yuming", "zhengjian", "zhengjianwang", "zhenshanren",
			"zhuanfalun", "zzzg", "10159", "118114", "12590", "12593",
			"13133685511", "1315897", "13158973797", "13725516608",
			"13875448369", "18box", "23079009", "5d6d", "9160011", "av",
			"baidugoogle", "bignews", "bitch", "boxun", "cdma", "chinaliberal",
			"chinamz", "chinesenewsnet", "cnd", "cpiu", "creaders", "dafa",
			"dajiyuan", "dfdz", "dick", "dpp", "falu", "falun", "falundafa",
			"flamesky", "flg", "freechina", "freedom", "freenet", "fuck",
			"gcd", "hongzhi", "hrichina", "huanet", "hxzi", "hypermart",
			"incest", "ip17908", "jb", "jiangdongriji", "jtyl", "keyword",
			"lei8", "lihongzhi", "making", "minghui", "minghuinews", "mmxdd",
			"mofile", "nacb", "naive", "net", "nmis", "paper64", "peacehall",
			"piao", "place", "playboy", "renminbao", "renmingbao", "rfa",
			"rjzj", "s3x6", "safeweb", "sex", "shit", "sim", "simple", "sm",
			"soccer01", "svdc", "taip", "tibetalk", "triangle", "triangleboy",
			"ultrasurf", "unixbox", "ustibet", "voa", "voachinese", "wangce",
			"webng", "woe", "wstaiji", "xinsheng", "yuming", "zhengjian",
			"zhengjianwang", "zhenshanren", "zhuanfalun", "zzzg", "fuck",
			"fuck", "keyword", "ip17908", "12593", "13725516608", "10159",
			"13875448369", "13133685511", "sim", "13158973797", "1315897",
			"chinaliberal", "dafa", "dajiyuan", "falun", "falundafa",
			"freedom", "gcd", "gcd", "huanet", "making", "paper64",
			"peacehall", "renmingbao", "safeweb", "simple", "taip", "yuming",
			"zhengjian", "hongzhi", "lihongzhi", "freechina", "shit",
			"23079009", "piao", "sex", "boxun", "chinamz", "chinesenewsnet",
			"cnd", "creaders", "dfdz", "dpp", "falu", "nmis", "svdc",
			"tibetalk", "triangle", "triangleboy", "ultrasurf", "unixbox",
			"voa", "wstaiji", "cdma", "bitch", "bignews", "jiangdongriji",
			"minghui", "playboy", "renminbao", "voachinese", "sex", "flg",
			"freenet", "hrichina", "incest", "nacb", "naive", "rfa", "ustibet",
			"wangce", "xinsheng", "zhengjianwang", "zhenshanren", "zhuanfalun",
			"minghuinews", "118114", "12590", "9160011", "av", "cdma",
			"hypermart", "net", "soccer01" };

	/**
	 * 获取一个随机字串
	 * 
	 * @return 随机字串
	 * @throws Exception
	 */
	public static String getRandomString() throws Exception{
		return getRandomStrings(1).get(0);
	}

	public static List<String> getRandomStrings(int number) throws Exception {
		if (number <= 0)
			return null;
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair("number", String.valueOf(number)));
		int tryTimes = 0;
		while (true) {
			tryTimes++;
			try {
				GetShortKeyNewResult result = (GetShortKeyNewResult) jsonToObject(
						"http://u.card.cd/GetShortKeyNew.json", data,
						GetShortKeyNewResult.class);
				if (result != null && result.getRlt() == 0) {
					return result.getKeys();
				} else {
					System.out.println(result.getTxt());
					System.out.println("KEY服务器错误");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Thread.sleep(50);
			}
			if (tryTimes > 10) {
				throw new Exception("KEY服务器错误");
			}
		}
	}

	public static String[] findDirtyWords(String content) throws Exception{
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair("content", content));
		int tryTimes = 0;
		while (true) {
			tryTimes++;
			try {
				DirtyWordResult result = (DirtyWordResult) Utils.jsonToObject(
						"http://u.card.cd/FindDirtyWord.json", data,
						DirtyWordResult.class);
				if (result != null) {
					if(result.getRlt()==1)
						return null;
					else if(result.getRlt()==0){
						return result.getDirtyWords();
					}
				} else {
					System.out.println("敏感词服务器错误");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Thread.sleep(50);
				e.printStackTrace();
			}
			if (tryTimes > 10) {
				throw new Exception("敏感词服务器错误");
			}
		}

	}

	private static boolean isMinganci(String str) {
		for (int i = 0; i < minganci.length; i++) {
			if (str.toLowerCase().contains(minganci[i])) {
				if (minganci[i].length() > 4)
					System.out.println("敏感词:" + str + "," + minganci[i]);
				return true;
			}
		}
		return false;
	}

	public static String getRandomStr() {

		// long h1=(UUID.randomUUID().toString().hashCode());
		// long h2=(UUID.randomUUID().toString().hashCode());
		// long h=h1<<32;
		// long l=h2<<32>>>32;
		//
		// long id=h|l;
		// if(id<0) id+=Long.MAX_VALUE;
		// id=id&0xffffffffffffl;
		// StringBuffer rlt=new StringBuffer(11);
		// while(id>0)
		// {
		// rlt.append(seed.charAt(((int)(id%62))));
		// id=id/62;
		// }
		// return rlt.toString();
		try {
			return getRandomString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	static List<String> names = new ArrayList<String>();

	public static String getShortStr(long l) {
		StringBuffer rlt = new StringBuffer();
		while (l > 0) {
			rlt.append(seed.charAt(((int) (l % 62))));
			l = l / 62;
		}
		return rlt.toString();

	}

	private static String getShortStr(String str) {
		long l = str.hashCode();
		l &= 0xffffffffl;
		long len = str.length();
		l += (len << 32);
		StringBuffer rlt = new StringBuffer();
		while (l > 0) {
			rlt.append(seed.charAt(((int) (l % 62))));
			l = l / 62;
		}
		return rlt.toString();
	}

	private static String getRandomStr(int maxLen) {
		Random random = new Random();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < maxLen; i++) {
			sb.append(seed.charAt(random.nextInt(62)));
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {

		// BufferedReader reader=new BufferedReader(new
		// FileReader("d:\\minganci.txt"));
		// String line=null;
		//
		// while((line=reader.readLine())!=null)
		// {
		// if(!hasChi(line))
		// {
		// System.out.print("\"");
		// System.out.print(line.toLowerCase());
		// System.out.print("\",");
		// }
		// }

		//
		// System.out.println();
		// System.out.println(md5("13255555555测试OBkDcpZ48z7"));
		// System.out.println(java.net.URLEncoder.encode("测试", "utf-8"));
		// Utils.jsonToObject1("http://localhost/testweb/TestAjaxForm.json",
		// "name=邓志国&name1=邓", String.class);
		// System.out.println(getRandomStr());
		// System.out.println(URLDecoder.decode("data=%7B%22table%22%3A%22TTrade%22%2C%22id%22%3A%22112154408933752%22%2C%22columns%22%3A%5B%7B%22name%22%3A%22Send%22%2C%22value%22%3Atrue%7D%2C%7B%22name%22%3A%22DeliverAddr%22%2C%22value%22%3A%22%E6%B5%99%E6%B1%9F%E7%9C%81%E6%B9%96%E5%B7%9E%E5%B8%82%E5%87%A4%E5%87%B0%E8%B7%AF777%E5%8F%B7+++%E4%BA%BA%E5%8A%9B%E8%B5%84%E6%BA%90%E9%83%A8%22%7D%2C%7B%22name%22%3A%22SellerName%22%2C%22value%22%3A%22yuccagz%22%7D%2C%7B%22name%22%3A%22DeliverDate%22%2C%22value%22%3A1320051830000%7D%2C%7B%22name%22%3A%22GoodsName%22%2C%22value%22%3A%22%E8%8B%B1%E5%8F%91%2FYINGFA+%E6%97%A0%E7%9A%B1%E5%86%85%E9%A2%97%E7%B2%92%E5%A4%A7%E5%9B%BD%E6%97%97%E6%B8%B8%E6%B3%B3%E5%B8%BD+%E5%A4%96%E5%9B%BD%E5%9B%BD%E6%97%97+%E5%A4%9A%E7%A7%8D%E5%85%A5%7E%E9%80%8F%E6%B0%94%E9%98%B2%E6%BB%91%22%7D%2C%7B%22name%22%3A%22ComName%22%2C%22value%22%3A%22%E5%9C%86%E9%80%9A%E9%80%9F%E9%80%92%22%7D%2C%7B%22name%22%3A%22Virtual%22%2C%22value%22%3Afalse%7D%2C%7B%22name%22%3A%22TaobaoId%22%2C%22value%22%3A%2220065865%22%7D%2C%7B%22name%22%3A%22TradeOver%22%2C%22value%22%3A%220%22%7D%2C%7B%22name%22%3A%22Mobile%22%2C%22value%22%3A%2213511220756%22%7D%2C%7B%22name%22%3A%22Id%22%2C%22value%22%3A%22112154408933752%22%7D%2C%7B%22name%22%3A%22Nikename%22%2C%22value%22%3A%22sam5555%22%7D%2C%7B%22name%22%3A%22ComSerial%22%2C%22value%22%3A%227005435979%22%7D%5D%7D&data=%7B%22table%22%3A%22TCardSend%22%2C%22id%22%3A%22dmc28j9%22%2C%22columns%22%3A%5B%7B%22name%22%3A%22TradeId%22%2C%22value%22%3A%22112154408933752%22%7D%2C%7B%22name%22%3A%22TaobaoId%22%2C%22value%22%3A%2220065865%22%7D%2C%7B%22name%22%3A%22Mobile%22%2C%22value%22%3A%2213511220756%22%7D%2C%7B%22name%22%3A%22Id%22%2C%22value%22%3A%22dmc28j9%22%7D%2C%7B%22name%22%3A%22ShareType%22%2C%22value%22%3A%220%22%7D%2C%7B%22name%22%3A%22ShareDate%22%2C%22value%22%3A1320128410935%7D%5D%7D&data=%7B%22table%22%3A%22TSms%22%2C%22id%22%3A%22QO4CkD4%22%2C%22columns%22%3A%5B%7B%22name%22%3A%22SmsStatus%22%2C%22value%22%3A200%7D%2C%7B%22name%22%3A%22TaobaoId%22%2C%22value%22%3A%2220065865%22%7D%2C%7B%22name%22%3A%22SmsContent%22%2C%22value%22%3A%22%E6%82%A8%E5%9C%A8%E3%80%90%E7%9C%9F%E8%89%B2%E5%BD%A9%E4%BD%93%E8%82%B2%E7%94%A8%E5%93%81%E3%80%91%E8%B4%AD%E4%B9%B0%E7%9A%84%E5%95%86%E5%93%81%E5%B7%B2%E7%BB%8F%E5%8F%91%E8%B4%A7%EF%BC%8C%E7%82%B9%E9%93%BE%E6%8E%A5%E5%8F%AF%E6%9F%A5%E7%9C%8B%E7%89%A9%E6%B5%81%E5%B9%B6%E4%BF%9D%E5%AD%98%E6%9C%AC%E5%BA%97%E5%90%8D%E7%89%87+http%3A%2F%2Ftb.card.cd%2F1dmc28j9%22%7D%2C%7B%22name%22%3A%22SmsPrice%22%2C%22value%22%3A700%7D%2C%7B%22name%22%3A%22Mobile%22%2C%22value%22%3A%2213511220756%22%7D%2C%7B%22name%22%3A%22Id%22%2C%22value%22%3A%22QO4CkD4%22%7D%5D%7D",
		// "utf-8"));
		System.out.println(Utils.getRandomString());

	}

	private static boolean hasChi(String str) {

		for (int i = 0; i < str.length(); i++) {
			if (seed.indexOf(str.charAt(i)) < 0)
				return true;
		}
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) > 128)
				return true;
		}
		return false;
	}

	/**
	 * 对一个字符串进行MD5算法，先UTF-8，然后MD5
	 * 
	 * @param src
	 *            要进行MD5的字符串
	 * @return MD5结果，Base16
	 */
	public static String md5(String src) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] rltBytes = md5.digest(src.getBytes("utf-8"));
			StringBuffer rlt = new StringBuffer();
			for (int i = 0; i < rltBytes.length; i++) {
				int v = (int) ((rltBytes[i]) & 0x000000ff);
				if (v <= 0xf) {
					rlt.append("0");
				}
				rlt.append(Integer.toHexString(v));
			}
			return rlt.toString();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 对一个Byte数组进行MD5算法
	 * 
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static byte[] md5(byte[] src) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		return md5.digest(src);

	}

	/**
	 * 访问一个Json WebService，并映射成对象。
	 * 
	 * @param url
	 *            WebService的URL
	 * @param data
	 *            WebService需要提交的数据对
	 * @param clz
	 *            结果对应的类
	 * @return 结果类，如果异常，返回空
	 */
	public static Object jsonToObject(String url, List<NameValuePair> data,
			Class<?> clz) {
		TransferInfo info = TransferUtil.postMethod(url, null, data, null);
		if (info.getStatusCode() >= 200 && info.getStatusCode() < 300)
			try {
				String rlt = new String(info.getContext(), "utf-8");
				return gson.fromJson(rlt, clz);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		return null;
	}

	public static Object jsonToObjectGet(String url,
			Class<?> clz) {
		TransferInfo info = TransferUtil.getMethod(url, null, null);
		if (info.getStatusCode() >= 200 && info.getStatusCode() < 300)
			try {
				String rlt = new String(info.getContext(), "utf-8");
				return gson.fromJson(rlt, clz);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		return null;
	}
	public static Object jsonToObject1(String url, String data, Class<?> clz) {
		try {
			TransferInfo info = TransferUtil.postMethod(url, null, data, null);
			if (info.getStatusCode() >= 200 && info.getStatusCode() < 300) {
				String rlt = new String(info.getContext(), "utf-8");
				return gson.fromJson(rlt, clz);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 访问一个WebService对象，其中有提交的文件，返回结果
	 * 
	 * @param url
	 *            WebService地址
	 * @param data
	 *            提交的参数
	 * @param files
	 *            上除的文件
	 * @param clz
	 *            结果类
	 * @return 结果对象
	 */
	public static Object jsonToObject(String url, List<NameValuePair> data,
			List<UploadFile> files, Class<?> clz) {
		TransferInfo info = TransferUtil.fileUploadMethod(url, null, data,
				null, files);
		if (info.getStatusCode() >= 200 && info.getStatusCode() < 300)
			try {
				return gson.fromJson(new String(info.getContext(), "utf-8"),
						clz);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		return null;
	}

	/**
	 * 返回一个UUID字符串，可以用来做随机ID
	 * 
	 * @return UUID
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	/**
	 * 后台服务喂狗
	 * @param id
	 * @return
	 */
	public static boolean FeedWatchdog(String id){
		int i=0;
		List<NameValuePair> data = new ArrayList<NameValuePair>();
		data.add(new BasicNameValuePair("id", id));
		while(i<5){
			try {
				FeedResult result=(FeedResult)jsonToObject("http://card.cd/watchdog/FeedDog.json", data, FeedResult.class);
				if(result==null) return false;
				if(result.getCode()==0)
					return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			i++;
			if(i>3)
				return false;
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
}
