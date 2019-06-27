package de.hybris.platform.xyformsservices.proxy.orbeonservices.impl;

import de.hybris.platform.xyformsservices.proxy.orbeonservices.CookieManager;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;

 
/**
 * Orbeon specific implementation for {@link CookieManager}
 * <p>
 * Since the class has session scope, only one instance of this class is attached to a client.
 */

@Scope("session")
public class DefaultCookieManager implements CookieManager
{
	private static final Logger LOG = Logger.getLogger(DefaultCookieManager.class);

	private final CookieStore cookieStore = new BasicCookieStore();

	@Override
	public void processRequest(final HttpURLConnection conn, final String url) throws URISyntaxException
	{
		final BrowserCompatSpec cookieSpec = new BrowserCompatSpec(); // because not thread-safe
		final CookieOrigin cookieOrigin = getCookieOrigin(url);

		this.cookieStore.clearExpired(new Date());

		final List<Cookie> relevantCookies = new ArrayList<>();
		for (final Cookie cookie : this.cookieStore.getCookies())
		{
			if (cookieSpec.match(cookie, cookieOrigin))
			{
				relevantCookies.add(cookie);
			}
		}

		// NOTE: BrowserCompatSpec always only return a single Cookie header
		if (relevantCookies.size() > 0)
		{
			final List<Header> headers = cookieSpec.formatCookies(relevantCookies);
			for (final Header h : headers)
			{
				LOG.debug("Cookie:[" + h.getName() + "][" + h.getValue() + "]");
				conn.setRequestProperty(h.getName(), h.getValue());
			}
		}
	}

	@Override
	public void processResponse(final HttpURLConnection conn, final String url) throws URISyntaxException,
			MalformedCookieException
	{
		final BrowserCompatSpec cookieSpec = new BrowserCompatSpec(); // because not thread-safe
		final CookieOrigin cookieOrigin = getCookieOrigin(url);

		for (final Map.Entry<String, List<String>> e : conn.getHeaderFields().entrySet())
		{
			final String name = e.getKey();
			if (name != null && "set-cookie".equals(name.toLowerCase()))
			{
				final List<String> value = e.getValue();
				for (final String s : value)
				{
					LOG.debug("Setting Cookie:[" + name + "][" + s + "]");
					final List<Cookie> cookies = cookieSpec.parse(new BasicHeader(name, s), cookieOrigin);
					for (final Cookie c : cookies)
					{
						this.cookieStore.addCookie(c);
					}
				}
			}
		}
	}

	public CookieOrigin getCookieOrigin(final String url) throws URISyntaxException
	{
		final URI uri = new URI(url);
		final int defaultPort = "https".equals(uri.getScheme()) ? 443 : 80;
		final int effectivePort = uri.getPort() < 0 ? defaultPort : uri.getPort();
		return new CookieOrigin(uri.getHost(), effectivePort, uri.getPath(), "https".equals(uri.getScheme()));
	}
}
