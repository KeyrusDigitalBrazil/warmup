/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sappricingbol.converter;

import java.util.HashMap;
import java.util.Map;


public class CodePageUtils
{
	private CodePageUtils()
	{
		//private constructor to hide public.
	}

	private static Map<String, String> initCodeMapJavaLangToSapLang()
	{
		Map<String, String> hashmap = new HashMap<String, String>();
		hashmap.put("IW", "HE");
		hashmap.put("ZH_CN", "ZH");
		hashmap.put("ZH_TW", "ZF");
		return hashmap;
	}

	public static String getSapLangForJavaLanguage(String s)
	{
		s = s.toUpperCase();
		String s1 = (String) codeMapJavaLang.get(s);
		if (s1 == null)
		{
			s1 = s;
		}
		return s1;
	}

	private static Map<String, String> codeMapJavaLang = initCodeMapJavaLangToSapLang();

}
