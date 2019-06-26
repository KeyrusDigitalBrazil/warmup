/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.chinesepspwechatpayservices.wechatpay;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * Wrapper of parameters required to call Wechat API
 */
public class WeChatPayParameters
{
	private static final String NONCE_STRING_CHAR_SET = "0123456789abcdefghijklmnopqrstuvwxyz";
	private static final int NONCE_STRING_LENGTH = 32;

	private final SortedMap<String, String> parameters;

	public WeChatPayParameters()
	{
		this.parameters = new TreeMap<>();
	}

	/**
	 * Add a parameter, will be ignored if the name or value is empty
	 *
	 * @param name
	 *           Parameter name
	 * @param value
	 *           Parameter value
	 */
	public void add(final String name, final String value)
	{
		if (StringUtils.isNotEmpty(value) && StringUtils.isNotEmpty(name))
		{
			parameters.put(name, value);
		}
	}

	/**
	 * Generate a query string from the parameters, e.g. a=1&b=2&c=3
	 *
	 * @return the query string
	 *
	 */
	public String generateQueryString()
	{
		return parameters.keySet().stream().map(k -> k + "=" + parameters.get(k)).collect(Collectors.joining("&"));
	}

	/**
	 * Generate the parameters in XML format, e.g. <xml><a>1</a><b>2</b><c>3</c></xml>
	 *
	 * @return the parameters in XML format
	 */
	public String generateXml()
	{
		return parameters.keySet().stream().map(k -> "<" + k + ">" + parameters.get(k) + "</" + k + ">")
				.collect(Collectors.joining("", "<xml>", "</xml>"));
	}

	/**
	 * Generate the complete URL for get request, e.g. http://www.XXX.com?a=1&b=2&c=3
	 *
	 * @param base
	 *           base URL
	 *
	 * @return the complete URL
	 */
	public String generateGetURL(final String base)
	{
		return FilenameUtils.normalize(base + "?" + generateQueryString());
	}

	/**
	 * Generate a signature
	 *
	 * @param key
	 *           key used to calculate signature
	 *
	 * @return the signature
	 */
	public String generateSignature(final String key)
	{
		final String query = generateQueryString() + "&key=" + key;
		return DigestUtils.md5Hex(query).toUpperCase(Locale.ENGLISH);
	}

	/**
	 * Generate a random string
	 *
	 * @return the random string
	 */
	public String generateNonce()
	{
		final SecureRandom rnd = new SecureRandom();
		final StringBuilder sb = new StringBuilder(NONCE_STRING_LENGTH);
		for (int i = 0; i < NONCE_STRING_LENGTH; i++)
		{
			sb.append(NONCE_STRING_CHAR_SET.charAt(rnd.nextInt(NONCE_STRING_CHAR_SET.length())));
		}
		return sb.toString();
	}

	/**
	 * @return the parameters
	 */
	public Map<String, String> getParameters()
	{
		return parameters;
	}
}
