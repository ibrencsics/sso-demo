package org.ib.sso.comm.lib.security;

import java.security.Principal;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.ws.security.util.DOM2Writer;
import org.w3c.dom.Element;

public class SAMLData {

	private Element token;
	
	private Principal userPrincipal;
	private Set<Principal> userRoles;
	private String issuer;
	private Subject subject;

	public Element getToken() {
		return token;
	}

	public void setToken(Element token) {
		this.token = token;
	}
	
	public Principal getUserPrincipal() {
		return userPrincipal;
	}

	public void setUserPrincipal(Principal userPrincipal) {
		this.userPrincipal = userPrincipal;
	}

	public Set<Principal> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(Set<Principal> userRoles) {
		this.userRoles = userRoles;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public String getTokenAsString() {
		if (getToken()==null) return null;
		
		return DOM2Writer.nodeToString(getToken());
	}
}
