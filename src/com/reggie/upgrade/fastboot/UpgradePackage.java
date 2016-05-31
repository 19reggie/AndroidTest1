package com.reggie.upgrade.fastboot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 * TODO<升级包下载>
 * 
 * @author: Reggie
 * @data: 2016年5月31日 上午10:27:03
 * @version: V1.0
 */
public class UpgradePackage {

	private static String url;// FTP服务器hostname
	private static String port;// FTP服务器端口号
	private static String username;// FTP登录账户
	private static String password;// FTP登录密码
	private static String parentRemotePath;// FTP服务器上版本的父目录

	public void downloadfile() {
		// 1.初始化将属性文件中的数据读入到本类的变量中
		init();
		FTPClient ftpC = new FTPClient();
		try {
			ftpC.connect(url);// 默认端口号
			// 2.登录FTP服务器
			ftpC.login(username, password);
			// 3.切换到FTP工作目录
			ftpC.changeWorkingDirectory(parentRemotePath);
			ArrayList<String> filelist = new ArrayList<String>();
			FTPFile[] fs = ftpC.listFiles();
			for (FTPFile ff : fs) {
				String[] mff = ff.toString().split(" ");
				String filename = mff[mff.length - 1].trim();
				if (filename.matches("imgs.*")) {// 正则匹配imgs.*
					filelist.add(filename);
					// System.out.println(filename);
				}
			}
			// 5.过滤出服务器的最新的升级包
			String latest_filelist = filelist.get(filelist.size() - 1);
			System.out.println("FTP服务器上当前最新版本为：" + latest_filelist);
			String[] latestVersion_arr = latest_filelist.split("-");
			String latestVersion_time = "20" + latestVersion_arr[1] + latestVersion_arr[2] + latestVersion_arr[3];
			String current_time = currentTime();

			// 6.判断服务器上的最新的升级包的日期与当前的日期是否相等
			// latestVersion_time与current_time相等
			if (latestVersion_time.equals(current_time)) {
				System.out.println("下载版本号为：" + latest_filelist);
				String childRemotePath = parentRemotePath + latest_filelist;
				String localPath = "d:/test_talpa_rom/" + latest_filelist;
				createFile("localPath=" + localPath);
				File f = new File(localPath);
				if (!f.exists()) {
					f.mkdirs();
				}
                //7.下载升级包
				System.out.println("开始下载升级包...");
				ftpC.changeWorkingDirectory(childRemotePath);
				ArrayList<String> filelist2 = new ArrayList<String>();
				FTPFile[] fs2 = ftpC.listFiles();
				for (FTPFile ff2 : fs2) {
					String[] mff2 = ff2.toString().split(" ");
					String filename2 = mff2[mff2.length - 1].trim();
					filelist2.add(filename2);
					File localfile2 = new File(localPath + "/" + filename2);
					System.out.println(localfile2);
					OutputStream is = new FileOutputStream(localfile2);
					ftpC.retrieveFile(filename2, is);
					is.close();
				}
				System.out.println("升级包下载完毕.");

			} else {// latestVersion_time与current_time不相等
				System.out.println("没有今天的版本");
				// 发送邮件通知

			}

			ftpC.logout();

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftpC.isConnected()) {
				try {
					ftpC.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void init() {
		FileInputStream fis;
		try {
			fis = new FileInputStream(new File("src/FtpDB.properties"));
			Properties props = new Properties();
			props.load(fis);
			url = props.getProperty("url");
			port = props.getProperty("port");
			username = props.getProperty("username");
			password = props.getProperty("password");
			parentRemotePath = props.getProperty("parentRemotePath");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String currentTime() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(d);
	}

	public static void createFile(String content) {
		String filename = "src/localPathDB.properties";
		File f = new File(filename);

		try {
			FileOutputStream is = new FileOutputStream(f);
			if (!f.exists()) {
				f.createNewFile();
			}
			byte[] contentBytes = content.getBytes();
			is.write(contentBytes);
			is.flush();
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
