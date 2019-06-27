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

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.couponservices.model.CouponRedemptionModel;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearchException;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapperException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * JUnit test suite for {@link DefaultCouponRedemptionDao}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCouponRedemptionDaoUnitTest
{
	private static final String COUPON_CODE = "SUMMER69";


	@Rule
	public final ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@InjectMocks
	private final DefaultCouponRedemptionDao redemptionDao = new DefaultCouponRedemptionDao();



	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

	}

	@Test
	public void nullTestGetCouponRedemptionByCouponCode() throws RuleParameterValueMapperException
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		redemptionDao.findCouponRedemptionsByCode(null);
	}

	@Test(expected = FlexibleSearchException.class)
	public void testGetCouponRedemptionByCouponCodeNotFound()
	{
		when(flexibleSearchService.<AbstractRuleEngineRuleModel> search(any(FlexibleSearchQuery.class)))
				.thenThrow(FlexibleSearchException.class);

		redemptionDao.findCouponRedemptionsByCode(COUPON_CODE);
	}


	@Test
	public void testCouponRedemptionFound() throws RuleParameterValueMapperException
	{
		final SearchResult<CouponRedemptionModel> searchResult = mock(SearchResult.class);
		final List<CouponRedemptionModel> couponRedemptionList = asList(Mockito.mock(CouponRedemptionModel.class));

		when(flexibleSearchService.<CouponRedemptionModel> search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);
		when(searchResult.getResult()).thenReturn(couponRedemptionList);

		final List<CouponRedemptionModel> couponRedemptionAsResult = redemptionDao.findCouponRedemptionsByCode(COUPON_CODE);

		Assert.assertEquals(couponRedemptionList, couponRedemptionAsResult);

	}

	@Test
	public void testCouponRedemptionWhenOrderAsParameter() throws RuleParameterValueMapperException
	{
		final AbstractOrderModel abstractOrder = mock(AbstractOrderModel.class);
		final SearchResult<CouponRedemptionModel> searchResult = mock(SearchResult.class);
		final List<CouponRedemptionModel> couponRedemptionList = asList(Mockito.mock(CouponRedemptionModel.class));


		when(flexibleSearchService.<CouponRedemptionModel> search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);
		when(searchResult.getResult()).thenReturn(couponRedemptionList);

		final List<CouponRedemptionModel> couponRedemptionAsResult = redemptionDao.findCouponRedemptionsByCodeAndOrder(COUPON_CODE,
				abstractOrder);

		Assert.assertEquals(couponRedemptionList, couponRedemptionAsResult);

	}

	@Test
	public void testCouponRedemptionWhenUserAsParameter() throws RuleParameterValueMapperException
	{
		final UserModel user = mock(UserModel.class);
		final SearchResult<CouponRedemptionModel> searchResult = mock(SearchResult.class);
		final List<CouponRedemptionModel> couponRedemptionList = asList(Mockito.mock(CouponRedemptionModel.class));


		when(flexibleSearchService.<CouponRedemptionModel> search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);
		when(searchResult.getResult()).thenReturn(couponRedemptionList);

		final List<CouponRedemptionModel> couponRedemptionAsResult = redemptionDao.findCouponRedemptionsByCodeAndUser(COUPON_CODE,
				user);

		Assert.assertEquals(couponRedemptionList, couponRedemptionAsResult);

	}

	@Test
	public void testCouponRedemptionWhenOrderAndUserAsParameter() throws RuleParameterValueMapperException
	{
		final AbstractOrderModel abstractOrder = mock(AbstractOrderModel.class);
		final UserModel user = mock(UserModel.class);
		final List<CouponRedemptionModel> couponRedemptionList = asList(Mockito.mock(CouponRedemptionModel.class));

		final SearchResult<CouponRedemptionModel> searchResult = mock(SearchResult.class);

		when(flexibleSearchService.<CouponRedemptionModel> search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);
		when(searchResult.getResult()).thenReturn(couponRedemptionList);

		final List<CouponRedemptionModel> couponRedemptionAsResult = redemptionDao
				.findCouponRedemptionsByCodeOrderAndUser(COUPON_CODE, abstractOrder, user);

		Assert.assertEquals(couponRedemptionList, couponRedemptionAsResult);

	}

}
