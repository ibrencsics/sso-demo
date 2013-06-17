package org.ib.sso.comm.ext;


import javax.annotation.Resource;
import javax.security.auth.callback.CallbackHandler;
import javax.xml.ws.WebServiceContext;

import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.trust.STSClient;
import org.ib.sso.comm.lib.CommLibException;
import org.ib.sso.comm.lib.security.SAMLData;
import org.ib.sso.comm.lib.security.WebServiceContextTool;
import org.ib.sso.comm.lib.security.X509Data;
import org.ib.sso.service.service1.Service1Endpoint;
import org.ib.sso.xsd.TestRequestType;
import org.ib.sso.xsd.TestResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class Service1Ext implements Service1Endpoint, ApplicationContextAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(Service1Ext.class.getPackage().getName());

	@Resource
    WebServiceContext wsCtx;
	
	private ApplicationContext appCtx;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.appCtx = applicationContext;
	}

	private final boolean stopHere = false;
	private final boolean auto = true;
	
	public TestResponseType testOperation(TestRequestType request) {
		
		TestResponseType response=null;
		
		X509Data x509Data=null;
		try {
			x509Data = WebServiceContextTool.getX509Data(wsCtx);
		} catch (CommLibException ex) {
			ex.printStackTrace();
			// TODO: throw web service exception
		}
		
		String callerDN = x509Data.getPrincipalName();
		
		STSClient stsClient = (STSClient) appCtx.getBean("stsClient");
		stsClient.getProperties().put("ws-security.username", DNParser.getCN(callerDN));
		
		Service1Endpoint service1=null;
		
		if (auto) {
			service1 = (Service1Endpoint) appCtx.getBean("Service1Client");
		}
		else {
			service1 = (Service1Endpoint) appCtx.getBean("Service1ManualClient");
			
			SamlTokenCallbackHandler cb = (SamlTokenCallbackHandler) appCtx.getBean("samlTokenCallbackHandler");
			try {
				SecurityToken token = stsClient.requestSecurityToken();
				cb.setToken(token.getToken());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (!stopHere) {
		
			response = service1.testOperation(request);
	
			try {
				SAMLData samlData = WebServiceContextTool.getSamlData(service1);
		
				System.out.println("******************** TOKEN ********************");
				System.out.println(samlData.getTokenAsString());
				System.out.println("******************** TOKEN ********************");
			} catch (CommLibException ex) {
				ex.printStackTrace();
				// TODO: throw web service exception
			}
		}
		else {
			response.setMessageId("23");
			response.getNode().add(DNParser.getCN(wsCtx.getUserPrincipal().getName()));
		}
			
		return response;
	}
}
