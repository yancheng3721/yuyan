package com.yuyan.emall.admin.controller.system;

import com.yuyan.emall.admin.service.system.DefdocService;
import com.yuyan.emall.admin.service.tree.Node;
import com.yuyan.emall.admin.service.tree.defdoc.DefdocTreeService;
import com.yuyan.emall.admin.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/12/8.
 */
@Controller
public class DefdocTreeController{
    public static final String MAIN_PAGE = "/tree/defdoc/frame";
    private static Logger logger = LoggerFactory
            .getLogger(DefdocTreeController.class);

    @Autowired
    private DefdocTreeService service;

    @Autowired
    private DefdocService defDocService;

    @RequestMapping("/tree/defdoc/frame")
    public String frame(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam(required = false) Map<String, Object> context,
                        ModelMap map){
        return "/tree/defdoc/frame";
    }

    @RequestMapping("/tree/defdoc/tree")
    public String tree(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam(required = false) Map<String, Object> context,
                       ModelMap map){
        String checkedId=request.getParameter("checkedId");
        Collection<Node> nodes = service.getRoots();
        Node n = service.getNodeByNodeId(checkedId);
        String rootStr = "[]";
        String checkedNode = "{}";
        if(nodes!=null) {
            rootStr = JSONUtil.toJSONString(nodes, "parent,root");
            map.put("rootStr",rootStr);
        }
        if(n!=null){
            checkedNode = JSONUtil.toJSONString(n,"parent,root");
            map.put("checkedNode",checkedNode);
        }
        return "/tree/defdoc/tree";
    }

    @RequestMapping("/tree/defdoc/getAllNodes")
    public void getAllNodes(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(required = false) Map<String, Object> context,
                            ModelMap map){
        Collection<Node> nodes = service.getAllNodes();
        String allNodesStr = "[]";
        if(nodes!=null){
            allNodesStr = JSONUtil.toJSONString(nodes,"parent,root");
            writeMsg(response,allNodesStr);
        }
    }

    @RequestMapping("/tree/defdoc/initial")
    public void initial(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam(required = false) Map<String, Object> context,
                        ModelMap map){
        service.initial();
    }

    @RequestMapping("/tree/defdoc/toSaveDefDoc")
    public String toSaveDefDoc(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(required = false) Map<String, Object> context,
                               ModelMap map){
        String nodeId=request.getParameter("ID");
        String parentId=request.getParameter("PARENT_ID");
        Map<String,Object> n = null;
        if(nodeId!=null && !"".equals(nodeId)){
            Map<String,String> params=new HashMap <String,String>();
            params.put("ID",nodeId);
            List<Map<String,Object>> results =  defDocService.queryAll(params);
            if(results !=null && results.size()>0){
                n = results.get(0);
            }
        }else {
            if(null == parentId ||"".equals(parentId.trim())){
                parentId="0";
            }
            n = new HashMap<String,Object>();
            n.put("PARENT_ID",parentId);
        }

        String parent = "{}";
        if(n!=null){
            parent = JSONUtil.toJSONString(n,"");
            map.put("parent",parent);
        }

        return "/tree/defdoc/saveDefdoc";
    }

    @RequestMapping("/tree/defdoc/deleteDefDoc")
    public void deleteDefDoc(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(required = false) Map<String, Object> context,
                               ModelMap map){
        String nodeId=request.getParameter("ID");
        String msg = "sucess";
        Node n = null;
        try{
            if(nodeId!=null && !"".equals(nodeId)){
                n = service.getNodeByNodeId(nodeId);
                if(n!=null){
                    Map<String,String> param = new HashMap<String,String>();
                    param.put("ID",""+n.getId());
                    defDocService.delete(param);
                }
            }
        }catch(Exception e){
            msg="error";
            logger.error("删除失败",e);
        }
        writeMsg(response,msg);
    }

    @RequestMapping("/tree/defdoc/initialTree")
    public String initialTree(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam(required = false) Map<String, Object> context,
                               ModelMap map){
        service.initial();
        return tree(request,response,context,map);
    }


    /**
     * 输出返回信息
     *
     * @param response
     * @param msg
     */
    public void writeMsg(HttpServletResponse response, String msg) {
        PrintWriter pw = null;
        try {
            response.setContentType("text/html;charset=utf-8");
            pw = response.getWriter();
            pw.write(msg);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }






}
