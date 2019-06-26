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
package de.hybris.platform.cmsfacades.synchronization.impl;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.data.SyncJobData;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.PK;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSynchronizationFacadeTest
{
	private static final String ONLINE = "online";
	private static final String STAGED = "staged";
	private static final String APPAREL_UK = "apparel-uk";

	@InjectMocks
	@Spy
	private DefaultSynchronizationFacade defaultSynchronizationFacade;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private Converter<Optional<CronJobModel>, SyncJobData> convertor;

	@Mock
	private Populator<Optional<SyncItemJobModel>, SyncJobData> populator;

	@Mock
	private FacadeValidationService facadeValidationService;

	@Mock
	private SyncItemJobModel syncItemJobModel;

	@Mock
	private CatalogVersionModel source;

	@Mock
	private CatalogVersionModel target;

	private CronJobModel firstCronJob;

	private CronJobModel secondCronJob;

	private CronJobModel thirdCronJob;

	private ArrayList<SyncItemJobModel> synchronizationsList;

	@Before
	public void setup()
	{
		synchronizationsList = Lists.newArrayList(syncItemJobModel);

		when(target.getVersion()).thenReturn(ONLINE);

		when( syncItemJobModel.getPk() ).thenReturn( PK.fromLong(1) );
		when( syncItemJobModel.getSourceVersion() ).thenReturn(source);
		when( syncItemJobModel.getTargetVersion() ).thenReturn(target);
		when( syncItemJobModel.getActive() ).thenReturn( true );

		when(source.getSynchronizations()).thenReturn(synchronizationsList);
		when(target.getIncomingSynchronizations()).thenReturn(synchronizationsList);

		firstCronJob = new CronJobModel();
		firstCronJob.setStatus(CronJobStatus.FINISHED);
		firstCronJob.setResult(CronJobResult.SUCCESS);
		firstCronJob.setEndTime(DateTime.now().toDate());
		firstCronJob.setCreationtime(DateTime.now().toDate());
		firstCronJob.setStartTime(DateTime.now().toDate());
		firstCronJob.setModifiedtime(DateTime.now().toDate());

		secondCronJob = new CronJobModel();
		secondCronJob.setStatus(CronJobStatus.UNKNOWN);
		secondCronJob.setModifiedtime(DateTime.now().plusMinutes(1).toDate());
		secondCronJob.setEndTime(DateTime.now().plusMinutes(1).toDate());
		secondCronJob.setCreationtime(DateTime.now().plusMinutes(1).toDate());
		secondCronJob.setStartTime(DateTime.now().plusMinutes(1).toDate());

		thirdCronJob = new CronJobModel();
		thirdCronJob.setStatus(CronJobStatus.PAUSED);
		thirdCronJob.setModifiedtime(DateTime.now().plusMinutes(2).toDate());
		thirdCronJob.setEndTime(DateTime.now().plusMinutes(2).toDate());
		thirdCronJob.setCreationtime(DateTime.now().plusMinutes(2).toDate());
		thirdCronJob.setStartTime(DateTime.now().plusMinutes(2).toDate());

		doNothing().when(facadeValidationService).validate(Mockito.any(), Mockito.any());
	}

	@Test
	public void shouldGetStatus()
	{
		final SyncRequestData request = createRequestData();
		when(catalogVersionService.getCatalogVersion(APPAREL_UK, STAGED)).thenReturn(source);
		final SyncJobData value = createSyncJobData(CronJobStatus.RUNNING.name(), new Date(), CronJobResult.UNKNOWN.name(),
				new Date(), new Date(), new Date(), new Date());
		when(convertor.convert(Mockito.any())).thenReturn(value);

		when(syncItemJobModel.getCronJobs()).thenReturn(Lists.newArrayList(firstCronJob));

		final SyncJobData result = defaultSynchronizationFacade.getSynchronizationByCatalogSourceTarget(request);

		//asserts
		verify(defaultSynchronizationFacade).findTheCronJob(APPAREL_UK, STAGED, ONLINE);
		verify(defaultSynchronizationFacade.getSyncJobConverter()).convert(Optional.of(firstCronJob));

		assertEquals(CronJobStatus.RUNNING.name(), result.getSyncStatus());
		assertEquals(CronJobResult.UNKNOWN.name(), result.getSyncResult());
		assertNotNull(result.getCreationDate());
		assertNotNull(result.getEndDate());
		assertNotNull(result.getStartDate());
		assertNotNull(result.getLastModifiedDate());

	}


	@Test
	public void shouldNotHaveCronJobs()
	{
		final SyncRequestData syncJobRequest = createRequestData();
		when(catalogVersionService.getCatalogVersion(APPAREL_UK, STAGED)).thenReturn(source);
		when(convertor.convert(Mockito.any())).thenReturn(createSyncJobData(null, null, null, null, null, null, null));
		when(syncItemJobModel.getCronJobs()).thenReturn(Lists.newArrayList());

		final SyncJobData result = defaultSynchronizationFacade.getSynchronizationByCatalogSourceTarget(syncJobRequest);

		//asserts
		verify(defaultSynchronizationFacade).findTheCronJob(APPAREL_UK, STAGED, ONLINE);
		verify(defaultSynchronizationFacade.getSyncJobConverter()).convert(Optional.empty());

		assertNull(result.getSyncResult());
		assertNull(result.getSyncStatus());
		assertNull(result.getCreationDate());
		assertNull(result.getEndDate());
		assertNull(result.getStartDate());
		assertNull(result.getLastModifiedDate());

	}

	@Test
	public void shouldNotHaveSynchronization_forCatalog()
	{
		final SyncRequestData request = createRequestData();
		when(catalogVersionService.getCatalogVersion(APPAREL_UK, STAGED)).thenReturn(source);

		//empty sync list
		source.setSynchronizations(Lists.newArrayList());

		when(convertor.convert(Mockito.any())).thenReturn(createSyncJobData(null, null, null, null, null, null, null));
		defaultSynchronizationFacade.getSynchronizationByCatalogSourceTarget(request);

		//asserts
		verify(defaultSynchronizationFacade).findTheCronJob(APPAREL_UK, STAGED, ONLINE);
		verify(defaultSynchronizationFacade.getSyncJobConverter()).convert(Optional.empty());
	}

	@Test
	public void shouldHaveMultipleCronJobs()
	{
		final SyncRequestData request = createRequestData();
		when(catalogVersionService.getCatalogVersion(APPAREL_UK, STAGED)).thenReturn(source);


		when(convertor.convert(Mockito.any()))
		.thenReturn(createSyncJobData(CronJobStatus.PAUSED.name(), DateTime.now().toDate(), CronJobResult.UNKNOWN.name(),
				DateTime.now().toDate(), DateTime.now().toDate(), DateTime.now().toDate(), DateTime.now().toDate()));

		when(syncItemJobModel.getCronJobs()).thenReturn(Lists.newArrayList(secondCronJob, thirdCronJob, firstCronJob));

		defaultSynchronizationFacade.getSynchronizationByCatalogSourceTarget(request);

		//asserts
		verify(defaultSynchronizationFacade).findTheCronJob(APPAREL_UK, STAGED, ONLINE);
		verify(defaultSynchronizationFacade.getSyncJobConverter()).convert(Optional.of(thirdCronJob));
	}

	@Test
	public void lastSynchronizationByCatalogTarget_ShouldGetStatus()
	{
		// Arrange
		final SyncRequestData request = createRequestByTargetData();
		when(catalogVersionService.getCatalogVersion(APPAREL_UK, ONLINE)).thenReturn(target);
		final SyncJobData value = createSyncJobData(CronJobStatus.RUNNING.name(), new Date(), CronJobResult.SUCCESS.name(),
				new Date(), new Date(), new Date(), new Date());
		when(convertor.convert(Mockito.any())).thenReturn(value);

		// Act
		final SyncJobData result = defaultSynchronizationFacade.getLastSynchronizationByCatalogTarget(request);

		// Assert
		verify(defaultSynchronizationFacade.getSyncJobConverter()).convert(Mockito.any());
		verify(defaultSynchronizationFacade.getSyncItemJobToSyncJobDataPopulator()).populate(Mockito.any(), eq(result));

		assertEquals(CronJobStatus.RUNNING.name(), result.getSyncStatus());
		assertEquals(CronJobResult.SUCCESS.name(), result.getSyncResult());
		assertThat(result.getCreationDate(), is(notNullValue()));
		assertThat(result.getEndDate(), is(notNullValue()));
		assertThat(result.getStartDate(), is(notNullValue()));
		assertThat(result.getLastModifiedDate(), is(notNullValue()));
	}

	@Test
	public void lastSynchronizationByCatalogTarget_ShouldReturnEmpty_WhenThereAreNoMatchingCronJobs()
	{
		// Arrange
		final SyncRequestData request = createRequestByTargetData();
		when(catalogVersionService.getCatalogVersion(APPAREL_UK, ONLINE)).thenReturn(target);
		when(convertor.convert(Mockito.any())).thenReturn(createSyncJobData(null, null, null, null, null, null, null));
		when(syncItemJobModel.getCronJobs()).thenReturn(Lists.newArrayList());

		// Act
		final SyncJobData result = defaultSynchronizationFacade.getLastSynchronizationByCatalogTarget(request);

		// Assert
		verify(defaultSynchronizationFacade.getSyncJobConverter()).convert(Optional.empty());

		assertNull(result.getSyncResult());
		assertNull(result.getSyncStatus());
		assertNull(result.getCreationDate());
		assertNull(result.getEndDate());
		assertNull(result.getStartDate());
		assertNull(result.getLastModifiedDate());
	}

	@Test
	public void lastSynchronizationByCatalogTarget_ShouldReturnLatestCronJob_OutOfAllMatchingCatalogVersions()
	{
		// Arrange
		final SyncRequestData request = createRequestByTargetData();
		when(catalogVersionService.getCatalogVersion(APPAREL_UK, ONLINE)).thenReturn(target);
		when(syncItemJobModel.getCronJobs()).thenReturn(Lists.newArrayList(secondCronJob, thirdCronJob, firstCronJob));

		// Act
		defaultSynchronizationFacade.getLastSynchronizationByCatalogTarget(request);

		// Assert
		verify(defaultSynchronizationFacade.getSyncJobConverter()).convert(Optional.of(firstCronJob));
	}

	protected SyncJobData createSyncJobData(final String status, final Date lastModified, final String syncResult,
		final Date startDate, final Date creationDate, final Date modifiedDate, final Date endDate )
	{
		return createSyncJobData(status, lastModified, syncResult, startDate, creationDate, modifiedDate, endDate, null, null);
	}

	protected SyncJobData createSyncJobData(final String status, final Date lastModified, final String syncResult,
			final Date startDate, final Date creationDate, final Date modifiedDate, final Date endDate, final String sourceCatalogVersion, final String targetCatalogVersion)
	{
		final SyncJobData syncJobData = new SyncJobData();
		syncJobData.setSyncStatus(status);
		syncJobData.setLastModifiedDate(lastModified);
		syncJobData.setCreationDate(creationDate);
		syncJobData.setEndDate(endDate);
		syncJobData.setStartDate(startDate);
		syncJobData.setSyncResult(syncResult);
		syncJobData.setSourceCatalogVersion(sourceCatalogVersion);
		syncJobData.setSourceCatalogVersion(targetCatalogVersion);

		return syncJobData;
	}

	protected SyncRequestData createRequestData()
	{
		final SyncRequestData syncJobRequest = new SyncRequestData();
		syncJobRequest.setCatalogId(APPAREL_UK);
		syncJobRequest.setSourceVersionId(STAGED);
		syncJobRequest.setTargetVersionId(ONLINE);
		return syncJobRequest;
	}

	protected SyncRequestData createRequestByTargetData()
	{
		final SyncRequestData syncJobRequest = new SyncRequestData();
		syncJobRequest.setCatalogId(APPAREL_UK);
		syncJobRequest.setTargetVersionId(ONLINE);

		return syncJobRequest;
	}
}
