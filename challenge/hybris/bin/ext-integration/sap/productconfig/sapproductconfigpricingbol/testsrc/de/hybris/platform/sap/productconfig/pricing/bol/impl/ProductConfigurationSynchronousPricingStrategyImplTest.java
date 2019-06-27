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
package de.hybris.platform.sap.productconfig.pricing.bol.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.sappricing.services.SapPricingEnablementService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigurationSynchronousPricingStrategyImplTest
{
	private ProductConfigurationSynchronousPricingStrategyImpl classUnderTest;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private SapPricingEnablementService sapPricingEnablementService;
	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy cartEntryLinkStrategy;
	@Mock
	private PricingService pricingService;
	@Mock
	private ProductConfigurationService configurationService;
	@Mock
	CartEntryModel cartEntry;
	@Mock
	CartModel cart;
	@Mock
	ModelService modelService;

	private final ConfigModel configModel = new ConfigModelImpl();

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigurationSynchronousPricingStrategyImpl();
		classUnderTest.setCommerceCartService(commerceCartService);
		classUnderTest.setSapPricingEnablementService(sapPricingEnablementService);
		classUnderTest.setAbstractOrderEntryLinkStrategy(cartEntryLinkStrategy);
		classUnderTest.setModelService(modelService);
		classUnderTest.setPricingService(pricingService);
		classUnderTest.setConfigurationService(configurationService);
		Mockito.when(sapPricingEnablementService.isCartPricingEnabled()).thenReturn(Boolean.TRUE);
		Mockito.when(cartEntry.getOrder()).thenReturn(cart);
		Mockito.when(cartEntry.getPk()).thenReturn(PK.fromLong(1));
		Mockito.when(cartEntry.getBasePrice()).thenReturn(Double.valueOf(1000));
		Mockito.when(pricingService.isActive()).thenReturn(Boolean.FALSE);
		final PriceModel currentTotal = new PriceModelImpl();
		currentTotal.setPriceValue(BigDecimal.valueOf(2000));
		currentTotal.setCurrency("EUR");
		configModel.setCurrentTotalPrice(currentTotal);
		Mockito.when(configurationService.retrieveConfigurationModel(Mockito.any())).thenReturn(configModel);
	}

	@Test
	public void testUpdateCartEntryPrices() throws CalculationException
	{
		final CommerceCartParameter parameters = new CommerceCartParameter();
		final boolean result = classUnderTest.updateCartEntryPrices(null, true, parameters);
		assertTrue(result);
		Mockito.verify(commerceCartService).recalculateCart(parameters);
	}

	@Test
	public void testUpdateCartEntryPrices_noParameters() throws CalculationException
	{
		final boolean result = classUnderTest.updateCartEntryPrices(cartEntry, true, null);
		assertTrue(result);
		Mockito.verify(commerceCartService).recalculateCart(Mockito.any(CommerceCartParameter.class));
		Mockito.verify(cartEntryLinkStrategy).getConfigIdForCartEntry("1");
	}

	@Test
	public void testUpdateCartEntryPrices_noCalculation() throws CalculationException
	{
		final CommerceCartParameter parameters = new CommerceCartParameter();
		final boolean result = classUnderTest.updateCartEntryPrices(null, false, parameters);
		assertFalse(result);
		Mockito.verify(commerceCartService, Mockito.times(0)).recalculateCart(parameters);
	}

	@Test
	public void testUpdateCartEntryPrices_Exception() throws CalculationException
	{
		final CommerceCartParameter parameters = new CommerceCartParameter();
		Mockito.doThrow(new CalculationException("unit test")).when(commerceCartService).recalculateCart(parameters);
		final boolean result = classUnderTest.updateCartEntryPrices(null, true, parameters);
		assertFalse(result);
	}

	@Test
	public void testUpdateCartEntryPrices_SynchronousPricingInactive() throws CalculationException
	{
		Mockito.when(sapPricingEnablementService.isCartPricingEnabled()).thenReturn(Boolean.FALSE);
		final CommerceCartParameter parameters = new CommerceCartParameter();
		final boolean result = classUnderTest.updateCartEntryPrices(cartEntry, true, parameters);
		assertTrue(result);
		Mockito.verify(commerceCartService).calculateCart(parameters);
	}

	@Test
	public void testUpdateCartEntryBasePrice_SynchronousPricingInactivce()
	{
		Mockito.when(sapPricingEnablementService.isCartPricingEnabled()).thenReturn(Boolean.FALSE);
		final boolean result = classUnderTest.updateCartEntryBasePrice(cartEntry);
		assertTrue(result);
		Mockito.verify(configurationService).retrieveConfigurationModel(Mockito.any());
	}

	@Test
	public void testUpdateCartEntryBasePrice_SynchronousPricingActive() throws CalculationException
	{
		final boolean result = classUnderTest.updateCartEntryBasePrice(cartEntry);
		assertFalse(result);

	}

	@Test
	public void testIsPricingErrorPresentInCart_SynchronousPricingActive()
	{
		assertFalse(classUnderTest.isCartPricingErrorPresent(null));
	}

	@Test
	public void testIsPricingErrorPresentInCart_SynchronousPricingInactive()
	{
		configModel.setPricingError(true);
		Mockito.when(sapPricingEnablementService.isCartPricingEnabled()).thenReturn(Boolean.FALSE);
		assertTrue(classUnderTest.isCartPricingErrorPresent(configModel));
	}
}
