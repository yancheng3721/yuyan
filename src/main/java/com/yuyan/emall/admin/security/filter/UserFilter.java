package com.yuyan.emall.admin.security.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yuyan.emall.admin.util.FileRowMapper;
import com.yuyan.emall.admin.util.PropertiesUtil;
import com.yuyan.emall.admin.util.ReadFileUtil;

public class UserFilter implements Filter {

	private static Map<String, String> whiteList = new HashMap<String, String>();
	private static final Logger logger = LoggerFactory.getLogger(UserFilter.class);
	static {
		refreshWhiteList();
		
	}

	public void doFilter(ServletRequest srequest, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) srequest;
		String urlPath = request.getServletPath();

		if(request.getSession().getAttribute("user")==null){
			request.getSession().setAttribute("user", "10000");
		}
		/*if (whiteList.get(urlPath) == null) {
			ServletContext servletContext = request.getSession()
					.getServletContext();
			ApplicationContext ctx = WebApplicationContextUtils
					.getWebApplicationContext(servletContext);
			UserService userService = ctx.getBean(UserService.class);
			Map<String, String> userMap = userService.getUserMap();
			String name = "";
			if(request.getSession().getAttribute("user") != null){
				name = request.getSession().getAttribute("user").toString();
			}else {
				//用户没有登录，跳转到登录页面
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				httpResponse.sendRedirect("noLogon.do");
				return;
			}
			
			String flag = userMap.get(name + urlPath);
			if ("ok".equals(flag)) {
				chain.doFilter(request, response);
			} else {
				// 无权限跳转到404页面
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("/page-404.html");
				dispatcher.forward(request, response);
				return;
			}
			
		} else {*/
			chain.doFilter(request, response);
		/*}*/
	}

	public void init(FilterConfig config) throws ServletException {
	}

	public void destroy() {
	}

	private static void refreshWhiteList() {
		logger.info("初始化权限");
		whiteList.clear();
		whiteList.put("/noLogon.do", "ok");
		whiteList.put("/logon.do", "ok");
		whiteList.put("/loginout.do", "ok");
		
		// 从配置文件读取白名单
		List<String> list = ReadFileUtil.readFile("filterWhiteList.txt",
				new FileRowMapper<String>() {

					public String mapRow(String s) {
						return s.trim();
					}

				});
		for (String res : list) {
			whiteList.put(res, "ok");
		}
	}
}
