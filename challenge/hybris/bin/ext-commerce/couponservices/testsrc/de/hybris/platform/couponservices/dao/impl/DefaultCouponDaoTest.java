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
package de.hybris.platform.couponservices.dao.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapperException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

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
public class DefaultCouponDaoTest
{
	private static final String ANY_STRING = "anyString";

	@Rule
	public final ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@InjectMocks
	private final DefaultCouponDao dao = new DefaultCouponDao();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void nullTestGetCouponById() throws RuleParameterValueMapperException
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		dao.findCouponById(null);
	}

	@Test
	public void noCouponFoundTest() throws RuleParameterValueMapperException
	{
		BDDMockito.given(flexibleSearchService.searchUnique(Mockito.anyObject())).willThrow(ModelNotFoundException.class);

		//expect
		expectedException.expect(ModelNotFoundException.class);

		//when
		dao.findCouponById(ANY_STRING);
	}

	@Test
	public void couponFoundTest() throws RuleParameterValueMapperException
	{
		//given
		final AbstractCouponModel coupon = Mockito.mock(AbstractCouponModel.class);

		BDDMockito.given(flexibleSearchService.searchUnique(Mockito.anyObject())).willReturn(coupon);

		//when
		final AbstractCouponModel couponAsResult = dao.findCouponById(ANY_STRING);

		//then
		Assert.assertEquals(coupon, couponAsResult);
	}
}
