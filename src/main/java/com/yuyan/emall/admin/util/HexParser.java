package com.yuyan.emall.admin.util;

import java.io.UnsupportedEncodingException;

public class HexParser {
	public static String encodingHex(String str) throws UnsupportedEncodingException{
		String ret = "";
		byte[] b = str.getBytes("utf-8");
		for (int i = 0; i < b.length; i++)
		{
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1)
			{
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String s= "修护";
		System.out.println(encodingHex(s));
	}
}
