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
package de.hybris.platform.sap.productconfig.backoffice.ssc.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.sap.productconfig.model.enums.DataLoadTriggerMode;
import de.hybris.platform.sap.productconfig.model.impl.DataLoaderCronjobParametersImpl;
import de.hybris.platform.sap.productconfig.model.model.CPQDataloadStatusModel;
import de.hybris.platform.sap.productconfig.model.model.DataLoaderCronJobModel;
import de.hybris.platform.sap.productconfig.model.model.DataLoaderStopCronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.ActionResult.StatusFlag;


@UnitTest
public class DataloaderInitialLoadItemActionTest
{

	private static final String ERROR_MESSAGE = "An Error message";
	private static final String SUCCESS_MESSAGE = "An Success message";
	private static final String CONFIRM_MESSAGE = "A confirmation Message";
	private static final String DL_NOT_STOPEED_MESSAGE = "Dataload not stopped message";
	@Spy
	private DataloaderInitialLoadItemAction classUnderTest;
	private ActionResult<String> result;
	@Mock
	private ActionContext<SAPConfigurationModel> mockedContext;
	@Mock
	private CronJobService mockedCronJobService;
	@Mock
	private ModelService mockedModelService;
	@Spy
	private DataLoaderCronjobParametersImpl mockedDataLoaderCronJobParameters;
	private DataLoaderCronJobModel jobModel;
	private DataLoaderStopCronJobModel stopJobModel;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setCronJobService(mockedCronJobService);
		classUnderTest.setModelService(mockedModelService);
		classUnderTest.setDataLoaderCronJobParameters(mockedDataLoaderCronJobParameters);
		// showMessageBox contains a static call, we can only "mock away" using a spy
		willDoNothing().given(classUnderTest).showMessageBox(Mockito.anyString());

		result = new ActionResult(ActionResult.SUCCESS);
		given(mockedContext.getLabel("text.sapproductconfig_initial_load_successful")).willReturn(SUCCESS_MESSAGE);
		given(mockedContext.getLabel("text.sapproductconfig_configuration_not_set")).willReturn(ERROR_MESSAGE);
		given(mockedContext.getLabel("text.sapproductconfig_initial_load_start_confirmation")).willReturn(CONFIRM_MESSAGE);
		given(mockedContext.getLabel("text.sapproductconfig_running_dataloadjob_not_stopped")).willReturn(DL_NOT_STOPEED_MESSAGE);


		jobModel = new DataLoaderCronJobModel();
		given(mockedModelService.create(DataLoaderCronJobModel.class)).willReturn(jobModel);
		stopJobModel = new DataLoaderStopCronJobModel();
		given(mockedModelService.create(DataLoaderStopCronJobModel.class)).willReturn(stopJobModel);

		willReturn(null).given(mockedDataLoaderCronJobParameters).retrieveNodeIdForStartJob();
		willReturn(null).given(mockedDataLoaderCronJobParameters).retrieveNodeIdForStopJob();
	}

	@Test
	public void testCanPerform()
	{
		assertTrue(classUnderTest.canPerform(null));
	}

	@Test
	public void testCheckPrametersOK()
	{
		final SAPConfigurationModel configuration = createValidSapConfigForDataloader();
		classUnderTest.checkParameters(null, result, configuration);
		verify(classUnderTest, never()).showMessageBox(anyString());
	}

	@Test
	public void testCheckPrametersNoConfig()
	{
		classUnderTest.checkParameters(mockedContext, result, null);
		assertErrorMessageShown();
	}

	@Test
	public void testCheckPrametersNullSapSeverRFCDestinationName()
	{
		final SAPConfigurationModel configuration = createValidSapConfigForDataloader();
		final SAPRFCDestinationModel destination = new SAPRFCDestinationModel();
		destination.setRfcDestinationName(null);
		configuration.setSapproductconfig_sapServer(destination);
		classUnderTest.checkParameters(mockedContext, result, configuration);
		assertErrorMessageShown();
	}

	@Test
	public void testCheckPrametersEmptySapSeverRFCDestinationName()
	{
		final SAPConfigurationModel configuration = createValidSapConfigForDataloader();
		final SAPRFCDestinationModel destination = new SAPRFCDestinationModel();
		destination.setRfcDestinationName("");
		configuration.setSapproductconfig_sapServer(destination);
		classUnderTest.checkParameters(mockedContext, result, configuration);
		assertErrorMessageShown();
	}

	@Test
	public void testCheckPrametersNoSapServer()
	{
		final SAPConfigurationModel configuration = createValidSapConfigForDataloader();
		configuration.setSapproductconfig_sapServer(null);
		classUnderTest.checkParameters(mockedContext, result, configuration);
		assertErrorMessageShown();
	}

	@Test
	public void testCheckPrametersEmptySapRfcDesinationName()
	{
		final SAPConfigurationModel configuration = createValidSapConfigForDataloader();
		configuration.setSapproductconfig_sapRFCDestination("");
		classUnderTest.checkParameters(mockedContext, result, configuration);
		assertErrorMessageShown();
	}

	@Test
	public void testCheckPrametersNullapRfcDesinationName()
	{
		final SAPConfigurationModel configuration = createValidSapConfigForDataloader();
		configuration.setSapproductconfig_sapRFCDestination(null);
		classUnderTest.checkParameters(mockedContext, result, configuration);
		assertErrorMessageShown();
	}

	protected void assertErrorMessageShown()
	{
		assertEquals(ActionResult.ERROR, result.getResultCode());
		verify(classUnderTest).showMessageBox(ERROR_MESSAGE);
	}


	protected SAPConfigurationModel createValidSapConfigForDataloader()
	{
		final SAPConfigurationModel configuration = new SAPConfigurationModel();
		final SAPRFCDestinationModel destination = new SAPRFCDestinationModel();
		destination.setRfcDestinationName("TEST TSET TEST");
		configuration.setSapproductconfig_sapRFCDestination("TEST TSET TSET");
		configuration.setSapproductconfig_sapServer(destination);
		return configuration;
	}

	@Test
	public void testGetConfirmationMessage()
	{

		final String confirmationMessage = classUnderTest.getConfirmationMessage(mockedContext);
		assertEquals(CONFIRM_MESSAGE, confirmationMessage);
	}

	@Test
	public void testNeedsConfirmationFalseNoJobs()
	{
		mockDatalodNotRunning();
		assertFalse(classUnderTest.needsConfirmation(mockedContext));
	}

	protected void mockDatalodNotRunning()
	{
		given(mockedCronJobService.getRunningOrRestartedCronJobs()).willReturn(Collections.emptyList());
	}

	@Test
	public void testNeedsConfirmationFalseWrongJobs()
	{
		final List<CronJobModel> jobList = new ArrayList<>();
		jobList.add(new CronJobModel());
		given(mockedCronJobService.getRunningOrRestartedCronJobs()).willReturn(jobList);
		assertFalse(classUnderTest.needsConfirmation(mockedContext));
	}

	@Test
	public void testNeedsConfirmationTrue()
	{
		mockDataLoaderRunning();
		assertTrue(classUnderTest.needsConfirmation(mockedContext));
	}

	protected void mockDataLoaderRunning()
	{
		final List<CronJobModel> jobList = new ArrayList<>();
		jobList.add(new DataLoaderCronJobModel());
		given(mockedCronJobService.getRunningOrRestartedCronJobs()).willReturn(jobList);
	}

	@Test
	public void testPerformInvalidConfig()
	{
		final ActionResult<String> result = classUnderTest.perform(mockedContext);
		assertEquals(ActionResult.ERROR, result.getResultCode());
	}

	@Test
	public void testPerformStopFailed()
	{
		final SAPConfigurationModel config = createValidSapConfigForDataloader();
		given(mockedContext.getData()).willReturn(config);
		mockDataLoaderRunning();
		stopJobModel.setResult(CronJobResult.ERROR);

		final ActionResult<String> result = classUnderTest.perform(mockedContext);
		// assert that stop job was attempted
		verify(mockedCronJobService).performCronJob(stopJobModel, true);
		assertEquals(ActionResult.ERROR, result.getResultCode());
		verify(classUnderTest).showMessageBox(DL_NOT_STOPEED_MESSAGE);
	}

	@Test
	public void testPerformOK()
	{
		final SAPConfigurationModel config = createValidSapConfigForDataloader();
		given(mockedContext.getData()).willReturn(config);
		given(mockedDataLoaderCronJobParameters.retrieveNodeIdForStartJob()).willReturn(null);

		final ActionResult<String> result = classUnderTest.perform(mockedContext);
		assertPerformOK(config, result);
		assertNull(jobModel.getNodeID());
	}

	@Test
	public void testPerformOKWithStop()
	{
		mockDataLoaderRunning();
		stopJobModel.setResult(CronJobResult.SUCCESS);
		final SAPConfigurationModel config = createValidSapConfigForDataloader();
		final CPQDataloadStatusModel status = new CPQDataloadStatusModel();
		config.setSapproductconfig_cpqDataloadStatus(status);
		given(mockedContext.getData()).willReturn(config);
		given(mockedDataLoaderCronJobParameters.retrieveNodeIdForStartJob()).willReturn(123);

		final ActionResult<String> result = classUnderTest.perform(mockedContext);
		// assert that stop job was attempted
		verify(mockedCronJobService).performCronJob(stopJobModel, true);
		assertPerformOK(config, result);
		assertEquals(Integer.valueOf(123), jobModel.getNodeID());
		assertSame(status, config.getSapproductconfig_cpqDataloadStatus());

	}

	protected void assertPerformOK(final SAPConfigurationModel config, final ActionResult<String> result)
	{
		// assert that perform job was called
		verify(mockedCronJobService).performCronJob(jobModel, false);

		// assert result
		assertEquals(ActionResult.SUCCESS, result.getResultCode());
		assertTrue(result.getStatusFlags().contains(StatusFlag.OBJECT_MODIFIED));
		verify(classUnderTest).showMessageBox(SUCCESS_MESSAGE);

		// assert config was completed and job object was constructed proper
		assertNotNull(config.getSapproductconfig_cpqDataloadStatus());
		assertSame(config, jobModel.getSapConfiguration());
		assertEquals(DataLoadTriggerMode.STARTINITIAL, jobModel.getTriggerMode());
	}

	@Test
	public void testStopDataloadError()
	{
		stopJobModel.setResult(CronJobResult.ERROR);
		final boolean success = classUnderTest.stopDataload();
		assertFalse(success);
	}

	@Test
	public void testStopDataloadOK()
	{
		given(mockedDataLoaderCronJobParameters.retrieveNodeIdForStopJob()).willReturn(null);
		stopJobModel.setResult(CronJobResult.SUCCESS);

		mockDatalodNotRunning();

		final boolean success = classUnderTest.stopDataload();

		assertTrue(success);
		verify(mockedCronJobService).performCronJob(stopJobModel, true);
		assertNull(stopJobModel.getNodeID());
	}

	@Test
	public void testStopDataloadOKNotWaiting()
	{
		given(mockedDataLoaderCronJobParameters.retrieveNodeIdForStopJob()).willReturn(123);
		stopJobModel.setResult(CronJobResult.SUCCESS);

		mockDataLoaderRunning();

		final boolean success = classUnderTest.stopDataload();

		assertTrue(success);
		verify(mockedCronJobService).performCronJob(stopJobModel, true);
		assertEquals(Integer.valueOf(123), stopJobModel.getNodeID());
	}


}
