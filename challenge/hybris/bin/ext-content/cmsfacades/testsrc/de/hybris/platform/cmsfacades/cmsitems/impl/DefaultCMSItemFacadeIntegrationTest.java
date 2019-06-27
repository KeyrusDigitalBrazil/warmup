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
package de.hybris.platform.cmsfacades.cmsitems.impl;

import static de.hybris.platform.cms2.enums.CmsPageStatus.ACTIVE;
import static de.hybris.platform.cms2.enums.CmsPageStatus.DELETED;
import static de.hybris.platform.cms2.model.contents.CMSItemModel.UID;
import static de.hybris.platform.cms2.model.pages.AbstractPageModel.DEFAULTPAGE;
import static de.hybris.platform.cms2.model.pages.AbstractPageModel.PAGESTATUS;
import static de.hybris.platform.cms2.model.pages.AbstractPageModel.TYPECODE;
import static de.hybris.platform.cms2.model.pages.ContentPageModel.LABEL;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_PAGE_REPLACE;
import static de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother.CatalogVersion.STAGED;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_APPLE;
import static java.util.Locale.ENGLISH;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.contains;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade;
import de.hybris.platform.cmsfacades.uniqueidentifier.EncodedItemComposedKey;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmsfacades.util.BaseIntegrationTest;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.PageTemplateModelMother;
import de.hybris.platform.cmsfacades.util.models.ProductPageModelMother;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSItemFacadeIntegrationTest extends BaseIntegrationTest
{
	private static final String ACTIVE_UNTIL = "activeUntil";
	private static final String ACTIVE_FROM = "activeFrom";
	private static final String CATALOG_VERSION = "catalogVersion";
	private static final String NAME = "name";
	private static final String ITEMTYPE = "itemtype";
	private static final String TITLE = "title";
	private static final String UUID = "uuid";
	private static final String MASTER_TEMPLATE = "masterTemplate";

	private static final String RESTORE_CONTENT_PAGE_UID = "restore-uid-homepage";
	private static final String RESTORE_CONTENT_PAGE_NAME = "restore-name-homepage";
	private static final String RESTORE_PRODUCT_PAGE_UID = "restore-uid-product-detail";
	private static final String RESTORE_PRODUCT_PAGE_NAME = "restore-name-product-detail";

	@Resource
	private CMSItemFacade defaultCMSItemFacade;
	@Resource
	private ModelService modelService;
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private UniqueItemIdentifierService cmsUniqueItemIdentifierService;
	@Resource
	private CMSAdminPageService cmsAdminPageService;

	@Resource
	private SiteModelMother siteModelMother;
	@Resource
	private ContentPageModelMother contentPageModelMother;
	@Resource
	private PageTemplateModelMother pageTemplateModelMother;
	@Resource
	private ProductPageModelMother productPageModelMother;

	private CatalogVersionModel catalogVersion;
	private String catalogVersionUUID;

	@Before
	public void setup()
	{
		setCurrentUserCmsManager();
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		catalogVersionUUID = cmsUniqueItemIdentifierService.getItemData(catalogVersion).get().getItemId();
	}

	/*
	 * CMSTimeRestrictionModel.USESTORETIMEZONE is blacklisted and has a default value. Should be able to create a new
	 * CMSTimeRestriction without passing a USESTORETIMEZONE value.
	 */
	@Test
	public void shouldCreateCMSTimeRestrictionWithoutUseStoreTimeZone() throws CMSItemNotFoundException
	{
		final Map<String, Object> timeRestriction = new HashMap<>();
		timeRestriction.put(ITEMTYPE, CMSTimeRestrictionModel._TYPECODE);
		timeRestriction.put(NAME, "TestTime");
		timeRestriction.put(CATALOG_VERSION, catalogVersionUUID);
		timeRestriction.put(ACTIVE_FROM, "2017-08-03T14:33:00+0000");
		timeRestriction.put(ACTIVE_UNTIL, "2017-08-17T14:33:00+0000");

		final Map<String, Object> createdItem = defaultCMSItemFacade.createItem(timeRestriction);

		assertThat(createdItem.keySet(), hasSize(greaterThan(5)));
		assertThat(createdItem.keySet(), not(contains(CMSTimeRestrictionModel.USESTORETIMEZONE)));
	}

	/*
	 * CMSTimeRestrictionModel.USESTORETIMEZONE is blacklisted and has a default value. Should be able to update a new
	 * CMSTimeRestriction without passing a USESTORETIMEZONE value.
	 */
	@Test
	public void shouldUpdateCMSTimeRestrictionWithoutUseStoreTimeZone() throws CMSItemNotFoundException
	{
		final Map<String, Object> timeRestriction = new HashMap<>();
		timeRestriction.put(ITEMTYPE, CMSTimeRestrictionModel._TYPECODE);
		timeRestriction.put(NAME, "TestTime");
		timeRestriction.put(CATALOG_VERSION, catalogVersionUUID);
		timeRestriction.put(ACTIVE_FROM, "2017-10-03T08:33:00+0000");
		timeRestriction.put(ACTIVE_UNTIL, "2017-10-17T08:33:00+0000");

		final Map<String, Object> createdItem = defaultCMSItemFacade.createItem(timeRestriction);

		final String newName = "TestTime-Renamed";
		createdItem.put(NAME, newName);
		createdItem.put(ACTIVE_FROM, "2017-12-15T10:33:00+0000");
		createdItem.put(ACTIVE_UNTIL, "2017-12-20T10:33:00+0000");
		final Map<String, Object> updatedItem = defaultCMSItemFacade.updateItem(createdItem.get(UUID).toString(), createdItem);

		assertThat(updatedItem.get(NAME), equalTo(newName));
	}

	@Test
	public void testUpdateContentPageToRestoreWithAnExistingPageLabelShouldDeleteTheCurrentPage() throws CMSItemNotFoundException
	{
		// GIVEN
		createElectronicsSite();
		final AbstractPageModel originalPage = contentPageModelMother.primaryHomePage(catalogVersion);
		contentPageModelMother.somePrimaryPage(catalogVersion, RESTORE_CONTENT_PAGE_UID, "homepage", CmsPageStatus.DELETED);

		final PageTemplateModel pageTemplate = pageTemplateModelMother.homepageTemplate(catalogVersion);
		final String restorePageUuid = getUuidForAppleStage(RESTORE_CONTENT_PAGE_UID);
		final String pageTemplateUuid = getUuidForAppleStage(pageTemplate.getUid());

		final Map<String, String> titleMap = new HashMap<>();
		titleMap.put(ENGLISH.toLanguageTag(), "custom content page test");

		final Map<String, Object> pageToRestore = new HashMap<>();
		pageToRestore.put(ITEMTYPE, ContentPageModel._TYPECODE);
		pageToRestore.put(NAME, RESTORE_CONTENT_PAGE_NAME);
		pageToRestore.put(UID, RESTORE_CONTENT_PAGE_UID);
		pageToRestore.put(LABEL, "/homepage");
		pageToRestore.put(PAGESTATUS, CmsPageStatus.ACTIVE.name());
		pageToRestore.put(DEFAULTPAGE, true);
		pageToRestore.put(CATALOG_VERSION, catalogVersionUUID);
		pageToRestore.put(UUID, restorePageUuid);
		pageToRestore.put(FIELD_PAGE_REPLACE, true);
		pageToRestore.put(TITLE, titleMap);
		pageToRestore.put(MASTER_TEMPLATE, pageTemplateUuid);

		// WHEN
		final Map<String, Object> createdItem = defaultCMSItemFacade.updateItem(restorePageUuid, pageToRestore);

		// THEN
		assertThat(originalPage.getPageStatus(), is(CmsPageStatus.DELETED));
	}

	@Test
	public void testUpdateProductPageToRestoreWithAnExistingPrimaryShouldDeleteTheCurrentPrimaryPage()
			throws CMSItemNotFoundException
	{
		// GIVEN
		createElectronicsSite();
		final AbstractPageModel originalPage = productPageModelMother.primaryProductPage(catalogVersion, CmsPageStatus.ACTIVE);
		productPageModelMother.primaryProductPage(catalogVersion, CmsPageStatus.DELETED, RESTORE_PRODUCT_PAGE_UID);

		final PageTemplateModel pageTemplate = pageTemplateModelMother.homepageTemplate(catalogVersion);
		final String restorePageUuid = getUuidForAppleStage(RESTORE_PRODUCT_PAGE_UID);
		final String pageTemplateUuid = getUuidForAppleStage(pageTemplate.getUid());

		final Map<String, String> titleMap = new HashMap<>();
		titleMap.put(ENGLISH.toLanguageTag(), "custom content page test");

		final Map<String, Object> pageToRestore = new HashMap<>();
		pageToRestore.put(TYPECODE, ProductPageModel._TYPECODE);
		pageToRestore.put(ITEMTYPE, ProductPageModel._TYPECODE);
		pageToRestore.put(NAME, RESTORE_PRODUCT_PAGE_NAME);
		pageToRestore.put(UID, RESTORE_PRODUCT_PAGE_UID);
		pageToRestore.put(PAGESTATUS, ACTIVE.name());
		pageToRestore.put(DEFAULTPAGE, true);
		pageToRestore.put(CATALOG_VERSION, catalogVersionUUID);
		pageToRestore.put(UUID, restorePageUuid);
		pageToRestore.put(FIELD_PAGE_REPLACE, true);
		pageToRestore.put(TITLE, titleMap);
		pageToRestore.put(MASTER_TEMPLATE, pageTemplateUuid);

		// WHEN
		final Map<String, Object> createdItem = defaultCMSItemFacade.updateItem(restorePageUuid, pageToRestore);

		// THEN
		assertThat(originalPage.getPageStatus(), is(DELETED));
	}

	protected void createElectronicsSite()
	{
		siteModelMother.createNorthAmericaElectronicsWithAppleStagedCatalog();
	}

	protected String getUuidForAppleStage(final String uid)
	{
		final EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey();
		itemComposedKey.setCatalogId(ID_APPLE.name());
		itemComposedKey.setCatalogVersion(STAGED.getVersion());
		itemComposedKey.setItemId(uid);

		return itemComposedKey.toEncoded();
	}

}
