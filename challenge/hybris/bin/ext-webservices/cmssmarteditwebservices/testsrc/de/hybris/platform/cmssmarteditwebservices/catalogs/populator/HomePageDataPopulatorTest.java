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
package de.hybris.platform.cmssmarteditwebservices.catalogs.populator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.catalogversion.service.CMSCatalogVersionService;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.data.HomePageData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class HomePageDataPopulatorTest
{
	private static final String GLOBAL_UID = "global-homepage-uid";
	private static final String GLOBAL_NAME = "Global Homepage";
	private static final String LOCAL_UID = "local-homepage-uid";
	private static final String LOCAL_NAME = "Local Homepage";

	@InjectMocks
	private HomePageDataPopulator populator;

	@Mock
	private CMSAdminPageService cmsAdminPageService;
	@Mock
	private CMSAdminSiteService cmsAdminSiteService;
	@Mock
	private CMSCatalogVersionService cmsCatalogVersionService;
	@Mock
	private Converter<AbstractPageModel, AbstractPageData> abstractPageDataConverter;

	@Mock
	private CMSSiteModel site;
	@Mock
	private CatalogVersionModel globalOnlineVersion;
	@Mock
	private CatalogVersionModel localStagedVersion;

	@Mock
	private ContentCatalogModel contentCatalog;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private HomePageData homePageData;
	@Mock
	private ContentPageModel oldHomepage;
	@Mock
	private ContentPageModel currentHomepage;
	@Mock
	private ContentPageModel fallbackHomepage;

	@Before
	public void setUp()
	{
		when(cmsAdminSiteService.getActiveSite()).thenReturn(site);
		when(cmsAdminPageService.getHomepage(site)).thenReturn(oldHomepage);
		when(catalogVersion.getCatalog()).thenReturn(contentCatalog);
	}

	@Test
	public void shouldPopulateHomePageData()
	{
		final List<CatalogVersionModel> catalogVersions = Arrays.asList(localStagedVersion);
		when(cmsCatalogVersionService.getFullHierarchyForCatalogVersion(catalogVersion, site)).thenReturn(catalogVersions);
		when(cmsCatalogVersionService.getSuperCatalogsActiveCatalogVersions(contentCatalog, site))
				.thenReturn(Collections.emptyList());
		when(cmsAdminPageService.getHomepage(catalogVersions)).thenReturn(currentHomepage);

		populator.populate(catalogVersion, homePageData);

		verify(abstractPageDataConverter).convert(currentHomepage);
		verify(homePageData).setCurrent(any());
		verify(abstractPageDataConverter).convert(oldHomepage);
		verify(homePageData).setOld(any());
		verify(abstractPageDataConverter, times(0)).convert(fallbackHomepage);
		verify(homePageData, times(0)).setFallback(any());
	}

	@Test
	public void shouldPopulateMultiCountryHomePageData()
	{
		final List<CatalogVersionModel> catalogVersions = Arrays.asList(globalOnlineVersion, localStagedVersion);
		final List<CatalogVersionModel> parentActiveCatalogVersions = Arrays.asList(globalOnlineVersion);
		when(cmsCatalogVersionService.getFullHierarchyForCatalogVersion(catalogVersion, site)).thenReturn(catalogVersions);
		when(cmsCatalogVersionService.getSuperCatalogsActiveCatalogVersions(contentCatalog, site))
				.thenReturn(parentActiveCatalogVersions);
		when(cmsAdminPageService.getHomepage(catalogVersions)).thenReturn(currentHomepage);
		when(cmsAdminPageService.getHomepage(parentActiveCatalogVersions)).thenReturn(fallbackHomepage);

		populator.populate(catalogVersion, homePageData);

		verify(abstractPageDataConverter).convert(currentHomepage);
		verify(homePageData).setCurrent(any());
		verify(abstractPageDataConverter).convert(oldHomepage);
		verify(homePageData).setOld(any());
		verify(abstractPageDataConverter).convert(fallbackHomepage);
		verify(homePageData).setFallback(any());
	}
}
