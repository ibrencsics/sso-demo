package org.ib.sso.comm.lib;

import org.ib.sso.xsd.TestRequestType;
import org.ib.sso.xsd.TestResponseType;

public class MessageProcessor {

	public static TestResponseType createResponse(TestRequestType request) {
		
		TestResponseType response = new TestResponseType();
        response.setMessageId(request.getMessageId());
        
//        response.getNode().addAll(request.)
        
		return response;
	}
}
