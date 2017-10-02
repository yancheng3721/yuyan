package com.yuyan.emall.admin.controller.selector;

import com.yuyan.emall.admin.base.Base;
import com.yuyan.emall.admin.base.PageCond;
import com.yuyan.emall.admin.service.selector.DropDownSelectService;
import com.yuyan.emall.admin.service.selector.SelectorService;
import com.yuyan.emall.admin.service.system.MenuService;
import com.yuyan.emall.admin.util.CsvReader;
import com.yuyan.emall.admin.util.JSONUtil;
import com.yuyan.emall.admin.util.ResponseBuilder;
import com.yuyan.emall.admin.vo.ResponseResult;
import com.yuyan.emall.admin.vo.UploadResult;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;


@Controller
public class SelectorController {

	public static final String MAIN_PAGE = "/selector/";
	
	private static Logger logger = LoggerFactory
			.getLogger(SelectorController.class);

	@Autowired
	private SelectorService service;


	@RequestMapping("/selector/query")
	public String query(
			@RequestParam(required = false) Map<String, Object> context,
			ModelMap map) {
		PageCond page = getPageFromContext(context);
        String module =(String) context.get("module");
		Map<String, String> param = getParamFromContext(context, "searchbox.");
		map.put("objs", service.query(module,param, page));
		map.put("page", page);
		map.put("searchbox", param);
		
		return MAIN_PAGE+module;
	}

    private PageCond getPageFromContext(Map<String, Object> context) {
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

    private Map<String, String> getParamFromContext(
            Map<String, Object> context, String keyprefix) {
        Map<String, String> result = new HashMap<String, String>();
        if (context != null && !context.isEmpty()) {
            for (Entry<String, Object> e : context.entrySet()) {
                String k = e.getKey();
                String v  = (String) e.getValue();
                if (k.startsWith(keyprefix)&&v!=null && !"".equals(v.trim())) {
                    int index = keyprefix.length();
                    String realKey = k.substring(index);
                    result.put(realKey, (String) v.trim());
                }
            }
        }
        return result;
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
