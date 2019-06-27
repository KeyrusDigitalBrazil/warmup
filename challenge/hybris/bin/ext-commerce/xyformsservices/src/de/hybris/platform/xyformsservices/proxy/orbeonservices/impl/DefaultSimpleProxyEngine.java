package de.hybris.platform.xyformsservices.proxy.orbeonservices.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.xyformsservices.proxy.orbeonservices.CookieManager;
import de.hybris.platform.xyformsservices.proxy.orbeonservices.UrlRewriter;
import de.hybris.platform.xyformsservices.proxy.ProxyEngine;
import de.hybris.platform.xyformsservices.proxy.ProxyException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * {@link ProxyEngine} implementation for Orbeon.
 * <p>
 * It uses a {@link CookieManager} to mantain orbeon's session.
 */
public class DefaultSimpleProxyEngine implements ProxyEngine
{
	private static final Logger LOG = Logger.getLogger(DefaultSimpleProxyEngine.class);

	protected static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();
	protected static final String ORBEON_PREFIX = "/orbeon";
	protected static final String ORBEON_PREFIX_PLUS_SLASH = ORBEON_PREFIX + "/";

	protected static final String POST_METHOD = "POST";
	protected static final String PUT_METHOD = "PUT";

	protected static final String LOCATION_HEADER = "Location";

	protected CookieManager cookieManager;
	protected UrlRewriter urlRewriter;
	private ConfigurationService configurationService;

	protected final Set<String> headersTo = new LinkedHashSet<>();
	protected final Set<String> headersFrom = new LinkedHashSet<>();

	/**
	 * Extracts the namespace coming from client.
	 *
	 * @param request
	 */
	@Override
	public String extractNamespace(final HttpServletRequest request)
	{
		return this.urlRewriter.extractNamespace(request);
	}

	@Override
	public void proxy(final HttpServletRequest request, final HttpServletResponse response, final String namespace,
			final String url, final boolean forceGetMethod, final Map<String, String> headers) throws ProxyException
	{
		LOG.debug("Proxying URL [" + url + "]");
		String method = request.getMethod();
		if (forceGetMethod)
		{
			method = "GET";
		}

		HttpURLConnection conn = null;
		try
		{
			conn = this.connect(request, url, method, headers == null ? new HashMap<String, String>() : headers);

			final int rc = conn.getResponseCode();
			LOG.debug("Setting Response Status Code: " + rc);
			response.setStatus(rc);

			if (this.isServerError(rc))
			{
				if (conn.getErrorStream() == null)
				{
					LOG.debug("No error stream");
					return;
				}

				if (!isTextFile(conn.getContentType()))
				{
					IOUtils.copy(conn.getErrorStream(), response.getOutputStream());
					return;
				}

				// gets the prefix to be prepended to WSRP addresses, for instance: /yacceleratorstorefront
				final String prefix = this.getPrefixFromRequest(request);
				final String s = IOUtils.toString(conn.getErrorStream(), DEFAULT_CHARSET);
				final String s1 = this.urlRewriter.rewrite(s, prefix, namespace, false);
				IOUtils.write(s1, response.getOutputStream(), DEFAULT_CHARSET);

				return;
			}

			final InputStream is = conn.getInputStream();

			this.sendResponseHeaders(this.headersFrom, conn, response);

			// the location must also be rewritten.
			if (this.headersFrom.contains(LOCATION_HEADER) && conn.getHeaderField(LOCATION_HEADER) != null)
			{
				final String location = conn.getHeaderField(LOCATION_HEADER);
				final String prefix = this.getPrefixFromRequest(request);
				final String newLocation = this.urlRewriter.rewrite(location, prefix, namespace, false);

				// first time in the string.
				final String uriPart = newLocation
						.substring(newLocation.indexOf(ORBEON_PREFIX_PLUS_SLASH) + ORBEON_PREFIX_PLUS_SLASH.length());

				final String hostPart = this.getHostPart(request.getRequestURL().toString());

				final String newURL = hostPart + uriPart;
				LOG.debug("Location rewritten: [" + newURL + "]");
				response.setHeader(LOCATION_HEADER, newURL);
				return;
			}

			// WSRP tags must be rewritten for text files
			if (isTextFile(conn.getContentType()))
			{
				// Read content
				final String content = IOUtils.toString(is, DEFAULT_CHARSET);

				// Rewrite and send
				final boolean escape = POST_METHOD.equals(method) || PUT_METHOD.equals(method);

				// gets the prefix to be prepended to WSRP addresses, for instance: /yacceleratorstorefront
				final String prefix = this.getPrefixFromRequest(request);

				// rewrite WSRP content
				final String res = this.urlRewriter.rewrite(content, prefix, namespace, escape);

				// The content is sent back to the client.
				IOUtils.write(res, response.getOutputStream(), DEFAULT_CHARSET);
			}
			else
			{
				// Simply forward content
				IOUtils.copy(is, response.getOutputStream());
			}
		}
		catch (final SocketTimeoutException e)
		{
			response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			throw new ProxyException(e);
		}
		catch (final Exception e)
		{
			throw new ProxyException(e);
		}
		finally
		{
			if (conn != null)
			{
				// We assume that conn.getInputStream() has been also closed.
				conn.disconnect();
			}
		}
	}

	/**
	 * Gets the host part of an URL, including protocol, host, port with no trailing slash.
	 *
	 * @param url
	 */
	protected String getHostPart(final String url)
	{
		return url.substring(0, url.indexOf("/", url.indexOf("//") + 3));
	}

	/**
	 * Gets the prefix from the request URI address.
	 *
	 * @param request
	 */
	protected String getPrefixFromRequest(final HttpServletRequest request)
	{
		final String uri = request.getRequestURI();
		final boolean webrootOverride = getConfigurationService()
			.getConfiguration()
			.getBoolean("xyformsservices.webroot.override");
		if (webrootOverride) {
			final String webrootAppendix = getConfigurationService()
				.getConfiguration()
				.getString("xyformsservices.webroot.path");
			return getHostPart(uri) + webrootAppendix;
		}
		return uri.substring(0, uri.indexOf("/", 1));
	}

	/**
	 * Stablishes an HTTP connection with the given URL.
	 *
	 * @param request
	 *           Request to get headers from.
	 * @param url
	 *           HTTP Address to be connected.
	 * @param method
	 *           request method for orbeon
	 * @throws URISyntaxException
	 *            If the URI is not well formed
	 * @throws MalformedURLException
	 *            If the URL is not well formed
	 * @throws IOException
	 *            If there is a connection problem with the remote server.
	 * @throws MalformedCookieException
	 *            If there is a problem when dealing with Cookies
	 */
	protected HttpURLConnection connect(final HttpServletRequest request, final String url, final String method,
			final Map<String, String> headers)
					throws URISyntaxException, MalformedURLException, IOException, MalformedCookieException
	{
		final URL newURL = new URL(url);
		final HttpURLConnection conn = (HttpURLConnection) newURL.openConnection();

		conn.setInstanceFollowRedirects(false);
		conn.setDoOutput(true);
		conn.setDoInput(true);

		LOG.debug("Request method: " + method);
		conn.setRequestMethod(method);

		// We copy the given headers to the current connection
		for (final Map.Entry<String, String> e : headers.entrySet())
		{
			conn.addRequestProperty(e.getKey(), e.getValue());
		}

		// Request headers need to be propagated
		this.sendRequestHeaders(this.headersTo, request, conn);

		// Process request cookies
		this.cookieManager.processRequest(conn, url);

		// Connection is made
		conn.connect();

		// We copy the content, only for PUT and POST
		if (POST_METHOD.equals(method) || PUT_METHOD.equals(method))
		{
			// If debug is enabled and content type is a text file, we show the output
			if (!LOG.isDebugEnabled() || !isTextFile(request.getContentType()))
			{
				IOUtils.copy(request.getInputStream(), conn.getOutputStream());
			}
			else if (LOG.isDebugEnabled())
			{
				final String content = IOUtils.toString(request.getInputStream());
				LOG.debug("Proxying POST content: [" + content + "]");
				IOUtils.write(content, conn.getOutputStream());
			}
		}

		final int rc = conn.getResponseCode();
		if (this.isServerError(rc))
		{
			return conn;
		}
		// Process cookies comming from connection
		this.cookieManager.processResponse(conn, url);

		return conn;
	}

	/**
	 * True if the status code represents an error on the server side
	 *
	 * @param sc
	 */
	protected boolean isServerError(final int sc)
	{
		return sc >= 400 && sc <= 499 || // Client-Error
				sc >= 500 && sc <= 599; // Server-Error
	}

	/**
	 * Returns true if the given contentType refers to a text file.
	 *
	 * @param contentType
	 */
	protected boolean isTextFile(final String contentType)
	{
		final String mediaType = XmlUtil.getContentTypeMediaType(contentType);
		return XmlUtil.isTextOrJSONContentType(mediaType) || XmlUtil.isXMLMediatype(mediaType);
	}

	/**
	 * Sends headers from the request to the remote server.
	 *
	 * @param headers
	 *           headers that should be sent
	 * @param request
	 *           Request to take header values from.
	 * @param conn
	 *           Remote server connection
	 */
	protected void sendRequestHeaders(final Set<String> headers, final HttpServletRequest request, final HttpURLConnection conn)
	{
		LOG.debug("Request headers send to orbeon:");
		for (final String h : headers)
		{
			final String header = request.getHeader(h);
			if (header != null)
			{
				conn.setRequestProperty(h, header);
				LOG.debug("-- [" + h + ":" + header + "]");
			}
		}
	}

	/**
	 * Get headers from remote connection and copies them back to the current response.
	 *
	 * @param headers
	 *           headers that should be copied back
	 * @param conn
	 *           Remote server connection
	 * @param response
	 *           Response to copy header values to.
	 */
	protected void sendResponseHeaders(final Set<String> headers, final HttpURLConnection conn, final HttpServletResponse response)
	{
		LOG.debug("Response headers coming from orbeon:");
		for (final String h : headers)
		{
			final String header = conn.getHeaderField(h);
			if (header != null)
			{
				response.addHeader(h, header);
				LOG.debug("-- [" + h + ":" + header + "]");
			}
		}
	}

	@Required
	public void setCookieManager(final CookieManager cookieManager)
	{
		this.cookieManager = cookieManager;
	}

	@Required
	public void setUrlRewriter(final UrlRewriter urlRewriter)
	{
		this.urlRewriter = urlRewriter;
	}

	@Required
	public void setHeadersTo(final List<String> headersTo)
	{
		this.headersTo.clear();
		this.headersTo.addAll(headersTo);
	}

	@Required
	public void setHeadersFrom(final List<String> headersFrom)
	{
		this.headersFrom.clear();
		this.headersFrom.addAll(headersFrom);
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}