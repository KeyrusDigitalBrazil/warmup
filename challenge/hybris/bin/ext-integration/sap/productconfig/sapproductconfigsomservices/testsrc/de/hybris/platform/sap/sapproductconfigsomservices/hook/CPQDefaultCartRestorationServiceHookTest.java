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
package de.hybris.platform.sap.sapproductconfigsomservices.hook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItemSalesDoc;
import de.hybris.platform.sap.sapproductconfigsomservices.prodconf.ProductConfigurationSomService;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */

@UnitTest
public class CPQDefaultCartRestorationServiceHookTest
{

	private CPQDefaultCartRestorationServiceHook classUnderTest;
	private AbstractOrderEntryModel entry1;
	private CPQItemSalesDoc item;
	private ProductConfigurationSomService productConfigurationServiceMock;
	private CPQConfigurableChecker cpqConfigurableCheckerMock;
	private ConfigModel configModel;

	/**
	 *
	 */
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new CPQDefaultCartRestorationServiceHook();
		item = new CPQItemSalesDoc();
		item.setHandle("1");
		entry1 = new AbstractOrderEntryModel();
		final ProductModel pModel = new ProductModel();
		pModel.setSapConfigurable(Boolean.TRUE);
		pModel.setCode("KAP");
		entry1.setProduct(pModel);
		entry1.setQuantity(new Long(2));
		entry1.setExternalConfiguration(null);

		productConfigurationServiceMock = EasyMock.createMock(ProductConfigurationSomService.class);
		cpqConfigurableCheckerMock = EasyMock.createMock(CPQConfigurableChecker.class);
		configModel = new ConfigModelImpl();
		EasyMock.expect(productConfigurationServiceMock.getConfigModel("KAP", null)).andReturn(configModel);
		productConfigurationServiceMock.setIntoSession("1", null);
		EasyMock.expectLastCall();
		EasyMock.expect(cpqConfigurableCheckerMock.isCPQConfiguratorApplicableProduct(pModel)).andReturn(true);
		classUnderTest.setProductConfigurationService(productConfigurationServiceMock);
		classUnderTest.setCpqConfigurableChecker(cpqConfigurableCheckerMock);

		EasyMock.replay(productConfigurationServiceMock);
		EasyMock.replay(cpqConfigurableCheckerMock);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapproductconfigsomservices.hook.CPQDefaultCartRestorationServiceHook#afterCreateItemHook(de.hybris.platform.core.model.order.AbstractOrderEntryModel, de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item)}.
	 */
	@Test
	public void testAfterCreateItemHook()
	{
		classUnderTest.afterCreateItemHook(entry1, item);
		final ConfigModel model = item.getProductConfiguration();
		assertNotNull(model);
		assertEquals(configModel, model);
		assertTrue(item.isConfigurable());
	}

}
