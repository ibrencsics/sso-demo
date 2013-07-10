package org.ib.sso.comm.in;

import org.ib.sso.service.service2.Service2Endpoint;
import org.ib.sso.service.service2.TestOperationFault;
import org.ib.sso.xsd.TestRequestType;
import org.ib.sso.xsd.TestResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class Service2Int implements Service2Endpoint, ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(Service2Int.class.getPackage().getName());
	
	private ApplicationContext appCtx;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.appCtx = applicationContext;
	}
	
	private final boolean stopHere = true;
	
	public TestResponseType testOperation(TestRequestType request)	throws TestOperationFault {

		LOG.debug("COMM >>> Service2Int.testOperation() called");
		
		TestResponseType response = new TestResponseType();
		response.setMessageId(request.getMessageId());
		
		response.getNode().add("COMM >>> Internal Service2 called");
		
		if (stopHere) {
			return response;
		}
		
		
		// call Service2
		
		response.getNode().add("COMM >>> Call Service2");
		
		Service2Endpoint service2Client = (Service2Endpoint) appCtx.getBean("service2Client");
		TestResponseType service2Response = service2Client.testOperation(request);
		
		response.getNode().addAll(service2Response.getNode());
		
		return response;
	}

}
