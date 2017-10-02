package com.yuyan.emall.admin.dao.selector;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DropDownSelectDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Map<String, Object>> query(String sql) {
		return jdbcTemplate.queryForList(sql);
	}
}
