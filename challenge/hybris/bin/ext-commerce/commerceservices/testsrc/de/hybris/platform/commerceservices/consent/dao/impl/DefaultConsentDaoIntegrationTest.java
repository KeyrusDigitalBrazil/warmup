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
package de.hybris.platform.commerceservices.consent.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.commerceservices.order.dao.impl.DefaultCommerceQuoteDao;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * JUnit test suite for {@link DefaultCommerceQuoteDao}
 */
@IntegrationTest
public class DefaultConsentDaoIntegrationTest extends ServicelayerTest
{
	private static final String CONSENT_TEMPLATE_ID = "CONSENT_TEMPLATE_1";
	private static final String WRONT_CONSENT_TEMPLATE_ID = "CONSENT_TEMPLATE_2";
	private static final String TEST_BASESITE_UID = "testSite";
	private static final String TEST_CUSTOMER_UID = "testcustomer"; // must be lower case!
	private static final String WRONG_CUSTOMER_UID = "testcustomer2";

	private static final DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");

	@Resource
	private DefaultConsentTemplateDao defaultConsentTemplateDao;

	@Resource
	private DefaultConsentDao defaultConsentDao;

	@Resource
	private UserService userService;

	@Resource
	private BaseSiteService baseSiteService;

	private BaseSiteModel baseSite;

	private CustomerModel customer;

	@Before
	public void setUp() throws Exception
	{
		// importing test csv
		importCsv("/commerceservices/test/testConsents.impex", "utf-8");
		baseSite = baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID);
		customer = userService.getUserForUID(TEST_CUSTOMER_UID, CustomerModel.class);
	}

	@Test
	public void shouldFindActiveConsentByCustomer() throws ParseException
	{
		final ConsentTemplateModel consentTemplate = defaultConsentTemplateDao
				.findLatestConsentTemplateByIdAndSite(CONSENT_TEMPLATE_ID, baseSite);
		final ConsentModel consent = defaultConsentDao.findConsentByCustomerAndConsentTemplate(customer, consentTemplate);
		assertEquals(2, consent.getConsentTemplate().getVersion().intValue());
		assertEquals(CONSENT_TEMPLATE_ID, consent.getConsentTemplate().getId());
		assertEquals("consent3", consent.getCode());
		assertEquals(TEST_CUSTOMER_UID, consent.getCustomer().getUid());

		final Date consentGivenDate = dateformat.parse("04-07-2017");
		assertEquals(consentGivenDate.compareTo(consent.getConsentGivenDate()), 0);
	}

	@Test
	public void shouldNotFindActiveConsentByWrongCustomer() throws ParseException
	{
		final ConsentTemplateModel consentTemplate = defaultConsentTemplateDao
				.findLatestConsentTemplateByIdAndSite(CONSENT_TEMPLATE_ID, baseSite);
		customer = userService.getUserForUID(WRONG_CUSTOMER_UID, CustomerModel.class);
		final ConsentModel consent = defaultConsentDao.findConsentByCustomerAndConsentTemplate(customer, consentTemplate);
		assertNull(consent);
	}

	@Test
	public void shouldNotFindActiveConsentByWrongConsentTemplate() throws ParseException
	{
		final ConsentTemplateModel consentTemplate = defaultConsentTemplateDao
				.findLatestConsentTemplateByIdAndSite(WRONT_CONSENT_TEMPLATE_ID, baseSite);
		final ConsentModel consent = defaultConsentDao.findConsentByCustomerAndConsentTemplate(customer, consentTemplate);
		assertNull(consent);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindActiveConsentifConsentTemplateisNull() throws ParseException
	{
		defaultConsentDao.findConsentByCustomerAndConsentTemplate(customer, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindActiveConsentifCustomerisNull() throws ParseException
	{
		defaultConsentDao.findConsentByCustomerAndConsentTemplate(null,
				defaultConsentTemplateDao.findLatestConsentTemplateByIdAndSite(CONSENT_TEMPLATE_ID, baseSite));
	}

	@Test
	public void shouldFindAllConsentsByCustomer() throws ParseException
	{
		final List<ConsentModel> consents = defaultConsentDao.findAllConsentsByCustomer(customer);
		assertNotNull("No consents found", consents);
		assertEquals(8, consents.size());

		ConsentModel oldConsent = consents.get(0);
		for (final ConsentModel consent : consents)
		{
			// make sure all consents are from the same customer
			assertEquals(TEST_CUSTOMER_UID, consent.getCustomer().getUid());

			// make sure all consents are ordered by given date DESC (can have same date or before, not after)
			assertTrue(!consent.getConsentGivenDate().after(oldConsent.getConsentGivenDate()));
			oldConsent = consent;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindAllConsentsByCustomerIfCustomerisNull() throws ParseException
	{
		defaultConsentDao.findAllConsentsByCustomer(null);
	}
}
