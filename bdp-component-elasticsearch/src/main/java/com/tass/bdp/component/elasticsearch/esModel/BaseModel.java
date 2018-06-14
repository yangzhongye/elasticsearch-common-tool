/** ======================================
 * Beijing JN TASS Technology Co.,Ltd.
 * Date   ：2018年1月25日 下午5:37:22
 * author ：yzy
 * Version：0.1
 * =========Modification History==========
 * Date          Name        Description
 * 2018年1月25日    yzy        创建BaseModel类
 */
package com.tass.bdp.component.elasticsearch.esModel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BaseModel {

	//elasticsearch 的地址
	public static String ES_ADDRESS = "";
	//elasticsearch 的端口
	public static int ES_PORT = 0;
	//elasticsearch 的index
	public static String ES_INDEX = "";
	//elasticsearch 的type
	public static String ES_TYPE = "";
	
	@Value("${es.address}")
    public void setES_ADDRESS(String eS_ADDRESS) {
        ES_ADDRESS = eS_ADDRESS;
    }
	@Value("${es.port}")
    public void setES_PORT(int eS_PORT) {
        ES_PORT = eS_PORT;
    }
	@Value("${es.index}")
    public void setES_INDEX(String eS_INDEX) {
        ES_INDEX = eS_INDEX;
    }
	@Value("${es.type}")
    public void setES_TYPE(String eS_TYPE) {
        ES_TYPE = eS_TYPE;
    }
}
