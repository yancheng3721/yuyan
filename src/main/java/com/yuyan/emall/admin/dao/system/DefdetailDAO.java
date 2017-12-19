package com.yuyan.emall.admin.dao.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

import com.yuyan.emall.admin.base.AbstractedBaseDAO;

@Repository
public class DefdetailDAO extends AbstractedBaseDAO{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public String getTBName(){
    	return "t_definition_detail";	
    }
    
    public String getQuerySql(){
    	return "select A.ID,A.DOC_ID,A.DEF_KEY,A.DEF_VALUE,A.STATE,A.REMARK,A.CREATE_TIME,A.UPDATE_TIME,A.UPDATE_USER,B.DOC_CODE,B.DOC_NAME from t_definition_detail A LEFT JOIN t_definition_doc B ON A.DOC_ID=B.ID";
    }
	
	public JdbcTemplate getJdbcTemplate(){
		return 	jdbcTemplate;
	}
    
    public String[] getUniqKeys(){
    	return "DOC_ID,DEF_KEY,DEF_VALUE,STATE".split(",");
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
