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
package de.hybris.platform.subscriptionfacades.order.converters.populator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.order.converters.populator.OrderEntryPopulator;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionfacades.converters.SubscriptionXStreamAliasConverter;
import de.hybris.platform.subscriptionfacades.data.BillingTimeData;
import de.hybris.platform.subscriptionfacades.data.OrderEntryPriceData;
import de.hybris.platform.subscriptionfacades.order.impl.BillingTimeComparator;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;
import de.hybris.platform.util.localization.Localization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Converter for converting order / cart entries. It sets the total and base prices for entries that have multiple
 * billing frequencies (as they are subscription products)
 */
public class SubscriptionOrderEntryPopulator extends OrderEntryPopulator
{
	private Converter<BillingTimeModel, BillingTimeData> billingTimeConverter;
	private SubscriptionXStreamAliasConverter subscriptionXStreamAliasConverter;
	private SubscriptionProductService subscriptionProductService;
	private Comparator billingTimeComparator;

	@Override
	public void populate(@Nonnull final AbstractOrderEntryModel source, @Nonnull final OrderEntryData target)
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);

		if (source.getOrder() != null && source.getOrder().getBillingTime() != null && source.getQuantity() != null)
		{
			target.setOrderEntryPrices(getOrderEntryPricesForEntry(source));
		}

		try
		{
			final ProductData productData = getSubscriptionXStreamAliasConverter().getSubscriptionProductDataFromXml(
					source.getXmlProduct());
			if (null != productData)
			{
				target.setProduct(productData);
			}
		}
		catch (final ClassCastException e)
		{
			throw new ConversionException("Incorrect object type. ProductData is required", e);
		}

		if (StringUtils.isNotEmpty(source.getOriginalSubscriptionId()) && source.getOriginalOrderEntry() != null)
		{
			target.setOriginalSubscriptionId(source.getOriginalSubscriptionId());
			target.setOriginalOrderCode(source.getOriginalOrderEntry().getOrder().getCode());
			target.setOriginalEntryNumber(source.getOriginalOrderEntry().getEntryNumber());

			final StringBuilder entryMessage = new StringBuilder();
			if (StringUtils.isNotEmpty(target.getEntryMessage()))
			{
				entryMessage.append(target.getEntryMessage()).append("; ");
			}
			final Object[] args =
			{ source.getOriginalOrderEntry().getProduct().getName() };
			final String upgradedMessage = Localization.getLocalizedString("subscriptionfacades.basket.subscription.upgradedFrom",
					args);
			entryMessage.append(upgradedMessage);
			target.setEntryMessage(entryMessage.toString());
		}
	}

	@Nonnull
	protected List<OrderEntryPriceData> getOrderEntryPricesForEntry(@Nonnull final AbstractOrderEntryModel source)
	{
		final Map<String, OrderEntryPriceData> orderEntryPriceContainer = new HashMap<>();

		final Collection<AbstractOrderEntryModel> abstractOrdersEntries = new ArrayList<>();
		abstractOrdersEntries.add(source);
		if (source.getChildEntries() != null)
		{
			abstractOrdersEntries.addAll(source.getChildEntries());
		}

		for (final AbstractOrderEntryModel abstractOrderEntry : abstractOrdersEntries)
		{
			final OrderEntryPriceData orderEntryPrice = createOrderEntryPriceDataForEntry(abstractOrderEntry);

			if (getSubscriptionProductService().isSubscription(abstractOrderEntry.getProduct()))
			{
				final ProductModel subscriptionProduct = source.getProduct();
				final BillingFrequencyModel productBillingFrequency = subscriptionProduct.getSubscriptionTerm().getBillingPlan()
						.getBillingFrequency();

				if (abstractOrderEntry.getOrder().getBillingTime().equals(productBillingFrequency))
				{
					orderEntryPrice.setDefaultPrice(true);
				}
			}

			orderEntryPriceContainer.put(abstractOrderEntry.getOrder().getBillingTime().getCode(), orderEntryPrice);
		}

		// extract the totals in the same order as the billingtimes are sorted in
		final List<OrderEntryPriceData> orderEntryPrices = new ArrayList<>();
		for (final BillingTimeData billingTime : buildBillingTimes(source))
		{
			final OrderEntryPriceData orderEntryPrice = orderEntryPriceContainer.get(billingTime.getCode());
			orderEntryPrices.add(orderEntryPrice == null ? new OrderEntryPriceData() : orderEntryPrice);
		}

		return orderEntryPrices;
	}

	@Nonnull
	protected List<BillingTimeData> buildBillingTimes(@Nonnull final AbstractOrderEntryModel source)
	{
		final List<AbstractOrderModel> children = new ArrayList<>(source.getOrder().getChildren());
		final Stream<BillingTimeModel> sortedBillingTimes = children.stream()
				.map(c -> c.getBillingTime()).sorted(getBillingTimeComparator());

		return Stream.concat(Stream.of(source.getOrder().getBillingTime()), sortedBillingTimes)
						.map(billingTime -> getBillingTimeConverter().convert(billingTime))
						.collect(Collectors.toList());
	}

	@Nonnull
	protected OrderEntryPriceData createOrderEntryPriceDataForEntry(@Nonnull final AbstractOrderEntryModel entry)
	{
		final OrderEntryPriceData orderEntryPrice = new OrderEntryPriceData();
		orderEntryPrice.setTotalPrice(createPrice(entry, entry.getTotalPrice()));
		orderEntryPrice.setBasePrice(createPrice(entry, entry.getBasePrice()));
		final BillingTimeData billingTime = getBillingTimeConverter().convert(entry.getOrder().getBillingTime());
		orderEntryPrice.setBillingTime(billingTime);

		return orderEntryPrice;
	}

	protected Converter<BillingTimeModel, BillingTimeData> getBillingTimeConverter()
	{
		return billingTimeConverter;
	}

	@Required
	public void setBillingTimeConverter(final Converter<BillingTimeModel, BillingTimeData> billingFrequencyConverter)
	{
		this.billingTimeConverter = billingFrequencyConverter;
	}

	protected SubscriptionXStreamAliasConverter getSubscriptionXStreamAliasConverter()
	{
		return subscriptionXStreamAliasConverter;
	}

	@Required
	public void setSubscriptionXStreamAliasConverter(final SubscriptionXStreamAliasConverter subscriptionXStreamAliasConverter)
	{
		this.subscriptionXStreamAliasConverter = subscriptionXStreamAliasConverter;
	}

	/**
	 * @return subscription product service.
	 */
	protected SubscriptionProductService getSubscriptionProductService()
	{
		return subscriptionProductService;
	}

	@Required
	public void setSubscriptionProductService(final SubscriptionProductService subscriptionProductService)
	{
		this.subscriptionProductService = subscriptionProductService;
	}

	protected Comparator getBillingTimeComparator()
	{
		return billingTimeComparator;
	}

	@Required
	public void setBillingTimeComparator(final BillingTimeComparator billingTimeComparator)
	{
		this.billingTimeComparator = billingTimeComparator;
	}

}
