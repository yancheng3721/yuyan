package com.yuyan.emall.admin.vo;

/**
 * 
 * @author administrator
 *
 */
public class ResponseResult {

	private String status;
	
	private Object obj;

	public ResponseResult(String status, Object obj) {
		this.status = status;
		this.obj = obj;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}
	
	
}
