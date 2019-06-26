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
package de.hybris.platform.personalizationservices.process.strategies;


import static de.hybris.platform.personalizationservices.constants.PersonalizationservicesConstants.SESSION_TOKEN;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationservices.model.process.CxPersonalizationProcessModel;
import de.hybris.platform.personalizationservices.process.strategies.impl.CxProcessParameterSessionTokenStrategy;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.session.impl.DefaultSessionTokenService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CxProcessParameterLoadSessionTokenKeyStrategyStrategyTest extends BaseCxProcessParameterStrategyTest
{
	private static final String SESSION_TOKEN_KEY = "sessionTokenKey";

	private final CxProcessParameterSessionTokenStrategy strategy = new CxProcessParameterSessionTokenStrategy();

	@Mock
	protected DefaultSessionTokenService defaultSessionTokenService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		strategy.setProcessParameterHelper(processParameterHelper);
		strategy.setDefaultSessionTokenService(defaultSessionTokenService);
		BDDMockito.given(defaultSessionTokenService.getOrCreateSessionToken()).willReturn(SESSION_TOKEN_KEY);
	}

	@Test
	public void shouldLoadSessionTokenFromProcess()
	{
		//given
		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();
		final BusinessProcessParameterModel processParameter = createBusinessProcessParameterModel(SESSION_TOKEN, SESSION_TOKEN_KEY);

		given(Boolean.valueOf(processParameterHelper.containsParameter(process, SESSION_TOKEN))).willReturn(Boolean.TRUE);
		given(processParameterHelper.getProcessParameterByName(process, SESSION_TOKEN)).willReturn(processParameter);

		//when
		strategy.load(process);

		//then
		verify(defaultSessionTokenService).setSessionToken(SESSION_TOKEN_KEY);
	}


	@Test
	public void shouldStoreSessionTokenInProcess()
	{
		//given
		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();
		given(defaultSessionTokenService.getOrCreateSessionToken()).willReturn(SESSION_TOKEN_KEY);


		//when
		strategy.store(process);

		//then
		verify(processParameterHelper).setProcessParameter(process, SESSION_TOKEN, SESSION_TOKEN_KEY);
	}
}
