package org.ib.sso.comm.lib;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

public class UsernamePasswordCallbackHandler implements CallbackHandler {

	private Map<String, String> passwords;
	
	public void setPasswords(Map<String, String> passwords) {
        this.passwords = passwords;
    }

    public Map<String, String> getPasswords() {
        return passwords;
    }
	
	@Override
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {

		if (getPasswords() == null || getPasswords().size() == 0) {
            return;
        }
		
		for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) { // CXF
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];

                String pw = getPasswords().get(pc.getIdentifier());
                pc.setPassword(pw);
            }
        }
	}

}
