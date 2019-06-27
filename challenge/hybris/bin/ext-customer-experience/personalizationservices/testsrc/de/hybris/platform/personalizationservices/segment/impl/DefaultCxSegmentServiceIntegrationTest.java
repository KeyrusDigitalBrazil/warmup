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
package de.hybris.platform.personalizationservices.segment.impl;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.personalizationservices.AbstractCxServiceTest;
import de.hybris.platform.personalizationservices.data.UserToSegmentData;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;
import de.hybris.platform.personalizationservices.segment.CxUserSegmentSessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


@IntegrationTest
public class DefaultCxSegmentServiceIntegrationTest extends AbstractCxServiceTest
{
	private static final String SEGMENT1 = "segment1";
	private static final String SEGMENT2 = "segment2";
	private static final String SEGMENT3 = "segment3";
	private static final String CUSTOMER_WITH_SEGMENT2_3 = "customer6@hybris.com";
	private static final String CUSTOMER_WITH_SEGMENT1_2_3_FOR_TWO_BASESITE = "customer7@hybris.com";
	private static final String BASE_SITE = "testSite";

	@Resource
	private DefaultCxSegmentService cxSegmentService;
	@Resource
	private UserService userService;
	@Resource
	private CxUserSegmentSessionService cxUserSegmentSessionService;
	@Resource
	private BaseSiteService baseSiteService;


	@Test
	public void findSegmentByCodeTest()
	{
		//when
		final Optional<CxSegmentModel> segment = cxSegmentService.getSegment(SEGMENT_CODE);

		//then
		assertTrue(segment.isPresent());
		assertTrue(SEGMENT_CODE.equals(segment.get().getCode()));
	}

	@Test
	public void findNoSegmentByCodeTest()
	{
		//when
		final Optional<CxSegmentModel> segment = cxSegmentService.getSegment(SEGMENT_CODE + "...");

		//then
		assertFalse(segment.isPresent());
	}

	@Test
	public void findSegmentsPaginated()
	{
		//given
		final int pageSize = 2;
		final Map<String, String> params = new HashMap<>();
		final SearchPageData<?> searchPageData = buildSearchPageData(1, pageSize);

		//when
		final SearchPageData<CxSegmentModel> segments = cxSegmentService.getSegments(params, searchPageData);

		//then
		Assert.assertEquals(10, segments.getPagination().getTotalNumberOfResults());
		Assert.assertEquals(5, segments.getPagination().getNumberOfPages());


		final List<CxSegmentModel> results = segments.getResults();
		Assert.assertNotNull(results);
		Assert.assertEquals(pageSize, results.size());

		Assert.assertEquals(SEGMENT2, results.get(0).getCode());
		Assert.assertEquals(SEGMENT3, results.get(1).getCode());
		Assert.assertEquals(pageSize, segments.getPagination().getPageSize());
	}

	@Test
	public void findSegmentsPaginated2()
	{
		//given
		final int pageSize = 3;
		final Map<String, String> params = new HashMap<>();
		final SearchPageData<?> searchPageData = buildSearchPageData(3, pageSize);

		//when
		final SearchPageData<CxSegmentModel> segments = cxSegmentService.getSegments(params, searchPageData);

		//then
		Assert.assertEquals(10, segments.getPagination().getTotalNumberOfResults());
		Assert.assertEquals(4, segments.getPagination().getNumberOfPages());

		final List<CxSegmentModel> results = segments.getResults();
		Assert.assertNotNull(results);
		Assert.assertEquals(1, results.size());

		Assert.assertEquals("segment9", results.get(0).getCode());
		Assert.assertEquals(pageSize, segments.getPagination().getPageSize());
	}

	@Test
	public void findSegmentsPaginatedOverScale()
	{
		//given
		final int pageSize = 3;
		final Map<String, String> params = new HashMap<>();
		final SearchPageData<?> searchPageData = buildSearchPageData(4, pageSize);

		//when
		final SearchPageData<CxSegmentModel> segments = cxSegmentService.getSegments(params, searchPageData);

		//then
		Assert.assertEquals(10, segments.getPagination().getTotalNumberOfResults());
		Assert.assertEquals(4, segments.getPagination().getNumberOfPages());

		final List<CxSegmentModel> results = segments.getResults();
		Assert.assertNotNull(results);
		Assert.assertEquals(0, results.size());
		Assert.assertEquals(pageSize, segments.getPagination().getPageSize());
	}

	@Test(expected = IllegalArgumentException.class)
	public void findSegmentsPaginatedBelowScale()
	{
		//given
		final int pageSize = 3;
		final Map<String, String> params = new HashMap<>();
		final SearchPageData<?> searchPageData = buildSearchPageData(-1, pageSize);

		//when
		final SearchPageData<CxSegmentModel> segments = cxSegmentService.getSegments(params, searchPageData);
	}

	@Test
	public void testGetUserToSegmentForUser()
	{
		//given
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE);
		baseSiteService.setCurrentBaseSite(baseSite, false);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT2_3); //
		final Collection<CxUserToSegmentModel> expected = createUserSegments(user, BigDecimal.ONE, baseSite, SEGMENT2, SEGMENT3);

		//when
		final Collection<CxUserToSegmentModel> result = cxSegmentService.getUserToSegmentForUser(user);

		//then
		verifySegments(expected, result);
	}

	@Test
	public void testGetUserToSegmentForAnonymous()
	{
		//given
		final UserModel user = userService.getAnonymousUser();
		final Collection<UserToSegmentData> segments = createUserSegmentsData(BigDecimal.ONE, SEGMENT1, SEGMENT2);
		cxUserSegmentSessionService.setUserSegmentsInSession(user, segments);

		//when
		final Collection<CxUserToSegmentModel> result = cxSegmentService.getUserToSegmentForUser(user);

		//then
		verifySegmentsData(segments, result);
	}

	@Test
	public void testGetUserToSegmentForCalculation()
	{
		//given
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE);
		baseSiteService.setCurrentBaseSite(baseSite, false);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT2_3); //
		final Collection<CxUserToSegmentModel> expected = createUserSegments(user, BigDecimal.ONE, baseSite, SEGMENT2, SEGMENT3);

		//when
		final Collection<CxUserToSegmentModel> result = cxSegmentService.getUserToSegmentForCalculation(user);

		//then
		verifySegments(expected, result);
	}

	@Test
	public void testGetUserToSegmentForCalculationWhenCurentBaseSiteNotSet()
	{
		//given
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT2_3); //
		final Collection<CxUserToSegmentModel> expected = createUserSegments(user, BigDecimal.ONE, baseSite, SEGMENT2, SEGMENT3);

		//when
		final Collection<CxUserToSegmentModel> result = cxSegmentService.getUserToSegmentForCalculation(user);

		//then
		verifySegments(expected, result);
	}

	@Test
	public void testGetUserToSegmentForCalculationWhenDataForTwoBaseSites()
	{
		//given
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(BASE_SITE);
		baseSiteService.setCurrentBaseSite(baseSite, false);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_3_FOR_TWO_BASESITE); //
		final Collection<CxUserToSegmentModel> expected = createUserSegments(user, BigDecimal.ONE, baseSite, SEGMENT1, SEGMENT2,
				SEGMENT3);

		//when
		final Collection<CxUserToSegmentModel> result = cxSegmentService.getUserToSegmentForCalculation(user);

		//then
		verifySegments(expected, result);
	}

	@Test
	public void testGetUserToSegmentForCalculationWhenAnonymousUser()
	{
		//given
		final UserModel user = userService.getAnonymousUser();
		final Collection<UserToSegmentData> segments = createUserSegmentsData(BigDecimal.ONE, SEGMENT1, SEGMENT2);
		cxUserSegmentSessionService.setUserSegmentsInSession(user, segments);

		//when
		final Collection<CxUserToSegmentModel> result = cxSegmentService.getUserToSegmentForCalculation(user);

		//then
		verifySegmentsData(segments, result);
	}

	protected Collection<UserToSegmentData> createUserSegmentsData(final BigDecimal affinity, final String... segments)
	{
		return Arrays.asList(segments).stream().map(s -> createUserSegmentData(affinity, s)).collect(Collectors.toList());
	}

	protected UserToSegmentData createUserSegmentData(final BigDecimal affinity, final String segmentCode)
	{
		final UserToSegmentData data = new UserToSegmentData();
		data.setAffinity(affinity);
		data.setCode(segmentCode);
		return data;
	}

	protected void verifySegmentsData(final Collection<UserToSegmentData> expected, final Collection<CxUserToSegmentModel> current)
	{
		Assert.assertEquals(expected.size(), current.size());

		final Map<String, BigDecimal> expectedDataMap = expected.stream().collect(//
				Collectors.toMap(//
						UserToSegmentData::getCode, //
						UserToSegmentData::getAffinity));

		Assert.assertTrue(current.stream().allMatch(us -> expectedDataMap.containsKey(us.getSegment().getCode())
				&& expectedDataMap.get(us.getSegment().getCode()).equals(us.getAffinity())));
	}

	protected Collection<CxUserToSegmentModel> createUserSegments(final UserModel user, final BigDecimal affinity,
			final BaseSiteModel baseSite, final String... segments)
	{
		return Arrays.asList(segments).stream().map(s -> createUserSegment(user, affinity, s, baseSite))
				.collect(Collectors.toList());
	}

	protected CxUserToSegmentModel createUserSegment(final UserModel user, final BigDecimal affinity, final String segmentCode,
			final BaseSiteModel baseSite)
	{
		final CxUserToSegmentModel data = new CxUserToSegmentModel();
		data.setUser(user);
		data.setAffinity(affinity);
		final Optional<CxSegmentModel> segmentModel = cxSegmentService.getSegment(segmentCode);
		data.setSegment(segmentModel.orElse(null));
		data.setBaseSite(baseSite);
		return data;
	}

	protected void verifySegments(final Collection<CxUserToSegmentModel> expected, final Collection<CxUserToSegmentModel> current)
	{
		Assert.assertEquals(expected.size(), current.size());

		final Map<String, BigDecimal> expectedAffinityMap = expected.stream().collect(//
				Collectors.toMap(//
						this::getUserSegmentKey, //
						CxUserToSegmentModel::getAffinity));

		Assert.assertTrue(current.stream().allMatch(us -> expectedAffinityMap.containsKey(getUserSegmentKey(us))
				&& expectedAffinityMap.get(getUserSegmentKey(us)).compareTo(us.getAffinity()) == 0));
	}

	protected String getUserSegmentKey(final CxUserToSegmentModel us)
	{
		return us.getSegment().getCode() + (us.getBaseSite() == null ? "" : us.getBaseSite().getUid());
	}

	protected SearchPageData<?> buildSearchPageData(final int currentPage, final int pageSize)
	{
		final SearchPageData result = new SearchPageData();

		result.setPagination(new PaginationData());
		result.getPagination().setCurrentPage(currentPage);
		result.getPagination().setPageSize(pageSize);
		result.getPagination().setNeedsTotal(true);
		return result;
	}

}
