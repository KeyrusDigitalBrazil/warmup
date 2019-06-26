<%@include file="head.inc"%>
<%@page import="com.sap.security.core.server.csi.XSSEncoder" %>
<a href="index.jsp">back to oci index page</a><hr><br>
<%		   
	//sample shop application
	//show (all) products
	//no enhancements for oci needed here

	ProductManager pM = JaloSession.getCurrentSession().getProductManager();
	SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
	
	Collection products = searchProducts(null, 100);
	if( !products.isEmpty() )
	{
		out.println("<b>" + XSSEncoder.encodeHTML(Integer.toString(products.size())) + "</b> Products found!<br><br>\n" );
		out.println("<table border='1'>\n<tr><th>ProduktID</th><th>Produktname</th><th>Details</th><th>Description</th></tr>\n");
		
		for( Iterator it = products.iterator(); it.hasNext(); )
		{
			Product product = ((Product)it.next());
			out.println("<tr><td>"+XSSEncoder.encodeHTML(product.getCode())+"</td><td>"+XSSEncoder.encodeHTML(product.getName())+"</td>" );
		
			if(product.getThumbnail()!= null)
			{
				out.println("<td><a href='productdetails.jsp?productid="+XSSEncoder.encodeHTML(XSSEncoder.encodeURL(product.getCode()))+"'><img src='"+XSSEncoder.encodeHTML(product.getThumbnail().getURL())+"'></a></td>\n" );
			}
			else
			{
				out.println("<td><a href='productdetails.jsp?productid="+XSSEncoder.encodeHTML(XSSEncoder.encodeURL(product.getCode()))+"'>Produktdetails</a></td>\n");
			}
		
			out.println("<td>" + XSSEncoder.encodeHTML(product.getDescription(ctx)) + "</td>");
			out.println("</tr>");
		}
		out.println("</table>");     
	}
	else
	{
		out.println("<br><b>Nothing found!</b><br>");
	}
%>

<%@include file="tail.inc"%>
