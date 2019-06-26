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
package de.hybris.platform.sap.productconfig.pricing.bol.backend.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.VariantConditionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.VariantConditionModelImpl;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;
import de.hybris.platform.variants.model.VariantProductModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.conn.jco.JCoTable;


@UnitTest
public class ProductConfigurationSapPricingItemMapperTest
{

	private static final String PCPV_CODE = "PCPV_CODE";
	private static final String PRODUCT_CODE = "CPQ_TEST";
	private static final BigDecimal THE_FACTOR = new BigDecimal("1.23");
	private static final String THE_KEY = "thekey";

	private final ProductConfigurationSapPricingItemMapper classUnderTest = new ProductConfigurationSapPricingItemMapper();

	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	@Mock
	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy;

	@Mock
	private CPQConfigurableChecker cpqConfigurableChecker;
	@Mock
	private OrderEntryModel orderEntry;
	@Mock
	private ProductModel productModel;
	@Mock
	private ProductModel mockProduct;
	@Mock
	private VariantProductModel varianteProduct;
	@Mock
	private JCoTable itItem;
	@Mock
	private JCoTable varcondTable;
	@Mock
	private ProductConfigurationService mockProductConfigurationService;


	private ConfigModel configModel;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		configModel = defineConfigModel();

		when(productModel.getCode()).thenReturn(PRODUCT_CODE);
		when(varianteProduct.getBaseProduct()).thenReturn(productModel);
		when(varianteProduct.getCode()).thenReturn(PCPV_CODE);
		when(orderEntry.getPk()).thenReturn(PK.fromLong(1L));
		when(orderEntry.getProduct()).thenReturn(productModel);
		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any())).thenReturn(true);
		when(cpqConfigurableChecker.isCPQConfigurableProduct(any())).thenReturn(true);
		classUnderTest.setCpqConfigurableChecker(cpqConfigurableChecker);
		when(configurationAbstractOrderIntegrationStrategy.getConfigurationForAbstractOrderEntry(orderEntry))
				.thenReturn(configModel);
		when(itItem.getTable(ProductConfigurationSapPricingItemMapper.VARCOND)).thenReturn(varcondTable);
		classUnderTest.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategy);
		classUnderTest.setConfigurationAbstractOrderIntegrationStrategy(configurationAbstractOrderIntegrationStrategy);
		classUnderTest.setConfigurationService(mockProductConfigurationService);
	}


	protected ConfigModel defineConfigModel()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		final VariantConditionModel vCond = new VariantConditionModelImpl();
		vCond.setKey(THE_KEY);
		vCond.setFactor(THE_FACTOR);
		final ArrayList<VariantConditionModel> conditions = new ArrayList();
		conditions.add(vCond);
		final InstanceModel rootInstance = new InstanceModelImpl();
		rootInstance.setVariantConditions(conditions);
		configModel.setRootInstance(rootInstance);

		return configModel;
	}


	@Test
	public void testFillImportParameters()
	{
		classUnderTest.fillVariantConditions(itItem, orderEntry);
		verify(varcondTable, times(1)).appendRow();
		verify(varcondTable, times(1)).setValue(ProductConfigurationSapPricingItemMapper.VARCOND, THE_KEY);
		verify(varcondTable, times(1)).setValue(ProductConfigurationSapPricingItemMapper.FACTOR,
				Double.valueOf(THE_FACTOR.doubleValue()));
	}

	@Test
	public void testFillImportParametersNotConfigurable()
	{
		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any())).thenReturn(false);
		classUnderTest.fillVariantConditions(itItem, orderEntry);
		verify(varcondTable, times(0)).appendRow();
		verify(varcondTable, times(0)).setValue(ProductConfigurationSapPricingItemMapper.VARCOND, THE_KEY);
		verify(varcondTable, times(0)).setValue(ProductConfigurationSapPricingItemMapper.FACTOR,
				Double.valueOf(THE_FACTOR.doubleValue()));
	}

	@Test
	public void testFillImportParametersPartiallyConfiguredProductVariant()
	{
		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(any())).thenReturn(true);
		classUnderTest.fillVariantConditions(itItem, orderEntry);
		verify(varcondTable, times(1)).appendRow();
		verify(varcondTable, times(1)).setValue(ProductConfigurationSapPricingItemMapper.VARCOND, THE_KEY);
		verify(varcondTable, times(1)).setValue(ProductConfigurationSapPricingItemMapper.FACTOR,
				Double.valueOf(THE_FACTOR.doubleValue()));
	}

	@Test
	public void testFillImportParametersNoVariantConditions()
	{
		configModel.getRootInstance().setVariantConditions(new ArrayList());
		classUnderTest.fillVariantConditions(itItem, orderEntry);
		verify(varcondTable, times(0)).appendRow();
		verify(varcondTable, times(0)).setValue(ProductConfigurationSapPricingItemMapper.VARCOND, THE_KEY);
		verify(varcondTable, times(0)).setValue(ProductConfigurationSapPricingItemMapper.FACTOR,
				Double.valueOf(THE_FACTOR.doubleValue()));
	}

	@Test
	public void testCreateDefaultConfiguration()
	{
		when(mockProductConfigurationService.createDefaultConfiguration(any())).thenReturn(configModel);

		final ConfigModel config = classUnderTest.createConfiguration(productModel);
		verify(mockProductConfigurationService, times(1)).createDefaultConfiguration(any());
	}

	@Test
	public void testCreateConfigurationForVariant()
	{
		when(cpqConfigurableChecker.isCPQConfigurableProduct(any())).thenReturn(false);
		when(cpqConfigurableChecker.isCPQChangeableVariantProduct(any())).thenReturn(true);
		when(mockProductConfigurationService.createConfigurationForVariant(PRODUCT_CODE, PCPV_CODE)).thenReturn(configModel);

		final ConfigModel config = classUnderTest.createConfiguration(varianteProduct);
		verify(mockProductConfigurationService, times(1)).createConfigurationForVariant(PRODUCT_CODE, PCPV_CODE);
	}

	@Test
	public void testFillVariantConditionsForProductList()
	{
		when(mockProductConfigurationService.createDefaultConfiguration(any())).thenReturn(configModel);

		final List<ProductModel> products = new ArrayList<>();
		products.add(productModel);
		classUnderTest.fillVariantConditions(itItem, products);

		verify(mockProductConfigurationService, times(1)).releaseSession(any());
	}

	@Test
	public void testFillVariantConditionsForPCPVProductList()
	{
		when(cpqConfigurableChecker.isCPQConfigurableProduct(any())).thenReturn(false);
		when(cpqConfigurableChecker.isCPQChangeableVariantProduct(any())).thenReturn(true);
		when(mockProductConfigurationService.createConfigurationForVariant(PRODUCT_CODE, PCPV_CODE)).thenReturn(configModel);

		final List<ProductModel> products = new ArrayList<>();
		products.add(varianteProduct);
		classUnderTest.fillVariantConditions(itItem, products);

		verify(mockProductConfigurationService, times(1)).releaseSession(any());
	}

	@Test
	public void testFillVariantConditions()
	{
		final String test_product_code = "test_product";
		when(mockProduct.getCode()).thenReturn(test_product_code);
		final List<ProductModel> products = new ArrayList<>();
		products.add(productModel);
		products.add(mockProduct);
		products.add(varianteProduct);

		when(cpqConfigurableChecker.isCPQConfigurableProduct(productModel)).thenReturn(true);
		when(cpqConfigurableChecker.isCPQChangeableVariantProduct(productModel)).thenReturn(false);

		when(cpqConfigurableChecker.isCPQConfigurableProduct(mockProduct)).thenReturn(true);
		when(cpqConfigurableChecker.isCPQChangeableVariantProduct(mockProduct)).thenReturn(false);

		when(cpqConfigurableChecker.isCPQConfigurableProduct(varianteProduct)).thenReturn(false);
		when(cpqConfigurableChecker.isCPQChangeableVariantProduct(varianteProduct)).thenReturn(true);

		assertNotNull(configModel);
		assertNotNull(configModel.getRootInstance());
		assertNotNull(configModel.getRootInstance().getVariantConditions());
		when(mockProductConfigurationService.createDefaultConfiguration(argThat(new KBKeyMatcher(PRODUCT_CODE))))
				.thenReturn(configModel);

		when(mockProductConfigurationService.createDefaultConfiguration(argThat(new KBKeyMatcher(test_product_code))))
				.thenThrow(new IllegalStateException());

		when(mockProductConfigurationService.createConfigurationForVariant(PRODUCT_CODE, PCPV_CODE)).thenReturn(configModel);

		classUnderTest.fillVariantConditions(itItem, products);
		verify(mockProductConfigurationService, times(2)).releaseSession(any());
	}

	private class KBKeyMatcher extends ArgumentMatcher<KBKey>
	{

		public KBKeyMatcher(final String productCode)
		{
			key = new KBKeyImpl(productCode);
		}

		private final KBKey key;

		@Override
		public boolean matches(final Object argument)
		{
			return argument instanceof KBKey && key.getProductCode().equals(((KBKey) argument).getProductCode());
		}

	}

}
