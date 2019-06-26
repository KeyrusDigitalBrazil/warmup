<%@page import="com.sap.security.core.server.csi.XSSEncoder" %>

<br><br>
<font size="1">
<% out.println("logged in as: " + XSSEncoder.encodeHTML(JaloSession.getCurrentSession().getUser().getUID())); %>
</font>
<hr>
<font size="1">
<%=XSSEncoder.encodeHTML(Config.getParameter("build.description"))%> - <%=XSSEncoder.encodeHTML(Config.getParameter("build.version"))%> - 
<%=XSSEncoder.encodeHTML(Config.getParameter("build.builddate"))%> - 
Copyright (c) 2017 SAP SE or an SAP affiliate company. - <a href="http://www.hybris.de">www.hybris.com</a></font><p>
<%
JaloSession.deactivate();
%>

</body>
</html>


