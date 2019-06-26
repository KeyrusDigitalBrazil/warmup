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
package de.hybris.platform.integration.cis.tax.service.impl;


import de.hybris.platform.commerceservices.externaltax.CalculateExternalTaxesStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.externaltax.ExternalTaxDocument;
import de.hybris.platform.integration.cis.tax.CisTaxDocOrder;
import de.hybris.platform.integration.cis.tax.service.CisTaxCalculationService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;

import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisOrder;
import com.hybris.cis.client.tax.models.CisTaxDoc;
import com.hybris.cis.service.CisClientTaxService;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public class DefaultCisTaxCalculationService implements CisTaxCalculationService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCisTaxCalculationService.class);
	private Converter<AbstractOrderModel, CisOrder> cisOrderConverter;
	private CisClientTaxService cisClientTaxService;
	private ConfigurationService configurationService;
	private Converter<CisTaxDocOrder, ExternalTaxDocument> externalTaxDocumentConverter;
	private CalculateExternalTaxesStrategy calculateExternalTaxesFallbackStrategy;
	private Converter<AddressModel, CisAddress> cisAddressConverter;
	private SessionService sessionService;

	private String tenantId;

	@Override
	public ExternalTaxDocument calculateExternalTaxes(final AbstractOrderModel abstractOrder)
	{
		final CisOrder cisOrder = getCisOrderConverter().convert(abstractOrder);

		return getExternalTaxDocument(abstractOrder, cisOrder);
	}

	protected ExternalTaxDocument getExternalTaxDocument(final AbstractOrderModel abstractOrder, final CisOrder cisOrder)
	{
		try
		{
			final CisTaxDoc cisTaxDoc;

			final String cisClientRef = abstractOrder.getGuid();
			if (abstractOrder instanceof CartModel)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("Getting taxes from external tax service for cart: %s %s", abstractOrder.getCode(),
							ReflectionToStringBuilder.toString(cisOrder, ToStringStyle.SHORT_PREFIX_STYLE)));
				}

				cisTaxDoc = getCisClientTaxService().quote(cisClientRef, getTenantId(), cisOrder);
			}
			else
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug(String.format("Getting taxes from external tax service for order: %s %s", abstractOrder.getCode(),
							ReflectionToStringBuilder.toString(cisOrder, ToStringStyle.SHORT_PREFIX_STYLE)));
				}

				cisTaxDoc = getCisClientTaxService().post(cisClientRef, getTenantId(), cisOrder);
			}

			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("External Tax Service returned Tax Document for order %s %s", abstractOrder.getCode(),
						ReflectionToStringBuilder.toString(cisTaxDoc, ToStringStyle.SHORT_PREFIX_STYLE)));
			}

			return getExternalTaxDocumentConverter().convert(new CisTaxDocOrder(cisTaxDoc, abstractOrder));
		}
		catch (Exception e)
		{
			return getCalculateExternalTaxesFallbackStrategy().calculateExternalTaxes(abstractOrder);
		}
	}

	protected CisClientTaxService getCisClientTaxService()
	{
		return cisClientTaxService;
	}

	@Required
	public void setCisClientTaxService(final CisClientTaxService cisClientTaxService)
	{
		this.cisClientTaxService = cisClientTaxService;
	}

	public SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public Converter<CisTaxDocOrder, ExternalTaxDocument> getExternalTaxDocumentConverter()
	{
		return externalTaxDocumentConverter;
	}

	public void setExternalTaxDocumentConverter(final Converter<CisTaxDocOrder, ExternalTaxDocument> externalTaxDocumentConverter)
	{
		this.externalTaxDocumentConverter = externalTaxDocumentConverter;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	public Converter<AddressModel, CisAddress> getCisAddressConverter()
	{
		return cisAddressConverter;
	}

	public void setCisAddressConverter(final Converter<AddressModel, CisAddress> cisAddressConverter)
	{
		this.cisAddressConverter = cisAddressConverter;
	}

	public CalculateExternalTaxesStrategy getCalculateExternalTaxesFallbackStrategy()
	{
		return calculateExternalTaxesFallbackStrategy;
	}

	public void setCalculateExternalTaxesFallbackStrategy(
			final CalculateExternalTaxesStrategy calculateExternalTaxesFallbackStrategy)
	{
		this.calculateExternalTaxesFallbackStrategy = calculateExternalTaxesFallbackStrategy;
	}

	public Converter<AbstractOrderModel, CisOrder> getCisOrderConverter()
	{
		return cisOrderConverter;
	}

	public void setCisOrderConverter(final Converter<AbstractOrderModel, CisOrder> cisOrderConverter)
	{
		this.cisOrderConverter = cisOrderConverter;
	}

	protected String getTenantId()
	{
		return tenantId;
	}

	@Required
	public void setTenantId(final String tenantId)
	{
		this.tenantId = tenantId;
	}
}
