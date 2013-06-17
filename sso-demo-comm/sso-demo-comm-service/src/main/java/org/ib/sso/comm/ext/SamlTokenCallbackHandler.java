package org.ib.sso.comm.ext;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.saml.ext.SAMLCallback;
import org.w3c.dom.Element;

public class SamlTokenCallbackHandler implements CallbackHandler {

	private Element token;
	
	public Element getToken() {
		return token;
	}

	public void setToken(Element token) {
		this.token = token;
	}


	@Override
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		
		if (getToken()==null)
			return;
		
		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof SAMLCallback) {
				SAMLCallback cb = (SAMLCallback) callbacks[i];
				cb.setAssertionElement(getToken());
			}
		}
	}

}
