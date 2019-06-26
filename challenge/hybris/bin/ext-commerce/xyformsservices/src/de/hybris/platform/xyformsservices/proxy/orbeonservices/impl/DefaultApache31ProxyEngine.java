package de.hybris.platform.xyformsservices.proxy.orbeonservices.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.xyformsservices.proxy.orbeonservices.UrlRewriter;
import de.hybris.platform.xyformsservices.proxy.orbeonservices.UrlRewriterException;
import de.hybris.platform.xyformsservices.proxy.ProxyEngine;
import de.hybris.platform.xyformsservices.proxy.ProxyException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * {@link ProxyEngine} implementation for Orbeon. Used Apache HttpClient 3.1.
 * <p>
 *
 */
public class DefaultApache31ProxyEngine implements ProxyEngine
{
	private static final Logger LOG = Logger.getLogger(DefaultApache31ProxyEngine.class);

	protected static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();
	protected static final String ORBEON_PREFIX = "/orbeon";
	protected static final String ORBEON_PREFIX_PLUS_SLASH = ORBEON_PREFIX + "/";

	protected static final String POST_METHOD = "POST";
	protected static final String PUT_METHOD = "PUT";
	protected static final String GET_METHOD = "GET";

	protected static final String LOCATION_HEADER = "Location";
	protected static final String CONTENT_TYPE_HEADER = "Content-type";

	protected UrlRewriter urlRewriter;

	protected final Set<String> headersTo = new LinkedHashSet<>();
	protected final Set<String> headersFrom = new LinkedHashSet<>();

	private HttpClient httpClient;
	private ConfigurationService configurationService;

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

	@SuppressWarnings("deprecation")
	@Override
	public void proxy(final HttpServletRequest request, final HttpServletResponse response, final String namespace,
			final String url, final boolean forceGetMethod, final Map<String, String> headers) throws ProxyException
	{

		LOG.debug("Proxying URL [" + url + "]");
		LOG.debug(" ******** request.getRequestedSessionId(): " + request.getRequestedSessionId());

		String method = request.getMethod();
		if (forceGetMethod)
		{
			method = GET_METHOD;
		}

		final HttpRequestBase httpMethod;

		httpMethod = createMethod(url, method);

		if (headers != null)
		{
			for (final Map.Entry<String, String> key : headers.entrySet())
			{
				httpMethod.addHeader(key.getKey(), key.getValue());
			}
		}

		this.sendRequestHeaders(this.headersTo, request, httpMethod);

		try
		{
			// We copy the content, only for PUT and POST
			if (POST_METHOD.equals(method) || PUT_METHOD.equals(method))
			{
				// If debug is enabled and content type is a text file, we show the output
				if (!LOG.isDebugEnabled() || !isTextFile(request.getContentType()))
				{
					((HttpEntityEnclosingRequestBase) httpMethod).setEntity(new InputStreamEntity(request.getInputStream()));
				}
				else if (LOG.isDebugEnabled())
				{
					final String content = IOUtils.toString(request.getInputStream());
					LOG.debug("Proxying POST content: [" + content + "]");
					((HttpEntityEnclosingRequestBase) httpMethod).setEntity(new StringEntity(content));
				}
			}
			final HttpResponse httpResponse = getHttpClient().execute(httpMethod);
			response.setStatus(httpResponse.getStatusLine().getStatusCode());

			this.sendResponseHeaders(this.headersFrom, httpResponse.getAllHeaders(), response);

			// the location must also be rewritten.
			if (this.headersFrom.contains(LOCATION_HEADER) && httpResponse.getFirstHeader(LOCATION_HEADER) != null)
			{
				final Header location = httpResponse.getFirstHeader(LOCATION_HEADER);
				final String prefix = this.getPrefixFromRequest(request);
				final String newLocation = this.urlRewriter.rewrite(location.getValue(), prefix, namespace, false);

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
			final InputStream is = httpResponse.getEntity().getContent();

			final Header contentType = httpResponse.getFirstHeader(CONTENT_TYPE_HEADER);

			if ((contentType != null) && isTextFile(contentType.getValue()))
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
		catch (IOException | UrlRewriterException e)
		{
			LOG.warn("error on execute http request", e);
			throw new ProxyException(e);
		}
		finally
		{
			httpMethod.releaseConnection();
		}
	}

	private HttpRequestBase createMethod(final String url, final String method) throws ProxyException
	{
		final HttpRequestBase httpMethod;
		switch (method)
		{
			case GET_METHOD:
				httpMethod = new HttpGet(url);
				break;
			case PUT_METHOD:
				httpMethod = new HttpPut(url);
				break;
			case POST_METHOD:
			{
				httpMethod = new HttpPost(url);
				break;
			}
			default:
				throw new ProxyException("The method: '" + method + "' doesn't supported.");
		}
		return httpMethod;
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
			return getConfigurationService()
				.getConfiguration()
				.getString("xyformsservices.webroot.path");
		}
		return uri.substring(0, uri.indexOf("/", 1));
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
	 * @param httpMethod
	 *           httpMethod
	 */
	protected void sendRequestHeaders(final Set<String> headers, final HttpServletRequest request,
			final HttpRequestBase httpMethod)
	{
		LOG.debug("Request headers send to orbeon:");
		for (final String h : headers)
		{
			final String header = request.getHeader(h);
			if (header != null)
			{
				httpMethod.addHeader(h, header);
				LOG.debug("-- [" + h + ":" + header + "]");
			}
		}
	}

	/**
	 * Get headers from remote connection and copies them back to the current response.
	 *
	 * @param headers
	 *           headers that should be copied back
	 * @param responseHeaders
	 *           Remote response Headers
	 * @param response
	 *           Response to copy header values to.
	 */
	protected void sendResponseHeaders(final Set<String> headers, final Header[] responseHeaders,
			final HttpServletResponse response)
	{
		LOG.debug("Response headers coming from orbeon:");
		for (final Header header : responseHeaders)
		{
			if (headers.contains(header.getName()))
			{
				response.addHeader(header.getName(), header.getValue());
				LOG.debug("-- [" + header.getName() + ":" + header.getValue() + "]");
			}

		}
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

	protected HttpClient getHttpClient()
	{
		return httpClient;
	}

	@Required
	public void setHttpClient(final HttpClient httpClient)
	{
		this.httpClient = httpClient;
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
