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
package de.hybris.platform.commerceservices.promotion.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.promotions.impl.DefaultPromotionGroupStrategy;
import de.hybris.platform.promotions.model.PromotionGroupModel;


public class DefaultCommercePromotionGroupStrategy extends DefaultPromotionGroupStrategy
{

	@Override
	public PromotionGroupModel getDefaultPromotionGroup(final AbstractOrderModel order)
	{
		if (order == null)
		{
			return getPromotionsService().getDefaultPromotionGroup();
		}

		final PromotionGroupModel promotionGroup = order.getSite() != null ? order.getSite().getDefaultPromotionGroup() : null;
		return (promotionGroup != null ? promotionGroup : getPromotionsService().getDefaultPromotionGroup());
	}
}
