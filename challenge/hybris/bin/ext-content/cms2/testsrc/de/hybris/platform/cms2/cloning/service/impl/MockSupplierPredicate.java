/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cms2.cloning.service.impl;

import de.hybris.platform.core.model.ItemModel;

import java.util.function.BiPredicate;
import java.util.function.Supplier;


/**
 * Mock class to mimic the presetValue supplier and predicate methods
 */
public class MockSupplierPredicate implements BiPredicate<ItemModel, String>, Supplier<String>
{

	@Override
	public String get()
	{
		return null;
	}

	@Override
	public boolean test(final ItemModel t, final String u)
	{
		return false;
	}

}
