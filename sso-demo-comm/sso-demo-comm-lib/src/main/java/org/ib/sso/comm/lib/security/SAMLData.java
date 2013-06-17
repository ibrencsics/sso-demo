package org.ib.sso.comm.lib.security;

import org.apache.ws.security.util.DOM2Writer;
import org.w3c.dom.Element;

public class SAMLData {

	private Element token;

	public Element getToken() {
		return token;
	}

	public void setToken(Element token) {
		this.token = token;
	}
	
	public String getTokenAsString() {
		if (getToken()==null) return null;
		
		return DOM2Writer.nodeToString(getToken());
	}
}
