/** ======================================
 * Beijing JN TASS Technology Co.,Ltd.
 * Date   ：2018年1月26日 上午9:39:10
 * author ：yzy
 * Version：0.1
 * =========Modification History==========
 * Date          Name        Description
 * 2018年1月26日    yzy        创建FieldModel类
 */
package com.tass.bdp.component.elasticsearch.esModel;

/**
 * 项目名称：bdp-component-elasticsearch </br> 
 * 类 名 称：FieldModel </br> 
 * 类 描 述：对每个统计字段的定义 </br> 
 * 创 建 人：yzy </br> 
 * 创建时间：2018年1月26日 上午9:39:10 </br> 
 * 修 改 人：yzy </br> 
 * 
 * @version：0.1 </br> 
 */
public class FieldModel {

    //统计类型-统计基数
    public static final int STATISTICS_TYPE_CARDINALITY = 1;
    //统计类型-统计总和
    public static final int STATISTICS_TYPE_SUM = 2;
    //统计类型-统计平均数
    public static final int STATISTICS_TYPE_AVG = 3;
    //统计类型-统计最大值
    public static final int STATISTICS_TYPE_MAX = 4;
    //统计类型-统计最小值
    public static final int STATISTICS_TYPE_MIN = 5;
    //统计类型-统计百分比
    public static final int STATISTICS_TYPE_PERCENTILES = 6;
    //统计类型-统计分类
    public static final int STATISTICS_TYPE_TERMS = 7;
    
    //字段
    private String field;
    //统计类型
    private int statisticsType;
    //是否是script
    private boolean script;
    //缺失值
    private Object missing;
    //嵌套filter
    private QueryModel filter;
    
    
    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }
    public int getStatisticsType() {
        return statisticsType;
    }
    public void setStatisticsType(int statisticsType) {
        this.statisticsType = statisticsType;
    }
    public boolean isScript() {
        return script;
    }
    public void setScript(boolean script) {
        this.script = script;
    }
    public Object getMissing() {
        return missing;
    }
    public void setMissing(Object missing) {
        this.missing = missing;
    }
    public QueryModel getFilter() {
        return filter;
    }
    public void setFilter(QueryModel filter) {
        this.filter = filter;
    }
}
