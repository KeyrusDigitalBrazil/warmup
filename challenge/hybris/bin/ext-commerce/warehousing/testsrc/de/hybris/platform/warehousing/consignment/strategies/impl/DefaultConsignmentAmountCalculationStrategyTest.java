/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.consignment.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultConsignmentAmountCalculationStrategyTest
{
	protected static final String CODE1 = "Code1";
	protected static final String CODE2 = "Code2";
	protected static final BigDecimal AMOUNT1 = BigDecimal.TEN;
	protected static final BigDecimal AMOUNT2 = BigDecimal.valueOf(20);
	protected static final BigDecimal DELIVERY_COST = BigDecimal.valueOf(5);
	protected static final BigDecimal DISCOUNTS = BigDecimal.valueOf(0.0);
	protected static final BigDecimal TAX1 = BigDecimal.valueOf(0.11);
	protected static final BigDecimal TAX2 = BigDecimal.valueOf(0.24);
	protected static final BigDecimal DELIVERY_COST_TAX = BigDecimal.valueOf(0.07);
	protected static final BigDecimal TOTAL_TAX = TAX1.add(TAX2).add(DELIVERY_COST_TAX);
	protected static final BigDecimal TOTAL_DISCOUNT = BigDecimal.valueOf(3.57d);
	protected static final BigDecimal PRODUCT_LEVEL_DISCOUNT = BigDecimal.ONE;

	@InjectMocks
	private DefaultConsignmentAmountCalculationStrategy consignmentAmountCalculationStrategy;

	@Mock
	private ConsignmentModel consignment1;
	@Mock
	private ConsignmentEntryModel consignmentEntry1;
	@Mock
	private OrderEntryModel orderEntry1;
	@Mock
	private TaxValue taxValue1;
	@Mock
	private ConsignmentModel consignment2;
	@Mock
	private ConsignmentEntryModel consignmentEntry2;
	@Mock
	private OrderEntryModel orderEntry2;
	@Mock
	private TaxValue taxValue2;
	@Mock
	private PaymentTransactionModel paymentTransaction;
	@Mock
	private OrderModel order;
	@Mock
	private CurrencyModel currency;
	@Mock
	private ProductModel product;
	@Mock
	private PaymentTransactionEntryModel paymentTransactionEntry;
	@Mock
	private DiscountValue discountValue;

	private Set<PaymentTransactionEntryModel> paymentTransactionEntries = new HashSet<>();
	private Set<ConsignmentModel> consignments = new LinkedHashSet<>();
	private List<PaymentTransactionModel> paymentTransactions = new ArrayList<>();
	private List<DiscountValue> discountValues = new ArrayList<>();
	private List<AbstractOrderEntryModel> orderEntryModels = new ArrayList<>();

	@Before
	public void setUp()
	{
		when(consignment1.getOrder()).thenReturn(order);
		when(consignment1.getCode()).thenReturn(CODE1);
		when(consignment1.getStatus()).thenReturn(ConsignmentStatus.READY);
		when(consignment1.getConsignmentEntries()).thenReturn(Sets.newHashSet(consignmentEntry1));
		when(consignmentEntry1.getOrderEntry()).thenReturn(orderEntry1);
		when(consignmentEntry1.getQuantity()).thenReturn(1l);
		when(consignmentEntry1.getConsignment()).thenReturn(consignment1);
		when(orderEntry1.getQuantity()).thenReturn(1l);
		when(orderEntry1.getTaxValues()).thenReturn(Sets.newHashSet(taxValue1));
		when(orderEntry1.getBasePrice()).thenReturn(AMOUNT1.doubleValue());
		final double orderEntry1TotalPrice = AMOUNT1.subtract(PRODUCT_LEVEL_DISCOUNT).doubleValue();
		when(orderEntry1.getTotalPrice()).thenReturn(orderEntry1TotalPrice);
		when(orderEntry1.getDiscountValues()).thenReturn(discountValues);
		when(orderEntry1.getOrder()).thenReturn(order);
		when(taxValue1.getValue()).thenReturn(TAX1.doubleValue());

		when(consignment2.getOrder()).thenReturn(order);
		when(consignment2.getCode()).thenReturn(CODE2);
		when(consignment2.getConsignmentEntries()).thenReturn(Sets.newHashSet(consignmentEntry2));
		when(consignment2.getStatus()).thenReturn(ConsignmentStatus.READY);
		when(consignmentEntry2.getOrderEntry()).thenReturn(orderEntry2);
		when(consignmentEntry2.getQuantity()).thenReturn(1l);
		when(consignmentEntry2.getConsignment()).thenReturn(consignment2);
		when(orderEntry2.getQuantity()).thenReturn(1l);
		when(orderEntry2.getTaxValues()).thenReturn(Sets.newHashSet(taxValue2));
		when(orderEntry2.getBasePrice()).thenReturn(AMOUNT2.doubleValue());
		final double orderEntry2TotalPrice = AMOUNT2.subtract(PRODUCT_LEVEL_DISCOUNT).doubleValue();
		when(orderEntry2.getTotalPrice()).thenReturn(orderEntry2TotalPrice);
		when(orderEntry2.getOrder()).thenReturn(order);
		when(taxValue2.getValue()).thenReturn(TAX2.doubleValue());

		when(order.getConsignments()).thenReturn(consignments);
		when(order.getPaymentTransactions()).thenReturn(paymentTransactions);
		when(order.getDeliveryCost()).thenReturn(DELIVERY_COST.doubleValue());
		when(order.getTotalPrice()).thenReturn(orderEntry1TotalPrice + orderEntry2TotalPrice + DELIVERY_COST.doubleValue());
		when(order.getTotalTax()).thenReturn(TOTAL_TAX.doubleValue());
		when(order.getTotalDiscounts()).thenReturn(DISCOUNTS.doubleValue());
		when(order.getCurrency()).thenReturn(currency);
		when(order.getTotalDiscounts()).thenReturn(TOTAL_DISCOUNT.doubleValue());
		when(order.getEntries()).thenReturn(orderEntryModels);
		when(order.getTotalTaxValues())
				.thenReturn(Collections.singletonList(new TaxValue("", DELIVERY_COST_TAX.doubleValue(), true, "USD")));

		when(discountValue.getAppliedValue()).thenReturn(PRODUCT_LEVEL_DISCOUNT.doubleValue());
		when(currency.getDigits()).thenReturn(2);
		when(consignment1.getPaymentTransactionEntries()).thenReturn(Collections.emptySet());
		when(consignment2.getPaymentTransactionEntries()).thenReturn(Collections.emptySet());

		paymentTransactions.add(paymentTransaction);
		consignments.addAll(Arrays.asList(consignment1, consignment2));
		paymentTransactionEntries.add(paymentTransactionEntry);
		discountValues.add(discountValue);
		orderEntryModels.add(orderEntry1);
		orderEntryModels.add(orderEntry2);
	}

	@After
	public void cleanUp()
	{
		consignments.clear();
	}

	@Test
	public void calculateAmountFirstConsignmentCapturedFirst()
	{
		final BigDecimal discountAmountForConsignment = TOTAL_DISCOUNT.multiply(AMOUNT1.subtract(PRODUCT_LEVEL_DISCOUNT))
				.divide(BigDecimal.valueOf(order.getTotalPrice()).subtract(DELIVERY_COST), currency.getDigits(),
						BigDecimal.ROUND_HALF_UP);
		final BigDecimal expectedAmount = AMOUNT1.subtract(PRODUCT_LEVEL_DISCOUNT).add(TAX1).subtract(discountAmountForConsignment);

		//then verify that amount to be captured = orderEntryTotalPrice + TAX - orderLevelDiscountProportionallyDisctibutedAmongstConsignments
		assertEquals(expectedAmount, consignmentAmountCalculationStrategy.calculateCaptureAmount(consignment1));
	}

	@Test
	public void calculateAmountSecondConsignmentCapturedFirst()
	{
		final BigDecimal discountAmountForConsignment = TOTAL_DISCOUNT.multiply(AMOUNT2.subtract(PRODUCT_LEVEL_DISCOUNT))
				.divide(BigDecimal.valueOf(order.getTotalPrice()).subtract(DELIVERY_COST), currency.getDigits(),
						BigDecimal.ROUND_HALF_UP);
		final BigDecimal expectedAmount = AMOUNT2.subtract(PRODUCT_LEVEL_DISCOUNT).add(TAX2).subtract(discountAmountForConsignment);

		//then verify that amount to be captured = orderEntryTotalPrice + TAX - orderLevelDiscountProportionallyDisctibutedAmongstConsignments
		assertEquals(expectedAmount, consignmentAmountCalculationStrategy.calculateCaptureAmount(consignment2));
	}

	@Test
	public void calculateLeftOverOnLastConsignment()
	{
		final BigDecimal firstConsignmentAmount = consignmentAmountCalculationStrategy.calculateCaptureAmount(consignment1);
		when(consignment1.getPaymentTransactionEntries()).thenReturn(paymentTransactionEntries);
		when(paymentTransactionEntry.getAmount()).thenReturn(firstConsignmentAmount);
		final BigDecimal orderLeftOverWithoutShippingCost = BigDecimal.valueOf(order.getTotalPrice()).add(TOTAL_TAX)
				.subtract(firstConsignmentAmount).subtract(DELIVERY_COST).subtract(DELIVERY_COST_TAX);
		assertEquals(orderLeftOverWithoutShippingCost, consignmentAmountCalculationStrategy.calculateCaptureAmount(consignment2));
	}

	@Test
	public void calculateOrderWithOnlyOneConsignment()
	{
		consignments.clear();
		consignments.add(consignment1);
		when(order.getTotalPrice()).thenReturn(AMOUNT1.subtract(PRODUCT_LEVEL_DISCOUNT).add(DELIVERY_COST).doubleValue());
		when(order.getTotalTax()).thenReturn(TAX1.add(DELIVERY_COST_TAX).doubleValue());
		final BigDecimal orderTotalWithoutShippingCost = BigDecimal.valueOf(order.getTotalPrice())
				.add(BigDecimal.valueOf(order.getTotalTax())).subtract(DELIVERY_COST).subtract(DELIVERY_COST_TAX);
		assertEquals(orderTotalWithoutShippingCost, consignmentAmountCalculationStrategy.calculateCaptureAmount(consignment1));
	}

	@Test(expected = IllegalStateException.class)
	public void calculateAmountGreaterThanAlreadyCaptured()
	{
		when(consignment2.getPaymentTransactionEntries()).thenReturn(paymentTransactionEntries);
		when(paymentTransactionEntry.getAmount()).thenReturn(AMOUNT1.add(AMOUNT2).add(TOTAL_TAX).add(AMOUNT2));
		consignmentAmountCalculationStrategy.calculateCaptureAmount(consignment1);
	}

	@Test(expected = IllegalStateException.class)
	public void calculateAmountForProductWithNoTaxValue()
	{
		when(orderEntry1.getTaxValues()).thenReturn(Collections.emptyList());
		when(orderEntry1.getProduct()).thenReturn(product);
		when(product.getCode()).thenReturn("123");
		consignmentAmountCalculationStrategy.calculateCaptureAmount(consignment1);
	}

	@Test
	public void shouldCalculateDiscountForIndividualConsignments()
	{
		calculateAndAssertDiscountAmountForConsignment(consignment1);
		calculateAndAssertDiscountAmountForConsignment(consignment2);
	}

	@Test
	public void shouldCalculateDiscountForTheLastConsignment()
	{
		//given consignment1 is packed
		when(consignment1.getPaymentTransactionEntries()).thenReturn(paymentTransactionEntries);

		final BigDecimal firstConsignmentAmount = consignment1.getConsignmentEntries().stream()
				.map(entry -> BigDecimal.valueOf(entry.getOrderEntry().getTotalPrice())
						.multiply(BigDecimal.valueOf(entry.getQuantity()))
						.divide(BigDecimal.valueOf(entry.getOrderEntry().getQuantity()), currency.getDigits(),
								BigDecimal.ROUND_HALF_UP)).reduce(BigDecimal::add).get();
		final BigDecimal firstConsignmentDiscount = firstConsignmentAmount.multiply(BigDecimal.valueOf(order.getTotalDiscounts()))
				.divide(BigDecimal.valueOf(order.getTotalPrice()).subtract(DELIVERY_COST), currency.getDigits(),
						RoundingMode.HALF_UP);

		assertEquals(TOTAL_DISCOUNT.subtract(firstConsignmentDiscount),
				consignmentAmountCalculationStrategy.calculateDiscountAmount(consignment2));
	}

	@Test
	public void shouldCalculateConsignmentEntryWithTax()
	{
		assertEquals(AMOUNT1.add(TAX1).subtract(PRODUCT_LEVEL_DISCOUNT),
				consignmentAmountCalculationStrategy.calculateConsignmentEntryAmount(consignmentEntry1, true));
	}

	@Test
	public void shouldCalculateConsignmentEntryWithoutTax()
	{
		assertEquals(AMOUNT1.subtract(PRODUCT_LEVEL_DISCOUNT).setScale(currency.getDigits()),
				consignmentAmountCalculationStrategy.calculateConsignmentEntryAmount(consignmentEntry1, false));
	}

	@Test
	public void shouldReturnZeroIfNoQtyLeftInCalculateConsignmentEntryAmount()
	{
		//	given
		when(orderEntry1.getQuantity()).thenReturn(0L);

		// when
		BigDecimal amount = consignmentAmountCalculationStrategy.calculateConsignmentEntryAmount(consignmentEntry1, true);

		// then
		assertEquals(BigDecimal.ZERO, amount);
	}

	/**
	 * Calculates discount amount for a given {@link ConsignmentModel} then asserts the calculated amount
	 *
	 * @param consignmentModel
	 * 		{@link ConsignmentModel} to calculate and assert the discount
	 */
	protected void calculateAndAssertDiscountAmountForConsignment(final ConsignmentModel consignmentModel)
	{
		final BigDecimal consignmentAmount = consignmentModel.getConsignmentEntries().stream()
				.map(entry -> BigDecimal.valueOf(entry.getOrderEntry().getTotalPrice())
						.multiply(BigDecimal.valueOf(entry.getQuantity()))
						.divide(BigDecimal.valueOf(entry.getOrderEntry().getQuantity()), currency.getDigits(),
								BigDecimal.ROUND_HALF_UP)).reduce(BigDecimal::add).get();
		final BigDecimal expectedResult = consignmentAmount.multiply(BigDecimal.valueOf(order.getTotalDiscounts()))
				.divide(BigDecimal.valueOf(order.getTotalPrice()).subtract(DELIVERY_COST), currency.getDigits(),
						RoundingMode.HALF_UP);
		assertEquals(expectedResult, consignmentAmountCalculationStrategy.calculateDiscountAmount(consignmentModel));
	}
}
