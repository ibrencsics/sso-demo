package org.ib.sso.service1;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.policy.PolicyOutInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebuggingInterceptor extends AbstractSoapInterceptor {

	private static final Logger LOG = LoggerFactory.getLogger(DebuggingInterceptor.class.getPackage().getName());
	
	public DebuggingInterceptor() {
		super(Phase.SETUP_ENDING);
		addAfter(PolicyOutInterceptor.class.getName());
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		
		for (Interceptor<? extends Message> interceptor : message.getInterceptorChain()) {
			LOG.debug("message interceptor: " + interceptor.getClass().getName());
			if (interceptor instanceof AbstractPhaseInterceptor)
				LOG.debug("  phase: " + ((AbstractPhaseInterceptor<? extends Message>)interceptor).getPhase());
		}
	}

}
