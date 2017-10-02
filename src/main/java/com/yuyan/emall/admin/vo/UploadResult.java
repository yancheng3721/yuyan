package com.yuyan.emall.admin.vo;

public class UploadResult {

	public static int SUCCESS = 1; //操作成功
	
	public static int FILE_ERROR = 2; //文件有错误
	
	public static int DUPLICATE_ERROR = 3; //数据库记录重复
	
	public static int ERROR = 4; //其他错误
	
	private int type;
	
	private String message;
	
	public UploadResult() {
		
	}

	public UploadResult(int type, String message) {
		this.type = type;
		this.message = message;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
