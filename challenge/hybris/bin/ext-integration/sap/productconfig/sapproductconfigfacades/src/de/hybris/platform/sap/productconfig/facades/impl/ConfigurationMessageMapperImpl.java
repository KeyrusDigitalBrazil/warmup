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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationMessageMapper;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageData;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageUISeverity;
import de.hybris.platform.sap.productconfig.facades.ValueFormatTranslator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.security.core.server.csi.XSSEncoder;


/**
 * Helper class to map the messages from product configuration(product level), cstic and cstic values
 */
public class ConfigurationMessageMapperImpl implements ConfigurationMessageMapper
{
	private static final Logger LOG = Logger.getLogger(ConfigurationMessageMapperImpl.class);
	private ValueFormatTranslator valueFormatTranslator;


	@Override
	public void mapMessagesFromModelToData(final CsticData cstciData, final CsticModel csticModel)
	{
		final Set<ProductConfigMessage> modelMessages = csticModel.getMessages();
		final List<ProductConfigMessageData> uiMessages = mapMessagesFromModelToData(modelMessages);
		uiMessages.sort(new ConfigurationMessageComparator());
		cstciData.setMessages(uiMessages);
	}

	@Override
	public void mapMessagesFromModelToData(final CsticValueData cstciValueData, final CsticValueModel csticValueModel)
	{
		final Set<ProductConfigMessage> modelMessages = csticValueModel.getMessages();
		final List<ProductConfigMessageData> uiMessages = mapMessagesFromModelToData(modelMessages);
		uiMessages.sort(new ConfigurationMessageComparator());
		cstciValueData.setMessages(uiMessages);
	}

	@Override
	public void mapMessagesFromModelToData(final ConfigurationData configData, final ConfigModel configModel)
	{
		final Set<ProductConfigMessage> modelMessages = configModel.getMessages();
		final List<ProductConfigMessageData> uiMessages = mapMessagesFromModelToData(modelMessages);
		configData.setMessages(uiMessages);
	}


	protected List<ProductConfigMessageData> mapMessagesFromModelToData(final Set<ProductConfigMessage> modelMessages)
	{
		final List<ProductConfigMessageData> uiMessages = new ArrayList(modelMessages.size());
		for (final ProductConfigMessage message : modelMessages)
		{
			final ProductConfigMessageData uiMessage = new ProductConfigMessageData();
			try
			{
				uiMessage.setMessage(encodeHTML(message.getMessage()));
				if (message.getExtendedMessage() != null)
				{
					uiMessage.setExtendedMessage(encodeHTML(message.getExtendedMessage()));
				}
			}
			catch (final UnsupportedEncodingException ex)
			{
				uiMessage.setMessage(null);
				LOG.warn("Message with key '" + message.getKey() + "' discarded due to unsupported encoding: " + ex.getMessage(), ex);
			}
			uiMessage.setSeverity(mapMessageSeverity(message));
			uiMessage.setPromoType(message.getPromoType());
			uiMessage.setEndDate(getValueFormatTranslator().formatDate(message.getEndDate()));
			uiMessages.add(uiMessage);
		}
		return uiMessages;
	}

	protected String encodeHTML(final String message) throws UnsupportedEncodingException
	{
		return XSSEncoder.encodeHTML(message);
	}

	protected ProductConfigMessageUISeverity mapMessageSeverity(final ProductConfigMessage message)
	{
		ProductConfigMessageUISeverity severity;
		if (ProductConfigMessageSeverity.WARNING.equals(message.getSeverity()))
		{
			// WARNING -> INFO (there is no warning level in the UI)
			severity = ProductConfigMessageUISeverity.INFO;
		}
		else if (ProductConfigMessageSeverity.INFO.equals(message.getSeverity()))
		{
			// INFO -> CONFIG
			severity = ProductConfigMessageUISeverity.CONFIG;
		}
		else
		{
			// for example ERROR -> ERROR
			severity = ProductConfigMessageUISeverity.valueOf(message.getSeverity().toString());
		}
		return severity;
	}

	/**
	 * @return the valueFormatTranslator
	 */
	public ValueFormatTranslator getValueFormatTranslator()
	{
		return valueFormatTranslator;
	}

	/**
	 * @param valueFormatTranslator
	 *           the valueFormatTranslator to set
	 */
	@Required
	public void setValueFormatTranslator(final ValueFormatTranslator valueFormatTranslator)
	{
		this.valueFormatTranslator = valueFormatTranslator;
	}


}
