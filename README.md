# openfire-jidvalidation-plugin
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<html>
<head>
    <title>JID Validation Plugin Readme</title>
    <style type="text/css">
        BODY {
            font-size : 100%;
        }
        BODY, TD, TH {
            font-family : tahoma, verdana, arial, helvetica, sans-serif;
            font-size : 0.8em;
        }
        H2 {
             font-size : 10pt;
             font-weight : bold;
        }
        A:hover {
            text-decoration : none;
        }
        H1 {
            font-family : tahoma, arial, helvetica, sans-serif;
            font-size : 1.4em;
            font-weight: bold;
            border-bottom : 1px #ccc solid;
            padding-bottom : 2px;
        }

        TT {
            font-family : courier new;
            font-weight : bold;
            color : #060;
        }
        PRE {
            font-family : courier new;
            font-size : 100%;
        }
    </style>
</head>
<body>

    <h1>
    JID Validation Pluging readme
    </h1>

    <h2>Overview</h2>
    <p>
    The JID Validation plugin adds JID Validation <a href="http://geekplace.eu/xeps/xep-jidprep/xep-jidprep.html">XEP-0328</a> 
    capabilities to Openfire. This plugin is designed to work with various Jabber clients to allow
    other users to prepare and validate a given JID.
    </p>

    <h2>Installation</h2>
    <p>
    Copy the file, &quot;jidvalidation.jar&quot; into the plugins directory of your Openfire installation. The plugin will
    then be automatically deployed. To upgrade to a new version: 1) go to the plugin screen of the Admin
    Console, 2) click on the delete icon on the same row as the currently installed search plugin, 3) 
    copy the new search.jar into the plugins directory of your Openfire installation.
    </p>

    <h2>Configuration</h2>
    <p>
    The JID Validation plugin is configured via the "Jid Validation Service Properites" sidebar item located under the "Server" tab
    in the Openfire Admin Console. By default, after the JID Validation plugin has been deployed all of its features
    are enabled. To enable or disable the plugin select the appropirate radio button and then click on the 
    "Save Properties" button.
    and click on the "Save Properties" button.
    </p>
</body>
</html>

