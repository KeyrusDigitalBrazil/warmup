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
package de.hybris.platform.personalizationservices.events.consent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.ConsentGivenEvent;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.personalizationservices.RecalculateAction;
import de.hybris.platform.personalizationservices.configuration.CxConfigurationService;
import de.hybris.platform.personalizationservices.consent.CxConsentService;
import de.hybris.platform.personalizationservices.service.CxRecalculationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


@UnitTest
public class CxConsentGivenEventListenerTest
{
	private CxConsentGivenEventListener listener;

	@Mock
	private CxConfigurationService configurationService;

	@Mock
	private CxConsentService consentService;

	@Mock
	private CxRecalculationService recalculationService;
	@Mock
	private UserService userService;

	@Mock
	private BaseSiteService baseSiteService;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		listener = new CxConsentGivenEventListener();
		listener.setCxConfigurationService(configurationService);
		listener.setCxConsentService(consentService);
		listener.setCxRecalculationService(recalculationService);
		listener.setBaseSiteService(baseSiteService);
		listener.setUserService(userService);
	}

	@Test
	public void testNull()
	{
		//given
		final ConsentGivenEvent event = null;

		//when
		listener.onApplicationEvent(event);

		//then
		verify(recalculationService, times(0)).recalculate(any(), anyList());
		verify(baseSiteService, times(0)).setCurrentBaseSite(any(BaseSiteModel.class), anyBoolean());
		verify(userService, times(0)).setCurrentUser(any());
	}


	@Test
	public void testNoData()
	{
		//given
		final ConsentGivenEvent event = new ConsentGivenEvent();

		//when
		listener.onApplicationEvent(event);

		//then
		verify(recalculationService, times(0)).recalculate(any(), anyList());
		verify(baseSiteService, times(0)).setCurrentBaseSite(any(BaseSiteModel.class), anyBoolean());
		verify(userService, times(0)).setCurrentUser(any());
	}

	@Test
	public void testGivenConsent()
	{
		//given
		final ConsentModel consent = setupConsentAndConfig(true, true, RecalculateAction.RECALCULATE);

		final ConsentGivenEvent event = new ConsentGivenEvent();
		event.setConsent(consent);

		//when
		listener.onApplicationEvent(event);

		//then
		verify(recalculationService, times(1)).recalculate(consent.getCustomer(),
				Lists.newArrayList(RecalculateAction.RECALCULATE));
		verify(baseSiteService, times(1)).setCurrentBaseSite(consent.getConsentTemplate().getBaseSite(), true);
		verify(userService, times(1)).setCurrentUser(consent.getCustomer());
	}

	@Test
	public void testGivenConsentInSession()
	{
		//given
		final ConsentModel consent = setupConsentAndConfig(true, true, RecalculateAction.RECALCULATE);
		doReturn(consent.getConsentTemplate().getBaseSite()).when(baseSiteService).getCurrentBaseSite();

		final ConsentGivenEvent event = new ConsentGivenEvent();
		event.setConsent(consent);

		//when
		listener.onApplicationEvent(event);

		//then
		verify(recalculationService, times(1)).recalculate(consent.getCustomer(),
				Lists.newArrayList(RecalculateAction.RECALCULATE));
		verify(baseSiteService, times(0)).setCurrentBaseSite(consent.getConsentTemplate().getBaseSite(), true);
		verify(userService, times(1)).setCurrentUser(consent.getCustomer());
	}

	@Test
	public void testDifferentGivenConsentInSession()
	{
		//given
		final ConsentModel consent = setupConsentAndConfig(true, false, RecalculateAction.RECALCULATE);
		doReturn(consent.getConsentTemplate().getBaseSite()).when(baseSiteService).getCurrentBaseSite();

		final ConsentGivenEvent event = new ConsentGivenEvent();
		event.setConsent(consent);

		//when
		listener.onApplicationEvent(event);

		//then
		verify(recalculationService, times(0)).recalculate(any(), anyList());
		verify(baseSiteService, times(0)).setCurrentBaseSite(any(BaseSiteModel.class), anyBoolean());
		verify(userService, times(0)).setCurrentUser(any());
	}

	@Test
	public void testNotGivenConsent()
	{
		//given
		final ConsentModel consent = setupConsentAndConfig(false, true, RecalculateAction.UPDATE);

		final ConsentGivenEvent event = new ConsentGivenEvent();
		event.setConsent(consent);

		//when
		listener.onApplicationEvent(event);

		//then
		verify(recalculationService, times(0)).recalculate(any(), anyList());
		verify(baseSiteService, times(0)).setCurrentBaseSite(any(BaseSiteModel.class), anyBoolean());
		verify(userService, times(0)).setCurrentUser(any());
	}

	private ConsentModel setupConsentAndConfig(final boolean activeConsent, final boolean consentFromConfig,
			final RecalculateAction... actions)
	{
		final String customerId = "userId";
		final CustomerModel customer = new CustomerModel();
		customer.setUid(customerId);

		doReturn(activeConsent).when(consentService).userHasActiveConsent(customer);

		final BaseSiteModel baseSite = new BaseSiteModel();
		baseSite.setUid("baseSiteUid");

		final Set<String> actionList = Stream.of(actions).map(Enum::toString).collect(Collectors.toSet());
		doReturn(actionList).when(configurationService).getConsentGivenActions(baseSite);

		final ConsentTemplateModel consentTemplate = new ConsentTemplateModel();
		consentTemplate.setBaseSite(baseSite);
		consentTemplate.setId("test");

		if (consentFromConfig)
		{
			doReturn(Sets.newHashSet(consentTemplate)).when(configurationService).getConsentTemplates();
		}
		else
		{
			doReturn(Collections.emptySet()).when(configurationService).getConsentTemplates();
		}

		final ConsentModel consent = new ConsentModel();
		consent.setConsentTemplate(consentTemplate);
		consent.setCustomer(customer);

		return consent;
	}

}
