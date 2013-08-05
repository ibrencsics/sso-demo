package org.ib.sso.service1;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPConduit;
import org.ib.sso.service.service1.Service1Endpoint;
import org.ib.sso.service.service1.TestOperationFault;
import org.ib.sso.xsd.TestRequestType;
import org.ib.sso.xsd.TestResponseType;
import org.junit.Test;

public class Service1ExtTest {

	private static final Logger LOG = LoggerFactory.getLogger(Service1ExtTest.class.getPackage().getName());
	
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
		TestResponseType response;
		try {
//			logInterceptors(service);
			response = service.testOperation(request);
//			LOG.info("Received: " + response.getMessageId() + " / " + response.getNode());
			displayResponse(response);
		} catch (TestOperationFault e) {
			LOG.error(e.getMessage());
			LOG.error(e.getFaultInfo().getType() + " / " + e.getFaultInfo().getDescription());
		}
	}
	
	@Test
	public void callAsymmetricTest() {
		
		Service1Endpoint service = (Service1Endpoint) getContext().getBean("Service1ExtAsymmetricClient");
		TestRequestType request = new TestRequestType();
		request.setMessageId("13");
		TestResponseType response;
		try {
//			logInterceptors(service);
			response = service.testOperation(request);
//			LOG.info("Received: " + response.getMessageId() + " / " + response.getNode());
			displayResponse(response);
		} catch (TestOperationFault e) {
			e.printStackTrace();
		}
	}
	
	private void displayResponse(TestResponseType response) {
		LOG.info("Received message ID: " + response.getMessageId());
		for (String message : response.getNode()) {
			LOG.info("  - " + message);
		}
	}
	
	private void logInterceptors(Object clientProxy) {
		Client client = ClientProxy.getClient(clientProxy);
		client.getOutInterceptors().add(new DebuggingInterceptor());
		
		for (Interceptor<? extends Message> outInterceptor : client.getOutInterceptors()) {
			LOG.info("client out interceptor: " + outInterceptor.getClass().getName());
		}
		
		for (Interceptor<? extends Message> outInterceptor : client.getEndpoint().getOutInterceptors()) {
			LOG.info("endpoint out interceptor: " + outInterceptor.getClass().getName());
		}
		
		for (Interceptor<? extends Message> outInterceptor : client.getBus().getOutInterceptors()) {
			LOG.info("bus out interceptor: " + outInterceptor.getClass().getName());
		}
	}
}
