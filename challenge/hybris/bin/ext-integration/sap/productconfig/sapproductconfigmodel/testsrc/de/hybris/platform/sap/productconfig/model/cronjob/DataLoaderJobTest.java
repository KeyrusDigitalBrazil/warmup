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
package de.hybris.platform.sap.productconfig.model.cronjob;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.sap.productconfig.model.enums.DataLoadTriggerMode;
import de.hybris.platform.sap.productconfig.model.impl.DataLoaderConfigurationHelperImpl;
import de.hybris.platform.sap.productconfig.model.impl.DataLoaderManagerContainerImpl;
import de.hybris.platform.sap.productconfig.model.intf.DataLoader;
import de.hybris.platform.sap.productconfig.model.model.CPQDataloadStatusModel;
import de.hybris.platform.sap.productconfig.model.model.DataLoaderCronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.custdev.projects.fbs.slc.dataloader.settings.IDataloaderConfiguration;
import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderManager;
import com.sap.sxe.loader.download.Message;


@SuppressWarnings("javadoc")
@UnitTest
public class DataLoaderJobTest
{
	private static final String CODE = "A";
	private static final String CODE2 = "B";

	private DataLoaderJob classUnderTest;
	private DataLoaderCronJobModel dataLoaderCronJobModel;
	private SAPConfigurationModel sapConfigurationModel;
	private DataLoaderManagerContainerImpl container;

	@Mock
	private CronJobService mockedCronJobService;
	private UnitTestPropertyAccess propertyAccessFacade;
	@Mock
	private DataloaderManager mockedDataloaderManager;
	@Mock
	private DataLoader mockedDataLoader;
	@Mock
	private ModelService mockedModelService;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new DataLoaderJob();
		classUnderTest.setCronJobService(mockedCronJobService);

		dataLoaderCronJobModel = new DataLoaderCronJobModel();
		dataLoaderCronJobModel.setCode(CODE);
		sapConfigurationModel = new SAPConfigurationModel();
		dataLoaderCronJobModel.setSapConfiguration(sapConfigurationModel);
		sapConfigurationModel.setSapproductconfig_sapRFCDestination("TEST RFC DEST");
		sapConfigurationModel.setSapproductconfig_sapServer(new SAPRFCDestinationModel());

		propertyAccessFacade = new UnitTestPropertyAccess();
		classUnderTest.setPropertyAccessFacade(propertyAccessFacade);
		container = new DataLoaderManagerContainerImpl();
		classUnderTest.setDataLoaderManagerContainer(container);
		final DataLoaderConfigurationHelperImpl dlHelper = new DataLoaderConfigurationHelperForTest();
		classUnderTest.setDataLoaderConfigurationHelper(dlHelper);
		classUnderTest.setMessageListener(new DefaultDataloaderMessageListenerImpl());

		classUnderTest.setProgressListener(new DefaultDataloaderProgressListenerImpl());

		// Real DataloaderManager has JCO dependency, to replace it with MOCK by mocking factory method, so UnitTest can run without JCO
		classUnderTest = spy(classUnderTest);
		willReturn(mockedDataloaderManager).given(classUnderTest).createDataloaderManager(
				Mockito.any(IDataloaderConfiguration.class));

		classUnderTest.setDataLoader(mockedDataLoader);
		classUnderTest.setModelService(mockedModelService);
	}

	@Test
	public void testMessageListener()
	{
		classUnderTest.getMessageListener().messageReported(
				new Message(DataLoaderJobTest.class.getSimpleName(), new Date().toString(), null, "a test Message"));
		assertTrue("No exception so far", true);
	}


	@Test
	public void testStartDeltaloadAfterInitialTrue()
	{
		assertTrue(classUnderTest.getPropertyAccessFacade().getStartDeltaloadAfterInitial());
	}

	@Test
	public void testStartDeltaloadAfterInitialFalse()
	{
		propertyAccessFacade.setStartDeltaloadAfterInitial(false);
		assertFalse(classUnderTest.getPropertyAccessFacade().getStartDeltaloadAfterInitial());
	}


	@Test
	public void testAbortNoRunningJobs()
	{
		assertFalse("No need to abort (no jobs)", classUnderTest.isAbortNeeded(dataLoaderCronJobModel));
	}

	@Test
	public void testAbortDifferentType()
	{
		final CronJobModel anotherCronJob = new CronJobModel();
		anotherCronJob.setCode(CODE);
		Mockito.when(mockedCronJobService.getRunningOrRestartedCronJobs()).thenReturn(Collections.singletonList(anotherCronJob));
		assertFalse("No need to abort (no jobs with same type)", classUnderTest.isAbortNeeded(dataLoaderCronJobModel));
	}

	@Test
	public void testAbortSameType()
	{
		givenAbortNeeded();
		assertTrue("Need to abort", classUnderTest.isAbortNeeded(dataLoaderCronJobModel));
	}

	protected void givenAbortNeeded()
	{
		final DataLoaderCronJobModel anotherDataLoaderCronJob = new DataLoaderCronJobModel();
		anotherDataLoaderCronJob.setCode(CODE2);
		Mockito.when(mockedCronJobService.getRunningOrRestartedCronJobs()).thenReturn(
				Collections.singletonList(anotherDataLoaderCronJob));
	}

	@Test
	public void testAbortSameJob()
	{
		final DataLoaderCronJobModel anotherDataLoaderCronJob = new DataLoaderCronJobModel();
		anotherDataLoaderCronJob.setCode(CODE);
		Mockito.when(mockedCronJobService.getRunningOrRestartedCronJobs()).thenReturn(
				Collections.singletonList(anotherDataLoaderCronJob));
		assertFalse("No Need to abort", classUnderTest.isAbortNeeded(dataLoaderCronJobModel));
	}



	@Test
	public void testIsResumePerformedInitialState()
	{
		assertFalse(classUnderTest.isResumePerformed());
	}

	@Test
	public void testIsResumePerformed()
	{
		container.setResumePerformed(true);
		assertTrue(classUnderTest.isResumePerformed());
	}


	@Test
	public void testPrepareDataloadManager()
	{
		final DataloaderManager manager = classUnderTest.prepareDataloadManager(dataLoaderCronJobModel);
		assertNotNull(manager);
		verify(manager).setOrReplaceMessageListener(classUnderTest.getMessageListener());
		verify(manager).setOrReplaceProgressListener(classUnderTest.getProgressListener());
		assertSame(sapConfigurationModel, classUnderTest.getProgressListener().getSapConfiguration());
	}

	@Test
	public void testInitializeDataLoaderManager()
	{
		final DataloaderManager manager = classUnderTest.initializeDataLoaderManager(dataLoaderCronJobModel);
		assertSame(manager, classUnderTest.getDataLoaderManagerContainer().getDataLoaderManager());
	}

	@Test()
	public void testInitializeDataLoaderManagerNull()
	{
		givenAbortNeeded();
		assertNull(classUnderTest.initializeDataLoaderManager(dataLoaderCronJobModel));
	}

	@Test
	public void testPerformStartInitialOnly()
	{
		dataLoaderCronJobModel.setTriggerMode(DataLoadTriggerMode.STARTINITIAL);
		propertyAccessFacade.setStartDeltaloadAfterInitial(false);
		final PerformResult result = classUnderTest.perform(dataLoaderCronJobModel);
		assertEquals(CronJobResult.SUCCESS, result.getResult());
		assertEquals(CronJobStatus.FINISHED, result.getStatus());
		verify(mockedDataLoader).performInitialLoad(sapConfigurationModel, mockedDataloaderManager, mockedModelService);
	}

	@Test
	public void testPerformStartInitialStopedManually()
	{
		dataLoaderCronJobModel.setTriggerMode(DataLoadTriggerMode.STARTINITIAL);
		propertyAccessFacade.setStartDeltaloadAfterInitial(true);
		given(mockedDataloaderManager.isStoppedDownloadManually()).willReturn(true);
		final PerformResult result = classUnderTest.perform(dataLoaderCronJobModel);
		assertEquals(CronJobResult.SUCCESS, result.getResult());
		assertEquals(CronJobStatus.FINISHED, result.getStatus());
		verify(mockedDataLoader).performInitialLoad(sapConfigurationModel, mockedDataloaderManager, mockedModelService);
	}


	@Test
	public void testPerformStartInitialAndDelta()
	{
		givenDeltaLoadAllowed();
		dataLoaderCronJobModel.setTriggerMode(DataLoadTriggerMode.STARTINITIAL);
		propertyAccessFacade.setStartDeltaloadAfterInitial(true);
		final PerformResult result = classUnderTest.perform(dataLoaderCronJobModel);
		assertEquals(CronJobResult.SUCCESS, result.getResult());
		assertEquals(CronJobStatus.FINISHED, result.getStatus());
		verify(mockedDataLoader).performInitialLoad(sapConfigurationModel, mockedDataloaderManager, mockedModelService);
		verify(mockedDataLoader).performDeltaLoad(sapConfigurationModel, mockedDataloaderManager, mockedModelService);
	}

	@Test
	public void testPerformStartDelta()
	{
		givenDeltaLoadAllowed();
		dataLoaderCronJobModel.setTriggerMode(DataLoadTriggerMode.STARTDELTA);
		final PerformResult result = classUnderTest.perform(dataLoaderCronJobModel);
		assertEquals(CronJobResult.SUCCESS, result.getResult());
		assertEquals(CronJobStatus.FINISHED, result.getStatus());
		verify(mockedDataLoader).performDeltaLoad(sapConfigurationModel, mockedDataloaderManager, mockedModelService);
	}

	private void givenDeltaLoadAllowed()
	{
		final CPQDataloadStatusModel dataloadStatus = new CPQDataloadStatusModel();
		dataloadStatus.setCpqLastInitialLoadStartTime(new Date(200));
		dataloadStatus.setCpqLastInitialLoadEndTime(new Date(400));
		sapConfigurationModel.setSapproductconfig_cpqDataloadStatus(dataloadStatus);
	}

	@Test
	public void testPerformStartDeltaButNoInitialBefore()
	{
		dataLoaderCronJobModel.setTriggerMode(DataLoadTriggerMode.STARTDELTA);
		final PerformResult result = classUnderTest.perform(dataLoaderCronJobModel);
		assertEquals(CronJobResult.DATALOAD_NO_INITIAL_DOWNLOAD, result.getResult());
		assertEquals(CronJobStatus.FINISHED, result.getStatus());
	}

	@Test
	public void testPerformStartInitialAlreadyRunning()
	{
		dataLoaderCronJobModel.setTriggerMode(DataLoadTriggerMode.STARTINITIAL);
		givenAbortNeeded();
		final PerformResult result = classUnderTest.perform(dataLoaderCronJobModel);
		assertEquals(CronJobResult.DATALOAD_ALREADY_RUNNING, result.getResult());
		assertEquals(CronJobStatus.FINISHED, result.getStatus());
	}

	@Test
	public void testPerformStartInitialWoConfiguration()
	{
		thrown.expect(IllegalArgumentException.class);
		dataLoaderCronJobModel.setSapConfiguration(null);
		dataLoaderCronJobModel.setTriggerMode(DataLoadTriggerMode.STARTINITIAL);
		classUnderTest.perform(dataLoaderCronJobModel);
	}



	@Test
	public void testPerformStartDeltaWrongMode()
	{
		thrown.expect(IllegalStateException.class);
		dataLoaderCronJobModel.setTriggerMode(DataLoadTriggerMode.STARTINITIAL);
		classUnderTest.startDeltaLoad(DataLoadTriggerMode.STARTINITIAL, sapConfigurationModel, classUnderTest
				.getDataLoaderManagerContainer().getDataLoaderManager());
	}

	@Test
	public void testPerformResume()
	{
		givenDeltaLoadAllowed();
		dataLoaderCronJobModel.setTriggerMode(DataLoadTriggerMode.RESUME);
		final PerformResult result = classUnderTest.perform(dataLoaderCronJobModel);
		assertEquals(CronJobResult.SUCCESS, result.getResult());
		assertEquals(CronJobStatus.FINISHED, result.getStatus());
	}

	@Test
	public void testPerformResumeNoConfig()
	{
		classUnderTest.getDataLoaderManagerContainer().setResumePerformed(false);
		dataLoaderCronJobModel.setSapConfiguration(null);
		dataLoaderCronJobModel.setTriggerMode(DataLoadTriggerMode.RESUME);
		final PerformResult result = classUnderTest.perform(dataLoaderCronJobModel);
		assertEquals(CronJobResult.DATALOAD_NO_INITIAL_DOWNLOAD, result.getResult());
		assertEquals(CronJobStatus.FINISHED, result.getStatus());
	}

	@Test
	public void testPerformResumeAlreadyDone()
	{
		dataLoaderCronJobModel.setTriggerMode(DataLoadTriggerMode.RESUME);
		classUnderTest.getDataLoaderManagerContainer().setResumePerformed(true);
		final PerformResult result = classUnderTest.perform(dataLoaderCronJobModel);
		assertEquals(CronJobResult.DATALOAD_RESUME_ATTEMPT_DONE, result.getResult());
		assertEquals(CronJobStatus.FINISHED, result.getStatus());
	}




	@Test
	public void testPerformStartDeltaWoConfiguration()
	{
		thrown.expect(IllegalArgumentException.class);
		dataLoaderCronJobModel.setSapConfiguration(null);
		dataLoaderCronJobModel.setTriggerMode(DataLoadTriggerMode.STARTDELTA);
		classUnderTest.perform(dataLoaderCronJobModel);
	}


	@Test
	public void testIsDeltaLoadAllowedTrue()
	{
		givenDeltaLoadAllowed();
		assertTrue(classUnderTest.isDeltaLoadStartAllowed(sapConfigurationModel));
	}

	@Test
	public void testIsDeltaLoadAllowedNoInitialLoad()
	{
		assertFalse(classUnderTest.isDeltaLoadStartAllowed(sapConfigurationModel));
	}

	@Test
	public void testIsDeltaLoadAllowedInitialJustStarted()
	{
		final CPQDataloadStatusModel dataloadStatus = new CPQDataloadStatusModel();
		dataloadStatus.setCpqLastInitialLoadStartTime(new Date(200));
		sapConfigurationModel.setSapproductconfig_cpqDataloadStatus(dataloadStatus);
		assertFalse(classUnderTest.isDeltaLoadStartAllowed(sapConfigurationModel));
	}

	@Test
	public void testIsDeltaLoadAllowedInitialJustReStarted()
	{
		final CPQDataloadStatusModel dataloadStatus = new CPQDataloadStatusModel();
		dataloadStatus.setCpqLastInitialLoadStartTime(new Date(200));
		dataloadStatus.setCpqLastInitialLoadEndTime(new Date(100));
		sapConfigurationModel.setSapproductconfig_cpqDataloadStatus(dataloadStatus);
		assertFalse(classUnderTest.isDeltaLoadStartAllowed(sapConfigurationModel));
	}

	@Test
	public void testCheckForResumeAlreadyDone()
	{
		classUnderTest.getDataLoaderManagerContainer().setResumePerformed(true);
		final PerformResult result = classUnderTest.checkForResume(dataLoaderCronJobModel, DataLoadTriggerMode.RESUME);
		assertEquals(CronJobResult.DATALOAD_RESUME_ATTEMPT_DONE, result.getResult());
		assertEquals(CronJobStatus.FINISHED, result.getStatus());
	}

	@Test
	public void testCheckForResumeWrongMode()
	{
		classUnderTest.getDataLoaderManagerContainer().setResumePerformed(false);
		assertNull(classUnderTest.checkForResume(dataLoaderCronJobModel, DataLoadTriggerMode.STARTINITIAL));
		assertFalse(classUnderTest.getDataLoaderManagerContainer().isResumePerformed());
	}

	@Test
	public void testCheckForResumeAlreadyOK()
	{
		classUnderTest.getDataLoaderManagerContainer().setResumePerformed(false);
		assertNull(classUnderTest.checkForResume(dataLoaderCronJobModel, DataLoadTriggerMode.RESUME));
		assertTrue(classUnderTest.getDataLoaderManagerContainer().isResumePerformed());
	}


	private static class DataLoaderConfigurationHelperForTest extends DataLoaderConfigurationHelperImpl
	{
		@Override
		protected boolean fieldsAvailable(final Class<?> clazz)
		{
			return false;
		}

	}
}
