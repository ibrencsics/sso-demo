package org.ib.sso.service1;

import org.ib.sso.service.service1.Service1Endpoint;
import org.ib.sso.service.service1.TestOperationFault;
import org.ib.sso.xsd.TestRequestType;
import org.ib.sso.xsd.TestResponseType;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Service1DirectTest {

	private ApplicationContext ctx;
	
	public ApplicationContext getContext() {
		if (ctx==null) {
			ctx = new ClassPathXmlApplicationContext("classpath:org/ib/sso/service1/cxf-client.xml");
		}
        return ctx;
    }
	
//	@Test
	public void callTest() {
		
		Service1Endpoint service = (Service1Endpoint) getContext().getBean("Service1Client");
		TestRequestType request = new TestRequestType();
		request.setMessageId("1");
		TestResponseType response;
		try {
			response = service.testOperation(request);
			System.out.println("Received: " + response.getMessageId() + " / " + response.getNode());
		} catch (TestOperationFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
