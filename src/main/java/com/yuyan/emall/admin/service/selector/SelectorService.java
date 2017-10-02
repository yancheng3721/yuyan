package com.yuyan.emall.admin.service.selector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yuyan.emall.admin.dao.selector.SqlContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yuyan.emall.admin.base.PageCond;
import com.yuyan.emall.admin.dao.selector.SelectorDAO;

@Service
public class SelectorService {

	@Autowired
	private SelectorDAO dao;
	
	public List<Map<String,Object>> query(String module,Map<String,String> param ,PageCond page){
        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
        String sql = SqlContext.getSqlByModule(module);
        if(sql!=null){
            result= dao.query(sql, param, page);
        }
        return result;
	}
	
}
