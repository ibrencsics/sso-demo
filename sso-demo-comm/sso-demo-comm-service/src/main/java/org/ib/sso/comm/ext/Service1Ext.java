package org.ib.sso.comm.ext;


import org.ib.sso.service.service1.Service1Endpoint;
import org.ib.sso.xsd.TestRequestType;
import org.ib.sso.xsd.TestResponseType;

public class Service1Ext implements Service1Endpoint {

	public TestResponseType testOperation(TestRequestType request) {
		System.out.println("Here we go");
		return null;
	}

}
