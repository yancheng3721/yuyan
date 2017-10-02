/*
 * Java CSV is a stream based library for reading and writing
 * CSV and other delimited data.
 *   
 * Copyright (C) Bruce Dunwiddie bruce@csvreader.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */
package com.yuyan.emall.admin.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.HashMap;

/**
 * A stream based parser for parsing delimited text data from a file or a
 * stream.
 */
public class CsvReader extends BufferedReader{
	
	
	public CsvReader(Reader in,String seperator) {
		super(in);
		this.seperator = seperator;
	}

	public CsvReader(InputStream is, Charset forName) {
		this(new InputStreamReader(is,forName), "≈≈≈");
	}

	private String seperator;

	private String[] cols = null;

	public boolean readRecord(){
		boolean result = false;
		cols = null;
		String lineStr = null;
		try {
			lineStr = super.readLine();
		} catch (IOException e) {
		}
		if(lineStr!=null){
			cols = lineStr.split(seperator,-1);
			result=true;
		}
		return result;
	}
	
	public String get(int index){
		String result = null;
		if(cols!=null && index<cols.length){
			result = cols[index];
		}
		return result;
	}

	public int getColumnCount() {
		return cols!=null?cols.length:0;
	}
	
}