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
package de.hybris.platform.sap.sapproductconfigsomservices.converters.populators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.VariantConfigurationInfoProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.data.CartEntryConfigurationAttributes;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationOrderIntegrationService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItem;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItemSalesDoc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultAbstractOrderEntryConfigurablePopulatorTest
{
	private static final String TEST_HANDLE = "TestHandle";
	final CPQItem item = new CPQItemSalesDoc();
	final String productId = "1";
	final String internalProductId = "01";

	private static final String TEST_PRODUCT_ID = "TestProductID";
	private static final int NUMBER_INT = 1;
	private static final BigDecimal QUANTITY = new BigDecimal(15);
	private static final BigDecimal GROSS_VALUE = new BigDecimal(8);
	private static final BigDecimal NET_VALUE_WO_FREIGHT = new BigDecimal(5);

	private final DefaultAbstractOrderEntryConfigurablePopulator classUnderTest = new DefaultAbstractOrderEntryConfigurablePopulator();
	private final Date creationDate = new Date(System.currentTimeMillis());

	private static final String ITEM_HANDLE = "123";
	private static final String VALUE_NAME = "Value A";
	private static final String CHARACTERISTIC_NAME = "Characteristic One";

	@Before
	public void setUp()
	{
		mockConfigurationEntities();
	}

	@Test
	public void testBeanInstanciation()
	{
		Assert.assertNotNull(classUnderTest);
	}

	@Test
	public void testPopulateAbstractOrderEntryPopulator()
	{
		final OrderEntryData target = new OrderEntryData();
		final Item item = new ItemSalesDoc();
		item.setHandle(TEST_HANDLE);
		classUnderTest.populate(item, target);
		final OrderEntryData orderEntry = target;
		Assert.assertEquals(orderEntry.getItemPK(), TEST_HANDLE);
	}

	@Test
	public void testKbIsPresent()
	{
		item.setKbDate(creationDate);
		item.setProductId(productId);
		final boolean isPresent = classUnderTest.isKbPresent(item);
		assertTrue(isPresent);
	}

	@Test
	public void testKbIsPresentNoKbDateOnItem()
	{
		item.setKbDate(null);
		final boolean isPresent = classUnderTest.isKbPresent(item);
		assertFalse(isPresent);
	}

	@Test
	public void testHandleConfigurationBackendLeads()
	{
		final OrderEntryData target = new OrderEntryData();
		final ProductData product = new ProductData();
		item.setKbDate(creationDate);
		item.setProductId(productId);
		item.setConfigurable(Boolean.TRUE.booleanValue());
		classUnderTest.handleConfigurationBackendLeads(item, target, product);
		assertTrue(target.isConfigurationAttached());
		assertTrue(product.getConfigurable().booleanValue());
	}

	@Test
	public void testHandleConfigurationBackendLeadsNoKb()
	{
		final OrderEntryData target = new OrderEntryData();
		final ProductData product = new ProductData();
		item.setKbDate(null);
		classUnderTest.handleConfigurationBackendLeads(item, target, product);
		assertFalse(target.isConfigurationAttached());
		assertNull(product.getConfigurable());
	}


	protected void mockConfigurationEntities()
	{
		final String configId = mockProductConfigurationService();
		mockSessionAccessService(configId);
		mockProductConfigurationOrderIntegrationService();
		final List<ConfigurationInfoData> configInfoDataList = mockConfigurationInfoDataList();
		mockConfigurationInfoConverter(configInfoDataList);
		mockProductService();
		mockVariantConfigurationInfoProvider(configInfoDataList);
	}

	protected void mockProductService()
	{
		final ProductService productService = EasyMock.createMock(ProductService.class);
		EasyMock.expect(productService.getProductForCode(TEST_PRODUCT_ID)).andReturn(new ProductModel());
		EasyMock.replay(productService);
		classUnderTest.setProductService(productService);
	}

	protected void mockVariantConfigurationInfoProvider(final List<ConfigurationInfoData> configInfoDataList)
	{
		final VariantConfigurationInfoProvider variantConfigurationInfoProvider = EasyMock
				.createMock(VariantConfigurationInfoProvider.class);
		EasyMock.expect(variantConfigurationInfoProvider.retrieveVariantConfigurationInfo(EasyMock.anyObject(ProductModel.class)))
				.andReturn(configInfoDataList);
		EasyMock.replay(variantConfigurationInfoProvider);
		classUnderTest.setVariantConfigurationInfoProvider(variantConfigurationInfoProvider);
	}

	protected void mockConfigurationInfoConverter(final List<ConfigurationInfoData> configInfoDataList)
	{
		final de.hybris.platform.servicelayer.dto.converter.Converter<ConfigModel, List<ConfigurationInfoData>> orderEntryConfigurationInfoConverter = EasyMock
				.createMock(de.hybris.platform.servicelayer.dto.converter.Converter.class);
		EasyMock.expect(orderEntryConfigurationInfoConverter.convert((ConfigModel) EasyMock.anyObject()))
				.andReturn(configInfoDataList);
		EasyMock.replay(orderEntryConfigurationInfoConverter);
		classUnderTest.setOrderEntryConfigurationInfoConverter(orderEntryConfigurationInfoConverter);
	}

	protected List<ConfigurationInfoData> mockConfigurationInfoDataList()
	{
		final List<ConfigurationInfoData> configInfoDataList = new ArrayList<>();

		final ConfigurationInfoData configInfoInline = new ConfigurationInfoData();
		configInfoInline.setConfigurationLabel(CHARACTERISTIC_NAME);
		configInfoInline.setConfigurationValue(VALUE_NAME);
		configInfoInline.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
		configInfoInline.setStatus(ProductInfoStatus.SUCCESS);
		configInfoDataList.add(configInfoInline);
		return configInfoDataList;
	}

	protected void mockSessionAccessService(final String configId)
	{
		final SessionAccessService sessionAccessService = EasyMock.createMock(SessionAccessService.class);
		EasyMock.expect(sessionAccessService.getConfigIdForCartEntry(ITEM_HANDLE)).andReturn(configId);
		EasyMock.replay(sessionAccessService);
		classUnderTest.setSessionAccessService(sessionAccessService);
	}

	protected String mockProductConfigurationService()
	{
		final ProductConfigurationService productConfigurationService = EasyMock.createMock(ProductConfigurationService.class);
		EasyMock
				.expect(Boolean
						.valueOf(productConfigurationService.hasKbForDate((String) EasyMock.anyObject(), (Date) EasyMock.anyObject())))
				.andReturn(Boolean.TRUE);

		final ConfigModel configModel = new ConfigModelImpl();
		final String configId = "S1";
		configModel.setId(configId);
		EasyMock.expect(productConfigurationService.retrieveConfigurationModel(configId)).andReturn(configModel);
		EasyMock.replay(productConfigurationService);
		classUnderTest.setProductConfigurationService(productConfigurationService);
		return configId;
	}

	protected void mockProductConfigurationOrderIntegrationService()
	{
		final CartEntryConfigurationAttributes configAttributes = new CartEntryConfigurationAttributes();
		configAttributes.setConfigurationConsistent(Boolean.TRUE);
		final ProductConfigurationOrderIntegrationService productConfigurationOrderIntegrationService = EasyMock
				.createMock(ProductConfigurationOrderIntegrationService.class);
		EasyMock
				.expect(productConfigurationOrderIntegrationService.calculateCartEntryConfigurationAttributes(
						(String) EasyMock.anyObject(), (String) EasyMock.anyObject(), (String) EasyMock.eq(null)))
				.andReturn(configAttributes);
		EasyMock.replay(productConfigurationOrderIntegrationService);
		classUnderTest.setProductConfigurationOrderIntegrationService(productConfigurationOrderIntegrationService);
	}

	@Test
	public void testHandleConfiguration()
	{
		item.setConfigurable(true);
		item.setHandle(ITEM_HANDLE);
		final ProductData productData = new ProductData();
		productData.setCode(productId);
		final OrderEntryData target = new OrderEntryData();
		classUnderTest.handleConfiguration(item, target, productData);
		final List<ConfigurationInfoData> configInfos = target.getConfigurationInfos();
		assertNotNull(configInfos);
		assertEquals(1, configInfos.size());
		assertEquals(CHARACTERISTIC_NAME, configInfos.get(0).getConfigurationLabel());
		assertEquals(VALUE_NAME, configInfos.get(0).getConfigurationValue());
		assertEquals(ConfiguratorType.CPQCONFIGURATOR, configInfos.get(0).getConfiguratorType());
	}

	@Test
	public void testHandleConfigurationVariant()
	{
		item.setConfigurable(false);
		item.setVariant(true);
		final ProductData productData = new ProductData();
		productData.setCode(TEST_PRODUCT_ID);
		final OrderEntryData target = new OrderEntryData();
		classUnderTest.handleConfiguration(item, target, productData);
		final List<ConfigurationInfoData> configInfos = target.getConfigurationInfos();
		assertNotNull(configInfos);
		assertEquals(1, configInfos.size());
		assertEquals(CHARACTERISTIC_NAME, configInfos.get(0).getConfigurationLabel());
		assertEquals(VALUE_NAME, configInfos.get(0).getConfigurationValue());
		assertEquals(ConfiguratorType.CPQCONFIGURATOR, configInfos.get(0).getConfiguratorType());
	}

	@Test
	public void testHandleVariant()
	{
		item.setVariant(true);
		final ProductData productData = new ProductData();
		productData.setCode(TEST_PRODUCT_ID);
		final OrderEntryData target = new OrderEntryData();
		assertNull(target.getConfigurationInfos());
		classUnderTest.handleVariant(item, target, productData);
		assertTrue(productData.getConfigurable());
		assertNotNull(target.getConfigurationInfos());
	}

	@Test
	public void testHandleVariantNotVariant()
	{
		item.setVariant(false);
		final ProductData productData = new ProductData();
		final OrderEntryData target = new OrderEntryData();
		classUnderTest.handleVariant(item, target, productData);
		assertNull(productData.getConfigurable());
		assertNull(target.getConfigurationInfos());
	}

}
