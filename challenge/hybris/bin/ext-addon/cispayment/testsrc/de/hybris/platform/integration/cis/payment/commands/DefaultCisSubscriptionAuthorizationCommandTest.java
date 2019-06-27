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
package de.hybris.platform.integration.cis.payment.commands;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hybris.charon.RawResponse;
import com.hybris.cis.service.CisClientPaymentService;
import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.integration.cis.payment.strategies.impl.CisPaymentIntegrationTestHelper;
import de.hybris.platform.integration.cis.payment.strategies.impl.CisPaymentResponseInterpretationStrategy;
import de.hybris.platform.payment.commands.SubscriptionAuthorizationCommand;
import de.hybris.platform.payment.commands.factory.CommandFactory;
import de.hybris.platform.payment.commands.factory.CommandFactoryRegistry;
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.testframework.TestUtils;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


@ManualTest
public class DefaultCisSubscriptionAuthorizationCommandTest extends ServicelayerTest
{
	private static final String TEST_CLIENT_REF = "JUNIT-TEST-CLIENT";

	@Resource
	private CisClientPaymentService cisClientPaymentService;

	@Resource
	private CommandFactoryRegistry commandFactoryRegistry;

	@Resource
	private CisPaymentResponseInterpretationStrategy cisPaymentResponseInterpretation;

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
		hpfUrl = hpfUrlResponse.header("location").get().replaceAll("https", "http");
	}

	@Test
	public void shouldAuthorize() throws Exception
	{
		//here we create a profile through Cybersource SOP, this profile will be used for the test authorizations
		final List<BasicNameValuePair> validFormData = CisPaymentIntegrationTestHelper.getValidFormDataMap();
		final Map<String, String> profileCreationResponse = CisPaymentIntegrationTestHelper.createNewProfile(hpfUrl, validFormData);

		final CreateSubscriptionResult subscriptionResult = getCreateSubscriptionResult(TEST_CLIENT_REF, profileCreationResponse);

		final BillingInfo billingInfo = createBillingInfo();
		final SubscriptionAuthorizationRequest request = new SubscriptionAuthorizationRequest("order1234",
				subscriptionResult.getSubscriptionInfoData().getSubscriptionID(), Currency.getInstance("USD"), BigDecimal.TEN,
				billingInfo, "cisCybersource");

		final CommandFactory commandFactory = commandFactoryRegistry.getFactory("cisCybersource");
		final SubscriptionAuthorizationCommand command = commandFactory.createCommand(SubscriptionAuthorizationCommand.class);
		final AuthorizationResult authResult = command.perform(request);
		assertEquals(TransactionStatus.ACCEPTED, authResult.getTransactionStatus());
	}

	@Test
	public void shouldAuthorizeAndGetRejected() throws Exception
	{
		//here we create a profile through Cybersource SOP, this profile will be used for the test authorizations
		final List<BasicNameValuePair> validFormData = CisPaymentIntegrationTestHelper.getValidFormDataMap();
		final Map<String, String> profileCreationResponse = CisPaymentIntegrationTestHelper.createNewProfile(hpfUrl, validFormData);
		final CreateSubscriptionResult subscriptionResult = getCreateSubscriptionResult(TEST_CLIENT_REF, profileCreationResponse);

		final BillingInfo billingInfo = createBillingInfo();
		final SubscriptionAuthorizationRequest request = new SubscriptionAuthorizationRequest(
				String.valueOf(System.currentTimeMillis()), subscriptionResult.getSubscriptionInfoData().getSubscriptionID(),
				Currency.getInstance("USD"), BigDecimal.valueOf(1500D), billingInfo, "cisCybersource");

		final CommandFactory commandFactory = commandFactoryRegistry.getFactory("cisCybersource");
		final SubscriptionAuthorizationCommand command = commandFactory.createCommand(SubscriptionAuthorizationCommand.class);

		final AuthorizationResult authResult;
		try
		{
			TestUtils.disableFileAnalyzer(
					"expected exception ServiceRequestException: 4015");
			authResult = command.perform(request);
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
		assertEquals(TransactionStatus.REJECTED, authResult.getTransactionStatus());
	}

	private CreateSubscriptionResult getCreateSubscriptionResult(final String testClientRef,
			final Map<String, String> profileCreationResponse)
	{
		final CreateSubscriptionResult subscriptionResult = cisPaymentResponseInterpretation
				.interpretResponse(profileCreationResponse, testClientRef, new HashMap<String, PaymentErrorField>());
		assertEquals("ACCEPT", subscriptionResult.getDecision());
		assertNotNull(subscriptionResult.getSubscriptionInfoData());
		assertNotNull(subscriptionResult.getSubscriptionInfoData().getSubscriptionID());
		return subscriptionResult;
	}

	private BillingInfo createBillingInfo()
	{
		final BillingInfo billingInfo = new BillingInfo();
		billingInfo.setCity("New York");
		billingInfo.setCountry("US");
		billingInfo.setEmail("test@hybris.com");
		billingInfo.setFirstName("test");
		billingInfo.setLastName("test");
		billingInfo.setPhoneNumber("1234567890");
		billingInfo.setPostalCode("10019");
		billingInfo.setState("NY");
		billingInfo.setStreet1("1700 Broadway");
		billingInfo.setStreet2("26th floor");
		return billingInfo;
	}
}
