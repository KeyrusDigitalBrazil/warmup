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
 package de.hybris.platform.chinesepspalipayservices.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;


/**
 * Deals with alipay related request
 */
public interface AlipayService
{

	/**
	 * Generates alipay url
	 *
	 * @param sParaTemp
	 *           parameters for generating url
	 * @param alipayConfig
	 *           alipay configuration
	 * @return alipay url
	 * @throws UnsupportedEncodingException
	 *            throw UnsupportedEncodingException when parameters contain unsupported encoding chars
	 */
	String generateUrl(Map<String, String> sParaTemp, AlipayConfiguration alipayConfig) throws UnsupportedEncodingException;

	/**
	 * Simulates the http post request, uses this method to generate the XML response from alipay
	 *
	 * @param sParaTemp
	 *           request parameters
	 * @param alipayConfig
	 *           alipay configuration
	 * @return XML response from alipay
	 */
	String postRequest(Map<String, String> sParaTemp, AlipayConfiguration alipayConfig);

	/**
	 * Filters signature parameters
	 *
	 * @param sArray
	 *           the request parameters
	 * @return filtered parameters
	 */
	Map<String, String> paraFilter(final Map<String, String> sArray);

	/**
	 * Creates signature
	 *
	 * @param sArray
	 *           the request parameters
	 * @param key
	 *           private key
	 * @param signType
	 *           signature type
	 * @return signature
	 */
	String buildMysign(Map<String, String> sArray, String key, String signType);

	/**
	 * Encrypted by signature type
	 *
	 * @param signType
	 *           signature type
	 * @param preStr
	 *           original string
	 * @return encrypted string
	 */
	default String encrypt(final String signType, final String preStr){
		if ("MD5".equalsIgnoreCase(signType))
		{
			return DigestUtils.md5Hex(preStr);
		}
		return "";
	}

	/**
	 * Encodes alipay link url
	 *
	 * @param params
	 *           the parameters used for encoding url
	 * @param charset
	 *           the charset used for encoding url
	 * @return encoded url
	 * @throws UnsupportedEncodingException
	 *            throw UnsupportedEncodingException when parameters contain unsupported encoding chars
	 */
	default String createLinkString(final Map<String, String> params, final String... charset)
			throws UnsupportedEncodingException
	{
		final String defaultCharset = "UTF-8";
		final List<String> keys = new ArrayList<>(params.keySet());
		final int maxDataLength = 256;
		final int maxBufferLength = 2048;
		Collections.sort(keys);
		
		final StringBuilder prestr = new StringBuilder(maxBufferLength);

		for (int i = 0; i < keys.size(); i++)
		{
			final String key = keys.get(i);
			final String value = URLEncoder.encode(params.get(key), charset.length > 0 ? charset[0] : defaultCharset);
			if (i == keys.size() - 1)
			{
				if (key.length() < maxDataLength)
				{
					prestr.append(key).append("=").append(value);
				}
			}
			else
			{
				if (key.length() < maxDataLength)
				{
					prestr.append(key).append("=").append(value).append("&");
				}
			}
		}

		return prestr.toString();
	}


}
