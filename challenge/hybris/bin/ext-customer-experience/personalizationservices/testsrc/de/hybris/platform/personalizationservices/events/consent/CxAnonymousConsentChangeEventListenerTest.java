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

import static de.hybris.platform.personalizationservices.RecalculateAction.RECALCULATE;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.event.AnonymousConsentChangeEvent;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.personalizationservices.RecalculateAction;
import de.hybris.platform.personalizationservices.action.CxActionResultService;
import de.hybris.platform.personalizationservices.configuration.CxConfigurationService;
import de.hybris.platform.personalizationservices.consent.CxConsentService;
import de.hybris.platform.personalizationservices.model.CxResultsModel;
import de.hybris.platform.personalizationservices.service.CxCatalogService;
import de.hybris.platform.personalizationservices.service.CxRecalculationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;


@UnitTest
public class CxAnonymousConsentChangeEventListenerTest
{
	private static final String TEMPLATE_ID = "template";

	private CxAnonymousConsentChangeEventListener listener;

	@Mock
	private CxConfigurationService configurationService;

	@Mock
	private CxConsentService consentService;

	@Mock
	private CxRecalculationService recalculationService;

	@Mock
	private SessionService sessionService;

	@Mock
	private UserService userService;

	@Mock
	private CxActionResultService cxActionResultService;

	@Mock
	private CxCatalogService cxCatalogService;

	@Mock
	private ModelService modelService;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private CxResultsModel cxResults;

	@Mock
	private CustomerModel anonymousUser;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		listener = new CxAnonymousConsentChangeEventListener();
		listener.setCxConfigurationService(configurationService);
		listener.setCxConsentService(consentService);
		listener.setCxRecalculationService(recalculationService);
		listener.setSessionService(sessionService);
		listener.setUserService(userService);
		listener.setCxActionResultService(cxActionResultService);
		listener.setCxCatalogService(cxCatalogService);
		listener.setModelService(modelService);

		final ConsentTemplateModel templateModel = new ConsentTemplateModel();
		templateModel.setId(TEMPLATE_ID);

		doReturn(Sets.newHashSet(templateModel)).when(configurationService).getConsentTemplates();

		when(userService.getAnonymousUser()).thenReturn(anonymousUser);

		when(cxCatalogService.getConfiguredCatalogVersions()).thenReturn(Collections.singletonList(catalogVersion));

		when(cxActionResultService.getCxResults(anonymousUser, catalogVersion)).thenReturn(Optional.of(cxResults));
 	}

	@Test
	public void testNull()
	{
		//given
		final AnonymousConsentChangeEvent event = null;

		//when
		listener.onApplicationEvent(event);

		//then
		verify(recalculationService, times(0)).recalculate(anyList());
	}


	@Test
	public void testNotGivenConsent()
	{
		//given
		final AnonymousConsentChangeEvent event = getEvent(false);

		//when
		listener.onApplicationEvent(event);

		//then
		verify(recalculationService, times(0)).recalculate(anyList());
	}

	@Test
	public void testGivenConsent()
	{
		//given
		final AnonymousConsentChangeEvent event = getEvent(true);

		doReturn(true).when(consentService).userHasActiveConsent(anonymousUser);

		final List<RecalculateAction> actionList = Arrays.asList(RECALCULATE);
		final Set<String> actionSet = actionList.stream().map(a -> a.toString()).collect(Collectors.toSet());
		doReturn(actionSet).when(configurationService).getConsentGivenActions();

		//when
		listener.onApplicationEvent(event);

		//then
		verify(recalculationService, times(1)).recalculate(actionList);
	}

	@Test
	public void testDifferentConsentGiven()
	{
		//given
		final AnonymousConsentChangeEvent event = getEvent(true);

		doReturn(true).when(consentService).userHasActiveConsent(anonymousUser);

		final ConsentTemplateModel templateModel = new ConsentTemplateModel();
		templateModel.setId("different");

		doReturn(Sets.newHashSet(templateModel)).when(configurationService).getConsentTemplates();

		final List<RecalculateAction> actionList = Arrays.asList(RECALCULATE);
		final Set<String> actionSet = actionList.stream().map(a -> a.toString()).collect(Collectors.toSet());
		doReturn(actionSet).when(configurationService).getConsentGivenActions();

		//when
		listener.onApplicationEvent(event);

		//then
		verify(recalculationService, times(0)).recalculate(anyList());
	}

	@Test
	public void testWithdrawnConsent()
	{
		//given
		final AnonymousConsentChangeEvent event = getEvent(false);

		doReturn(false).when(consentService).userHasActiveConsent(anonymousUser);

		//when
		listener.onApplicationEvent(event);

		//then
		verify(cxCatalogService, times(1)).getConfiguredCatalogVersions();
		verify(cxActionResultService, times(1)).getCxResults(anonymousUser, catalogVersion);
		verify(modelService, times(1)).remove(cxResults);
		verify(recalculationService, times(1)).recalculate(anonymousUser, Collections.singletonList(RecalculateAction.RECALCULATE));
	}

	@Test
	public void testWithdrawnConsentWhenNoCatalogVersion()
	{
		//given
		final AnonymousConsentChangeEvent event = getEvent(false);

		doReturn(false).when(consentService).userHasActiveConsent(anonymousUser);
		when(cxCatalogService.getConfiguredCatalogVersions()).thenReturn(Collections.emptyList());

		//when
		listener.onApplicationEvent(event);

		//then
		verify(cxCatalogService, times(1)).getConfiguredCatalogVersions();
		verify(cxActionResultService, times(0)).getCxResults(anonymousUser, catalogVersion);
		verify(modelService, times(0)).remove(cxResults);
		verify(recalculationService, times(1)).recalculate(anonymousUser, Collections.singletonList(RecalculateAction.RECALCULATE));
	}

	@Test
	public void testWithdrawnConsentWhenNoCxResults()
	{
		//given
		final AnonymousConsentChangeEvent event = getEvent(false);

		doReturn(false).when(consentService).userHasActiveConsent(anonymousUser);
		when(cxActionResultService.getCxResults(anonymousUser, catalogVersion)).thenReturn(Optional.empty());


		//when
		listener.onApplicationEvent(event);

		//then
		verify(cxCatalogService, times(1)).getConfiguredCatalogVersions();
		verify(cxActionResultService, times(1)).getCxResults(anonymousUser, catalogVersion);
		verify(modelService, times(0)).remove(cxResults);
		verify(recalculationService, times(1)).recalculate(anonymousUser, Collections.singletonList(RecalculateAction.RECALCULATE));
 	}

	AnonymousConsentChangeEvent getEvent(final boolean given)
	{
		if (given)
		{
			return new AnonymousConsentChangeEvent(TEMPLATE_ID, "", "GIVEN", java.util.Collections.emptyMap());
		}
		else
		{
			return new AnonymousConsentChangeEvent(TEMPLATE_ID, "GIVEN", "WITHDRAWN", java.util.Collections.emptyMap());

		}
	}
}
