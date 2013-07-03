package org.ib.sso.comm.ext;


import javax.annotation.Resource;
import javax.security.auth.callback.CallbackHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.WebServiceContext;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.tokenstore.TokenStore;
import org.apache.cxf.ws.security.trust.STSClient;
import org.ib.sso.comm.CommException;
import org.ib.sso.comm.lib.CommLibException;
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

public class Service1Ext implements Service1Endpoint, ApplicationContextAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(Service1Ext.class.getPackage().getName());

	private static final String SAML2_TOKEN_TYPE = 
			"http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0";
	private static final String PUBLIC_KEY_KEYTYPE = 
			"http://docs.oasis-open.org/ws-sx/ws-trust/200512/PublicKey";
	
	
	@Resource
    WebServiceContext wsCtx;
	
	private ApplicationContext appCtx;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.appCtx = applicationContext;
	}

	private final boolean stopHere = false;
	private final boolean auto = true;
	
	enum StsClientType {
		HTTPS("stsClient", "Service1Client"), WSSEC("stsClientWsSec", "Service1ClientWsSec");
	
		private String id;
		private String serviceId;
		
		StsClientType(String id, String serviceId) {
			this.id = id;
			this.serviceId = serviceId;
		}
		
		String getId() {
			return this.id;
		}

		String getServiceId() {
			return serviceId;
		}
	}
	private final StsClientType stsClientType = StsClientType.HTTPS;
	
	
	public TestResponseType testOperation(TestRequestType request) throws TestOperationFault {
		
		TestResponseType response=null;
		
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
		
		String callerDN = x509Data.getPrincipalName();
		LOG.debug("COMM (service) >>> Service1Ext called by " + callerDN);
		
		LOG.debug("COMM (service) >>> STSClient '" + stsClientType.getId() + "' will be used");
		STSClient stsClient = (STSClient) appCtx.getBean(/*"stsClient"*/ stsClientType.getId());
		
		String callerCN = DNParser.getCN(callerDN);
		stsClient.getProperties().put("ws-security.username", callerCN);
		LOG.debug("COMM (service) >>> STSClient: UsernameToken.username set to " + callerCN);
		
		Service1Endpoint service1=null;
		
		if (auto) {
			service1 = (Service1Endpoint) appCtx.getBean(/*"Service1Client"*/stsClientType.getServiceId());
		}
		else {
			service1 = (Service1Endpoint) appCtx.getBean("Service1ManualClient");
			
			stsClient.setKeyType(PUBLIC_KEY_KEYTYPE);
			stsClient.setTokenType(SAML2_TOKEN_TYPE);
			stsClient.setClaims(getClaimRequestDOM());
			
			SamlTokenCallbackHandler cb = (SamlTokenCallbackHandler) appCtx.getBean("samlTokenCallbackHandler");
			try {
				SecurityToken token = stsClient.requestSecurityToken();
				cb.setToken(token.getToken());
				
				Client client = ClientProxy.getClient(service1);
				Endpoint ep = client.getEndpoint();
				
				TokenStore store = (TokenStore)ep.getEndpointInfo().getProperty(TokenStore.class.getName());
				store.add(token.getId(), token);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (!stopHere) {
		
			LOG.debug("COMM (service) >>> Service1 call started");
			response = service1.testOperation(request);
			LOG.debug("COMM (service) >>> Service1 call ended");
	
			try {
				SAMLData samlData = WebServiceContextTool.getSamlData(service1);
		
				LOG.debug("COMM (service) >>> ******************** TOKEN ********************");
				LOG.debug("COMM (service) >>> " + samlData.getTokenAsString());
				LOG.debug("COMM (service) >>> ******************** TOKEN ********************");
			} catch (CommLibException ex) {
				ex.printStackTrace();
				// TODO: throw web service exception
			}
		}
		else {
			response.setMessageId("23");
			response.getNode().add(DNParser.getCN(wsCtx.getUserPrincipal().getName()));
		}
			
		return response;
	}
	
	private Element getClaimRequestDOM() {
		
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
	
	private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder;
	}
}
