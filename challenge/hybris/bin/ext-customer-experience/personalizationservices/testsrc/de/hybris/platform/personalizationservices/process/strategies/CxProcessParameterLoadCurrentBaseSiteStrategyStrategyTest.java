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


import static de.hybris.platform.personalizationservices.constants.PersonalizationservicesConstants.BASE_SITE_UID_CX_BUSINESS_PROCESS_PARAMETER;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.personalizationservices.model.process.CxPersonalizationProcessModel;
import de.hybris.platform.personalizationservices.process.strategies.impl.CxProcessParameterBaseSiteStrategy;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.site.BaseSiteService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@UnitTest
public class CxProcessParameterLoadCurrentBaseSiteStrategyStrategyTest extends BaseCxProcessParameterStrategyTest
{
	private static final String BASE_SITE_UID = "baseSiteUid";

	private final CxProcessParameterBaseSiteStrategy strategy = new CxProcessParameterBaseSiteStrategy();

	@Mock
	protected BaseSiteService baseSiteService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		strategy.setBaseSiteService(baseSiteService);
		strategy.setProcessParameterHelper(processParameterHelper);
	}

	@Test
	public void shouldLoadCurrentBaseSiteFromProcess()
	{
		//given
		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();

		final BusinessProcessParameterModel processParameter = createBusinessProcessParameterModel(BASE_SITE_UID_CX_BUSINESS_PROCESS_PARAMETER, BASE_SITE_UID);

		given(baseSiteService.getCurrentBaseSite()).willReturn(null);
		given(Boolean.valueOf(processParameterHelper.containsParameter(process, BASE_SITE_UID_CX_BUSINESS_PROCESS_PARAMETER))).willReturn(Boolean.TRUE);
		given(processParameterHelper.getProcessParameterByName(process, BASE_SITE_UID_CX_BUSINESS_PROCESS_PARAMETER)).willReturn(processParameter);

		//when
		strategy.load(process);

		//then
		verify(baseSiteService).setCurrentBaseSite(BASE_SITE_UID, false);
	}

	@Test
	public void shouldStoreCurrentBaseSiteInProcess()
	{
		//given
		final CxPersonalizationProcessModel process = new CxPersonalizationProcessModel();
		BaseSiteModel baseSite = new BaseSiteModel();
		baseSite.setUid(BASE_SITE_UID);
		
		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSite);

		//when
		strategy.store(process);

		//then
		verify(processParameterHelper).setProcessParameter(process, BASE_SITE_UID_CX_BUSINESS_PROCESS_PARAMETER, BASE_SITE_UID);
	}
}
