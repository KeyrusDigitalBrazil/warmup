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

import de.hybris.platform.chineselogisticservices.delivery.dao.C2LItemZoneDeliveryModeDao;
import de.hybris.platform.commerceservices.strategies.impl.DefaultDeliveryModeLookupStrategy;
import de.hybris.platform.core.model.c2l.C2LItemModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;




/**
 * This strategy defines the process of looking up the delivery cost.
 */
public class ChineseDeliveryModeLookupStrategy extends DefaultDeliveryModeLookupStrategy
{
	private C2LItemZoneDeliveryModeDao c2LItemZoneDeliveryModeDao;

	@Override
	public List<DeliveryModeModel> getSelectableDeliveryModesForOrder(final AbstractOrderModel abstractOrderModel)
	{
		if (isPickUpOnlyOrder(abstractOrderModel))
		{
			return new ArrayList<>(getPickupDeliveryModeDao().findPickupDeliveryModesForAbstractOrder(
					abstractOrderModel));
		}

		final AddressModel deliveryAddress = abstractOrderModel.getDeliveryAddress();
		if (abstractOrderModel.getStore() == null)
		{
			return super.getSelectableDeliveryModesForOrder(abstractOrderModel);
		}

		if (deliveryAddress != null && deliveryAddress.getCountry() != null)
		{
			if (!"CN".equalsIgnoreCase(deliveryAddress.getCountry().getIsocode()))
			{
				return super.getSelectableDeliveryModesForOrder(abstractOrderModel);
			}
			return findDeliveryModeByAddress(abstractOrderModel, deliveryAddress);
		}
		else
		{
			return Collections.emptyList();
		}
	}

	protected List<DeliveryModeModel> findDeliveryModeByAddress(final AbstractOrderModel abstractOrderModel,
			final AddressModel deliveryAddress)
	{
		final C2LItemModel[] c2lItemModels =
		{ deliveryAddress.getCityDistrict(), deliveryAddress.getCity(), deliveryAddress.getRegion(),
					deliveryAddress.getCountry() };
		final CurrencyModel currency = abstractOrderModel.getCurrency();
		if (currency != null)
		{
			for (final C2LItemModel c2lItemModel : c2lItemModels)
			{
				final List<DeliveryModeModel> deliveryModes = findDeliveryModeByC2LItem(abstractOrderModel, c2lItemModel);
				if (!deliveryModes.isEmpty())
				{
					return deliveryModes;
				}
			}
		}
		return Collections.emptyList();
	}


	protected List<DeliveryModeModel> findDeliveryModeByC2LItem(final AbstractOrderModel abstractOrderModel, final C2LItemModel c2LItem)
	{
		final List<DeliveryModeModel> deliveryModes = new ArrayList<>();
		if (c2LItem != null && c2LItem.getZone() != null)
		{
			final Collection<DeliveryModeModel> deliveryModesResult = getC2LItemZoneDeliveryModeDao().findDeliveryModesByC2LItem(c2LItem,
					abstractOrderModel);
			if (!CollectionUtils.isEmpty(deliveryModesResult))
			{
				deliveryModes.addAll(deliveryModesResult);
			}
		}
		return deliveryModes;
	}

	protected C2LItemZoneDeliveryModeDao getC2LItemZoneDeliveryModeDao()
	{
		return c2LItemZoneDeliveryModeDao;
	}

	@Required
	public void setC2LItemZoneDeliveryModeDao(final C2LItemZoneDeliveryModeDao c2lItemZoneDeliveryModeDao)
	{
		c2LItemZoneDeliveryModeDao = c2lItemZoneDeliveryModeDao;
	}

}
