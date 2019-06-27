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
package de.hybris.platform.personalizationyprofile.strategy.impl;


import static de.hybris.platform.personalizationyprofile.constants.PersonalizationyprofileConstants.CONSENT_REFERENCE_SESSION_ATTR_KEY;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationservices.model.process.CxPersonalizationProcessModel;
import de.hybris.platform.personalizationservices.process.strategies.BaseCxProcessParameterStrategyTest;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.session.SessionService;


@UnitTest
public class CxProcessParameterLoadConsentReferenceStrategyStrategyTest extends BaseCxProcessParameterStrategyTest
{
	private static final String CONSENT_REFERENCE_KEY = "consentReferenceKey";

	private final CxProcessParameterConsentReferenceStrategy strategy = new CxProcessParameterConsentReferenceStrategy();

	@Mock
	private SessionService sessionService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		strategy.setProcessParameterHelper(processParameterHelper);
		strategy.setSessionService(sessionService);
		BDDMockito.given(sessionService.getAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY)).willReturn(CONSENT_REFERENCE_KEY);
	}

	@Test
	public void shouldLoadConsentReferenceFromProcess()
	{
		//given
		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();
		final BusinessProcessParameterModel processParameter = createBusinessProcessParameterModel(CONSENT_REFERENCE_SESSION_ATTR_KEY, CONSENT_REFERENCE_KEY);

		given(Boolean.valueOf(processParameterHelper.containsParameter(process, CONSENT_REFERENCE_SESSION_ATTR_KEY))).willReturn(Boolean.TRUE);
		given(processParameterHelper.getProcessParameterByName(process, CONSENT_REFERENCE_SESSION_ATTR_KEY)).willReturn(processParameter);

		//when
		strategy.load(process);

		//then
		verify(sessionService).setAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY,CONSENT_REFERENCE_KEY);
	}


	@Test
	public void shouldStoreConsentReferenceInProcess()
	{
		//given
		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();
		given(sessionService.getAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY)).willReturn(CONSENT_REFERENCE_KEY);

		//when
		strategy.store(process);

		//then
		verify(processParameterHelper).setProcessParameter(process, CONSENT_REFERENCE_SESSION_ATTR_KEY, CONSENT_REFERENCE_KEY);
	}

	@Test
	public void shouldNotStoreConsentReferenceInProcess()
	{
		//given
		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();
		given(sessionService.getAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY)).willReturn(null);

		//when
		strategy.store(process);

		//then
		verify(processParameterHelper,times(0)).setProcessParameter(process, CONSENT_REFERENCE_SESSION_ATTR_KEY, CONSENT_REFERENCE_KEY);
	}
}
