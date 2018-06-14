/** ======================================
 * Beijing JN TASS Technology Co.,Ltd.
 * Date   ：2018年1月25日 下午5:37:22
 * author ：yzy
 * Version：0.1
 * =========Modification History==========
 * Date          Name        Description
 * 2018年1月25日    yzy        创建EsTransportClientFactory类
 */
package com.tass.bdp.component.elasticsearch.esModel;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class EsTransportClientFactory {

	private static TransportClient client = null;
	/**
	 * 获取es的客户端实例
	 * @return
	 */
	@SuppressWarnings("resource")
	public static TransportClient getTransportClient() {
		if(client != null) {
			return client;
		}
		synchronized (TransportClient.class) {
			if(client != null) {
				return client;
			}
			try {
				client = new PreBuiltTransportClient(Settings.EMPTY)
						.addTransportAddress(new TransportAddress(InetAddress.getByName(BaseModel.ES_ADDRESS), BaseModel.ES_PORT));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return client;
		}
	}
	
	public static void closeClient() {
		if(client != null) {
			client.close();
			client = null;
		}
	}
}
