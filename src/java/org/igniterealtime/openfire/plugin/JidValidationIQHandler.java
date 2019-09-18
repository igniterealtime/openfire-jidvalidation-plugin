
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
import org.jivesoftware.openfire.XMPPServer;
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
import java.util.List;


/**
 * An IQ handler that implements XEP-0328: JID Validation Service.
 *
 * @author Manasse Ngudia, ngudiamanasse@gmail.com
 * @see <a href="http://geekplace.eu/xeps/xep-jidprep/xep-jidprep.html">XEP-0328: JID Validation Service</a>
 */
public class JidValidationIQHandler extends IQHandler  implements ServerFeaturesProvider{
    private static final Logger Log = LoggerFactory.getLogger( JidValidationIQHandler.class );
    public static final String  SERVICEENABLED = "plugin.jidvalidation.serviceEnabled";
    private static final String ELEMENT_REQUEST = "jid-validate-request";
    private static final String ELEMENT_RESULT = "jid-validate-result";
    private static final String NAMESPACE = "urn:xmpp:jidprep:1";
    private final IQHandlerInfo info;

    private SystemProperty<Boolean> PROPERTY_ENABLED ;

    public JidValidationIQHandler() {
        super( "XEP-0328: JID Validation Service" );
        if(!SystemProperty.getProperty(SERVICEENABLED).isPresent()){
            PROPERTY_ENABLED = SystemProperty.Builder.ofType( Boolean.class )
                                .setKey( SERVICEENABLED )
                                .setDefaultValue( Boolean.TRUE)
                                .setDynamic(Boolean.TRUE)
                                .addListener(JidValidationIQHandler::onChangeSystemProperty)
                                .build();
        }
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
        if ( packet.isResponse() )
        {
            Log.debug( "Responding with an error to an IQ request of type 'error' or''result': {}", packet );
            response.setError(Condition.service_unavailable );
            return response;
        }

        if ( IQ.Type.set == packet.getType() )
        {
            Log.debug( "Responding with an error to an IQ request of type 'set': {}", packet );
            response.setError(Condition.service_unavailable );
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
                            Element resultElement = DocumentHelper.createElement(QName.get(ELEMENT_RESULT, info.getNamespace()));
                            Element validElement =  resultElement.addElement("valid-jid");
                            validElement.addElement("localpart").setText(jid.getNode());
                            validElement.addElement("domainpart").setText(jid.getDomain());
                            validElement.addElement("resourcepart").setText(jid.getResource()); 
                            response.setChildElement(resultElement);
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
        Log.debug( "Responding with an error to an IQ request if something fails or an unexpected element" );
        response.setError(Condition.service_unavailable );
        return response;
    }

    public static void onChangeSystemProperty(final boolean enabled) {
        try {
            if(!enabled){
                List<IQHandler> iqHandlers = XMPPServer.getInstance().getIQHandlers();
                for(IQHandler iqHandlerIn : iqHandlers){
                    if (iqHandlerIn instanceof JidValidationIQHandler){
                        JidValidationIQHandler iqHandlerJid =  (JidValidationIQHandler) iqHandlerIn;
                        //remove iqHandler to server
                        for (final Iterator<String> it = iqHandlerJid.getFeatures(); it.hasNext();) {
                            XMPPServer.getInstance().getIQDiscoInfoHandler().removeServerFeature(it.next());
                        }
                        XMPPServer.getInstance().getIQRouter().removeHandler(iqHandlerJid);
                    }
                }
            }else{
                JidValidationIQHandler iqHandlerAdd = new JidValidationIQHandler();
                XMPPServer.getInstance().getIQRouter().addHandler(iqHandlerAdd);
                for (final Iterator<String> it = iqHandlerAdd.getFeatures(); it.hasNext();) {
                    XMPPServer.getInstance().getIQDiscoInfoHandler().addServerFeature(it.next());
                }
            }
        } catch (Exception e) {
            Log.debug( "onChangeSystemProperty {} "+e.toString());
        }
    }
}