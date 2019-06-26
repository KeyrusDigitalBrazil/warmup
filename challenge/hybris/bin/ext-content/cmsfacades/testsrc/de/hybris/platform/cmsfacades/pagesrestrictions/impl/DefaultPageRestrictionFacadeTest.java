/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.pagesrestrictions.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminRestrictionService;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.data.PageRestrictionData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.factory.ErrorFactory;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.tx.MockTransactionManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.Errors;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPageRestrictionFacadeTest
{

	private static final String PAGE_ID_1 = "page-id-1";
	private static final String PAGE_ID_2 = "page-id-2";
	private static final String RESTRICTION_ID_1 = "restriction-id-1";
	private static final String RESTRICTION_ID_2 = "restriction-id-2";

	@InjectMocks
	private DefaultPageRestrictionFacade pageRestrictionFacade;

	@Mock
	private CMSAdminPageService adminPageService;
	@Mock
	private CMSAdminRestrictionService adminRestrictionService;
	@Mock
	private FacadeValidationService facadeValidationService;
	@Mock
	private ErrorFactory errorFactory;
	@Mock
	private AbstractPageModel page1;
	@Mock
	private AbstractPageModel page2;
	@Mock
	private AbstractRestrictionModel restriction1;
	@Mock
	private AbstractRestrictionModel restriction2;
	@Mock
	private PageRestrictionData pageRetrictionData;
	@Mock
	private CatalogVersionModel catalogVersion;

	private PlatformTransactionManager transactionManager;

	@Before
	public void setUp() throws CMSItemNotFoundException
	{
		when(page1.getUid()).thenReturn(PAGE_ID_1);
		when(page2.getUid()).thenReturn(PAGE_ID_2);
		when(restriction1.getUid()).thenReturn(RESTRICTION_ID_1);
		when(restriction2.getUid()).thenReturn(RESTRICTION_ID_2);

		when(adminPageService.getPageForIdFromActiveCatalogVersion(PAGE_ID_1)).thenReturn(page1);
		when(adminPageService.getActiveCatalogVersion()).thenReturn(catalogVersion);

		when(adminRestrictionService.getRestrictionsForPage(page1)).thenReturn(Arrays.asList(restriction1, restriction2));
		when(adminRestrictionService.getRestrictionsForPage(page2)).thenReturn(Arrays.asList(restriction2));

		transactionManager = new MockTransactionManager();
		pageRestrictionFacade.setTransactionManager(transactionManager);
	}

	@Test
	public void shouldGetRestrictionsByPage() throws CMSItemNotFoundException
	{
		final List<PageRestrictionData> result = pageRestrictionFacade.getRestrictionsByPage(PAGE_ID_1);
		assertThat(result.size(), is(2));
		assertThat(result.get(0).getPageId(), is(PAGE_ID_1));
		assertThat(result.get(0).getRestrictionId(), is(RESTRICTION_ID_1));
		assertThat(result.get(1).getPageId(), is(PAGE_ID_1));
		assertThat(result.get(1).getRestrictionId(), is(RESTRICTION_ID_2));
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void shouldNotGetRestrictionsByPage_PageNotFound() throws CMSItemNotFoundException
	{
		when(adminPageService.getPageForIdFromActiveCatalogVersion(PAGE_ID_1)).thenThrow(new UnknownIdentifierException("test"));
		pageRestrictionFacade.getRestrictionsByPage(PAGE_ID_1);
		fail();
	}

	@Test
	public void shouldGetAllPagesRestrictions()
	{
		when(adminPageService.getAllPages()).thenReturn(Arrays.asList(page1, page2));
		when(adminRestrictionService.getRestrictionsForPage(page2)).thenReturn(Arrays.asList(restriction2));

		final List<PageRestrictionData> result = pageRestrictionFacade.getAllPagesRestrictions();
		assertThat(result.size(), is(3));
		assertThat(result.get(0).getPageId(), is(PAGE_ID_1));
		assertThat(result.get(0).getRestrictionId(), is(RESTRICTION_ID_1));
		assertThat(result.get(1).getPageId(), is(PAGE_ID_1));
		assertThat(result.get(1).getRestrictionId(), is(RESTRICTION_ID_2));
		assertThat(result.get(2).getPageId(), is(PAGE_ID_2));
		assertThat(result.get(2).getRestrictionId(), is(RESTRICTION_ID_2));
	}

	@Test
	public void shouldUpdateRestrictionRelationsByPage() throws AmbiguousIdentifierException, CMSItemNotFoundException
	{
		when(adminPageService.getPageForIdFromActiveCatalogVersionByPageStatuses(PAGE_ID_1,
				Arrays.asList(CmsPageStatus.ACTIVE))).thenReturn(page1);
		when(page1.getDefaultPage()).thenReturn(Boolean.TRUE);
		doNothing().when(facadeValidationService).validate(any(), any());
		when(pageRetrictionData.getPageId()).thenReturn(PAGE_ID_1);
		when(pageRetrictionData.getRestrictionId()).thenReturn(RESTRICTION_ID_1);

		when(adminRestrictionService.getRestrictionsForPage(page1)).thenReturn(Arrays.asList(restriction1, restriction2));
		doNothing().when(adminRestrictionService).deleteRelation(restriction1, page1);
		doNothing().when(adminRestrictionService).deleteRelation(restriction2, page1);

		when(adminRestrictionService.getRestriction(RESTRICTION_ID_1)).thenReturn(restriction1);

		pageRestrictionFacade.updateRestrictionRelationsByPage(PAGE_ID_1, Arrays.asList(pageRetrictionData));

		verify(adminRestrictionService, times(2)).deleteRelation(any(AbstractRestrictionModel.class), any(AbstractPageModel.class));
		verify(adminRestrictionService).createRelation(page1, restriction1);
	}

	@Test(expected = ValidationException.class)
	public void shouldFailUpdateRestrictionRelationsByPage_NoRestrictionOnVariationPage() throws CMSItemNotFoundException
	{
		when(adminPageService.getPageForIdFromActiveCatalogVersionByPageStatuses(PAGE_ID_1, Arrays.asList(CmsPageStatus.ACTIVE)))
				.thenReturn(page1);
		when(page1.getDefaultPage()).thenReturn(Boolean.FALSE);
		final Errors errors = mock(Errors.class);
		when(errorFactory.createInstance(page1)).thenReturn(errors);

		pageRestrictionFacade.updateRestrictionRelationsByPage(PAGE_ID_1, Collections.emptyList());
	}

}
