/** ======================================
 * Beijing JN TASS Technology Co.,Ltd.
 * Date   ：2018年1月25日 下午5:37:22
 * author ：yzy
 * Version：0.1
 * =========Modification History==========
 * Date          Name        Description
 * 2018年1月25日    yzy        创建ElasticsearchComponent类
 */
package com.tass.bdp.component.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages ="com.tass.bdp.mvc,com.tass.bdp.mybatis,com.tass.bdp.component.elasticsearch")
public class ElasticsearchComponent {

	public static void main(String[] args) {
		SpringApplication.run(ElasticsearchComponent.class, args);
	}

}
