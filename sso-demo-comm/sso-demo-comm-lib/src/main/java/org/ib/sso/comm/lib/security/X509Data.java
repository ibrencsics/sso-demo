package org.ib.sso.comm.lib.security;

import javax.security.auth.x500.X500Principal;

public class X509Data {
	
	private X500Principal principal;

	public X500Principal getPrincipal() {
		return principal;
	}

	public void setPrincipal(X500Principal principal) {
		this.principal = principal;
	}
	
	public String getPrincipalName() {
		return this.principal!=null ? this.principal.getName() : null;
	}
}
