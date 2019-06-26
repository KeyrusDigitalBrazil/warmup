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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;

import java.util.Date;


/**
 * Builder for {@link ProductConfigMessage} objects.<br>
 * <b>This builder is NOT Thread-Safe</b><br>
 * In case more than one object needs to be constructed with the same builder call
 * {@link ProductConfigMessageBuilder#reset()} before starting with the next object.<br>
 *
 */
public class ProductConfigMessageBuilder
{
	private ProductConfigMessageImpl message;

	public ProductConfigMessageBuilder()
	{
		message = new ProductConfigMessageImpl();
	}

	public ProductConfigMessageBuilder reset()
	{
		message = new ProductConfigMessageImpl();
		return this;
	}

	public ProductConfigMessageBuilder appendMessage(final String messageStr)
	{
		message.setMessage(messageStr);
		return this;
	}

	public ProductConfigMessageBuilder appendKey(final String key)
	{
		message.setKey(key);
		return this;
	}

	public ProductConfigMessageBuilder appendSeverity(final ProductConfigMessageSeverity severity)
	{
		message.setSeverity(severity);
		return this;
	}

	public ProductConfigMessageBuilder appendSource(final ProductConfigMessageSource source)
	{
		message.setSource(source);
		return this;
	}

	public ProductConfigMessageBuilder appendSubType(final ProductConfigMessageSourceSubType subType)
	{
		message.setSubType(subType);
		return this;
	}

	public ProductConfigMessageBuilder appendPromoType(final ProductConfigMessagePromoType promoType)
	{
		message.setPromoType(promoType);
		return this;
	}

	public ProductConfigMessageBuilder appendExtendedMessage(final String extendedMessage)
	{
		message.setExtendedMessage(extendedMessage);
		return this;
	}

	public ProductConfigMessageBuilder appendEndDate(final Date endDate)
	{
		message.setEndDate(endDate);
		return this;
	}

	public ProductConfigMessageBuilder appendBasicFields(final String messageStr, final String messageKey,
			final ProductConfigMessageSeverity severity)
	{
		return appendMessage(messageStr).appendKey(messageKey).appendSeverity(severity);
	}

	public ProductConfigMessageBuilder appendSourceAndType(final ProductConfigMessageSource source,
			final ProductConfigMessageSourceSubType subType)
	{
		return appendSource(source).appendSubType(subType);
	}

	public ProductConfigMessageBuilder appendPromotionFields(final ProductConfigMessagePromoType promoType,
			final String extendedMessage, final Date endDate)
	{
		return appendPromoType(promoType).appendExtendedMessage(extendedMessage).appendEndDate(endDate);
	}

	public ProductConfigMessage build()
	{
		final ProductConfigMessage result = message;
		message = null;
		return result;
	}
}
