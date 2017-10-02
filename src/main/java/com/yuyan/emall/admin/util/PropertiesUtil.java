package com.yuyan.emall.admin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class PropertiesUtil {
	private static final Logger log = Logger.getLogger(PropertiesUtil.class);
	public static final String CLASSPATH_LOAD_WAY = "classpath";
	public static final String FILE_LOAD_WAY = "file";
	private static String path = "/opt/search/admin/configs/path.properties";

	public PropertiesUtil() {
		super();
	}

	public static Properties read() {
		Properties p = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(path);
			p.load(inputStream);
			inputStream.close();
		} catch (Exception e1) {
			log.warn("获取文件失败", e1);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
					inputStream = null;
				} catch (IOException e) {
					log.info(e.getMessage(), e);
				}

			}
		}
		return p;
	}

	public static Properties read(String properPath) {
		Properties p = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(properPath);
			p.load(inputStream);
			inputStream.close();
		} catch (IOException e1) {
			log.error("文件关闭失败", e1);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
					inputStream = null;
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}

			}
		}
		return p;
	}

	public static synchronized void store(Properties p, String path) {
		if (StringUtils.isNotEmpty(path)) {
			File f = new File(path);
			OutputStream os = null;
			try {
				os = new FileOutputStream(f);
				p.store(os, "");
			} catch (Exception e) {
				log.error("保存配置文件发生异常！", e);
			} finally {
				IOUtils.closeQuietly(os);
			}
		}
	}

	public static Properties loadProperties(final String path) {
		InputStream inputStream = getFileStream(path);
		if (inputStream == null) {
			return null;
		}
		Properties p = new Properties();
		try {
			p.load(inputStream);
		} catch (IOException e1) {
			log.error("加载配置文件异常", e1);
			return null;
		} finally {
			try {
				IOUtils.closeQuietly(inputStream);
			} catch (Exception ex) {
				log.error("加载配置文件异常", ex);
			}
		}
		return p;
	}

	private static InputStream getFileStream(String partten) {
		InputStream result = null;
		if (partten.indexOf(":") == -1) {
			PropertiesUtil.class.getClassLoader().getResourceAsStream(partten);
		}
		if (CLASSPATH_LOAD_WAY.equals(partten.split(":")[0])) {// classpath:
			result = PropertiesUtil.class.getClassLoader().getResourceAsStream(
					partten.split(":")[1]);
		} else if (FILE_LOAD_WAY.equals(partten.split(":")[0])) {// file:
			try {
				result = new FileInputStream(new File(partten.split(":")[1]));
			} catch (Exception e) {
				log.info("", e);
			}
		}
		return result;
	}
}