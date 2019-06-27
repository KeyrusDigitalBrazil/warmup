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
package de.hybris.platform.sap.sapproductconfigsomservices.bolfacade.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;
import de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


/**
 *
 */

@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:configuration-strategy.xml" })
public class CPQDefaultBolCartFacadeTest extends SapordermanagmentBolSpringJunitTest
{
	CPQDefaultBolCartFacade classUnderTest = new CPQDefaultBolCartFacade();
	Item item = new ItemSalesDoc();
	private final String productId = "KDAC";

	/**
	 *
	 */
	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		final SapPartnerService partnerService = EasyMock.createMock(SapPartnerService.class);
		EasyMock.expect(partnerService.getCurrentSapCustomerId()).andReturn("A");
		EasyMock.expect(partnerService.getCurrentSapContactId()).andReturn("A");
		EasyMock.replay(partnerService);
		classUnderTest.setSapPartnerService(partnerService);
		classUnderTest.setGenericFactory(genericFactory);
		final Basket cart = classUnderTest.getCart();
		assertNotNull(cart);
		item.setNumberInt(10);
		item.setProductId(productId);
		cart.addItem(item);
	}

	@Test
	public void testGetProductIdFromConfigModel()
	{
		final ConfigModel configModel = createCfgModel();
		final String productIdNewItem = classUnderTest.getProductIdFromConfigModel(configModel);
		assertEquals(productId, productIdNewItem);
	}

	/**
	 * @return
	 */
	ConfigModel createCfgModel()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		final InstanceModel rootInstance = new InstanceModelImpl();

		rootInstance.setName(productId);
		configModel.setRootInstance(rootInstance);
		return configModel;
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testUpdateConfigurationInCartItemDoesNotExist() throws CommunicationException
	{
		final ConfigModel configModel = createCfgModel();
		final String handle = "A";
		classUnderTest.updateConfigurationInCart(handle, configModel);
	}

}
