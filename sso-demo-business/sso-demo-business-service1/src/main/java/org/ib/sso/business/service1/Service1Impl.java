package org.ib.sso.business.service1;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

import org.ib.sso.comm.lib.CommLibException;
import org.ib.sso.comm.lib.security.SAMLData;
import org.ib.sso.comm.lib.security.WebServiceContextTool;
import org.ib.sso.service.service1.Service1Endpoint;
import org.ib.sso.service.service1.TestOperationFault;
import org.ib.sso.xsd.TestFaultType;
import org.ib.sso.xsd.TestRequestType;
import org.ib.sso.xsd.TestResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Service1Impl implements Service1Endpoint {

	private static final Logger LOG = LoggerFactory.getLogger(Service1Impl.class.getPackage().getName());
	
	@Resource
    WebServiceContext context;

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
			
			response.getNode().add("Service1: Successfully called by user " + samlData.getUserPrincipal().getName());
			
		} catch (CommLibException e) {
			e.printStackTrace();
			
			TestFaultType testFaultType = new TestFaultType();
			testFaultType.setType("Business");
			testFaultType.setDescription(e.getMessage());
			throw new TestOperationFault(e.getMessage(), testFaultType);
		}
		
		return response;
	}

}
