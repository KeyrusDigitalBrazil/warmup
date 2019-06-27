<%@ page import="com.sap.security.core.server.csi.XSSEncoder" %>
<%@include file="head.inc"%>
<a href="index.jsp">back to oci index page</a><hr><br>

<%

	//sample shop application 
	//page for searching products
	//no enhancements for oci needed here
	
	SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
	
	String searchTerm = (String)request.getParameter("searchterm");
	out.println("searching for: "+XSSEncoder.encodeHTML(searchTerm)+"<br>");
	
	Collection products = searchProducts(searchTerm, 100);
	out.println("<b>" + XSSEncoder.encodeHTML(Integer.toString(products.size())) + "</b> Produkte gefunden!<br>");
	
	out.println("<table border='1'>\n<tr><th>ProduktID</th><th>Produktname</th><th>Details</th><th>Description</th></tr>\n");
	for( Iterator it = products.iterator(); it.hasNext(); )
	{
		Product product = ((Product)it.next());
		out.println("<tr><td>"+XSSEncoder.encodeHTML(product.getCode())+"</td><td>"+XSSEncoder.encodeHTML(product.getName())+"</td>" );
		
		if( product.getThumbnail() != null )
		{
			out.println("<td><a href='productdetails.jsp?productid="+XSSEncoder.encodeHTML(XSSEncoder.encodeURL(product.getCode()))+"'><img src='"+XSSEncoder.encodeHTML(product.getThumbnail().getURL())+"'></a></td>\n");
		}
		else
		{
			out.println("<td><a href='productdetails.jsp?productid="+XSSEncoder.encodeHTML(XSSEncoder.encodeURL(product.getCode()))+"'>Produktdetails</a></td>\n");
		}
		
		out.println("<td>" + XSSEncoder.encodeHTML(product.getDescription(ctx)) + "</td>");
		out.println("</tr>");		
	}
	
	out.println("</table>");
%>

<%@include file="tail.inc"%>
