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
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.commercefacades.comment.data.CommentData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.strategies.ModifiableChecker;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Converter for converting order / cart entries
 */
public class OrderEntryPopulator implements Populator<AbstractOrderEntryModel, OrderEntryData>
{
	private static final Logger LOG = LoggerFactory.getLogger(OrderEntryPopulator.class);
	private Converter<ProductModel, ProductData> productConverter;
	private Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter;
	private PriceDataFactory priceDataFactory;
	private ModifiableChecker<AbstractOrderEntryModel> entryOrderChecker;
	private Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter;
	private Converter<AbstractOrderEntryProductInfoModel, List<ConfigurationInfoData>> productConfigurationConverter;
	private EntryGroupService entryGroupService;
	private Converter<CommentModel, CommentData> orderCommentConverter;


	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	protected Converter<DeliveryModeModel, DeliveryModeData> getDeliveryModeConverter()
	{
		return deliveryModeConverter;
	}

	@Required
	public void setDeliveryModeConverter(final Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter)
	{
		this.deliveryModeConverter = deliveryModeConverter;
	}

	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

	protected ModifiableChecker<AbstractOrderEntryModel> getEntryOrderChecker()
	{
		return entryOrderChecker;
	}

	@Required
	public void setEntryOrderChecker(final ModifiableChecker<AbstractOrderEntryModel> entryOrderChecker)
	{
		this.entryOrderChecker = entryOrderChecker;
	}

	protected Converter<PointOfServiceModel, PointOfServiceData> getPointOfServiceConverter()
	{
		return pointOfServiceConverter;
	}

	@Required
	public void setPointOfServiceConverter(final Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter)
	{
		this.pointOfServiceConverter = pointOfServiceConverter;
	}

	protected Converter<AbstractOrderEntryProductInfoModel, List<ConfigurationInfoData>> getProductConfigurationConverter()
	{
		return productConfigurationConverter;
	}

	@Required
	public void setProductConfigurationConverter(
			final Converter<AbstractOrderEntryProductInfoModel, List<ConfigurationInfoData>> productConfigurationConverter)
	{
		this.productConfigurationConverter = productConfigurationConverter;
	}

	protected EntryGroupService getEntryGroupService()
	{
		return entryGroupService;
	}

	@Required
	public void setEntryGroupService(final EntryGroupService entryGroupService)
	{
		this.entryGroupService = entryGroupService;
	}

	@Required
	public void setOrderCommentConverter(final Converter<CommentModel, CommentData> orderEntryCommentConverter)
	{
		this.orderCommentConverter = orderEntryCommentConverter;
	}

	protected Converter<CommentModel, CommentData> getOrderCommentConverter()
	{
		return orderCommentConverter;
	}

	@Override
	public void populate(final AbstractOrderEntryModel source, final OrderEntryData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		addCommon(source, target);
		addProduct(source, target);
		addTotals(source, target);
		addDeliveryMode(source, target);
		addConfigurations(source, target);
		addEntryGroups(source, target);
		addComments(source, target);
	}

	protected void addEntryGroups(final AbstractOrderEntryModel source, final OrderEntryData target)
	{
		if (source.getEntryGroupNumbers() != null)
		{
			target.setEntryGroupNumbers(new ArrayList<>(source.getEntryGroupNumbers()));
		}
	}

	protected void addConfigurations(final AbstractOrderEntryModel source, final OrderEntryData target)
	{
		final List<ConfigurationInfoData> configurations
				= getProductConfigurationConverter().convertAll(source.getProductInfos()).stream()
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		target.setConfigurationInfos(configurations);
		if (configurations != null)
		{
			target.setStatusSummaryMap(configurations.stream()
					.peek(i -> {
						if (i.getStatus() == null)
						{
							LOG.warn("Missing status in configuration {}", i.getConfigurationLabel());
						}
					})
					.peek(config -> {
						if (config.getStatus() == null)
						{
							throw new IllegalStateException("Configuration info " + config.getConfigurationLabel()
									+ " has null status. Check populator of configuration type " + config.getConfiguratorType());
						}
					}).map(ConfigurationInfoData::getStatus)
					.collect(Collectors.toMap(Function.identity(), item -> 1, (first, second) -> first + second)));
		}
	}

	protected void addDeliveryMode(final AbstractOrderEntryModel orderEntry, final OrderEntryData entry)
	{
		if (orderEntry.getDeliveryMode() != null)
		{
			entry.setDeliveryMode(getDeliveryModeConverter().convert(orderEntry.getDeliveryMode()));
		}

		if (orderEntry.getDeliveryPointOfService() != null)
		{
			entry.setDeliveryPointOfService(getPointOfServiceConverter().convert(orderEntry.getDeliveryPointOfService()));
		}
	}

	protected void addCommon(final AbstractOrderEntryModel orderEntry, final OrderEntryData entry)
	{
		entry.setEntryNumber(orderEntry.getEntryNumber());
		entry.setQuantity(orderEntry.getQuantity());
		adjustUpdateable(entry, orderEntry);
	}


	protected void adjustUpdateable(final OrderEntryData entry, final AbstractOrderEntryModel entryToUpdate)
	{
		entry.setUpdateable(getEntryOrderChecker().canModify(entryToUpdate));
	}

	protected void addProduct(final AbstractOrderEntryModel orderEntry, final OrderEntryData entry)
	{
		entry.setProduct(getProductConverter().convert(orderEntry.getProduct()));
	}

	protected void addTotals(final AbstractOrderEntryModel orderEntry, final OrderEntryData entry)
	{
		if (orderEntry.getBasePrice() != null)
		{
			entry.setBasePrice(createPrice(orderEntry, orderEntry.getBasePrice()));
		}
		if (orderEntry.getTotalPrice() != null)
		{
			entry.setTotalPrice(createPrice(orderEntry, orderEntry.getTotalPrice()));
		}
	}

	protected void addComments(final AbstractOrderEntryModel orderEntry, final OrderEntryData entry)
	{
		entry.setComments(getOrderCommentConverter().convertAll(orderEntry.getComments()));
	}

	protected PriceData createPrice(final AbstractOrderEntryModel orderEntry, final Double val)
	{
		return getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(val.doubleValue()),
				orderEntry.getOrder().getCurrency());
	}
}
