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
package de.hybris.platform.commercefacades.consent.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.consent.ConsentFacade;
import de.hybris.platform.commercefacades.consent.data.ConsentData;
import de.hybris.platform.commercefacades.consent.data.ConsentTemplateData;
import de.hybris.platform.commerceservices.consent.exceptions.CommerceConsentGivenException;
import de.hybris.platform.commerceservices.consent.exceptions.CommerceConsentWithdrawnException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * JUnit test suite for {@link DefaultConsentFacade}
 */
@IntegrationTest
public class DefaultConsentFacadeIntegrationTest extends ServicelayerTest
{
	private static final String CURRENT_USER_UID = "testcustomer";

	private static final String TEST_BASESITE_UID = "testSite";
	private static final String TEST_BASESITE2_UID = "testSite2";

	private static final String CONSENT_TEMPLATE_1 = "CONSENT_TEMPLATE_1";
	private static final String CONSENT_TEMPLATE_3 = "CONSENT_TEMPLATE_3";
	private static final String CONSENT_TEMPLATE_2 = "CONSENT_TEMPLATE_2";
	private static final String CONSENT_TEMPLATE_NON_EXISTING = "CONSENT_TEMPLATE_7";

	private static final Integer VERSION_0 = Integer.valueOf(0);
	private static final Integer VERSION_2 = Integer.valueOf(2);
	private static final Integer VERSION_NON_EXISTING = Integer.valueOf(100);

	private static final String CONSENT_3 = "consent3";
	private static final String CONSENT_NON_EXISTING = "consent100";

	private static final int ONE_MINUTE_DELTA = 60000;

	@Resource
	private UserService userService;

	@Resource
	private TimeService timeService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private ConsentFacade consentFacade;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/commerceservices/test/testConsents.impex", "utf-8");
		userService.setCurrentUser(userService.getUserForUID(CURRENT_USER_UID));
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
	}

	@Test
	public void getLatestConsentTemplateShouldFindConsentTemplate() throws ParseException
	{
		final ConsentTemplateData consentTemplateData = consentFacade.getLatestConsentTemplate(CONSENT_TEMPLATE_1);

		testIdAndVersion(consentTemplateData, CONSENT_TEMPLATE_1, VERSION_2);
		assertEquals(CONSENT_3, consentTemplateData.getConsentData().getCode());
	}

	@Test(expected = ModelNotFoundException.class)
	public void getLatestConsentTemplateShouldThrowException() throws ParseException
	{
		consentFacade.getLatestConsentTemplate(CONSENT_TEMPLATE_NON_EXISTING);
	}

	@Test
	public void getConsentsShouldReturnThreeConsentTemplates() throws ParseException
	{
		final List<ConsentTemplateData> consentTemplateDataList = consentFacade.getConsentTemplatesWithConsents();

		assertEquals(3, consentTemplateDataList.size());

		final ConsentTemplateData consentTemplateData1 = consentTemplateDataList.get(0);
		testIdAndVersion(consentTemplateData1, CONSENT_TEMPLATE_1, VERSION_2);
		assertEquals(CONSENT_3, consentTemplateData1.getConsentData().getCode());

		final ConsentTemplateData consentTemplateData2 = consentTemplateDataList.get(1);
		testIdAndVersion(consentTemplateData2, CONSENT_TEMPLATE_2, VERSION_2);
		assertNull(consentTemplateData2.getConsentData());

		final ConsentTemplateData consentTemplateData3 = consentTemplateDataList.get(2);
		testIdAndVersion(consentTemplateData3, CONSENT_TEMPLATE_3, VERSION_0);
		assertNull(consentTemplateData3.getConsentData());
	}

	@Test
	public void getConsentsShouldReturnAnEmptyList() throws ParseException
	{
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE2_UID), false);

		final List<ConsentTemplateData> consentTemplateDataList = consentFacade.getConsentTemplatesWithConsents();

		assertEquals(0, consentTemplateDataList.size());
	}

	@Test
	public void testGivenAndWithdrawConsentWorkflow() throws ParseException
	{
		// Check there are no Consents given for ConsentTemplate: CONSENT_TEMPLATE_3
		assertNull(getConsentData(CONSENT_TEMPLATE_3, consentFacade.getConsentTemplatesWithConsents()));

		// Give consent --> new Consent entity should be created
		consentFacade.giveConsent(CONSENT_TEMPLATE_3, VERSION_0);
		final long consentGivenTimestamp = timeService.getCurrentTime().getTime();

		// Check that new consent was created with set consentGiven timestamp
		final ConsentData addedConsentData = getConsentData(CONSENT_TEMPLATE_3, consentFacade.getConsentTemplatesWithConsents());
		assertNotNull(addedConsentData);
		assertTrue((consentGivenTimestamp - addedConsentData.getConsentGivenDate().getTime()) < ONE_MINUTE_DELTA); // The consent was created within the last minute
		assertNull(addedConsentData.getConsentWithdrawnDate());

		// Try giving the consent again --> we expect the exception
		boolean consentGivenExceptionThrown = false;
		try
		{
			consentFacade.giveConsent(CONSENT_TEMPLATE_3, VERSION_0);
		}
		catch (final CommerceConsentGivenException e)
		{
			consentGivenExceptionThrown = true;
		}
		assertTrue(consentGivenExceptionThrown);

		// Check that no new consent was added
		final ConsentData existingConsent = getConsentData(CONSENT_TEMPLATE_3, consentFacade.getConsentTemplatesWithConsents());
		assertNotNull(addedConsentData);
		assertEquals(addedConsentData.getCode(), existingConsent.getCode());
		assertEquals(addedConsentData.getConsentGivenDate(), existingConsent.getConsentGivenDate());
		assertEquals(addedConsentData.getConsentWithdrawnDate(), existingConsent.getConsentWithdrawnDate());

		// Withdraw the consent --> existing Consent entity should be updated
		consentFacade.withdrawConsent(existingConsent.getCode());
		final long consentWithdrawnTimestamp = timeService.getCurrentTime().getTime();

		// Check that consent was withdrawn
		final ConsentData withdrawnConsent = getConsentData(CONSENT_TEMPLATE_3, consentFacade.getConsentTemplatesWithConsents());
		assertNotNull(withdrawnConsent);
		assertEquals(existingConsent.getCode(), withdrawnConsent.getCode());
		assertTrue((consentWithdrawnTimestamp - withdrawnConsent.getConsentWithdrawnDate().getTime()) < ONE_MINUTE_DELTA); // The consent was created within the last minute

		// Attempt to withdraw the consent again --> CommerceConsentWithdrawnException is thrown
		boolean isCommerceConsentWithdrawnException = false;
		try
		{
			consentFacade.withdrawConsent(existingConsent.getCode());
		}
		catch (final CommerceConsentWithdrawnException e)
		{
			isCommerceConsentWithdrawnException = true;
		}
		assertTrue(
				"CommerceConsentWithdrawnException was not thrown when user tried to withdraw a consent that was already withdrawn",
				isCommerceConsentWithdrawnException);

		// Check that nothing happened (check consentWithdrawn timestamp)
		final ConsentData withdrawnConsent2 = getConsentData(CONSENT_TEMPLATE_3, consentFacade.getConsentTemplatesWithConsents());
		assertNotNull(withdrawnConsent2);
		assertEquals(withdrawnConsent2.getCode(), withdrawnConsent2.getCode());
		assertEquals(withdrawnConsent.getConsentWithdrawnDate(), withdrawnConsent2.getConsentWithdrawnDate());

		// Give consent again --> New Consent entity should be created
		consentFacade.giveConsent(CONSENT_TEMPLATE_3, VERSION_0);

		// Check that new consent was added on top of the withdrawn one
		final ConsentData newConsent = getConsentData(CONSENT_TEMPLATE_3, consentFacade.getConsentTemplatesWithConsents());
		assertNotNull(addedConsentData);
		assertNotEquals(withdrawnConsent.getCode(), newConsent.getCode());
	}

	@Test(expected = ModelNotFoundException.class)
	public void testGivenConsentForNonExistingConsentTemplateId()
	{
		consentFacade.giveConsent(CONSENT_TEMPLATE_NON_EXISTING, VERSION_0);
	}

	@Test(expected = ModelNotFoundException.class)
	public void testGivenConsentForNonExistingConsentTemplateVersion()
	{
		consentFacade.giveConsent(CONSENT_TEMPLATE_3, VERSION_NON_EXISTING);
	}

	@Test(expected = ModelNotFoundException.class)
	public void testWithdrawConsentForNonExistingConsent()
	{
		consentFacade.withdrawConsent(CONSENT_NON_EXISTING);
	}

	protected ConsentData getConsentData(final String consentTemplateId, final List<ConsentTemplateData> consentTemplateDataList)
	{
		return consentTemplateDataList.stream().filter(consentTemplateData -> consentTemplateId.equals(consentTemplateData.getId()))
				.findFirst().map(ConsentTemplateData::getConsentData).orElse(null);
	}

	protected void testIdAndVersion(final ConsentTemplateData consentTemplateData, final String expectedId,
			final Integer expectedVersion)
	{
		assertEquals(expectedId, consentTemplateData.getId());
		assertEquals(expectedVersion, consentTemplateData.getVersion());
	}
}
