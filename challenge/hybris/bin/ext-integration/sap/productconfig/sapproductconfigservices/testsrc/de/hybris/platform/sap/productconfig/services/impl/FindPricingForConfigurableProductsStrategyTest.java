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
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.PriceValue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class FindPricingForConfigurableProductsStrategyTest
{
	final private Double BASE_PRICE = new Double("111.11");

	private FindPricingForConfigurableProductsStrategy classUnderTest = new FindPricingForConfigurableProductsStrategy();

	@Mock
	private CPQConfigurableChecker cpqConfigurableChecker;

	@Mock
	private ModelService modelService;

	@Mock
	private PriceFactory priceFactory;

	@Mock
	private AbstractOrderEntry entryItem;

	@Mock
	private ProductModel product;

	@Mock
	private AbstractOrderModel order;

	private final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();

	private final CurrencyModel currencyModel = new CurrencyModel();

	@Before
	public void setup() throws JaloPriceFactoryException
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setModelService(modelService);
		classUnderTest.setCpqConfigurableChecker(cpqConfigurableChecker);
		entry.setProduct(product);
		entry.setOrder(order);
		entry.setBasePrice(BASE_PRICE);
		currencyModel.setIsocode("EUR");
		given(order.getNet()).willReturn(Boolean.TRUE);
		given(order.getCurrency()).willReturn(currencyModel);
		given(product.getCode()).willReturn("PRODUCT_CODE");
	}

	@Test
	public void testFindBasePriceNeitherKMATNorChangeableVariant() throws CalculationException, Exception
	{
		final PriceValue priceValue = new PriceValue("EUR", 1.0, true);
		classUnderTest = spy(classUnderTest);
		willReturn(priceFactory).given(classUnderTest).getCurrentPriceFactory();
		given(modelService.getSource(entry)).willReturn(entryItem);
		given(priceFactory.getBasePrice(entryItem)).willReturn(priceValue);
		final PriceValue returnedPriceValue = classUnderTest.findBasePrice(entry);
		assertEquals(priceValue, returnedPriceValue);
	}

	@Test
	public void testFindBasePriceKMATorChangeableVariant() throws CalculationException, Exception
	{
		given(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(product)).willReturn(Boolean.TRUE);
		final PriceValue returnedPriceValue = classUnderTest.findBasePrice(entry);
		assertEquals(BASE_PRICE.doubleValue(), returnedPriceValue.getValue(), 0.01);
	}

}

