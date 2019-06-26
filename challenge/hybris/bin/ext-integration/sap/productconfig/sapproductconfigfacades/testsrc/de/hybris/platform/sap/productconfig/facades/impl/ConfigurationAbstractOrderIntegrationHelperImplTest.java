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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.facades.populator.VariantOverviewPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConfigurationAbstractOrderIntegrationHelperImplTest
{
	private static final int NON_EXISTING_ENTRY_NUMBER = 1;
	private static final int ENTRY_NUMBER = 3;
	private static final String CONFIG_ID = "configId";
	private static final String PRODUCT_CODE = "productCode";
	private String extConfig;
	private KBKey kbKey;

	private ConfigurationAbstractOrderIntegrationHelperImpl classUnderTest;

	@Mock
	private ProductConfigurationService productConfigurationService;
	@Mock
	private VariantOverviewPopulator variantOverviewPopulator;
	@Mock
	private CPQConfigurableChecker cpqConfigurableChecker;
	@Mock
	private ConfigPricing pricing;

	private AbstractOrderModel order;
	private List<AbstractOrderEntryModel> orderEntryList;
	private ConfigModel configModel;
	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	@Mock
	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy;
	@Mock
	private ConfigurationVariantUtil configurationVariantUtil;

	private static final String EXT_CONFIG_KB_NOT_EXISTING = "kbNotExisting";
	private final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();

	protected void addOrderEntry(final String configId, final int entryNumber)
	{
		configModel = new ConfigModelImpl();
		configModel.setId(configId);
		extConfig = "myExtConfigForId" + configId;
		given(productConfigurationService.createConfigurationFromExternal(Mockito.any(KBKey.class), Mockito.eq(extConfig)))
				.willReturn(configModel);

		entry.setEntryNumber(Integer.valueOf(entryNumber));
		entry.setProduct(new ProductModel());
		entry.getProduct().setCode(PRODUCT_CODE);
		entry.setOrder(order);
		orderEntryList.add(entry);

		when(configurationAbstractOrderIntegrationStrategy.isKbVersionForEntryExisting(entry)).thenReturn(true);
		when(configurationAbstractOrderIntegrationStrategy.getConfigurationForAbstractOrderEntryForOneTimeAccess(entry))
				.thenReturn(configModel);
	}

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ConfigurationAbstractOrderIntegrationHelperImpl();
		classUnderTest.setProductConfigurationService(productConfigurationService);
		classUnderTest.setVariantOverviewPopulator(variantOverviewPopulator);
		classUnderTest.setCpqConfigurableChecker(cpqConfigurableChecker);
		classUnderTest.setConfigPricing(pricing);
		classUnderTest.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategy);
		classUnderTest.setConfigurationAbstractOrderIntegrationStrategy(configurationAbstractOrderIntegrationStrategy);
		classUnderTest.setConfigurationVariantUtil(configurationVariantUtil);

		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any(ProductModel.class))).thenReturn(true);

		order = new AbstractOrderModel();
		orderEntryList = new ArrayList<>();
		order.setEntries(orderEntryList);
		addOrderEntry(CONFIG_ID, ENTRY_NUMBER);

		kbKey = new KBKeyImpl(PRODUCT_CODE);
	}

	@Test
	public void testAbstractOrderEntryLinkStrategy()
	{
		assertEquals(configurationAbstractOrderEntryLinkStrategy, classUnderTest.getAbstractOrderEntryLinkStrategy());
	}

	@Test
	public void testFindEntry()
	{
		final AbstractOrderEntryModel result = classUnderTest.findEntry(order, ENTRY_NUMBER);
		assertNotNull(result);
		assertEquals(entry, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindEntryNotFound()
	{
		classUnderTest.findEntry(order, NON_EXISTING_ENTRY_NUMBER);
	}

	@Test
	public void testPrepareOverviewData()
	{
		final ConfigurationOverviewData result = classUnderTest.prepareOverviewData(kbKey, configModel);
		assertNotNull(result);
		assertEquals(kbKey.getProductCode(), result.getProductCode());
		assertEquals(CONFIG_ID, result.getId());
	}

	@Test
	public void testRetrieveConfigurationOverviewData_versionExists()
	{
		final ConfigurationOverviewData result = classUnderTest.retrieveConfigurationOverviewData(order, ENTRY_NUMBER);
		assertNotNull(result);
		assertEquals(kbKey.getProductCode(), result.getProductCode());
		assertEquals(CONFIG_ID, result.getId());
	}

	@Test
	public void testRetrieveConfigurationOverviewData_versionNotExists()
	{
		Mockito.when(configurationAbstractOrderIntegrationStrategy.isKbVersionForEntryExisting(entry)).thenReturn(false);
		final ConfigurationOverviewData result = classUnderTest.retrieveConfigurationOverviewData(order, ENTRY_NUMBER);
		assertNull(result);
	}



	@Test
	public void testGetConfigurationOverviewDataForVariant()
	{
		final VariantProductModel variantProductModel = new VariantProductModel();
		variantProductModel.setCode(PRODUCT_CODE);
		final ConfigurationOverviewData result = classUnderTest.getConfigurationOverviewDataForVariant(entry, variantProductModel);
		assertNotNull(result);
		assertEquals(PRODUCT_CODE, result.getProductCode());
		verify(variantOverviewPopulator).populate(Mockito.eq(variantProductModel), Mockito.any());
	}

	@Test
	public void testRetrieveConfigurationForVariant()
	{
		final VariantProductModel variantProductModel = new VariantProductModel();
		variantProductModel.setCode(PRODUCT_CODE);
		orderEntryList.get(0).setProduct(variantProductModel);
		given(configurationVariantUtil.isCPQNotChangeableVariantProduct(variantProductModel)).willReturn(true);
		final ConfigurationOverviewData result = classUnderTest.retrieveConfigurationOverviewData(order, ENTRY_NUMBER);

		assertNotNull(result);
		assertEquals(PRODUCT_CODE, result.getProductCode());
		verify(variantOverviewPopulator).populate(Mockito.eq(variantProductModel), Mockito.any());
	}

	@Test
	public void testRetrieveConfigurationKMAT()
	{
		final VariantProductModel variantProductModel = new VariantProductModel();
		variantProductModel.setCode(PRODUCT_CODE);
		entry.setProduct(variantProductModel);
		final ConfigurationOverviewData result = classUnderTest.retrieveConfigurationOverviewData(order, ENTRY_NUMBER);

		assertNotNull(result);
		assertEquals(PRODUCT_CODE, result.getProductCode());
		verify(configurationAbstractOrderIntegrationStrategy).getConfigurationForAbstractOrderEntryForOneTimeAccess(entry);
	}

	@Test
	public void testIsKbExistingFalse()
	{
		Mockito.when(configurationAbstractOrderIntegrationStrategy.isKbVersionForEntryExisting(entry)).thenReturn(false);
		assertFalse(classUnderTest.isKbVersionForEntryExisting(entry));
	}

	@Test
	public void testIsKbExistingTrue()
	{
		assertTrue(classUnderTest.isKbVersionForEntryExisting(entry));
	}

	@Test
	public void testIsKbExistingForNotConfigurable()
	{

		final AbstractOrderEntryModel orderEntry = orderEntryList.get(0);
		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any(ProductModel.class))).thenReturn(true);
		assertTrue(classUnderTest.isKbVersionForEntryExisting(orderEntry));
	}

	@Test
	public void testIsReorderable_true()
	{
		assertTrue(classUnderTest.isReorderable(order));
	}



	@Test
	public void testIsReorderable_false()
	{
		final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();
		orderEntry.setProduct(new ProductModel());
		orderEntry.getProduct().setCode(PRODUCT_CODE);
		order.getEntries().add(orderEntry);
		assertFalse(classUnderTest.isReorderable(order));

	}

	@Test
	public void testRetrieveConfigurationOverviewDataNotChangeableVariant()
	{
		final VariantProductModel variantProductModel = new VariantProductModel();
		variantProductModel.setCode(PRODUCT_CODE);
		orderEntryList.get(0).setProduct(variantProductModel);
		given(configurationVariantUtil.isCPQNotChangeableVariantProduct(variantProductModel)).willReturn(true);
		final ConfigurationOverviewData result = classUnderTest.retrieveConfigurationOverviewData(order, ENTRY_NUMBER);
		verify(variantOverviewPopulator).populate(Mockito.any(), Mockito.any());
		verify(configurationAbstractOrderIntegrationStrategy, never())
				.getConfigurationForAbstractOrderEntryForOneTimeAccess(Mockito.any());
	}

	@Test
	public void testRetrieveConfigurationOverviewDataKmatOrChangeableVariant()
	{
		final VariantProductModel variantProductModel = new VariantProductModel();
		variantProductModel.setCode(PRODUCT_CODE);
		orderEntryList.get(0).setProduct(variantProductModel);
		given(configurationVariantUtil.isCPQNotChangeableVariantProduct(variantProductModel)).willReturn(false);
		final ConfigurationOverviewData result = classUnderTest.retrieveConfigurationOverviewData(order, ENTRY_NUMBER);
		verify(variantOverviewPopulator, never()).populate(Mockito.any(), Mockito.any());
		verify(configurationAbstractOrderIntegrationStrategy).getConfigurationForAbstractOrderEntryForOneTimeAccess(Mockito.any());
	}
}
