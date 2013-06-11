/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.fediz.examples.service;

import java.io.StringWriter;
import java.security.Principal;

import javax.annotation.Resource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.interceptor.security.SAMLSecurityContext;
import org.apache.cxf.security.SecurityContext;
import org.apache.hello_world_soap_http.Greeter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GreeterImpl implements Greeter {

    private static final Logger LOG = LoggerFactory.getLogger(GreeterImpl.class.getPackage().getName());

    @Resource
    WebServiceContext context;

    public String greetMe() {
        LOG.info("Executing operation greetMe");
        System.out.println("Executing operation greetMe");
        
        MessageContext ctx = context.getMessageContext();
        SecurityContext secCtx = (SecurityContext)ctx.get(SecurityContext.class.getName());
        if (secCtx instanceof SAMLSecurityContext) {
        	SAMLSecurityContext samlCtx = (SAMLSecurityContext)secCtx;
        	
        	Element token = samlCtx.getAssertionElement();
                    // ((SAMLSecurityContext)secCtx).getAssertionElement();
        	System.out.println("Received token:");
        	System.out.println(nodeToString(token));
        }
        
        
        if (context == null) {
            return "Unknown user";
        } else {
            Principal p = context.getUserPrincipal();
            if (p == null) {
                return "Principal null";
            }
            return p.getName();
        }
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
