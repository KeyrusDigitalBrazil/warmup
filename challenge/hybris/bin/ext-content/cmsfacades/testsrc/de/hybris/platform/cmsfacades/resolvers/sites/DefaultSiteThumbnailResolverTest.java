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
package de.hybris.platform.cmsfacades.resolvers.sites;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.catalogversion.service.CMSCatalogVersionService;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.resolvers.sites.impl.DefaultSiteThumbnailResolver;
import de.hybris.platform.core.model.media.MediaModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSiteThumbnailResolverTest
{
	private String URL_CONTENT_PAGE_ACTIVE_CATALOG1 = "url_content_page_active_catalog1";
	private String URL_CONTENT_PAGE_ACTIVE_CATALOG2 = "url_content_page_active_catalog2";
	private String URL_CONTENT_PAGE_NOT_ACTIVE_CATALOG = "url_content_page_not_active_catalog";

	@InjectMocks
	private DefaultSiteThumbnailResolver resolver;

	@Mock
	private CMSCatalogVersionService cmsCatalogVersionService;
	@Mock
	private CMSAdminSiteService cmsAdminSiteService;
	@Mock
	private CMSAdminPageService cmsAdminPageService;

	@Mock
	private CMSSiteModel activeSite;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private ContentCatalogModel contentCatalogModel;

	@Mock
	private CatalogVersionModel catalogVersionModelActiveWithHomepage1;
	@Mock
	private CatalogVersionModel catalogVersionModelActiveWithHomepage2;
	@Mock
	private CatalogVersionModel catalogVersionModelActiveWithoutHomepage;

	@Mock
	private CatalogVersionModel catalogVersionModelNotActiveWithHomepage;
	@Mock
	private CatalogVersionModel catalogVersionModelNotActiveWithoutHomepage;

	@Mock
	private ContentPageModel contentPageModelForActiveCatalog1;
	@Mock
	private ContentPageModel contentPageModelForActiveCatalog2;
	@Mock
	private ContentPageModel contentPageModelForNotActiveCatalog;

	@Mock
	private MediaModel contentPageModelForActiveCatalogImage1;
	@Mock
	private MediaModel contentPageModelForActiveCatalogImage2;
	@Mock
	private MediaModel contentPageModelForNotActiveCatalogImage;

	@Mock
	private CMSSiteModel cmsSiteModel;

	@Before
	public void setUp()
	{
		when(catalogVersion.getCatalog()).thenReturn(contentCatalogModel);
		when(cmsAdminSiteService.getActiveSite()).thenReturn(activeSite);

		when(catalogVersionModelActiveWithHomepage1.getActive()).thenReturn(true);
		when(catalogVersionModelActiveWithHomepage2.getActive()).thenReturn(true);
		when(catalogVersionModelActiveWithoutHomepage.getActive()).thenReturn(true);

		when(catalogVersionModelNotActiveWithHomepage.getActive()).thenReturn(false);
		when(catalogVersionModelNotActiveWithoutHomepage.getActive()).thenReturn(false);


		when(cmsAdminPageService.getHomepage(catalogVersionModelActiveWithHomepage1)).thenReturn(contentPageModelForActiveCatalog1);
		when(cmsAdminPageService.getHomepage(catalogVersionModelActiveWithHomepage2)).thenReturn(contentPageModelForActiveCatalog2);
		when(cmsAdminPageService.getHomepage(catalogVersionModelActiveWithoutHomepage)).thenReturn(null);

		when(cmsAdminPageService.getHomepage(catalogVersionModelNotActiveWithHomepage)).thenReturn(
				contentPageModelForNotActiveCatalog);
		when(cmsAdminPageService.getHomepage(catalogVersionModelNotActiveWithoutHomepage)).thenReturn(null);

		List<CatalogVersionModel> superCatalogVersionList = new ArrayList<>();
		superCatalogVersionList.addAll(Arrays.asList(
				catalogVersionModelActiveWithHomepage1,
				catalogVersionModelActiveWithoutHomepage,
				catalogVersionModelActiveWithHomepage2,
				catalogVersionModelNotActiveWithHomepage,
				catalogVersionModelNotActiveWithoutHomepage
		));

		when(cmsCatalogVersionService.getSuperCatalogsCatalogVersions(contentCatalogModel, activeSite)).thenReturn(superCatalogVersionList);

		when(contentPageModelForActiveCatalog1.getPreviewImage()).thenReturn(contentPageModelForActiveCatalogImage1);
		when(contentPageModelForActiveCatalog2.getPreviewImage()).thenReturn(contentPageModelForActiveCatalogImage2);
		when(contentPageModelForNotActiveCatalog.getPreviewImage()).thenReturn(contentPageModelForNotActiveCatalogImage);

		when(contentPageModelForActiveCatalogImage1.getDownloadURL()).thenReturn(URL_CONTENT_PAGE_ACTIVE_CATALOG1);
		when(contentPageModelForActiveCatalogImage2.getDownloadURL()).thenReturn(URL_CONTENT_PAGE_ACTIVE_CATALOG2);
		when(contentPageModelForNotActiveCatalogImage.getDownloadURL()).thenReturn(URL_CONTENT_PAGE_NOT_ACTIVE_CATALOG);
	}

	@Test
	public void shouldResolveFirstFromTheBottomActiveCatalogHomepageThumbnailUrl()
	{
		// Given
		when(catalogVersion.getActive()).thenReturn(true);

		// When
		Optional<String> optionalUrl = resolver.resolveHomepageThumbnailUrl(catalogVersion);
		String url = optionalUrl.get();

		// Then
		assertThat(url, is(URL_CONTENT_PAGE_ACTIVE_CATALOG2));
	}

	@Test
	public void shouldResolveFirstFromTheBottomNotActiveCatalogHomepageThumbnailUrl()
	{
		// Given
		when(catalogVersion.getActive()).thenReturn(false);

		// When
		Optional<String> optionalUrl = resolver.resolveHomepageThumbnailUrl(catalogVersion);
		String url = optionalUrl.get();

		// Then
		assertThat(url, is(URL_CONTENT_PAGE_NOT_ACTIVE_CATALOG));
	}

	@Test
	public void shouldReturnEmptyForUndefinedHomepage()
	{
		// Given
		when(cmsAdminPageService.getHomepage(cmsSiteModel)).thenReturn(null);

		// When
		Optional<String> optionalUrl = resolver.resolveHomepageThumbnailUrl(cmsSiteModel);

		// Then
		assertFalse(optionalUrl.isPresent());
	}

	@Test
	public void shouldReturnEmptyForUndefinedPreviewImage()
	{
		// Given
		when(cmsAdminPageService.getHomepage(cmsSiteModel)).thenReturn(contentPageModelForActiveCatalog1);
		when(contentPageModelForActiveCatalog1.getPreviewImage()).thenReturn(null);

		// When
		Optional<String> optionalUrl = resolver.resolveHomepageThumbnailUrl(cmsSiteModel);

		// Then
		assertFalse(optionalUrl.isPresent());
	}
}

