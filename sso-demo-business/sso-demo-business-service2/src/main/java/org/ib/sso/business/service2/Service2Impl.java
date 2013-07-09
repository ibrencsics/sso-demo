package org.ib.sso.business.service2;

import org.ib.sso.service.service2.Service2Endpoint;
import org.ib.sso.service.service2.TestOperationFault;
import org.ib.sso.xsd.TestRequestType;
import org.ib.sso.xsd.TestResponseType;

public class Service2Impl implements Service2Endpoint {

	public TestResponseType testOperation(TestRequestType request) throws TestOperationFault {
		
		TestResponseType response = new TestResponseType();
		response.setMessageId(request.getMessageId());
		
		response.getNode().add("Service2 >>> Called");
		
		return response;
	}

}
