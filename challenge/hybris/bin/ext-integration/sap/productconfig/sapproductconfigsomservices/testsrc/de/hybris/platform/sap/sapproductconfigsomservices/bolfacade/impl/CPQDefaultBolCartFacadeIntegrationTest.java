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

import static org.junit.Assert.assertNotNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.sapordermgmtservices.partner.SapPartnerService;
import de.hybris.platform.sap.sapproductconfigsombol.integraationtests.base.JCoIntegrationTestBase;

/**
 *
 */

@IntegrationTest
public class CPQDefaultBolCartFacadeIntegrationTest extends JCoIntegrationTestBase
{
	private static final String PRODUCT_STD = "KD990KAC";
	private static final String PRODUCT_CFG = "KD990MIX";
	CPQDefaultBolCartFacade classUnderTest = new CPQDefaultBolCartFacade();

	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		classUnderTest.setGenericFactory(genericFactory);

		final SapPartnerService sapPartnerServiceMock = EasyMock.createNiceMock(SapPartnerService.class);
		EasyMock.expect(sapPartnerServiceMock.getCurrentSapContactId()).andReturn("0000154407").anyTimes();
		EasyMock.expect(sapPartnerServiceMock.getCurrentSapCustomerId()).andReturn("0000100171").anyTimes();

		EasyMock.replay(sapPartnerServiceMock);

		classUnderTest.setSapPartnerService(sapPartnerServiceMock);
	}

	@Test
	@Ignore("TIGER-3541")
	public void testAddConfigurationToCart()
	{
		final ConfigModel configModel = createCfgModel();
		final String itemKey = classUnderTest.addConfigurationToCart(configModel);
		assertNotNull(itemKey);
	}



	ConfigModel createCfgModel()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		final InstanceModel rootInstance = new InstanceModelImpl();

		rootInstance.setName(PRODUCT_CFG);
		rootInstance.setId("1");
		configModel.setRootInstance(rootInstance);
		return configModel;
	}

}
