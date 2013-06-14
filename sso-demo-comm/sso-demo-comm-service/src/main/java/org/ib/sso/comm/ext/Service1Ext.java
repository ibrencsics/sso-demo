package org.ib.sso.comm.ext;


import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.security.transport.TLSSessionInfo;
import org.apache.cxf.ws.security.trust.STSClient;
import org.ib.sso.service.service1.Service1Endpoint;
import org.ib.sso.xsd.TestRequestType;
import org.ib.sso.xsd.TestResponseType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class Service1Ext implements Service1Endpoint, ApplicationContextAware {

	@Resource
    WebServiceContext wsCtx;
	
	private ApplicationContext appCtx;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.appCtx = applicationContext;
	}

	
	public TestResponseType testOperation(TestRequestType request) {
		
		System.out.println("Called");
		
		if (wsCtx.getUserPrincipal()!=null)
			System.out.println("Cert DN: " + wsCtx.getUserPrincipal().getName());
		
		MessageContext msgCtx = wsCtx.getMessageContext();
//		System.out.println("msgCty: " + msgCtx.keySet());
//		for (String key : msgCtx.keySet()) {
//			System.out.println("key: " + key + " - value: " + msgCtx.get(key));
//		}
		
		
		STSClient stsClient = (STSClient) appCtx.getBean("stsClient");
		stsClient.getProperties().put("ws-security.username", DNParser.getCN(wsCtx.getUserPrincipal().getName()));
		
		Service1Endpoint service1 = (Service1Endpoint) appCtx.getBean("Service1Client");
		TestResponseType response = service1.testOperation(request);
		return response;
			
//		TestResponseType response = new TestResponseType();
//		response.setMessageId("23");
//		response.getNode().add(DNParser.getCN(wsCtx.getUserPrincipal().getName()));
//		return response;
	}
}
