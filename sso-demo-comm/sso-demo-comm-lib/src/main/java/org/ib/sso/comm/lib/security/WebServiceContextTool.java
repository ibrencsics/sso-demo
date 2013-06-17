package org.ib.sso.comm.lib.security;

import javax.security.auth.x500.X500Principal;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.security.SAMLSecurityContext;
import org.apache.cxf.security.SecurityContext;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.tokenstore.TokenStore;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.ib.sso.comm.lib.CommLibException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class WebServiceContextTool {

	private static final Logger LOG = LoggerFactory.getLogger(WebServiceContextTool.class.getPackage().getName());
	
	public static X509Data getX509Data(WebServiceContext wsCtx) throws CommLibException {
		
		if (wsCtx==null) 
			throw new CommLibException("WebServiceContext is null");
		
		X509Data x509Data = new X509Data();
		
		if (wsCtx.getUserPrincipal() instanceof X500Principal) {
			X500Principal x500Principal = (X500Principal) wsCtx.getUserPrincipal();
			x509Data.setPrincipal(x500Principal);
		}
		
		// this is not used now
		MessageContext ctx = wsCtx.getMessageContext();
		SecurityContext secCtx = (SecurityContext)ctx.get(SecurityContext.class.getName());
		
		if (secCtx!=null) {
			if (secCtx instanceof SAMLSecurityContext) {
	        	SAMLSecurityContext samlCtx = (SAMLSecurityContext)secCtx;
			}
			else if (secCtx instanceof WSS4JInInterceptor) {
				WSS4JInInterceptor wss4jCtx = (WSS4JInInterceptor) secCtx;
			}
			LOG.info("secCtx: " + secCtx.getClass());
		} else {
			LOG.info("secCtx is null");
		}
		
		return x509Data;
	}
	
	public static SAMLData getSamlData(Object clientProxy) throws CommLibException {
		
		if (clientProxy==null) 
			throw new CommLibException("Client proxy is null");
		
		SAMLData samlData = new SAMLData();
		
		try {
			Client client = ClientProxy.getClient(clientProxy);
			Endpoint ep = client.getEndpoint();
			
			String id = (String)ep.get(SecurityConstants.TOKEN_ID);
			
			TokenStore store = (TokenStore)ep.getEndpointInfo().getProperty(TokenStore.class.getName());
			SecurityToken tok = store.getToken(id);
			Element e = tok.getToken();
			
			samlData.setToken(e);
			
		} catch (Exception ex) {
			throw new CommLibException(ex);
		}
		
		return samlData;
	}
}