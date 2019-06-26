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
package de.hybris.platform.cmsfacades.sites.populator.model;

import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.APPAREL;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.data.SiteData;
import de.hybris.platform.cmsfacades.resolvers.sites.SiteThumbnailResolver;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.model.ItemModelInternalContext;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SiteModelPopulatorTest
{
	@Mock
	private ItemModelInternalContext mockContext;
	@Mock
	private CMSAdminPageService cmsAdminPageService;

	@Mock
	private SiteThumbnailResolver siteThumbnailResolver;

	@Mock
	private ContentPageModel homepage;
	@Mock
	private MediaModel thumbnail;
	@Mock
	private LocalizedPopulator localizedPopulator;

	@Mock
	private ContentCatalogModel contentCatalogModel1;

	@Mock
	private ContentCatalogModel contentCatalogModel2;

	@InjectMocks
	private SiteModelPopulator populator;

	private final CMSSiteModel sourceSite = new CMSSiteModel();

	@Before
	public void setup()
	{
		when(siteThumbnailResolver.resolveHomepageThumbnailUrl(any(CMSSiteModel.class))).thenReturn(of(APPAREL.getThumbnailUri()));
		when(cmsAdminPageService.getHomepage(sourceSite)).thenReturn(homepage);
		when(homepage.getPreviewImage()).thenReturn(thumbnail);
		when(thumbnail.getDownloadURL()).thenReturn(APPAREL.getThumbnailUri());

		when(contentCatalogModel1.getId()).thenReturn("cm1");
		when(contentCatalogModel2.getId()).thenReturn("cm2");
		sourceSite.setContentCatalogs(Arrays.asList(contentCatalogModel1, contentCatalogModel2));
	}

	@Test
	public void populateWillPopulateADtoWhenGivenAModelIgnoreName() throws Exception
	{
		final SiteData targetSiteData = new SiteData();
		final Map<String, String> names = new HashMap<>();
		names.put(Locale.ENGLISH.toString(), "test-site-name");
		targetSiteData.setName(names);

		populator.populate(sourceSite, targetSiteData);

		verify(localizedPopulator).populate(any(), any());
	}

	@Test
	public void populateWillPopulateADtoAndLocalizedPopulatorHasBeenCalled() throws Exception
	{
		final SiteData targetSiteData = new SiteData();
		populator.populate(sourceSite, targetSiteData);
		verify(localizedPopulator).populate(Mockito.any(), Mockito.any());
	}

	@Test
	public void assertsThatGetSiteDataNameSetterPopulatesTheTarget()
	{
		final String expectedValue = "value";
		final SiteData targetSiteData = new SiteData();
		targetSiteData.setName(new HashMap<>());
		final BiConsumer<Locale, String> siteDataNameSetter = populator.getSiteDataNameSetter(targetSiteData);
		siteDataNameSetter.accept(Locale.ENGLISH, expectedValue);
		assertThat(targetSiteData.getName().get(localizedPopulator.getLanguage(Locale.ENGLISH)), is(expectedValue));
	}

	@Test
	public void assertsThatGetSiteModelNameGetter()
	{
		final String expectedValue = "value";
		final CMSSiteModel model = Mockito.mock(CMSSiteModel.class);
		when(model.getName(Locale.ENGLISH)).thenReturn(expectedValue);
		final Function<Locale, String> siteModelNameGetter = populator.getSiteModelNameGetter(model);
		final String returnedValue = siteModelNameGetter.apply(Locale.ENGLISH);
		assertThat(returnedValue, is(expectedValue));
	}

	@Test
	public void populatorAddsContentCatalogsAndKeepsThemSorted()
	{
		final List<String> expectedList = Arrays.asList("cm1", "cm2");
		final SiteData targetSiteData = new SiteData();
		populator.populate(sourceSite, targetSiteData);

		assertEquals(targetSiteData.getContentCatalogs(), expectedList);
	}
}
