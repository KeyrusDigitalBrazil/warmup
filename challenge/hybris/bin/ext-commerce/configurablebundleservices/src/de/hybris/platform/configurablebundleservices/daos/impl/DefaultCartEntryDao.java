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

package de.hybris.platform.configurablebundleservices.daos.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.configurablebundleservices.daos.OrderEntryDao;


/**
 * Default implementation of the {@link OrderEntryDao} for sub-type {@link CartEntryModel}
 */
public class DefaultCartEntryDao extends AbstractOrderEntryDao<CartModel, CartEntryModel>
{
	@Override
	public PK getItemType()
	{
		final TypeModel typeModel = getTypeService().getTypeForCode(CartEntryModel._TYPECODE);
		return typeModel.getPk();
	}
}
