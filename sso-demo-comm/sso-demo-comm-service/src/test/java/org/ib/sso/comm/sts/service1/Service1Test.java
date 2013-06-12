package org.ib.sso.comm.sts.service1;

import org.apache.hello_world_soap_http.Greeter;
import org.ib.sso.service.service1.Service1Endpoint;
import org.ib.sso.xsd.TestRequestType;
import org.ib.sso.xsd.TestResponseType;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Service1Test {

	private ApplicationContext ctx;
	
	
	public ApplicationContext getContext() {
		if (ctx==null) {
			ctx = new ClassPathXmlApplicationContext("classpath:org/ib/sso/comm/sts/service1/applicationContext.xml");
		}
        return ctx;
    }
	
	@Test
	public void callTest() {
//		Greeter service = (Greeter) getContext().getBean("HelloServiceClient");
//		String reply = service.greetMe();
//		System.out.println("Received: " + reply);
		
		Service1Endpoint service = (Service1Endpoint) getContext().getBean("Service1Client");
		TestRequestType request = new TestRequestType();
		request.setMessageId("1");
		TestResponseType response = service.testOperation(request);
		System.out.println("Received: " + response.getMessageId() + " / " + response.getNode());
	}
}
