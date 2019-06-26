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
package de.hybris.platform.cms2.version;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cms2.version.service.CMSVersionService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Ignore;


@Ignore("CMSVersion test base class. @Ignore annotation required for inheritance in test classes.")
@IntegrationTest
public class AbstractCMSVersionIntegrationTest extends ServicelayerTest
{
	public static final String HOMEPAGE = "homepage";
	public static final String CMS_CATALOG = "cms_Catalog";
	public static final String CATALOG_VERSION = "CatalogVersion1";
	public static final String EMPTY_CONTENT_SLOT_UID = "EmptySlot";
	public static final String COMPONENT_FOR_EMPTY_SLOT_UID = "LinkForEmptySlot";

	@Resource
	protected ModelService modelService;

	@Resource
	protected CMSAdminSiteService cmsAdminSiteService;

	@Resource
	protected CatalogVersionService catalogVersionService;

	@Resource
	protected CMSAdminPageService cmsAdminPageService;

	@Resource
	protected CMSVersionService cmsVersionService;

	@Resource
	protected UserService userService;

	protected CMSVersionModel contentPageCMSVersion;
	protected ContentPageModel contentPage;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/cmsVersionTestData.impex", "utf-8");
		importCsv("/test/cmsTypePermissionTestData.impex", "UTF-8");
		contentPage = cmsAdminPageService.getHomepage(catalogVersionService.getAllCatalogVersions().iterator().next());

		cmsAdminSiteService.setActiveCatalogVersion(CMS_CATALOG, CATALOG_VERSION);
		catalogVersionService.setSessionCatalogVersion(CMS_CATALOG, CATALOG_VERSION);

		final UserModel cmsmanager = userService.getUserForUID("cmsmanager");
		userService.setCurrentUser(cmsmanager);
	}
}

