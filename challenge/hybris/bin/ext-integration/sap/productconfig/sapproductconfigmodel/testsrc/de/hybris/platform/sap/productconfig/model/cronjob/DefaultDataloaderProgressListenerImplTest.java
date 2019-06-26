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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.productconfig.model.model.CPQDataloadStatusModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderQueuesInfo;
import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.Progress;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultDataloaderProgressListenerImplTest
{
	DefaultDataloaderProgressListenerImpl classUnderTest = new DefaultDataloaderProgressListenerImpl();

	@Mock
	SAPConfigurationModel sapConfiguratioModel;

	@Mock
	ModelService modelService;

	@Mock
	private Progress progress;

	@Mock
	private DataloaderQueuesInfo dataLoaderQInfo;

	@Mock
	private CPQDataloadStatusModel datloadstatus;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(progress.getDataloaderQueuesInfo()).thenReturn(dataLoaderQInfo);
	}

	@Test
	public void testSAPConfiguration()
	{
		classUnderTest.setSapConfiguration(sapConfiguratioModel);
		assertEquals(sapConfiguratioModel, classUnderTest.getSapConfiguration());
	}

	@Test
	public void testModelService()
	{
		classUnderTest.setModelService(modelService);
		assertEquals(modelService, classUnderTest.getModelService());
	}

	@Test(expected = IllegalStateException.class)
	public void testProgressReportedNoConfiguration()
	{
		classUnderTest.progressReported(progress);
	}

	@Test
	public void testProgressReported()
	{
		Mockito.when(sapConfiguratioModel.getSapproductconfig_cpqDataloadStatus()).thenReturn(datloadstatus);
		classUnderTest.setSapConfiguration(sapConfiguratioModel);
		classUnderTest.setModelService(modelService);
		classUnderTest.progressReported(progress);
		Mockito.verify(modelService).save(Mockito.anyObject());
	}

}
