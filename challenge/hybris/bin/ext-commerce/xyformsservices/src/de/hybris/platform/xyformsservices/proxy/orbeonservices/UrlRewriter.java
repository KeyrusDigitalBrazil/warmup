package de.hybris.platform.xyformsservices.proxy.orbeonservices;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;


/**
 * Rewrite resource addresses inside text files
 */
public interface UrlRewriter
{
	/**
	 * Rewrites resource addresses
	 * 
	 * @param content
	 *           Content to be parsed for resource URLs
	 * @param encodeForXML
	 *           If XML should be encoded (&amp; as &amp;amp;)
	 * @param namespace
	 *           Prefix used for html id's
	 * @param prefix
	 *           Prefix to be prepended to URLs
	 * @throws IOException
	 */
	public String rewrite(final String content, final String prefix, final String namespace, final boolean encodeForXML)
			throws UrlRewriterException;

	/**
	 * Extracts the namespace coming from client.
	 * 
	 * @param request
	 */
	public String extractNamespace(final HttpServletRequest request);
}
