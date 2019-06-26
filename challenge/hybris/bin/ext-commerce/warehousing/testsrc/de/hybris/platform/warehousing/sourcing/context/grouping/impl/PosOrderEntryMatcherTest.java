/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.sourcing.context.grouping.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import org.junit.Assert;
import org.junit.Test;


public class PosOrderEntryMatcherTest
{
	private final PosOrderEntryMatcher matcher = new PosOrderEntryMatcher();

	@Test
	public void shouldGetNullMatchingObject_WhenNullInEntry()
	{
		final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();

		final PointOfServiceModel pos = matcher.getMatchingObject(orderEntry);
		Assert.assertNull(pos);
	}

	@Test
	public void shouldGetPosMatchingObject()
	{
		final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();
		final PointOfServiceModel pos = new PointOfServiceModel();
		orderEntry.setDeliveryPointOfService(pos);

		final PointOfServiceModel matchedObject = matcher.getMatchingObject(orderEntry);
		Assert.assertEquals(pos, matchedObject);
	}
}
