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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ConfiguratorSettingsService;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonKbDeterminationFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameterWithValues;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ValueParameter;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class ProductCsticAndValueParameterProviderCPSImplTest
{
	private static final String KB_ID = "815";
	private static final String PRODUCT_CODE = "PRODUCT";
	private static final String CSTIC_ID = "VALUE";
	private static final String CSTIC_NAME = "This is a value";
	private static final String VALUE_1_ID = "1";
	private static final String VALUE_1_NAME = "Val 1";
	private static final String VALUE_2_ID = "2";
	private static final String VALUE_2_NAME = "Val 2";

	private ProductCsticAndValueParameterProviderCPSImpl classUnderTest;

	@Mock
	private CharonKbDeterminationFacade charonKbDeterminationFacade;

	@Mock
	private ConfigurationMasterDataService configurationMasterDataService;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private CPSBaseSiteProvider cpsBaseSiteProvider;

	@Mock
	private ConfiguratorSettingsService configuratorSettingsService;

	@Mock
	private ProductDao productDao;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new ProductCsticAndValueParameterProviderCPSImpl();
		classUnderTest.setCharonKbDeterminationFacade(charonKbDeterminationFacade);
		classUnderTest.setConfigMasterDataService(configurationMasterDataService);
		classUnderTest.setBaseSiteService(baseSiteService);
		classUnderTest.setCpsBaseSiteProvider(cpsBaseSiteProvider);
		classUnderTest.setProductDao(productDao);
		classUnderTest.setConfiguratorSettingsService(configuratorSettingsService);
	}

	@Test
	public void testGetCharacteristcs()
	{
		when(configurationMasterDataService.getMasterData(KB_ID)).thenReturn(createMasterDataKnowledgeBase());
		final Map<String, CPSMasterDataCharacteristicContainer> result = classUnderTest.getCharacteristcs(KB_ID);

		assertNotNull(result);
		assertEquals(1, result.size());
		final CPSMasterDataCharacteristicContainer cstic = result.get(CSTIC_ID);
		assertEquals(CSTIC_ID, cstic.getId());
		assertEquals(2, cstic.getPossibleValueGlobals().size());
	}

	@Test(expected = IllegalStateException.class)
	public void testGetCharacteristcsWithUnknownKbId()
	{
		when(configurationMasterDataService.getMasterData(KB_ID)).thenReturn(null);
		classUnderTest.getCharacteristcs(KB_ID);

		fail();
	}

	@Test
	public void testGetValuesForCstic()
	{
		final List<ValueParameter> result = classUnderTest.getValuesForCstic(createPossibleValueMap());
		assertNotNull(result);
		assertEquals(2, result.size());

		for (final ValueParameter parameter : result)
		{
			if (VALUE_1_ID.equals(parameter.getValueName()))
			{
				assertEquals(VALUE_1_NAME, parameter.getValueDescription());
			}
			else if (VALUE_2_ID.equals(parameter.getValueName()))
			{
				assertEquals(VALUE_2_NAME, parameter.getValueDescription());
			}
			else
			{
				fail();
			}
		}
	}

	@Test
	public void testGetCsticParameters()
	{
		final CsticParameterWithValues result = classUnderTest.getCsticParameters(createCPSCharacteristic());

		assertNotNull(result);
		assertNotNull(result.getCstic());
		final CsticParameter cstic = result.getCstic();
		assertEquals(CSTIC_ID, cstic.getCsticName());
		assertEquals(CSTIC_NAME, cstic.getCsticDescription());

		assertNotNull(result.getValues());
		assertEquals(2, result.getValues().size());
	}

	@Test(expected = IllegalStateException.class)
	public void testRetrieveProductCsticsAndValuesParametersUnknownProduct()
	{
		when(charonKbDeterminationFacade.getCurrentKbIdForProduct(PRODUCT_CODE)).thenReturn(null);
		classUnderTest.retrieveProductCsticsAndValuesParameters(PRODUCT_CODE);

		fail();
	}

	@Test
	public void testRetrieveProductCsticsAndValuesParametersCPQConfigurableProduct()
	{
		when(configurationMasterDataService.getMasterData(KB_ID)).thenReturn(createMasterDataKnowledgeBase());
		when(charonKbDeterminationFacade.getCurrentKbIdForProduct(PRODUCT_CODE)).thenReturn(Integer.valueOf(KB_ID));
		when(cpsBaseSiteProvider.getConfiguredBaseSite()).thenReturn(new BaseSiteModel());
		mockProductModel(ConfiguratorType.CPQCONFIGURATOR);
		final Map<String, CsticParameterWithValues> result = classUnderTest.retrieveProductCsticsAndValuesParameters(PRODUCT_CODE);
		assertNotNull(result);
		assertEquals(1, result.size());
		assertNotNull(result.get(CSTIC_ID));
	}

	@Test
	public void testRetrieveProductCsticsAndValuesParametersNotCPQConfigurableProduct()
	{
		when(configurationMasterDataService.getMasterData(KB_ID)).thenReturn(createMasterDataKnowledgeBase());
		when(charonKbDeterminationFacade.getCurrentKbIdForProduct(PRODUCT_CODE)).thenReturn(Integer.valueOf(KB_ID));
		when(cpsBaseSiteProvider.getConfiguredBaseSite()).thenReturn(new BaseSiteModel());
		mockProductModel(null);
		final Map<String, CsticParameterWithValues> result = classUnderTest.retrieveProductCsticsAndValuesParameters(PRODUCT_CODE);
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testRetrieveProductCsticsAndValuesParametersVariantProduct()
	{
		when(configurationMasterDataService.getMasterData(KB_ID)).thenReturn(createMasterDataKnowledgeBase());
		when(charonKbDeterminationFacade.getCurrentKbIdForProduct(PRODUCT_CODE)).thenReturn(Integer.valueOf(KB_ID));
		when(cpsBaseSiteProvider.getConfiguredBaseSite()).thenReturn(new BaseSiteModel());
		mockVariantProductModel();
		final Map<String, CsticParameterWithValues> result = classUnderTest.retrieveProductCsticsAndValuesParameters(PRODUCT_CODE);
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	protected void mockProductModel(final ConfiguratorType cpqconfigurator)
	{
		final List<ProductModel> productModelList = new ArrayList<>();
		final ProductModel productModel1 = new ProductModel();
		final ProductModel productModel2 = new ProductModel();
		productModelList.add(productModel1);
		productModelList.add(productModel2);
		when(productDao.findProductsByCode(PRODUCT_CODE)).thenReturn(productModelList);

		final List<AbstractConfiguratorSettingModel> settingList = new ArrayList<>();
		if (cpqconfigurator != null)
		{
			final AbstractConfiguratorSettingModel settingModel = new AbstractConfiguratorSettingModel();
			settingModel.setConfiguratorType(cpqconfigurator);
			settingList.add(settingModel);
		}
		when(configuratorSettingsService.getConfiguratorSettingsForProduct(Mockito.any())).thenReturn(settingList);
	}

	protected void mockVariantProductModel()
	{
		final List<ProductModel> productModelList = new ArrayList<>();
		final ProductModel productModel1 = new VariantProductModel();
		final ProductModel productModel2 = new VariantProductModel();
		productModelList.add(productModel1);
		productModelList.add(productModel2);
		when(productDao.findProductsByCode(PRODUCT_CODE)).thenReturn(productModelList);
	}

	protected CPSMasterDataKnowledgeBaseContainer createMasterDataKnowledgeBase()
	{
		final CPSMasterDataKnowledgeBaseContainer masterData = new CPSMasterDataKnowledgeBaseContainer();

		final Map<String, CPSMasterDataCharacteristicContainer> characteristics = new HashMap<>();
		characteristics.put(CSTIC_ID, createCPSCharacteristic());
		masterData.setCharacteristics(characteristics);

		return masterData;
	}

	private Map<String, CPSMasterDataPossibleValue> createPossibleValueMap()
	{
		final Map<String, CPSMasterDataPossibleValue> possibleValueGlobals = new HashMap<>();
		possibleValueGlobals.put(VALUE_1_ID, createValue(VALUE_1_ID, VALUE_1_NAME));
		possibleValueGlobals.put(VALUE_2_ID, createValue(VALUE_2_ID, VALUE_2_NAME));
		return possibleValueGlobals;
	}

	protected CPSMasterDataCharacteristicContainer createCPSCharacteristic()
	{
		final CPSMasterDataCharacteristicContainer cpsCstic = new CPSMasterDataCharacteristicContainer();
		cpsCstic.setName(CSTIC_NAME);
		cpsCstic.setId(CSTIC_ID);
		cpsCstic.setPossibleValueGlobals(createPossibleValueMap());

		return cpsCstic;
	}

	protected CPSMasterDataPossibleValue createValue(final String id, final String name)
	{
		final CPSMasterDataPossibleValue value = new CPSMasterDataPossibleValue();
		value.setId(id);
		value.setName(name);
		return value;
	}

}
