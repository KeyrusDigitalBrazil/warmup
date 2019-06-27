package de.hybris.platform.xyformsservices.proxy.orbeonservices.impl;

import de.hybris.platform.xyformsservices.proxy.orbeonservices.UrlRewriter;
import de.hybris.platform.xyformsservices.proxy.orbeonservices.UrlRewriterException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;


/**
 * Utils for modifying Orbeon content.
 */
public class DefaultUrlRewriter implements UrlRewriter
{
	private static final Logger LOG = Logger.getLogger(DefaultUrlRewriter.class);

	protected static final String BASE_TAG = "wsrp_rewrite";
	protected static final String START_TAG = BASE_TAG + "?";
	protected static final String END_TAG = "/" + BASE_TAG;

	protected static final int BASE_TAG_LENGTH = BASE_TAG.length();
	protected static final int END_TAG_LENGTH = END_TAG.length();

	protected static final String WSRP_URL_TYPE_PARAM = "wsrp-urlType";
	protected static final String WSRP_MODE_PARAM = "wsrp-mode";
	protected static final String WSRP_WINDOW_STATE_PARAM = "wsrp-windowState";
	protected static final String WSRP_NAVIGATIONAL_STATE_PARAM = "wsrp-navigationalState";

	protected static final String URL_TYPE_BLOCKING_ACTION = "blockingAction";
	protected static final String URL_TYPE_RENDER = "render";
	protected static final String URL_TYPE_RESOURCE = "resource";

	protected static final String PATH_PARAMETER_NAME = "orbeon.path";

	protected static final Pattern PATTERN_AMP;
	protected static final String STANDARD_PARAMETER_ENCODING = "utf-8";

	static
	{
		final String token = "[^=&]+";
		PATTERN_AMP = Pattern.compile("(" + token + ")=(" + token + ")?(?:&amp;|&|(?<!&amp;|&)\\z)");
	}

	@Override
	public String extractNamespace(final HttpServletRequest request)
	{
		String namespace = "";
		if (request.getParameter("namespace") != null)
		{
			namespace = request.getParameter("namespace");
		}
		return namespace;
	}

	@Override
	public String rewrite(final String content, final String prefix, final String namespace, final boolean encodeForXML)
			throws UrlRewriterException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Original Content: \n" + content);
		}
		final int stringLength = content.length();
		int currentIndex = 0;
		int index;
		final StringBuffer writer = new StringBuffer();
		while ((index = content.indexOf(BASE_TAG, currentIndex)) != -1)
		{
			// Write up to the current mark
			writer.append(content.substring(currentIndex, index));

			// Check if escaping is requested
			if (index + BASE_TAG_LENGTH * 2 <= stringLength
					&& content.substring(index + BASE_TAG_LENGTH, index + BASE_TAG_LENGTH * 2).equals(BASE_TAG))
			{
				// Write escaped tag, update index and keep looking
				writer.append(BASE_TAG);
				currentIndex = index + BASE_TAG_LENGTH * 2;
				continue;
			}

			if (index < stringLength - BASE_TAG_LENGTH && content.charAt(index + BASE_TAG_LENGTH) == '?')
			{
				// URL encoding
				// Find the matching end mark
				final int endIndex = content.indexOf(END_TAG, index);
				if (endIndex == -1)
				{
					throw new UrlRewriterException("Missing end tag for WSRP encoded URL.");
				}
				final String encodedURL = content.substring(index + START_TAG.length(), endIndex);

				currentIndex = endIndex + END_TAG_LENGTH;

				final String decodedURL = wsrpToURL(encodedURL, prefix, namespace);
				LOG.debug("Encoded URL [" + encodedURL + "]");
				LOG.debug("Decoded URL [" + decodedURL + "]");
				writer.append(encodeForXML ? escapeXMLMinimal(decodedURL) : decodedURL);
			}
			else if (index < stringLength - BASE_TAG_LENGTH && content.charAt(index + BASE_TAG_LENGTH) == '_')
			{
				if (namespace != null && !namespace.isEmpty())
				{
					writer.append(namespace + "_");
				}

				currentIndex = index + BASE_TAG_LENGTH + 1; // includes the trailing '_'
			}
			else
			{
				throw new UrlRewriterException("Invalid wsrp rewrite tagging.");
			}
		}
		// Write remainder of string
		if (currentIndex < stringLength)
		{
			writer.append(content.substring(currentIndex, content.length()));
		}

		final String res = writer.toString();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Rewritten Content: \n" + res);
		}
		return res;
	}

	protected String getStringFromObjectArray(final Object[] values)
	{
		if (values == null || values.length == 0 || !(values[0] instanceof String))
		{
			return null;
		}
		else
		{
			return (String) values[0];
		}
	}

	protected String wsrpToURL(final String encodedURL, final String prefix, final String namespace) throws UrlRewriterException
	{
		// Parse URL
		final Map<String, String[]> wsrpParameters = decodeQueryString(encodedURL);

		// Check URL type and create URL
		try
		{
			final String urlTypeValue = getStringFromObjectArray(wsrpParameters.get(WSRP_URL_TYPE_PARAM));
			if (urlTypeValue == null)
			{
				throw new UrlRewriterException("Missing URL type for WSRP encoded URL: " + encodedURL);
			}

			final String baseURL = prefix + "/orbeon";
			if (!URL_TYPE_RESOURCE.equals(urlTypeValue) && !URL_TYPE_BLOCKING_ACTION.equals(urlTypeValue)
					&& !URL_TYPE_RENDER.equals(urlTypeValue))
			{
				throw new UrlRewriterException("Invalid URL type for WSRP encoded URL: " + encodedURL);
			}

			// Get navigational state
			final Map<String, String[]> navigationParameters;
			final String navigationalStateValue = getStringFromObjectArray(wsrpParameters.get(WSRP_NAVIGATIONAL_STATE_PARAM));
			if (navigationalStateValue != null)
			{
				final String navigationalState = navigationalStateValue.startsWith("amp;") ? navigationalStateValue.substring(4)
						: navigationalStateValue;
				final String decodedNavigationalState = URLDecoder.decode(navigationalState, "utf-8");

				navigationParameters = decodeQueryString(decodedNavigationalState);
			}
			else
			{
				throw new UrlRewriterException("navigationalStateValue is not specified");
			}

			final String uid = namespace != null && !namespace.isEmpty() ? "?namespace=" + namespace : "";

			if (URL_TYPE_RESOURCE.equals(urlTypeValue) || URL_TYPE_RENDER.equals(urlTypeValue)
					|| URL_TYPE_BLOCKING_ACTION.equals(urlTypeValue))
			{
				final String resourcePath = navigationParameters.get(PATH_PARAMETER_NAME)[0];

				// Encode the other parameters directly into the resource id, as they are really part of the identity
				// of the resource and have nothing to do with the current render parameters.
				navigationParameters.remove(PATH_PARAMETER_NAME); // WARNING: mutate navigationParameters

				final String resource = URLDecoder.decode(resourcePath, "UTF-8");

				return baseURL + resource + uid;
			}
			else
			{
				return baseURL + uid;
			}

		}
		catch (final Exception e)
		{
			throw new UrlRewriterException(e);
		}
	}

	protected void addValueToStringArrayMap(final Map<String, String[]> map, final String name, final String value)
	{
		map.put(name, (String[]) ArrayUtils.add(map.get(name), value));
	}

	protected Map<String, String[]> decodeQueryString(final String queryString) throws UrlRewriterException
	{

		final Map<String, String[]> result = new LinkedHashMap<String, String[]>();
		if (queryString != null)
		{
			final Matcher matcher = PATTERN_AMP.matcher(queryString);
			int matcherEnd = 0;
			while (matcher.find())
			{
				matcherEnd = matcher.end();
				try
				{
					String name = URLDecoder.decode(matcher.group(1), STANDARD_PARAMETER_ENCODING);
					final String group2 = matcher.group(2);

					final String value = group2 != null ? URLDecoder.decode(group2, STANDARD_PARAMETER_ENCODING) : "";

					// Handle the case where the source contains &amp;amp; because of double escaping which does occur in
					// full Ajax updates!
					if (name.startsWith("amp;"))
					{
						name = name.substring(4);
					}

					// NOTE: Replace spaces with '+'. This is an artifact of the fact that URLEncoder/URLDecoder
					// are not fully reversible.
					addValueToStringArrayMap(result, name, value.replace(' ', '+'));
				}
				catch (final UnsupportedEncodingException e)
				{
					// Should not happen as we are using a required encoding
					throw new UrlRewriterException(e);
				}
			}
			if (queryString.length() != matcherEnd)
			{
				// There was garbage at the end of the query.
				throw new UrlRewriterException("Malformed URL: " + queryString);
			}
		}
		return result;
	}

	protected String escapeXMLMinimal(String str)
	{
		str = str.replace("&", "&amp;");
		str = str.replace("<", "&lt;");
		return str;
	}
}
