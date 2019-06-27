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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItem;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CPQDefaultBolCartFacadeUnitTest
{
	private static final String NEW_HANDLE = "new handle";

	private static final String HANDLE = "handle";

	private static final String NEW_PRODUCT_ID = "new product id";

	private static final String PRODUCT_CODE = "product code";

	@InjectMocks
	private CPQDefaultBolCartFacade classUnderTest;

	@Mock
	private GenericFactory genericFactory;

	@Mock
	private Basket cart;
	@Mock
	private CPQItem item;
	@Mock
	private CPQItem newItem;

	private final ConfigModel configModel = new ConfigModelImpl();

	@Before
	public void setup()
	{
		when(genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_CART)).thenReturn(cart);
		when(cart.getItem(any())).thenReturn(item);
		when(cart.isInitialized()).thenReturn(true);
		when(cart.createItem()).thenReturn(newItem);
		configModel.setRootInstance(new InstanceModelImpl());
		configModel.getRootInstance().setName(PRODUCT_CODE);
		when(item.getProductId()).thenReturn(PRODUCT_CODE);
		when(item.getHandle()).thenReturn(HANDLE);
		when(newItem.getHandle()).thenReturn(NEW_HANDLE);
		when(item.getQuantity()).thenReturn(BigDecimal.ONE);
	}

	@Test
	public void testHandleUpdateReplaceVariant()
	{
		configModel.getRootInstance().setName(NEW_PRODUCT_ID);
		final String result = classUnderTest.handleUpdate(configModel, item);
		assertNotNull(result);
		assertEquals(NEW_HANDLE, result);
		verify(item).setProductId("");
		verify(newItem).setConfigurable(true);
		verify(newItem).setProductConfiguration(configModel);
	}

	@Test
	public void testHandleUpdateConfigurableProduct()
	{
		final String result = classUnderTest.handleUpdate(configModel, item);
		assertNotNull(result);
		assertEquals(HANDLE, result);
		verify(item).setProductConfigurationDirty(true);
		verify(item).setProductConfiguration(configModel);
	}

	@Test
	public void testUpdateConfigurationInCart()
	{
		final String result = classUnderTest.updateConfigurationInCart("key", configModel);
		assertNotNull(result);
		assertEquals(HANDLE, result);
		verify(item).setProductConfigurationDirty(true);
		verify(item).setProductConfiguration(configModel);
	}

	@Test
	public void testPerformVariantReplacementQuantity()
	{
		when(item.getQuantity()).thenReturn(BigDecimal.TEN);
		classUnderTest.performVariantReplacement(configModel, item);
		verify(newItem).setQuantity(eq(BigDecimal.TEN));
	}

}
