package org.ib.sso.business.service1;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.ws.security.WSPasswordCallback;

public class CommonCallbackHandler implements CallbackHandler {

    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) { // CXF
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
                if ("service1key".equals(pc.getIdentifier())) {
                    pc.setPassword("service1pass");
                    break;
                } 
            }
        }
    }
}