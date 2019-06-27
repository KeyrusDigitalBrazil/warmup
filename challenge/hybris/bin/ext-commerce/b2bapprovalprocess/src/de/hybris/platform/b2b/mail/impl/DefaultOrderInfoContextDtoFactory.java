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
package de.hybris.platform.b2b.mail.impl;

import de.hybris.platform.b2b.mail.OrderInfoContextDtoFactory;
import de.hybris.platform.b2b.services.B2BCurrencyConversionService;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.ReflectionUtils;


public class DefaultOrderInfoContextDtoFactory implements OrderInfoContextDtoFactory<OrderInfoContextDto>
{
	private static final Logger LOG = Logger.getLogger(DefaultOrderInfoContextDtoFactory.class);
	private CommonI18NService commonI18NService;
	private ConfigurationService configurationService;
	private final Locale locale = Locale.ENGLISH;
	private B2BCurrencyConversionService b2bCurrencyConversionService;
	private B2BOrderService b2BOrderService;

	protected List<OrderInfoContextDto.OrderEntryInfoContextDto> createOrderInfoEntries(final OrderModel order)
	{
		final List<AbstractOrderEntryModel> entries = order.getEntries();
		final List<OrderInfoContextDto.OrderEntryInfoContextDto> orderInfoEntries = new ArrayList<OrderInfoContextDto.OrderEntryInfoContextDto>();

		// variant products do not have names, but their base product has
		for (final AbstractOrderEntryModel entry : entries)
		{
			final String basePrice = b2bCurrencyConversionService.formatCurrencyAmount(locale, order.getCurrency(),
					entry.getBasePrice().doubleValue());
			final String totalPrice = b2bCurrencyConversionService.formatCurrencyAmount(locale, order.getCurrency(),
					entry.getTotalPrice().doubleValue());
			final String discount = b2bCurrencyConversionService.formatCurrencyAmount(locale, order.getCurrency(),
					getB2BOrderService().getTotalDiscount(entry));
			final ProductModel sku = entry.getProduct();
			final OrderInfoContextDto.OrderEntryInfoContextDto orderInfoEntry = new OrderInfoContextDto.OrderEntryInfoContextDto();

			// set all string fields to a blank string so that they don't show up in the velocity template if they
			// are not set.
			setAllStringAttributesToEmpty(orderInfoEntry);
			orderInfoEntry.setOrderEntry(entry);
			orderInfoEntry.setBasePrice(basePrice);
			orderInfoEntry.setTotalPrice(totalPrice);
			orderInfoEntry.setProductName(sku.getName());
			orderInfoEntry.setOrderEntryNumber(entry.getProduct().getCode());
			orderInfoEntry.setOrderEntryStatus(entry.getOrder().getStatus().getCode());
			orderInfoEntry.setDiscountPrice(discount); // the adjustment to the base price.
			orderInfoEntries.add(orderInfoEntry);
		}
		return orderInfoEntries;
	}


	/**
	 * Initializes this dto with values form the order to be used by velocity or anyother rendering engine. The default
	 * implementation
	 *
	 * @param order
	 *           A hybris Order model
	 * @return An initialized Context
	 */
	public OrderInfoContextDto createOrderInfoContextDto(final OrderModel order)
	{
		// set all string fields to a blank string so that they don't show up in the velocity template if they are
		// not set.

		final OrderInfoContextDto orderInfoContext = new OrderInfoContextDto();
		setAllStringAttributesToEmpty(orderInfoContext);
		orderInfoContext.setPermissionResults(order.getPermissionResults());
		orderInfoContext.setOrderNumber(order.getCode());
		orderInfoContext.setOrderInfoEntries(this.createOrderInfoEntries(order));

		orderInfoContext.setHasDiscounts(getB2BOrderService().hasItemDiscounts(order));
		orderInfoContext.setCurrencyIsoCode(order.getCurrency().getIsocode());
		orderInfoContext.setSubtotalAmount(getB2bCurrencyConversionService().formatCurrencyAmount(locale, order.getCurrency(),
				order.getTotalPrice().doubleValue() - order.getDeliveryCost().doubleValue()));
		orderInfoContext.setTaxAmount(getB2bCurrencyConversionService().formatCurrencyAmount(locale, order.getCurrency(),
				order.getTotalTax().doubleValue()));
		orderInfoContext.setDeliveryCost(getB2bCurrencyConversionService().formatCurrencyAmount(locale, order.getCurrency(),
				order.getDeliveryCost().doubleValue()));
		orderInfoContext.setTotalAmount(getB2bCurrencyConversionService().formatCurrencyAmount(locale, order.getCurrency(),
				order.getTotalPrice().doubleValue() + order.getTotalTax().doubleValue()));
		return orderInfoContext;
	}

	/**
	 * Initialize all the class fields of type String to an empty string.
	 *
	 * @param ctx
	 *           the new all string attributes to empty
	 */
	public void setAllStringAttributesToEmpty(final Object ctx)
	{
		try
		{
			final List<Field> fields = new ArrayList(Arrays.asList(ctx.getClass().getDeclaredFields()));
			// get fields of all the super class instance
			final List<Class<?>> superClasses = ClassUtils.getAllSuperclasses(ctx.getClass());
			for (final Class superclass : superClasses)
			{
				fields.addAll(Arrays.asList(superclass.getDeclaredFields()));
			}
			for (final Field field : fields)
			{
				if (field.getType().isInstance(""))
				{
					field.setAccessible(true);
					ReflectionUtils.setField(field, ctx, "");
				}
			}
		}
		catch (final Exception e)
		{
			LOG.warn(e.getMessage(), e);
		}
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

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected B2BCurrencyConversionService getB2bCurrencyConversionService()
	{
		return b2bCurrencyConversionService;
	}

	@Required
	public void setB2bCurrencyConversionService(final B2BCurrencyConversionService b2bCurrencyConversionService)
	{
		this.b2bCurrencyConversionService = b2bCurrencyConversionService;
	}

	protected B2BOrderService getB2BOrderService()
	{
		return b2BOrderService;
	}

	@Required
	public void setB2BOrderService(final B2BOrderService b2bOrderService)
	{
		b2BOrderService = b2bOrderService;
	}

}
