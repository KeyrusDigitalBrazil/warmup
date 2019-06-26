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
package de.hybris.platform.couponservices.rule.strategies.impl.mappers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.dao.impl.DefaultCouponDao;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapperException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CouponRuleParameterValueMapperTest
{
	private static final String ANY_STRING = "anyString";

	@Rule
	public final ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Mock
	private DefaultCouponDao couponDao;

	@Mock
	private AbstractCouponModel coupon;

	@InjectMocks
	private final CouponRuleParameterValueMapper mapper = new CouponRuleParameterValueMapper();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void nullTestFromString() throws RuleParameterValueMapperException
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		mapper.fromString(null);
	}

	@Test
	public void nullTestToString() throws RuleParameterValueMapperException
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		mapper.toString(null);
	}

	@Test
	public void noCouponFoundTest() throws RuleParameterValueMapperException
	{
		//given
		BDDMockito.given(couponDao.findCouponById(Mockito.anyString())).willThrow(ModelNotFoundException.class);

		//expect
		expectedException.expect(RuleParameterValueMapperException.class);

		//when
		mapper.fromString(ANY_STRING);
	}

	@Test
	public void mappedProductIsFirstFoundTest() throws RuleParameterValueMapperException
	{
		//given
		final AbstractCouponModel coupon = Mockito.mock(AbstractCouponModel.class);

		BDDMockito.given(couponDao.findCouponById(Mockito.anyString())).willReturn(coupon);

		//when
		final AbstractCouponModel mappedCoupon = mapper.fromString(ANY_STRING);

		//then
		Assert.assertEquals(coupon, mappedCoupon);
	}

	@Test
	public void objectToStringTest() throws RuleParameterValueMapperException
	{
		//given
		BDDMockito.given(coupon.getCouponId()).willReturn(ANY_STRING);

		//when
		final String stringRepresentation = mapper.toString(coupon);

		//then
		Assert.assertEquals(ANY_STRING, stringRepresentation);
	}
}
