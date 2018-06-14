/** ======================================
 * Beijing JN TASS Technology Co.,Ltd.
 * Date   ：2018年1月25日 下午5:37:22
 * author ：yzy
 * Version：0.1
 * =========Modification History==========
 * Date          Name        Description
 * 2018年1月25日    yzy        创建EsQueryUtil类
 */
package com.tass.bdp.component.elasticsearch.esModel;

import java.util.List;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;

public class EsQueryUtil {

	//通用查询
	public static String commonSearch(CommonModel commonModel) {
	    TransportClient client = EsTransportClientFactory.getTransportClient();
		
		SearchRequestBuilder srb = client.prepareSearch(BaseModel.ES_INDEX);
		srb.setTypes(BaseModel.ES_TYPE)
		.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		//srb.setQuery(QueryBuilders.matchAllQuery());
		
		QueryModel queryModel = commonModel.getQueryModel();
		//加入查询条件筛选
		//srb.setPostFilter(paramToQueryBuilder(queryModel.getFilterList()));
		srb.setQuery(paramToQueryBuilder(queryModel.getFilterList()));
		//设置分页条数
		srb.setFrom(queryModel.getFrom()).setSize(queryModel.getSize()).setExplain(true);
		//添加排序
		if(queryModel.getSortFiled() != null) {
			srb.addSort(queryModel.getSortFiled(), SortOrder.fromString(queryModel.getOrder()));
		}
		
		//最后总聚合
		AggregationBuilder aggBuilder = null;
		//分组聚合
		AggregationBuilder termsAggBuilder = null;
		//判断是否日期直方
        if(commonModel.getDateHistogramField() != null) {
            aggBuilder = AggregationBuilders
                    .dateHistogram("histogram_" + commonModel.getDateHistogramField())
                    .field(commonModel.getDateHistogramField());
            if(commonModel.getDateHistogramType() == CommonModel.HISTOGRAM_TYPE_MONTH) { //直方分组类型为月
                ((DateHistogramAggregationBuilder)aggBuilder).dateHistogramInterval(DateHistogramInterval.MONTH).format("yyyy-MM");
            }else if(commonModel.getDateHistogramType() == CommonModel.HISTOGRAM_TYPE_DAY) { //直方分组类型为天
                ((DateHistogramAggregationBuilder)aggBuilder).dateHistogramInterval(DateHistogramInterval.DAY).format("yy-MM-dd");
            }
        }
        //判断是否分组
        if(commonModel.getGroupField() != null) {
            termsAggBuilder = AggregationBuilders
                    .terms("terms_" + commonModel.getGroupField())
                    .field(commonModel.getGroupField())
                    .size(20000);
            if(aggBuilder == null) {
                aggBuilder = termsAggBuilder;
            }else {
                aggBuilder.subAggregation(termsAggBuilder);
            }
        }
		//获取聚合字段信息
		List<FieldModel> fieldList = commonModel.getFieldList();
		//添加聚合信息
		for(int i = 0; fieldList != null && i < fieldList.size(); i++) {
		    AggregationBuilder fieldAggBuilder = null;
		    FieldModel field = fieldList.get(i);
		    
		    if(field.getStatisticsType() == FieldModel.STATISTICS_TYPE_CARDINALITY) { //基数计算
		        fieldAggBuilder = AggregationBuilders
		                .cardinality("cardinality_" + field.getField())
		                .precisionThreshold(20000); //内存中允许使用的计数器个数
		        if(!field.isScript()) {
		            ((CardinalityAggregationBuilder)fieldAggBuilder).field(field.getField());
		        }else {
		            Script script = new Script(field.getField());
		            ((CardinalityAggregationBuilder)fieldAggBuilder).script(script);
		        }
		        //设置缺失值
		        if(field.getMissing() != null) {
		            ((CardinalityAggregationBuilder)fieldAggBuilder).missing(field.getMissing());
		        }
		    }else if(field.getStatisticsType() == FieldModel.STATISTICS_TYPE_SUM) { //求和计算
		        fieldAggBuilder = AggregationBuilders.sum("sum_" + field.getField());
		        if(!field.isScript()) {
		            ((SumAggregationBuilder)fieldAggBuilder).field(field.getField());
		        }else {
		            Script script = new Script(field.getField());
                    ((SumAggregationBuilder)fieldAggBuilder).script(script);
		        }
		        //设置缺失值
                if(field.getMissing() != null) {
                    ((SumAggregationBuilder)fieldAggBuilder).missing(field.getMissing());
                }
		    }else if(field.getStatisticsType() == FieldModel.STATISTICS_TYPE_AVG) { //平均数计算
		        fieldAggBuilder = AggregationBuilders.avg("avg_" + field.getField());
		        if(!field.isScript()) {
                    ((AvgAggregationBuilder)fieldAggBuilder).field(field.getField());
                }else {
                    Script script = new Script(field.getField());
                    ((AvgAggregationBuilder)fieldAggBuilder).script(script);
                }
		        //设置缺失值
                if(field.getMissing() != null) {
                    ((AvgAggregationBuilder)fieldAggBuilder).missing(field.getMissing());
                }
		    }else if(field.getStatisticsType() == FieldModel.STATISTICS_TYPE_MAX) { //最大值计算
		        fieldAggBuilder = AggregationBuilders.max("max_" + field.getField());
		        if(!field.isScript()) {
                    ((MaxAggregationBuilder)fieldAggBuilder).field(field.getField());
                }else {
                    Script script = new Script(field.getField());
                    ((MaxAggregationBuilder)fieldAggBuilder).script(script);
                }
		        //设置缺失值
                if(field.getMissing() != null) {
                    ((MaxAggregationBuilder)fieldAggBuilder).missing(field.getMissing());
                }
		    }else if(field.getStatisticsType() == FieldModel.STATISTICS_TYPE_MIN) { //最小值计算
		        fieldAggBuilder = AggregationBuilders.min("min_" + field.getField());
		        if(!field.isScript()) {
                    ((MinAggregationBuilder)fieldAggBuilder).field(field.getField());
                }else {
                    Script script = new Script(field.getField());
                    ((MinAggregationBuilder)fieldAggBuilder).script(script);
                }
		        //设置缺失值
                if(field.getMissing() != null) {
                    ((MinAggregationBuilder)fieldAggBuilder).missing(field.getMissing());
                }
		    }else if(field.getStatisticsType() == FieldModel.STATISTICS_TYPE_PERCENTILES) { //百分比计算
		        fieldAggBuilder = AggregationBuilders.percentiles("percentiles_" + field.getField());
		        if(!field.isScript()) {
                    ((PercentilesAggregationBuilder)fieldAggBuilder).field(field.getField());
                }else {
                    Script script = new Script(field.getField());
                    ((PercentilesAggregationBuilder)fieldAggBuilder).script(script);
                }
		        //设置缺失值
                if(field.getMissing() != null) {
                    ((PercentilesAggregationBuilder)fieldAggBuilder).missing(field.getMissing());
                }
		    }else if(field.getStatisticsType() == FieldModel.STATISTICS_TYPE_TERMS) { //分类统计
		        fieldAggBuilder = AggregationBuilders.terms("terms_" + field.getField());
		        if(!field.isScript()) {
		            ((TermsAggregationBuilder)fieldAggBuilder).field(field.getField());
		        }else {
		            Script script = new Script(field.getField());
		            ((TermsAggregationBuilder)fieldAggBuilder).script(script);
		        }
		    }
		    
		    //判断是否是过滤后再聚合
		    if(field.getFilter() != null) {
		        fieldAggBuilder = AggregationBuilders
                        .filter("filter" + i + "_" + field.getField(), paramToQueryBuilder(field.getFilter().getFilterList()))
                        .subAggregation(fieldAggBuilder);
            }
		    
		    //添加嵌套聚合
		    if(termsAggBuilder != null) { //存在分组聚合
		        termsAggBuilder.subAggregation(fieldAggBuilder);
		    }else if(aggBuilder == null) { //分组聚合为空，判断总聚合是否为空
		        srb.addAggregation(fieldAggBuilder);
		    }else {
		        aggBuilder.subAggregation(fieldAggBuilder);
		    }
		}
		//如果总聚合不为空 则添加到查询
		if(aggBuilder != null) {
		    srb.addAggregation(aggBuilder);
		}
		
		SearchResponse sr = srb.get();
		return sr.toString();
	}
	
	private static BoolQueryBuilder paramToQueryBuilder(List<ParamModel> paramList) {
	  //过滤条件查询
        BoolQueryBuilder postFilter = QueryBuilders.boolQuery();
        for(int i = 0; paramList != null && i < paramList.size(); i++) {
            ParamModel pm = paramList.get(i);
            QueryBuilder qb = null;
            if(pm.getSymbolOne() == ParamModel.EQ) { //等于计算
                qb = QueryBuilders.termQuery(pm.getField(), pm.getValue());
            }else if(pm.getSymbolOne() == ParamModel.GT) { //大于计算
                qb = QueryBuilders.rangeQuery(pm.getField()).gt(pm.getFrom());
            }else if(pm.getSymbolOne() == ParamModel.GTE) { //大于等于计算
                qb = QueryBuilders.rangeQuery(pm.getField()).gte(pm.getFrom());
            }else if(pm.getSymbolOne() == ParamModel.LIKE) { //like 计算（主要用于未分词的字段）
                qb = QueryBuilders.wildcardQuery(pm.getField(), "*"+pm.getValue()+"*");
            }else if(pm.getSymbolOne() == ParamModel.MATCH) { //match 计算（主要用于分词后的字段）
                qb = QueryBuilders.matchQuery(pm.getField(), pm.getValue());
            }
            if(pm.getSymbolTwo() == ParamModel.LT) { //小于计算
                if(qb == null) {
                    qb = QueryBuilders.rangeQuery(pm.getField()).lt(pm.getTo());
                }else {
                    ((RangeQueryBuilder)qb).lt(pm.getTo());
                }
            }else if(pm.getSymbolTwo() == ParamModel.LTE) { //小于等于计算
                if(qb == null) {
                    qb = QueryBuilders.rangeQuery(pm.getField()).lte(pm.getTo());
                }else {
                    ((RangeQueryBuilder)qb).lte(pm.getTo());
                }
            }
            //判断是and 或者是 or 或者是not
            if(pm.getLink() == ParamModel.AND) {
                postFilter.must(qb);
            }else if(pm.getLink() == ParamModel.OR) {
                postFilter.should(qb);
            }else if(pm.getLink() == ParamModel.NOT) {
                postFilter.mustNot(qb);
            }
        }
	    return postFilter;
	}
	
	//全文搜索
	public static String fullTextSearch(Object text, String... fields) {
		TransportClient client = EsTransportClientFactory.getTransportClient();
		
		SearchRequestBuilder srb = client.prepareSearch(BaseModel.ES_INDEX);
		srb.setTypes(BaseModel.ES_TYPE)
		.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		srb.setQuery(QueryBuilders.multiMatchQuery(text, fields));
		srb.setFrom(0).setSize(50).setExplain(true);
		
		SearchResponse sr = srb.get();
		return sr.toString();
	}
}
