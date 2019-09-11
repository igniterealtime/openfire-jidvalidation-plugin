package org.igniterealtime.openfire.plugin;

import java.io.File;
import java.security.Security;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  * This class initialize the JidValidationPlugin
  */
public class JidValidationPlugin implements Plugin
{
    private static final Logger Log = LoggerFactory.getLogger(JidValidationPlugin.class );

    @Override
    public void initializePlugin( PluginManager manager, File pluginDirectory )
    {
        Log.info("Initializing JID Validation Plugin");
    }

    @Override
    public void destroyPlugin()
    {
        Log.info("Destroying JID Validation Plugin");
    }
}
