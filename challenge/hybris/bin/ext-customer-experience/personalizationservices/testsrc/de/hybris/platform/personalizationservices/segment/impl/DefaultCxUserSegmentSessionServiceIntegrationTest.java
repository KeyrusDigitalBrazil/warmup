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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.AbstractCxServiceTest;
import de.hybris.platform.personalizationservices.CxCalculationContext;
import de.hybris.platform.personalizationservices.constants.PersonalizationservicesConstants;
import de.hybris.platform.personalizationservices.data.UserToSegmentData;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCxUserSegmentSessionServiceIntegrationTest extends AbstractCxServiceTest
{
	private static final String BASE_SITE_WITH_USER_SEGMENT_STORE_IN_SESSION = "testSite1";
	private static final String BASE_SITE_WITH_USER_SEGMENT_NOT_STORE_IN_SESSION = "testSite";
	private static final String SEGMENT1 = "segment1";
	private static final String SEGMENT2 = "segment2";
	private static final String SEGMENT3 = "segment3";
	private static final String SEGMENT4 = "segment4";
	private static final String CUSTOMER_WITH_SEGMENT1 = "customer1@hybris.com";
	private static final String CUSTOMER_WITH_SEGMENT1_2_3 = "customer7@hybris.com";
	private static final String CUSTOMER_WITHOUT_SEGMENT = "customer10@hybris.com";
	private static final String CUSTOMER_WITH_SEGMENT1_2_FOR_DEFAULT_PROVIDER = "customer11@hybris.com";
	private static final String DEFAULT_PROVIDER_ID = "defaultProviderId";
	private static final String PROVIDER_1_ID = "provider1Id";

	@Resource
	private DefaultCxUserSegmentSessionService cxUserSegmentSessionService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private UserService userService;
	@Resource
	private SessionService sessionService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		importData(new ClasspathImpExResource("/personalizationservices/test/testdata_cxconfig.impex", "UTF-8"));
		sessionService.setAttribute(PersonalizationservicesConstants.ACTIVE_PERSONALIZATION, Boolean.TRUE);
	}

	@Test
	public void testIsUserSegmentStoreInSession()
	{
		//given
		baseSiteService.setCurrentBaseSite(BASE_SITE_WITH_USER_SEGMENT_STORE_IN_SESSION, false);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1); //

		//when
		final boolean result = cxUserSegmentSessionService.isUserSegmentStoredInSession(user);

		//then
		assertTrue(result);
	}

	@Test
	public void testIsUserSegmentNotStoreInSession()
	{
		//given
		baseSiteService.setCurrentBaseSite(BASE_SITE_WITH_USER_SEGMENT_NOT_STORE_IN_SESSION, false);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1); //

		//when
		final boolean result = cxUserSegmentSessionService.isUserSegmentStoredInSession(user);

		//then
		Assert.assertFalse(result);
	}

	@Test
	public void testIsUserSegmentStoreInSessionForAnonymous()
	{
		//given
		final UserModel anonymous = userService.getAnonymousUser();

		//when
		final boolean result = cxUserSegmentSessionService.isUserSegmentStoredInSession(anonymous);

		//then
		assertTrue(result);
	}

	@Test
	public void testGetUserSegments()
	{
		//given
		baseSiteService.setCurrentBaseSite(BASE_SITE_WITH_USER_SEGMENT_STORE_IN_SESSION, false);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_3); //
		userService.setCurrentUser(user);
		final Collection<UserToSegmentData> expected = createUserSegments(user, BigDecimal.ONE, SEGMENT1, SEGMENT2, SEGMENT3);

		//when
		final Collection<UserToSegmentData> result = cxUserSegmentSessionService.getUserSegmentsFromSession(user);

		//then
		verifySegments(expected, result);
	}

	@Test
	public void testGetUserSegmentsWhenUserSegmentIsNotStoreInSession()
	{
		//given
		baseSiteService.setCurrentBaseSite(BASE_SITE_WITH_USER_SEGMENT_NOT_STORE_IN_SESSION, false);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_3); //
		userService.setCurrentUser(user);

		//when
		final Collection<UserToSegmentData> result = cxUserSegmentSessionService.getUserSegmentsFromSession(user);

		//then
		assertTrue(result.isEmpty());
	}

	@Test
	public void testSetUserSegments()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_3); //
		final Collection<UserToSegmentData> userSegments = createUserSegments(user, BigDecimal.valueOf(0.7), SEGMENT2, SEGMENT4);

		//when
		cxUserSegmentSessionService.setUserSegmentsInSession(user, userSegments);

		//then
		verifySegments(userSegments, cxUserSegmentSessionService.getUserSegmentsFromSession(user));
	}


	@Test
	public void testAddUserSegments()
	{
		//given
		baseSiteService.setCurrentBaseSite(BASE_SITE_WITH_USER_SEGMENT_STORE_IN_SESSION, false);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_3); //
		userService.setCurrentUser(user);
		final Collection<UserToSegmentData> userSegments = createUserSegments(user, BigDecimal.valueOf(0.7), SEGMENT2, SEGMENT4);
		final Collection<UserToSegmentData> expectedUserSegments = createUserSegments(user, BigDecimal.ONE, SEGMENT1, SEGMENT3);
		expectedUserSegments.addAll(userSegments);

		//when
		cxUserSegmentSessionService.addUserSegmentsInSession(user, userSegments);

		//then
		verifySegments(expectedUserSegments, cxUserSegmentSessionService.getUserSegmentsFromSession(user));
	}


	@Test
	public void testRemoveUserSegments()
	{
		//given
		baseSiteService.setCurrentBaseSite(BASE_SITE_WITH_USER_SEGMENT_STORE_IN_SESSION, false);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_3); //
		userService.setCurrentUser(user);
		final Collection<UserToSegmentData> userSegmentsToRemove = createUserSegments(user, BigDecimal.ONE, SEGMENT2, SEGMENT4);
		final Collection<UserToSegmentData> expectedUserSegments = createUserSegments(user, BigDecimal.ONE, SEGMENT1, SEGMENT3);

		//when
		cxUserSegmentSessionService.removeUserSegmentsFromSession(user, userSegmentsToRemove);

		//then
		verifySegments(expectedUserSegments, cxUserSegmentSessionService.getUserSegmentsFromSession(user));
	}

	@Test
	public void testLoadUserSegmentsIntoSession()
	{
		//given
		baseSiteService.setCurrentBaseSite(BASE_SITE_WITH_USER_SEGMENT_STORE_IN_SESSION, false);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_3); //
		final Collection<UserToSegmentData> expected = createUserSegments(user, BigDecimal.ONE, SEGMENT1, SEGMENT2, SEGMENT3);

		//when
		cxUserSegmentSessionService.loadUserSegmentsIntoSession(user);

		//then
		verifySegments(expected, cxUserSegmentSessionService.getUserSegmentsFromSession(user));
	}

	@Test
	public void testSetUserSegmentsWithContext()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITHOUT_SEGMENT); //
		final Collection<UserToSegmentData> userSegments = createUserSegments(user, DEFAULT_PROVIDER_ID, BigDecimal.valueOf(0.7),
				SEGMENT2, SEGMENT4);
		final CxCalculationContext context = new CxCalculationContext();
		context.setSegmentUpdateProviders(Collections.singleton(DEFAULT_PROVIDER_ID));

		//when
		cxUserSegmentSessionService.setUserSegmentsInSession(user, userSegments, context);

		//then
		verifySegments(userSegments, cxUserSegmentSessionService.getUserSegmentsFromSession(user));
	}

	@Test
	public void testSetUserSegmentsWithEmptyContext()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITHOUT_SEGMENT); //
		final Collection<UserToSegmentData> userSegments = createUserSegments(user, DEFAULT_PROVIDER_ID, BigDecimal.valueOf(0.7),
				SEGMENT2, SEGMENT4);
		final CxCalculationContext context = new CxCalculationContext();

		//when
		cxUserSegmentSessionService.setUserSegmentsInSession(user, userSegments, context);

		//then
		verifySegments(userSegments, cxUserSegmentSessionService.getUserSegmentsFromSession(user));
	}

	@Test
	public void testSetUserSegmentsWithNullContext()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITHOUT_SEGMENT); //
		final Collection<UserToSegmentData> userSegments = createUserSegments(user, DEFAULT_PROVIDER_ID, BigDecimal.valueOf(0.7),
				SEGMENT2, SEGMENT4);

		//when
		cxUserSegmentSessionService.setUserSegmentsInSession(user, userSegments, null);

		//then
		verifySegments(userSegments, cxUserSegmentSessionService.getUserSegmentsFromSession(user));
	}

	@Test
	public void testSetUserSegmentsForProvider()
	{
		//given
		baseSiteService.setCurrentBaseSite(BASE_SITE_WITH_USER_SEGMENT_STORE_IN_SESSION, false);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_FOR_DEFAULT_PROVIDER);
		userService.setCurrentUser(user);
		final Collection<UserToSegmentData> expectedUserSegments = createUserSegments(user, DEFAULT_PROVIDER_ID, BigDecimal.ONE,
				SEGMENT1, SEGMENT2);
		final Collection<UserToSegmentData> userSegments = createUserSegments(user, PROVIDER_1_ID, BigDecimal.valueOf(0.7),
				SEGMENT2, SEGMENT4);
		expectedUserSegments.addAll(userSegments);
		final CxCalculationContext context = new CxCalculationContext();
		context.setSegmentUpdateProviders(Collections.singleton(PROVIDER_1_ID));

		//when
		cxUserSegmentSessionService.setUserSegmentsInSession(user, userSegments, context);

		//then
		verifySegments(expectedUserSegments, cxUserSegmentSessionService.getUserSegmentsFromSession(user));
	}

	@Test
	public void testSetUserSegmentsWhenNoSegmentsForProviderContext()
	{
		//given
		final UserModel user = userService.getUserForUID(CUSTOMER_WITHOUT_SEGMENT); //
		final Collection<UserToSegmentData> userSegments = createUserSegments(user, DEFAULT_PROVIDER_ID, BigDecimal.valueOf(0.7),
				SEGMENT2, SEGMENT4);
		final CxCalculationContext context = new CxCalculationContext();
		context.setSegmentUpdateProviders(Collections.singleton(PROVIDER_1_ID));

		//when
		cxUserSegmentSessionService.setUserSegmentsInSession(user, userSegments, context);

		//then
		assertTrue(CollectionUtils.isEmpty(cxUserSegmentSessionService.getUserSegmentsFromSession(user)));
	}

	@Test
	public void testAddUserSegmentsWithProvider()
	{
		//given
		baseSiteService.setCurrentBaseSite(BASE_SITE_WITH_USER_SEGMENT_STORE_IN_SESSION, false);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_FOR_DEFAULT_PROVIDER); //
		userService.setCurrentUser(user);
		final Collection<UserToSegmentData> userSegments = createUserSegments(user, DEFAULT_PROVIDER_ID, BigDecimal.valueOf(0.7),
				SEGMENT2, SEGMENT4);
		userSegments.addAll(createUserSegments(user, PROVIDER_1_ID, BigDecimal.ONE, SEGMENT1, SEGMENT3));

		final Collection<UserToSegmentData> expectedUserSegments = userSegments.stream().collect(Collectors.toList());
		expectedUserSegments.add(createUserSegment(DEFAULT_PROVIDER_ID, BigDecimal.ONE, SEGMENT1));

		//when
		cxUserSegmentSessionService.addUserSegmentsInSession(user, userSegments);

		//then
		verifySegments(expectedUserSegments, cxUserSegmentSessionService.getUserSegmentsFromSession(user));
	}

	@Test
	public void testRemoveUserSegmentsForProvider()
	{
		//given
		baseSiteService.setCurrentBaseSite(BASE_SITE_WITH_USER_SEGMENT_STORE_IN_SESSION, false);
		final UserModel user = userService.getUserForUID(CUSTOMER_WITH_SEGMENT1_2_FOR_DEFAULT_PROVIDER); //
		userService.setCurrentUser(user);
		final Collection<UserToSegmentData> userSegmentsToRemove = createUserSegments(user, DEFAULT_PROVIDER_ID, BigDecimal.ONE,
				SEGMENT2);
		final Collection<UserToSegmentData> expectedUserSegments = createUserSegments(user, DEFAULT_PROVIDER_ID, BigDecimal.ONE,
				SEGMENT1);

		//when
		cxUserSegmentSessionService.removeUserSegmentsFromSession(user, userSegmentsToRemove);

		//then
		verifySegments(expectedUserSegments, cxUserSegmentSessionService.getUserSegmentsFromSession(user));
	}

	protected Collection<UserToSegmentData> createUserSegments(final UserModel user, final BigDecimal affinity,
			final String... segments)
	{
		return Arrays.asList(segments).stream().map(s -> createUserSegment(affinity, s)).collect(Collectors.toList());
	}

	protected UserToSegmentData createUserSegment(final BigDecimal affinity, final String segmentCode)
	{
		final UserToSegmentData data = new UserToSegmentData();
		data.setAffinity(affinity);
		data.setCode(segmentCode);

		return data;
	}

	protected Collection<UserToSegmentData> createUserSegments(final UserModel user, final String providerId,
			final BigDecimal affinity, final String... segments)
	{
		return Arrays.asList(segments).stream().map(s -> createUserSegment(providerId, affinity, s)).collect(Collectors.toList());
	}

	protected UserToSegmentData createUserSegment(final String providerId, final BigDecimal affinity, final String segmentCode)
	{
		final UserToSegmentData data = createUserSegment(affinity, segmentCode);
		data.setProvider(providerId);

		return data;
	}

	protected void verifySegments(final Collection<UserToSegmentData> expected, final Collection<UserToSegmentData> current)
	{
		assertEquals(expected.size(), current.size());

		final Map<String, BigDecimal> expectedAffinityMap = expected.stream().collect(//
				Collectors.toMap(//
						this::getUserToSegmentKey, //
						UserToSegmentData::getAffinity));

		assertTrue(current.stream().allMatch(us -> expectedAffinityMap.containsKey(getUserToSegmentKey(us))
				&& expectedAffinityMap.get(getUserToSegmentKey(us)).compareTo(us.getAffinity()) == 0));
	}

	protected String getUserToSegmentKey(final UserToSegmentData data)
	{
		return data.getCode() + "_" + data.getProvider();
	}
}
