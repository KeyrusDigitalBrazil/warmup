/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.assistedserviceservices.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants;
import de.hybris.platform.assistedserviceservices.utils.AssistedServiceSession;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class AssistedServiceQuoteUserIdentificationStrategyIntegrationTest extends ServicelayerTest
{
	private static final String CUSTOMER_UID = "customer@ichikawa.com";
	private static final String ASAGENT = "asagent";
	private static final String SELLER_APPROVER_UID = "salesapprover@ichikawa.com";
	private static final String SALES_REP_UID = "salesemployee@nakano.com";

	@Resource
	private AssistedServiceQuoteUserIdentificationStrategy assistedServiceQuoteUserIdentificationStrategy;

	@Resource
	private SessionService sessionService;

	@Resource
	private UserService userService;

	@Before
	public void setup() throws Exception
	{
		importCsv("/assistedserviceservices/test/agents.impex", "UTF-8");

		userService.setCurrentUser(userService.getUserForUID(CUSTOMER_UID));
	}

	@Test
	public void shouldGetSalesRepAsCurrentQuoteUser()
	{
		final AssistedServiceSession asmSession = new AssistedServiceSession();

		asmSession.setAgent(userService.getUserForUID(SALES_REP_UID));
		sessionService.setAttribute(AssistedserviceservicesConstants.ASM_SESSION_PARAMETER, asmSession);

		final UserModel currentQuoteUser = assistedServiceQuoteUserIdentificationStrategy.getCurrentQuoteUser();
		assertNotNull("currentQuoteUser", currentQuoteUser);
		assertEquals(SALES_REP_UID, currentQuoteUser.getUid());
	}

	@Test
	public void shouldGetSalesApproverAsCurrentQuoteUser()
	{
		final AssistedServiceSession asmSession = new AssistedServiceSession();

		asmSession.setAgent(userService.getUserForUID(SELLER_APPROVER_UID));
		sessionService.setAttribute(AssistedserviceservicesConstants.ASM_SESSION_PARAMETER, asmSession);

		final UserModel currentQuoteUser = assistedServiceQuoteUserIdentificationStrategy.getCurrentQuoteUser();
		assertNotNull("currentQuoteUser", currentQuoteUser);
		assertEquals(SELLER_APPROVER_UID, currentQuoteUser.getUid());
	}

	@Test
	public void shouldGetCustomerAsCurrentQuoteUserIfOnlyASAgentIsInSession()
	{
		final AssistedServiceSession asmSession = new AssistedServiceSession();

		asmSession.setAgent(userService.getUserForUID(ASAGENT));
		sessionService.setAttribute(AssistedserviceservicesConstants.ASM_SESSION_PARAMETER, asmSession);

		final UserModel currentQuoteUser = assistedServiceQuoteUserIdentificationStrategy.getCurrentQuoteUser();
		assertNotNull("currentQuoteUser", currentQuoteUser);
		assertEquals(CUSTOMER_UID, currentQuoteUser.getUid());
	}

	@Test
	public void shouldGetCustomerAsCurrentQuoteUser()
	{
		sessionService.setAttribute(AssistedserviceservicesConstants.ASM_SESSION_PARAMETER, null);

		final UserModel currentQuoteUser = assistedServiceQuoteUserIdentificationStrategy.getCurrentQuoteUser();
		assertNotNull("currentQuoteUser", currentQuoteUser);
		assertEquals(CUSTOMER_UID, currentQuoteUser.getUid());
	}
}
