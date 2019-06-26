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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.willReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.sap.productconfig.model.dataloader.configuration.DataloaderSourceParameters;
import de.hybris.platform.servicelayer.model.ItemModelContext;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.custdev.projects.fbs.slc.dataloader.standalone.DataloaderConfiguration;


@SuppressWarnings("javadoc")
@UnitTest
public class DataLoaderConfigurationHelperImplTest
{
	private DataLoaderConfigurationHelperImpl classUnderTest;

	@Mock
	private SAPRFCDestinationModel sapDestinationModel;

	@Mock
	private MediaModel mediaModel;

	@Mock
	private ItemModelContext itemModelContextKBFilterFile;

	@Mock
	private SAPConfigurationModel sapConfigurationModel;

	private static final String destination = "destination";

	private static final String serverRfcDestination = "ZZZ000";


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new DataLoaderConfigurationHelperImpl();
		Mockito.when(sapDestinationModel.getRfcDestinationName()).thenReturn(destination);
		Mockito.when(mediaModel.getItemModelContext()).thenReturn(itemModelContextKBFilterFile);
		Mockito.when(Boolean.valueOf(itemModelContextKBFilterFile.isUpToDate())).thenReturn(Boolean.FALSE);
		Mockito.when(sapConfigurationModel.getSapproductconfig_filterKnowledgeBase()).thenReturn(mediaModel);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetDataLoaderSourceParamNoDestination()
	{
		classUnderTest.getDataloaderSourceParam(sapConfigurationModel);
	}

	@Test
	public void testGetDataLoaderSourceParam()
	{
		Mockito.when(sapConfigurationModel.getSapproductconfig_sapServer()).thenReturn(sapDestinationModel);
		final DataloaderSourceParameters dataloaderSourceParam = classUnderTest.getDataloaderSourceParam(sapConfigurationModel);
		assertEquals("We expect a destination name", destination, dataloaderSourceParam.getServerRfcDestination());
	}

	@Test
	public void testCreateConfigMapFromConfig()
	{
		classUnderTest.setConfigClazz(DataloaderConfigurationForTest.class);
		classUnderTest = Mockito.spy(classUnderTest);
		willReturn("aValue").given(classUnderTest).getHybrisConfigParam(Mockito.anyString());
		willReturn("anotherValue").given(classUnderTest).getHybrisConfigParam(Mockito.anyString(), Mockito.anyString());
		final DataloaderSourceParameters params = new DataloaderSourceParameters();

		final Map<String, String> configMap = classUnderTest.createConfigMap(params);

		assertNotNull("We expect a map", configMap);
		assertEquals(13, configMap.size());
		final String targetFromProperties = configMap.get(DataloaderConfiguration.TARGET_FROM_PROPERTIES);
		assertEquals("TargetFromProperties=false expected", Boolean.toString(false), targetFromProperties);
	}

	@Test
	public void testCreateConfigMapFromParams()
	{
		// sting class does not contain dataload fields
		classUnderTest.setConfigClazz(String.class);

		final DataloaderSourceParameters params = new DataloaderSourceParameters();
		params.setServerRfcDestination(serverRfcDestination);
		final Map<String, String> configMap = classUnderTest.createConfigMap(params);
		assertNotNull("We expect a map", configMap);
		assertEquals(4, configMap.size());

		final String dest = configMap.get(DataloaderConfiguration.OUTBOUND_DESTINATION_NAME);

		assertEquals("Destination expected", serverRfcDestination, dest);
		final String targetFromProperties = configMap.get(DataloaderConfiguration.TARGET_FROM_PROPERTIES);
		assertEquals("TargetFromProperties=true expected", Boolean.toString(true), targetFromProperties);
	}



	@Test
	public void testPrepareFilterFiles()
	{
		final Map<String, String> dataloaderConfigMap = new HashMap<String, String>();
		classUnderTest.prepareFilterFiles(dataloaderConfigMap, sapConfigurationModel);
		final String kbFilterPath = dataloaderConfigMap.get(DataloaderConfiguration.KB_FILTER_FILE_PATH);
		assertNull("We expect no filter path since media model is not upToDate", kbFilterPath);
	}

	@Test
	public void testGetPathForMedia()
	{
		assertNull("No path since model is not upToDate", classUnderTest.getAbsolutFilePathForMedia(mediaModel));
	}

	@Test
	public void testFieldsAvailableTrue()
	{
		final boolean fieldsAvailable = classUnderTest.fieldsAvailable(DataloaderConfigurationForTest.class);
		assertTrue(fieldsAvailable);
	}

	@Test
	public void testFieldsAvailableFalse()
	{
		final boolean fieldsAvailable = classUnderTest.fieldsAvailable(String.class);
		assertFalse(fieldsAvailable);
	}

	private static class DataloaderConfigurationForTest
	{
		public static final String DB_JNDI_DATASOURCE = "DB_JNDI_DATASOURCE";
		public static final String DB_JNDI_USAGE = "DB_JNDI_USAGE";
	}
}
