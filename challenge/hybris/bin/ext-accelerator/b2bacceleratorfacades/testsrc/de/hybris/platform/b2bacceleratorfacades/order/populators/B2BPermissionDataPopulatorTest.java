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
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.enums.B2BPeriodRange;
import de.hybris.platform.b2b.enums.B2BPermissionTypeEnum;
import de.hybris.platform.b2b.model.B2BBudgetExceededPermissionModel;
import de.hybris.platform.b2b.model.B2BOrderThresholdPermissionModel;
import de.hybris.platform.b2b.model.B2BOrderThresholdTimespanPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionTypeData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
@SuppressWarnings("deprecation")
public class B2BPermissionDataPopulatorTest
{
	private static final Double TEST_THRESHOLD = Double.valueOf(10000.0f);
	private static final String TEST_ORDER_THRESHOLD_PERMISSION = "testOrderThresholdPermission";
	private static final String TEST_ORDER_THRESHOLD_TIME_SPAN_PERMISSION = "testOrderThresholdTimeSpanPermission";
	private static final String TEST_BUDGET_EXCEEDED_PERMISSION = "testBudgetExceededPermission";
	private static final String TEST_UNIT = "Test Unit";
	private static final String TEST_UNIT_ID = "testUnitId";

	@Mock
	private Converter<CurrencyModel, CurrencyData> currencyConverter;

	@Mock
	private Converter<B2BPermissionTypeEnum, B2BPermissionTypeData> b2bPermissionTypeConverter;

	@Mock
	private B2BUnitModel b2bUnitModel;

	private final B2BPermissionDataPopulator b2bPermissionDataPopulator = new B2BPermissionDataPopulator();

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		BDDMockito.given(currencyConverter.convert(Mockito.any(CurrencyModel.class))).willReturn(new CurrencyData());
		BDDMockito.given(b2bPermissionTypeConverter.convert(Mockito.any(B2BPermissionTypeEnum.class))).willReturn(
				new B2BPermissionTypeData());

		BDDMockito.given(b2bUnitModel.getUid()).willReturn(TEST_UNIT_ID);
		BDDMockito.given(b2bUnitModel.getLocName()).willReturn(TEST_UNIT);
		BDDMockito.given(b2bUnitModel.getActive()).willReturn(Boolean.TRUE);

		b2bPermissionDataPopulator.setCurrencyConverter(currencyConverter);
		b2bPermissionDataPopulator.setB2BPermissionTypeConverter(b2bPermissionTypeConverter);
	}

	@Test
	public void testPopulateBudgetExceededPermission()
	{
		final B2BPermissionModel source = prepareBudgetExceededPermission();
		final B2BPermissionData target = new B2BPermissionData();

		b2bPermissionDataPopulator.populate(source, target);

		doCommonAssertions(target);
		Assert.assertEquals("Unexpected code", TEST_BUDGET_EXCEEDED_PERMISSION, target.getCode());
		Assert.assertNull("Currency was not null", target.getCurrency());
		Assert.assertNull("PeriodRange was not null", target.getPeriodRange());
		Assert.assertNull("TimeSpan was not null", target.getTimeSpan());
		Assert.assertNull("Value was not null", target.getValue());
	}

	@Test
	public void testPopulateOrderThresholdPermission()
	{
		final B2BPermissionModel source = prepareOrderThresholdPermission();
		final B2BPermissionData target = new B2BPermissionData();

		b2bPermissionDataPopulator.populate(source, target);

		doCommonAssertions(target);
		doCommonOrderThresholdAssertions(target);
		Assert.assertEquals("Unexpected code", TEST_ORDER_THRESHOLD_PERMISSION, target.getCode());
		Assert.assertNull("PeriodRange was not null", target.getPeriodRange());
		Assert.assertNull("TimeSpan was not null", target.getTimeSpan());
	}

	@Test
	public void testPopulateOrderThresholdTimeSpanPermission()
	{
		final B2BPermissionModel source = prepareOrderThresholdTimeSpanPermission();
		final B2BPermissionData target = new B2BPermissionData();

		b2bPermissionDataPopulator.populate(source, target);

		doCommonAssertions(target);
		doCommonOrderThresholdAssertions(target);
		Assert.assertEquals("Unexpected code", TEST_ORDER_THRESHOLD_TIME_SPAN_PERMISSION, target.getCode());
		Assert.assertEquals("Unexpected period range", B2BPeriodRange.YEAR, target.getPeriodRange());
		Assert.assertEquals("Unexpected time span", B2BPeriodRange.YEAR.name(), target.getTimeSpan());
	}

	protected void doCommonOrderThresholdAssertions(final B2BPermissionData target)
	{
		Assert.assertEquals("Unexpected threshold", TEST_THRESHOLD, target.getValue());
		Assert.assertNotNull("Currency was null", target.getCurrency());
	}

	protected void doCommonAssertions(final B2BPermissionData target)
	{
		Assert.assertNotNull("B2BPermissionTypeData was null", target.getB2BPermissionTypeData());
		Assert.assertNotNull("B2BUnitData was null", target.getUnit());
		Assert.assertEquals("Unexpected unit uid", TEST_UNIT_ID, target.getUnit().getUid());
		Assert.assertEquals("Unexpected unit name", TEST_UNIT, target.getUnit().getName());
		Assert.assertEquals("Unit was not active", Boolean.TRUE, Boolean.valueOf(target.getUnit().isActive()));
	}

	protected B2BPermissionModel prepareBudgetExceededPermission()
	{
		final B2BPermissionModel model = new B2BBudgetExceededPermissionModel();
		model.setCode(TEST_BUDGET_EXCEEDED_PERMISSION);
		model.setUnit(b2bUnitModel);
		return model;
	}

	protected B2BOrderThresholdPermissionModel prepareOrderThresholdPermission()
	{
		final B2BOrderThresholdPermissionModel model = new B2BOrderThresholdPermissionModel();
		model.setCode(TEST_ORDER_THRESHOLD_PERMISSION);
		model.setUnit(b2bUnitModel);
		model.setThreshold(TEST_THRESHOLD);
		return model;
	}

	protected B2BOrderThresholdPermissionModel prepareOrderThresholdTimeSpanPermission()
	{
		final B2BOrderThresholdTimespanPermissionModel model = new B2BOrderThresholdTimespanPermissionModel();
		model.setCode(TEST_ORDER_THRESHOLD_TIME_SPAN_PERMISSION);
		model.setUnit(b2bUnitModel);
		model.setThreshold(TEST_THRESHOLD);
		model.setRange(B2BPeriodRange.YEAR);
		return model;
	}
}
