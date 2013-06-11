package org.ib.sso.comm.sts.service1;

import org.apache.hello_world_soap_http.Greeter;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Service1Test {

	private ApplicationContext ctx;
	
	
	public ApplicationContext getContext() {
		if (ctx==null) {
			ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		}
        return ctx;
    }
	
	@Test
	public void callTest() {
		Greeter service = (Greeter) getContext().getBean("HelloServiceClient");
		String reply = service.greetMe();
		System.out.println("Received: " + reply);
	}
}
