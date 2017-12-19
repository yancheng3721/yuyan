package com.yuyan.emall.admin.service.tree.defdoc;

import com.yuyan.emall.admin.service.system.DefdocService;
import com.yuyan.emall.admin.service.tree.AbstractTreeService;
import com.yuyan.emall.admin.service.tree.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by admin on 2017/12/8.
 */
@Service
public class DefdocTreeService extends AbstractTreeService{

    @Autowired
    private DefdocService service;

    @PostConstruct
    public void initial(){
        super.initial();
    }
    @Override
    public Collection<Node> getAllNodes() {
        List<Map<String,Object>> list =  service.queryAll(new HashMap<String, String>());
        Collection<Node> results = new ArrayList<Node>();
        if(list!=null&&list.size()>0){
            for (Map<String,Object> m:list){
                Node n = convert(m);
                if(n!=null){
                    results.add(n);
                }
            }
        }
        return results;
    }

    private Node convert(Map<String, Object> m) {
        DefDoc result = null;
        if(m!=null){
            result = new DefDoc();
            result.setId((Long) m.get("ID"));
            result.setParentId((Long) m.get("PARENT_ID"));
            result.setDocCode((String) m.get("DOC_CODE"));
            result.setDocName((String) m.get("DOC_NAME"));
            result.setRemark((String) m.get("REMARK"));
        }
        return result;
    }
}
