package com.yuyan.emall.admin.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PageCond {

	private static Logger logger = LoggerFactory
	.getLogger(PageCond.class);

	private int begin =0;
	
	/**
     * 每页多少条记录
     */
	private int length =40;
	
	private long total;
	private int currentPage=1;
	private int totalPage;
	private int beforePage;
	private int nextPage;
	
	public static final int SORT_ASC=0;
	public static final int SORT_DESC=1;
	
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getBegin() {
		return begin;
	}
	public void setBegin(int begin) {
		this.begin = begin;
	}
	public int getLength() {
		if (length<=10) {
			return 10;
		}
		return length;
	}
	public void setLength(int length) {
		if (length <= 10 ) {
			length = 10;
		}
		this.length = length;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public int getBeforePage() {
		return beforePage;
	}
	public void setBeforePage(int beforePage) {
		this.beforePage = beforePage;
	}
	public int getNextPage() {
		return nextPage;
	}
	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}
	
	public int getStart(){
		return (currentPage-1)*length;
	}
	public int getRows(){
		return length;
	}
	
	public void reCalculate(){
		int current = this.getCurrentPage();
		int totalPage=(int) this.getTotal()/this.getLength();
		if(this.getTotal()%this.getLength()!=0){totalPage++;};
		if(this.getTotal()==0){current=1;totalPage=1;};
		if(totalPage<current){current=totalPage;};
		
		int begin = this.getLength()*(current-1);
		int next=current==totalPage?totalPage:current+1;
		int before=current>1?current-1:current;
		this.setNextPage(next);
		this.setBeforePage(before);
		this.setBegin(begin);
		this.setTotalPage(totalPage);
		this.setCurrentPage(current);
		
		//writePage();
	}
	@SuppressWarnings("unused")
	private void writePage() {
		// TODO Auto-generated method stub
		logger.info("page.getBegin():"+this.begin);
		logger.info("page.getCurrentPage():"+this.currentPage);
		logger.info("page.getLength():"+this.length);
		logger.info("page.getTotal():"+this.total);
		logger.info("page.getTotalPage():"+this.totalPage);
		logger.info("page.getNextPage():"+this.nextPage);
		logger.info("page.getBeforePage():"+this.beforePage);
		
	}
}
