package org.ib.sso.service1;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.ib.sso.service.service1.Service1Endpoint;
import org.ib.sso.xsd.TestRequestType;
import org.ib.sso.xsd.TestResponseType;
import org.junit.Test;

public class Service1ExtTest {

	private ApplicationContext ctx;
	
	public ApplicationContext getContext() {
		if (ctx==null) {
			ctx = new ClassPathXmlApplicationContext("classpath:org/ib/sso/service1/Service1ExtTestClient.xml");
		}
        return ctx;
    }
	
	@Test
	public void callTransportTest() {
		
		Service1Endpoint service = (Service1Endpoint) getContext().getBean("Service1ExtTransportClient");
		TestRequestType request = new TestRequestType();
		request.setMessageId("12");
		TestResponseType response = service.testOperation(request);
		System.out.println("Received: " + response.getMessageId() + " / " + response.getNode());
	}
	
	@Test
	public void callAsymmetricTest() {
		
		Service1Endpoint service = (Service1Endpoint) getContext().getBean("Service1ExtAsymmetricClient");
		TestRequestType request = new TestRequestType();
		request.setMessageId("13");
		TestResponseType response = service.testOperation(request);
		System.out.println("Received: " + response.getMessageId() + " / " + response.getNode());
	}
}
