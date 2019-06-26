package de.hybris.platform.xyformsservices.proxy.orbeonservices.impl;

import org.apache.commons.lang.StringUtils;


/**
 * Util class to detect content types.
 */
public class XmlUtil
{
	public static final String TEXT_CONTENT_TYPE_PREFIX = "text/";
	public static final String XML_CONTENT_TYPE1 = "text/xml";
	public static final String XML_CONTENT_TYPE2 = "application/xml";
	public static final String XML_CONTENT_TYPE3_SUFFIX = "+xml";
	public static final String XML_CONTENT_TYPE = XML_CONTENT_TYPE2;

	public static boolean isTextOrJSONContentType(final String contentType)
	{
		return contentType != null && (isTextContentType(contentType) || contentType.startsWith("application/json"));
	}

	public static boolean isTextContentType(final String contentType)
	{
		return contentType != null && contentType.startsWith(TEXT_CONTENT_TYPE_PREFIX);
	}

	public static boolean isXMLMediatype(final String mediatype)
	{
		return mediatype != null
				&& (mediatype.equals(XML_CONTENT_TYPE1) || mediatype.equals(XML_CONTENT_TYPE2) || mediatype
				.endsWith(XML_CONTENT_TYPE3_SUFFIX));
	}

	public static String getContentTypeMediaType(String contentType)
	{
		contentType = StringUtils.trimToNull(contentType);
		if (contentType == null)
		{
			return null;
		}

		final int semicolonIndex = contentType.indexOf(";");
		if (semicolonIndex == -1)
		{
			return contentType;
		}

		final String mediatype = StringUtils.trimToNull(contentType.substring(0, semicolonIndex));
		if (mediatype == null || mediatype.equalsIgnoreCase("content/unknown"))
		{
			return null;
		}
		else
		{
			return mediatype;
		}
	}
}
