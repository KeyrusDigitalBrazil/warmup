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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.ABTestCMSComponentContainerModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSAdminContentSlotServiceIntegrationTest extends ServicelayerTransactionalTest
{
	private static final Integer FIRST_INDEX = new Integer(0);
	private static final Integer SECOND_INDEX = new Integer(1);
	private static final Integer THIRD_INDEX = new Integer(2);
	private static final Integer FAIL_INDEX = new Integer(-2);
	private static final String ACTIVE_CATALOG_VERSION = "activeCatalogVersion";

	@Resource
	private SessionService sessionService;
	@Resource
	private ModelService modelService;
	@Resource
	private CMSAdminContentSlotService cmsAdminContentSlotService;
	@Resource
	private CMSAdminSiteService cmsAdminSiteService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private CMSAdminPageService cmsAdminPageService;
	@Resource
	private UserService userService;

	private ContentSlotModel slot;
	private ABTestCMSComponentContainerModel component;
	private CatalogModel catalog;
	private CatalogVersionModel catalogVersion;

	private ContentSlotModel globalSlot;
	private ContentSlotModel regionSlot;
	private ContentSlotModel localSlot;
	private CatalogVersionModel globalOnlineCatalog;
	private CatalogVersionModel globalStagedCatalog;
	private CatalogVersionModel regionStagedCatalog;
	private CatalogVersionModel localStagedCatalog;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/test/cmsTypePermissionTestData.impex", "UTF-8");
		final UserModel cmsmanager = userService.getUserForUID("cmsmanager");
		userService.setCurrentUser(cmsmanager);

		component = new ABTestCMSComponentContainerModel();
		catalog = new CatalogModel();
		catalog.setId("testCatalog-addComponentToSlot");
		catalogVersion = new CatalogVersionModel();
		catalogVersion.setCatalog(catalog);
		catalogVersion.setVersion("1.0");
		component.setCatalogVersion(catalogVersion);
		component.setUid("testComponent");

		slot = new ContentSlotModel();
		slot.setCatalogVersion(catalogVersion);
		slot.setUid("testSlot");
	}

	public void multiCountrySetUp() throws ImpExException
	{
		importCsv("/test/cmsMultiCountryTestData.csv", "UTF-8");

		globalOnlineCatalog = catalogVersionService.getCatalogVersion("MultiCountryTestContentCatalog", "OnlineVersion");
		globalStagedCatalog = catalogVersionService.getCatalogVersion("MultiCountryTestContentCatalog", "StagedVersion");
		regionStagedCatalog = catalogVersionService.getCatalogVersion("MultiCountryTestContentCatalog-region", "StagedVersion");
		localStagedCatalog = catalogVersionService.getCatalogVersion("MultiCountryTestContentCatalog-local", "StagedVersion");

		globalSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions("FooterSlot",
				Arrays.asList(globalOnlineCatalog));
		regionSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions("FooterSlot-region",
				Arrays.asList(regionStagedCatalog));
		localSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions("FooterSlot-local",
				Arrays.asList(localStagedCatalog));

		final CMSSiteModel site = new CMSSiteModel();
		site.setContentCatalogs(Arrays.asList((ContentCatalogModel) globalOnlineCatalog.getCatalog(),
				(ContentCatalogModel) regionStagedCatalog.getCatalog(), (ContentCatalogModel) localStagedCatalog.getCatalog()));
		site.setActive(Boolean.TRUE);
		site.setUid("MultiCountrySite");
		modelService.save(site);
		cmsAdminSiteService.setActiveSite(site);
	}

	@Test
	public void shouldAddComponentToContentSlot_firstPosition()
	{
		final ArrayList<AbstractCMSComponentModel> components = new ArrayList<AbstractCMSComponentModel>();
		slot.setCmsComponents(components);
		cmsAdminContentSlotService.addCMSComponentToContentSlot(component, slot, FIRST_INDEX);

		assertFalse(slot.getCmsComponents().isEmpty());
		assertEquals(1, slot.getCmsComponents().size());
		assertEquals(component, slot.getCmsComponents().get(FIRST_INDEX.intValue()));
	}

	@Test
	public void shouldAddComponentToContentSlot_secondPosition()
	{
		final ABTestCMSComponentContainerModel component1 = new ABTestCMSComponentContainerModel();
		final ABTestCMSComponentContainerModel component2 = new ABTestCMSComponentContainerModel();

		component1.setCatalogVersion(catalogVersion);
		component1.setUid("testComponent1");
		component2.setCatalogVersion(catalogVersion);
		component2.setUid("testComponent2");

		final List<AbstractCMSComponentModel> components = new ArrayList<AbstractCMSComponentModel>();
		components.add(component1);
		components.add(component2);
		slot.setCmsComponents(components);
		cmsAdminContentSlotService.addCMSComponentToContentSlot(component, slot, SECOND_INDEX);

		assertFalse(slot.getCmsComponents().isEmpty());
		assertEquals(3, slot.getCmsComponents().size());
		assertEquals(component.getUid(), slot.getCmsComponents().get(SECOND_INDEX.intValue()).getUid());
	}

	@Test(expected = TypePermissionException.class)
	public void shouldThrowTypePermissionExceptionIfUSerDoesNotHaveChangePermissionOnContentSlotType()
	{
		// GIVEN
		userService.setCurrentUser(userService.getAnonymousUser());

		// WHEN
		cmsAdminContentSlotService.addCMSComponentToContentSlot(component, slot, SECOND_INDEX);
	}

	@Test
	public void shouldAddComponentToContentSlot_thirdPosition()
	{
		final ABTestCMSComponentContainerModel component1 = new ABTestCMSComponentContainerModel();
		final ABTestCMSComponentContainerModel component2 = new ABTestCMSComponentContainerModel();

		component1.setCatalogVersion(catalogVersion);
		component1.setUid("testComponent1");
		component2.setCatalogVersion(catalogVersion);
		component2.setUid("testComponent2");

		final List<AbstractCMSComponentModel> components = new ArrayList<AbstractCMSComponentModel>();
		components.add(component1);
		components.add(component2);
		slot.setCmsComponents(components);
		cmsAdminContentSlotService.addCMSComponentToContentSlot(component, slot, THIRD_INDEX);

		assertFalse(slot.getCmsComponents().isEmpty());
		assertEquals(3, slot.getCmsComponents().size());
		assertEquals(component.getUid(), slot.getCmsComponents().get(THIRD_INDEX.intValue()).getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFail_nullIndex()
	{
		cmsAdminContentSlotService.addCMSComponentToContentSlot(component, slot, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFail_negativeIndex()
	{
		cmsAdminContentSlotService.addCMSComponentToContentSlot(component, slot, FAIL_INDEX);
	}

	@Test
	public void shouldSortLocalizedContentSlot() throws ImpExException
	{
		multiCountrySetUp();
		final List<ContentSlotModel> results = cmsAdminContentSlotService.getSortedMultiCountryContentSlots(
				Arrays.asList(globalSlot, regionSlot, localSlot),
				Arrays.asList(globalOnlineCatalog, regionStagedCatalog, localStagedCatalog));

		assertThat(results, containsInAnyOrder(localSlot, regionSlot));
	}

	@Test
	public void shouldSortContentSlotBeEmpty_NoLocalizationForRootSlot() throws ImpExException
	{
		multiCountrySetUp();
		final List<ContentSlotModel> results = cmsAdminContentSlotService
				.getSortedMultiCountryContentSlots(Arrays.asList(globalSlot), Arrays.asList(globalOnlineCatalog));

		assertThat(results, empty());
	}

	@Test
	public void shouldGetOverrideForGlobalSlot() throws ImpExException
	{
		multiCountrySetUp();
		sessionService.setAttribute(ACTIVE_CATALOG_VERSION, regionStagedCatalog.getPk());
		final AbstractPageModel regionPage = cmsAdminPageService.getPageForIdFromActiveCatalogVersion("TestHomePageRegionEU");
		final ContentSlotModel contentSlot = cmsAdminContentSlotService
				.getContentSlotForIdAndCatalogVersions("Section1Slot-TestHomePageGlobal", Arrays.asList(globalStagedCatalog));

		final ContentSlotModel result = cmsAdminContentSlotService.getContentSlotOverride(regionPage, contentSlot);

		final ContentSlotModel expectedOverrideSlot = cmsAdminContentSlotService
				.getContentSlotForIdAndCatalogVersions("Section1Slot-TestHomePageRegionEU", Arrays.asList(regionStagedCatalog));
		assertThat(result, equalTo(expectedOverrideSlot));
	}

	@Test
	public void shouldGetNoOverrideForChildSlot() throws ImpExException
	{
		multiCountrySetUp();
		sessionService.setAttribute(ACTIVE_CATALOG_VERSION, regionStagedCatalog.getPk());
		final AbstractPageModel regionPage = cmsAdminPageService.getPageForIdFromActiveCatalogVersion("TestHomePageRegionEU");
		final ContentSlotModel contentSlot = cmsAdminContentSlotService.getContentSlotForId("Section1Slot-TestHomePageRegionEU");

		final ContentSlotModel result = cmsAdminContentSlotService.getContentSlotOverride(regionPage, contentSlot);

		assertThat(result, nullValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldGetContentSlotsForPage() throws ImpExException
	{
		multiCountrySetUp();
		// This test should find all content slots defined for the page template and custom slots defined for the page
		sessionService.setAttribute(ACTIVE_CATALOG_VERSION, regionStagedCatalog.getPk());
		final AbstractPageModel regionPage = cmsAdminPageService.getPageForIdFromActiveCatalogVersion("TestHomePageRegionEU");

		final Collection<ContentSlotData> results = cmsAdminContentSlotService.getContentSlotsForPage(regionPage);

		final ContentSlotModel expectedOverrideSlot = cmsAdminContentSlotService
				.getContentSlotForIdAndCatalogVersions("Section1Slot-TestHomePageRegionEU", Arrays.asList(regionStagedCatalog));
		final ContentSlotModel expectedGlobalSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions("SiteLogoSlot",
				Arrays.asList(globalStagedCatalog));

		assertThat(results,
				hasItems(
						allOf(hasProperty("uid", equalTo(expectedOverrideSlot.getUid())),
								hasProperty("contentSlot", equalTo(expectedOverrideSlot))),
						allOf(hasProperty("uid", equalTo(expectedGlobalSlot.getUid())),
								hasProperty("contentSlot", equalTo(expectedGlobalSlot)))));
	}
}
