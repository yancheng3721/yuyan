package com.yuyan.emall.admin.dao.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

import com.yuyan.emall.admin.base.AbstractedBaseDAO;

@Repository
public class MenuDAO extends AbstractedBaseDAO{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public String getTBName(){
    	return "T_MENU";	
    }
    
    public String getQuerySql(){
    	return "SELECT A.ID,A.NAME,A.CODE,A.PARENT_ID,A.ACTION,A.ICON,A.CREATE_TIME,A.UPDATE_TIME,A.UPDATE_USER,B.NAME AS PARENT_NAME,B.CODE AS PARENT_CODE FROM T_MENU A LEFT JOIN T_MENU B ON A.PARENT_ID=B.ID";	
    }
	
	public JdbcTemplate getJdbcTemplate(){
		return 	jdbcTemplate;
	}
    
    public String[] getUniqKeys(){
    	return "ID".split(",");
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
