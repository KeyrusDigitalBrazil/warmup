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
package de.hybris.platform.subscriptionservices.search.solrfacetsearch.provider.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This ValueProvider will provide the base price value for a subscription {@link ProductModel} which is the highest
 * recurring price from its price plan. For other product types the standard functionality is used.
 */
public class SubscriptionProductPriceValueProvider extends SubscriptionAwareFieldValueProvider implements FieldValueProvider
{
	private FieldNameProvider fieldNameProvider;
	private SubscriptionCommercePriceService commercePriceService;
	private CommonI18NService commonI18NService;
	private SessionService sessionService;
	private PriceService priceService;

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		validateParameterNotNullStandardMessage("model", model);

		final List<FieldValue> fieldValues = new ArrayList<>();

		if (model instanceof ProductModel && !indexConfig.getCurrencies().isEmpty())
		{
			final ProductModel product = (ProductModel) model;
			for (final CurrencyModel currency : indexConfig.getCurrencies())
			{
				fieldValues.addAll(createFieldValue(product, indexedProperty, currency));
			}
			return fieldValues;

		}

		return fieldValues;

	}

	protected List<FieldValue> createFieldValue(final ProductModel product, final IndexedProperty indexedProperty,
			final CurrencyModel currency)
	{
		final List<FieldValue> fieldValues = new ArrayList<>();
		Double basePrice = null;

		if (getSubscriptionProductService().isSubscription(product))
		{

			basePrice = getBasePriceForSubscriptionProduct(product, currency);
		}
		else
		{
			basePrice = getBasePriceForNonSubscriptionProduct(product, currency);
		}

		addFieldValues(fieldValues, indexedProperty, currency, basePrice);

		return fieldValues;
	}

	protected Double getBasePriceForSubscriptionProduct(final ProductModel product, final CurrencyModel currency)
	{
		final Object value = getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				getCommonI18NService().setCurrentCurrency(currency);
				final SubscriptionPricePlanModel pricePlan = getCommercePriceService().getSubscriptionPricePlanForProduct(product);
				if (pricePlan != null && CollectionUtils.isNotEmpty(pricePlan.getRecurringChargeEntries()))
				{
					final RecurringChargeEntryModel recurringChargeEntryModel = getCommercePriceService()
							.getLastRecurringPriceFromPlan(pricePlan);
					return recurringChargeEntryModel != null ? recurringChargeEntryModel.getPrice() : null;
				}
				return null;
			}
		});

		if (value instanceof Double)
		{
			return (Double) value;
		}
		return null;
	}

	protected Double getBasePriceForNonSubscriptionProduct(final ProductModel product, final CurrencyModel currency)
	{
		final Object value = getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				getCommonI18NService().setCurrentCurrency(currency);
				final List<PriceInformation> priceInfos = getPriceService().getPriceInformationsForProduct(product);
				if (priceInfos != null && !priceInfos.isEmpty())
				{
					return priceInfos.get(0).getPriceValue().getValue();
				}
				return null;
			}
		});

		if (value instanceof Double)
		{
			return (Double) value;
		}
		return null;
	}

	protected void addFieldValues(final List<FieldValue> fieldValues, final IndexedProperty indexedProperty,
			final CurrencyModel currency, final Object value)
	{
		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty,
				currency.getIsocode().toLowerCase());
		fieldValues.addAll(fieldNames.stream().map(fieldName -> new FieldValue(fieldName, value)).collect(Collectors.toList()));
	}

	protected FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	@Required
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	protected SubscriptionCommercePriceService getCommercePriceService()
	{
		return commercePriceService;
	}

	@Required
	public void setCommercePriceService(final SubscriptionCommercePriceService commercePriceService)
	{
		this.commercePriceService = commercePriceService;
	}

	protected PriceService getPriceService()
	{
		return priceService;
	}

	@Required
	public void setPriceService(final PriceService priceService)
	{
		this.priceService = priceService;
	}

}
