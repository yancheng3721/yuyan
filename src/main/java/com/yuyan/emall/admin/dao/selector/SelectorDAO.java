package com.yuyan.emall.admin.dao.selector;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Component;

import com.yuyan.emall.admin.base.PageCond;
import com.yuyan.emall.admin.base.PageUtil;

@Component
public class SelectorDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	private static Map<String, String> typeMap = new HashMap<String, String>();

	static {
		typeMap.put(String.valueOf(java.sql.Types.VARCHAR), "STRING");
		typeMap.put(String.valueOf(java.sql.Types.LONGVARCHAR), "STRING");
		typeMap.put(String.valueOf(java.sql.Types.CHAR), "STRING");
		typeMap.put(String.valueOf(java.sql.Types.TIMESTAMP), "STRING");
		typeMap.put(String.valueOf(java.sql.Types.INTEGER), "NUMBER");
		typeMap.put(String.valueOf(java.sql.Types.DOUBLE), "NUMBER");
        typeMap.put(String.valueOf(java.sql.Types.DECIMAL), "NUMBER");
        typeMap.put(String.valueOf(java.sql.Types.BIGINT), "NUMBER");
    }

	public List<Map<String, Object>> query(String sql,
			Map<String, String> param, PageCond page) {
		return querySql(sql, param, page);
	}

	public List<Map<String, Object>> querySql(String sql,
			Map<String, String> param, PageCond page) {
		Map<String, String> map = new HashMap<String, String>();
		SqlRowSetMetaData srsmd = jdbcTemplate.queryForRowSet(
				sql + " FETCH FIRST 1 ROWS ONLY WITH UR").getMetaData();
		int columnCount = srsmd.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String name = srsmd.getColumnName(i);// 字段
			String fieldType = String.valueOf(srsmd.getColumnType(i));// 字段类型
			map.put(name, fieldType);
		}
		List<Object> values = new ArrayList<Object>();
		StringBuffer queryParams = new StringBuffer();
		if (param != null) {
			for (Map.Entry<String, String> e : param.entrySet()) {
				String key = e.getKey();
				String v = e.getValue();
				if ("".equals(v)) {
					continue;
				}
				String op = "";
				if ("STRING".equals(typeMap.get(map.get(key)))) {
					op = " LIKE ";
				} else {
					op = " = ";
				}
				queryParams.append(" AND ").append(key).append(op)
						.append(" ? ");
				if (" LIKE ".equals(op)) {
					v = "%" + v + "%";
				}
				values.add(v);
			}
		}
		return PageUtil.doPageWithParam(jdbcTemplate, sql, page,
				queryParams.toString(), values.toArray());
	}

}
