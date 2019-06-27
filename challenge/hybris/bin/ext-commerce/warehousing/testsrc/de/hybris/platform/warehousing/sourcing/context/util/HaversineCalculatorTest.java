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
package de.hybris.platform.warehousing.sourcing.context.util;

import org.junit.Assert;
import org.junit.Test;


public class HaversineCalculatorTest
{
	private static Double DISTANCE_SMALL = 0.549310944307586;
	private static Double DISTANCE_LARGE = 3042.8996222576793;

	@Test
	public void shouldReturnCorrectDistanceSmall()
	{
		final double lat1 = 38.898556;
		final double lon1 = -77.037852;
		final double lat2 = 38.897147;
		final double lon2 = -77.043934;

		final Double distance = HaversineCalculator.calculate(lat1, lon1, lat2, lon2);
		Assert.assertEquals(DISTANCE_SMALL, distance);
	}

	@Test
	public void shouldReturnCorrectDistanceLarge()
	{
		final double lat1 = 38.898556;
		final double lon1 = -77.037852;
		final double lat2 = 19.3200988;
		final double lon2 = -99.1521845;

		final Double distance = HaversineCalculator.calculate(lat1, lon1, lat2, lon2);
		Assert.assertEquals(DISTANCE_LARGE, distance);
	}
}
