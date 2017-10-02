package com.yuyan.emall.admin.service.system;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yuyan.emall.admin.base.Base;
import com.yuyan.emall.admin.base.PageCond;
import com.yuyan.emall.admin.dao.system.ResourceDAO;

@Service
public class ResourceService {

    @Autowired
    private ResourceDAO dao;
    
    public List<Map<String,Object>> query(Map<String,String> param,PageCond page){
        return dao.query(param, page);
    }
    
    @Transactional
    public int save(Map<String,String> param){
        int result = 0;
        if(param!=null){
            String id=param.get(Base.ID);
            if(id!=null && !"".equals(id)){
                result = dao.updateAllFields(param);
            }else{
                result = dao.insert(param);
            }
        }
        return result;
    }
    
    @Transactional
    public int delete(Map<String,String> param){
        int result = dao.delete(param);
        return result;
    }
    
    @Transactional
	public int update(Map<String, String> param) {
		return dao.updateWithoutId(param);
	}
    
    public boolean exists(Map<String,String> param){
        return dao.exists(param);
    }
    
    @Transactional
    public void batchDelete(List<Map<String, String>> params) {
		if(params!=null&& params.size()>0){
			for(Map<String, String> param:params){
				dao.delete(param);
			}
		}
	}
    
    public List<Map<String,Object>> queryAll(Map<String,String> param){
    	return dao.queryAll(param);
    }
    
    public Map<String,Object> expand(Map<String,String> param){
    	return dao.expand(param);
    }
    
   public int batchUpdate(List<Map<String, String>> params) {
		List<Map<String, String>> addList = new ArrayList<Map<String, String>>();
		List<Map<String, String>> updateList = new ArrayList<Map<String, String>>();
		if (params != null && params.size() > 0) {
			for (Map<String, String> map : params) {
				boolean flag = dao.exist(map);
				if (flag) {
					updateList.add(map);
				} else {
					addList.add(map);
				}
			}
			for (Map<String, String> map : addList) {
				save(map);
			}
			for (Map<String, String> map : updateList) {
				update(map);
			}
		}
		return 0;
	}
    
    public void export(HttpServletResponse response,PageCond page,Map<String, String> param ,String[] columns){
        dao.export(response, page, param ,columns);
    }
    
    public Set<String> getUniqueKey() {
		Set<String> set = new HashSet<String>();
		CollectionUtils.addAll(set, dao.getUniqKeys());
		return set;
	}
	
	// 检查时间是否重叠
	public boolean checkTime(Map<String, String> param) {
		boolean result = false;
		String id = param.get(Base.ID);
		String keyword = param.get("KEYWORD");
		List<Map<String, Object>> list = dao.queryByKeyword(keyword, id);
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date s = new Date();
		Date e = new Date();
		try {
			s = sim.parse(param.get("START_TIME"));
			e = sim.parse(param.get("END_TIME"));
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		Date ss = new Date();
		Date ee = new Date();
		for (Map<String, Object> m : list) {
			String start = String.valueOf(m.get("START_TIME"));
			String end = String.valueOf(m.get("END_TIME"));
			try {
				ss = sim.parse(start);
				ee = sim.parse(end);
			} catch (ParseException ex) {
				ex.printStackTrace();
			}
			if ((s.after(ss) && s.before(ee)) || (e.after(ss) && e.before(ee))) {
				result = true;
				break;
			}
		}
		return result;
	}
}
