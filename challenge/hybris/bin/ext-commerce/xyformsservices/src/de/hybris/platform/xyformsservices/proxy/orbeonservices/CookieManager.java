
package de.hybris.platform.xyformsservices.proxy.orbeonservices;

import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import org.apache.http.cookie.MalformedCookieException;


/**
 * Cookie Manager that helps proxying content from another web application
 */
public interface CookieManager
{
	/**
	 * Copies the stored cookies to the given http connection. If no previous manager exists one will be created and
	 * stored as part of the current session.
	 * 
	 * @param conn
	 *           Http Connection to copy the cookies to
	 * @param url
	 *           URL to be accessed.
	 * @throws URISyntaxException
	 */
	public void processRequest(final HttpURLConnection conn, final String url) throws URISyntaxException;

	/**
	 * Returning cookies coming from the proxied connection are stored locally, for instance: Set-Cookie.
	 * 
	 * @param conn
	 *           Http Connection to copy the cookies from
	 * @param url
	 *           URL that has been accessed
	 * @throws URISyntaxException
	 * @throws MalformedCookieException
	 */
	public void processResponse(final HttpURLConnection conn, final String url) throws URISyntaxException,
			MalformedCookieException;
}
