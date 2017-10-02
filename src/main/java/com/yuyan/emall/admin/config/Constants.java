package com.yuyan.emall.admin.config;

import java.util.Properties;

import com.yuyan.emall.admin.util.PropertiesUtil;

public class Constants {
	public static Properties p = new Properties();;
	public static final String CONFIG_FILE = "/opt/concentrate/admin/configs/concentrate_configs.properties";
	public static final String CONFIG_PATH = "/opt/concentrate/admin/configs/";
	public static final String MODULE_FILE = "modules.txt";

	public static final String DATA_PATH = "/opt/concentrate/admin/txt/";

	static {
		initial();
	}

	public static void initial() {
		try {
			p = PropertiesUtil.loadProperties("file:" + CONFIG_FILE);
		} catch (Exception e) {
		}
		try {
			ConfigUpdater.overrideDefaultByConfigFile(p,
					Constants.class.newInstance());
		} catch (Exception e) {
		}
	}

}
