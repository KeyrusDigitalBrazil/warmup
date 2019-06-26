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
package de.hybris.platform.commercefacades.consent.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.consent.data.ConsentTemplateData;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultConsentFacadeTest
{
	public static final String consentId1 = "consentId1";

	@Mock
	private UserService userService;

	@Mock
	private CommerceConsentService commerceConsentService;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private Converter<ConsentTemplateModel, ConsentTemplateData> consentTemplateConverter;

	@Mock
	private BaseSiteModel baseSite;

	@Mock
	private CustomerModel customerModel;

	@Mock
	private ConsentModel consentModel;

	@Mock
	private ConsentTemplateModel consentTemplateModel1, consentTemplateModel2;

	@Mock
	private ConsentTemplateData consentTemplateData1, consentTemplateData2;

	@Spy
	@InjectMocks
	private DefaultConsentFacade consentFacade;

	@Before
	public void setup()
	{
		doReturn(customerModel).when(userService).getCurrentUser();
		doReturn(baseSite).when(baseSiteService).getCurrentBaseSite();
		doReturn(consentTemplateModel1).when(commerceConsentService).getLatestConsentTemplate(consentId1, baseSite);
		doReturn(consentTemplateData1).when(consentTemplateConverter).convert(consentTemplateModel1);
		doReturn(consentTemplateData2).when(consentTemplateConverter).convert(consentTemplateModel2);
	}

	@Test
	public void testGetConsent()
	{
		final ConsentTemplateData retrievedConsentModel = consentFacade.getLatestConsentTemplate(consentId1);

		assertEquals(consentTemplateData1, retrievedConsentModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShouldNotGetConsentIfConsentTemplateIdIsNull()
	{
		consentFacade.getLatestConsentTemplate(null);
	}

	@Test
	public void testGetUserConsents()
	{
		doReturn(Arrays.asList(consentTemplateModel1, consentTemplateModel2)).when(commerceConsentService)
				.getConsentTemplates(baseSite);

		final List<ConsentTemplateData> retrievedConsents = consentFacade.getConsentTemplatesWithConsents();

		assertEquals(2, retrievedConsents.size());
		assertTrue(retrievedConsents.contains(consentTemplateData1));
		assertTrue(retrievedConsents.contains(consentTemplateData2));
	}

	@Test
	public void testGiveConsent()
	{
		final Integer version = Integer.valueOf(10);
		doReturn(version).when(consentTemplateModel1).getVersion();
		doReturn(consentTemplateModel1).when(commerceConsentService).getConsentTemplate(consentId1, version, baseSite);

		consentFacade.giveConsent(consentId1, version);

		verify(commerceConsentService).getConsentTemplate(consentId1, version, baseSite);
		verify(commerceConsentService).giveConsent(customerModel, consentTemplateModel1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShouldNotGiveConsentIfConsentTemplateIdIsNull()
	{
		consentFacade.giveConsent(null, Integer.valueOf(10));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShouldNotGiveConsentIfConsentTemplateVersionIsNull()
	{
		consentFacade.giveConsent(consentId1, null);
	}

	@Test
	public void testWithdrawConsent()
	{
		doReturn(consentModel).when(commerceConsentService).getConsent(consentId1);

		consentFacade.withdrawConsent(consentId1);

		verify(commerceConsentService).getConsent(consentId1);
		verify(commerceConsentService).withdrawConsent(consentModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShouldNotWithdrawConsentIfConsentCodeIsNull()
	{
		consentFacade.withdrawConsent(null);
	}
}
