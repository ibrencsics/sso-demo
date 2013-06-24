package org.ib.sso.sts.issue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.CallbackHandler;
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
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.ws.security.WSDocInfo;
import org.apache.ws.security.WSSConfig;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.processor.Processor;
import org.apache.ws.security.processor.SAMLTokenProcessor;
import org.ib.sso.comm.lib.UsernamePasswordCallbackHandler;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Commons {

	public static final String SAML1_TOKEN_TYPE = 
			"http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV1.1";
	public static final String SAML2_TOKEN_TYPE = 
			"http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0";
	public static final String SYMMETRIC_KEY_KEYTYPE = 
			"http://docs.oasis-open.org/ws-sx/ws-trust/200512/SymmetricKey";
	public static final String PUBLIC_KEY_KEYTYPE = 
			"http://docs.oasis-open.org/ws-sx/ws-trust/200512/PublicKey";
	public static final String BEARER_KEYTYPE = 
			"http://docs.oasis-open.org/ws-sx/ws-trust/200512/Bearer";
	public static final String DEFAULT_ADDRESS = 
			"https://localhost:8081/doubleit/services/doubleittransportsaml1";
	
	public static SecurityToken requestSecurityToken(
	        String tokenType, 
	        String keyType, 
	        Bus bus,
	        String endpointAddress,
	        boolean secure
	    ) throws Exception {
	        return requestSecurityToken(tokenType, keyType, null, bus, endpointAddress, null, secure);
	    }

	public static SecurityToken requestSecurityToken(
			String tokenType, 
			String keyType, 
			Bus bus,
			String endpointAddress,
			String context,
			boolean secure
			) throws Exception {
		return requestSecurityToken(tokenType, keyType, null, bus, endpointAddress, context, secure);
	}

	public static SecurityToken requestSecurityToken(
			String tokenType, 
			String keyType,
			Element supportingToken,
			Bus bus,
			String endpointAddress,
			String context,
			boolean secure
			) throws Exception {
		STSClient stsClient = new STSClient(bus);
		
		String port = secure ? "9443" : "9080";
		String protocol = secure ? "https" : "http";
		String wsdl = secure ? "STSService" : "STSSymmetricService";
		String wsdlPort = secure ? "TransportUT_Port" : "SymmetricUT_Port";
		// https://localhost:9443/sso-demo-security-sts/STSService?wsdl
		stsClient.setWsdlLocation(protocol + "://localhost:" + port + "/sso-demo-security-sts/" + wsdl + "?wsdl");
		
		stsClient.setServiceName("{http://docs.oasis-open.org/ws-sx/ws-trust/200512/}SecurityTokenService");
		stsClient.setEndpointName("{http://docs.oasis-open.org/ws-sx/ws-trust/200512/}" + wsdlPort);

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(SecurityConstants.USERNAME, "Libri");
		properties.put(
				SecurityConstants.CALLBACK_HANDLER, 
				getCallbackHandler()
//				"org.ib.sso.comm.sts.common.CommonCallbackHandler"
				);
		properties.put(SecurityConstants.IS_BSP_COMPLIANT, "false");

		if (Commons.PUBLIC_KEY_KEYTYPE.equals(keyType)) {
			properties.put(SecurityConstants.STS_TOKEN_USERNAME, "commkey");
			properties.put(SecurityConstants.STS_TOKEN_PROPERTIES, "comm.properties");
		
			properties.put("ws-security.encryption.properties", "comm.properties");
			properties.put("ws-security.encryption.username", "sts");
			
			stsClient.setUseCertificateForConfirmationKeyInfo(true);
		}
		if (supportingToken != null) {
			stsClient.setOnBehalfOf(supportingToken);
		}
		if (context != null) {
			stsClient.setContext(context);
		}

//		stsClient.setClaims(getClaimRequest());
		stsClient.setClaims(getClaimRequestDOM());
		
		stsClient.setProperties(properties);
		stsClient.setTokenType(tokenType);
		stsClient.setKeyType(keyType);
		
		return stsClient.requestSecurityToken(endpointAddress);
	}
	
	private static CallbackHandler getCallbackHandler() {
		UsernamePasswordCallbackHandler cb = new UsernamePasswordCallbackHandler();
		
		Map<String,String> passwords = new HashMap<String, String>();
		passwords.put("commkey", "commpass");
		passwords.put("Libri", "dummypass");
		
		cb.setPasswords(passwords);
		
		return cb;
	}
	
	private static Element getClaimRequestDOM() {
		
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
	
	private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder;
	}
	
	private static Element getClaimRequest() {
		
		String claim = "<wst:Claims Dialect=\"http://schemas.xmlsoap.org/ws/2005/05/identity\" xmlns:wst=\"http://docs.oasis-open.org/ws-sx/ws-trust/200512\" xmlns:ic=\"http://schemas.xmlsoap.org/ws/2005/05/identity\"><ic:ClaimType Uri=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role\"/></wst:Claims>";
		
		Element node=null;
		
		try {
			node = DocumentBuilderFactory
				    .newInstance()
				    .newDocumentBuilder()
				    .parse(new ByteArrayInputStream(claim.getBytes()))
				    .getDocumentElement();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return node;
	}
	
	public static String docToString(Node doc) {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
			return output;
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static List<WSSecurityEngineResult> processToken(SecurityToken token) throws Exception {
        RequestData requestData = new RequestData();
        WSSConfig wssConfig = WSSConfig.getNewInstance();
        wssConfig.setWsiBSPCompliant(false);
        requestData.setWssConfig(wssConfig);
        CallbackHandler callbackHandler = getCallbackHandler();
        requestData.setCallbackHandler(callbackHandler);
        Crypto crypto = CryptoFactory.getInstance("sts.properties");
        requestData.setDecCrypto(crypto);
        requestData.setSigCrypto(crypto);
        
        Processor processor = new SAMLTokenProcessor();
        return processor.handleToken(
            token.getToken(), requestData, new WSDocInfo(token.getToken().getOwnerDocument())
        );
    }

}
