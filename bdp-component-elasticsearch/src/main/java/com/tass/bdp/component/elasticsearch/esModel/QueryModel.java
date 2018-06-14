/** ======================================
 * Beijing JN TASS Technology Co.,Ltd.
 * Date   ：2018年1月25日 下午5:37:22
 * author ：yzy
 * Version：0.1
 * =========Modification History==========
 * Date          Name        Description
 * 2018年1月25日    yzy        创建QueryModel类
 */
package com.tass.bdp.component.elasticsearch.esModel;

import java.util.List;

public class QueryModel {

	public static final String ORDER_DESC = "desc";
	public static final String ORDER_ASC = "asc";
	
	private List<ParamModel> filterList;
	private int size = 10;
	private int from = 0;
	private String sortFiled;
	private String order = ORDER_DESC;
	
	public List<ParamModel> getFilterList() {
		return filterList;
	}
	public void setFilterList(List<ParamModel> filterList) {
		this.filterList = filterList;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public String getSortFiled() {
		return sortFiled;
	}
	public void setSortFiled(String sortFiled) {
		this.sortFiled = sortFiled;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
}
