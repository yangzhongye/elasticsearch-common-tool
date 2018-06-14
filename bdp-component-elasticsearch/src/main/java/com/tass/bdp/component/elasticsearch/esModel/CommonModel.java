/** ======================================
 * Beijing JN TASS Technology Co.,Ltd.
 * Date   ：2018年1月25日 下午5:37:22
 * author ：yzy
 * Version：0.1
 * =========Modification History==========
 * Date          Name        Description
 * 2018年1月25日    yzy        创建CommonModel类
 */
package com.tass.bdp.component.elasticsearch.esModel;

import java.util.List;

/**
 * 项目名称：bdp-component-elasticsearch </br> 
 * 类 名 称：CommonModel </br> 
 * 类 描 述：通用的elasticsearch查询使用 </br> 
 * 创 建 人：yzy </br> 
 * 创建时间：2018年1月25日 下午5:37:22 </br> 
 * @version：0.1 </br> 
 */
public class CommonModel {
    
    //直方类型-天
    public static final int HISTOGRAM_TYPE_DAY = 1;
    //直方类型-月
    public static final int HISTOGRAM_TYPE_MONTH = 2;
    

    //高级查询条件
    private QueryModel queryModel;
    //分组字段
    private String groupField;
    //日期直方字段
    private String dateHistogramField;
    //日期直方类型
    private int dateHistogramType;
    //字段值列表
    private List<FieldModel> fieldList;
    
    
    public QueryModel getQueryModel() {
        return queryModel;
    }
    public void setQueryModel(QueryModel queryModel) {
        this.queryModel = queryModel;
    }
    public String getGroupField() {
        return groupField;
    }
    public void setGroupField(String groupField) {
        this.groupField = groupField;
    }
    public String getDateHistogramField() {
        return dateHistogramField;
    }
    public void setDateHistogramField(String dateHistogramField) {
        this.dateHistogramField = dateHistogramField;
    }
    public int getDateHistogramType() {
        return dateHistogramType;
    }
    public void setDateHistogramType(int dateHistogramType) {
        this.dateHistogramType = dateHistogramType;
    }
    public List<FieldModel> getFieldList() {
        return fieldList;
    }
    public void setFieldList(List<FieldModel> fieldList) {
        this.fieldList = fieldList;
    }
}
