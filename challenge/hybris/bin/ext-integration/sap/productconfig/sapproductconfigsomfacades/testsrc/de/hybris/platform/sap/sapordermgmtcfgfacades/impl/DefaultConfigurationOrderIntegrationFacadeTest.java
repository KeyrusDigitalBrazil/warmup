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
package de.hybris.platform.sap.sapordermgmtcfgfacades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.external.impl.ConfigurationImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.order.OrderService;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItem;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItemSalesDoc;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Date;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultConfigurationOrderIntegrationFacadeTest
{
	DefaultConfigurationOrderIntegrationFacade classUnderTest = new DefaultConfigurationOrderIntegrationFacade();
	private static final String code = "1234";
	private static final int entryNumber = 0;
	private OrderService orderService;
	private BaseStoreService baseStoreService;
	private final ConfigModel configModel = new ConfigModelImpl();
	private InstanceModel rootInstance;
	private BaseStoreModel baseStore;
	private SAPConfigurationModel sapConfiguration;
	private static final String productCode = "Product";
	private static final String configId = "1";
	private final Item item = new CPQItemSalesDoc();
	private final Configuration externalConfiguration = new ConfigurationImpl();
	private Date date;

	@Before
	public void initialize()
	{
		rootInstance = new InstanceModelImpl();
		rootInstance.setName(productCode);
		configModel.setRootInstance(rootInstance);
		configModel.setId(configId);
		orderService = EasyMock.createMock(OrderService.class);
		classUnderTest.setOrderService(orderService);
		EasyMock.expect(orderService.getItemFromOrder(code, String.valueOf(entryNumber))).andReturn(item);
		EasyMock.replay(orderService);

		final ProductConfigurationService productConfigurationService = EasyMock.createMock(ProductConfigurationService.class);
		EasyMock.expect(productConfigurationService.createConfigurationFromExternalSource(externalConfiguration))
				.andReturn(configModel);
		productConfigurationService.releaseSession(configId, true);
		EasyMock.expectLastCall();
		EasyMock.replay(productConfigurationService);
		((CPQItem) item).setExternalConfiguration(externalConfiguration);
		date = new Date();
		((CPQItem) item).setKbDate(date);
		item.setProductId(code);
		classUnderTest.setProductConfigurationService(productConfigurationService);

		sapConfiguration = EasyMock.createMock(SAPConfigurationModel.class);
		EasyMock.expect(sapConfiguration.isSapordermgmt_enabled()).andReturn(Boolean.TRUE);
		EasyMock.replay(sapConfiguration);

		baseStore = EasyMock.createMock(BaseStoreModel.class);
		EasyMock.expect(baseStore.getSAPConfiguration()).andReturn(sapConfiguration).anyTimes();
		EasyMock.replay(baseStore);

		baseStoreService = EasyMock.createMock(BaseStoreService.class);
		EasyMock.expect(baseStoreService.getCurrentBaseStore()).andReturn(baseStore).anyTimes();
		EasyMock.replay(baseStoreService);
		classUnderTest.setOrderService(orderService);
		classUnderTest.setBaseStoreService(baseStoreService);
	}


	@Test
	public void testGetConfiguration()
	{
		final ConfigurationOverviewData configurationOverviewData = classUnderTest.getConfiguration(code, entryNumber);
		assertNotNull(configurationOverviewData);
		assertEquals(productCode, configurationOverviewData.getProductCode());
		assertEquals(configId, configurationOverviewData.getId());
	}

	@Test
	public void testGetConfiguration2()
	{
		final ConfigModel configModel = classUnderTest.getConfiguration(item);
		assertNotNull(configModel);
		assertEquals(this.configModel, configModel);
	}

	@Test
	public void testEnrichConfigurationFromItem()
	{
		classUnderTest.enrichConfigurationFromItem(externalConfiguration, (CPQItem) item);
		final KBKey kbKey = externalConfiguration.getKbKey();
		assertNotNull(kbKey);
		assertEquals(code, kbKey.getProductCode());
		assertEquals(date, kbKey.getDate());
	}

	@Test
	public void testOrderService()
	{
		classUnderTest.setOrderService(orderService);
		assertEquals(orderService, classUnderTest.getOrderService());
	}

	@Test
	public void testBaseStoreService()
	{
		assertEquals(baseStoreService, classUnderTest.getBaseStoreService());
	}


}
