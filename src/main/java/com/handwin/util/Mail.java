package com.handwin.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * @author zyihua
 * 
 */
public class Mail {

	private MimeMessage mimeMsg; // MIME邮件对象

	private Session session; // 邮件会话对象

	private Properties props; // 系统属性

	private boolean isAuth = false; // smtp是否需要认证

	private String userName; // smtp认证用户名和密码

	private String password;

	private Multipart multipart;

	private String host;

	/**
	 * @param host
	 *            STMP服务器地址
	 * @param isAuth
	 *            服务器是否需要身份验证
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 */
	public Mail(String host, boolean isAuth, String userName, String password) {
		this.userName = userName;
		this.password = password;
		setMailProperties(host, isAuth);
	}

	/**
	 * @param sendTo
	 *            收件人必须符合RFC822规范，多地址用逗号(,)分隔
	 * @param head
	 *            邮件标题
	 * @param carbon
	 *            抄送
	 * @param blind
	 *            密送
	 * @param body
	 *            邮件内容
	 * @param host
	 *            STMP服务器地址
	 * @param from
	 *            发送人
	 * @param isAuth
	 *            服务器是否需要身份验证
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 */
	public Mail(String sendTo, String head, String body, String carbon,
			String blind, String host, String from, boolean isAuth,
			String userName, String password) {
		this.userName = userName;
		this.password = password;
		setMailProperties(host, isAuth);
		createMail(head,from, body,  sendTo);

	}

	/**
	 * 设置邮件服务器
	 * 
	 * @param host
	 *            STMP服务器地址
	 * @param isAuth
	 *            服务器是否需要身份验证
	 */
	public void setMailProperties(String host, boolean isAuth) {
		this.isAuth = isAuth;
		this.host = host;
		if (props == null)
			props = System.getProperties();
		props.put("mail.smtp.host", host);
		multipart = new MimeMultipart();
		if (isAuth) {
			props.put("mail.smtp.auth", "true");
			// 获得邮件会话对象
			session = Session.getInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					// TODO Auto-generated method stub
					return new PasswordAuthentication(userName, password);
				}
			});
		} else {
			props.put("mail.smtp.auth", "false");
			session = Session.getInstance(props, null);
		}
		mimeMsg = new MimeMessage(session); // 创建MIME邮件对象

	}

	/**
	 * 建立邮件
	 * 
	 * @param head
	 *            邮件标题
	 * @param body
	 *            邮件内容
	 * @param from
	 *            发件人
	 * @param sendTo
	 *            收件人必须符合RFC822规范，多地址用逗号(,)分隔
	 */
	public void createMail(String head, String from, String body, String sendTo) {
		try {
			if (props == null)
				setMailProperties(host, isAuth);

			mimeMsg.setSubject(head);
			mimeMsg.setFrom(new InternetAddress(from));
			mimeMsg.addRecipients(MimeMessage.RecipientType.TO,
					InternetAddress.parse(sendTo));
			BodyPart bodyPart = new MimeBodyPart();
			bodyPart.setContent(
					"<meta http-equiv=Content-Type content=text/html; charset=gb2312>"
							+ body, "text/html;charset=GB2312");
			multipart.addBodyPart(bodyPart);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 建立邮件
	 * 
	 * @param head
	 *            邮件标题
	 * @param body
	 *            邮件内容
	 * @param from
	 *            发件人
	 * @param fromNickName
	 *            发件人名称
	 * @param sendTo
	 *            收件人必须符合RFC822规范，多地址用逗号(,)分隔
	 */
	public void createMail(String head, String from, String fromNickName,
			String body, String sendTo) {
		try {
			if (props == null)
				setMailProperties(host, isAuth);

			mimeMsg.setSubject(head);
			mimeMsg.setFrom(new InternetAddress(from, fromNickName));
			mimeMsg.addRecipients(MimeMessage.RecipientType.TO,
					InternetAddress.parse(sendTo));
			BodyPart bodyPart = new MimeBodyPart();
			bodyPart.setContent(
					"<meta http-equiv=Content-Type content=text/html; charset=gb2312>"
							+ body, "text/html;charset=GB2312");
			multipart.addBodyPart(bodyPart);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加附件
	 * 
	 * @param files
	 *            需要添加的文件
	 * @throws MessagingException
	 * @throws java.io.UnsupportedEncodingException
	 */
	public void addFileAffix(File[] files) throws UnsupportedEncodingException,
			MessagingException {
		for (int i = 0; i < files.length; i++) {
			BodyPart affixPart = new MimeBodyPart();
			FileDataSource filed = new FileDataSource(files[i]);
			affixPart.setDataHandler(new DataHandler(filed));
			affixPart.setFileName(MimeUtility.encodeText(filed.getName()));
			multipart.addBodyPart(affixPart);
		}
	}

	/**
	 * 添加附件
	 * 
	 * @param fileName
	 *            文件名
	 * @param inputs
	 *            文件流
	 * @throws java.io.IOException
	 * @throws MessagingException
	 */
	public void addFileAffix(String[] fileName, InputStream[] inputs)
			throws IOException, MessagingException {
		for (int i = 0; i < fileName.length; i++) {
			if (i == inputs.length)
				break;
			BodyPart affixPart = new MimeBodyPart();
			FileDataSource filed = new FileDataSource(fileName[i]);
			OutputStream out = filed.getOutputStream();
			byte[] temp = new byte[1024];
			int size = 0;
			while ((size = inputs[i].read(temp)) != -1) {
				if (size < 1024) {
					byte[] endByte = new byte[size];
					for (int j = 0; j < endByte.length; j++) {
						endByte[j] = temp[j];
					}
					out.write(endByte);
					// System.out.println(new String(endByte));
				} else {
					out.write(temp);
					// System.out.println(new String(temp));
				}
			}
			out.close();
			inputs[i].close();
			affixPart.setDataHandler(new DataHandler(filed));
			affixPart.setFileName(MimeUtility.encodeText(filed.getName()));
			multipart.addBodyPart(affixPart);
		}
	}

	/**
	 * 添加附件
	 * 
	 * @param filePath
	 *            文件所在路径
	 * @throws MessagingException
	 * @throws java.io.UnsupportedEncodingException
	 */
	public void addFileAffix(String[] filePath) throws MessagingException,
			UnsupportedEncodingException {
		for (int i = 0; i < filePath.length; i++) {
			BodyPart affixPart = new MimeBodyPart();
			FileDataSource filed = new FileDataSource(filePath[i]);
			affixPart.setDataHandler(new DataHandler(filed));
			affixPart.setFileName(MimeUtility.encodeText(filed.getName()));
			multipart.addBodyPart(affixPart);
		}
	}

	public String getHost() {
		return host;
	}

	public boolean isAuth() {
		return isAuth;
	}

	public String getUserName() {
		return userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUserNAME(String userName) {
		this.userName = userName;
	}

	/**
	 * 设置抄送
	 * 
	 * @param carbon
	 *            密送人地址必须符合RFC822规范，多地址用逗号(,)分隔 可以为空
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void setCarbon(String carbon) throws AddressException,
			MessagingException {
		if (carbon != null && !"".equals(carbon.trim()))
			mimeMsg.addRecipients(MimeMessage.RecipientType.CC,
					InternetAddress.parse(carbon));
	}

	/**
	 * 设置密送
	 * 
	 * @param blind
	 *            密送人地址必须符合RFC822规范，多地址用逗号(,)分隔 可以为空
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void setBlind(String blind) throws AddressException,
			MessagingException {
		if (blind != null && !"".equals(blind.trim()))
			mimeMsg.addRecipients(MimeMessage.RecipientType.BCC,
					InternetAddress.parse(blind));
	}

	/**
	 * 发送邮件
	 * 
	 * @throws MessagingException
	 */
	public void sendMail() throws MessagingException {
		mimeMsg.setContent(multipart);
		mimeMsg.saveChanges();
		Transport.send(mimeMsg);
	}
}