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
package de.hybris.platform.chineselogisticservices.delivery.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.chineselogisticservices.delivery.dao.C2LItemZoneDeliveryModeValueDao;
import de.hybris.platform.chineselogisticservices.strategies.impl.ChineseFindDeliveryCostStrategy;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.delivery.impl.DefaultDeliveryService;
import de.hybris.platform.core.model.c2l.C2LItemModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.jalo.order.delivery.JaloDeliveryModeException;
import de.hybris.platform.util.PriceValue;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Service for Delivery
 */
public class ChineseDeliveryService extends DefaultDeliveryService implements DeliveryService
{
	private static final Logger LOG = Logger.getLogger(ChineseFindDeliveryCostStrategy.class);
	private C2LItemZoneDeliveryModeValueDao c2LItemZoneDeliveryModeValueDao;

	@Override
	public PriceValue getDeliveryCostForDeliveryModeAndAbstractOrder(final DeliveryModeModel deliveryMode,
			final AbstractOrderModel abstractOrder)
	{
		if (abstractOrder.getDeliveryAddress() == null)
		{
			return null;
		}
		if (!"CN".equalsIgnoreCase(abstractOrder.getDeliveryAddress().getCountry().getIsocode()))
		{
			return super.getDeliveryCostForDeliveryModeAndAbstractOrder(deliveryMode, abstractOrder);
		}
		validateParameterNotNull(deliveryMode, "deliveryMode model cannot be null");
		validateParameterNotNull(abstractOrder, "abstractOrder model cannot be null");

		final AddressModel deliveryAddress = abstractOrder.getDeliveryAddress();
		final C2LItemModel[] c2lItemModels =
		{ deliveryAddress.getCityDistrict(), deliveryAddress.getCity(), deliveryAddress.getRegion(), deliveryAddress.getCountry() };

		try
		{
			for (final C2LItemModel c2lItemModel : c2lItemModels)
			{
				final PriceValue priceValue = getCost(abstractOrder, deliveryMode, c2lItemModel);
				if (priceValue != null)
				{
					return priceValue;
				}
			}
		}
		catch (final JaloDeliveryModeException e)//NOSONAR
		{
			LOG.error("Get delivery cost for delivery mode exception!");
			return null;
		}
		return null;
	}

	/**
	 * Get the cost of a given order with specific delivery mode
	 * 
	 * @param order
	 *           order model
	 * @param deliveryMode
	 *           delivery mode
	 * @param c2LItem
	 *           c2l item
	 * @return cost of the order
	 * @throws JaloDeliveryModeException
	 *            when address model is not complete
	 */
	public PriceValue getCost(final AbstractOrderModel order, final DeliveryModeModel deliveryMode, final C2LItemModel c2LItem)
			throws JaloDeliveryModeException
	{
		final AddressModel addr = order.getDeliveryAddress();
		if (addr == null)
		{
			throw new JaloDeliveryModeException("getCost(): delivery address was NULL in order " + order, 0);
		}

		final CountryModel country = addr.getCountry();
		if (country == null)
		{
			throw new JaloDeliveryModeException("getCost(): country of delivery address " + addr + " was NULL in order " + order, 0);
		}

		final CurrencyModel curr = order.getCurrency();
		if (curr == null)
		{
			throw new JaloDeliveryModeException("getCost(): currency was NULL in order " + order, 0);
		}

		if (c2LItem != null && c2LItem.getZone() != null)
		{
			final ZoneDeliveryModeValueModel bestMatch = c2LItemZoneDeliveryModeValueDao.findDeliveryModeValueByC2LItem(c2LItem,
					order, deliveryMode);

			if (bestMatch != null)
			{
				return new PriceValue(curr.getIsocode(), bestMatch.getValue(), bestMatch.getDeliveryMode().getNet());
			}
		}
		return null;
	}


	protected C2LItemZoneDeliveryModeValueDao getC2LItemZoneDeliveryModeValueDao()
	{
		return c2LItemZoneDeliveryModeValueDao;
	}

	@Required
	public void setC2LItemZoneDeliveryModeValueDao(final C2LItemZoneDeliveryModeValueDao c2lItemZoneDeliveryModeValueDao)
	{
		c2LItemZoneDeliveryModeValueDao = c2lItemZoneDeliveryModeValueDao;
	}


}
