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
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.assistedserviceservices.utils.AssistedServiceSession;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserTypeIdentificationStrategy;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AssistedServiceQuoteUserIdentificationStrategyTest
{
	private static final String TEST_UID = "testUId";

	@InjectMocks
	AssistedServiceQuoteUserIdentificationStrategy assistedServiceQuoteUserIdentificationStrategy = new AssistedServiceQuoteUserIdentificationStrategy();

	@Mock
	private AssistedServiceService assistedServiceService;
	@Mock
	private QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;
	@Mock
	private UserService userService;

	private UserModel customer;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		customer = new UserModel();
		given(userService.getCurrentUser()).willReturn(customer);
	}

	@Test
	public void shouldNotGetASMAgentAsCurrentUserIfNoASMSession()
	{
		assertEquals(customer, assistedServiceQuoteUserIdentificationStrategy.getCurrentQuoteUser());
	}

	@Test
	public void shouldNotGetASMAgentAsCurrentUserIfNoASMAgentInSession()
	{
		final AssistedServiceSession asmSession = new AssistedServiceSession();
		given(assistedServiceService.getAsmSession()).willReturn(asmSession);
		assertEquals(customer, assistedServiceQuoteUserIdentificationStrategy.getCurrentQuoteUser());
	}

	@Test
	public void shouldNotGetASMAgentAsCurrentUserIfOnlyASAgentIsInSession()
	{
		final AssistedServiceSession asmSession = mock(AssistedServiceSession.class);
		final UserModel agent = new UserModel();
		agent.setUid(TEST_UID);
		when(asmSession.getAgent()).thenReturn(agent);
		given(assistedServiceService.getAsmSession()).willReturn(asmSession);
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(any())).willReturn(Optional.empty());
		assertEquals(customer, assistedServiceQuoteUserIdentificationStrategy.getCurrentQuoteUser());
	}

	@Test
	public void shouldGetASMAgentAsCurrentUserIfSalesRepIsInSession()
	{
		final AssistedServiceSession asmSession = mock(AssistedServiceSession.class);
		final UserModel agent = new UserModel();
		agent.setUid(TEST_UID);
		when(asmSession.getAgent()).thenReturn(agent);
		given(assistedServiceService.getAsmSession()).willReturn(asmSession);
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(any())).willReturn(Optional.of(QuoteUserType.SELLER));
		assertEquals(TEST_UID, assistedServiceQuoteUserIdentificationStrategy.getCurrentQuoteUser().getUid());
	}

	@Test
	public void shouldGetASMAgentAsCurrentUserIfSellerApproverIsInSession()
	{
		final AssistedServiceSession asmSession = mock(AssistedServiceSession.class);
		final UserModel agent = new UserModel();
		agent.setUid(TEST_UID);
		when(asmSession.getAgent()).thenReturn(agent);
		given(assistedServiceService.getAsmSession()).willReturn(asmSession);
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(any())).willReturn(
				Optional.of(QuoteUserType.SELLERAPPROVER));
		assertEquals(TEST_UID, assistedServiceQuoteUserIdentificationStrategy.getCurrentQuoteUser().getUid());
	}
}
