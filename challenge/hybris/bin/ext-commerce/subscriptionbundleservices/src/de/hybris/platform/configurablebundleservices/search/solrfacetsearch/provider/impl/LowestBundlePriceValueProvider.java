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

package de.hybris.platform.configurablebundleservices.search.solrfacetsearch.provider.impl;

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
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This ValueProvider will provide the value for the {@link ProductModel}'s lowest possible price in any bundle.
 */
public class LowestBundlePriceValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider
{
	private FieldNameProvider fieldNameProvider;
	private BundleRuleService bundleRuleService;
	private PriceService priceService;
	private CommonI18NService commonI18NService;
	private SessionService sessionService;
	private SubscriptionCommercePriceService commercePriceService;
	private SubscriptionProductService subscriptionProductService;

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		validateParameterNotNullStandardMessage("model", model);

		final List<FieldValue> fieldValues = new ArrayList<>();

		if (model instanceof ProductModel && !indexConfig.getCurrencies().isEmpty())
		{
			final ProductModel productModel = (ProductModel) model;
			for (final CurrencyModel currency : indexConfig.getCurrencies())
			{
				fieldValues.addAll(createFieldValue(productModel, indexedProperty, currency));
			}
		}

		return fieldValues;
	}

	protected List<FieldValue> createFieldValue(final ProductModel productModel, final IndexedProperty indexedProperty,
			final CurrencyModel currency)
	{
		final List<FieldValue> fieldValues = new ArrayList<>();
		final ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRuleWithLowestPrice(
				productModel, currency);
		Double lowestPrice = null;

		if (priceRule != null && priceRule.getPrice() != null)
		{
			lowestPrice = Double.valueOf(priceRule.getPrice().doubleValue());
		}
		else if (getSubscriptionProductService().isSubscription(productModel))
		{
			final Object value = getPriceForSubscription(productModel, currency);
			if (value instanceof Double)
			{
				lowestPrice = (Double) value;
			}
		}
		else
		{
			final Object value = getPriceForProduct(productModel, currency);
			if (value instanceof Double)
			{
				lowestPrice = (Double) value;
			}
		}

		addFieldValues(fieldValues, indexedProperty, currency, lowestPrice);

		return fieldValues;
	}

	protected Object getPriceForSubscription(final ProductModel productModel, final CurrencyModel currency)
	{
		return getSessionService().executeInLocalView(new SessionExecutionBody()
				{
					@Override
					public Object execute()
					{
						getCommonI18NService().setCurrentCurrency(currency);
						final SubscriptionPricePlanModel pricePlan = getCommercePriceService().getSubscriptionPricePlanForProduct(
								productModel);
						if (pricePlan != null && CollectionUtils.isNotEmpty(pricePlan.getRecurringChargeEntries()))
						{
							return getCommercePriceService().getFirstRecurringPriceFromPlan(pricePlan).getPrice();
						}
						return null;
					}
				});
	}

	protected Object getPriceForProduct(final ProductModel productModel, final CurrencyModel currency)
	{
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				getCommonI18NService().setCurrentCurrency(currency);
				final List<PriceInformation> priceInfos = getPriceService().getPriceInformationsForProduct(productModel);
				if (CollectionUtils.isNotEmpty(priceInfos))
				{
					return priceInfos.get(0).getPriceValue().getValue();
				}
				return null;
			}
		});
	}


	protected void addFieldValues(final List<FieldValue> fieldValues, final IndexedProperty indexedProperty,
			final CurrencyModel currency, final Object value)
	{
		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty,
				currency.getIsocode().toLowerCase());
		for (final String fieldName : fieldNames)
		{
			fieldValues.add(new FieldValue(fieldName, value));
		}
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

	@Required
	public void setBundleRuleService(final BundleRuleService bundleRuleService)
	{
		this.bundleRuleService = bundleRuleService;
	}

	protected BundleRuleService getBundleRuleService()
	{
		return bundleRuleService;
	}

	@Required
	public void setPriceService(final PriceService priceService)
	{
		this.priceService = priceService;
	}

	protected PriceService getPriceService()
	{
		return priceService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
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

	@Required
	public void setCommercePriceService(final SubscriptionCommercePriceService commercePriceService)
	{
		this.commercePriceService = commercePriceService;
	}

	protected SubscriptionCommercePriceService getCommercePriceService()
	{
		return commercePriceService;
	}

	@Required
	public void setSubscriptionProductService(final SubscriptionProductService subscriptionProductService)
	{
		this.subscriptionProductService = subscriptionProductService;
	}

	protected SubscriptionProductService getSubscriptionProductService()
	{
		return subscriptionProductService;
	}
}
