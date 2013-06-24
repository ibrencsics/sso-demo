package org.ib.sso.sts.issue;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.cxf.ws.security.trust.claims.ClaimsCallback;
import org.apache.ws.security.WSDocInfo;
import org.apache.ws.security.WSSConfig;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.processor.Processor;
import org.apache.ws.security.processor.SAMLTokenProcessor;
import org.apache.ws.security.saml.SAMLKeyInfo;
import org.apache.ws.security.saml.ext.AssertionWrapper;
import org.apache.ws.security.saml.ext.OpenSAMLUtil;
import org.apache.ws.security.util.DOM2Writer;
import org.ib.sso.comm.lib.UsernamePasswordCallbackHandler;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class IssueTest {
	
	private static final org.apache.commons.logging.Log LOG = 
	        org.apache.commons.logging.LogFactory.getLog(IssueTest.class);

//	private static final String SAML1_TOKEN_TYPE = 
//			"http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV1.1";
//	private static final String SAML2_TOKEN_TYPE = 
//			"http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0";
//	private static final String SYMMETRIC_KEY_KEYTYPE = 
//			"http://docs.oasis-open.org/ws-sx/ws-trust/200512/SymmetricKey";
//	private static final String PUBLIC_KEY_KEYTYPE = 
//			"http://docs.oasis-open.org/ws-sx/ws-trust/200512/PublicKey";
//	private static final String BEARER_KEYTYPE = 
//			"http://docs.oasis-open.org/ws-sx/ws-trust/200512/Bearer";
//	private static final String DEFAULT_ADDRESS = 
//			"https://localhost:8081/doubleit/services/doubleittransportsaml1";


//	@Test
	public void issueSAML1BearerTest() {

		SpringBusFactory bf = new SpringBusFactory();
		URL busFile = IssueTest.class.getResource("cxf-client.xml");

		Bus bus = bf.createBus(busFile.toString());
		SpringBusFactory.setDefaultBus(bus);
		SpringBusFactory.setThreadDefaultBus(bus);
		
		try {
			SecurityToken token = Commons.requestSecurityToken(Commons.SAML1_TOKEN_TYPE, Commons.BEARER_KEYTYPE, bus, Commons.DEFAULT_ADDRESS, true);
			System.out.println(Commons.docToString(token.getToken()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@Test
	public void issueSAML2BearerTest() {
		SpringBusFactory bf = new SpringBusFactory();
		URL busFile = IssueTest.class.getResource("cxf-client.xml");

		Bus bus = bf.createBus(busFile.toString());
		SpringBusFactory.setDefaultBus(bus);
		SpringBusFactory.setThreadDefaultBus(bus);
		
		try {
			SecurityToken token = Commons.requestSecurityToken(Commons.SAML2_TOKEN_TYPE, Commons.BEARER_KEYTYPE, bus, Commons.DEFAULT_ADDRESS, true);
			System.out.println(Commons.docToString(token.getToken()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void issueSAML2PublicKeyTest() {
		SpringBusFactory bf = new SpringBusFactory();
		URL busFile = IssueTest.class.getResource("cxf-client.xml");

		Bus bus = bf.createBus(busFile.toString());
		SpringBusFactory.setDefaultBus(bus);
		SpringBusFactory.setThreadDefaultBus(bus);
		
		try {
			SecurityToken token = Commons.requestSecurityToken(Commons.SAML2_TOKEN_TYPE, Commons.PUBLIC_KEY_KEYTYPE, bus, Commons.DEFAULT_ADDRESS, true);
			
			List<WSSecurityEngineResult> results = Commons.processToken(token);
			
			assertTrue(results != null && results.size() == 1);
	        AssertionWrapper assertion = (AssertionWrapper)results.get(0).get(WSSecurityEngineResult.TAG_SAML_ASSERTION);
	        assertTrue(assertion != null);
	        assertTrue(assertion.getSaml1() == null && assertion.getSaml2() != null);
	        assertTrue(assertion.isSigned());
	        
	        List<String> methods = assertion.getConfirmationMethods();
	        String confirmMethod = null;
	        if (methods != null && methods.size() > 0) {
	            confirmMethod = methods.get(0);
	        }
	        assertTrue(OpenSAMLUtil.isMethodHolderOfKey(confirmMethod));
	        SAMLKeyInfo subjectKeyInfo = assertion.getSubjectKeyInfo();
	        assertTrue(subjectKeyInfo.getCerts() != null);
			 
	        assertEquals(assertion.getSubjectKeyInfo().getCerts()[0].getSubjectDN().getName(), "CN=www.comm.com, OU=Comm, O=Comm, ST=Budapest, C=HU");
	        assertEquals(assertion.getSignatureKeyInfo().getCerts()[0].getSubjectDN().getName(), "CN=www.sts.com, OU=STS, O=STS, ST=Budapest, C=HU");
	        
	        LOG.debug(DOM2Writer.nodeToString(assertion.getElement()));
//	        System.out.println(assertion.getSubjectKeyInfo().getCerts()[0].getSubjectDN());
//	        System.out.println(assertion.getSignatureKeyInfo().getCerts()[0].getSubjectDN());
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
