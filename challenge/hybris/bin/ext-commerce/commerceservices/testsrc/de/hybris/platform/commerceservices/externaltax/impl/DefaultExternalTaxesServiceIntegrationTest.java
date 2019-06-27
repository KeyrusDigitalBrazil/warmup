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
package de.hybris.platform.commerceservices.externaltax.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultExternalTaxesServiceIntegrationTest extends ServicelayerTest
{
	@Resource
	private DefaultExternalTaxesService defaultExternalTaxesService;

	@Resource
	private ModelService modelService;

	@Resource
	private CommonI18NService commonI18NService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
	}

	@Test
	public void saveOrderShouldSetCalculatedToTrue()
	{
		final CartModel cart = modelService.create(CartModel.class);
		cart.setCode("saveOrderShouldSetCalculatedToTrue");
		cart.setCalculated(Boolean.FALSE);
		cart.setDate(new Date());
		cart.setCurrency(commonI18NService.getCurrency("USD"));

		final CustomerModel customer = new CustomerModel();
		customer.setUid("uid");
		cart.setUser(customer);


		modelService.save(cart);
		Assert.assertEquals(Boolean.FALSE, cart.getCalculated());

		defaultExternalTaxesService.saveOrder(cart);
		Assert.assertEquals(Boolean.TRUE, cart.getCalculated());
	}

}
