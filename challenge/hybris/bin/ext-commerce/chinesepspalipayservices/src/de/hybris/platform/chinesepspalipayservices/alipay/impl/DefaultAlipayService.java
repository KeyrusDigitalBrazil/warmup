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
package de.hybris.platform.chinesepspalipayservices.alipay.impl;

import de.hybris.platform.chinesepspalipayservices.alipay.AlipayConfiguration;
import de.hybris.platform.chinesepspalipayservices.alipay.AlipayService;
import de.hybris.platform.chinesepspalipayservices.alipay.HttpProtocolHandler;
import de.hybris.platform.chinesepspalipayservices.constants.PaymentConstants;
import de.hybris.platform.chinesepspalipayservices.data.HttpRequest;
import de.hybris.platform.chinesepspalipayservices.data.HttpResponse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link AlipayService}
 * 
 */
public class DefaultAlipayService implements AlipayService
{
	private String[] unAllowedParams;
	private static final Logger LOG = Logger.getLogger(DefaultAlipayService.class);

	@Override
	public String generateUrl(final Map<String, String> sParaTemp, final AlipayConfiguration alipayConfig)
	{
		final StringBuilder strResult = new StringBuilder();
		try
		{
			strResult.append(alipayConfig.getWebGateway());
			final Map<String, String> sPara = buildRequestPara(sParaTemp, alipayConfig.getWebKey(), alipayConfig.getSignType());
			strResult.append(createLinkString(sPara));
		}
		catch (final UnsupportedEncodingException e)
		{
			LOG.error("UnsupportedEncodingException occured while decoding the cookie", e);
		}
		return strResult.toString();
	}

	@Override
	public String postRequest(final Map<String, String> sParaTemp, final AlipayConfiguration alipayConfig)
	{
		final Map<String, String> sPara = buildRequestPara(sParaTemp, alipayConfig.getWebKey(), alipayConfig.getSignType());
		final HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
		final HttpRequest request = new HttpRequest();
		request.setCharset(PaymentConstants.Basic.INPUT_CHARSET);
		request.setParameters(generatNameValuePairList(sPara));
		request.setUrl(alipayConfig.getWebGateway() + "_input_charset=" + PaymentConstants.Basic.INPUT_CHARSET);
		request.setMethod(PaymentConstants.HTTP.METHOD_POST);
		final HttpResponse response = httpProtocolHandler.execute(request);
		if (response == null)
		{
			return null;
		}
		final String strResult = response.getStringResult();
		return strResult;
	}

	@Override
	public String buildMysign(final Map<String, String> sArray, final String key, final String signType)
	{
		String prestr;
		try
		{
			prestr = createLinkString(sArray);
		}
		catch (final UnsupportedEncodingException e)
		{
			LOG.error("UnsupportedEncodingException occured while decoding the cookie", e);
			return "";
		}
		prestr = prestr + key;
		return encrypt(signType, prestr);
	}

	@Override
	public Map<String, String> paraFilter(final Map<String, String> sArray)
	{
		final Map<String, String> result = new HashMap<>();
		if (MapUtils.isEmpty(sArray))
		{
			return result;
		}

		for (final Map.Entry<String, String> entry : sArray.entrySet())
		{
			final String value = entry.getValue();
			final String key = entry.getKey();
			if (StringUtils.isNoneEmpty(value) && !ArrayUtils.contains(getUnAllowedParams(), key))
			{
				result.put(key, value);
			}
		}
		return result;
	}

	protected Map<String, String> buildRequestPara(final Map<String, String> sParaTemp, final String key,
			final String signType)
	{
		final Map<String, String> sPara = paraFilter(sParaTemp);
		final String mysign = buildMysign(sPara, key, signType);
		sPara.put("sign", mysign);
		sPara.put("sign_type", signType);
		return sPara;
	}


	protected List<NameValuePair> generatNameValuePairList(final Map<String, String> properties)
	{

		final List<NameValuePair> nameValuePairList = new ArrayList<>();


		for (final Map.Entry<String, String> entry : properties.entrySet())
		{

			nameValuePairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		return nameValuePairList;
	}

	protected String[] getUnAllowedParams()
	{
		return unAllowedParams;
	}

	@Required
	public void setUnAllowedParams(final String[] unAllowedParams)
	{
		this.unAllowedParams = unAllowedParams;
	}


}
