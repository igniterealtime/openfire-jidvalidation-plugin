
/** 
 * Copyright (C) 2019 Ignite Realtime Foundation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.igniterealtime.openfire.plugin;


import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.disco.ServerFeaturesProvider;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.util.SystemProperty;

import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError.Condition;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.Iterator;

/**
 * An IQ handler that implements XEP-0328: JID Validation Service.
 *
 * @author Manasse Ngudia, ngudiamanasse@gmail.com
 * @see <a href="http://geekplace.eu/xeps/xep-jidprep/xep-jidprep.html">XEP-0328: JID Validation Service</a>
 */
public class JidValidationIQHandler extends IQHandler  implements ServerFeaturesProvider{
    private static final Logger Log = LoggerFactory.getLogger( JidValidationIQHandler.class );

    private static final String ELEMENT_REQUEST = "jid-validate-request";
    private static final String ELEMENT_RESULT = "jid-validate-result";
    private static final String NAMESPACE = "urn:xmpp:jidprep:1";
    private final IQHandlerInfo info;

    public static SystemProperty<Boolean> PROPERTY_ENABLED = SystemProperty.Builder.ofType( Boolean.class )
        .setKey( "plugin.jidvalidation.serviceEnabled" )
        .setDefaultValue( Boolean.TRUE)
        .setDynamic(Boolean.TRUE)
        .build(); 

    public JidValidationIQHandler() {
        super( "XEP-0328: JID Validation Service" );
        info = new IQHandlerInfo(ELEMENT_REQUEST, NAMESPACE);
    }

	/**
     * Returns the handler information to help generically handle IQ packets.
     *
     * @return The IQHandlerInfo for this handler
     */
    @Override
    public IQHandlerInfo getInfo()
    {
        return info;
    }

    @Override
	public Iterator<String> getFeatures() {
        if ( !PROPERTY_ENABLED.getValue() )
        {
            return null;
        }
	    return Collections.singleton( info.getNamespace() ).iterator();
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
        final IQ response = IQ.createResultIQ(packet);
        JID jid = null;

        if ( !PROPERTY_ENABLED.getValue() )
        {
            Log.debug( "Unable to process request: service has been disabled by configuration." );
            response.setError( Condition.service_unavailable );
            return response;
        }

        if (IQ.Type.get.equals(packet.getType())) {
            Element childElement = packet.getChildElement();
            String namespace = null;
            if(childElement != null){
                namespace = childElement.getNamespaceURI();
            }
            if(childElement.getName().equals(ELEMENT_REQUEST)
                && namespace.equals(info.getNamespace())){
                    Element dataElement = (Element) childElement.elementIterator().next();
                    if(dataElement.getName().equals("maybe-jid")){
                        String jidToCheck= dataElement.getStringValue();
                        try {
                            jid = new JID(jidToCheck);
                            if(jid != null){
                                Element resultElement = DocumentHelper.createElement(QName.get(ELEMENT_RESULT, info.getNamespace()));
                                Element validElement =  resultElement.addElement("valid-jid");
                                validElement.addElement("localpart").setText(jid.getNode());
                                validElement.addElement("domainpart").setText(jid.getDomain());
                                validElement.addElement("resourcepart").setText(jid.getResource()); 
                                response.setChildElement(resultElement);
                            }
                            return response;
                        } catch (Exception e) {
                            Element resultElement = DocumentHelper.createElement(QName.get(ELEMENT_RESULT, info.getNamespace()));
                            Element invalidElement =  resultElement.addElement("invalid-jid");
                            invalidElement.addElement("reason").setText(e.getMessage());
                            response.setChildElement(resultElement);
                            return response;
                        }
                        
                    }
            }
        }
        return response;
    }

}