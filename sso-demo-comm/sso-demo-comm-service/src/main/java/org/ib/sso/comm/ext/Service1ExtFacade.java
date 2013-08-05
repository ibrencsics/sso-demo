package org.ib.sso.comm.ext;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.WebServiceContext;

import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.trust.STSClient;
import org.ib.sso.comm.lib.CommLibException;
import org.ib.sso.comm.lib.SamlTokenCallbackHandler;
import org.ib.sso.comm.lib.security.SAMLData;
import org.ib.sso.comm.lib.security.WebServiceContextTool;
import org.ib.sso.comm.lib.security.X509Data;
import org.ib.sso.service.service1.Service1Endpoint;
import org.ib.sso.service.service1.TestOperationFault;
import org.ib.sso.xsd.TestFaultType;
import org.ib.sso.xsd.TestRequestType;
import org.ib.sso.xsd.TestResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Service1ExtFacade implements Service1Endpoint, ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(Service1ExtFacade.class.getPackage().getName());
	
	private ApplicationContext appCtx;
	private boolean stopHere;
	private STSClient stsClient;
	private Service1Endpoint service1;
	private String keyType;
	private String tokenType;
	
	@Resource
    WebServiceContext wsCtx;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.appCtx = applicationContext;
	}
	
	public void setStopHere(boolean stopHere) {
		this.stopHere = stopHere;
	}
	
	public void setStsClient(STSClient stsClient) {
		this.stsClient = stsClient;
	}
	
	public void setService1(Service1Endpoint service1) {
		this.service1 = service1;
	}
	
	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	
	@Override
	public TestResponseType testOperation(TestRequestType request) 	throws TestOperationFault {
		
		TestResponseType allResults = new TestResponseType();
		allResults.setMessageId(request.getMessageId());
		
		// Check if there is a X.509 client certificate in the request
		X509Data x509Data=null;
		try {
			x509Data = WebServiceContextTool.getX509Data(wsCtx);
		} catch (CommLibException ex) {
			LOG.error("COMM (service) >>> " + ex.getMessage());
			LOG.error(ex.fillInStackTrace().toString());
			
			TestFaultType testFaultType = new TestFaultType();
			testFaultType.setType("Security");
			testFaultType.setDescription(ex.getMessage());
			throw new TestOperationFault(ex.getMessage(), testFaultType);
		}
		
		// Log the DN of the certificate
		String callerDN = x509Data.getPrincipalName();
		LOG.debug("COMM (service) >>> Service1Ext called by " + callerDN);
	
		// User in STSClient = CN
		String callerCN = DNParser.getCN(callerDN);
		stsClient.getProperties().put("ws-security.username", callerCN);
		LOG.debug("COMM (service) >>> STSClient: UsernameToken.username set to " + callerCN);
	
		// Configure STSClient
		stsClient.setKeyType(keyType);
		stsClient.setTokenType(tokenType);
		stsClient.setClaims(getClaimRequestDOM());
		
		// Call STS
		SamlTokenCallbackHandler cb = (SamlTokenCallbackHandler) appCtx.getBean("samlTokenCallbackHandler");
		try {
			SecurityToken token = stsClient.requestSecurityToken();
			cb.setToken(token.getToken());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Call Service1
		TestResponseType response=null;
		
		allResults.getNode().add("COMM >>> Calling Service1");
		
		if (!stopHere) {
			LOG.debug("COMM (service) >>> Service1 call started");
			response = service1.testOperation(request);
			LOG.debug("COMM (service) >>> Service1 call ended");
		}
		else {
			response = new TestResponseType();
			response.setMessageId("23");
			response.getNode().add(DNParser.getCN(wsCtx.getUserPrincipal().getName()));
		}
		
		allResults.getNode().addAll(response.getNode());
		
		return allResults;
	}

	
	protected Element getClaimRequestDOM() {
		
		try {
			Document doc = getDocumentBuilder().newDocument();
			Element elementCLaims = doc.createElementNS("http://docs.oasis-open.org/ws-sx/ws-trust/200512", "wst:Claims");
			doc.appendChild(elementCLaims);
			
			Attr attrDialect = doc.createAttribute("Dialect");
			attrDialect.setValue("http://schemas.xmlsoap.org/ws/2005/05/identity");
			elementCLaims.setAttributeNode(attrDialect);
			
			Element elementClaimType = doc.createElementNS("http://schemas.xmlsoap.org/ws/2005/05/identity", "ic:ClaimType");
			elementCLaims.appendChild(elementClaimType);
			
			Attr attrUri = doc.createAttribute("Uri");
			attrUri.setValue("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role");
			elementClaimType.setAttributeNode(attrUri);
			
			return elementCLaims;
			
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	protected DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder;
	}
}
