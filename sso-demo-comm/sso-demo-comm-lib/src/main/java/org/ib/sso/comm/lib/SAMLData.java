package org.ib.sso.comm.lib;

import java.security.Principal;
import java.util.Set;

import javax.security.auth.Subject;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.interceptor.security.SAMLSecurityContext;
import org.apache.cxf.security.SecurityContext;
import org.ib.sso.utils.DOMUtils;

/**
 * Grants access to the SAML token received
 * @author Ivan Brencsics
 */
public class SAMLData {
	
	private SAMLSecurityContext ctx;
	
	public static SAMLData newInstance(WebServiceContext wsCtx) throws CommLibException {
		
		if (wsCtx==null)
			throw new CommLibException("WebServiceContext is null");
		
		MessageContext ctx = wsCtx.getMessageContext();
		SecurityContext secCtx = (SecurityContext)ctx.get(SecurityContext.class.getName());
		
		if (secCtx instanceof SAMLSecurityContext) {
        	SAMLSecurityContext samlCtx = (SAMLSecurityContext)secCtx;
        	
        	SAMLData samlData = new SAMLData();
        	samlData.setCtx(samlCtx);
        	
        	return samlData;
		}
		
		throw new CommLibException("No SAMLSecurityContext found");
	}

	public SAMLSecurityContext getCtx() {
		return ctx;
	}

	public void setCtx(SAMLSecurityContext ctx) {
		this.ctx = ctx;
	}
	
	
	public Principal getUserPrincipal() {
		return getCtx().getUserPrincipal();
	}
	
	public Set<Principal> getUserRoles() {
		return getCtx().getUserRoles();
	}
	
	public String getIssuer() {
		return getCtx().getIssuer();
	}
	
	public Subject getSubject() {
		return getCtx().getSubject();
	}
	
	public String getTokenAsString() {
		return DOMUtils.nodeToString(getCtx().getAssertionElement());
	}
	
	// add more getters that give back for instance the HoK certificate, signing certificate, ...
}
