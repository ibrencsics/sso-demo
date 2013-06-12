package org.ib.sso.business.service1;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

import org.ib.sso.comm.lib.CommLibException;
import org.ib.sso.comm.lib.SAMLData;
import org.ib.sso.service.service1.Service1Endpoint;
import org.ib.sso.xsd.TestRequestType;
import org.ib.sso.xsd.TestResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Service1Impl implements Service1Endpoint {

	private static final Logger LOG = LoggerFactory.getLogger(Service1Impl.class.getPackage().getName());
	
	@Resource
    WebServiceContext context;

	public TestResponseType testOperation(TestRequestType request) {
		LOG.info("Service1.testOperation called");
		
		TestResponseType response = new TestResponseType();
        response.setMessageId(request.getMessageId());
		
		try {
			SAMLData samlData = SAMLData.newInstance(context);
			
			LOG.info("Principal: " + samlData.getUserPrincipal().getName());
			LOG.info("Roles: " + samlData.getUserRoles());
			LOG.info("Issuer: " + samlData.getIssuer());
			LOG.info("Subject: " + samlData.getSubject());
			LOG.info("Token: ");
			LOG.info(samlData.getTokenAsString());
			
			response.getNode().add("Service1: Successfully called by user " + samlData.getUserPrincipal().getName());
			
		} catch (CommLibException e) {
			e.printStackTrace();
			response.getNode().add("Service1: " + e.getMessage());
		}
		
		return response;
	}

}
