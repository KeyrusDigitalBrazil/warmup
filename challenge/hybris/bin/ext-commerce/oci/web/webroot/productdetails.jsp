<%@include file="head.inc"%>
<%@page import="com.sap.security.core.server.csi.XSSEncoder" %>
<a href="index.jsp">back to oci index page</a><hr><br>
<%

	//sample shop application
	//page to show the product details
	//no enhancements for oci needed here
	
	Collection products = JaloSession.getCurrentSession().getProductManager().getProductsByCode(request.getParameter("productid"));
	SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
		
	if( !products.isEmpty() )
	{
		Product p = ((Product)products.iterator().next());
		
		out.println("<b>Code:</b> " + XSSEncoder.encodeHTML(p.getCode()) + "<br>");
		out.println("<b>Name:</b> " + XSSEncoder.encodeHTML(p.getName()) + "<br>");     
		out.println("<b>Description:</b> " + XSSEncoder.encodeHTML(p.getDescription()) + "<br>"); 
		if( p.getPicture() != null )
		{
			out.println("<img src='"+XSSEncoder.encodeHTML(p.getPicture().getURL())+"'>" );
		}
		else
		{
			out.println("no picture");
		}
		
		out.println("<a href='cart.jsp?productid="+XSSEncoder.encodeHTML(p.getCode())+"'>add to cart</a>"); 
	}
%>

<%@include file="tail.inc"%>
