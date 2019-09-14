<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*,
                 org.jivesoftware.openfire.XMPPServer,
                 org.igniterealtime.openfire.plugin.JidValidationPlugin,
                 org.jivesoftware.openfire.user.*,
                 org.jivesoftware.util.*"
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%  // Get parameters
    boolean save = request.getParameter("save") != null;
    boolean success = request.getParameter("success") != null;
    boolean jidvalidationEnabled = ParamUtils.getBooleanParameter(request, "jidvalidationEnabled");

    JidValidationPlugin plugin = (JidValidationPlugin) XMPPServer.getInstance().getPluginManager().getPlugin("jidvalidation");

    // Handle a save
    Map<String,String> errors = new HashMap<String,String>();
    if (save) {
        if (errors.size() == 0) {
            plugin.setServiceEnabled(jidvalidationEnabled);
            response.sendRedirect("jidvalidation-props-edit-form.jsp?success=true");
            return;
        }
    }
    jidvalidationEnabled = plugin.getServiceEnabled();
%>

<html>
    <head>
        <title><fmt:message key="jidvalidation.props.edit.form.title" /></title>
        <meta name="pageID" content="jidvalidation-props-edit-form"/>
    </head>
    <body>
        <p>
        <fmt:message key="jidvalidation.props.edit.form.directions" />
        </p>

        <%  if (success) { %>
            <div class="jive-success">
                <table cellpadding="0" cellspacing="0" border="0">
                    <tbody>
                        <tr><td class="jive-icon"><img src="images/success-16x16.gif" width="16" height="16" border="0" alt=""></td>
                        <td class="jive-icon-label">
                            <fmt:message key="jidvalidation.props.edit.form.successful_edit" />
                        </td></tr>
                    </tbody>
                </table>
            </div><br>
        <%  } else if (errors.size() > 0) { %>
            <div class="jive-error">
            <table cellpadding="0" cellspacing="0" border="0">
            <tbody>
                <tr><td class="jive-icon"><img src="images/error-16x16.gif" width="16" height="16" border="0" alt=""></td>
                <td class="jive-icon-label">
                    <fmt:message key="jidvalidation.props.edit.form.error" />
                </td></tr>
            </tbody>
            </table>
            </div><br>
        <%  } %>
        <form action="jidvalidation-props-edit-form.jsp?save" method="post">
            <div class="jive-contentBoxHeader"><fmt:message key="jidvalidation.props.edit.form.service_enabled" /></div>
            <div class="jive-contentBox">
                <p>
                <fmt:message key="jidvalidation.props.edit.form.service_enabled_directions" />
                </p>
                <table cellpadding="3" cellspacing="0" border="0" width="100%">
                    <tbody>
                        <tr>
                            <td width="1%">
                                <input type="radio" name="jidvalidationEnabled" value="true" id="rb01"
                                <%= ((jidvalidationEnabled) ? "checked" : "") %>>
                            </td>
                            <td width="99%">
                                <label for="rb01"><b><fmt:message key="jidvalidation.props.edit.form.enabled" /></b></label> - <fmt:message key="jidvalidation.props.edit.form.enabled_details" />
                            </td>
                        </tr>
                        <tr>
                            <td width="1%">
                                <input type="radio" name="jidvalidationEnabled" value="false" id="rb02"
                                <%= ((!jidvalidationEnabled) ? "checked" : "") %>>
                            </td>
                            <td width="99%">
                                <label for="rb02"><b><fmt:message key="jidvalidation.props.edit.form.disabled" /></b></label> - <fmt:message key="jidvalidation.props.edit.form.disabled_details" />
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <br>
            <input type="submit" value="<fmt:message key="jidvalidation.props.edit.form.save_properties" />">
        </form>

    </body>
</html>
