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
package de.hybris.platform.commerceservices.order.hook.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.ProductConfigurationHandler;
import de.hybris.platform.commerceservices.order.ProductConfigurationHandlerFactory;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.product.ConfiguratorSettingsService;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;



@UnitTest
public class ConfigurableProductAddToCartMethodHookTest
{
	private ConfigurableProductAddToCartMethodHook hook;
	private final ConfiguratorSettingsService configuratorSettingsService = mock(ConfiguratorSettingsService.class);

	@Before
	public void setup()
	{
		hook = new ConfigurableProductAddToCartMethodHook();
		final ProductConfigurationHandler handler = mock(ProductConfigurationHandler.class);
		final AbstractOrderEntryProductInfoModel orderedProductInfo = new AbstractOrderEntryProductInfoModel();
		when(handler.createProductInfo(any(AbstractConfiguratorSettingModel.class)))
				.thenReturn(Collections.singletonList(orderedProductInfo));
		final ProductConfigurationHandlerFactory factory = mock(ProductConfigurationHandlerFactory.class);
		when(factory.handlerOf(any(ConfiguratorType.class))).thenReturn(handler);
		hook.setConfigurationFactory(factory);
		final ModelService modelService = mock(ModelService.class);
		hook.setModelService(modelService);
		hook.setConfiguratorSettingsService(configuratorSettingsService);
	}

	@Test
	public void shouldUpdateCartEntry() throws CommerceCartModificationException
	{
		final ProductModel product = new ProductModel();
		when(configuratorSettingsService.getConfiguratorSettingsForProduct(product))
				.thenReturn(Collections.singletonList(new AbstractConfiguratorSettingModel()));
		final CartModel cart = new CartModel();
		cart.setEntries(Collections.singletonList(new CartEntryModel()));
		cart.getEntries().get(0).setEntryNumber(Integer.valueOf(1));
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(cart);
		parameter.setEntryNumber(1);
		parameter.setProduct(product);
		final CommerceCartModification modification = new CommerceCartModification();
		modification.setEntry(cart.getEntries().get(0));
		modification.getEntry().setProduct(product);
		modification.setQuantityAdded(1L);

		hook.afterAddToCart(parameter, modification);

		assertNotNull(cart.getEntries().get(0).getProductInfos());
		assertFalse(cart.getEntries().get(0).getProductInfos().isEmpty());
	}
}
