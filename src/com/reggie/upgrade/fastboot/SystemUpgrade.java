package com.reggie.upgrade.fastboot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

/**
 * TODO<Android fastboot刷机方法>
 * 
 * @author: Reggie
 * @data: 2016年5月30日 下午5:52:29
 * @version: V1.0
 */

public class SystemUpgrade {

	private static String localpath;

	public void fastbootflash() {
		init();
		System.out.println(localpath);

		System.out.println("正在刷机中...");
		cmd("adb reboot bootloader");
		time_sleep(15);

		ArrayList<String> list = new ArrayList<String>();
		list.add("system");
		list.add("recovery");
		list.add("boot");
		list.add("userdata");
		list.add("cache");

		for (int i = 0; i < list.size(); i++) {
			System.out.println(i + 1 + "   " + list.get(i));
			String command = "fastboot flash " + list.get(i) + " " + localpath + "/" + list.get(i) + ".img";
			System.out.println(command);
			cmd(command);
			time_sleep(6);
		}
		cmd("fastboot reboot");
		System.out.println("升级完毕.");
	}

	public void cmd(String command) {
		StringBuffer sb = new StringBuffer();
		String line = null;
		try {
			Process proc = Runtime.getRuntime().exec(command);
			proc.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void time_sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void init() {
		FileInputStream fis;
		try {
			fis = new FileInputStream(new File("src/localPathDB.properties"));
			Properties props = new Properties();
			props.load(fis);
			localpath = props.getProperty("localPath");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
