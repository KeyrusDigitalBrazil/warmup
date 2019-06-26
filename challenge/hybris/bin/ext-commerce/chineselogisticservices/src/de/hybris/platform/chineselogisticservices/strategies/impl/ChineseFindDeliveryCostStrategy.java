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
package de.hybris.platform.chineselogisticservices.strategies.impl;

import de.hybris.platform.chineselogisticservices.delivery.impl.ChineseDeliveryService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.order.strategies.calculation.impl.DefaultFindDeliveryCostStrategy;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.PriceValue;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This strategy defines the process of looking up the delivery cost.
 */
public class ChineseFindDeliveryCostStrategy extends DefaultFindDeliveryCostStrategy
{
	private static final Logger LOG = Logger.getLogger(ChineseFindDeliveryCostStrategy.class);

	private transient ChineseDeliveryModeLookupStrategy chineseDeliveryModeLookupStrategy;
	private transient ChineseDeliveryService chineseDeliveryService;


	@Override
	public PriceValue getDeliveryCost(final AbstractOrderModel order)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("order", order);
		if (order.getStore() == null || order.getStore().getPickupInStoreMode() == null)
		{
			return super.getDeliveryCost(order);
		}
		getModelService().save(order);
		final List<DeliveryModeModel> deliveryModeModels = chineseDeliveryModeLookupStrategy.getSelectableDeliveryModesForOrder(order);
		if (deliveryModeModels != null && !deliveryModeModels.isEmpty())
		{
			try
			{
				final DeliveryModeModel deliveryModeModel = order.getDeliveryMode();
				if (deliveryModeModel == null)
				{
					return new PriceValue(order.getCurrency().getIsocode(), 0.0, order.getNet().booleanValue());
				}
				return chineseDeliveryService.getDeliveryCostForDeliveryModeAndAbstractOrder(deliveryModeModel, order);
			}
			//Catch root exception as the super implementation
			catch (final Exception e)//NOSONAR
			{
				LOG.warn("Could not find deliveryCost for order [" + order.getCode() + "] due to : " + e.getMessage()
						+ "... skipping!");
				return new PriceValue(order.getCurrency().getIsocode(), 0.0, order.getNet().booleanValue());
			}
		}
		return new PriceValue(order.getCurrency().getIsocode(), 0.0, order.getNet().booleanValue());
	}

	protected ChineseDeliveryModeLookupStrategy getChineseDeliveryModeLookupStrategy()
	{
		return chineseDeliveryModeLookupStrategy;
	}

	@Required
	public void setChineseDeliveryModeLookupStrategy(final ChineseDeliveryModeLookupStrategy chineseDeliveryModeLookupStrategy)
	{
		this.chineseDeliveryModeLookupStrategy = chineseDeliveryModeLookupStrategy;
	}


	protected ChineseDeliveryService getChineseDeliveryService()
	{
		return chineseDeliveryService;
	}

	@Required
	public void setChineseDeliveryService(final ChineseDeliveryService chineseDeliveryService)
	{
		this.chineseDeliveryService = chineseDeliveryService;
	}

}
