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
package de.hybris.platform.sap.sapordermgmtcfgfacades.cart.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.sapproductconfigsomservices.prodconf.impl.DefaultProductConfigurationService;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultStandardOrderEntryPopulatorTest
{
	DefaultStandardOrderEntryPopulator classUnderTest = new DefaultStandardOrderEntryPopulator();
	private AbstractOrderEntryModel source;
	private String pk;
	private DefaultProductConfigurationService defaultConfigurationService;
	private ProductModel productModel;


	@Test
	public void testPopulate()
	{


		final OrderEntryData target = new OrderEntryData();
		classUnderTest.populateCFGAttributes(source, target);
		assertTrue(target.isConfigurationAttached());
		assertEquals(pk, target.getItemPK());
	}

	@Before
	public void init()
	{
		source = EasyMock.createMock(AbstractOrderEntryModel.class);
		productModel = EasyMock.createMock(ProductModel.class);
		pk = "1";
		final String cfg = "<XML>";
		final PK key = PK.parse(pk);
		EasyMock.expect(source.getPk()).andReturn(key).anyTimes();
		EasyMock.expect(source.getExternalConfiguration()).andReturn(cfg);
		EasyMock.expect(productModel.getSapConfigurable()).andReturn(true);
		defaultConfigurationService = EasyMock.createMock(DefaultProductConfigurationService.class);
		EasyMock.expect(defaultConfigurationService.isInSession(pk)).andReturn(true);
		final SessionAccessService sessionAccessService = EasyMock.createMock(SessionAccessService.class);
		sessionAccessService.setConfigIdForCartEntry(pk, null);
		EasyMock.expect(sessionAccessService.getCartEntryForConfigId(pk)).andReturn(null);
		defaultConfigurationService.setSessionAccessService(sessionAccessService);
		classUnderTest.setProductConfigurationService(defaultConfigurationService);
		EasyMock.expect(source.getProduct()).andReturn(productModel);
		EasyMock.expect(productModel.getSapConfigurable()).andReturn(Boolean.TRUE);
		EasyMock.replay(source, sessionAccessService, defaultConfigurationService, productModel);


	}

	@Test
	public void testProductConfigurationService()
	{

		assertEquals(defaultConfigurationService, classUnderTest.getProductConfigurationService());
	}

	@Test
	public void testIsConfigurationSessionAvailable()
	{
		assertTrue(classUnderTest.isConfigurationSessionAvailable(pk));
	}
}
