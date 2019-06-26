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
package de.hybris.platform.sap.productconfig.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.productconfig.model.enums.DataloadStatus;
import de.hybris.platform.sap.productconfig.model.jalo.CPQDataloadStatus;
import de.hybris.platform.sap.productconfig.model.model.CPQDataloadStatusModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;

import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderFailureException;
import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderManagerImpl;


@SuppressWarnings("javadoc")
@UnitTest
public class DataLoaderImplTest
{
	private final DataLoaderImpl classUnderTest = new DataLoaderImpl();

	private CPQDataloadStatusModel dataLoadStatus;
	private SAPConfigurationModel sapConfiguration;

	@Mock
	private DataloaderManagerImpl dataloaderManager;
	@Mock
	private ModelService modelService;






	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		dataLoadStatus = new CPQDataloadStatusModel();
		sapConfiguration = new SAPConfigurationModel();
		sapConfiguration.setSapproductconfig_cpqDataloadStatus(dataLoadStatus);
	}

	@Test
	public void testInitialLoad() throws DataloaderFailureException
	{
		classUnderTest.performInitialLoad(sapConfiguration, dataloaderManager, modelService);
		Mockito.verify(modelService, VerificationModeFactory.atLeastOnce()).save(dataLoadStatus);
		assertEquals(DataloadStatus.INITIAL_LOAD_COMPLETED, dataLoadStatus.getCpqDataloadStatus());
	}

	@Test
	public void testInitialLoadStausIsRunningInBetween() throws DataloaderFailureException
	{
		willThrow(DataloaderFailureException.class).given(dataloaderManager).startInitialDownload();
		try
		{
			classUnderTest.performInitialLoadRaisingException(dataloaderManager, sapConfiguration, modelService);
		}
		catch (final DataloaderFailureException ex)
		{
			//expected
		}
		assertEquals(DataloadStatus.INITIAL_LOAD, dataLoadStatus.getCpqDataloadStatus());
		Mockito.verify(modelService, VerificationModeFactory.atLeastOnce()).save(dataLoadStatus);
	}

	@Test
	public void testInitialLoadNullStatus() throws DataloaderFailureException
	{
		sapConfiguration.setSapproductconfig_cpqDataloadStatus(null);
		classUnderTest.performInitialLoad(sapConfiguration, dataloaderManager, modelService);
		Mockito.verify(modelService, VerificationModeFactory.atLeastOnce()).save(Mockito.any(CPQDataloadStatus.class));
		assertNotNull(sapConfiguration.getSapproductconfig_cpqDataloadStatus());
		assertEquals(DataloadStatus.INITIAL_LOAD_COMPLETED, sapConfiguration.getSapproductconfig_cpqDataloadStatus()
				.getCpqDataloadStatus());
	}

	@Test
	public void testInitialLoadDeltaStoppedManually() throws DataloaderFailureException
	{
		given(dataloaderManager.isStoppedDownloadManually()).willReturn(true);
		classUnderTest.performInitialLoad(sapConfiguration, dataloaderManager, modelService);
		Mockito.verify(modelService, VerificationModeFactory.atLeastOnce()).save(dataLoadStatus);
		assertEquals(DataloadStatus.INITIAL_LOAD_STOPPED, dataLoadStatus.getCpqDataloadStatus());

	}

	@Test
	public void testDeltaLoad() throws DataloaderFailureException
	{
		classUnderTest.performDeltaLoad(sapConfiguration, dataloaderManager, modelService);
		Mockito.verify(modelService, VerificationModeFactory.atLeastOnce()).save(dataLoadStatus);
		assertEquals(DataloadStatus.DELTA_LOAD, dataLoadStatus.getCpqDataloadStatus());
	}

	@Test
	public void testDeltaLoadDeltaStoppedManually() throws DataloaderFailureException
	{
		given(dataloaderManager.isStoppedDownloadManually()).willReturn(true);
		classUnderTest.performDeltaLoad(sapConfiguration, dataloaderManager, modelService);
		Mockito.verify(modelService, VerificationModeFactory.atLeastOnce()).save(dataLoadStatus);
		assertEquals(DataloadStatus.DELTA_LOAD_STOPPED, dataLoadStatus.getCpqDataloadStatus());
	}


	@Test
	public void testResetStatistics()
	{
		dataLoadStatus.setCpqCurrentDeltaLoadTransferredVolume(BigDecimal.ONE);
		dataLoadStatus.setCpqCurrentInitialLoadTransferredVolume(BigDecimal.ONE);
		dataLoadStatus.setCpqNumberOfEntriesInDeltaLoadQueue(Integer.valueOf(Integer.MAX_VALUE));
		classUnderTest.resetStatistics(dataLoadStatus);
		assertNull(dataLoadStatus.getCpqCurrentDeltaLoadTransferredVolume());
		assertNull(dataLoadStatus.getCpqCurrentInitialLoadTransferredVolume());
		assertNull(dataLoadStatus.getCpqNumberOfEntriesInDeltaLoadQueue());
	}

	@Test
	public void testInitialLoadError() throws DataloaderFailureException
	{
		willThrow(DataloaderFailureException.class).given(dataloaderManager).startInitialDownload();
		try
		{
			classUnderTest.performInitialLoad(sapConfiguration, dataloaderManager, modelService);
		}
		catch (final IllegalStateException ex)
		{
			//expected
			assertEquals(DataloaderFailureException.class, ex.getCause().getClass());
		}
		Mockito.verify(modelService, VerificationModeFactory.atLeastOnce()).save(dataLoadStatus);
		assertEquals(DataloadStatus.ERROR, dataLoadStatus.getCpqDataloadStatus());
	}

	@Test
	public void testDeltaLoadError() throws DataloaderFailureException
	{
		willThrow(DataloaderFailureException.class).given(dataloaderManager).startDeltaDownload();
		try
		{
			classUnderTest.performDeltaLoad(sapConfiguration, dataloaderManager, modelService);
		}
		catch (final IllegalStateException ex)
		{
			//expected
			assertEquals(DataloaderFailureException.class, ex.getCause().getClass());
		}
		Mockito.verify(modelService, VerificationModeFactory.atLeastOnce()).save(dataLoadStatus);
		assertEquals(DataloadStatus.ERROR, dataLoadStatus.getCpqDataloadStatus());
	}

}
