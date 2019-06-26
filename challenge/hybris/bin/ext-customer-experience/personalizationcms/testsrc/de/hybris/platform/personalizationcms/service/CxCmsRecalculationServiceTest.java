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
package de.hybris.platform.personalizationcms.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.misc.CMSFilter;
import de.hybris.platform.cms2.model.preview.CMSPreviewTicketModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.RecalculateAction;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.personalizationservices.service.CxCatalogService;
import de.hybris.platform.personalizationservices.service.CxService;
import de.hybris.platform.personalizationservices.trigger.dao.CxSegmentTriggerDao;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CxCmsRecalculationServiceTest
{
	private static String PREVIEW_TICKET_ID = "previewTicketId";
	private final CxCmsRecalculationService service = new CxCmsRecalculationService();

	@Mock
	private CxService cxService;
	@Mock
	private SessionService sessionService;
	@Mock
	CMSPreviewService cmsPreviewService;
	@Mock
	CxSegmentTriggerDao cxSegmentTriggerDao;

	@Mock
	private UserModel user;
	@Mock
	private CatalogVersionModel cxCatalogVersion1;
	@Mock
	private CatalogVersionModel cxCatalogVersion2;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private CxVariationModel variation1;
	@Mock
	private CxVariationModel variation2;
	@Mock
	private CxCustomizationModel customization;
	@Mock
	private CxSegmentModel segment;
	@Mock
	private PreviewDataModel previewData;
	@Mock
	private CMSPreviewTicketModel ticketModel;
	@Mock
	private CxCatalogService cxCatalogService;

	@Before
	public void initMocks()
	{
		MockitoAnnotations.initMocks(this);
		service.setCxService(cxService);
		service.setCxCatalogService(cxCatalogService);
		service.setSessionService(sessionService);
		service.setCmsPreviewService(cmsPreviewService);
		service.setCxSegmentTriggerDao(cxSegmentTriggerDao);

		when(Boolean.valueOf(cxCatalogService.isPersonalizationInCatalog(catalogVersion))).thenReturn(Boolean.FALSE);
		when(Boolean.valueOf(cxCatalogService.isPersonalizationInCatalog(cxCatalogVersion1))).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(cxCatalogService.isPersonalizationInCatalog(cxCatalogVersion2))).thenReturn(Boolean.TRUE);
		when(ticketModel.getPreviewData()).thenReturn(previewData);
		when(sessionService.getAttribute(CMSFilter.PREVIEW_TICKET_ID_PARAM)).thenReturn(PREVIEW_TICKET_ID);
		when(cmsPreviewService.getPreviewTicket(PREVIEW_TICKET_ID)).thenReturn(ticketModel);
		when(variation1.getCustomization()).thenReturn(customization);
		when(variation1.getCatalogVersion()).thenReturn(cxCatalogVersion1);
		when(variation2.getCustomization()).thenReturn(customization);
		when(variation2.getCatalogVersion()).thenReturn(cxCatalogVersion1);
		when(customization.getRank()).thenReturn(Integer.valueOf(1));
	}

	@Test
	public void recalculateForPreviewDataWithVariationsTest()
	{
		//given
		when(previewData.getCatalogVersions()).thenReturn(Arrays.asList(cxCatalogVersion1, catalogVersion));
		when(previewData.getVariations()).thenReturn(Arrays.asList(variation1, variation2));

		//when
		service.recalculate(user, Collections.singletonList(RecalculateAction.RECALCULATE));

		//then
		verify(cxService, times(0)).calculateAndLoadPersonalizationInSession(same(user), same(catalogVersion), anyList());
		final ArgumentCaptor<Collection> argument = ArgumentCaptor.forClass(Collection.class);
		verify(cxService, times(1)).calculateAndLoadPersonalizationInSession(same(user), same(cxCatalogVersion1),
				argument.capture());
		final Collection variationList = argument.getValue();
		assertTrue(variationList.contains(variation1));
		assertTrue(variationList.contains(variation2));
	}

	@Test
	public void recalculateForPreviewDataWithSegmentsTest()
	{
		//given
		when(previewData.getCatalogVersions()).thenReturn(Arrays.asList(cxCatalogVersion1, catalogVersion));
		final Collection<CxSegmentModel> segments = Collections.singleton(segment);
		when(previewData.getSegments()).thenReturn(segments);
		when(cxSegmentTriggerDao.findApplicableVariations(segments, cxCatalogVersion1))
				.thenReturn(Arrays.asList(variation1, variation2));

		//when
		service.recalculate(user, Collections.singletonList(RecalculateAction.RECALCULATE));

		//then
		verify(cxService, times(0)).calculateAndLoadPersonalizationInSession(same(user), same(catalogVersion), anyList());
		final ArgumentCaptor<Collection> argument = ArgumentCaptor.forClass(Collection.class);
		verify(cxService, times(1)).calculateAndLoadPersonalizationInSession(same(user), same(cxCatalogVersion1),
				argument.capture());
		final Collection variationList = argument.getValue();
		assertTrue(variationList.contains(variation1));
		assertTrue(variationList.contains(variation2));
	}

	@Test
	public void recalculateForPreviewDataWithoutVariationsAndSegmentsTest()
	{
		//given
		when(previewData.getCatalogVersions()).thenReturn(Arrays.asList(cxCatalogVersion1, cxCatalogVersion2, catalogVersion));

		//when
		service.recalculate(user, Collections.singletonList(RecalculateAction.RECALCULATE));

		//then
		verify(cxService, times(1)).clearPersonalizationInSession(user, cxCatalogVersion1);
		verify(cxService, times(1)).clearPersonalizationInSession(user, cxCatalogVersion2);
		verify(cxService, times(0)).clearPersonalizationInSession(user, catalogVersion);
	}
}
