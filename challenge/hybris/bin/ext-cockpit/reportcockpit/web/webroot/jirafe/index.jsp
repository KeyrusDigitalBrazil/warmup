<%@ page import="javax.servlet.jsp.JspWriter" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ page import="java.io.IOException" %>
<%!
	public void writeEscapedParam(final String param, final HttpServletRequest request, final JspWriter out) throws IOException
	{
		out.write(StringEscapeUtils.escapeEcmaScript(request.getParameter(param)));
	}
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<link rel="stylesheet"  type="text/css" media="screen" href="https://test.jirafe.com/dashboard/css/hybris_ui.css" />
		<%-- Disabled - https://jira.hybris.com/browse/CNG-2536 --%>
		<%-- <script type="text/javascript" src="https://test.jirafe.com/dashboard/js/hybris_ui.js"></script> --%>
	</head>
<body>
	<div id="container" style="background: white; margin:auto;width: 1170px;"> 
	<div id="jirafe"></div>
	</div>
	<script type="text/javascript">
		if (typeof jQuery != 'undefined') {
			(function($) {
				 $('#jirafe').jirafe({
				    api_url:    '<% writeEscapedParam("jirafeApiUrl", request, out); %>',
				    api_token:  '<% writeEscapedParam("jirafeApiToken", request, out); %>',
		            app_id:     '<% writeEscapedParam("jirafeAppId", request, out); %>',
				    version:    '<% writeEscapedParam("jirafeVersion", request, out); %>'
				 });
			})(jQuery);
		}
		setTimeout(function() {
			if ($('mod-jirafe') == undefined){
				$('messages').insert ("<ul class=\"messages\"><li class=\"error-msg\">We're unable to connect with the Jirafe service for the moment. Please wait a few minutes and refresh this page later.</li></ul>");        
			}        
		}, 2000);
	</script>
</body>
</html>
