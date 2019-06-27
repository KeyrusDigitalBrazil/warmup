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
package de.hybris.platform.cmsfacades.util.models;

import static de.hybris.platform.cmsfacades.util.models.MediaModelMother.MediaTemplate.THUMBNAIL;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.enums.CmsApprovalStatus;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.util.builder.ContentPageModelBuilder;
import de.hybris.platform.cmsfacades.util.dao.impl.ContentPageDao;

import org.springframework.beans.factory.annotation.Required;


public class ContentPageModelMother extends AbstractModelMother<ContentPageModel>
{

	public static final String UID_HOMEPAGE = "uid-homepage";
	public static final String UID_HOMEPAGE_EU = "uid-homepage-eu";
	public static final String UID_SEARCHPAGE = "uid-searchpage";
	public static final String UID_PRIMARY_HOMEPAGE = "uid-primary-homepage";
	public static final String UID_PRIMARY_SEARCHPAGE = "uid-primary-searchpage";
	public static final String LABEL_HOMEPAGE = "/homepage";
	public static final String LABEL_SEARCHPAGE = "/searchpage";
	public static final String NAME_SUFFIX = "_page";
	public static final String NAME_HOMEPAGE = "Home";
	public static final String NAME_PRIMARY_HOMEPAGE = "Primary Home";
	public static final String NAME_HOMEPAGE_EU = "Home-Europe";
	public static final String NAME_PRIMARY_SEARCHPAGE = "Primary Search";
	public static final String NAME_SEARCHPAGE = "Search";
	public static final String TITLE_SUFFIX = "_pagetitle";
	public static final String TITLE_HOMEPAGE = NAME_HOMEPAGE + TITLE_SUFFIX;
	public static final String TITLE_HOMEPAGE_EU = NAME_HOMEPAGE_EU + TITLE_SUFFIX;
	public static final String TITLE_PRIMARY_HOMEPAGE = NAME_PRIMARY_HOMEPAGE + TITLE_SUFFIX;
	public static final String TITLE_SEARCHPAGE = NAME_SEARCHPAGE + TITLE_SUFFIX;
	public static final String TITLE_PRIMARY_SEARCHPAGE = NAME_PRIMARY_SEARCHPAGE + TITLE_SUFFIX;

	private ContentPageDao contentPageDao;
	private PageTemplateModelMother pageTemplateModelMother;
	private MediaModelMother mediaModelMother;

	public ContentPageModel homePage(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> getContentPageDao().getByUidAndCatalogVersion(UID_HOMEPAGE, catalogVersion), //
				() -> ContentPageModelBuilder.aModel() //
						.withUid(UID_HOMEPAGE) //
						.withCatalogVersion(catalogVersion) //
						.asHomepage() //
						.withMasterTemplate(pageTemplateModelMother.homepageTemplate(catalogVersion)) //
						.withDefaultPage(Boolean.FALSE) //
						.withLabel(LABEL_HOMEPAGE) //
						.withName(NAME_HOMEPAGE) //
						.withEnglishTitle(TITLE_HOMEPAGE) //
						.withPageStatus(CmsPageStatus.ACTIVE) //
						.withApprovalStatus(CmsApprovalStatus.APPROVED) //
						.withThumbnail(mediaModelMother.createMediaModel(catalogVersion, THUMBNAIL)).build());
	}

	public ContentPageModel homePageEurope(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> getContentPageDao().getByUidAndCatalogVersion(UID_HOMEPAGE_EU, catalogVersion), //
				() -> ContentPageModelBuilder.aModel() //
						.withUid(UID_HOMEPAGE_EU) //
						.withCatalogVersion(catalogVersion) //
						.asHomepage() //
						.withMasterTemplate(pageTemplateModelMother.homepageTemplate(catalogVersion)) //
						.withDefaultPage(Boolean.FALSE) //
						.withLabel(LABEL_HOMEPAGE) //
						.withName(NAME_HOMEPAGE_EU) //
						.withEnglishTitle(TITLE_HOMEPAGE_EU) //
						.withPageStatus(CmsPageStatus.ACTIVE) //
						.withApprovalStatus(CmsApprovalStatus.APPROVED) //
						.withThumbnail(mediaModelMother.createMediaModel(catalogVersion, THUMBNAIL)).build());
	}

	public ContentPageModel primaryHomePage(final CatalogVersionModel catalogVersion, final CmsPageStatus pageStatus)
	{
		return getOrSaveAndReturn( //
				() -> getContentPageDao().getByUidAndCatalogVersion(UID_PRIMARY_HOMEPAGE, catalogVersion), //
				() -> ContentPageModelBuilder.aModel() //
						.withUid(UID_PRIMARY_HOMEPAGE) //
						.withCatalogVersion(catalogVersion) //
						.asHomepage() //
						.withMasterTemplate(pageTemplateModelMother.homepageTemplate(catalogVersion)) //
						.withDefaultPage(Boolean.TRUE) //
						.withLabel(LABEL_HOMEPAGE) //
						.withName(NAME_PRIMARY_HOMEPAGE) //
						.withEnglishTitle(TITLE_PRIMARY_HOMEPAGE) //
						.withPageStatus(pageStatus) //
						.withApprovalStatus(CmsApprovalStatus.APPROVED) //
						.withThumbnail(mediaModelMother.createMediaModel(catalogVersion, THUMBNAIL)).build());
	}

	public ContentPageModel primaryHomePage(final CatalogVersionModel catalogVersion)
	{
		return primaryHomePage(catalogVersion, CmsPageStatus.ACTIVE);
	}

	public ContentPageModel searchPageFromHomePageTemplate(final CatalogVersionModel catalogVersion,
			final CmsPageStatus pageStatus)
	{
		return getOrSaveAndReturn( //
				() -> getContentPageDao().getByUidAndCatalogVersion(UID_SEARCHPAGE, catalogVersion), //
				() -> ContentPageModelBuilder.aModel() //
						.withUid(UID_SEARCHPAGE) //
						.withCatalogVersion(catalogVersion) //
						.withMasterTemplate(pageTemplateModelMother.homepageTemplate(catalogVersion)) //
						.withThumbnail(mediaModelMother.createMediaModel(catalogVersion, THUMBNAIL)) //
						.withDefaultPage(Boolean.FALSE) //
						.withEnglishTitle(TITLE_SEARCHPAGE) //
						.withLabel(LABEL_SEARCHPAGE) //
						.withPageStatus(pageStatus) //
						.withName(NAME_SEARCHPAGE).build());
	}

	public ContentPageModel searchPageFromHomePageTemplate(final CatalogVersionModel catalogVersion)
	{
		return this.searchPageFromHomePageTemplate(catalogVersion, CmsPageStatus.ACTIVE);
	}

	public ContentPageModel primarySearchPageFromHomePageTemplate(final CatalogVersionModel catalogVersion,
			final CmsPageStatus pageStatus)
	{
		return getOrSaveAndReturn( //
				() -> getContentPageDao().getByUidAndCatalogVersion(UID_PRIMARY_SEARCHPAGE, catalogVersion), //
				() -> ContentPageModelBuilder.aModel() //
						.withUid(UID_PRIMARY_SEARCHPAGE) //
						.withCatalogVersion(catalogVersion) //
						.withMasterTemplate(pageTemplateModelMother.homepageTemplate(catalogVersion)) //
						.withThumbnail(mediaModelMother.createMediaModel(catalogVersion, THUMBNAIL)) //
						.withDefaultPage(Boolean.TRUE) //
						.withEnglishTitle(TITLE_PRIMARY_SEARCHPAGE) //
						.withLabel(LABEL_SEARCHPAGE) //
						.withPageStatus(pageStatus) //
						.withName(NAME_PRIMARY_SEARCHPAGE).build());
	}

	public ContentPageModel primarySearchPageFromHomePageTemplate(final CatalogVersionModel catalogVersion)
	{
		return primarySearchPageFromHomePageTemplate(catalogVersion, CmsPageStatus.ACTIVE);
	}

	public ContentPageModel searchPage(final CatalogVersionModel catalogVersion)
	{
		return this.searchPage(catalogVersion, UID_SEARCHPAGE, NAME_SEARCHPAGE, TITLE_SEARCHPAGE, Boolean.FALSE);
	}

	public ContentPageModel primarySearchPage(final CatalogVersionModel catalogVersion)
	{
		return this.searchPage(catalogVersion, UID_PRIMARY_SEARCHPAGE, NAME_PRIMARY_SEARCHPAGE, TITLE_PRIMARY_SEARCHPAGE,
				Boolean.TRUE);
	}

	protected ContentPageModel searchPage(final CatalogVersionModel catalogVersion, final String uid, final String name,
			final String title, final boolean isPrimaryPage)
	{
		return getOrSaveAndReturn( //
				() -> getContentPageDao().getByUidAndCatalogVersion(UID_SEARCHPAGE, catalogVersion), //
				() -> ContentPageModelBuilder.aModel() //
						.withUid(uid) //
						.withCatalogVersion(catalogVersion) //
						.asHomepage() //
						.withMasterTemplate(pageTemplateModelMother.searchPageTemplate(catalogVersion)) //
						.withDefaultPage(isPrimaryPage) //
						.withLabel(LABEL_SEARCHPAGE) //
						.withName(name) //
						.withEnglishTitle(title) //
						.withApprovalStatus(CmsApprovalStatus.APPROVED) //
						.withPageStatus(CmsPageStatus.ACTIVE)
						.withThumbnail(mediaModelMother.createMediaModel(catalogVersion, THUMBNAIL)).build());
	}

	public ContentPageModel somePage(final CatalogVersionModel catalogVersion, final String uid, final String nameTitle,
			final CmsPageStatus pageStatus)
	{
		return this.somePage(catalogVersion, uid, nameTitle, pageStatus, Boolean.FALSE);
	}

	public ContentPageModel somePrimaryPage(final CatalogVersionModel catalogVersion, final String uid, final String nameTitle,
			final CmsPageStatus pageStatus)
	{
		return this.somePage(catalogVersion, uid, nameTitle, pageStatus, Boolean.TRUE);
	}

	protected ContentPageModel somePage(final CatalogVersionModel catalogVersion, final String uid, final String nameTitle,
			final CmsPageStatus pageStatus, final boolean isPrimaryPage)
	{
		return getOrSaveAndReturn( //
				() -> getContentPageDao().getByUidAndCatalogVersion(uid, catalogVersion), //
				() -> ContentPageModelBuilder.aModel() //
						.withUid(uid) //
						.withCatalogVersion(catalogVersion) //
						.withMasterTemplate(pageTemplateModelMother.homepageTemplate(catalogVersion)) //
						.withThumbnail(mediaModelMother.createMediaModel(catalogVersion, THUMBNAIL)) //
						.withName(nameTitle + NAME_SUFFIX) //
						.withDefaultPage(isPrimaryPage) //
						.withLabel("/" + nameTitle) //
						.withEnglishTitle(nameTitle + TITLE_SUFFIX) //
						.withApprovalStatus(CmsApprovalStatus.APPROVED) //
						.withPageStatus(pageStatus) //
						.withOnlyOneRestrictionMustApply(Boolean.TRUE).build());
	}

	public ContentPageDao getContentPageDao()
	{
		return contentPageDao;
	}

	@Required
	public void setContentPageDao(final ContentPageDao contentPageDao)
	{
		this.contentPageDao = contentPageDao;
	}

	public PageTemplateModelMother getPageTemplateModelMother()
	{
		return pageTemplateModelMother;
	}

	@Required
	public void setPageTemplateModelMother(final PageTemplateModelMother pageTemplateModelMother)
	{
		this.pageTemplateModelMother = pageTemplateModelMother;
	}

	public MediaModelMother getMediaModelMother()
	{
		return mediaModelMother;
	}

	@Required
	public void setMediaModelMother(final MediaModelMother mediaModelMother)
	{
		this.mediaModelMother = mediaModelMother;
	}
}
