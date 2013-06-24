package org.ib.sso.sts.issue;

import java.net.URL;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.junit.Test;

public class IssueSymmetricTest {

	private static final org.apache.commons.logging.Log LOG = 
	        org.apache.commons.logging.LogFactory.getLog(IssueSymmetricTest.class);

	@Test
	public void issueSAML2PublicKeyTest() {
		SpringBusFactory bf = new SpringBusFactory();
		URL busFile = IssueTest.class.getResource("cxf-client-symmetric.xml");

		Bus bus = bf.createBus(busFile.toString());
		SpringBusFactory.setDefaultBus(bus);
		SpringBusFactory.setThreadDefaultBus(bus);
		
		try {
			SecurityToken token = Commons.requestSecurityToken(Commons.SAML2_TOKEN_TYPE, Commons.PUBLIC_KEY_KEYTYPE, bus, Commons.DEFAULT_ADDRESS, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
