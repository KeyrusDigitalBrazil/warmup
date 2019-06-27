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
/**
 *
 */
package de.hybris.platform.personalizationservices.configuration.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.personalizationservices.model.config.CxConfigModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CxConfigValidationInterceptorTest
{
	private final CxConfigValidationInterceptor interceptor = new CxConfigValidationInterceptor();
	private final CxConfigModel apparelCxConfig = new CxConfigModel();

	@Mock
	private InterceptorContext interceptorContext;

	@Mock
	private BaseSiteModel apparelBaseSite;

	@Mock
	private BaseSiteModel electronicsBaseSite;

	@Mock
	private ConsentTemplateModel apparelConsentTemplate1;

	@Mock
	private ConsentTemplateModel apparelConsentTemplate2;

	@Mock
	private ConsentTemplateModel electronicsConsentTemplate;


	@Before
	public void setupMocks()
	{
		MockitoAnnotations.initMocks(this);

		BDDMockito.given(apparelConsentTemplate1.getBaseSite()).willReturn(apparelBaseSite);
		BDDMockito.given(apparelConsentTemplate2.getBaseSite()).willReturn(apparelBaseSite);
		BDDMockito.given(electronicsConsentTemplate.getBaseSite()).willReturn(electronicsBaseSite);

		final HashSet<BaseSiteModel> baseSiteSet = new HashSet();
		baseSiteSet.add(apparelBaseSite);
		apparelCxConfig.setBaseSites(baseSiteSet);
	}

	@Test
	public void shouldNotThrowExceptionWhenBaseSitesMatch() throws InterceptorException
	{
		final HashSet<ConsentTemplateModel> consentTemplateSet = new HashSet();
		consentTemplateSet.add(apparelConsentTemplate1);
		consentTemplateSet.add(apparelConsentTemplate2);
		apparelCxConfig.setConsentTemplates(consentTemplateSet);

		interceptor.onValidate(apparelCxConfig, interceptorContext);
	}

	@Test(expected = InterceptorException.class)
	public void shouldThrowExceptionWhenBaseSitesDoNotMatch() throws InterceptorException
	{
		final HashSet<ConsentTemplateModel> consentTemplateSet = new HashSet();
		consentTemplateSet.add(electronicsConsentTemplate);
		apparelCxConfig.setConsentTemplates(consentTemplateSet);

		interceptor.onValidate(apparelCxConfig, interceptorContext);
	}
}
