    
/*
 * Copyright (C) 2019 IgniterRealTime. All rights reserved.
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

import java.io.File;
import java.security.Security;
import java.util.Map;
import java.util.Iterator;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.util.PropertyEventDispatcher;
import org.jivesoftware.util.PropertyEventListener;
import org.jivesoftware.util.JiveGlobals;

import org.igniterealtime.openfire.plugin.JidValidationIQHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Packet;

/**
 * An Openfire plugin that implements XEP-0328: JID Validation Service
 * This plugin allowS XMPP entities to prepare and validate a given JID
 * @see <a href="http://geekplace.eu/xeps/xep-jidprep/xep-jidprep.html">XEP-0328 :JID Validation</a>.
 * @author <a href="mailto:ngudiamanasse@gmail.com">Manasse Ngudia</a>
 */
public class JidValidationPlugin implements Plugin, PropertyEventListener, PacketInterceptor {
    private static final Logger Log = LoggerFactory.getLogger(JidValidationPlugin.class);

    public static final String SERVICEENABLED = "plugin.jidvalidation.serviceEnabled";

    private boolean serviceEnabled;

    private JidValidationIQHandler iqHandler;

    public JidValidationPlugin() {
        serviceEnabled = JiveGlobals.getBooleanProperty(SERVICEENABLED, true);
    }

    /**
     * Checks if the jidvalidation service is enabled.
     * 
     * @return true if jidcalidation service is enabled.
     */
    public boolean getServiceEnabled() {
        return this.serviceEnabled;
    }

    /**
     * Enables or disables the jidvalidation service. When disabled,
     * 
     * @param enabled
     */
    public void setServiceEnabled(boolean enabled) {
        serviceEnabled = enabled;
        JiveGlobals.setProperty(SERVICEENABLED, enabled ? "true" : "false");
    }

    @Override
    public void initializePlugin(PluginManager manager, File pluginDirectory) {
        Log.info("Initializing JID Validation Plugin");
        PropertyEventDispatcher.addListener(this);
        Log.debug( "Registering IQ Handlers..." );
        iqHandler = new JidValidationIQHandler();
        XMPPServer.getInstance().getIQRouter().addHandler( iqHandler);
        Log.debug( "Registering Server Features..." );
        for ( final Iterator<String> it = iqHandler.getFeatures(); it.hasNext(); )
        {
            XMPPServer.getInstance().getIQDiscoInfoHandler().addServerFeature( it.next() );
        }
    }

    @Override
    public void destroyPlugin() {
        PropertyEventDispatcher.removeListener(this);
        Log.info("Destroying JID Validation Plugin");
        Log.debug( "Removing Server Features..." );
        for ( final Iterator<String> it = iqHandler.getFeatures(); it.hasNext(); )
        {
            XMPPServer.getInstance().getIQDiscoInfoHandler().removeServerFeature( it.next() );
        }
        if ( iqHandler != null ){
            Log.debug( "Removing IQ Handler..." );
            XMPPServer.getInstance().getIQRouter().removeHandler( iqHandler);
        }   
    }

    @Override
    public void propertyDeleted(String property, Map<String, Object> params) {
        if (property.equals(SERVICEENABLED)) {
            this.serviceEnabled = true;
        }
    }

    @Override
    public void propertySet(String property, Map<String, Object> params) {
        if (property.equals(SERVICEENABLED)) {
            this.serviceEnabled = Boolean.parseBoolean((String) params.get("value"));
        }

    }

    @Override
    public void xmlPropertyDeleted(String arg0, Map<String, Object> arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void xmlPropertySet(String arg0, Map<String, Object> arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {
        // TODO Auto-generated method stub
        if(!serviceEnabled){
            Log.info("JID Validation Plugin enabled");
        }else{
            Log.info("JID Validation Plugin disabled");

        }

    }
}
