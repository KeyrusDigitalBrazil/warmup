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
package de.hybris.platform.subscriptionservices.price.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.price.impl.DefaultCommercePriceService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.subscriptionservices.jalo.ExtendedPriceFactory;
import de.hybris.platform.subscriptionservices.model.BillingEventModel;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SubscriptionCommercePriceService}.
 */
public class DefaultSubscriptionCommercePriceService extends DefaultCommercePriceService implements
		SubscriptionCommercePriceService
{
	private static final Logger LOG = Logger.getLogger(DefaultSubscriptionCommercePriceService.class);

	private ModelService modelService;

	@SuppressWarnings("deprecation")
	@Override
	@Nullable
	public SubscriptionPricePlanModel getSubscriptionPricePlanForEntry(final AbstractOrderEntryModel entry)
	{
		final AbstractOrderEntry entryItem = getModelService().getSource(entry);
		PriceRow priceRowItem;
		try
		{
			priceRowItem = getCurrentPriceFactory().getPriceRow(entryItem);
		}
		catch (final JaloPriceFactoryException e)
		{
			throw new SystemException(e.getMessage(), e);
		}


		PriceRowModel priceRow = null;
		if (priceRowItem != null)
		{
			priceRow = getModelService().get(priceRowItem);
		}

		if (priceRow instanceof SubscriptionPricePlanModel)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Found subscription price plan: " + ((SubscriptionPricePlanModel) priceRow).getName());
			}
			return (SubscriptionPricePlanModel) priceRow;
		}
		else
		{
			LOG.warn("Found no subscription price plan for product: " + entryItem.getProduct().getCode());
			return null;
		}
	}

	@Override
	@Nullable
	public SubscriptionPricePlanModel getSubscriptionPricePlanForProduct(final SubscriptionProductModel subscriptionProduct)
	{
		return getSubscriptionPricePlanForProduct((ProductModel) subscriptionProduct);
	}

	@Override
	@Nullable
	public SubscriptionPricePlanModel getSubscriptionPricePlanForProduct(@Nonnull final ProductModel subscriptionProduct)
	{
		final Product productItem = getModelService().getSource(subscriptionProduct);
		PriceRow priceRowItem;

		try
		{
			priceRowItem = getCurrentPriceFactory().getPriceRow(productItem);
		}
		catch (final JaloPriceFactoryException e)
		{
			throw new SystemException(e.getMessage(), e);
		}

		if (priceRowItem == null)
		{
			return null;
		}

		final PriceRowModel priceRow = getModelService().get(priceRowItem);

		if (priceRow instanceof SubscriptionPricePlanModel)
		{
			LOG.debug("Found subscription price row: " + ((SubscriptionPricePlanModel) priceRow).getName());
			return (SubscriptionPricePlanModel) priceRow;
		}
		else
		{
			LOG.info("Found no subscription price plan for product: " + subscriptionProduct.getCode());
			return null;
		}
	}

	@Override
	@Nullable
	public RecurringChargeEntryModel getFirstRecurringPriceFromPlan(@Nullable final SubscriptionPricePlanModel pricePlan)
	{
		if (pricePlan == null || CollectionUtils.isEmpty(pricePlan.getRecurringChargeEntries()))
		{
			return null;
		}


		return pricePlan.getRecurringChargeEntries().iterator().next();
	}

	@Override
	@Nullable
	public RecurringChargeEntryModel getLastRecurringPriceFromPlan(@Nullable final SubscriptionPricePlanModel pricePlan)
	{
		if (pricePlan == null || CollectionUtils.isEmpty(pricePlan.getRecurringChargeEntries()))
		{
			return null;
		}

		Collection<RecurringChargeEntryModel> collection = pricePlan.getRecurringChargeEntries();

		return collection.toArray(new RecurringChargeEntryModel[collection.size()])[collection.size()-1];
	}

	protected ExtendedPriceFactory getCurrentPriceFactory()
	{
		return (ExtendedPriceFactory) OrderManager.getInstance().getPriceFactory();
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	@Nullable
	public OneTimeChargeEntryModel getOneTimeChargeEntryPlan(@Nonnull final SubscriptionPricePlanModel pricePlan,
															 @Nonnull final BillingEventModel billingEvent)
	{
		OneTimeChargeEntryModel oneTimeCharge = null;

		validateParameterNotNullStandardMessage("pricePlan", pricePlan);
		validateParameterNotNullStandardMessage("billingEvent", billingEvent);

		if (CollectionUtils.isNotEmpty(pricePlan.getOneTimeChargeEntries()))
		{
			for (final OneTimeChargeEntryModel chargeEntry : pricePlan.getOneTimeChargeEntries())
			{
				if (billingEvent.equals(chargeEntry.getBillingEvent()))
				{
					oneTimeCharge = chargeEntry;
					break;
				}
			}
		}

		return oneTimeCharge;
	}

}
