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

import static com.google.common.collect.Lists.newArrayList;
import static de.hybris.platform.assertions.BaseCommerceAssertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.storelocator.impl.DefaultGPS;
import de.hybris.platform.storelocator.impl.GoogleMapsServiceWrapper;
import de.hybris.platform.storelocator.location.Location;
import de.hybris.platform.storelocator.model.GeocodeAddressesCronJobModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GeocodingJobTest
{
	protected static final Integer BATCH_SIZE = Integer.valueOf(5);
	protected static final Integer INTERNAL_DELAY = Integer.valueOf(1);
	public static final double WROCLAW_LATITUDE = 51.110111;
	public static final double WROCLAW_LONGTITUDE = 17.031964;

	@InjectMocks
	private GeocodingJob geocodeAddressesJob;
	@Mock
	private CronJobService cronJobService;
	@Mock
	private ModelService modelService;
	private GeoWebServiceWrapper geoServiceWrapper;
	@Mock
	private PointOfServiceDao pointOfServiceDao;
	@Mock
	private PointOfServiceModel wroclaw;

	@Before
	public void setUp() throws Exception
	{
		geoServiceWrapper = new GoogleMapsServiceWrapper()
		{
			@Override
			public GPS geocodeAddress(final Location address)
			{
				return new DefaultGPS(WROCLAW_LATITUDE, WROCLAW_LONGTITUDE);
			}
		};
		geocodeAddressesJob.setGeoServiceWrapper(geoServiceWrapper);
	}

	protected GeocodeAddressesCronJobModel cronJob(final Integer batchSize, final Integer internalDelay)
	{
		final GeocodeAddressesCronJobModel cronJob = mock(GeocodeAddressesCronJobModel.class);
		given(Integer.valueOf(cronJob.getBatchSize())).willReturn(batchSize);
		given(Integer.valueOf(cronJob.getInternalDelay())).willReturn(internalDelay);
		return cronJob;
	}

	@Test
	public void shouldFailCronJobWhenUsingWrongCronJobType() throws Exception
	{
		//given
		final CronJobModel cronJob = new CronJobModel();
		//when
		final PerformResult result = geocodeAddressesJob.perform(cronJob);
		//then
		assertThat(result).failed().aborted();
	}

	@Test
	public void shouldGeocodeAddresses() throws Exception
	{
		//given
		given(pointOfServiceDao.getPosToGeocode(BATCH_SIZE.intValue())).willReturn(newArrayList(wroclaw));
		final CronJobModel cronJob = cronJob(BATCH_SIZE, INTERNAL_DELAY);
		//when
		final PerformResult result = geocodeAddressesJob.perform(cronJob);
		//then
		verify(wroclaw).setLatitude(Double.valueOf(WROCLAW_LATITUDE));
		verify(wroclaw).setLongitude(Double.valueOf(WROCLAW_LONGTITUDE));
		assertThat(result).succeded().finished();
	}
}
