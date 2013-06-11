package org.ib.sso.comm.sts.dom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.smartcardio.ATR;
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

import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DOMTest {
	
//	@Test
	public void createXMLTest() throws ParserConfigurationException {
		
		Document doc = getDocumentBuilder().newDocument();
		Element rootElement = doc.createElementNS("urn:oasis:names:tc:SAML:1.0:assertion", "saml1:Assertion");
		doc.appendChild(rootElement);
		
		Element conditions = doc.createElement("saml1:Conditions");
		rootElement.appendChild(conditions);
		
		Attr attrNotBefore = doc.createAttribute("NotBefore");
		attrNotBefore.setValue("2013-06-11T09:15:34.370Z");
		conditions.setAttributeNode(attrNotBefore);
		
		Element signature = doc.createElementNS("http://www.w3.org/2000/09/xmldsig#", "ds:Signature");
		rootElement.appendChild(signature);
		
		System.out.println(nodeToString(doc));
	}
	
//	@Test
	public void createClaimsTest() throws ParserConfigurationException {
		
		Document doc = getDocumentBuilder().newDocument();
		Element elementCLaims = doc.createElementNS("http://docs.oasis-open.org/ws-sx/ws-trust/200512", "wst:Claims");
		doc.appendChild(elementCLaims);
		
		Attr attrDialect = doc.createAttribute("Dialect");
		attrDialect.setValue("http://schemas.xmlsoap.org/ws/2005/05/identity");
		elementCLaims.setAttributeNode(attrDialect);
		
//		Attr nsIC = doc.createAttribute("xmlns:ic");
//		nsIC.setValue("http://schemas.xmlsoap.org/ws/2005/05/identity");
//		elementCLaims.setAttributeNode(nsIC);
		
		Element elementClaimType = doc.createElementNS("http://schemas.xmlsoap.org/ws/2005/05/identity", "ic:ClaimType");
		elementCLaims.appendChild(elementClaimType);
		
		Attr attrUri = doc.createAttribute("Uri");
		attrUri.setValue("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role");
		elementClaimType.setAttributeNode(attrUri);
		
		System.out.println(nodeToString(doc));
	}
	
	@Test
	public void parseTokenTest() throws Exception {
		
		String token = "<saml2:Assertion xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ID=\"_59D70FA3CA953F66F4137095413903411\" IssueInstant=\"2013-06-11T12:35:39.034Z\" Version=\"2.0\" xsi:type=\"saml2:AssertionType\"><saml2:Issuer>SSO Demo STS</saml2:Issuer><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#dsa-sha1\"/><ds:Reference URI=\"#_59D70FA3CA953F66F4137095413903411\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/><ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"><ec:InclusiveNamespaces xmlns:ec=\"http://www.w3.org/2001/10/xml-exc-c14n#\" PrefixList=\"xs\"/></ds:Transform></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/><ds:DigestValue>Pe5T5PLVsvK7/W7WXcF0e9AYKNg=</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>CF0bMe0t1daO784RiLXxq2LH2tkvktXX7d46GNZwj9HGGcHWr3+zTg==</ds:SignatureValue><ds:KeyInfo><ds:X509Data><ds:X509Certificate>MIIDRTCCAq6gAwIBAgIBATANBgkqhkiG9w0BAQQFADBlMQswCQYDVQQGEwJIVTERMA8GA1UECBMIQnVkYXBlc3QxETAPBgNVBAcTCEJ1ZGFwZXN0MQwwCgYDVQQKEwNDQTExDDAKBgNVBAsTA0NBMTEUMBIGA1UEAxMLd3d3LmNhMS5jb20wHhcNMTMwNjEwMTM0NDQwWhcNMTYwMzA2MTM0NDQwWjBSMQswCQYDVQQGEwJIVTERMA8GA1UECBMIQnVkYXBlc3QxDDAKBgNVBAoTA1NUUzEMMAoGA1UECxMDU1RTMRQwEgYDVQQDEwt3d3cuc3RzLmNvbTCCAbgwggEsBgcqhkjOOAQBMIIBHwKBgQD9f1OBHXUSKVLfSpwu7OTn9hG3UjzvRADDHj+AtlEmaUVdQCJR+1k9jVj6v8X1ujD2y5tVbNeBO4AdNG/yZmC3a5lQpaSfn+gEexAiwk+7qdf+t8Yb+DtX58aophUPBPuD9tPFHsMCNVQTWhaRMvZ1864rYdcq7/IiAxmd0UgBxwIVAJdgUI8VIwvMspK5gqLrhAvwWBz1AoGBAPfhoIXWmz3ey7yrXDa4V7l5lK+7+jrqgvlXTAs9B4JnUVlXjrrUWU/mcQcQgYC0SRZxI+hMKBYTt88JMozIpuE8FnqLVHyNKOCjrh4rs6Z1kW6jfwv6ITVi8ftiegEkO8yk8b6oUZCJqIPf4VrlnwaSi2ZegHtVJWQBTDv+z0kqA4GFAAKBgQCVTF/zS6ThM5GaZucHISOi3iYgQo85LbzTy/cIZ4wHUqBG02lJ3kCgQBzUUR7vM2HzzQNSWBWKTjMu/yZzfzj/ptmLtnCltqILzhsfawG57Ss4fj5gUN+Bf7bOTJpes4tvMPS1k1JufUssxzaSM5RurIE7as2Fv+clOH+LyEqnKjANBgkqhkiG9w0BAQQFAAOBgQAj5egeLMTAu1TZpm+ipZCUY+OLcJV+NMHEt5/0cMI6Oub6xgmgOd6eNTUURuobftvSOODD0ypor9VsBL3T0hQ8A1Ze+kQ5AMLwHgE7h4bzMe5/KN1VNZRHny5MaDbz4Ink/bgCXsZhAacdjynqtRbBdkbZ2jPUfrGevV+sc2QS8Q==</ds:X509Certificate></ds:X509Data></ds:KeyInfo></ds:Signature><saml2:Subject><saml2:NameID Format=\"urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified\" NameQualifier=\"http://cxf.apache.org/sts\">Libri</saml2:NameID><saml2:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:holder-of-key\"><saml2:SubjectConfirmationData xsi:type=\"saml2:KeyInfoConfirmationDataType\"><ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:X509Data><ds:X509Certificate>MIIDSDCCArGgAwIBAgIBAzANBgkqhkiG9w0BAQQFADBlMQswCQYDVQQGEwJIVTERMA8GA1UECBMIQnVkYXBlc3QxETAPBgNVBAcTCEJ1ZGFwZXN0MQwwCgYDVQQKEwNDQTExDDAKBgNVBAsTA0NBMTEUMBIGA1UEAxMLd3d3LmNhMS5jb20wHhcNMTMwNjEwMjIwMTAzWhcNMTYwMzA2MjIwMTAzWjBVMQswCQYDVQQGEwJIVTERMA8GA1UECBMIQnVkYXBlc3QxDTALBgNVBAoTBENvbW0xDTALBgNVBAsTBENvbW0xFTATBgNVBAMTDHd3dy5jb21tLmNvbTCCAbgwggEsBgcqhkjOOAQBMIIBHwKBgQD9f1OBHXUSKVLfSpwu7OTn9hG3UjzvRADDHj+AtlEmaUVdQCJR+1k9jVj6v8X1ujD2y5tVbNeBO4AdNG/yZmC3a5lQpaSfn+gEexAiwk+7qdf+t8Yb+DtX58aophUPBPuD9tPFHsMCNVQTWhaRMvZ1864rYdcq7/IiAxmd0UgBxwIVAJdgUI8VIwvMspK5gqLrhAvwWBz1AoGBAPfhoIXWmz3ey7yrXDa4V7l5lK+7+jrqgvlXTAs9B4JnUVlXjrrUWU/mcQcQgYC0SRZxI+hMKBYTt88JMozIpuE8FnqLVHyNKOCjrh4rs6Z1kW6jfwv6ITVi8ftiegEkO8yk8b6oUZCJqIPf4VrlnwaSi2ZegHtVJWQBTDv+z0kqA4GFAAKBgQCpPQasYOWmPQTgjoj5kADh5lup/uIPEQzU4DZGTWZmbdNAHizCQ9ORwZyfCHMa3kGM8JqPCTuHZ9JKQb75bwbMA1fYqnTItKl1ZcOTcUM2MoEIZ19iqu9GzwNB0kp83hwvoTkQDH2JVk7D3wCb7xuGBmWSJxaJ595djIwx1u1fkTANBgkqhkiG9w0BAQQFAAOBgQCAVerdOgNuw7YYGZ7wd8Cy9uMdbm16HaRrw0Sb9ChSwTUIgI2LNq8p0o30MVIS4xvqfx4865LG8h+oc9ataqMvVsMK3ErdvpxPWHNc1TinkKENLZSUsbDja3jVU0S6vZ8lY1AbpEXHbvW0+cfrAhNyx7CMFiWOVn31Jm0iyme6Yw==</ds:X509Certificate></ds:X509Data></ds:KeyInfo></saml2:SubjectConfirmationData></saml2:SubjectConfirmation></saml2:Subject><saml2:Conditions NotBefore=\"2013-06-11T12:35:39.035Z\" NotOnOrAfter=\"2013-06-11T12:55:39.035Z\"><saml2:AudienceRestriction><saml2:Audience>https://localhost:8081/doubleit/services/doubleittransportsaml1</saml2:Audience></saml2:AudienceRestriction></saml2:Conditions><saml2:AttributeStatement><saml2:Attribute Name=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role\" NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified\"><saml2:AttributeValue xsi:type=\"xs:string\">Role 1</saml2:AttributeValue></saml2:Attribute><saml2:Attribute Name=\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role\" NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified\"><saml2:AttributeValue xsi:type=\"xs:string\">Role 2</saml2:AttributeValue></saml2:Attribute></saml2:AttributeStatement></saml2:Assertion>";
		
		Document doc = getDocumentBuilder().parse(new ByteArrayInputStream(token.getBytes()));
		
		//optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();
		
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		
		String principal = null;
		
		NodeList nList = doc.getElementsByTagName("saml2:NameID");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			principal = nNode.getTextContent();
		}
		
		System.out.println("Principal: " + principal);
		
		List<String> roles = new ArrayList<String>();
		
		nList = doc.getElementsByTagName("saml2:Attribute");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				 
				Element eElement = (Element) nNode;
				String attrName = eElement.getAttribute("Name");
				
				if ("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role".equals(attrName)) {
					roles.add(eElement.getTextContent());
				}
			}
		}
		
		System.out.println("Roles: " + roles);
	}
	
	
	private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder;
	}
	
	private String nodeToString(Node doc) {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
			StringWriter writer = new StringWriter();
			
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			
//			String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
			String output = writer.toString();
			
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
}
