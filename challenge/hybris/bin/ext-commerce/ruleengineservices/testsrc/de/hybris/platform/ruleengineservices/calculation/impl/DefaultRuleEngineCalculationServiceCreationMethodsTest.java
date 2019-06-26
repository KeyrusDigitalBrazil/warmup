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
package de.hybris.platform.ruleengineservices.calculation.impl;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.order.calculation.domain.AbstractDiscount;
import de.hybris.order.calculation.domain.LineItem;
import de.hybris.order.calculation.domain.LineItemDiscount;
import de.hybris.order.calculation.domain.MultiLineItemDiscount;
import de.hybris.order.calculation.domain.Order;
import de.hybris.order.calculation.domain.OrderCharge;
import de.hybris.order.calculation.domain.OrderDiscount;
import de.hybris.order.calculation.money.AbstractAmount;
import de.hybris.order.calculation.money.Currency;
import de.hybris.order.calculation.money.Money;
import de.hybris.order.calculation.money.Percentage;
import de.hybris.platform.ruleengineservices.calculation.MinimumAmountValidationStrategy;
import de.hybris.platform.ruleengineservices.rao.DeliveryModeRAO;
import de.hybris.platform.ruleengineservices.rao.DiscountRAO;
import de.hybris.platform.ruleengineservices.rao.ShipmentRAO;
import de.hybris.platform.ruleengineservices.util.CurrencyUtils;
import de.hybris.platform.ruleengineservices.util.OrderUtils;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleEngineCalculationServiceCreationMethodsTest
{
	@Mock
	private CommonI18NService commonI18NService;
	@InjectMocks
	private DefaultRuleEngineCalculationService service;

	private final Currency currency = new Currency("GBP", 2);

	private static final boolean ABSOLUTE = true;
	private static final boolean RELATIVE = !ABSOLUTE;

	private static final boolean PER_UNIT = true;
	private static final boolean NOT_PER_UNIT = !PER_UNIT;

	// stubbed instance of MinimumAmountValidationStrategy
	private final MinimumAmountValidationStrategy minimumAmountValidationStrategyLowerLimitInvalid = new MinimumAmountValidationStrategy()
	{

		@Override
		public boolean isOrderLowerLimitValid(final Order cart, final OrderDiscount discount)
		{
			return false;
		}

		@Override
		public boolean isLineItemLowerLimitValid(final LineItem lineItem, final LineItemDiscount discount)
		{
			return false;
		}
	};

	// stubbed instance of MinimumAmountValidationStrategy
	private final MinimumAmountValidationStrategy minimumAmountValidationStrategyLowerLimitValid = new MinimumAmountValidationStrategy()
	{

		@Override
		public boolean isOrderLowerLimitValid(final Order cart, final OrderDiscount discount)
		{
			return true;
		}

		@Override
		public boolean isLineItemLowerLimitValid(final LineItem lineItem, final LineItemDiscount discount)
		{
			return true;
		}
	};

	@Before
	public void setUp() throws Exception
	{
		final OrderUtils orderUtils = new OrderUtils();
		final CurrencyUtils currencyUtils = new CurrencyUtils();
		currencyUtils.setCommonI18NService(commonI18NService);

		service = new DefaultRuleEngineCalculationService();
		service.setOrderUtils(orderUtils);
		service.setCurrencyUtils(currencyUtils);
	}

	@Test
	public void testCreateOrderDiscountAbsoluteOrderLowerLimitInvalid()
	{
		final BigDecimal amount = BigDecimal.valueOf(1000, currency.getDigits());//10.00
		final Order cart = new Order(currency, null);
		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitInvalid);
		final OrderDiscount createOrderDiscount = service.createOrderDiscount(cart, ABSOLUTE, amount);

		final Money discountAmount = (Money) createOrderDiscount.getAmount();

		assertThat(discountAmount.getCurrency(), is(currency));
		assertThat(discountAmount.getAmount(), equalTo(BigDecimal.ZERO.setScale(currency.getDigits())));
	}

	@Test
	public void testCreateOrderDiscountRelativeOrderLowerLimitInvalid()
	{
		final BigDecimal amount = BigDecimal.valueOf(1000, currency.getDigits());//10.00
		final Order cart = new Order(currency, null);
		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitInvalid);
		final OrderDiscount createOrderDiscount = service.createOrderDiscount(cart, RELATIVE, amount);

		final Money discountAmount = (Money) createOrderDiscount.getAmount();

		assertThat(discountAmount.getAmount(), comparesEqualTo(BigDecimal.ZERO));
	}

	@Test
	public void testCreateOrderDiscountAbsoluteOrderLowerLimitValid()
	{
		final BigDecimal amount = BigDecimal.valueOf(1000, currency.getDigits());//10.00
		final Order cart = new Order(currency, null);
		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitValid);
		final OrderDiscount createOrderDiscount = service.createOrderDiscount(cart, ABSOLUTE, amount);

		final Money discountAmount = (Money) createOrderDiscount.getAmount();

		assertThat(discountAmount.getCurrency(), is(currency));
		assertThat(discountAmount.getAmount(), equalTo(amount));
	}

	@Test
	public void testCreateOrderDiscountRelativeOrderLowerLimitValid()
	{
		final BigDecimal amount = BigDecimal.valueOf(1000, currency.getDigits());//10.00
		final Order cart = new Order(currency, null);
		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitValid);
		final OrderDiscount orderDiscount = service.createOrderDiscount(cart, RELATIVE, amount);

		final Money convertedToAbsoluteDiscountAmount = (Money) orderDiscount.getAmount();

		assertThat(convertedToAbsoluteDiscountAmount.getAmount(), comparesEqualTo(BigDecimal.ZERO));
	}


	@Test
	public void testCreateDiscountRAO()
	{
		final LineItemDiscount lineItemDiscount = new LineItemDiscount(mock(AbstractAmount.class));
		try
		{
			service.createDiscountRAO(lineItemDiscount);
		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("OrderDiscount must have Money or Percentage amount set."));
		}

		assertCreateDiscountRAO(new LineItemDiscount(new Money(BigDecimal.valueOf(1000, currency.getDigits()), currency)));
		assertCreateDiscountRAO(new OrderDiscount(new Money(BigDecimal.valueOf(1000, currency.getDigits()), currency)));
		assertCreateDiscountRAO(new MultiLineItemDiscount(new Money(BigDecimal.valueOf(1000, currency.getDigits()), currency)));

	}

	protected void assertCreateDiscountRAO(final AbstractDiscount discount)
	{
		final DiscountRAO createDiscountRAO = service.createDiscountRAO(discount);
		if (discount.getAmount() instanceof Money)
		{
			final Money amount = (Money) discount.getAmount();
			assertThat(createDiscountRAO.getValue(), is(amount.getAmount()));
			assertThat(createDiscountRAO.getCurrencyIsoCode(), is(amount.getCurrency().getIsoCode()));
		}
		else
		{
			fail("Unrecognised discount amount");
		}
	}

	@Test
	public void testCreateLineItemDiscountLineItemBooleanBigDecimalPerUnitApplicableUnitsAbsolute()
	{
		final BigDecimal discountAmount = BigDecimal.valueOf(100, currency.getDigits());
		final BigDecimal lineItemAmount = BigDecimal.valueOf(300, currency.getDigits());
		final LineItem lineItem = new LineItem(new Money(lineItemAmount, currency));
		final int applicableUnits = 2;
		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitValid);

		final LineItemDiscount createLineItemDiscount = service.createLineItemDiscount(lineItem, ABSOLUTE, discountAmount, PER_UNIT,
				applicableUnits);


		assertCreateLineItemDiscountAbsolute(lineItem, createLineItemDiscount, PER_UNIT, currency,
				BigDecimal.valueOf(50, currency.getDigits()));

	}

	@Test
	public void testCreateLineItemDiscountLineItemBooleanBigDecimalNotPerUnitApplicableUnitsAbsolute()
	{
		final BigDecimal discountAmt = BigDecimal.valueOf(100, currency.getDigits());
		final BigDecimal lineItemAmt = BigDecimal.valueOf(300, currency.getDigits());
		final LineItem lineItem = new LineItem(new Money(lineItemAmt, currency));
		final int applicableUnits = 2;
		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitValid);

		final LineItemDiscount discount = service.createLineItemDiscount(lineItem, ABSOLUTE, discountAmt, NOT_PER_UNIT,
				applicableUnits);

		assertCreateLineItemDiscountAbsolute(lineItem, discount, NOT_PER_UNIT, currency,
				BigDecimal.valueOf(50, currency.getDigits()));

	}

	@Test
	public void testCreateLineItemDiscountLineItemBooleanBigDecimalPerUnitApplicableUnitsRelative()
	{
		final BigDecimal lineItemAmt = BigDecimal.valueOf(300, currency.getDigits());
		final LineItem lineItem = new LineItem(new Money(lineItemAmt, currency));
		final BigDecimal discountAmt = BigDecimal.valueOf(5000, currency.getDigits());
		final int applicableUnits = 3;
		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitValid);
		final LineItemDiscount discount = service.createLineItemDiscount(lineItem, RELATIVE, discountAmt, PER_UNIT,
				applicableUnits);


		assertCreateLineItemDiscountAbsolute(lineItem, discount, PER_UNIT, currency, discountAmt);
	}

	@Test
	public void testCreateLineItemDiscountLineItemBooleanBigDecimalNotPerUnitApplicableUnitsRelative()
	{
		final BigDecimal lineItemAmt = BigDecimal.valueOf(300, currency.getDigits());
		final LineItem lineItem = new LineItem(new Money(lineItemAmt, currency));
		final BigDecimal discountAmt = BigDecimal.valueOf(5000, currency.getDigits());
		final int applicableUnits = 3;
		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitValid);
		final LineItemDiscount discount = service.createLineItemDiscount(lineItem, RELATIVE, discountAmt, NOT_PER_UNIT,
				applicableUnits);

		assertCreateLineItemDiscountAbsolute(lineItem, discount, NOT_PER_UNIT, currency, discountAmt);

	}

	@Test
	public void testCreateLineItemDiscountLineItemBooleanBigDecimalBooleanAbsoluteScenerio1()
	{
		final BigDecimal lineValue = BigDecimal.valueOf(1000, currency.getDigits());
		final LineItem lineItem = new LineItem(new Money(lineValue, currency));
		final BigDecimal discountValue = BigDecimal.valueOf(100, currency.getDigits());

		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitValid);

		final LineItemDiscount createLineItemDiscount = service.createLineItemDiscount(lineItem, ABSOLUTE, discountValue, PER_UNIT);
		assertCreateLineItemDiscountAbsolute(lineItem, createLineItemDiscount, PER_UNIT, currency, BigDecimal.valueOf(100, 2));
	}

	@Test
	public void testCreateLineItemDiscountLineItemBooleanBigDecimalBooleanAbsoluteScenerio2()
	{
		final BigDecimal lineValue = BigDecimal.valueOf(1000, currency.getDigits());
		final LineItem lineItem = new LineItem(new Money(lineValue, currency));
		final BigDecimal discountValue = BigDecimal.valueOf(100, currency.getDigits());

		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitValid);

		final LineItemDiscount createLineItemDiscount = service.createLineItemDiscount(lineItem, ABSOLUTE, discountValue,
				NOT_PER_UNIT);
		assertCreateLineItemDiscountAbsolute(lineItem, createLineItemDiscount, NOT_PER_UNIT, currency, BigDecimal.valueOf(100, 2));

	}

	@Test
	public void testCreateLineItemDiscountLineItemBooleanBigDecimalBooleanAbsoluteScenerio3()
	{
		final BigDecimal lineValue = BigDecimal.valueOf(1000, currency.getDigits());
		final LineItem lineItem = new LineItem(new Money(lineValue, currency));

		final BigDecimal discountValue = BigDecimal.valueOf(100, currency.getDigits());

		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitInvalid);
		final LineItemDiscount createLineItemDiscount = service.createLineItemDiscount(lineItem, ABSOLUTE, discountValue, PER_UNIT);
		assertCreateLineItemDiscountAbsolute(lineItem, createLineItemDiscount, NOT_PER_UNIT, currency,
				BigDecimal.ZERO.setScale(currency.getDigits()));
	}

	@Test
	public void testCreateLineItemDiscountLineItemBooleanBigDecimalBooleanAbsoluteScenerio4()
	{

		final BigDecimal lineValue = BigDecimal.valueOf(1000, currency.getDigits());
		final LineItem lineItem = new LineItem(new Money(lineValue, currency));
		final BigDecimal discountValue = BigDecimal.valueOf(100, currency.getDigits());

		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitInvalid);

		final LineItemDiscount createLineItemDiscount = service.createLineItemDiscount(lineItem, ABSOLUTE, discountValue,
				NOT_PER_UNIT);
		assertCreateLineItemDiscountAbsolute(lineItem, createLineItemDiscount, NOT_PER_UNIT, currency,
				BigDecimal.ZERO.setScale(currency.getDigits()));
	}

	protected void assertCreateLineItemDiscountAbsolute(final LineItem lineItem, final LineItemDiscount createLineItemDiscount,
			final boolean perUnit, final Currency currency, final BigDecimal valueOf)
	{
		assertEquals(createLineItemDiscount.isPerUnit(), perUnit);
		final AbstractAmount amount = createLineItemDiscount.getAmount();
		assertThat(lineItem.getDiscounts().get(0), is(createLineItemDiscount));

		if (amount instanceof Money)
		{
			final Money money = (Money) amount;
			assertThat(money.getCurrency(), is(currency));
			assertThat(money.getAmount(), is(valueOf));
		}
		if (amount instanceof Percentage)
		{
			final Percentage money = (Percentage) amount;
			assertThat(money.getRate(), is(valueOf));

		}

	}

	protected void assertEquals(final boolean expected, final boolean actual)
	{
		final Boolean isExpected = Boolean.valueOf(expected);
		final Boolean isActual = Boolean.valueOf(actual);
		Assert.assertEquals(isExpected, isActual);
	}

	@Test
	public void testCreateLineItemDiscountLineItemBooleanBigDecimalBooleanRelative()
	{
		final BigDecimal lineValue = BigDecimal.valueOf(1000, currency.getDigits());
		final LineItem lineItem = new LineItem(new Money(lineValue, currency));
		final BigDecimal discountValue = BigDecimal.valueOf(100, currency.getDigits());

		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitValid);

		final LineItemDiscount createLineItemDiscount = service.createLineItemDiscount(lineItem, RELATIVE, discountValue, PER_UNIT);
		assertCreateLineItemDiscount(createLineItemDiscount, BigDecimal.valueOf(100, currency.getDigits()), PER_UNIT);

		final LineItemDiscount createLineItemDiscount2 = service.createLineItemDiscount(lineItem, RELATIVE, discountValue,
				NOT_PER_UNIT);
		assertCreateLineItemDiscount(createLineItemDiscount2, BigDecimal.valueOf(100, currency.getDigits()), NOT_PER_UNIT);

		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitInvalid);

		final LineItemDiscount createLineItemDiscount3 = service.createLineItemDiscount(lineItem, RELATIVE, discountValue,
				PER_UNIT);
		assertCreateLineItemDiscount(createLineItemDiscount3, BigDecimal.ZERO, NOT_PER_UNIT);

		final LineItemDiscount createLineItemDiscount4 = service.createLineItemDiscount(lineItem, RELATIVE, discountValue,
				NOT_PER_UNIT);
		assertCreateLineItemDiscount(createLineItemDiscount4, BigDecimal.ZERO, NOT_PER_UNIT);
	}

	protected void assertCreateLineItemDiscount(final LineItemDiscount createLineItemDiscount, final BigDecimal valueOf,
			final boolean perUnit)
	{
		final Percentage money = (Percentage) createLineItemDiscount.getAmount();
		assertThat(money.getRate(), is(valueOf));

		assertEquals(createLineItemDiscount.isPerUnit(), perUnit);
	}


	@Test
	public void testCreateLineItemDiscountLineItemBooleanBigDecimalAbsolute()
	{
		final BigDecimal lineValue = BigDecimal.valueOf(1000, currency.getDigits());
		final LineItem lineItem = new LineItem(new Money(lineValue, currency));
		final BigDecimal discountValue = BigDecimal.valueOf(100, currency.getDigits());

		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitValid);
		final LineItemDiscount createLineItemDiscount = service.createLineItemDiscount(lineItem, ABSOLUTE, discountValue);
		final Money money = (Money) createLineItemDiscount.getAmount();
		assertThat(money.getCurrency(), is(currency));
		assertThat(money.getAmount(), is(BigDecimal.valueOf(100, currency.getDigits())));

		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitInvalid);
		final LineItemDiscount createLineItemDiscount2 = service.createLineItemDiscount(lineItem, ABSOLUTE, discountValue);
		final Money money2 = (Money) createLineItemDiscount2.getAmount();
		assertThat(money2.getCurrency(), is(currency));
		assertThat(money2.getAmount(), is(BigDecimal.ZERO.setScale(currency.getDigits())));

	}

	@Test
	public void testCreateLineItemDiscountLineItemBooleanBigDecimalRelative()
	{
		final BigDecimal lineValue = BigDecimal.valueOf(1000, currency.getDigits());
		final LineItem lineItem = new LineItem(new Money(lineValue, currency));
		final BigDecimal discountValue = BigDecimal.valueOf(100, currency.getDigits());

		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitValid);
		final LineItemDiscount createLineItemDiscount = service.createLineItemDiscount(lineItem, RELATIVE, discountValue);
		final Percentage percentage = (Percentage) createLineItemDiscount.getAmount();
		assertThat(percentage.getRate(), is(BigDecimal.valueOf(100, currency.getDigits())));

		service.setMinimumAmountValidationStrategy(minimumAmountValidationStrategyLowerLimitInvalid);
		final LineItemDiscount createLineItemDiscount2 = service.createLineItemDiscount(lineItem, RELATIVE, discountValue);
		final Percentage percentage2 = (Percentage) createLineItemDiscount2.getAmount();
		assertThat(percentage2.getRate(), is(BigDecimal.ZERO));

	}

	@Test
	public void testCreateShippingCharge()
	{
		final Order spyOnCart = spy(new Order(currency, null));
		final BigDecimal value = BigDecimal.valueOf(1000, currency.getDigits());

		final OrderCharge createShippingCharge = service.createShippingCharge(spyOnCart, ABSOLUTE, value);
		verify(spyOnCart).addCharge(createShippingCharge);
		final Money money = (Money) createShippingCharge.getAmount();
		assertThat(money.getCurrency(), is(currency));
		assertThat(money.getAmount(), is(value));

		final OrderCharge createShippingCharge2 = service.createShippingCharge(spyOnCart, RELATIVE, value);
		verify(spyOnCart).addCharge(createShippingCharge2);
		final Percentage percent = (Percentage) createShippingCharge2.getAmount();
		assertThat(percent.getRate(), is(value));
	}

	@Test
	public void testCreateShipmentRAO()
	{
		try
		{
			service.createShipmentRAO(null);
		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("mode must not be null."));
		}

		final DeliveryModeRAO mode = new DeliveryModeRAO();
		final ShipmentRAO createShipmentRAO = service.createShipmentRAO(mode);
		assertThat(createShipmentRAO.getMode(), is(equalTo(mode)));
	}

}
