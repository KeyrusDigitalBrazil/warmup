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

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.cronjob.constants.CronJobConstants;
import de.hybris.platform.cronjob.jalo.CronJobManager;
import de.hybris.platform.cronjob.jalo.Job;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.JaloAbstractTypeException;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.storelocator.impl.CommerceMockGeoWebServiceWrapper;
import de.hybris.platform.storelocator.jalo.GeocodeAddressesCronJob;
import de.hybris.platform.storelocator.model.GeocodeAddressesCronJobModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class GeocodingJobIT extends BaseCommerceBaseTest
{
	public static final Integer BATCH_SIZE = Integer.valueOf(6);
	public static final Integer INTERNAL_DELAY = Integer.valueOf(1);
	public static final String GEOCODING_CRON_JOB_CODE = "testCronJob";
	@Resource
	private GeocodingJob geocodeAddressesJob;

	@Resource(name = "defaultCommerceMockGeoWebServiceWrapper")
	private CommerceMockGeoWebServiceWrapper defaultCommerceMockGeoWebServiceWrapper;

	@Resource
	private CronJobService cronJobService;

	@Resource
	private ModelService modelService;
	@Resource
	private PointOfServiceDao pointOfServiceDao;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createGeocodingCronJob(BATCH_SIZE, INTERNAL_DELAY);
		createTestPosEntries();
	}

	protected void createTestPosEntries() throws Exception
	{
		importCsv("/import/test/PointOfServiceSampleTestData.csv", "UTF-8");

		final Collection<PointOfServiceModel> posToGeocode = pointOfServiceDao.getPosToGeocode();
		assertThat(posToGeocode).hasSize(BATCH_SIZE.intValue())
				.as("Initially, we expect all entries to be submitted for geocoding");
	}

	protected void createGeocodingCronJob(final Integer batchSize, final Integer internalDelay)
			throws JaloGenericCreationException, JaloAbstractTypeException
	{
		final CronJobManager cronjobManager = (CronJobManager) JaloSession.getCurrentSession().getExtensionManager()
				.getExtension(CronJobConstants.EXTENSIONNAME);
		final Job job = cronjobManager.createBatchJob("job");

		final ComposedType geocodeAddressesCronJob = jaloSession.getTypeManager().getComposedType(GeocodeAddressesCronJob.class);

		final Map<String, Object> values = new HashMap<String, Object>();
		values.put(GeocodeAddressesCronJob.CODE, GEOCODING_CRON_JOB_CODE);
		values.put(GeocodeAddressesCronJob.BATCHSIZE, batchSize);
		values.put(GeocodeAddressesCronJob.INTERNALDELAY, internalDelay);
		values.put(GeocodeAddressesCronJob.ACTIVE, Boolean.FALSE);
		values.put(GeocodeAddressesCronJob.JOB, job);
		geocodeAddressesCronJob.newInstance(values);
	}

	protected GeocodeAddressesCronJobModel getGeocodingCronJob()
	{
		return (GeocodeAddressesCronJobModel) cronJobService.getCronJob(GEOCODING_CRON_JOB_CODE);
	}

	@Test
	public void shouldPerformGeocodingInBatches() throws Exception
	{
		//assume
		Assume.assumeTrue(CommerceMockGeoWebServiceWrapper.class.equals(geocodeAddressesJob.getGeoServiceWrapper().getClass()));
		//given
		final Collection<PointOfServiceModel> posToGeocode = pointOfServiceDao.getPosToGeocode();
		final int totalNotGeocodedPOS = posToGeocode.size();
		final int expectedErrorsDuringGeocoding = posToGeocode.stream().filter(this::isMockedForFailure).collect(toList()).size();
		//when
		geocodeAddressesJob.perform(getGeocodingCronJob());
		//then
		final int oustandingNotGeocodedPOS = pointOfServiceDao.getPosToGeocode().size();
		final int recentlyGeocodedPos = totalNotGeocodedPOS - oustandingNotGeocodedPOS;
		assertThat(recentlyGeocodedPos).isEqualTo(BATCH_SIZE.intValue() - expectedErrorsDuringGeocoding)
				.as("Geocoded entries amount must be equal to job's batch size");
	}

	protected boolean isMockedForFailure(final PointOfServiceModel pos)
	{
		return "exception".equalsIgnoreCase(pos.getAddress().getTown());
	}
}
