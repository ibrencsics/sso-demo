package org.ib.sso.comm.lib.security;

import java.util.Map;

import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.tokenstore.TokenStore;
import org.apache.ws.security.util.DOM2Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class TestTools {
	
	private static final Logger LOG = LoggerFactory.getLogger(TestTools.class.getPackage().getName());

	public static void dumpMsgCtx(MessageContext msgCtx) {
		System.out.println("msgCty: " + msgCtx.keySet());
		for (String key : msgCtx.keySet()) {
			System.out.println("key: " + key + " - value: " + msgCtx.get(key));
		}
	}
	
	public static void dumpClient(Client client) {
		for (Interceptor<?> interceptor : client.getOutInterceptors()) {
			LOG.info("client out interceptor: " + interceptor);
		}
	}
	
	public static void dumpEndpoint(Endpoint endpoint) {
		
		for (Map.Entry<String, Object> entry : endpoint.entrySet()) {
			LOG.info("endpoint key=" + entry.getKey() + "; value=" + entry.getValue());
		}
		
		for (Interceptor<?> interceptor : endpoint.getOutInterceptors()) {
			LOG.info("ep out interceptor: " + interceptor);
		}
		
		EndpointInfo epinfo = endpoint.getEndpointInfo();
		for (Map.Entry<String, Object> entry : epinfo.getProperties().entrySet()) {
			LOG.info("ep info key=" + entry.getKey() + "; value=" + entry.getValue());
		}
	}
	
	public static void dumpStoredTokens(Endpoint endpoint) {
		TokenStore store = (TokenStore)endpoint.getEndpointInfo().getProperty(TokenStore.class.getName());
		
		for (String tokenId : store.getTokenIdentifiers()) {
			LOG.info("token from store id=" + tokenId);
			
			SecurityToken tok = store.getToken(tokenId);
			Element e = tok.getToken();
	
			LOG.info("******************** TOKEN ********************");
			LOG.info(DOM2Writer.nodeToString(e));
			LOG.info("******************** TOKEN ********************");
		}
	}
}
