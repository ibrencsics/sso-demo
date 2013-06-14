package org.ib.sso.comm.ext;

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
                if ("commkey".equals(pc.getIdentifier())) {
                    pc.setPassword("commpass");
                    break;
                } else if ("Libri".equals(pc.getIdentifier())) {
                    pc.setPassword("dummypass");
                    break;
                } else if ("Libri2".equals(pc.getIdentifier())) {
                    pc.setPassword("dummypass");
                    break;
                } else if ("BKV".equals(pc.getIdentifier())) {
                    pc.setPassword("dummypass");
                    break;
                } else if ("www.client.com".equals(pc.getIdentifier())) {
                    pc.setPassword("dummypass");
                    break;
                }
            }
        }
    }
}