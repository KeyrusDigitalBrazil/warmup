/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commerceservices.consent.interceptors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.consent.dao.impl.DefaultConsentTemplateDao;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;



@IntegrationTest
public class DefaultConsentPrepareInterceptorTest extends ServicelayerTest
{
	private static final String CONSENT_TEMPLATE_ID = "CONSENT_TEMPLATE_1";
	private static final String TEST_BASESITE_UID = "testSite";
	private static final String TEST_CUSTOMER_UID = "testcustomer"; // must be lower case!
	@Resource
	private DefaultConsentTemplateDao defaultConsentTemplateDao;

	@Resource
	private UserService userService;

	@Resource
	private ModelService modelService;

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
	public void shouldVerifyWhetherConsentCodeIsGenerated()
	{
		final ConsentTemplateModel consentTemplate = defaultConsentTemplateDao
				.findLatestConsentTemplateByIdAndSite(CONSENT_TEMPLATE_ID, baseSite);
		assertNotNull("consentTemplate should not be null", consentTemplate);

		final ConsentModel consent = modelService.create(ConsentModel.class);
		consent.setConsentTemplate(consentTemplate);
		consent.setCustomer(customer);
		assertTrue("Consent Code should be blank", StringUtils.isBlank(consent.getCode()));
		modelService.save(consent);
		modelService.refresh(consent);
		assertTrue("Consent Code should not be blank", StringUtils.isNotBlank(consent.getCode()));
	}
}
