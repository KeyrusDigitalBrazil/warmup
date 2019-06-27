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
package de.hybris.platform.cmsfacades.cmsitems.populators;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.util.BaseIntegrationTest;
import de.hybris.platform.cmsfacades.util.models.*;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class TrashPagePopulatorIntegrationTest extends BaseIntegrationTest
{
	protected static final String NAV_NODE_TITLE = "Navigation Title";
	protected static final String NAV_NODE_ID = "667NavNode";
	protected static final String NAV_ENTRY_ID_0 = "667NavNodeEntry";
	protected static final String NAV_ENTRY_ID_1 = "667NavNodeEntry1";
	protected static final String NAV_ENTRY_ID_2 = "667NavNodeEntry2";
	protected static final String NAV_ENTRY_ID_3 = "667NavNodeEntry3";

	@Resource
	protected CMSAdminSiteService cmsAdminSiteService;

	@Resource
	protected ModelService modelService;

	@Resource
	protected CMSAdminPageService cmsAdminPageService;

	@Resource
	protected CatalogVersionService catalogVersionService;

	@Resource
	protected CMSNavigationService cmsNavigationService;

	@Resource
	protected TrashPagePopulator cmsTrashPagePopulator;

	@Resource
	protected FlexibleSearchService flexibleSearchService;

	// mothers
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;

	@Resource
	private ContentPageModelMother contentPageModelMother;

	@Resource
	private CMSNavigationNodeModelMother navigationNodeModelMother;

	@Resource
	private LinkComponentModelMother linkComponentModelMother;

	@Resource
	private CMSNavigationEntryModelMother navigationEntryModelMother;

	protected ContentPageModel homepage;
	protected Map<String, Object> map;
	protected CatalogVersionModel catalogVersion;

	@Before
	public void setup()
	{
		final CatalogVersionModel catalogVersion = catalogVersionModelMother
				.createStagedCatalogVersionModelWithId(ContentCatalogModelMother.CatalogTemplate.ID_STAGED);
		homepage = contentPageModelMother
				.homePage(catalogVersion);
		final CMSNavigationNodeModel navigationNodeTree = navigationNodeModelMother.createNavigationNodeTree(catalogVersion);
		final CMSNavigationNodeModel navigationNode = navigationNodeModelMother
				.createNavigationNode(NAV_NODE_ID, NAV_NODE_ID, navigationNodeTree, NAV_NODE_TITLE, catalogVersion);

		// links
		final CMSLinkComponentModel linkComponentModel0 = linkComponentModelMother
				.createContentPageLinkComponentModel(catalogVersion);

		final CMSLinkComponentModel linkComponentModel1 = linkComponentModelMother
				.createContentPageLinkComponentModel(catalogVersion);

		// entries
		navigationEntryModelMother
				.createEntryAndAddToNavigationNode(navigationNode, catalogVersion, homepage, NAV_ENTRY_ID_0);
		navigationEntryModelMother
				.createEntryAndAddToNavigationNode(navigationNode, catalogVersion, homepage, NAV_ENTRY_ID_1);
		navigationEntryModelMother
				.createEntryAndAddToNavigationNode(navigationNode, catalogVersion, linkComponentModel0, NAV_ENTRY_ID_2);
		navigationEntryModelMother
				.createEntryAndAddToNavigationNode(navigationNode, catalogVersion, linkComponentModel1, NAV_ENTRY_ID_3);

		map = new HashMap<>();
		map.put(ContentPageModel.UID, homepage.getUid());

		cmsAdminSiteService.setActiveCatalogVersion(catalogVersion);
	}

	@Test
	public void shouldRemoveNavigationEntriesFromNavigationNodesWhenContentPageIsInDeletedStatus()
	{
		// GIVEN
		map.put(ContentPageModel.PAGESTATUS, CmsPageStatus.DELETED.getCode());

		assertThat(getNumberOfNavEntries(), is(4));

		// WHEN
		cmsTrashPagePopulator.populate(map, homepage);

		// THEN
		assertThat(getNumberOfNavEntries(), is(0));
	}

	@Test
	public void shouldNotRemoveNavigationEntriesFromNavigationNodesWhenContentPageIsInActiveStatus()
	{
		// GIVEN
		map.put(ContentPageModel.PAGESTATUS, CmsPageStatus.ACTIVE.getCode());

		assertThat(getNumberOfNavEntries(), is(4));

		// WHEN
		cmsTrashPagePopulator.populate(map, homepage);

		// THEN
		assertThat(getNumberOfNavEntries(), is(4));
	}

	@Test
	public void shouldNotRemoveNavigationEntriesFromNavigationNodesWhenMapHasNoStatus()
	{
		// GIVEN
		assertThat(getNumberOfNavEntries(), is(4));

		// WHEN
		cmsTrashPagePopulator.populate(map, homepage);

		// THEN
		assertThat(getNumberOfNavEntries(), is(4));
	}

	protected int getNumberOfNavEntries()
	{
		return cmsNavigationService.getNavigationEntriesByPage(homepage).size();
	}
}
