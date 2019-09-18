
/** 
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
import java.util.Iterator;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.XMPPServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An Openfire plugin that implements XEP-0328: JID Validation Service This
 * plugin allowS XMPP entities to prepare and validate a given JID
 * 
 * @see <a href="http://geekplace.eu/xeps/xep-jidprep/xep-jidprep.html">XEP-0328
 *      :JID Validation</a>.
 * @author <a href="mailto:ngudiamanasse@gmail.com">Manasse Ngudia</a>
 */
public class JidValidationPlugin implements Plugin {
    private static final Logger Log = LoggerFactory.getLogger(JidValidationPlugin.class);
    private JidValidationIQHandler iqHandler = null;
    
    @Override
    public void initializePlugin(PluginManager manager, File pluginDirectory) {
        Log.info("Initializing JID Validation Plugin");
        if(iqHandler == null){
            iqHandler = new JidValidationIQHandler();
            XMPPServer.getInstance().getIQRouter().addHandler(iqHandler);
            for (final Iterator<String> it = iqHandler.getFeatures(); it.hasNext();) {
                XMPPServer.getInstance().getIQDiscoInfoHandler().addServerFeature(it.next());
            }
        }
    }

    @Override
    public void destroyPlugin() {
        Log.info("Destroying JID Validation Plugin");
        if (iqHandler != null) {
            for (final Iterator<String> it = iqHandler.getFeatures(); it.hasNext();) {
                XMPPServer.getInstance().getIQDiscoInfoHandler().removeServerFeature(it.next());
            }
            XMPPServer.getInstance().getIQRouter().removeHandler(iqHandler);
        }
    }

    
}
