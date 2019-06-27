/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.integration.cis.tax.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


@UnitTest
public class DefaultCisDetermineExternalTaxStrategyTest
{
	private DefaultCisDetermineExternalTaxStrategy defaultOmsDetermineExternalTaxStrategy;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultOmsDetermineExternalTaxStrategy = new DefaultCisDetermineExternalTaxStrategy();
	}

	@Test
	public void shouldNotCalculateTaxes()
	{
		final CartModel cart = mock(CartModel.class);
		given(cart.getNet()).willReturn(Boolean.FALSE);
		final boolean calculateTaxes = defaultOmsDetermineExternalTaxStrategy.shouldCalculateExternalTaxes(cart);
		assertEquals(calculateTaxes, false);
	}

	@Test
	public void shouldCalculateTaxes()
	{
		final AddressModel address = mock(AddressModel.class);

		final DeliveryModeModel deliveryMode = mock(DeliveryModeModel.class);

		final CartModel cart = mock(CartModel.class);
		final BaseStoreModel baseStore = mock(BaseStoreModel.class);
		given(baseStore.getExternalTaxEnabled()).willReturn(Boolean.TRUE);
		given(cart.getStore()).willReturn(baseStore);
		given(cart.getNet()).willReturn(Boolean.TRUE);
		given(cart.getDeliveryAddress()).willReturn(address);
		given(cart.getDeliveryMode()).willReturn(deliveryMode);

		final boolean calculateTaxes = defaultOmsDetermineExternalTaxStrategy.shouldCalculateExternalTaxes(cart);
		assertEquals(calculateTaxes, true);
	}
}
