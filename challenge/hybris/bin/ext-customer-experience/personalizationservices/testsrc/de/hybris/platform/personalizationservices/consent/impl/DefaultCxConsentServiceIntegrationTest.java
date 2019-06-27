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
/**
 *
 */
package de.hybris.platform.personalizationservices.consent.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCxConsentServiceIntegrationTest extends ServicelayerTransactionalTest
{

	private static final String CUSTOMER = "customer1@hybris.com";
	private static final String CONSENT_GIVEN_MESSAGE = "Consent given where it should not";
	private static final String CONSENT_NOT_GIVEN_MESSAGE = "Consent not given where it should";

	private static final String USER_CONSENTS = "user-consents";
	private static final String CONSENT_GIVEN = "GIVEN";
	private static final String CONSENT_WITHDRAWN = "WITHDRAWN";

	private static final String CONSENT_NAME1 = "TEMPLATE1";
	private static final String CONSENT_NAME2 = "TEMPLATE2";
	private static final String CONSENT_NAME3 = "TEMPLATE3";
	private static final String CONSENT_NAME4 = "TEMPLATE4";



	@Resource
	private DefaultCxConsentService cxConsentService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	SessionService sessionService;

	@Resource
	UserService userService;

	@Before
	public void setup() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importData(new ClasspathImpExResource("/personalizationservices/test/testdata_consent.impex", "UTF-8"));
	}

	@Test
	public void testAnonymousConsentFromFallback()
	{
		//given
		baseSiteService.setCurrentBaseSite("testSite1", true);
		final CustomerModel user = userService.getAnonymousUser();

		//when
		final boolean actual = cxConsentService.userHasActiveConsent(user);

		//then
		Assert.assertTrue(CONSENT_NOT_GIVEN_MESSAGE, actual);
	}

	@Test
	public void testAnonymousConsentFromExposedTemplates()
	{
		//given
		baseSiteService.setCurrentBaseSite("testSite2", true);
		setSessionConsent(CONSENT_NAME1, null);
		addToSessionConsent(CONSENT_NAME2, CONSENT_GIVEN);

		final CustomerModel user = userService.getAnonymousUser();

		//when
		final boolean actual = cxConsentService.userHasActiveConsent(user);

		//then
		Assert.assertFalse(CONSENT_GIVEN_MESSAGE, actual);
	}

	@Test
	public void testAnonymousGivenConsentFromExposedTemplates()
	{
		//given
		baseSiteService.setCurrentBaseSite("testSite2", true);
		setSessionConsent(CONSENT_NAME1, CONSENT_GIVEN);
		addToSessionConsent(CONSENT_NAME2, CONSENT_GIVEN);

		final CustomerModel user = userService.getAnonymousUser();

		//when
		final boolean actual = cxConsentService.userHasActiveConsent(user);

		//then
		Assert.assertTrue(CONSENT_NOT_GIVEN_MESSAGE, actual);
	}

	@Test
	public void testAnonymousGivenConsentFromMissingTemplates()
	{
		//given
		baseSiteService.setCurrentBaseSite("testSite2", true);
		setSessionConsent(CONSENT_NAME1, CONSENT_GIVEN);

		final CustomerModel user = userService.getAnonymousUser();

		//when
		final boolean actual = cxConsentService.userHasActiveConsent(user);

		//then
		Assert.assertFalse(CONSENT_GIVEN_MESSAGE, actual);
	}

	@Test
	public void testAnonymousConsentFromMixedTemplates()
	{
		//given
		baseSiteService.setCurrentBaseSite("testSite3", true);
		setSessionConsent(CONSENT_NAME3, CONSENT_GIVEN);
		addToSessionConsent(CONSENT_NAME4, CONSENT_WITHDRAWN);
		final CustomerModel user = userService.getAnonymousUser();

		//when
		final boolean actual = cxConsentService.userHasActiveConsent(user);

		//then
		Assert.assertTrue(CONSENT_NOT_GIVEN_MESSAGE, actual);
	}

	@Test
	public void testAnonymousConsentWhenSessionConsentIsNull()
	{
		//given
		baseSiteService.setCurrentBaseSite("testSite2", true);
		final CustomerModel user = userService.getAnonymousUser();

		//when
		final boolean actual = cxConsentService.userHasActiveConsent(user);

		//then
		Assert.assertFalse(CONSENT_GIVEN_MESSAGE, actual);
	}

	@Test
	public void testAnonymousConsentWhenSessionConsentIsEmpty()
	{
		//given
		baseSiteService.setCurrentBaseSite("testSite2", true);
		sessionService.setAttribute(USER_CONSENTS, new HashMap<>());
		final CustomerModel user = userService.getAnonymousUser();

		//when
		final boolean actual = cxConsentService.userHasActiveConsent(user);

		//then
		Assert.assertFalse(CONSENT_GIVEN_MESSAGE, actual);
	}

	private void setSessionConsent(final String name, final String value)
	{
		final Map<String, String> consentMap = new HashMap<>();
		consentMap.put(name, value);
		sessionService.setAttribute(USER_CONSENTS, consentMap);
	}

	private void addToSessionConsent(final String name, final String value)
	{
		Map<String, String> consentMap = sessionService.getOrLoadAttribute(USER_CONSENTS, HashMap::new);
		consentMap = new HashMap<>(consentMap);
		consentMap.put(name, value);
		sessionService.setAttribute(USER_CONSENTS, consentMap);

	}

	@Test
	public void testLoggedInConsentFromFallback()
	{
		//given
		baseSiteService.setCurrentBaseSite("testSite1", true);
		final UserModel user = userService.getUserForUID(CUSTOMER);

		//when
		final boolean actual = cxConsentService.userHasActiveConsent(user);

		//then
		Assert.assertTrue(CONSENT_NOT_GIVEN_MESSAGE, actual);
	}

	@Test
	public void testLoggedInConsentGiven()
	{
		//given
		baseSiteService.setCurrentBaseSite("testSite2", true);
		final UserModel user = userService.getUserForUID(CUSTOMER);

		//when
		final boolean actual = cxConsentService.userHasActiveConsent(user);

		//then
		Assert.assertTrue(CONSENT_NOT_GIVEN_MESSAGE, actual);
	}

	@Test
	public void testLoggedInConsentWithdrawn()
	{
		//given
		baseSiteService.setCurrentBaseSite("testSite3", true);
		final UserModel user = userService.getUserForUID(CUSTOMER);

		//when
		final boolean actual = cxConsentService.userHasActiveConsent(user);

		//then
		Assert.assertFalse(CONSENT_GIVEN_MESSAGE, actual);
	}

}
