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
package de.hybris.platform.chinesepspalipayservices.strategies.impl;

import de.hybris.platform.chinesepspalipayservices.alipay.AlipayConfiguration;
import de.hybris.platform.chinesepspalipayservices.alipay.AlipayService;
import de.hybris.platform.chinesepspalipayservices.data.AlipayCancelPaymentRequestData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayDirectPayRequestData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayPaymentStatusRequestData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawCancelPaymentResult;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawPaymentStatus;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRefundRequestData;
import de.hybris.platform.chinesepspalipayservices.exception.AlipayException;
import de.hybris.platform.chinesepspalipayservices.strategies.AlipayCreateRequestStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class DefaultAlipayCreateRequestStrategy implements AlipayCreateRequestStrategy
{

	private static final Logger LOG = Logger.getLogger(DefaultAlipayCreateRequestStrategy.class);

	private AlipayConfiguration alipayConfiguration;
	private AlipayService alipayService;
	
	@Override
	public String createDirectPayUrl(final AlipayDirectPayRequestData requestData) throws AlipayException
	{
		Map<String, String> payRequestMap;
		String generatedUrl = "";
		try
		{
			payRequestMap = describeRequest(requestData);
			generatedUrl = getAlipayService().generateUrl(payRequestMap, getAlipayConfiguration());
		}
		catch (ReflectiveOperationException | UnsupportedEncodingException e)
		{
			throw new AlipayException("Create eefund url failed", e);
		}
		return generatedUrl;
	}

	@Override
	public AlipayRawPaymentStatus submitPaymentStatusRequest(final AlipayPaymentStatusRequestData checkRequest)
			throws ReflectiveOperationException
	{
		final Map<String, String> alipayPaymentStatusRequestData = describeRequest(checkRequest);
		final String xmlString = getAlipayService().postRequest(alipayPaymentStatusRequestData, getAlipayConfiguration());
		if (StringUtils.isNotEmpty(xmlString))
		{
			final AlipayRawPaymentStatus alipayRawPaymentStatus = (AlipayRawPaymentStatus) parserXML(xmlString,
					"de.hybris.platform.chinesepspalipayservices.data.AlipayRawPaymentStatus");
			return alipayRawPaymentStatus;
		}
		return null;
	}

	@Override
	public AlipayRawCancelPaymentResult submitCancelPaymentRequest(final AlipayCancelPaymentRequestData closeRequest)
			throws ReflectiveOperationException
	{
		Map<String, String> alipayCancelPaymentRequestData;
		alipayCancelPaymentRequestData = describeRequest(closeRequest);
		final String xmlString = getAlipayService().postRequest(alipayCancelPaymentRequestData, getAlipayConfiguration());
		if (StringUtils.isNotEmpty(xmlString))
		{
			final AlipayRawCancelPaymentResult alipayRawCancelPaymentResult = (AlipayRawCancelPaymentResult) parserXML(xmlString,
					"de.hybris.platform.chinesepspalipayservices.data.AlipayRawCancelPaymentResult");
			return alipayRawCancelPaymentResult;
		}
		return null;
	}

	@Override
	public String createRefundUrl(final AlipayRefundRequestData refundData) throws AlipayException

	{
		Map<String, String> refundRequestMap;
		String generatedUrl = "";
		try
		{
			refundRequestMap = describeRequest(refundData);
			generatedUrl = getAlipayService().generateUrl(refundRequestMap, getAlipayConfiguration());
		}
		catch (ReflectiveOperationException | UnsupportedEncodingException e)
		{
			throw new AlipayException("Create Refund Url failed", e);
		}
		return generatedUrl;
	}

	protected Map<String, String> describeRequest(final Object bean) throws ReflectiveOperationException
	{
		final Map<String, String> describeMap = BeanUtils.describe(bean);
		describeMap.remove("class");
		describeMap.remove("quantity");
		final Map<String, String> alipayRequestMap = new HashMap<>();

		for (final Map.Entry<String, String> entry : describeMap.entrySet())
		{
			String alipayKey = covert2SnakeCase(entry.getKey());
			if ("input_charset".equalsIgnoreCase(alipayKey))
			{
				alipayKey = "_" + alipayKey;
			}
			if (StringUtils.isNotEmpty(entry.getValue()) && entry.getValue() != null)
			{
				alipayRequestMap.put(alipayKey, entry.getValue());
			}
			else
			{
				alipayRequestMap.put(alipayKey, "");
			}
		}

		return alipayRequestMap;
	}

	protected Object parserXML(final String xmlString, final String className)
	{
		try
		{

			final Class responseClass = Class.forName(className);
			final Object newResponse = responseClass.newInstance();
			final Map<String, String> responseMap = new HashMap<>();
			final SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(true);
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			final SAXParser saxParser = factory.newSAXParser();
			final DefaultHandler handler = new DefaultAlipayHandler(newResponse, responseMap);

			final InputStream in = IOUtils.toInputStream(xmlString, "UTF-8");
			saxParser.parse(in, handler);
			return newResponse;
		}
		catch (final SAXException | IOException | ParserConfigurationException e)// NOSONAR
		{
			LOG.error("Parse xml error");
		}
		catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e)// NOSONAR
		{
			LOG.error("New Instance error");
		}
		return null;

	}

	protected String covert2SnakeCase(final String camelCase)
	{
		final String regex = "([a-z])([A-Z])";
		final String replacement = "$1_$2";
		return camelCase.replaceAll(regex, replacement).toLowerCase(Locale.ENGLISH);
	}

	protected AlipayConfiguration getAlipayConfiguration()
	{
		return alipayConfiguration;
	}

	@Required
	public void setAlipayConfiguration(final AlipayConfiguration alipayConfiguration)
	{
		this.alipayConfiguration = alipayConfiguration;
	}

	protected AlipayService getAlipayService()
	{
		return alipayService;
	}

	@Required
	public void setAlipayService(final AlipayService alipayService)
	{
		this.alipayService = alipayService;
	}

	public static class DefaultAlipayHandler extends DefaultHandler
	{
		private String preTag = null;
		private final Object newResponse;
		private final Map<String, String> responseMap;

		public DefaultAlipayHandler(final Object newResponse, final Map<String, String> responseMap)
		{
			super();
			this.newResponse = newResponse;
			this.responseMap = responseMap;
		}

		@Override
		public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
				throws SAXException
		{
			preTag = qName;
		}

		@Override
		public void endElement(final String uri, final String localName, final String qName) throws SAXException
		{
			if ("alipay".equalsIgnoreCase(qName))
			{
				preTag = null;
				try
				{
					BeanUtils.populate(newResponse, responseMap);
				}
				catch (final IllegalAccessException | InvocationTargetException e)//NOSONAR
				{
					LOG.error("Convert xml to Bean error");
				}
			}

		}

		@Override
		public void characters(final char[] ch, final int start, final int length) throws SAXException
		{
			if (preTag != null)
			{
				final String content = new String(ch, start, length);
				String camelTag = WordUtils.capitalizeFully(preTag, new char[]
				{ '_' }).replaceAll("_", "");
				camelTag = WordUtils.uncapitalize(camelTag);

				if (camelTag != null && !camelTag.isEmpty() && !content.isEmpty() && !"\n".equalsIgnoreCase(content))
				{
					responseMap.put(camelTag, content);
				}
			}
		}

	}
}
