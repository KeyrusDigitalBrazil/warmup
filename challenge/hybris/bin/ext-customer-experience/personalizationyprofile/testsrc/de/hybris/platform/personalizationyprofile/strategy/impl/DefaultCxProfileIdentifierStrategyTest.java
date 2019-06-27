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
package de.hybris.platform.personalizationyprofile.strategy.impl;

import static de.hybris.platform.personalizationyprofile.constants.PersonalizationyprofileConstants.CONSENT_REFERENCE_SESSION_ATTR_KEY;
import static de.hybris.platform.personalizationyprofile.constants.PersonalizationyprofileConstants.IDENTITY_ORIGIN_USER_ACCOUNT;
import static de.hybris.platform.personalizationyprofile.constants.PersonalizationyprofileConstants.IDENTITY_TYPE_EMAIL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.personalizationyprofile.yaas.ProfileReference;
import de.hybris.platform.personalizationyprofile.yaas.client.CxIdentityServiceClient;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.session.impl.DefaultSessionTokenService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hybris.charon.exp.HttpException;


@UnitTest
public class DefaultCxProfileIdentifierStrategyTest
{
	private static final String USER_ID = "userId";
	private static final String PROFILE_ID = "profileId";
	private static final String PROFILE_ID_FOR_REGISTERED = "profileIdForRegistered";
	private static final String CONSENT_REFERENCE_VALUE = "consentReference";
	private static final String SESSION_TOKEN_VALUE = "sessionToken";
	private static final String BOOLEAN_PAUSE_TRACKING_PARAMETER = "booleanPauseTrackingParameter";
	private static final String SECOND_BOOLEAN_PAUSE_TRACKING_PARAMETER = "secondbooleanPauseTrackingParameter";
	private static final String NONE_BOOLEAN_PAUSE_TRACKING_PARAMETER = "noneBooleanPauseTrackingParameter";

	private DefaultCxProfileIdentifierStrategy strategy;

	@Mock
	private CxIdentityServiceClient cxIdentityServiceClient;

	@Mock
	private SessionService sessionService;

	@Mock
	private DefaultSessionTokenService defaultSessionTokenService;

	@Mock
	private UserService userService;

	@Mock
	private CustomerModel user;

	@Mock
	private CustomerModel anonymousUser;

	private List<String> pauseConsentReferenceUseParameters;

	private String sessionAttrKey;

	private final ProfileReference profileReference = new ProfileReference();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		strategy = new DefaultCxProfileIdentifierStrategy();
		strategy.setCxIdentityServiceClient(cxIdentityServiceClient);
		strategy.setSessionService(sessionService);
		strategy.setUserService(userService);
		strategy.setDefaultSessionTokenService(defaultSessionTokenService);
		pauseConsentReferenceUseParameters = Collections.emptyList();
		strategy.setPauseConsentReferenceUseParameters(pauseConsentReferenceUseParameters);
		when(user.getUid()).thenReturn(USER_ID);
		when(defaultSessionTokenService.getOrCreateSessionToken()).thenReturn(SESSION_TOKEN_VALUE);
		sessionAttrKey = strategy.getSessionAttributeKey(user);
		profileReference.setProfileId(PROFILE_ID_FOR_REGISTERED);
		when(Boolean.valueOf(userService.isAnonymousUser(user))).thenReturn(Boolean.FALSE);
		when(Boolean.valueOf(userService.isAnonymousUser(anonymousUser))).thenReturn(Boolean.TRUE);
	}

	@Test
	public void testGetProfileIdentifierFromSession()
	{
		//given
		when(sessionService.getAttribute(sessionAttrKey)).thenReturn(PROFILE_ID);

		//when
		final String profileIdentifier = strategy.getProfileIdentifier(user);

		//then
		assertEquals(PROFILE_ID, profileIdentifier);
	}

	@Test
	public void testGetProfileIdentifierForRegisteredUser()
	{
		//given
		when(sessionService.getAttribute(sessionAttrKey)).thenReturn(null);
		when(cxIdentityServiceClient.getProfileReferences(USER_ID, IDENTITY_TYPE_EMAIL, IDENTITY_ORIGIN_USER_ACCOUNT)).thenReturn(
				Collections.singletonList(profileReference));

		//when
		final String profileIdentifier = strategy.getProfileIdentifier(user);

		//then
		verify(cxIdentityServiceClient, times(1)).getProfileReferences(any(), any(), any());
		assertEquals(PROFILE_ID_FOR_REGISTERED, profileIdentifier);
	}

	@Test
	public void testGetProfileIdentifierForAnonymousUser()
	{
		//given
		when(sessionService.getAttribute(sessionAttrKey)).thenReturn(null);
		when(sessionService.getAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY)).thenReturn(CONSENT_REFERENCE_VALUE);

		//when
		final String profileIdentifier = strategy.getProfileIdentifier(anonymousUser);

		//then
		verify(cxIdentityServiceClient, times(0)).getProfileReferences(any(), any(), any());
		assertEquals(CONSENT_REFERENCE_VALUE, profileIdentifier);
	}

	@Test
	public void testGetProfileIdentifierForAnonymousUserProfileTrackingPausedOneBooleanFalse()
	{
		//given
		when(sessionService.getAttribute(sessionAttrKey)).thenReturn(null);
		pauseConsentReferenceUseParameters = Collections.singletonList(BOOLEAN_PAUSE_TRACKING_PARAMETER);
		strategy.setPauseConsentReferenceUseParameters(pauseConsentReferenceUseParameters);
		when(sessionService.getAttribute(BOOLEAN_PAUSE_TRACKING_PARAMETER)).thenReturn(Boolean.FALSE);
		when(sessionService.getAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY)).thenReturn(CONSENT_REFERENCE_VALUE);

		//when
		final String profileIdentifier = strategy.getProfileIdentifier(anonymousUser);

		//then
		assertEquals(CONSENT_REFERENCE_VALUE, profileIdentifier);
	}

	@Test
	public void testGetProfileIdentifierForAnonymousUserProfileTrackingPausedOneBooleanTrue()
	{
		//given
		when(sessionService.getAttribute(sessionAttrKey)).thenReturn(null);
		pauseConsentReferenceUseParameters = Collections.singletonList(BOOLEAN_PAUSE_TRACKING_PARAMETER);
		strategy.setPauseConsentReferenceUseParameters(pauseConsentReferenceUseParameters);
		when(sessionService.getAttribute(BOOLEAN_PAUSE_TRACKING_PARAMETER)).thenReturn(Boolean.TRUE);

		//when
		final String profileIdentifier = strategy.getProfileIdentifier(anonymousUser);

		//then
		assertEquals(null, profileIdentifier);
	}

	@Test
	public void testGetProfileIdentifierForAnonymousUserProfileTrackingPausedOneNoneBoolean()
	{
		//given
		when(sessionService.getAttribute(sessionAttrKey)).thenReturn(null);
		pauseConsentReferenceUseParameters = Collections.singletonList(NONE_BOOLEAN_PAUSE_TRACKING_PARAMETER);
		strategy.setPauseConsentReferenceUseParameters(pauseConsentReferenceUseParameters);
		when(sessionService.getAttribute(NONE_BOOLEAN_PAUSE_TRACKING_PARAMETER)).thenReturn(StringUtils.SPACE);

		//when
		final String profileIdentifier = strategy.getProfileIdentifier(anonymousUser);

		//then
		assertEquals(null, profileIdentifier);
	}

	@Test
	public void testGetProfileIdentifierForAnonymousUserProfileTrackingPausedOneNoneBooleanNotExists()
	{
		//given
		when(sessionService.getAttribute(sessionAttrKey)).thenReturn(null);
		pauseConsentReferenceUseParameters = Collections.singletonList(NONE_BOOLEAN_PAUSE_TRACKING_PARAMETER);
		strategy.setPauseConsentReferenceUseParameters(pauseConsentReferenceUseParameters);
		when(sessionService.getAttribute(NONE_BOOLEAN_PAUSE_TRACKING_PARAMETER)).thenReturn(null);
		when(sessionService.getAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY)).thenReturn(CONSENT_REFERENCE_VALUE);

		//when
		final String profileIdentifier = strategy.getProfileIdentifier(anonymousUser);

		//then
		assertEquals(CONSENT_REFERENCE_VALUE, profileIdentifier);
	}

	@Test
	public void testGetProfileIdentifierForAnonymousUserProfileTrackingPausedOneBooleanAndOneNoneBoolean()
	{
		//given
		when(sessionService.getAttribute(sessionAttrKey)).thenReturn(null);
		pauseConsentReferenceUseParameters = Arrays.asList(BOOLEAN_PAUSE_TRACKING_PARAMETER, NONE_BOOLEAN_PAUSE_TRACKING_PARAMETER);
		strategy.setPauseConsentReferenceUseParameters(pauseConsentReferenceUseParameters);
		when(sessionService.getAttribute(BOOLEAN_PAUSE_TRACKING_PARAMETER)).thenReturn(Boolean.TRUE);
		when(sessionService.getAttribute(NONE_BOOLEAN_PAUSE_TRACKING_PARAMETER)).thenReturn(StringUtils.SPACE);

		//when
		final String profileIdentifier = strategy.getProfileIdentifier(anonymousUser);

		//then
		assertEquals(null, profileIdentifier);
	}

	@Test
	public void testGetProfileIdentifierForAnonymousUserProfileTrackingPausedTwoBooleanAndOneNoneBoolean()
	{
		//given
		when(sessionService.getAttribute(sessionAttrKey)).thenReturn(null);
		pauseConsentReferenceUseParameters = Arrays.asList(BOOLEAN_PAUSE_TRACKING_PARAMETER, NONE_BOOLEAN_PAUSE_TRACKING_PARAMETER);
		strategy.setPauseConsentReferenceUseParameters(pauseConsentReferenceUseParameters);
		when(sessionService.getAttribute(BOOLEAN_PAUSE_TRACKING_PARAMETER)).thenReturn(Boolean.FALSE);
		when(sessionService.getAttribute(SECOND_BOOLEAN_PAUSE_TRACKING_PARAMETER)).thenReturn(Boolean.TRUE);
		when(sessionService.getAttribute(NONE_BOOLEAN_PAUSE_TRACKING_PARAMETER)).thenReturn(StringUtils.SPACE);

		//when
		final String profileIdentifier = strategy.getProfileIdentifier(anonymousUser);

		//then
		assertEquals(null, profileIdentifier);
	}

	@Test
	public void testGetProfileIdentifierForAnonymousUserProfileTrackingPausedOneBooleanAndOneNoneBooleanNotExist()
	{
		//given
		when(sessionService.getAttribute(sessionAttrKey)).thenReturn(null);
		pauseConsentReferenceUseParameters = Arrays.asList(BOOLEAN_PAUSE_TRACKING_PARAMETER, NONE_BOOLEAN_PAUSE_TRACKING_PARAMETER);
		strategy.setPauseConsentReferenceUseParameters(pauseConsentReferenceUseParameters);
		when(sessionService.getAttribute(BOOLEAN_PAUSE_TRACKING_PARAMETER)).thenReturn(null);
		when(sessionService.getAttribute(NONE_BOOLEAN_PAUSE_TRACKING_PARAMETER)).thenReturn(null);
		when(sessionService.getAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY)).thenReturn(CONSENT_REFERENCE_VALUE);

		//when
		final String profileIdentifier = strategy.getProfileIdentifier(anonymousUser);

		//then
		assertEquals(CONSENT_REFERENCE_VALUE, profileIdentifier);
	}


	@Test
	public void testGetProfileIdentifierFallbackValueForRegisteredUser()
	{
		//given
		when(sessionService.getAttribute(sessionAttrKey)).thenReturn(null);
		when(cxIdentityServiceClient.getProfileReferences(USER_ID, IDENTITY_TYPE_EMAIL, IDENTITY_ORIGIN_USER_ACCOUNT)).thenReturn(
				Collections.emptyList());
		when(sessionService.getAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY)).thenReturn(CONSENT_REFERENCE_VALUE);

		//when
		final String profileIdentifier = strategy.getProfileIdentifier(user);

		//then
		verify(cxIdentityServiceClient, times(1)).getProfileReferences(any(), any(), any());
		assertEquals(CONSENT_REFERENCE_VALUE, profileIdentifier);
	}

	@Test
	public void testNoProfileIdentifier()
	{
		//given
		when(sessionService.getAttribute(sessionAttrKey)).thenReturn(null);
		when(cxIdentityServiceClient.getProfileReferences(USER_ID, IDENTITY_TYPE_EMAIL, IDENTITY_ORIGIN_USER_ACCOUNT)).thenThrow(
				new HttpException(Integer.valueOf(400), "Bad request"));
		when(sessionService.getAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY)).thenReturn(null);

		//when
		final String profileIdentifier = strategy.getProfileIdentifier(user);

		//then
		assertNull(profileIdentifier);
		verify(cxIdentityServiceClient, times(1)).getProfileReferences(any(), any(), any());
	}

	@Test
	public void testRuntimeExceptionFromIdentityService()
	{
		//given
		when(sessionService.getAttribute(sessionAttrKey)).thenReturn(null);
		when(cxIdentityServiceClient.getProfileReferences(USER_ID, IDENTITY_TYPE_EMAIL, IDENTITY_ORIGIN_USER_ACCOUNT)).thenThrow(
				new RuntimeException("Unknown exception"));
		when(sessionService.getAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY)).thenReturn(null);

		//when
		final String profileIdentifier = strategy.getProfileIdentifier(user);

		//then
		assertNull(profileIdentifier);
		verify(cxIdentityServiceClient, times(1)).getProfileReferences(any(), any(), any());
	}

}
