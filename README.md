# elasticsearch-search-util
基于spring boot 
elasticsearch 6.1.2

Elasticsearch通用封装详细设计

一．封装类列表
 
1.EsTransportClientFactory为连接客户端工厂类字段，
方法：
I．getTransportClient方法 为单例模式获取连接客户端
II．closeClient方法 关闭客户端

2.EsQueryUtil 为主要查询工具类
	方法：
I．	commonSearch 通用查询 参数为封装的CommonModel
II．	fullTextSearch 全文搜索 参数第一个为查询内容Object 第二个参数是可变长String， 可传多个 表示要搜索的字段
3.esModel下为封装的查询条件model类
I．		BaseModel 为基本配置类 包括es的地址、端口、索引、类型等
II． 		ParamModel 为筛选参数类
III. 	QueryModel 为查询设置类
IV．	FieldModel 为要查询的字段类
V．		CommonModel 为通用查询类

二．查询类详解
	1.创建ParamModel使用and() or() not() 三种方法，这些方法会返回这个类一个新的对象，分别对应了三种不同的连接符，多个查询拼接的逻辑是and、or、还是not
然后通过field方法设置要查询的字段名称，调用eq方法代表是等于计算，参数为等值计算的值，gt为大于运算，gte为大于等于计算，lt为小于计算，lte为小于等于计算，like为包含计算，match为匹配计算
2.QueryModel为查询筛选类 使用时主要是创建了ParamModel的list后放入，其他参数size代表查询的条数，from为开始的条数 这俩参数配合可实现分页操作，sortField为排序字段，order为排序策略 （desc为降序，asc为升序）
3.FieldModel 为对应要查询的字段设置类，field成员变量为要统计的字段名，statisticType为统计类型字段（包括基数CARDINALITY、总和SUM、平均数AVG、最大值MAX、最小值MIN、百分比计算PERCENTILES），script为是否为脚本字段（此值为true的话，则field字段为计算脚本），missing为缺失值设置后对应这个值的数据不计入计算，filter为QueryModel类型的对象，可针对筛选后的结果对此字段计算时再进行一次数据筛选，然后再进行计算
三.查询示例
1.查询例子的数据为
name	age	birthday	class
宋江1	17	1992-02-15	1
宋江10	29	1990-03-21	3
宋江11	29	1990-03-21	3
宋江12	29	1990-03-21	3
宋江13	29	1990-03-21	3
宋江2	27	1994-02-15	1
宋江3	22	1992-02-15	1
宋江4	26	1991-03-21	1
宋江5	29	1990-03-21	1
宋江8	29	1990-03-21	2

2. 查询各年龄人数
QueryModel queryModel = new QueryModel();
        queryModel.setSize(0);
        
        CommonModel commonModel = new CommonModel();
        commonModel.setQueryModel(queryModel);
        //分组字段
        commonModel.setGroupField("age");
  
        String r = EsQueryUtil.commonSearch(commonModel);
        System.out.println("aggCommon:" + r);

返回结果：
{
  "took": 5,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": 10,
    "max_score": 0,
    "hits": []
  },
  "aggregations": {
    "term_age": {
      "doc_count_error_upper_bound": 0,
      "sum_other_doc_count": 0,
      "buckets": [
        {
          "key": 29,
          "doc_count": 6
        },
        {
          "key": 17,
          "doc_count": 1
        },
        {
          "key": 22,
          "doc_count": 1
        },
        {
          "key": 26,
          "doc_count": 1
        },
        {
          "key": 27,
          "doc_count": 1
        }
      ]
    }
  }
}

3. 利用脚本将年龄放大10倍的平均数
QueryModel queryModel = new QueryModel();
        queryModel.setSize(0);
        
        CommonModel commonModel = new CommonModel();
        commonModel.setQueryModel(queryModel);
        
        FieldModel fm = new FieldModel();
        fm.setField("doc['age'].value * 10");
        fm.setScript(true);
        fm.setStatisticsType(FieldModel.STATISTICS_TYPE_AVG);
        List<FieldModel> fmList = new ArrayList<>();
        fmList.add(fm);
        commonModel.setFieldList(fmList);
        
        String r = EsQueryUtil.commonSearch(commonModel);
        System.out.println("aggCommon:" + r);


返回结果：
{
	"took": 3,
	"timed_out": false,
	"_shards": {
		"total": 5,
		"successful": 5,
		"skipped": 0,
		"failed": 0
	},
	"_clusters": {
		"total": 0,
		"successful": 0,
		"skipped": 0
	},
	"hits": {
		"total": 10,
		"max_score": 0.0,
		"hits": []
	},
	"aggregations": {
		"avg_doc['age'].value * 10": {
			"value": 266.0
		}
	}
}

4.按着月的直方统计各个月的年龄平均数
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

返回数据：
{
	"took": 1,
	"timed_out": false,
	"_shards": {
		"total": 5,
		"successful": 5,
		"skipped": 0,
		"failed": 0
	},
	"_clusters": {
		"total": 0,
		"successful": 0,
		"skipped": 0
	},
	"hits": {
		"total": 10,
		"max_score": 0.0,
		"hits": []
	},
	"aggregations": {
		"histogram_birthday": {
			"buckets": [{
				"key_as_string": "1990-03",
				"key": 636249600000,
				"doc_count": 6,
				"avg_age": {
					"value": 29.0
				}
			}, {
				"key_as_string": "1990-04",
				"key": 638928000000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1990-05",
				"key": 641520000000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1990-06",
				"key": 644198400000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1990-07",
				"key": 646790400000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1990-08",
				"key": 649468800000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1990-09",
				"key": 652147200000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1990-10",
				"key": 654739200000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1990-11",
				"key": 657417600000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1990-12",
				"key": 660009600000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1991-01",
				"key": 662688000000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1991-02",
				"key": 665366400000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1991-03",
				"key": 667785600000,
				"doc_count": 1,
				"avg_age": {
					"value": 26.0
				}
			}, {
				"key_as_string": "1991-04",
				"key": 670464000000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1991-05",
				"key": 673056000000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1991-06",
				"key": 675734400000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1991-07",
				"key": 678326400000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1991-08",
				"key": 681004800000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1991-09",
				"key": 683683200000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1991-10",
				"key": 686275200000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1991-11",
				"key": 688953600000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1991-12",
				"key": 691545600000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1992-01",
				"key": 694224000000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1992-02",
				"key": 696902400000,
				"doc_count": 2,
				"avg_age": {
					"value": 19.5
				}
			}, {
				"key_as_string": "1992-03",
				"key": 699408000000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1992-04",
				"key": 702086400000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1992-05",
				"key": 704678400000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1992-06",
				"key": 707356800000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1992-07",
				"key": 709948800000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1992-08",
				"key": 712627200000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1992-09",
				"key": 715305600000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1992-10",
				"key": 717897600000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1992-11",
				"key": 720576000000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1992-12",
				"key": 723168000000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1993-01",
				"key": 725846400000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1993-02",
				"key": 728524800000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1993-03",
				"key": 730944000000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1993-04",
				"key": 733622400000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1993-05",
				"key": 736214400000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1993-06",
				"key": 738892800000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1993-07",
				"key": 741484800000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1993-08",
				"key": 744163200000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1993-09",
				"key": 746841600000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1993-10",
				"key": 749433600000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1993-11",
				"key": 752112000000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1993-12",
				"key": 754704000000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1994-01",
				"key": 757382400000,
				"doc_count": 0,
				"avg_age": {
					"value": null
				}
			}, {
				"key_as_string": "1994-02",
				"key": 760060800000,
				"doc_count": 1,
				"avg_age": {
					"value": 27.0
				}
			}]
		}
	}
}
