package com.yuyan.emall.admin.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 * 读取文件
 */
public class ReadFileUtil {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReadFileUtil.class);

	static SimpleDateFormat sim = new SimpleDateFormat("yyyyMMddHHmmss");

	// 读取本地文件
	public static <T> List<T> readFile(String fileName, FileRowMapper<T> frm) {
		List<T> list = new ArrayList<T>();
		File file = new File("/opt/search/admin/txt");
		String path = file.getAbsolutePath();
		try {
			File f = new File(path + "/" + fileName);
			if (f.isFile() && f.getName().equals(fileName) && f.exists()) {
				BufferedReader reader = null;
				InputStreamReader isr = null;
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(path + "/" + fileName);
					isr = new InputStreamReader(fis, "UTF-8");
					reader = new BufferedReader(isr);
					String line;
					LOGGER.info("读取文件" + fileName + "开始");
					while ((line = reader.readLine()) != null) {
						T t = frm.mapRow(line);
						if (t != null) {
							list.add(t);
						}
					}
					LOGGER.info("读取文件" + fileName + "结束");
					reader.close();
					isr.close();
					fis.close();
				} catch (Exception e) {
					LOGGER.error("读取文件异常", e);
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
						}
					}
					if (isr != null) {
						try {
							isr.close();
						} catch (IOException e) {
						}
					}
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
						}
					}
				}
			}
		} catch (NullPointerException e) {
			LOGGER.error("路径" + path + "没有文件", e);
		}
		return list;
	}

	// 读取partnumber对应的三级目录id,一个商品可以挂多个目录
	public static Map<String, List<String>> readGoodsDirectoryFile(
			Map<String, String> directoryMap) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		File file = new File("/opt/search/admin/goodsGroup/");
		String path = file.getAbsolutePath();
		SimpleDateFormat sim = new SimpleDateFormat("yyyyMMdd");
		String prefix = sim.format(Calendar.getInstance().getTime());// 文件以日期为前缀
		try {
			File[] files = new File(path).listFiles();
			for (File f : files) {
				if (f.isFile() && f.getName().contains(prefix) && f.exists()
						&& f.getName().contains("file")) {// 包含当前日期和“file”的文件
					if (f.getName().endsWith(".txt")) {
						BufferedReader reader = null;
						InputStreamReader isr = null;
						FileInputStream fis = null;
						try {
							fis = new FileInputStream(path + "/" + f.getName());
							isr = new InputStreamReader(fis, "UTF-8");
							reader = new BufferedReader(isr);
							String line;
							LOGGER.info("读取文件" + f.getName() + "开始");
							while ((line = reader.readLine()) != null) {
								String[] ss = line.split(",");
								String partnumber = ss[0].trim();
								if (ss.length >= 2) {
									for (int i = 1; i < ss.length; i++) {
										String directoryId = ss[i].substring(
												ss[i].lastIndexOf(":") + 1)
												.trim();
										String directoryName = directoryMap
												.get(directoryId);
										if (map.containsKey(partnumber)) {
											map.get(partnumber).add(
													directoryName);
										} else {
											List<String> l = new ArrayList<String>(
													1);// List默认大小设为1
											l.add(directoryName);
											map.put(partnumber, l);
										}
									}
								}
							}
							LOGGER.info("读取文件" + f.getName() + "结束");
							reader.close();
							isr.close();
							fis.close();
						} catch (Exception e) {
							LOGGER.error("读取文件异常", e);
						} finally {
							if (reader != null) {
								try {
									reader.close();
								} catch (IOException e) {
								}
							}
							if (isr != null) {
								try {
									isr.close();
								} catch (IOException e) {
								}
							}
							if (fis != null) {
								try {
									fis.close();
								} catch (IOException e) {
								}
							}
							f.renameTo(new File(path
									+ "/"
									+ f.getName()
									+ "."
									+ sim.format(Calendar.getInstance()
											.getTime())));// 读完后重新命名
						}
					}
				} else {
					LOGGER.info("删除文件" + f.getName());
					f.delete();// 不是当天的文件就删除
				}
			}
		} catch (NullPointerException e) {
			LOGGER.error("路径" + path + "没有文件", e);
		}
		return map;
	}

	public static void main(String[] args) {
		File file = new File("/opt/search/admin/txt/reservation.obj");
		String path = file.getAbsolutePath();
		System.out.println(path);
		/*
		 * File f = new File(path + "/directory.txt"); f.renameTo(new File(path
		 * + "/" + f.getName() + ".history")); System.out.println(f.exists() +
		 * ":" + f.getName());
		 */
	}

	// 读取节能补贴文件
	public static Map<String, List<String>> readEnergyFile(String fileName) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		File file = new File("/opt/search/admin/txt");
		String path = file.getAbsolutePath();
		try {
			File f = new File(path + "/" + fileName);
			if (f.isFile() && f.getName().equals(fileName) && f.exists()) {
				BufferedReader reader = null;
				InputStreamReader isr = null;
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(path + "/" + fileName);
					isr = new InputStreamReader(fis, "UTF-8");
					reader = new BufferedReader(isr);
					String line;
					LOGGER.info("读取文件" + fileName + "开始");
					while ((line = reader.readLine()) != null) {
						String[] ss = line.split(",");
						if (ss.length >= 2) {
							String[] cities = ss[1].split("\\*-\\*");
							List<String> cityList = new ArrayList<String>();
							Collections.addAll(cityList, cities);
							map.put(ss[0].trim(), cityList);
						}
					}
					LOGGER.info("读取文件" + fileName + "结束");
					reader.close();
					isr.close();
					fis.close();
				} catch (Exception e) {
					LOGGER.error("读取文件异常", e);
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
						}
					}
					if (isr != null) {
						try {
							isr.close();
						} catch (IOException e) {
						}
					}
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
						}
					}
				}
			}
		} catch (NullPointerException e) {
			LOGGER.error("路径" + path + "没有文件", e);
		}
		return map;
	}

}
