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

package de.hybris.platform.configurablebundleservices.bundle.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.order.impl.OrderEntryModifiableChecker;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import javax.annotation.Nonnull;


/**
 * Tests whether an order/cart entry can be remove or not
 */
public class BundleOrderEntryModifiableChecker extends OrderEntryModifiableChecker
{

	@Override
	public boolean canModify(@Nonnull final AbstractOrderEntryModel entryToUpdate)
	{
		validateParameterNotNullStandardMessage("entryToUpdate", entryToUpdate);
		if (entryToUpdate.getBundleNo() != null && entryToUpdate.getBundleNo().intValue() > 0)
		{
			return false;
		}
		return super.canModify(entryToUpdate);
	}
}
