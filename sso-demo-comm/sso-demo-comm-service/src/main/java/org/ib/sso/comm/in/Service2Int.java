package org.ib.sso.comm.in;

import org.ib.sso.comm.lib.MessageProcessor;
import org.ib.sso.service.service2.Service2Endpoint;
import org.ib.sso.service.service2.TestOperationFault;
import org.ib.sso.xsd.TestRequestType;
import org.ib.sso.xsd.TestResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service2Int implements Service2Endpoint {

	private static final Logger LOG = LoggerFactory.getLogger(Service2Int.class.getPackage().getName());
	
	public TestResponseType testOperation(TestRequestType request)	throws TestOperationFault {

		LOG.debug("COMM >>> Service2Int.testOperation() called");
		
		TestResponseType response = MessageProcessor.createResponse(request);
		
		return null;
	}

}
