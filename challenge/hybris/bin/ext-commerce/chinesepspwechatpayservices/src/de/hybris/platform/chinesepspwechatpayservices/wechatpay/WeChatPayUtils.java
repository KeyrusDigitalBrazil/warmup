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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.WordUtils;


/**
 * Utilities functions
 */
public class WeChatPayUtils
{

	/**
	 *
	 */
	private WeChatPayUtils()
	{
		throw new IllegalAccessError("WeChatPayUtils class");
	}

	public static Map<String, String> convertKey2CamelCase(final Map<String, String> snakeCaseMap)
	{
		final Map<String, String> camelCaseMap = new LinkedHashMap<>();
		for (final Map.Entry<String, String> entry : snakeCaseMap.entrySet())
		{
			final String value = entry.getValue();
			final String key = entry.getKey();
			String camelKey = WordUtils.capitalizeFully(key, new char[]
			{ '_' }).replaceAll("_", "");
			camelKey = WordUtils.uncapitalize(camelKey);
			camelCaseMap.put(camelKey, value);
		}
		return camelCaseMap;
	}
}
