package com.yuyan.emall.admin.dao.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

import com.yuyan.emall.admin.base.AbstractedBaseDAO;

@Repository
public class ModuleDAO extends AbstractedBaseDAO{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public String getTBName(){
    	return "T_MODULE";	
    }
    
    public String getQuerySql(){
    	return "SELECT ID,NAME,CODE,CREATE_TIME,UPDATE_TIME,UPDATE_USER FROM T_MODULE";	
    }
	
	public JdbcTemplate getJdbcTemplate(){
		return 	jdbcTemplate;
	}
    
    public String[] getUniqKeys(){
    	return "NAME".split(",");
    }
	
	public List<Map<String, Object>> queryByKeyword(String keyword, String id) {
		String sql = "SELECT START_TIME,END_TIME FROM " + getTBName()
				+ " WHERE KEYWORD = ?";
		if (StringUtils.isNotEmpty(id)) {
			sql = sql + " AND ID !=" + id;
		}
		return jdbcTemplate.queryForList(sql, new Object[] { keyword });
	}
}
