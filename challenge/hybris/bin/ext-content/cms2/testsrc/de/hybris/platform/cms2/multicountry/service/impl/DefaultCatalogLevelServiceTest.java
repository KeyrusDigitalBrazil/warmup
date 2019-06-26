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
package de.hybris.platform.cms2.multicountry.service.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCatalogLevelServiceTest
{
	@InjectMocks
	private DefaultCatalogLevelService catalogLevelService;

	@Mock
	private CMSAdminSiteService cmsAdminSiteService;

	@Mock
	private CMSSiteModel currentSite;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private Comparator<ContentSlotModel> cmsContentSlotCatalogLevelComparator;

	@Before
	public void setup()
	{
		when(cmsAdminSiteService.getActiveSite()).thenReturn(currentSite);
		when(configurationService.getConfiguration()).thenReturn(configuration);
	}

	@Test
	public void shouldReturnDefaultCatalogLevelWhenActiveSiteInSessionIsNull()
	{
		final ContentCatalogModel parent = new ContentCatalogModel();
		final ContentCatalogModel child = new ContentCatalogModel();
		child.setSuperCatalog(parent);

		when(currentSite.getContentCatalogs()).thenReturn(Arrays.asList(parent, child));
		when(cmsAdminSiteService.getActiveSite()).thenReturn(null);

		final int result = catalogLevelService.getCatalogLevel(child);

		assertThat(result, equalTo(0));
	}

	@Test
	public void shouldReturnCatalogLevelWithOneLevelHierarchy()
	{
		final ContentCatalogModel parent = new ContentCatalogModel();
		final ContentCatalogModel child = new ContentCatalogModel();
		child.setSuperCatalog(parent);

		when(currentSite.getContentCatalogs()).thenReturn(Arrays.asList(parent, child));

		final int result = catalogLevelService.getCatalogLevel(child);

		assertThat(result, equalTo(1));
	}

	@Test
	public void shouldReturnCatalogLevelWithTwoLevelsHierarchy()
	{
		final ContentCatalogModel parent = new ContentCatalogModel();
		final ContentCatalogModel child1 = new ContentCatalogModel();
		child1.setSuperCatalog(parent);
		final ContentCatalogModel child2 = new ContentCatalogModel();
		child2.setSuperCatalog(child1);

		when(currentSite.getContentCatalogs()).thenReturn(Arrays.asList(parent, child1, child2));

		final int result = catalogLevelService.getCatalogLevel(child2);

		assertThat(result, equalTo(2));
	}

	@Test
	public void shouldReturnIsTopLevel()
	{
		final ContentCatalogModel contentCatalog = new ContentCatalogModel();

		final boolean result = catalogLevelService.isTopLevel(contentCatalog);

		assertThat(result, equalTo(Boolean.TRUE));
	}

	@Test
	public void shouldReturnIsIntermediateLevel()
	{
		final ContentCatalogModel contentCatalog = new ContentCatalogModel();
		contentCatalog.setSuperCatalog(new ContentCatalogModel());
		contentCatalog.setSubCatalogs(wrapValueAsSet(new ContentCatalogModel()));

		final boolean result = catalogLevelService.isIntermediateLevel(contentCatalog);

		assertThat(result, equalTo(Boolean.TRUE));
	}

	@Test
	public void shouldReturnIsBottomLevel()
	{
		final ContentCatalogModel contentCatalog = new ContentCatalogModel();
		contentCatalog.setSuperCatalog(new ContentCatalogModel());

		final boolean result = catalogLevelService.isBottomLevel(contentCatalog);

		assertThat(result, equalTo(Boolean.TRUE));
	}

	@Test
	public void shouldReturnAllSubCatalogs()
	{
		final ContentCatalogModel parent = new ContentCatalogModel();
		final ContentCatalogModel child1 = new ContentCatalogModel();
		final ContentCatalogModel child2 = new ContentCatalogModel();
		child1.setSubCatalogs(wrapValueAsSet(child2));
		parent.setSubCatalogs(wrapValueAsSet(child1));

		when(currentSite.getContentCatalogs()).thenReturn(Arrays.asList(parent, child1, child2));

		final Collection<ContentCatalogModel> result = catalogLevelService.getAllSubCatalogs(parent);

		assertThat(result, hasSize(2));
	}

	protected <T extends ItemModel> Set<T> wrapValueAsSet(final T value)
	{
		return new HashSet(Collections.singletonList(value));
	}
}
