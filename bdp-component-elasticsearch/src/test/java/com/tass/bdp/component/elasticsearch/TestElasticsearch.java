/** ======================================
 * Beijing JN TASS Technology Co.,Ltd.
 * Date   ：2018年1月25日 下午5:37:22
 * author ：yzy
 * Version：0.1
 * =========Modification History==========
 * Date          Name        Description
 * 2018年1月25日    yzy        创建TestElasticsearch类
 */
package com.tass.bdp.component.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.tass.bdp.component.elasticsearch.esModel.CommonModel;
import com.tass.bdp.component.elasticsearch.esModel.EsQueryUtil;
import com.tass.bdp.component.elasticsearch.esModel.FieldModel;
import com.tass.bdp.component.elasticsearch.esModel.ParamModel;
import com.tass.bdp.component.elasticsearch.esModel.QueryModel;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestElasticsearch {

	//测试通用搜索
	@Test
	public void testCommonSearch() {
		QueryModel queryModel = new QueryModel();
		List<ParamModel> filterList = new ArrayList<>();
		filterList.add(ParamModel.and().field("age").eq(27));
		//filterList.add(ParamModel.and().field("title").eq("1234"));
		
		//queryModel.setSortFiled("_id");
		//queryModel.setOrder(QueryModel.ORDER_DESC);
		queryModel.setFilterList(filterList);
		
		CommonModel commonModel = new CommonModel();
		commonModel.setQueryModel(queryModel);
		String r = EsQueryUtil.commonSearch(commonModel);
		System.out.println("common:" + r);
	}
	
	//测试全文搜索
	@Test
	public void testFullTextSearch() {
		String r = EsQueryUtil.fullTextSearch("12", "name", "address");
		System.out.println("fullText:" + r);
	}
	
	//测试like
	@Test
	public void testCommonSearchLike() {
		QueryModel queryModel = new QueryModel();
		List<ParamModel> filterList = new ArrayList<>();
		filterList.add(ParamModel.and().field("name").like("小"));
		
		queryModel.setFilterList(filterList);
		CommonModel commonModel = new CommonModel();
        commonModel.setQueryModel(queryModel);
		String r = EsQueryUtil.commonSearch(commonModel);
		System.out.println("like:" + r);
	}
	
	//测试match
	@Test
	public void testCommonSearchMatch() {
		QueryModel queryModel = new QueryModel();
		List<ParamModel> filterList = new ArrayList<>();
		filterList.add(ParamModel.and().field("address").match("北京"));
		
		queryModel.setFilterList(filterList);
		CommonModel commonModel = new CommonModel();
        commonModel.setQueryModel(queryModel);
		String r = EsQueryUtil.commonSearch(commonModel);
		System.out.println("match:" + r);
	}
	
	@Test
	public void testAggCommonSearch() {
	    QueryModel queryModel = new QueryModel();
	    queryModel.setSize(0);
	    
	    CommonModel commonModel = new CommonModel();
	    commonModel.setQueryModel(queryModel);
	    
	    //对年龄求平均数
	    FieldModel fm = new FieldModel();
	    fm.setField("age");
	    fm.setStatisticsType(FieldModel.STATISTICS_TYPE_AVG);
	    List<FieldModel> fmList = new ArrayList<>();
	    fmList.add(fm);
	    commonModel.setFieldList(fmList);
	    //加入按生日 直方
	    commonModel.setDateHistogramField("birthday");
	    commonModel.setDateHistogramType(CommonModel.HISTOGRAM_TYPE_MONTH);
	    String r = EsQueryUtil.commonSearch(commonModel);
	    System.out.println("aggCommon:" + r);
	}
}
