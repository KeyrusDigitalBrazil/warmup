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
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.commerceservices.order.dao.impl.DefaultCommerceQuoteDao;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.site.BaseSiteService;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * JUnit test suite for {@link DefaultCommerceQuoteDao}
 */
@IntegrationTest
public class DefaultConsentTemplateDaoIntegrationTest extends ServicelayerTest
{
	private static final String TEST_BASESITE_UID = "testSite";
	private static final String TEST_BASESITE_UID2 = "testSite2";
	private static final String WRONG_BASESITE_UID = "wrongTestSite";

	private static final String CONSENT_TEMPLATE_ID_1 = "CONSENT_TEMPLATE_1";
	private static final String CONSENT_TEMPLATE_ID_2 = "CONSENT_TEMPLATE_2";
	private static final String CONSENT_TEMPLATE_ID_3 = "CONSENT_TEMPLATE_3";
	private static final String WRONG_CONSENT_TEMPLATE_ID = "WRONG_CONSENT_TEMPLATE_ID";

	private static final Integer VERSION_0 = Integer.valueOf(0);
	private static final Integer VERSION_2 = Integer.valueOf(2);

	@Resource
	private DefaultConsentTemplateDao defaultConsentTemplateDao;

	@Resource
	private BaseSiteService baseSiteService;

	private BaseSiteModel baseSite;

	@Before
	public void setUp() throws Exception
	{
		// importing test csv
		importCsv("/commerceservices/test/testConsents.impex", "utf-8");
		baseSite = baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID);
	}

	@Test
	public void shouldFindLatestConsentTemplateByIdAndSite()
	{
		final ConsentTemplateModel consentTemplate = defaultConsentTemplateDao
				.findLatestConsentTemplateByIdAndSite(CONSENT_TEMPLATE_ID_1, baseSite);
		assertEquals(2, consentTemplate.getVersion().intValue());
	}

	@Test(expected = ModelNotFoundException.class)
	public void shouldThrowExceptionWhenNoConsentTemplateFound()
	{
		defaultConsentTemplateDao.findLatestConsentTemplateByIdAndSite(WRONG_CONSENT_TEMPLATE_ID, baseSite);
	}

	@Test
	public void shouldFindConsentTemplateByIdAndVersionAndSite()
	{
		final ConsentTemplateModel consentTemplate = defaultConsentTemplateDao
				.findConsentTemplateByIdAndVersionAndSite(CONSENT_TEMPLATE_ID_1, VERSION_0, baseSite);
		assertNotNull(consentTemplate);
		assertEquals(VERSION_0, consentTemplate.getVersion());
	}

	@Test(expected = ModelNotFoundException.class)
	public void shouldThrowExceptionWhenNoConsentTemplateFoundForGivenIDVersionAndSite()
	{
		defaultConsentTemplateDao.findConsentTemplateByIdAndVersionAndSite(WRONG_CONSENT_TEMPLATE_ID, VERSION_0, baseSite);
	}

	@Test
	public void shouldFindLatestConsentTemplateForEachIdAndSite()
	{
		final List<ConsentTemplateModel> consentTemplates = defaultConsentTemplateDao.findConsentTemplatesBySite(baseSite);
		assertEquals(3, consentTemplates.size());

		final ConsentTemplateModel consentTemplate1 = consentTemplates.get(0);
		assertTrue(consentTemplate1.getId().equals(CONSENT_TEMPLATE_ID_1));
		assertTrue(consentTemplate1.getVersion().equals(VERSION_2));

		final ConsentTemplateModel consentTemplate2 = consentTemplates.get(1);
		assertTrue(consentTemplate2.getId().equals(CONSENT_TEMPLATE_ID_2));
		assertTrue(consentTemplate2.getVersion().equals(VERSION_2));

		final ConsentTemplateModel consentTemplate3 = consentTemplates.get(2);
		assertTrue(consentTemplate3.getId().equals(CONSENT_TEMPLATE_ID_3));
		assertTrue(consentTemplate3.getVersion().equals(Integer.valueOf(0)));
	}

	@Test
	public void shouldReturnEmptyListWhenNoConsentTemplatesFound()
	{
		final List<ConsentTemplateModel> consentTemplates = defaultConsentTemplateDao
				.findConsentTemplatesBySite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID2));
		assertTrue(consentTemplates.isEmpty());
	}

	@Test(expected = ModelNotFoundException.class)
	public void shouldNotFindConsentTemplateByWrongId()
	{
		defaultConsentTemplateDao.findLatestConsentTemplateByIdAndSite(WRONG_CONSENT_TEMPLATE_ID, baseSite);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindConsentTemplateByWrongSite()
	{
		baseSite = baseSiteService.getBaseSiteForUID(WRONG_BASESITE_UID);
		defaultConsentTemplateDao.findLatestConsentTemplateByIdAndSite(CONSENT_TEMPLATE_ID_1, baseSite);
	}
}
