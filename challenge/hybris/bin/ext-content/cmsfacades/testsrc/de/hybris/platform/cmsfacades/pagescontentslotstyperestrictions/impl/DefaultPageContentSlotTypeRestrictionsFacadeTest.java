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
package de.hybris.platform.cmsfacades.pagescontentslotstyperestrictions.impl;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.catalogversion.service.CMSCatalogVersionService;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.model.CMSComponentTypeModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.multicountry.service.CatalogLevelService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminTypeRestrictionsService;
import de.hybris.platform.cmsfacades.data.ContentSlotTypeRestrictionsData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPageContentSlotTypeRestrictionsFacadeTest
{
	private static final String VALID_PAGE_UID = "validPageUid";
	private static final String VALID_CONTENTSLOT_UID = "validContentSlotUid";

	@InjectMocks
	private DefaultPageContentSlotTypeRestrictionsFacade defaultContentSlotDetailsFacade;

	@Mock
	private CMSAdminTypeRestrictionsService cmsAdminTypeRestrictionsService;
	@Mock
	private CMSAdminContentSlotService cmsAdminContentSlotService;
	@Mock
	private CMSAdminPageService cmsAdminPageService;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private CatalogVersionModel parentCatalogVersionModel;
	@Mock
	private ContentCatalogModel catalogModel;
	@Mock
	private ContentCatalogModel parentCatalogModel;
	@Mock
	private CMSSiteModel cmsSite;
	@Mock
	private CatalogLevelService catalogLevelService;
	@Mock
	private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;
	@Mock
	private CMSAdminSiteService cmsAdminSiteService;
	@Mock
	private CMSCatalogVersionService cmsCatalogVersionService;
	@Mock
	private CatalogVersionService catalogVersionService;

	@Before
	public void setup() throws Exception
	{
		when(cmsAdminSiteService.getActiveCatalogVersion()).thenReturn(catalogVersionModel);
		when(catalogVersionModel.getCatalog()).thenReturn(catalogModel);
		when(catalogModel.getCmsSites()).thenReturn(Arrays.asList(cmsSite));
		when(parentCatalogModel.getActiveCatalogVersion()).thenReturn(parentCatalogVersionModel);

		doAnswer(invocation -> {
			final Object[] args = invocation.getArguments();
			final Supplier supplier = (Supplier)args[0];
			return supplier.get();
		}).when(sessionSearchRestrictionsDisabler).execute(any());

		when(cmsAdminTypeRestrictionsService.getTypeRestrictionsForContentSlot(
				cmsAdminPageService.getPageForIdFromActiveCatalogVersion(VALID_PAGE_UID),
				cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions(VALID_CONTENTSLOT_UID, Arrays.asList(catalogVersionModel, parentCatalogVersionModel))))
		.thenReturn(new HashSet<CMSComponentTypeModel>());

		when(catalogVersionService.getSessionCatalogVersions())
				.thenReturn(Arrays.asList(parentCatalogVersionModel, catalogVersionModel));
	}

	@Test
	public void getTypeRestrictionsForPageContentSlots_forCatalogWithParents_Returns_ContentSlotTypeRestrictionsDataList() throws Exception
	{
		// GIVEN
		final List<CatalogVersionModel> catalogVersionModelList = new ArrayList<>();
		catalogVersionModelList.add(catalogVersionModel);
		catalogVersionModelList.add(parentCatalogVersionModel);
		when(cmsCatalogVersionService.getSuperCatalogsActiveCatalogVersions(catalogModel, cmsSite)).thenReturn(catalogVersionModelList);

		// WHEN
		final ContentSlotTypeRestrictionsData actualTypeRestrictions = defaultContentSlotDetailsFacade
				.getTypeRestrictionsForContentSlotUID(VALID_PAGE_UID, VALID_CONTENTSLOT_UID);

		// THEN
		assertFalse(actualTypeRestrictions.getContentSlotUid() == null);
	}

	@Test
	public void getTypeRestrictionsForPageContentSlots_forCatalogWithNoParents_Returns_ContentSlotTypeRestrictionsDataList() throws Exception
	{
		// GIVEN
		final List<CatalogVersionModel> catalogVersionModelList = new ArrayList<>();
		catalogVersionModelList.add(catalogVersionModel);
		when(cmsCatalogVersionService.getSuperCatalogsActiveCatalogVersions(catalogModel, cmsSite)).thenReturn(catalogVersionModelList);

		// WHEN
		final ContentSlotTypeRestrictionsData actualTypeRestrictions = defaultContentSlotDetailsFacade
				.getTypeRestrictionsForContentSlotUID(VALID_PAGE_UID, VALID_CONTENTSLOT_UID);

		// THEN
		assertFalse(actualTypeRestrictions.getContentSlotUid() == null);
	}
}
