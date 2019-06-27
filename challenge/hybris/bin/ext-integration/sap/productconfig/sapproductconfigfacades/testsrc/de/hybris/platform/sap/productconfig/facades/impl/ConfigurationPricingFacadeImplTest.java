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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigurationTestData;
import de.hybris.platform.sap.productconfig.facades.PriceValueUpdateData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.services.impl.PricingServiceImpl;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class ConfigurationPricingFacadeImplTest
{
	private String instanceId1;
	private String instanceId2;
	private static final String INSTANCE_NAME_1 = "SUBINSTANCE1LEVEL1";
	private static final String INSTANCE_NAME_2 = "SUBINSTANCE1LEVEL2";
	private static final String CSTIC_UI_KEY_1 = "cstic ui key 1";
	private static final String CSTIC_UI_KEY_2 = "cstic ui key 2";

	private ConfigurationPricingFacadeImpl pricingFacade;

	@Mock
	private PricingServiceImpl mockedPricingService;

	@Mock
	private Converter<PriceSummaryModel, PricingData> priceSummaryConverter;

	@Mock
	private Converter<PriceValueUpdateModel, PriceValueUpdateData> deltaPriceConverter;

	@Mock
	private UniqueUIKeyGenerator uiKeyGenerator;
	@Mock
	private ProductConfigurationService configurationService;

	private static final String configId = "1";
	final List<String> csticUIKeys = new ArrayList<>();
	private static final ConfigModel configModel = ConfigurationTestData.createConfigModelWithGroupsAndSubInstances();
	private CsticQualifier csticQualifier1;
	private CsticQualifier csticQualifier2;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		pricingFacade = Mockito.spy(new ConfigurationPricingFacadeImpl());
		pricingFacade.setPricingService(mockedPricingService);
		pricingFacade.setConfigurationService(configurationService);
		pricingFacade.setPriceSummaryConverter(priceSummaryConverter);
		pricingFacade.setDeltaPriceConverter(deltaPriceConverter);
		pricingFacade.setUiKeyGenerator(uiKeyGenerator);
		csticUIKeys.add(CSTIC_UI_KEY_1);
		csticUIKeys.add(CSTIC_UI_KEY_2);
		getInstanceIds();
		csticQualifier1 = createCsticQualifier(ConfigurationTestData.CHBOX_NAME, INSTANCE_NAME_1, instanceId1);
		Mockito.when(uiKeyGenerator.splitId(CSTIC_UI_KEY_1)).thenReturn(csticQualifier1);
		csticQualifier2 = createCsticQualifier(ConfigurationTestData.CHBOX_LIST_NAME, INSTANCE_NAME_2, instanceId2);
		Mockito.when(uiKeyGenerator.splitId(CSTIC_UI_KEY_2)).thenReturn(csticQualifier2);
		Mockito.when(configurationService.retrieveConfigurationModel(configId)).thenReturn(configModel);
		Mockito.when(deltaPriceConverter.convert(Mockito.any())).thenReturn(new PriceValueUpdateData());
	}

	private void getInstanceIds()
	{
		// instance ids are generated dynamically in ConfigurationTestData and need to be read from config model
		instanceId1 = configModel.getRootInstance().getSubInstances().get(0).getId();
		assertNotNull(instanceId1);
		instanceId2 = configModel.getRootInstance().getSubInstances().get(0).getSubInstances().get(0).getId();
		assertNotNull(instanceId2);
	}


	private CsticQualifier createCsticQualifier(final String csticName, final String instanceName, final String instanceId)
	{
		final CsticQualifier csticQualifier1 = new CsticQualifier();
		csticQualifier1.setCsticName(csticName);
		csticQualifier1.setInstanceName(instanceName);
		csticQualifier1.setInstanceId(instanceId);
		return csticQualifier1;
	}

	@Test
	public void testGetPriceSummary()
	{
		final PriceSummaryModel priceSummaryModel = new PriceSummaryModel();
		Mockito.when(mockedPricingService.getPriceSummary(configId)).thenReturn(priceSummaryModel);

		final PricingData pricingData = null;
		assertEquals(pricingFacade.getPriceSummary(configId), pricingData);
	}


	@Test
	public void testIsPricingServiceActive()
	{
		given(Boolean.valueOf(mockedPricingService.isActive())).willReturn(Boolean.TRUE);
		assertTrue(pricingFacade.isPricingServiceActive());
	}

	@Test
	public void testIsPricingServiceNotActive()
	{
		given(Boolean.valueOf(mockedPricingService.isActive())).willReturn(Boolean.FALSE);
		assertFalse(pricingFacade.isPricingServiceActive());
	}

	@Test
	public void testRetrieveValueUpdateModel_qualifier()
	{
		final List<PriceValueUpdateModel> result = pricingFacade.retrieveValueUpdateModel(csticUIKeys, configModel);
		assertNotNull(result);
		assertEquals(2, result.size());
		assertNotNull(result.get(0).getCsticQualifier());
		assertEquals(csticQualifier1, result.get(0).getCsticQualifier());
		assertEquals(csticQualifier2, result.get(1).getCsticQualifier());
	}

	@Test
	public void testRetrieveValueUpdateModel_selectedValues()
	{
		final List<PriceValueUpdateModel> result = pricingFacade.retrieveValueUpdateModel(csticUIKeys, configModel);
		assertNotNull(result.get(0).getSelectedValues());
		assertNotNull(result.get(1).getSelectedValues());
		assertEquals(1, result.get(0).getSelectedValues().size());
		assertEquals(1, result.get(1).getSelectedValues().size());
		assertTrue(result.get(0).getSelectedValues().contains("X"));
		assertTrue(result.get(1).getSelectedValues().contains("VAL2"));
	}

	@Test
	public void testGetDeltaPrices()
	{
		final List<PriceValueUpdateData> result = pricingFacade.getValuePrices(csticUIKeys, configId);
		assertNotNull(result);
		assertEquals(csticUIKeys.size(), result.size());
		assertEquals(2, result.size());
	}

	@Test
	public void testRetrieveCsticAndInstance()
	{
		final InstanceModel lastFound = configModel.getRootInstance().getSubInstances().get(0);
		assertTrue(pricingFacade.isLastFoundInstanceMatching(csticQualifier1, lastFound));
		final Pair<CsticModel, InstanceModel> result = pricingFacade.retrieveCsticAndInstance(csticQualifier1,
				configModel.getRootInstance(), lastFound);
		assertNotNull(result);
		assertSame(lastFound, result.getRight());
		Mockito.verify(pricingFacade, Mockito.times(0)).retrieveInstance(Mockito.any(), Mockito.any());
	}
}
