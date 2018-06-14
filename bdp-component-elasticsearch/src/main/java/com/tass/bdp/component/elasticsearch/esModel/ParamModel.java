/** ======================================
 * Beijing JN TASS Technology Co.,Ltd.
 * Date   ：2018年1月25日 下午5:37:22
 * author ：yzy
 * Version：0.1
 * =========Modification History==========
 * Date          Name        Description
 * 2018年1月25日    yzy        创建ParamModel类
 */
package com.tass.bdp.component.elasticsearch.esModel;

public class ParamModel {

	//连接运算符
	public static final int AND = 1;
	public static final int OR = 2;
	public static final int NOT = 3;
	
	//运算符
	public static final int EQ = 7;
	public static final int GT = 1;
	public static final int LT = 2;
	public static final int GTE = 3;
	public static final int LTE = 4;
	public static final int LIKE = 5;
	public static final int MATCH = 6;
	
	private String field;
	private Object value;
	private Object from;
	private Object to;
	private int link;
	private int symbolOne;
	private int symbolTwo;
	
	private ParamModel(int link) {
		this.link = link;
	}
	
	//初始化为and
	public static ParamModel and() {
		return new ParamModel(ParamModel.AND);
	}
	
	//初始化为or
	public static ParamModel or() {
		return new ParamModel(ParamModel.OR);
	}
	
	//初始化为not
	public static ParamModel not() {
		return new ParamModel(ParamModel.NOT);
	}
	
	//设置字段
	public ParamModel field(String field) {
		this.field = field;
		return this;
	}
	
	//相等运算
	public ParamModel eq(Object v) {
		this.symbolOne = ParamModel.EQ;
		this.value = v;
		return this;
	}
	
	//大于运算
	public ParamModel gt(Object v) {
		this.symbolOne = ParamModel.GT;
		this.from = v;
		return this;
	}
	
	//大于等于
	public ParamModel gte(Object v) {
		this.symbolOne = ParamModel.GTE;
		this.from = v;
		return this;
	}
	
	//小于
	public ParamModel lt(Object v) {
		this.symbolTwo = ParamModel.LT;
		this.to = v;
		return this;
	}
	
	//小于等于
	public ParamModel lte(Object v) {
		this.symbolTwo = ParamModel.LTE;
		this.to = v;
		return this;
	}
	
	//like
	public ParamModel like(Object v) {
		this.symbolOne = ParamModel.LIKE;
		this.value = v;
		return this;
	}
	//match
	public ParamModel match(Object v) {
		this.symbolOne = ParamModel.MATCH;
		this.value = v;
		return this;
	}

	public String getField() {
		return field;
	}

	public Object getValue() {
		return value;
	}

	public Object getFrom() {
		return from;
	}

	public Object getTo() {
		return to;
	}

	public int getLink() {
		return link;
	}

	public int getSymbolOne() {
		return symbolOne;
	}

	public int getSymbolTwo() {
		return symbolTwo;
	}
}
