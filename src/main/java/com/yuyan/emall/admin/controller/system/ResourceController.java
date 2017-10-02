package com.yuyan.emall.admin.controller.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.yuyan.emall.admin.base.Base;
import com.yuyan.emall.admin.base.PageCond;
import com.yuyan.emall.admin.util.JSONUtil;
import com.yuyan.emall.admin.util.CsvReader;
import com.yuyan.emall.admin.util.ResponseBuilder;
import com.yuyan.emall.admin.vo.ResponseResult;
import com.yuyan.emall.admin.vo.UploadResult;
import com.yuyan.emall.admin.service.system.ResourceService;
import com.yuyan.emall.admin.service.selector.DropDownSelectService;


@Controller
public class ResourceController {

	public static final String MAIN_PAGE = "/system/manageResource";
	
	private static Logger logger = LoggerFactory
			.getLogger(ResourceController.class);

	@Autowired
	private ResourceService service;
	@Autowired
	private DropDownSelectService dropDownSelectService;


	@RequestMapping("/resource/manageResource")
	public String resourceManage(
			@RequestParam(required = false) Map<String, Object> context,
			ModelMap map) {
		PageCond page = getPageFromContext(context);
		Map<String, String> param = getParamFromContext(context, "searchbox.");
		map.put("objs", service.query(param, page));
		map.put("page", page);
		map.put("searchbox", param);
		
		return MAIN_PAGE;
	}

	@RequestMapping("/resource/queryResource")
	public String queryResource(
			@RequestParam(required = false) Map<String, Object> context,
			ModelMap map) {
		PageCond page = getPageFromContext(context);
		Map<String, String> param = getParamFromContext(context, "searchbox.");
		map.put("objs", service.query(param, page));
		map.put("page", page);
		map.put("searchbox", param);
		
		return MAIN_PAGE;
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

	@RequestMapping("/resource/saveResource")
	public void save(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false) Map<String, Object> context,
			ModelMap map) {
		Map<String, String> param = getParamFromContext(context, "");
		String user = request.getSession().getAttribute("user").toString();
		param.put(Base.UPDATE_USER, user);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now = df.format(new Date());
		param.put(Base.UPDATE_TIME, now);
		if(param.get(Base.ID) == null ||"".equals(param.get(Base.ID))){
			param.put(Base.CREATE_TIME, now);
		}
		String msg = "success";
		try {
			if (service.exists(param)) {
				msg = "exists";
			} else {
				service.save(param);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			msg = "failed";
		}
		writeMsg(response, msg);
	}

	@RequestMapping("/resource/deleteResource")
	public void delete(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false) Map<String, Object> context,
			ModelMap map) {
		Map<String, String> param = getParamFromContext(context, "");

		String msg = "success";
		try {
			service.delete(param);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			msg = "failed";
		}
		writeMsg(response, msg);
	}

	
	@RequestMapping("/resource/expandResource")
	public void expandResource(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(required = false) Map<String, Object> context,
			ModelMap map) {
		Map<String, String> param = getParamFromContext(context, "");
		Map<String, Object> obj = service.expand(param);
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.write(JSONUtil.toJSONString(obj, "","yyyy-mm-dd HH:mm:ss"));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (pw != null)
				pw.close();
		}
		
	}

	@RequestMapping("/resource/batchDeleteResource")
	public void  batchDelete(@RequestParam(required = false)Map<String,Object> context,ModelMap map,HttpServletResponse response){
		String msg="success";
		List<Map<String,String>> params = new ArrayList<Map<String,String>>();
		try{
			String ids = (String) context.get("ids");
			if(!StringUtils.isEmpty(ids)){
				ids = ids.replaceAll("，", ",");
				String[] idAry = ids.split(",");
				for(int i=0;i<idAry.length;i++){
					String id = idAry[i];
					if(!StringUtils.isEmpty(id)){
						Map<String,String> param = new HashMap<String,String>();
						param.put(Base.ID, id);
						params.add(param);
					}
				}
				service.batchDelete(params);
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			msg="failed";
		}
		writeMsg(response, msg);
		
	}

	
	@RequestMapping("/resource/uploadResource")
    @ResponseBody
	public ResponseResult uploadPage(@RequestParam MultipartFile scan, HttpServletRequest request,
            HttpServletResponse response) {
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String importFields = request.getParameter("importFields");
	    if(importFields==null ||"".equals(importFields)){
            return null;
        }
	    String [] importAry = importFields.split(",");
        int columnLimit = importAry.length;
        UploadResult result = new UploadResult();
        result.setType(UploadResult.SUCCESS);
        result.setMessage("上传成功");
        List<Map<String,String>> params = new ArrayList<Map<String, String>>();
        InputStream is;
        try {
            is = scan.getInputStream();
            CsvReader csvReader = new CsvReader(is, Charset.forName("GBK"));
            int count = 0;
            Set<String> set = service.getUniqueKey();
			Map<String, Integer> map = new HashMap<String, Integer>();// key-上传每条数据唯一键字段，value-
																		// 唯一字段所在行数
			int[] arr = new int[set.size()];// 存放唯一键在上传字段的位置
            while (csvReader.readRecord()) {
            	Map<String, String> resourceMap = new HashMap<String, String>();
                count++;
                if (csvReader.getColumnCount() != columnLimit) {
                    result.setType(UploadResult.FILE_ERROR);
                    result.setMessage("第" + count + "行必须是"+columnLimit+"列！");
                    return ResponseBuilder.ok(result);
                }
                if (count > 2000) {
                    result.setType(UploadResult.FILE_ERROR);
                    result.setMessage("每次上传不能超过2000条！");
                    return ResponseBuilder.ok(result);
                }
                String startTime = "";
				String endTime = "";
				int index = 0;
                for (int i = 0; i < importAry.length; i++) {
                	String t = csvReader.get(i).trim();
					if ("null".equals(t.toLowerCase())) {
						t = "";
					}
                	if (set.contains(importAry[i])) {
						arr[index] = i;
						index++;
					}
					if ("START_TIME".equals(importAry[i])) {
						startTime = t;
					} else if ("END_TIME".equals(importAry[i])) {
						endTime = t;
					}
					resourceMap.put(importAry[i], t);
				}
				String uniqueKey = "";
				for (int j = 0; j < arr.length; j++) {
					uniqueKey = uniqueKey + csvReader.get(arr[j]).trim();
				}
				if (map.containsKey(uniqueKey)) {
					result.setType(UploadResult.FILE_ERROR);
					result.setMessage("第" + count + "行数据与第"
							+ map.get(uniqueKey) + "行重复！");
					return ResponseBuilder.ok(result);
				}else{
					map.put(uniqueKey, count);
				}
				if (!"".equals(startTime) && !"".equals(endTime)) {
						Date start = sim.parse(startTime);
						Date end = sim.parse(endTime);
						if (end.before(start)) {
							result.setType(UploadResult.FILE_ERROR);
							result.setMessage("第" + count + "行开始时间大于结束时间！");
							return ResponseBuilder.ok(result);
						}else {
							resourceMap.put("START_TIME",
								sim.format(start));
							resourceMap.put("END_TIME", sim.format(end));
						}
					}
                String user = request.getSession().getAttribute("user").toString();
                resourceMap.put(Base.UPDATE_USER, user);
                String now = sim.format(new Date());
                resourceMap.put(Base.UPDATE_TIME, now);
                if(resourceMap.get(Base.ID) == null ||"".equals(resourceMap.get(Base.ID))){
                    resourceMap.put(Base.CREATE_TIME, now);
                }
                params.add(resourceMap);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("表格必须是"+columnLimit+"列", e);
            result.setType(UploadResult.ERROR);
            result.setMessage("表格必须是"+columnLimit+"列");
        } catch (ParseException e) {
			logger.error("时间格式错误", e);
			result.setType(UploadResult.ERROR);
			result.setMessage("时间格式错误");
		} catch (FileNotFoundException e) {
            logger.error("没找到csv文件", e);
            result.setType(UploadResult.ERROR);
            result.setMessage("没找到csv文件");
        } catch (IOException e) {
            logger.error("读取出错", e);
            result.setType(UploadResult.ERROR);
            result.setMessage("读取出错");
        }
        if (result.getType() != 1) {
			return ResponseBuilder.ok(result);
		}
        try {
			service.batchUpdate(params);
			if (params.size() != 0) {
				result.setMessage("上传成功！");
			} else {
				result.setMessage("上传文件为空，上传失败！");
			}
		} catch (Exception e) {
			result.setMessage("上传失败！");
		}
        return ResponseBuilder.ok(result);
	}

	    @RequestMapping("/resource/exportResource")
	    public void exportResource(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false) Map<String, Object> context) {
	        PageCond page = getPageFromContext(context);
	        page.setLength(10000);
	        Map<String, String> param = getParamFromContext(context, "searchbox.");
	        String[] columns = request.getParameter("exportFields").split(",");
	        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	        String outputfilename = df.format(new Date());// new Date()为获取当前系统时间
	        response.setCharacterEncoding("GBK");
	        String filename = "Resource_" + outputfilename;
	        response.addHeader("Content-Disposition", "attachment;filename=" + filename + ".csv");
	        service.export(response, page, param,columns);
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
