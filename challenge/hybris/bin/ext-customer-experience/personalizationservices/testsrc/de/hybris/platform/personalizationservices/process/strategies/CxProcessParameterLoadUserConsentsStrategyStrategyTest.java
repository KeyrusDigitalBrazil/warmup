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


import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationservices.model.process.CxPersonalizationProcessModel;
import de.hybris.platform.personalizationservices.process.strategies.impl.CxProcessParameterUserConsentsStrategy;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.session.SessionService;


@UnitTest
public class CxProcessParameterLoadUserConsentsStrategyStrategyTest extends BaseCxProcessParameterStrategyTest
{
	private static final String SESSION_CONSENTS = "user-consents";
	private static Map<String, String> sessionConsentMap;
	private final CxProcessParameterUserConsentsStrategy strategy = new CxProcessParameterUserConsentsStrategy();

	@Mock
	private SessionService sessionService;

	@Before
	public void setUp()
	{
		sessionConsentMap = new HashMap<>(Collections.singletonMap("consent", "GIVEN"));
		MockitoAnnotations.initMocks(this);
		strategy.setProcessParameterHelper(processParameterHelper);
		strategy.setSessionService(sessionService);
		BDDMockito.given(sessionService.getAttribute(SESSION_CONSENTS)).willReturn(sessionConsentMap);
	}

	@Test
	public void shouldLoadUserConsentsFromProcess()
	{
		//given
		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();
		final BusinessProcessParameterModel processParameter = createBusinessProcessParameterModel(SESSION_CONSENTS,
				sessionConsentMap);

		given(Boolean.valueOf(processParameterHelper.containsParameter(process, SESSION_CONSENTS))).willReturn(Boolean.TRUE);
		given(processParameterHelper.getProcessParameterByName(process, SESSION_CONSENTS)).willReturn(processParameter);

		//when
		strategy.load(process);

		//then
		verify(sessionService).setAttribute(SESSION_CONSENTS, sessionConsentMap);
	}


	@Test
	public void shouldStoreUserConsentsInProcess()
	{
		//given
		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();
		given(sessionService.getAttribute(SESSION_CONSENTS)).willReturn(sessionConsentMap);

		//when
		strategy.store(process);

		//then
		verify(processParameterHelper).setProcessParameter(process, SESSION_CONSENTS, sessionConsentMap);
	}

	@Test
	public void shouldNotStoreUserConsentsInProcess()
	{
		//given
		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();
		given(sessionService.getAttribute(SESSION_CONSENTS)).willReturn(null);

		//when
		strategy.store(process);

		//then
		verify(processParameterHelper, times(0)).setProcessParameter(process, SESSION_CONSENTS, sessionConsentMap);
	}
}
