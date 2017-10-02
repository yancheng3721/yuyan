package com.yuyan.emall.admin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializeUtil {
    private static final Logger log = LoggerFactory.getLogger(SerializeUtil.class);

	// 序列化对象到文件
	public static void serialize(String fileName, Object o) {
		try {
			// 创建一个对象输出流，将对象输出到文件
			boolean flag = true;
			File f = new File(fileName);
			f = f.getAbsoluteFile();
			if (f.exists()) {
				flag = f.delete();
				log.info("删除旧文件" + fileName + ":" + flag);
			}
			if (flag) {
				ObjectOutputStream out = new ObjectOutputStream(
						new FileOutputStream(fileName));
				out.writeObject("序列化日期是："); // 序列化一个字符串到文件
				out.writeObject(new Date()); // 序列化一个当前日期对象到文件
				out.writeObject(o); // 序列化对象
				out.close();
			} else {
				log.error("删除旧文件" + fileName + "失败，新文件无法生成");
			}
		} catch (Exception x) {
			log.info((x.toString()));
		}
	}

	// 从文件反序列化到对象
	@SuppressWarnings("unused")
	public static Object deserialize(String fileName) {
		Object result = null;
		try {
			// 创建一个对象输入流，从文件读取对象
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					fileName));
			// 注意读对象时必须按照序列化对象顺序读，否则会出错
			// 读取字符串
			String today = (String) (in.readObject());
			// 读取日期对象
			Date date = (Date) (in.readObject());
			// 读取对象并调用它的toString()方法
			result = in.readObject();
			in.close();
		} catch (Exception x) {
			log.error(x.toString(), "读取文件" + fileName + "失败");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		/*
		 * Map<String, Object> mm = new HashMap<String, Object>(); Map<String,
		 * Map<String, Object>> m2 = new HashMap<String, Map<String, Object>>();
		 * Map<String, Map<String, Object>> m3 = new HashMap<String, Map<String,
		 * Object>>(); Map<String, Map<String, Object>> m4 = new HashMap<String,
		 * Map<String, Object>>(); mm.put("brand", "12345"); m2.put("child",
		 * mm); m3.put("child", mm); m4.put("child", mm); Collection<Map<String,
		 * Map<String, Object>>> ms = new ArrayList<Map<String, Map<String,
		 * Object>>>(); ms.add(m2); ms.add(m3); ms.add(m4);
		 * serialize("/opt/search/admin/txt/brand.obj", ms);
		 * 
		 * Collection<Map<String, Map<String, Object>>> deser =
		 * (Collection<Map<String, Map<String, Object>>>)
		 * deserialize("/opt/search/admin/txt/brand.obj");
		 * System.out.println(deser); Iterator<Map<String, Map<String, Object>>>
		 * it = deser.iterator();
		 * 
		 * Map<String, Map<String, Object>> m5 = it.next(); Map<String,
		 * Map<String, Object>> m6 = it.next(); Map<String, Map<String, Object>>
		 * m7 = it.next(); System.out.println(m5 == m6);
		 * System.out.println(m5.get("child") == m6.get("child"));
		 */
		Map<String, Object> d = new HashMap<String, Object>();
		int[] a = { 1, 0, 1 };
		d.put("d", a);
		serialize("/opt/search/admin/txt/a.obj", d);
		Map<String, Object> s = (Map<String, Object>) deserialize("/opt/search/admin/txt/a.obj");
		System.out.println((int[]) s.get("d"));
	}
}
