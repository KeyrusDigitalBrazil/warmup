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
package de.hybris.platform.commerceservices.order.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for DefaultQuoteCartValidationStrategy
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultQuoteCartValidationStrategyTest
{
	@InjectMocks
	private DefaultQuoteCartValidationStrategy defaultQuoteCartValidationStrategy;

	private AbstractOrderModel source;
	private AbstractOrderModel target;
	private List<AbstractOrderEntryModel> sourceEntries;
	private List<AbstractOrderEntryModel> targetEntries;
	private AbstractOrderEntryModel sourceEntry;
	private AbstractOrderEntryModel targetEntry;
	private ProductModel sourceProduct;
	private ProductModel targetProduct;

	@Before
	public void setup()
	{
		source = new AbstractOrderModel();
		target = new AbstractOrderModel();

		sourceEntries = new ArrayList<AbstractOrderEntryModel>();
		targetEntries = new ArrayList<AbstractOrderEntryModel>();

		sourceEntry = new AbstractOrderEntryModel();
		targetEntry = new AbstractOrderEntryModel();

		sourceProduct = new ProductModel();
		targetProduct = new ProductModel();
	}


	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionForNullFromAbstractOrderModel()
	{
		defaultQuoteCartValidationStrategy.validate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionForNullToAbstractOrderModel()
	{
		defaultQuoteCartValidationStrategy.validate(source, null);
	}

	@Test
	public void shouldReturnTrueForValidAbstractOrders()
	{
		final Double subTotal = new Double(100);
		source.setSubtotal(subTotal);
		target.setSubtotal(subTotal);

		final Double totalDiscounts = new Double(10);
		source.setTotalDiscounts(totalDiscounts);
		target.setTotalDiscounts(totalDiscounts);

		final Integer entryNumber = new Integer(1);
		sourceEntry.setEntryNumber(entryNumber);
		targetEntry.setEntryNumber(entryNumber);

		sourceProduct.setCode("testCode");
		targetProduct.setCode("testCode");

		sourceEntry.setProduct(sourceProduct);
		targetEntry.setProduct(targetProduct);

		final Long qty = new Long(2);
		sourceEntry.setQuantity(qty);
		targetEntry.setQuantity(qty);

		final Double totalPrice = new Double(99);
		sourceEntry.setTotalPrice(totalPrice);
		targetEntry.setTotalPrice(totalPrice);

		sourceEntries.add(sourceEntry);
		targetEntries.add(targetEntry);

		source.setEntries(sourceEntries);
		target.setEntries(targetEntries);

		Assert.assertTrue("the two abstract orders should be unmodified",
				defaultQuoteCartValidationStrategy.validate(source, target));
	}

	@Test
	public void shouldReturnFalseForValidAbstractOrders()
	{
		final Double subTotal = new Double(100);
		source.setSubtotal(subTotal);
		Assert.assertFalse("the two abstract orders should have subtotal",
				defaultQuoteCartValidationStrategy.validate(source, target));
		target.setSubtotal(subTotal);

		final Double totalDiscounts = new Double(10);
		source.setTotalDiscounts(totalDiscounts);
		Assert.assertFalse("the two abstract orders should have different total discounts",
				defaultQuoteCartValidationStrategy.validate(source, target));
		target.setTotalDiscounts(totalDiscounts);

		sourceEntries.add(sourceEntry);
		targetEntries.add(targetEntry);

		source.setEntries(sourceEntries);
		target.setEntries(targetEntries);

		final Integer entryNumber = new Integer(1);
		sourceEntry.setEntryNumber(entryNumber);
		Assert.assertFalse("the two abstract order entries should have different entry numbers",
				defaultQuoteCartValidationStrategy.validate(source, target));
		targetEntry.setEntryNumber(entryNumber);

		sourceProduct.setCode("testCode");

		sourceEntry.setProduct(sourceProduct);
		targetEntry.setProduct(targetProduct);
		Assert.assertFalse("the two abstract order entries should have different products",
				defaultQuoteCartValidationStrategy.validate(source, target));
		targetProduct.setCode("testCode");

		final Long qty = new Long(2);
		sourceEntry.setQuantity(qty);
		Assert.assertFalse("the two abstract order entries should have different quantites",
				defaultQuoteCartValidationStrategy.validate(source, target));
		targetEntry.setQuantity(qty);

		final Double totalPrice = new Double(99);
		sourceEntry.setTotalPrice(totalPrice);
		Assert.assertFalse("the two abstract order entries should have different total price",
				defaultQuoteCartValidationStrategy.validate(source, target));
		targetEntry.setTotalPrice(totalPrice);

	}
}
