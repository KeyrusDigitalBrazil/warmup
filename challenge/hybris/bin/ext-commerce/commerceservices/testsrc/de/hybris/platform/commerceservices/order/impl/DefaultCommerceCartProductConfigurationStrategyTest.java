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
package de.hybris.platform.commerceservices.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.ProductConfigurationHandler;
import de.hybris.platform.commerceservices.order.ProductConfigurationHandlerFactory;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.ProductConfigurationItem;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Test suite for {@link DefaultCommerceCartProductConfigurationStrategy}
 */
@UnitTest
public class DefaultCommerceCartProductConfigurationStrategyTest
{

	@InjectMocks
	private DefaultCommerceCartProductConfigurationStrategy productConfigurationStrategy = new DefaultCommerceCartProductConfigurationStrategy();

	@Mock
	private ProductConfigurationHandlerFactory configurationHandlerFactory;
	@Mock
	private ModelService modelService;
	@Mock
	private CartService cartService;
	@Mock
	private ProductConfigurationHandler productConfigurationHandler;

	private static final ConfiguratorType RADIOBUTTON_CONFIGURATOR_TYPE = ConfiguratorType.valueOf("RADIOBUTTON");

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		given(configurationHandlerFactory.handlerOf(eq(RADIOBUTTON_CONFIGURATOR_TYPE))).willReturn(productConfigurationHandler);
	}

	@After
	public void noMoreInteractions() {
		verifyNoMoreInteractions(configurationHandlerFactory, modelService, cartService, productConfigurationHandler);
	}

	@Test
	public void testConfigureCartEntryNullCart()
	{
		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();

		try
		{
			productConfigurationStrategy.configureCartEntry(commerceCartParameter);
			Assert.fail("CommerceCartModificationException is expected when configuring entry with no cart in cart parameter");
		}
		catch (CommerceCartModificationException e)
		{
			Assert.assertEquals("Null cart", e.getMessage());
		}
	}

	@Test
	public void testConfigureCartEntryNullCartEntries()
	{
		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		final CartModel cartModel = new CartModel();
		commerceCartParameter.setCart(cartModel);

		try
		{
			productConfigurationStrategy.configureCartEntry(commerceCartParameter);
			Assert.fail("CommerceCartModificationException is expected when configuring entry with null cart entries");
		}
		catch (CommerceCartModificationException e)
		{
			Assert.assertEquals("Cart has no entries", e.getMessage());
		}
	}

	@Test
	public void testConfigureCartEntryNullConfiguration() throws CommerceCartModificationException
	{
		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		final CartModel cartModel = new CartModel();
		cartModel.setEntries(Collections.emptyList());
		commerceCartParameter.setCart(cartModel);
		commerceCartParameter.setProductConfiguration(null);

		try
		{
			productConfigurationStrategy.configureCartEntry(commerceCartParameter);
			Assert.fail("CommerceCartModificationException is expected when configuring with null product configuration" +
					"in commerce cart parameter");
		}
		catch (CommerceCartModificationException e)
		{
			Assert.assertEquals("Product configuration is null", e.getMessage());
		}
	}

	@Test
	public void testConfigureCartEntryNullConfiguratorType() throws CommerceCartModificationException
	{
		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		final CartModel cartModel = new CartModel();
		cartModel.setEntries(Collections.emptyList());
		commerceCartParameter.setCart(cartModel);

		final ProductConfigurationItem configurationItem = new ProductConfigurationItem();
		configurationItem.setConfiguratorType(null);
		configurationItem.setKey("key");
		configurationItem.setValue("value");
		configurationItem.setStatus(ProductInfoStatus.NONE);
		commerceCartParameter.setProductConfiguration(Collections.singletonList(configurationItem));

		try
		{
			productConfigurationStrategy.configureCartEntry(commerceCartParameter);
			Assert.fail("CommerceCartModificationException is expected when configuring entry where at least one " +
					"configuration item has null configurator type");
		}
		catch (CommerceCartModificationException e)
		{
			Assert.assertEquals("Product configuration item has null type", e.getMessage());
		}
	}

	@Test
	public void testConfigureCartEntryNoHandler() throws CommerceCartModificationException {
		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		final CartModel cartModel = new CartModel();
		cartModel.setEntries(Collections.emptyList());

		final ProductConfigurationItem configurationItem = new ProductConfigurationItem();
		configurationItem.setConfiguratorType(RADIOBUTTON_CONFIGURATOR_TYPE);
		configurationItem.setKey("key");
		configurationItem.setValue("value");
		configurationItem.setStatus(ProductInfoStatus.NONE);
		commerceCartParameter.setProductConfiguration(Collections.singletonList(configurationItem));
		commerceCartParameter.setCart(cartModel);
		commerceCartParameter.setEntryNumber(1);

		given(configurationHandlerFactory.handlerOf(any())).willReturn(null);

		final CartEntryModel entryModel = new CartEntryModel();
		given(cartService.getEntryForNumber(Mockito.any(), Mockito.anyInt())).willReturn(entryModel);

		try
		{
			productConfigurationStrategy.configureCartEntry(commerceCartParameter);
			Assert.fail("CommerceCartModificationException is expected when configuring entry with no hanlder for" +
					"specific configuration type");
		}
		catch (CommerceCartModificationException e)
		{
			Assert.assertEquals("No handler for configuration type RADIOBUTTON", e.getMessage());
			verify(configurationHandlerFactory).handlerOf(RADIOBUTTON_CONFIGURATOR_TYPE);
			verify(cartService).getEntryForNumber(cartModel, 1);
		}
	}


	@Test
	public void testConfigureCartEntryEmptyCartEntries() throws CommerceCartModificationException
	{
		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		final CartModel cartModel = new CartModel();
		cartModel.setEntries(Collections.emptyList());
		commerceCartParameter.setCart(cartModel);

		commerceCartParameter.setEntryNumber(1);
		commerceCartParameter.setProductConfiguration(Collections.emptyList());

		productConfigurationStrategy.configureCartEntry(commerceCartParameter);
		verify(cartService).getEntryForNumber(cartModel, 1);
	}

	@Test
	public void testConfigureCartEntry() throws CommerceCartModificationException
	{
		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		final CartModel cartModel = new CartModel();
		final CartEntryModel entryModel = new CartEntryModel();
		final AbstractOrderEntryProductInfoModel productInfo = new AbstractOrderEntryProductInfoModel();
		final ProductConfigurationItem configurationItem = new ProductConfigurationItem();

		cartModel.setEntries(Collections.emptyList());
		configurationItem.setConfiguratorType(RADIOBUTTON_CONFIGURATOR_TYPE);
		configurationItem.setKey("key");
		configurationItem.setValue("value");
		configurationItem.setStatus(ProductInfoStatus.NONE);
		commerceCartParameter.setProductConfiguration(Collections.singletonList(configurationItem));
		commerceCartParameter.setCart(cartModel);
		commerceCartParameter.setEntryNumber(1);
		productInfo.setConfiguratorType(RADIOBUTTON_CONFIGURATOR_TYPE);
		entryModel.setProductInfos(Collections.singletonList(productInfo));

		final AbstractOrderEntryProductInfoModel model = new AbstractOrderEntryProductInfoModel();
		given(cartService.getEntryForNumber(Mockito.any(), Mockito.anyInt())).willReturn(entryModel);
		given(productConfigurationHandler.convert(any(), any())).willReturn(Collections.singletonList(model));

		productConfigurationStrategy.configureCartEntry(commerceCartParameter);

		final ArgumentCaptor<List<AbstractOrderEntryProductInfoModel>> captor = ArgumentCaptor
				.forClass((Class)List.class);
		verify(cartService).getEntryForNumber(cartModel, 1);
		verify(configurationHandlerFactory).handlerOf(RADIOBUTTON_CONFIGURATOR_TYPE);
		verify(productConfigurationHandler).convert(Collections.singletonList(configurationItem), entryModel);
		verify(modelService).saveAll(captor.capture());
		verify(modelService).save(entryModel);

		Assert.assertEquals(entryModel, captor.getValue().get(0).getOrderEntry());
	}
}
