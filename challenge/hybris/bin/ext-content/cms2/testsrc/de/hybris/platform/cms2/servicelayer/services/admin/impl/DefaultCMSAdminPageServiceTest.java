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
package de.hybris.platform.cms2.servicelayer.services.admin.impl;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.multicountry.comparator.CMSItemCatalogLevelComparator;
import de.hybris.platform.cms2.servicelayer.daos.CMSPageDao;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultCMSAdminPageServiceTest
{
	private static final List<CmsPageStatus> SUCCESS_STATUS = Arrays.asList(CmsPageStatus.ACTIVE);
	private static final String FAQ_PAGE_UID = "cool-uid";
	private static final String INVALID = "invalid";
	private static final String REGIONAL_VERSION = "regionalStaged";
	private static final String REGIONAL_CATALOG = "regionalContentCatalog";
	private static final String TEST_PAGE_TYPE = "testPageType";

	@Spy
	@InjectMocks
	private DefaultCMSAdminPageService pageService;

	@Mock
	private CMSPageDao cmsPageDao;
	@Mock
	private TypeService typeService;
	@Mock
	private CMSItemCatalogLevelComparator cmsItemCatalogLevelComparator;
	@Mock
	private CMSItemCatalogLevelComparator reversedComparator;

	@Mock
	private ComposedTypeModel composedTypeModel;
	@Mock
	private CMSPageTypeModel pageType1;
	@Mock
	private CMSPageTypeModel pageType2;

	@Mock
	private ContentPageModel globalFaqPage;
	@Mock
	private ContentPageModel regionalFaqPage;
	@Mock
	private ContentPageModel localFaqPage;
	@Mock
	private ContentPageModel termsPage;
	@Mock
	private ContentPageModel globalHomepage;
	@Mock
	private ContentPageModel regionalHomepage;

	@Mock
	private CatalogVersionModel globalCatalogVersionStaged;
	@Mock
	private CatalogVersionModel globalCatalogVersionOnline;
	@Mock
	private CatalogVersionModel regionalCatalogVersionStaged;
	@Mock
	private CatalogVersionModel regionalCatalogVersionOnline;
	@Mock
	private CatalogVersionModel localCatalogVersionOnline;
	@Mock
	private CatalogVersionModel localCatalogVersionStaged;

	@Mock
	private ContentCatalogModel globalContentCatalog;
	@Mock
	private ContentCatalogModel regionalContentCatalog;
	@Mock
	private ContentCatalogModel localContentCatalog;
	@Mock
	private PermissionCRUDService permissionCRUDService;

	@Mock
	private CMSSiteModel cmsSite;

	@Before
	public void setUp()
	{
		when(typeService.getComposedTypeForClass(AbstractPageModel.class)).thenReturn(composedTypeModel);
		when(composedTypeModel.getAllSubTypes()).thenReturn(Arrays.asList(pageType1, pageType2));
		when(pageType1.getCode()).thenReturn(TEST_PAGE_TYPE);
		when(pageType2.getCode()).thenReturn(TEST_PAGE_TYPE);

		when(permissionCRUDService.canReadType(pageType1)).thenReturn(true);
		when(permissionCRUDService.canReadType(pageType2)).thenReturn(false);

		when(cmsItemCatalogLevelComparator.reversed()).thenReturn(reversedComparator);

		when(regionalContentCatalog.getName()).thenReturn(REGIONAL_CATALOG);
		when(regionalCatalogVersionStaged.getVersion()).thenReturn(REGIONAL_VERSION);
	}

	@Test
	public void shouldGetAllPageTypes()
	{
		final Collection<CMSPageTypeModel> pageTypes = pageService.getAllPageTypes();
		assertThat(pageTypes, containsInAnyOrder(pageType1));
	}

	@Test
	public void shouldGetPageTypeByCode()
	{
		final Optional<CMSPageTypeModel> pageType = pageService.getPageTypeByCode(TEST_PAGE_TYPE);
		assertThat(pageType.isPresent(), is(true));
		assertThat(pageType.get(), is(pageType1));
	}

	@Test
	public void shouldNotGetPageTypeByInvalidCode()
	{
		final Optional<CMSPageTypeModel> pageType = pageService.getPageTypeByCode(INVALID);
		assertThat(pageType.isPresent(), is(false));
	}

	@Test
	public void shouldGetHomepageForSite()
	{
		when(cmsSite.getContentCatalogs()).thenReturn(Arrays.asList(globalContentCatalog));
		when(globalContentCatalog.getActiveCatalogVersion()).thenReturn(globalCatalogVersionOnline);

		when(globalHomepage.isHomepage()).thenReturn(true);
		when(cmsPageDao.findAllContentPagesByCatalogVersionsAndPageStatuses(Arrays.asList(globalCatalogVersionOnline),
				SUCCESS_STATUS)).thenReturn(Arrays.asList(globalFaqPage, termsPage, globalHomepage));

		final ContentPageModel result = pageService.getHomepage(cmsSite);

		assertThat(result, equalTo(globalHomepage));
	}

	@Test
	public void shouldGetHomepageForMultiCountryCatalogs()
	{
		final List<CatalogVersionModel> catalogVersions = Arrays.asList(globalCatalogVersionOnline, regionalCatalogVersionStaged);
		when(cmsPageDao.findAllContentPagesByCatalogVersionsAndPageStatuses(catalogVersions, SUCCESS_STATUS))
				.thenReturn(Arrays.asList(regionalFaqPage, termsPage, regionalHomepage));
		when(regionalHomepage.isHomepage()).thenReturn(true);

		final ContentPageModel result = pageService.getHomepage(catalogVersions);

		assertThat(result, equalTo(regionalHomepage));
	}

	@Test
	public void shouldGetHomepageForCatalogVersions()
	{
		final List<CatalogVersionModel> catalogVersions = Arrays.asList(localCatalogVersionStaged, localCatalogVersionOnline);

		when(globalHomepage.isHomepage()).thenReturn(true);
		when(cmsPageDao.findAllContentPagesByCatalogVersionsAndPageStatuses(catalogVersions, SUCCESS_STATUS))
				.thenReturn(Arrays.asList(localFaqPage, termsPage, globalHomepage));

		final ContentPageModel result = pageService.getHomepage(catalogVersions);

		assertThat(result, equalTo(globalHomepage));
	}

	@Test
	public void shouldGetNullHomepageForCatalogVersions()
	{
		final List<CatalogVersionModel> catalogVersions = Arrays.asList(localCatalogVersionStaged, localCatalogVersionOnline);

		when(cmsPageDao.findAllContentPagesByCatalogVersions(catalogVersions)).thenReturn(Arrays.asList(localFaqPage, termsPage));

		final ContentPageModel result = pageService.getHomepage(catalogVersions);

		assertThat(result, is(nullValue()));
	}

	@Test
	public void shouldGetAllActiveContentPages()
	{
		final CatalogVersionModel catalogVersionStaged = mock(CatalogVersionModel.class);
		final CatalogVersionModel catalogVersionOnline = mock(CatalogVersionModel.class);
		final List<CatalogVersionModel> catalogVersions = Arrays.asList(catalogVersionStaged, catalogVersionOnline);

		final ContentPageModel faqPage = mock(ContentPageModel.class);
		final ContentPageModel termsPage = mock(ContentPageModel.class);

		when(cmsPageDao.findAllContentPagesByCatalogVersionsAndPageStatuses(catalogVersions, SUCCESS_STATUS))
				.thenReturn(Arrays.asList(faqPage, termsPage));

		when(reversedComparator.compare(faqPage, termsPage)).thenReturn(1);
		when(reversedComparator.compare(termsPage, faqPage)).thenReturn(-1);

		final Collection<ContentPageModel> pages = pageService.getAllContentPagesForPageStatuses(catalogVersions, SUCCESS_STATUS);

		Assert.assertThat(pages, equalTo(Arrays.asList(termsPage, faqPage)));

		verify(cmsPageDao).findAllContentPagesByCatalogVersionsAndPageStatuses(catalogVersions, SUCCESS_STATUS);
	}

	@Test
	public void shouldGetAllActivePagesForCatalogVersionAndPageStatuses()
	{
		final CatalogVersionModel catalogVersionStaged = mock(CatalogVersionModel.class);

		final ContentPageModel faqPage = mock(ContentPageModel.class);
		final ContentPageModel termsPage = mock(ContentPageModel.class);

		when(cmsPageDao.findAllPagesByCatalogVersionAndPageStatuses(catalogVersionStaged, SUCCESS_STATUS))
				.thenReturn(Arrays.asList(faqPage, termsPage));

		pageService.getAllPagesForCatalogVersionAndPageStatuses(catalogVersionStaged, SUCCESS_STATUS);
		verify(cmsPageDao).findAllPagesByCatalogVersionAndPageStatuses(catalogVersionStaged, SUCCESS_STATUS);
	}

	@Test
	public void shouldGetPageForIdFromLocalCatalog()
	{
		when(cmsPageDao.findPagesByIdAndPageStatuses(any(), any(), any())).thenReturn(Arrays.asList(localFaqPage));

		final AbstractPageModel result = pageService.getPageForId(FAQ_PAGE_UID, Arrays.asList(localCatalogVersionStaged),
				SUCCESS_STATUS);

		assertThat(result, equalTo(localFaqPage));
	}

	@Test
	public void shouldGetPageForIdFromParentCatalog()
	{
		when(cmsSite.getContentCatalogs())
				.thenReturn(Arrays.asList(globalContentCatalog, regionalContentCatalog, localContentCatalog));

		final List<CatalogVersionModel> catalogVersions = Arrays.asList(globalCatalogVersionOnline, regionalCatalogVersionOnline,
				localCatalogVersionStaged);
		when(cmsPageDao.findPagesByIdAndPageStatuses(FAQ_PAGE_UID, catalogVersions, SUCCESS_STATUS))
				.thenReturn(Arrays.asList(globalFaqPage, regionalFaqPage));

		when(reversedComparator.compare(globalFaqPage, regionalFaqPage)).thenReturn(1);
		when(reversedComparator.compare(regionalFaqPage, globalFaqPage)).thenReturn(-1);

		final AbstractPageModel result = pageService.getPageForId(FAQ_PAGE_UID, catalogVersions, SUCCESS_STATUS);

		assertThat(result, equalTo(regionalFaqPage));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldFailGetPageForIdNoPageFound()
	{
		when(cmsPageDao.findPagesByIdAndPageStatuses(any(), any(), any())).thenReturn(Collections.emptyList());

		pageService.getPageForId(FAQ_PAGE_UID, Arrays.asList(localCatalogVersionStaged), SUCCESS_STATUS);
	}

	@Test
	public void shouldFindActivePagesByType()
	{
		doReturn(localCatalogVersionStaged).when(pageService).getActiveCatalogVersion();

		pageService.findPagesByTypeAndPageStatuses(composedTypeModel, Boolean.TRUE, SUCCESS_STATUS);

		verify(cmsPageDao).findPagesByTypeAndPageStatuses(composedTypeModel, Arrays.asList(localCatalogVersionStaged), Boolean.TRUE,
				SUCCESS_STATUS);
	}

}
