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
package de.hybris.platform.acceleratorfacades.urlencoder.impl;

import de.hybris.platform.acceleratorfacades.urlencoder.UrlEncoderFacade;
import de.hybris.platform.acceleratorfacades.urlencoder.data.UrlEncoderData;
import de.hybris.platform.acceleratorservices.urlencoder.UrlEncoderService;
import de.hybris.platform.acceleratorservices.urlencoder.attributes.UrlEncodingAttributeManager;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Facade implementation for UrlEncoding attributes
 */
public class DefaultUrlEncoderFacade implements UrlEncoderFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultUrlEncoderFacade.class.getName());
	private static final String URL_ENCODING_DATA_ATTR = "urlEncodingData";
	private SessionService sessionService;
	private UrlEncoderService urlEncoderService;

	@Override
	public boolean isValid(final String attributeName, final String value)
	{
		final UrlEncodingAttributeManager attributeManager = getUrlEncoderService().getUrlEncodingAttrManagerMap().get(
				attributeName);
		if (attributeManager != null)
		{
			return attributeManager.isValid(value);
		}
		return false;
	}

	protected String extractEncodingPattern(final List<UrlEncoderData> urlEncodingAttributes)
	{
		String pattern = null;
		for (final UrlEncoderData urlEncoderData : urlEncodingAttributes)
		{
			pattern = StringUtils.isBlank(pattern) ? urlEncoderData.getCurrentValue() : pattern + "/"
					+ urlEncoderData.getCurrentValue();
		}
		writeDebugLog(" Encoding pattern after processing :  [" + pattern + "]");
		return pattern;
	}

	@Override
	public List<UrlEncoderData> getCurrentUrlEncodingData()
	{
		if (getSessionService().getAttribute(URL_ENCODING_DATA_ATTR) == null)
		{
			final Collection<String> urlEncodingAttributes = getUrlEncoderService().getEncodingAttributesForSite();
			final List<UrlEncoderData> urlEncoderDataList = new ArrayList<UrlEncoderData>(urlEncodingAttributes.size());

			for (final String attribute : urlEncodingAttributes)
			{
				final UrlEncodingAttributeManager attributeManager = getUrlEncoderService().getUrlEncodingAttrManagerMap().get(
						attribute);
				if (attributeManager != null)
				{
					final UrlEncoderData urlEncoderData = new UrlEncoderData();
					urlEncoderData.setAttributeName(attribute);
					urlEncoderData.setCurrentValue(attributeManager.getCurrentValue());
					urlEncoderData.setDefaultValue(attributeManager.getDefaultValue());
					urlEncoderDataList.add(urlEncoderData);
				}
			}
			getSessionService().setAttribute(URL_ENCODING_DATA_ATTR, urlEncoderDataList);
		}

		return getSessionService().getAttribute(URL_ENCODING_DATA_ATTR);
	}

	@Override
	public void updateSiteFromUrlEncodingData()
	{
		for (final UrlEncoderData urlEncoderData : getCurrentUrlEncodingData())
		{
			final UrlEncodingAttributeManager attributeManager = getUrlEncoderService().getUrlEncodingAttrManagerMap().get(
					urlEncoderData.getAttributeName());
			if (attributeManager != null)
			{
				attributeManager.updateAndSyncForAttrChange(urlEncoderData.getCurrentValue());
			}
		}
	}

	@Override
	public String calculateAndUpdateUrlEncodingData(final String uri, final String contextPath)
	{
		final List<UrlEncoderData> urlEncodingAttributes = getCurrentUrlEncodingData();
		final String[] splitUrl = StringUtils.split(uri, "/");
		int splitUrlCounter = ArrayUtils.isNotEmpty(splitUrl) && StringUtils.remove(contextPath, "/").equals(splitUrl[0]) ? 1 : 0;

		final StringBuilder patternSb = new StringBuilder();
		for (final UrlEncoderData urlEncoderData : urlEncodingAttributes)
		{
			String tempValue = urlEncoderData.getCurrentValue();
			if (splitUrlCounter < splitUrl.length)
			{
				tempValue = splitUrl[splitUrlCounter];
				if (!isValid(urlEncoderData.getAttributeName(), tempValue))
				{
					tempValue = urlEncoderData.getDefaultValue();
					writeDebugLog("Encoding attributes are absent. Injecting default value :  [" + tempValue + "]");
				}
				urlEncoderData.setCurrentValue(tempValue);
				splitUrlCounter++;
			}

			if (patternSb.length() != 0)
			{
				patternSb.append('/');
			}
			patternSb.append(tempValue);

		}
		return patternSb.toString();
	}

	protected void writeDebugLog(final String message)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(message);
		}
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected UrlEncoderService getUrlEncoderService()
	{
		return urlEncoderService;
	}

	@Required
	public void setUrlEncoderService(final UrlEncoderService urlEncoderService)
	{
		this.urlEncoderService = urlEncoderService;
	}
}
