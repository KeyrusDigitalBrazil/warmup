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
package de.hybris.platform.storelocator.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;


import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.storelocator.exception.PointOfServiceDaoException;


@UnitTest
public class DefaultPointOfServiceDaoTest
{
	private DefaultPointOfServiceDao pointOfServiceDao = new DefaultPointOfServiceDao();

	@Test
	public void shouldRaiseExceptionWhenSelectingItemsForGeocodingWithInvalidBatchSize() throws Exception
	{
		//when
		final Throwable throwable = catchThrowable(() -> pointOfServiceDao.getPosToGeocode(-1));
		//then
		assertThat(throwable).isInstanceOf(PointOfServiceDaoException.class).hasMessage("Batch size must be positive number");
	}
}
