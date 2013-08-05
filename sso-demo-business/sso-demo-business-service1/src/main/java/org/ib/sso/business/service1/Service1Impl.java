package org.ib.sso.business.service1;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

import org.ib.sso.comm.lib.CommLibException;
import org.ib.sso.comm.lib.SamlTokenCallbackHandler;
import org.ib.sso.comm.lib.security.SAMLData;
import org.ib.sso.comm.lib.security.WebServiceContextTool;
import org.ib.sso.service.service1.Service1Endpoint;
import org.ib.sso.service.service1.TestOperationFault;
import org.ib.sso.service.service2.Service2Endpoint;
import org.ib.sso.xsd.TestFaultType;
import org.ib.sso.xsd.TestRequestType;
import org.ib.sso.xsd.TestResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class Service1Impl implements Service1Endpoint, ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(Service1Impl.class.getPackage().getName());
	
	@Resource
    WebServiceContext context;
	
	private ApplicationContext appCtx;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.appCtx = applicationContext;
	}

	private final boolean stopHere = false;
	
	public TestResponseType testOperation(TestRequestType request) throws TestOperationFault {
		LOG.debug("Service1.testOperation called");
		
		TestResponseType response = new TestResponseType();
        response.setMessageId(request.getMessageId());
		
		try {
			SAMLData samlData = WebServiceContextTool.getSamlData(context);
			
			LOG.debug("Principal: " + samlData.getUserPrincipal().getName());
			LOG.debug("Roles: " + samlData.getUserRoles());
			LOG.debug("Issuer: " + samlData.getIssuer());
			LOG.debug("Subject: " + samlData.getSubject());
			LOG.debug("Token: ");
			LOG.debug(samlData.getTokenAsString());
			
			response.getNode().add("Service1 >>> Called by user " + samlData.getUserPrincipal().getName());
			
			if (stopHere) {
				return response;
			}
			
			
			// set token
			
			SamlTokenCallbackHandler cb = (SamlTokenCallbackHandler) appCtx.getBean("samlTokenCallbackHandler");
			cb.setToken(samlData.getToken());
			
			// call Service2
			
			response.getNode().add("Service1 >>> Calling Service2");
			Service2Endpoint service2Client = (Service2Endpoint) appCtx.getBean("service2IntClient");
			TestResponseType service2Response = service2Client.testOperation(request);
			response.getNode().addAll(service2Response.getNode());
			
		} catch (CommLibException e) {
			e.printStackTrace();
			
			TestFaultType testFaultType = new TestFaultType();
			testFaultType.setType("Business");
			testFaultType.setDescription(e.getMessage());
			throw new TestOperationFault(e.getMessage(), testFaultType);
			
		} catch (org.ib.sso.service.service2.TestOperationFault e) {
			e.printStackTrace();
			
			TestFaultType testFaultType = new TestFaultType();
			testFaultType.setType("Business");
			testFaultType.setDescription(e.getMessage());
			throw new TestOperationFault(e.getMessage(), testFaultType);
		}
		
		return response;
	}
}
