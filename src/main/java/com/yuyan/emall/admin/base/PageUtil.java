package com.yuyan.emall.admin.base;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class PageUtil {
	private static final Logger logger = LoggerFactory
			.getLogger(PageUtil.class);

	public static <T> List<T> doPage(JdbcTemplate jdbcTemplate, String sql,
			RowMapper<T> rm, PageCond page) {
		sql = sql.toLowerCase();
		String sqlCount = "SELECT COUNT(*) FROM (" + replaceOrderBy(sql)
				+ ") AS T";
		int ALL = jdbcTemplate.queryForInt(sqlCount);
		page.setTotal(ALL);
		page.reCalculate();

		String sqlPageBegin = "select * from (";
		String sqlPageEnd = ")as T limit " + page.getBegin()
				+ "," + page.getLength();
		return jdbcTemplate.query(sqlPageBegin + sql + sqlPageEnd, rm);
	}

	public static <T> List<T> doPageNotToLowerCase(JdbcTemplate jdbcTemplate,
			String sql, RowMapper<T> rm, PageCond page) {
		String sqlCount = "SELECT COUNT(*) FROM (" + replaceOrderBy(sql)
				+ ") AS T";
		int ALL = jdbcTemplate.queryForInt(sqlCount);
		page.setTotal(ALL);
		page.reCalculate();

		String sqlPageBegin = "select * from ((";
		String sqlPageEnd = ")as T limit " + page.getBegin()
				+ "," + page.getLength();
		return jdbcTemplate.query(sqlPageBegin + sql + sqlPageEnd, rm);
	}

	public static List<Map<String, Object>> doPage(JdbcTemplate jdbcTemplate,
			String sql, PageCond page, Object... params) {
		sql = sql.toLowerCase();
		String sqlCount = "SELECT COUNT(*) FROM (" + replaceOrderBy(sql)
				+ ") AS T";
		int ALL = jdbcTemplate.queryForInt(sqlCount, params);
		page.setTotal(ALL);
		page.reCalculate();

		String sqlPageBegin = "select * from (";
		String sqlPageEnd = ") as T limit " + page.getBegin()
				+ "," + page.getLength() ;
		return jdbcTemplate.queryForList(sqlPageBegin + sql + sqlPageEnd,
				params);
	}

	public static List<Map<String, Object>> doPageWithParam(
			JdbcTemplate jdbcTemplate, String sql, PageCond page,
			String condition, Object... params) {
		sql = sql.toLowerCase();
		String sqlCount = "SELECT COUNT(*) FROM (" + replaceOrderBy(sql)
				+ ") AS T WHERE 1=1 " + condition;
		int ALL = jdbcTemplate.queryForInt(sqlCount, params);
		page.setTotal(ALL);
		page.reCalculate();

		String sqlPageBegin = "select * from (";
		String sqlPageEnd = ")as DK where 1=1 " + condition
				+ " limit " + page.getBegin() + ","
				+ page.getLength();
		return jdbcTemplate.queryForList(sqlPageBegin + sql + sqlPageEnd,
				params);
	}

	private static String replaceOrderBy(String sql) {

		String result = sql;
		result = result.toLowerCase();
		try {
			String tmp = result;
			while (tmp.indexOf("  ") > -1) {
				tmp = tmp.replace("  ", " ");
			}
            if(tmp.indexOf("order by")>-1){
                tmp = tmp.substring(0, tmp.indexOf("order by"));
            }

			result = tmp;
		} catch (Exception e) {
			logger.error("", e);
		}
		return result;

	}

	public static PageCond getPageFromContext(Map<String, Object> context) {
		PageCond page = new PageCond();
		page.setCurrentPage(context.get("currentPage") == null
				|| "".equals(context.get("currentPage"))
				|| !NumberUtils.isNumber(context.get("currentPage").toString()) ? 1
				: Integer.parseInt(context.get("currentPage").toString()));
		page.setLength(context.get("length") == null
				|| "".equals(context.get("length"))
				|| !NumberUtils.isNumber(context.get("length").toString()) ? 10
				: Integer.parseInt(context.get("length").toString()));
		return page;
	}

	public static void main(String[] args) {
		String sql = "SELECT ORDER FROM TTT ORDER    BY ORDER DESC LIMIT 0,1";
		String replaced = PageUtil.replaceOrderBy(sql);
		System.out.println(replaced);
	}

	public static PageCond getPageFromRequestContext(HttpServletRequest request) {
		PageCond page = new PageCond();
		page.setCurrentPage(request.getParameter("currentPage") == null
				|| "".equals(request.getParameter("currentPage"))
				|| !NumberUtils.isNumber(request.getParameter("currentPage")
						.toString()) ? 1 : Integer.parseInt(request
				.getParameter("currentPage").toString()));
		page.setLength(request.getParameter("length") == null
				|| "".equals(request.getParameter("length"))
				|| !NumberUtils.isNumber(request.getParameter("length")
						.toString()) ? 10 : Integer.parseInt(request
				.getParameter("length").toString()));
		return page;
	}
}
