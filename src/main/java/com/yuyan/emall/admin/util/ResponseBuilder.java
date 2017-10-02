package com.yuyan.emall.admin.util;

import com.yuyan.emall.admin.vo.ResponseResult;

public class ResponseBuilder {

	public final static String OK = "ok";
	
	public final static String ERROR = "error";
	
	public static ResponseResult ok(String msg) {
		return new ResponseResult(OK, msg);
	}
	
	public static ResponseResult error(String msg) {
		return new ResponseResult(ERROR, msg);
	}
	
	public static ResponseResult ok(Object obj) {
		return new ResponseResult(OK, obj);
	}
	
	public static ResponseResult error(Object obj) {
		return new ResponseResult(ERROR, obj);
	}
}
