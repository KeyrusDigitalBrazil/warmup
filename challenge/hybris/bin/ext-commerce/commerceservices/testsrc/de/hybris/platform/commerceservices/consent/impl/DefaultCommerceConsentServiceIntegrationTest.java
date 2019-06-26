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
package de.hybris.platform.commerceservices.consent.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.consent.exceptions.CommerceConsentGivenException;
import de.hybris.platform.commerceservices.consent.exceptions.CommerceConsentWithdrawnException;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;


/**
 * JUnit test suite for {@link DefaultCommerceConsentService}
 */
@IntegrationTest
public class DefaultCommerceConsentServiceIntegrationTest extends ServicelayerTest
{
	private static final String USER_UID = "testcustomer";
	private static final String USER_UID2 = "testcustomer2";

	private static final String TEST_BASESITE_UID = "testSite";
	private static final String TEST_BASESITE_UID2 = "testSite2";
	private static final String TEST_BASESITE_UID_NON_EXISTING = "nonExistingTestSite";

	private static final String CONSENT_TEMPLATE_1 = "CONSENT_TEMPLATE_1";
	private static final String CONSENT_TEMPLATE_3 = "CONSENT_TEMPLATE_3";
	private static final String CONSENT_TEMPLATE_2 = "CONSENT_TEMPLATE_2";

	private static final Integer VERSION_0 = Integer.valueOf(0);
	private static final Integer VERSION_2 = Integer.valueOf(2);
	private static final Integer VERSION_NON_EXISTING = Integer.valueOf(6);

	private static final String CONSENT_1 = "consent1";
	private static final String CONSENT_3 = "consent3";
	private static final String CONSENT_NON_EXISTING = "nonExistingConsentCode";

	private static final int ONE_MINUTE_DELTA = 60000;

	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

	@Resource
	private TimeService timeService;

	@Resource
	private UserService userService;

	@Resource
	private BaseSiteService baseSiteService;

	private BaseSiteModel currentBaseSite;

	private CustomerModel customer1, customer2;

	@Resource
	private CommerceConsentService commerceConsentService;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/commerceservices/test/testConsents.impex", "utf-8");

		customer1 = (CustomerModel) userService.getUserForUID(USER_UID);
		customer2 = (CustomerModel) userService.getUserForUID(USER_UID2);
		currentBaseSite = baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID);
		baseSiteService.setCurrentBaseSite(currentBaseSite, false);
	}

	@Test
	public void testGetConsentTemplates()
	{
		final List<ConsentTemplateModel> consentTemplateList = commerceConsentService
				.getConsentTemplates(baseSiteService.getCurrentBaseSite());

		assertEquals(3, consentTemplateList.size());
		testIdAndVersion(consentTemplateList.get(0), CONSENT_TEMPLATE_1, VERSION_2);
		testIdAndVersion(consentTemplateList.get(1), CONSENT_TEMPLATE_2, VERSION_2);
		testIdAndVersion(consentTemplateList.get(2), CONSENT_TEMPLATE_3, VERSION_0);
	}

	@Test
	public void testGetConsentTemplatesWhenNoConsentAvailableForSite()
	{
		final List<ConsentTemplateModel> consentTemplateList = commerceConsentService
				.getConsentTemplates(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID2));

		assertTrue(consentTemplateList.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetConsentTemplatesWhenWrongSiteProvided()
	{
		final List<ConsentTemplateModel> consentTemplateList = commerceConsentService
				.getConsentTemplates(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID_NON_EXISTING));

		assertTrue(consentTemplateList.isEmpty());
	}

	@Test
	public void testGetConsentTemplateForExistingConsentTemplate()
	{
		final ConsentTemplateModel consentTemplateModel = commerceConsentService.getConsentTemplate(CONSENT_TEMPLATE_1, VERSION_2,
				currentBaseSite);

		assertNotNull(consentTemplateModel);
	}

	@Test(expected = ModelNotFoundException.class)
	public void testGetConsentTemplateForNonExistingConsentTemplateBecauseOfWrongVersion()
	{
		commerceConsentService.getConsentTemplate(CONSENT_TEMPLATE_1, VERSION_NON_EXISTING, currentBaseSite);
	}

	@Test(expected = ModelNotFoundException.class)
	public void testGetConsentTemplateForNonExistingConsentTemplate()
	{
		commerceConsentService.getConsentTemplate(TEST_BASESITE_UID_NON_EXISTING, VERSION_2, currentBaseSite);
	}

	@Test
	public void testGetLatestConsentTemplateForExistingConsentTemplate()
	{
		final ConsentTemplateModel consentTemplateModel = commerceConsentService.getLatestConsentTemplate(CONSENT_TEMPLATE_1,
				currentBaseSite);

		assertNotNull(consentTemplateModel);
		assertEquals(VERSION_2, consentTemplateModel.getVersion());
	}

	@Test(expected = ModelNotFoundException.class)
	public void testGetLatestConsentTemplateForNonExistingConsentTemplate()
	{
		commerceConsentService.getLatestConsentTemplate(TEST_BASESITE_UID_NON_EXISTING, currentBaseSite);
	}

	@Test
	public void testGetConsentReturnsCorrectConsent()
	{
		final ConsentModel retrievedConsentModel = commerceConsentService.getConsent(CONSENT_1);

		assertEquals(CONSENT_TEMPLATE_1, retrievedConsentModel.getConsentTemplate().getId());
		assertEquals(USER_UID, retrievedConsentModel.getCustomer().getUid());
		assertEquals("02-07-2017", dateFormatter.format(retrievedConsentModel.getConsentGivenDate()));
		assertNull(retrievedConsentModel.getConsentWithdrawnDate());
	}

	@Test(expected = ModelNotFoundException.class)
	public void testGetConsentThrowsExceptionWhenNoConsentFound()
	{
		commerceConsentService.getConsent(CONSENT_NON_EXISTING);
	}

	@Test
	public void testGetActiveConsentReturnsTheLatestGivenConsent()
	{
		final ConsentTemplateModel consentTemplate = commerceConsentService.getLatestConsentTemplate(CONSENT_TEMPLATE_1,
				currentBaseSite);
		assertNotNull("consentTemplate should not be null", consentTemplate);
		final ConsentModel consentModel = commerceConsentService.getActiveConsent(customer1, consentTemplate);
		assertNotNull(consentModel);
		assertEquals(CONSENT_3, consentModel.getCode());
		assertEquals(customer1, consentModel.getCustomer());
		assertEquals(consentTemplate, consentModel.getConsentTemplate());
		assertEquals("04-07-2017", dateFormatter.format(consentModel.getConsentGivenDate()));
	}

	@Test
	public void testGetActiveConsentWhenNoConsentExists()
	{
		final ConsentModel consentModel = commerceConsentService.getActiveConsent(customer1,
				commerceConsentService.getLatestConsentTemplate(CONSENT_TEMPLATE_3, currentBaseSite));
		assertNull(consentModel);
	}

	@Test
	public void testGiveConsentWhenNoConsentExists()
	{
		final ConsentTemplateModel consentTemplateModel = commerceConsentService.getLatestConsentTemplate(CONSENT_TEMPLATE_3,
				currentBaseSite);

		// Check that no consent exists
		final ConsentModel consentModel = commerceConsentService.getActiveConsent(customer1, consentTemplateModel);
		assertNull(consentModel);

		// Give consent
		commerceConsentService.giveConsent(customer1, consentTemplateModel);
		final long consentGivenTimestamp = timeService.getCurrentTime().getTime();

		// Check that consent exists now
		final ConsentModel addedConsentModel = commerceConsentService.getActiveConsent(customer1, consentTemplateModel);
		assertNotNull(addedConsentModel);
		assertTrue((consentGivenTimestamp - addedConsentModel.getConsentGivenDate().getTime()) < ONE_MINUTE_DELTA); // The consent was created within the last minute
		assertNull(addedConsentModel.getConsentWithdrawnDate());
	}

	@Test(expected = CommerceConsentGivenException.class)
	public void testGiveConsentWhenConsentGivenAlready()
	{
		final ConsentTemplateModel consentTemplateModel = commerceConsentService.getLatestConsentTemplate(CONSENT_TEMPLATE_1,
				currentBaseSite);

		// Check that consent given already
		final ConsentModel consentModel = commerceConsentService.getActiveConsent(customer1, consentTemplateModel);
		assertNotNull(consentModel);
		assertNotNull(consentModel.getConsentGivenDate());
		assertNull(consentModel.getConsentWithdrawnDate());

		// Give consent
		commerceConsentService.giveConsent(customer1, consentTemplateModel);
	}

	@Test
	public void testGiveConsentWhenConsentAvailableButWithdraw()
	{
		final ConsentTemplateModel consentTemplateModel = commerceConsentService.getLatestConsentTemplate(CONSENT_TEMPLATE_2,
				currentBaseSite);

		// Check that consent withdraw already
		final ConsentModel consentModel = commerceConsentService.getActiveConsent(customer2, consentTemplateModel);
		assertNotNull(consentModel);
		assertNotNull(consentModel.getConsentGivenDate());
		assertNotNull(consentModel.getConsentWithdrawnDate());

		// Give consent
		commerceConsentService.giveConsent(customer2, consentTemplateModel);

		// Check that new consent was not added
		final ConsentModel addedConsentModel = commerceConsentService.getActiveConsent(customer2, consentTemplateModel);
		assertNotNull(addedConsentModel);
		assertNotEquals(consentModel.getCode(), addedConsentModel.getCode());
		assertNotEquals(consentModel.getConsentGivenDate(), addedConsentModel.getConsentGivenDate());
	}

	@Test(expected = CommerceConsentWithdrawnException.class)
	public void testWithdrawConsentWhenWithdrawnAlready()
	{
		final ConsentTemplateModel consentTemplateModel = commerceConsentService.getLatestConsentTemplate(CONSENT_TEMPLATE_2,
				currentBaseSite);
		final ConsentModel consentModel = commerceConsentService.getActiveConsent(customer2, consentTemplateModel);
		userService.setCurrentUser(customer2);
		assertNotNull(consentModel.getConsentWithdrawnDate());

		// Withdraw consent
		commerceConsentService.withdrawConsent(consentModel);
	}

	@Test
	public void testWithdrawConsentWhenConsentWasNotWithdrawnYet()
	{
		final ConsentTemplateModel consentTemplateModel = commerceConsentService.getLatestConsentTemplate(CONSENT_TEMPLATE_1,
				currentBaseSite);
		final ConsentModel consentModel = commerceConsentService.getActiveConsent(customer1, consentTemplateModel);
		userService.setCurrentUser(customer1);

		// Withdraw consent
		commerceConsentService.withdrawConsent(consentModel);
		final long consentWithdrawnTimestamp = timeService.getCurrentTime().getTime();

		// Check that consentWithdrawn timestamp has been updated
		final ConsentModel withdrawnConsentModel = commerceConsentService.getActiveConsent(customer1, consentTemplateModel);
		assertNotNull(withdrawnConsentModel);
		assertEquals(consentModel.getCode(), withdrawnConsentModel.getCode());
		assertTrue((consentWithdrawnTimestamp - withdrawnConsentModel.getConsentWithdrawnDate().getTime()) < ONE_MINUTE_DELTA); // The consent was created within the last minute
	}

	@Test(expected = AccessDeniedException.class)
	public void testWithdrawConsentWhenConsentNotAssociatedWithUser()
	{
		final ConsentTemplateModel consentTemplateModel = commerceConsentService.getLatestConsentTemplate(CONSENT_TEMPLATE_1,
				currentBaseSite);
		final ConsentModel consentModel = commerceConsentService.getActiveConsent(customer1, consentTemplateModel);
		userService.setCurrentUser(customer2);

		// Withdraw consent
		commerceConsentService.withdrawConsent(consentModel);
	}

	@Test
	public void testUserDoesntHaveEffectivelyActiveConsentWhenNoConsentExists()
	{
		final ConsentTemplateModel consentTemplateModel = commerceConsentService.getLatestConsentTemplate(CONSENT_TEMPLATE_3,
				currentBaseSite);
		final ConsentModel consentModel = commerceConsentService.getActiveConsent(customer1, consentTemplateModel);
		assertNull(consentModel);

		assertFalse(commerceConsentService.hasEffectivelyActiveConsent(customer1, consentTemplateModel));
	}

	@Test
	public void testUserHasEffectivelyActiveConsentWhenConsentWasNotWithdrawnYet()
	{
		final ConsentTemplateModel consentTemplateModel = commerceConsentService.getLatestConsentTemplate(CONSENT_TEMPLATE_1,
				currentBaseSite);
		final ConsentModel consentModel = commerceConsentService.getActiveConsent(customer1, consentTemplateModel);
		assertNotNull(consentModel);
		assertNull(consentModel.getConsentWithdrawnDate());

		assertTrue(commerceConsentService.hasEffectivelyActiveConsent(customer1, consentTemplateModel));
	}

	@Test
	public void testUserDoesntHaveEffectivelyActiveConsentWhenConsentWasWithdrawn()
	{
		final ConsentTemplateModel consentTemplateModel = commerceConsentService.getLatestConsentTemplate(CONSENT_TEMPLATE_2,
				currentBaseSite);
		final ConsentModel consentModel = commerceConsentService.getActiveConsent(customer2, consentTemplateModel);
		assertNotNull(consentModel);
		assertNotNull(consentModel.getConsentWithdrawnDate());

		assertFalse(commerceConsentService.hasEffectivelyActiveConsent(customer2, consentTemplateModel));
	}

	private void testIdAndVersion(final ConsentTemplateModel consentTemplateModel, final String expectedId,
			final Integer expectedVersion)
	{
		assertEquals(expectedId, consentTemplateModel.getId());
		assertEquals(expectedVersion, consentTemplateModel.getVersion());
	}
}
