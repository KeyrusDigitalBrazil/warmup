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
package de.hybris.platform.storelocator;

import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.impl.DefaultGPS;
import de.hybris.platform.storelocator.impl.GeometryUtils;
import de.hybris.platform.test.RunnerCreator;
import de.hybris.platform.test.TestThreadsHolder;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


public class GeometryMgrTest
{

	//native threads may be OS limited
	private static final int THREAD_LIMIT = 250;

	/**
	 * the following example tests the geodesy calculations. Here the equator's length is examined basing on the global
	 * international GPS ellipsoid model WGS84
	 */
	@Test
	public void testElipticalDistanceCalculator()
	{
		try
		{
			GPS from = new DefaultGPS();
			//we are looking for the equator's length
			from = from.create("0\u00b00'0\"N", "0\u00b00'0\"E");
			final GPS gspTo = from.create("0\u00b00'0\"N", "180\u00b0E");
			//the error is probably big.. 100km
			//we should get half of equator's length
			Assert.assertEquals(20000, GeometryUtils.getElipticalDistanceKM(from, gspTo), 100);

		}
		catch (final GeoLocatorException e)
		{
			Assert.fail();
		}

	}

	/**
	 * The following test examines the behavior of the GeometryMgr.getElipticalDistanceKM() loaded with 250 multithreaded
	 * queries
	 *
	 * @throws Throwable
	 * @throws GeoLocatorException
	 */
	@Test
	public void testLoadedElipticalDistanceCalculator() throws Throwable, GeoLocatorException
	{
		//init points - two points on the same latitude,  1.9 degrees offset between them
		final double latA = 34.123456;
		double lonA = 0;
		final double latB = latA;
		double lonB = lonA + 1.9;

		final List<Double> lonAList = new ArrayList<Double>();
		final List<Double> lonBList = new ArrayList<Double>();

		for (int i = 0; i < THREAD_LIMIT; i++)
		{
			//shifting the points at the same latitude should give 250 test cases with the same resulting distance
			lonAList.add(Double.valueOf(lonA));
			lonBList.add(Double.valueOf(lonB));

			lonA += 1.3;
			lonB += 1.3;
			//assures valid crossing of 180 degree longitude.
			if (lonA > 180)
			{
				lonA = -360 + lonA;
			}
			if (lonB > 180)
			{
				lonB = -360 + lonB;
			}
		}

		final RunnerCreator<Runnable> runnerCreator = new RunnerCreator()
		{
			@Override
			public Runnable newRunner(final int threadNumber)
			{
				final Double lonAFromList = lonAList.get(threadNumber);
				final Double lonBFromList = lonBList.get(threadNumber);

				return new ConcurrentElipticalDistanceKMRunner(latA, lonAFromList.doubleValue(), latB, lonBFromList.doubleValue());
			}
		};

		final TestThreadsHolder runners = new TestThreadsHolder(THREAD_LIMIT, runnerCreator);
		try
		{
			runners.startAll();
		}
		catch( IllegalStateException ex)
		{
			Assert.fail(ex.getMessage());
		}
	}

	class ConcurrentElipticalDistanceKMRunner implements Runnable
	{
		private GPS one;
		private GPS two;

		public ConcurrentElipticalDistanceKMRunner(final double latA, final double lonA, final double latB, final double lonB)
		{
			final GPS creator = new DefaultGPS();
			this.setOne(creator.create(latA, lonA));
			this.setTwo(creator.create(latB, lonB));
		}

		@Override
		public void run()
		{
			final double distance = GeometryUtils.getElipticalDistanceKM(this.getOne(), this.getTwo());
			final double abs = Math.abs(175.27422 - distance);
			if( abs >= 0.00001 )
			{
				throw new IllegalStateException("Every thread calculating at different starting point should be very close to the experimental value");
			}
		}

		public GPS getOne()
		{
			return one;
		}

		public void setOne(final GPS one)
		{
			this.one = one;
		}

		public GPS getTwo()
		{
			return two;
		}

		public void setTwo(final GPS two)
		{
			this.two = two;
		}
	}


}
