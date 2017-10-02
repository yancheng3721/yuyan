package com.yuyan.emall.admin.base;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

public abstract class AbstractedBaseDAO {
	private static final Logger logger = LoggerFactory
			.getLogger(AbstractedBaseDAO.class);

	public abstract String getTBName();

	public abstract String getQuerySql();

	public abstract JdbcTemplate getJdbcTemplate();

	public abstract String[] getUniqKeys();

	private Map<String, String> coloumnInfo = new HashMap<String, String>();
	private static Map<String, String> typeMap = new HashMap<String, String>();
	private static Map<String, String> orgTypeMap = new HashMap<String, String>();
	private Map<String, Map<String, String>> tableInfo = new HashMap<String, Map<String, String>>();
	static {

		typeMap.put(String.valueOf(java.sql.Types.VARCHAR), "STRING");
		typeMap.put(String.valueOf(java.sql.Types.LONGVARCHAR), "STRING");
		typeMap.put(String.valueOf(java.sql.Types.CHAR), "STRING");
		typeMap.put(String.valueOf(java.sql.Types.TIMESTAMP), "STRING");
		typeMap.put(String.valueOf(java.sql.Types.INTEGER), "NUMBER");
		typeMap.put(String.valueOf(java.sql.Types.DOUBLE), "NUMBER");
        typeMap.put(String.valueOf(java.sql.Types.DECIMAL), "NUMBER");
        typeMap.put(String.valueOf(Types.BIGINT), "NUMBER");

    }

	protected Map<String, String> getColumnInfoMap() {
		if (coloumnInfo == null || coloumnInfo.size() < 1) {
			initMap();
		}
		return coloumnInfo;
	}

	protected Map<String, String> getOrgTypeMap() {
		if (orgTypeMap == null || orgTypeMap.size() < 1) {
			initMap();
		}
		return orgTypeMap;
	}

	@PostConstruct
	private void initMap() {
		// List<Map<String,Object>> columns =
		// getJdbcTemplate().queryForList("select COLUMN_NAME,DATA_TYPE from sysibm.columns where UPPER(TABLE_NAME) = '"+getTBName()+"'");
		Thread t = new Thread() {
			public void run() {
				try {
					SqlRowSetMetaData srsmd = getJdbcTemplate().queryForRowSet(
							getQuerySql().replaceAll("#queryParams#", "")
									+ " limit 1")
							.getMetaData();
					int columnCount = srsmd.getColumnCount();
					for (int i = 1; i <= columnCount; i++) {
						String name = srsmd.getColumnName(i);
						String fieldType = String.valueOf(srsmd
								.getColumnType(i));
						coloumnInfo.put(name, typeMap.get(fieldType));
						orgTypeMap.put(name, fieldType);
					}
				} catch (Exception e) {
					logger.error("", e);
				}
				try {
					List<Map<String, Object>> columns = getJdbcTemplate()
							.queryForList(
									"select COLUMN_NAME,DATA_TYPE from information_schema.columns where UPPER(TABLE_NAME) = '"
											+ getTBName() + "'");
					if (columns != null && columns.size() > 0) {
						for (Map<String, Object> m : columns) {
							String name = (String) m.get("COLUMN_NAME");
							String type = (String) m.get("DATA_TYPE");
							Map<String, String> col = new HashMap<String, String>();
							col.put("NAME", name);
							col.put("TYPE", type);
							tableInfo.put(name, col);
						}
					}
				} catch (Exception e) {
					logger.error("", e);
				}

			}
		};
		t.setDaemon(true);
		t.start();

	}

	protected String getQueryColumnValue(String key, String value) {
		String type = getOrgTypeMap().get(key);
		if (type != null) {
			if ("CHARACTER VARYING".equals(type) || "CHARACTER".equals(type)) {
				return "'" + value + "%'";
			}
		}
		return getColumnValue(key, value);

	}

	protected String getColumnValue(String key, String value) {
		if ("STRING".equals(getColumnInfoMap().get(key))) {
			return "'" + value + "'";
		} else {
			return value;
		}
	}

	public int insert(Map<String, String> param) {
		int result = 0;
		if (param != null) {
			StringBuffer fields = new StringBuffer();
			StringBuffer values = new StringBuffer();
			List<Object> data = new ArrayList<Object>();
			// System.out.println("插入参数："+mapToString(param));
			int i = 0;
			for (Map.Entry<String, String> e : param.entrySet()) {
				String key = e.getKey();
				String v = e.getValue();
				if (Base.ID.equals(key) || tableInfo.get(key) == null) {
					continue;
				}
				if (i != 0) {
					fields.append(" , ");
					values.append(" , ");
				}
				fields.append(key);
				values.append(" ? ");
				if ("".equals(v)) {
					data.add(null);
				} else {
					data.add(v);
				}
				i++;
			}

			String sql = "INSERT INTO " + getTBName() + "(" + fields
					+ ") VALUES (" + values + ")";
			result = getJdbcTemplate().update(sql, data.toArray());
		}
		return result;
	}

	public List<Map<String, Object>> query(Map<String, String> param,
			PageCond page) {
		String sql = getQuerySql();
		return querySql(sql, param, page);
	}

	public List<Map<String, Object>> querySql(String sql,
			Map<String, String> param, PageCond page) {
		List<Object> values = new ArrayList<Object>();
		StringBuffer queryParams = new StringBuffer();
		if (param != null) {
			for (Map.Entry<String, String> e : param.entrySet()) {
				String key = e.getKey();
				String v = e.getValue();
				if ("".equals(v)) {
					continue;
				}
				// queryParams.append(" AND ").append(key).append(getOp(key)).append(getQueryColumnValue(key,v));
				String op = getOp(key);
				queryParams.append(" AND ").append(key).append(getOp(key))
						.append(" ? ");
				if (" LIKE ".equals(op)) {
					v = "%" + v + "%";
				}
				values.add(v);
			}
		}
		return PageUtil.doPageWithParam(getJdbcTemplate(), sql, page,
				queryParams.toString(), values.toArray());
	}

	public String getOp(String key) {
		key = key.substring(key.lastIndexOf(".") + 1);
		String result = "=";
		String type = getOrgTypeMap().get(key);
		if (type != null) {
			if (String.valueOf(java.sql.Types.VARCHAR).equals(type)
					|| String.valueOf(java.sql.Types.CHAR).equals(type)) {
				result = " LIKE ";
			}
			if ("START_TIME".equals(key)) {
				result = ">=";
			}
			if ("END_TIME".equals(key)) {
				result = "<=";
			}
		}
		return result;
	}

	public int update(Map<String, String> param) {
		if (param != null && param.get(Base.ID) != null) {
			StringBuffer setPairs = new StringBuffer();
			int i = 0;
			List<Object> values = new ArrayList<Object>();
			for (Map.Entry<String, String> e : param.entrySet()) {
				String key = e.getKey();
				String v = e.getValue();
				if (Base.ID.equals(key) || Base.CREATE_TIME.equals(key)) {
					continue;
				}
				if (tableInfo.get(key) != null) {
					if (i != 0) {
						setPairs.append(" , ");
					}
					// setPairs.append(key).append("=").append(getColumnValue(key,v));
					setPairs.append(key).append("=").append("?");
					values.add(v);
					i++;
				}
			}
			String sql = "update " + getTBName() + " set " + setPairs
					+ " where id= ? ";
			values.add(param.get(Base.ID));
			return getJdbcTemplate().update(sql, values.toArray());
		}
		return 0;
	}

	// 没有ID的更新，用于批量上传
	public int updateWithoutId(Map<String, String> param) {
		StringBuffer setPairs = new StringBuffer();
		StringBuffer wherePairs = new StringBuffer();
		Set<String> set = new HashSet<String>();
		CollectionUtils.addAll(set, getUniqKeys());
		List<Object> setValues = new ArrayList<Object>();
		List<Object> whereValues = new ArrayList<Object>();
		for (Map.Entry<String, String> e : param.entrySet()) {
			String key = e.getKey();
			String v = e.getValue();
			if (Base.CREATE_TIME.equals(key)) {
				continue;
			}
			if (tableInfo.get(key) != null) {// 表里有这个字段
				if (set.contains(key)) {// 唯一字段包含当前字段
					if (StringUtils.isEmpty(v)) {
						wherePairs.append(key).append(" is null and ");
					} else {
						wherePairs.append(key).append("=").append("? and ");
						whereValues.add(v);
					}
				} else {
					setPairs.append(key).append("=").append("?,");
					setValues.add(v);
				}
			}
		}
		String w = wherePairs.substring(0, wherePairs.lastIndexOf("and"));
		String s = setPairs.substring(0, setPairs.lastIndexOf(","));
		String sql = "update " + getTBName() + " set " + s + " where " + w;
		setValues.addAll(whereValues);
		return getJdbcTemplate().update(sql, setValues.toArray());
	}

	public int updateAllFields(Map<String, String> param) {
		if (param != null && param.get(Base.ID) != null) {
			StringBuffer setPairs = new StringBuffer();
			int i = 0;
			List<Object> values = new ArrayList<Object>();
			for (Map.Entry<String, Map<String, String>> e : tableInfo
					.entrySet()) {
				String key = e.getKey();
				if (Base.ID.equals(key) || Base.CREATE_TIME.equals(key)) {
					continue;
				}
				if (i != 0) {
					setPairs.append(" , ");
				}
				String v = param.get(key);
				// setPairs.append(key).append("=").append(getColumnValue(key,v));
				setPairs.append(key).append("=").append("?");
				values.add(v);
				i++;

			}
			String sql = "update " + getTBName() + " set " + setPairs
					+ " where id= ? ";
			values.add(param.get(Base.ID));
			return getJdbcTemplate().update(sql, values.toArray());
		}
		return 0;
	}

	public int delete(Map<String, String> param) {
		if (param != null && param.get(Base.ID) != null) {
			String sql = "DELETE FROM " + getTBName() + " WHERE ID= "
					+ param.get(Base.ID);
			return getJdbcTemplate().update(sql);
		}
		return 0;
	}

	// 检测存在时，主键都中有数值类型，不能为空，有空则无法判断唯一性，直接返回false
	public boolean exists(Map<String, String> param) {
		String sql = "SELECT 1 FROM " + getTBName()
				+ " WHERE 1=1 #queryParams#";
		StringBuffer queryParams = new StringBuffer();
		List<Object> values = new ArrayList<Object>();
		if (param != null) {
			String[] uniqKeys = getUniqKeys();
			if (uniqKeys != null && uniqKeys.length > 0) {

				for (String k : uniqKeys) {
					if (param.get(k) == null || "".equals(param.get(k))) {// 唯一键没值
																			// 不做存在判断
						return false;
					}
					queryParams.append(" AND ");
					// queryParams.append(k).append("=").append(getColumnValue(k,param.get(k)));
					queryParams.append(k).append("=?");
					values.add(param.get(k));
				}
				if (param.get(Base.ID) != null
						&& !"".equals(param.get(Base.ID))) {// 修改时的重复判断，不能与自己比
					// queryParams.append(" AND ").append(Base.ID).append(" != ").append(getColumnValue(Base.ID,param.get(Base.ID)));
					queryParams.append(" AND ").append(Base.ID).append(" != ?");
					values.add(param.get(Base.ID));
				}
			} else {
				return false;
			}
		}
		sql = sql.replaceAll("#queryParams#", queryParams.toString());

		return getJdbcTemplate().queryForList(sql, values.toArray()).size() > 0;
	}

	// 检测存在时，主键都为字符串类型，可以为空(数据库对应null)
	public boolean exist(Map<String, String> param) {
		String sql = "SELECT 1 FROM " + getTBName()
				+ " WHERE 1=1 #queryParams#";
		StringBuffer queryParams = new StringBuffer();
		List<Object> values = new ArrayList<Object>();
		if (param != null) {
			String[] uniqKeys = getUniqKeys();
			if (uniqKeys != null && uniqKeys.length > 0) {
				for (String k : uniqKeys) {
					if (param.get(k) == null || "".equals(param.get(k).trim())) {
						queryParams.append(" AND ");
						queryParams.append(k).append(" is null");
					} else {
						queryParams.append(" AND ");
						queryParams.append(k).append("=?");
						values.add(param.get(k));
					}
				}
				if (param.get(Base.ID) != null
						&& !"".equals(param.get(Base.ID))) {// 修改时的重复判断，不能与自己比
					// queryParams.append(" AND ").append(Base.ID).append(" != ").append(getColumnValue(Base.ID,param.get(Base.ID)));
					queryParams.append(" AND ").append(Base.ID).append(" != ?");
					values.add(param.get(Base.ID));
				}
			} else {
				return false;
			}
		}
		sql = sql.replaceAll("#queryParams#", queryParams.toString());

		return getJdbcTemplate().queryForList(sql, values.toArray()).size() > 0;
	}

	public List<Map<String, Object>> queryAll(Map<String, String> param) {
		String sql = getQuerySql();
		List<Object> values = null;
		if (param != null) {
			values = new ArrayList<Object>();
			StringBuffer queryParams = new StringBuffer();
			for (Map.Entry<String, String> e : param.entrySet()) {
				String key = e.getKey();
				String v = e.getValue();
				if ("".equals(v)) {
					continue;
				}
				String op = "=";
				if ("START_TIME".equals(key)) {
					op = ">=";
				}
				if ("END_TIME".equals(key)) {
					op = "<=";
				}

				// queryParams.append(" AND ").append(key).append(op).append(getColumnValue(key,v));
				queryParams.append(" AND ").append(key).append(op)
						.append(" ? ");
				values.add(v);
			}
			sql = sql.replaceAll("#queryParams#", queryParams.toString());

		}
		return getJdbcTemplate().queryForList(sql,
				values == null ? null : values.toArray());
	}

	public Map<String, Object> expand(Map<String, String> param) {
		Map<String, Object> result = null;
		if (param.get("ID") != null) {
			result = getJdbcTemplate().queryForMap(
					"SELECT * FROM ("
							+ getQuerySql().replaceAll("#queryParams#", "")
							+ ") AS _T WHERE ID=" + param.get("ID"));
		}
		return result;
	}

	public List<Map<String, Object>> executeQuery(String sql) {
		return getJdbcTemplate().queryForList(sql);
	}

	public void export(HttpServletResponse response, PageCond page,
			Map<String, String> param, String[] columns) {
		Writer writer = null;
		try {
			String seperator = param.get("__seperator");
			if (seperator == null) {
				seperator = "≈≈≈";
			}
			param.remove("__seperator");
			writer = response.getWriter();
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			List<Map<String, Object>> list = querySql(getExportSql(), param,
					page);
			for (Map<String, Object> item : list) {
				for (String col : columns) {
					bufferedWriter.write(item.get(col) + seperator);
				}
				bufferedWriter.write("\n");
				bufferedWriter.flush();
			}
			bufferedWriter.close();
			writer.close();
		} catch (Exception e) {
			logger.error(e.toString(), e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
	}

	public String getExportSql() {
		return getQuerySql();
	}

}
