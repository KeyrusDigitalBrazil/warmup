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
package de.hybris.platform.chineselogisticservices.delivery.dao.impl;

import de.hybris.platform.chineselogisticservices.delivery.dao.C2LItemZoneDeliveryModeDao;
import de.hybris.platform.commerceservices.delivery.dao.impl.DefaultCountryZoneDeliveryModeDao;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.C2LItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.jalo.link.Link;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class ChineseC2LItemZoneDeliveryModeDao extends DefaultCountryZoneDeliveryModeDao implements C2LItemZoneDeliveryModeDao
{
	private static final String STORE_TO_DELIVERY_MODE_RELATION = "BaseStore2DeliveryModeRel";

	@Override
	public Collection<DeliveryModeModel> findDeliveryModesByC2LItem(final C2LItemModel c2LItem,
			final AbstractOrderModel abstractOrder)
	{
		final StringBuilder query = new StringBuilder("SELECT DISTINCT {zdm:").append(ItemModel.PK).append("}");
		query.append(" FROM { ").append(ZoneDeliveryModeValueModel._TYPECODE).append(" AS val");
		query.append(" JOIN ").append(ZoneDeliveryModeModel._TYPECODE).append(" AS zdm");
		query.append(" ON {val:").append(ZoneDeliveryModeValueModel.DELIVERYMODE).append("}={zdm:").append(ItemModel.PK)
				.append('}');
		query.append(" JOIN ").append(STORE_TO_DELIVERY_MODE_RELATION).append(" AS s2d");
		query.append(" ON {val:").append(ZoneDeliveryModeValueModel.DELIVERYMODE).append("}={s2d:").append(Link.TARGET).append('}');
		query.append(" } WHERE {val:").append(ZoneDeliveryModeValueModel.CURRENCY).append("}=?currency");
		query.append(" AND {val:").append(ZoneDeliveryModeValueModel.ZONE).append("}=?deliveryDestination");
		query.append(" AND {s2d:").append(Link.SOURCE).append("}=?store");
		query.append(" AND {zdm:").append(ZoneDeliveryModeModel.NET).append("}=?net");
		query.append(" AND {zdm:").append(ZoneDeliveryModeModel.ACTIVE).append("}=?active");

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("deliveryDestination", c2LItem.getZone());
		params.put("currency", abstractOrder.getCurrency());
		params.put("net", abstractOrder.getNet());
		params.put("active", Boolean.TRUE);
		params.put("store", abstractOrder.getStore());

		return doSearch(query.toString(), params, DeliveryModeModel.class);
	}

}
