package org.ib.sso.business.service1;

import java.security.Principal;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.interceptor.security.SAMLSecurityContext;
import org.apache.cxf.security.SecurityContext;
import org.ib.sso.service.service1.Service1Endpoint;
import org.ib.sso.xsd.TestRequestType;
import org.ib.sso.xsd.TestResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import org.ib.sso.utils.DOMUtils;

public class Service1Impl implements Service1Endpoint {

	private static final Logger LOG = LoggerFactory.getLogger(Service1Impl.class.getPackage().getName());
	
	@Resource
    WebServiceContext context;

	public TestResponseType testOperation(TestRequestType request) {
		LOG.info("Service1.testOperation called");
		
		MessageContext ctx = context.getMessageContext();
        SecurityContext secCtx = (SecurityContext)ctx.get(SecurityContext.class.getName());
        if (secCtx instanceof SAMLSecurityContext) {
        	SAMLSecurityContext samlCtx = (SAMLSecurityContext)secCtx;
        	
        	Element token = samlCtx.getAssertionElement();
        	LOG.info(DOMUtils.nodeToString(token));
        }
        
        TestResponseType response = new TestResponseType();
        response.setMessageId(request.getMessageId());
        
        if (context == null) {
        	response.getNode().add("Service1: unknown user");
        } else {
            Principal p = context.getUserPrincipal();
            if (p == null) {
                response.getNode().add("Service1: principal is null");
            }
            response.getNode().add("Service1: called successfully by " + p.getName());
        }
        
		return response;
	}

}
