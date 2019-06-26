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
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.event.ConsentWithdrawnEvent;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.personalizationservices.RecalculateAction;
import de.hybris.platform.personalizationservices.action.CxActionResultService;
import de.hybris.platform.personalizationservices.configuration.CxConfigurationService;
import de.hybris.platform.personalizationservices.consent.CxConsentService;
import de.hybris.platform.personalizationservices.model.CxResultsModel;
import de.hybris.platform.personalizationservices.segment.CxUserSegmentService;
import de.hybris.platform.personalizationservices.service.CxCatalogService;
import de.hybris.platform.personalizationservices.service.CxRecalculationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;


@UnitTest
public class CxConsentWithdrawnEventListenerTest
{
	private static final String CUSTOMER_ID = "userId";

	private CxConsentWithdrawnEventListener listener;

	@Mock
	private CxConfigurationService configurationService;

	@Mock
	private CxConsentService consentService;

	@Mock
	private CxRecalculationService recalculationService;

	@Mock
	private CxUserSegmentService cxUserSegmentService;

	@Mock
	private CxActionResultService cxActionResultService;

	@Mock
	private CxCatalogService cxCatalogService;

	@Mock
	private ModelService modelService;

	@Mock
	private CxResultsModel cxResults;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private CustomerModel customer;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		listener = new CxConsentWithdrawnEventListener();
		listener.setCxConfigurationService(configurationService);
		listener.setCxConsentService(consentService);
		listener.setCxRecalculationService(recalculationService);
		listener.setCxUserSegmentService(cxUserSegmentService);
		listener.setCxActionResultService(cxActionResultService);
		listener.setCxCatalogService(cxCatalogService);
		listener.setModelService(modelService);

		when(customer.getUid()).thenReturn(CUSTOMER_ID);
		when(cxCatalogService.getConfiguredCatalogVersions()).thenReturn(Collections.singletonList(catalogVersion));
		when(cxActionResultService.getCxResults(customer, catalogVersion)).thenReturn(Optional.of(cxResults));
 	}

	@Test
	public void testNull()
	{
		//given
		final ConsentWithdrawnEvent event = null;

		//when
		listener.onApplicationEvent(event);

		//then
		verify(recalculationService, times(0)).recalculate(any(), anyList());
		verify(cxUserSegmentService, times(0)).setUserSegments(any(), anyList());
		verify(cxCatalogService, times(0)).getConfiguredCatalogVersions();
		verify(cxActionResultService, times(0)).getCxResults(any(), any());
		verify(modelService, times(0)).remove(any());
	}


	@Test
	public void testNoData()
	{
		//given
		final ConsentWithdrawnEvent event = new ConsentWithdrawnEvent();

		//when
		listener.onApplicationEvent(event);

		//then
		verify(recalculationService, times(0)).recalculate(any(), anyList());
		verify(cxUserSegmentService, times(0)).setUserSegments(any(), anyList());
		verify(cxCatalogService, times(0)).getConfiguredCatalogVersions();
		verify(cxActionResultService, times(0)).getCxResults(any(), any());
		verify(modelService, times(0)).remove(any());
	}

	@Test
	public void testWithdrawnConsent()
	{
		//given
		final ConsentModel consent = setupConsent(true);
		final ConsentWithdrawnEvent event = new ConsentWithdrawnEvent();
		event.setConsent(consent);

		//when
		listener.onApplicationEvent(event);

		//then
		verify(recalculationService, times(1)).recalculate(customer, Collections.singletonList(RecalculateAction.RECALCULATE));
		verify(cxUserSegmentService, times(1)).setUserSegments(customer, Collections.emptyList());
		verify(cxCatalogService, times(1)).getConfiguredCatalogVersions();
		verify(cxActionResultService, times(1)).getCxResults(customer, catalogVersion);
		verify(modelService, times(1)).remove(cxResults);
	}

	@Test
	public void testWithdrawnDifferentConsent()
	{
		//given
		final ConsentModel consent = setupConsent(false);
		final ConsentWithdrawnEvent event = new ConsentWithdrawnEvent();
		event.setConsent(consent);

		//when
		listener.onApplicationEvent(event);

		//then
		verify(recalculationService, times(0)).recalculate(any(), anyList());
		verify(cxUserSegmentService, times(0)).setUserSegments(any(), anyList());
		verify(cxCatalogService, times(0)).getConfiguredCatalogVersions();
		verify(cxActionResultService, times(0)).getCxResults(any(), any());
		verify(modelService, times(0)).remove(any());
	}

	@Test
	public void testWithdrawnConsentWhenNoCatalogVersion()
	{
		//given
		final ConsentModel consent = setupConsent(true);
		final ConsentWithdrawnEvent event = new ConsentWithdrawnEvent();
		event.setConsent(consent);
		when(cxCatalogService.getConfiguredCatalogVersions()).thenReturn(Collections.emptyList());

		//when
		listener.onApplicationEvent(event);

		//then
		verify(recalculationService, times(1)).recalculate(customer, Collections.singletonList(RecalculateAction.RECALCULATE));
		verify(cxUserSegmentService, times(1)).setUserSegments(customer, Collections.emptyList());
		verify(cxCatalogService, times(1)).getConfiguredCatalogVersions();
		verify(cxActionResultService, times(0)).getCxResults(any(), any());
		verify(modelService, times(0)).remove(any());
	}

	@Test
	public void testWithdrawnConsentWhenNoCxResults()
	{
		//given
		final ConsentModel consent = setupConsent(true);
		final ConsentWithdrawnEvent event = new ConsentWithdrawnEvent();
		event.setConsent(consent);
		when(cxActionResultService.getCxResults(customer, catalogVersion)).thenReturn(Optional.empty());

		//when
		listener.onApplicationEvent(event);

		//then
		verify(recalculationService, times(1)).recalculate(customer, Collections.singletonList(RecalculateAction.RECALCULATE));
		verify(cxUserSegmentService, times(1)).setUserSegments(customer, Collections.emptyList());
		verify(cxCatalogService, times(1)).getConfiguredCatalogVersions();
		verify(cxActionResultService, times(1)).getCxResults(customer, catalogVersion);
		verify(modelService, times(0)).remove(any());
	}

	private ConsentModel setupConsent(final boolean consentFromConfig)
	{
		final ConsentTemplateModel templateModel = new ConsentTemplateModel();
		templateModel.setId("testId");

		if (consentFromConfig)
		{
			doReturn(Sets.newHashSet(templateModel)).when(configurationService).getConsentTemplates();
		}
		else
		{
			doReturn(Collections.emptySet()).when(configurationService).getConsentTemplates();
		}

		final ConsentModel consent = new ConsentModel();
		consent.setConsentTemplate(templateModel);
		consent.setCustomer(customer);

		return consent;
	}
}
