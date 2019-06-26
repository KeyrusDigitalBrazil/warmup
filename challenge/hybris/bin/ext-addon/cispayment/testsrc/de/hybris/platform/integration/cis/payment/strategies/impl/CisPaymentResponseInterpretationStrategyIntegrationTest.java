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
package de.hybris.platform.integration.cis.payment.strategies.impl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hybris.charon.RawResponse;
import com.hybris.cis.service.CisClientPaymentService;
import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;
import de.hybris.platform.acceleratorservices.payment.enums.DecisionsEnum;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.site.BaseSiteService;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


@ManualTest
public class CisPaymentResponseInterpretationStrategyIntegrationTest extends ServicelayerTest
{
	private static final String TEST_CLIENT_REF = "JUNIT-TEST-CLIENT";

	@Resource
	private CisPaymentResponseInterpretationStrategy cisPaymentResponseInterpretation;
	@Resource
	private CisClientPaymentService cisClientPaymentService;
	@Resource
	private BaseSiteService baseSiteService;

	private String hpfUrl;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/test/cisTestAcceleratorData.csv", "UTF-8");
		final BaseSiteModel site = baseSiteService.getBaseSiteForUID("testSite");
		assertNotNull("no baseSite with uid 'testSite", site);
		site.setChannel(SiteChannel.B2C);
		baseSiteService.setCurrentBaseSite(site, false);

		final RawResponse<String> hpfUrlResponse = cisClientPaymentService.pspUrl("test", Registry.getCurrentTenant().getTenantID());
		hpfUrl = hpfUrlResponse.header("location").get();
	}

	@Test
	public void testValidPaymentResponseInterpretation() throws Exception
	{
		final List<BasicNameValuePair> validFormData = CisPaymentIntegrationTestHelper.getValidFormDataMap();
		final Map<String, String> profileCreationResponse = CisPaymentIntegrationTestHelper.createNewProfile(hpfUrl, validFormData);
		final Map<String, PaymentErrorField> errors = new HashMap<String, PaymentErrorField>();

		final CreateSubscriptionResult subscriptionResult = cisPaymentResponseInterpretation
				.interpretResponse(profileCreationResponse, TEST_CLIENT_REF, errors);
		assertNotNull(subscriptionResult);
		assertNotNull(subscriptionResult.getDecision());
		assertNotNull(subscriptionResult.getReasonCode());

		assertEquals(DecisionsEnum.ACCEPT.name(), subscriptionResult.getDecision());
	}

	@Test
	public void testMissingPaymentDetailsResponseInterpretation() throws Exception
	{
		final Map<String, PaymentErrorField> errors = new HashMap<String, PaymentErrorField>();

		final List<BasicNameValuePair> formDataMissingDetails = CisPaymentIntegrationTestHelper.getFormDataMapMissingDetails();
		final Map<String, String> profileCreationResponse = CisPaymentIntegrationTestHelper.createNewProfile(hpfUrl,
				formDataMissingDetails);

		final CreateSubscriptionResult subscriptionResult = cisPaymentResponseInterpretation
				.interpretResponse(profileCreationResponse, TEST_CLIENT_REF, errors);
		assertNotNull(subscriptionResult);
		assertNotNull(subscriptionResult.getDecision());
		assertNotNull(subscriptionResult.getReasonCode());

		assertEquals(DecisionsEnum.REJECT.name(), subscriptionResult.getDecision());
	}
}
