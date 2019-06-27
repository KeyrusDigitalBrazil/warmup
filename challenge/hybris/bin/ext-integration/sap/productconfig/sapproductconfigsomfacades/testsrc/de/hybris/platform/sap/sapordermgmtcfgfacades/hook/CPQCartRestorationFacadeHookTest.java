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
package de.hybris.platform.sap.sapordermgmtcfgfacades.hook;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.sapproductconfigsomservices.prodconf.impl.DefaultProductConfigurationService;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */

@UnitTest
public class CPQCartRestorationFacadeHookTest
{

	/**
	 *
	 */
	private static final double PRICE = 100.0;
	/**
	 *
	 */
	private static final String CONFIGURATION = "Configuration";
	private static final double DELTA = 0.0;
	private DefaultProductConfigurationService defaultConfigurationService;
	private ModelService modelService;
	private CPQCartRestorationFacadeHook classUnderTest;
	private String pk;
	private OrderEntryData entry;
	private AbstractOrderEntryModel entryModel;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		pk = "1";
		entryModel = new AbstractOrderEntryModel();
		entry = new OrderEntryData();
		entry.setItemPK(pk);
		classUnderTest = new CPQCartRestorationFacadeHook();
		defaultConfigurationService = EasyMock.createMock(DefaultProductConfigurationService.class);
		EasyMock.expect(defaultConfigurationService.getExternalConfiguration(pk)).andReturn(CONFIGURATION);
		EasyMock.expect(defaultConfigurationService.getTotalPrice(pk)).andReturn(PRICE);
		EasyMock.expect(defaultConfigurationService.isInSession(pk)).andReturn(true);
		modelService = EasyMock.createMock(DefaultModelService.class);
		modelService.save(entryModel);
		EasyMock.expectLastCall();
		EasyMock.replay(defaultConfigurationService, modelService);
		classUnderTest.setProductConfigurationService(defaultConfigurationService);
		classUnderTest.setModelService(modelService);
	}

	/**
	 * Test method for {@link de.hybris.platform.sap.sapordermgmtcfgfacades.hook.CPQCartRestorationFacadeHook#afterAddCartEntriesToStandardCart(de.hybris.platform.commercefacades.order.data.OrderEntryData, de.hybris.platform.core.model.order.AbstractOrderEntryModel)}.
	 */
	@Test
	public void testAfterAddCartEntriesToStandardCart()
	{
		classUnderTest.afterAddCartEntriesToStandardCart(entry, entryModel);
		assertEquals(CONFIGURATION, entryModel.getExternalConfiguration());
		assertEquals(PRICE, entryModel.getBasePrice().doubleValue(), DELTA);
	}

}
