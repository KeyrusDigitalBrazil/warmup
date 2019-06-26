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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cmsfacades.util.builder.ContentSlotForPageModelBuilder;
import de.hybris.platform.cmsfacades.util.dao.impl.ContentSlotForPageDao;

import org.springframework.beans.factory.annotation.Required;


public class ContentSlotForPageModelMother extends AbstractModelMother<ContentSlotForPageModel>
{
	public static final String UID_HEADER_HOMEPAGE = "uid-header-homepage";
	public static final String UID_HEADER_HOMEPAGE_EU = "uid-header-homepage-eu";
	public static final String UID_FOOTER_HOMEPAGE = "uid-footer-homepage";
	public static final String UID_FOOTER_SEARCHPAGE = "uid-footer-searchpage";

	private ContentSlotForPageDao contentSlotForPageDao;
	private ContentSlotModelMother contentSlotModelMother;
	private ContentPageModelMother contentPageModelMother;

	public ContentSlotForPageModel HeaderHomepage_ParagraphOnly(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> getContentSlotForPageDao().getByUidAndCatalogVersion(UID_HEADER_HOMEPAGE, catalogVersion), //
				() -> ContentSlotForPageModelBuilder.aModel() //
				.withUid(UID_HEADER_HOMEPAGE) //
				.withCatalogVersion(catalogVersion) //
				.withContentSlot(getContentSlotModelMother().createHeaderSlotWithParagraph(catalogVersion)) //
				.withPage(getContentPageModelMother().homePage(catalogVersion)) //
				.withPosition(ContentSlotNameModelMother.NAME_HEADER) //
				.build());
	}

	public ContentSlotForPageModel HeaderHomepageEurope_ParagraphOnly(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> getContentSlotForPageDao().getByUidAndCatalogVersion(UID_HEADER_HOMEPAGE_EU, catalogVersion), //
				() -> ContentSlotForPageModelBuilder.aModel() //
				.withUid(UID_HEADER_HOMEPAGE_EU) //
				.withCatalogVersion(catalogVersion) //
				.withContentSlot(getContentSlotModelMother().createHeaderEuropeSlotWithParagraph(catalogVersion)) //
				.withPage(getContentPageModelMother().homePageEurope(catalogVersion)) //
				.withPosition(ContentSlotNameModelMother.NAME_HEADER) //
				.build());
	}

	public ContentSlotForPageModel HeaderHomepage_FlashComponentOnly(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> getContentSlotForPageDao().getByUidAndCatalogVersion(UID_HEADER_HOMEPAGE, catalogVersion), //
				() -> ContentSlotForPageModelBuilder.aModel() //
				.withUid(UID_HEADER_HOMEPAGE) //
				.withCatalogVersion(catalogVersion) //
				.withContentSlot(getContentSlotModelMother().createHeaderSlotWithFlashComponent(catalogVersion)) //
				.withPage(getContentPageModelMother().homePage(catalogVersion)) //
				.withPosition(ContentSlotNameModelMother.NAME_HEADER) //
				.build());
	}

	public ContentSlotForPageModel HeaderHomepage_LinkOnly(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> getContentSlotForPageDao().getByUidAndCatalogVersion(UID_HEADER_HOMEPAGE, catalogVersion), //
				() -> ContentSlotForPageModelBuilder.aModel() //
				.withUid(UID_HEADER_HOMEPAGE) //
				.withCatalogVersion(catalogVersion) //
				.withContentSlot(getContentSlotModelMother().createHeaderSlotWithLink(catalogVersion)) //
				.withPage(getContentPageModelMother().homePage(catalogVersion)) //
				.withPosition(ContentSlotNameModelMother.NAME_HEADER) //
				.build());
	}

	public ContentSlotForPageModel HeaderHomepage_ParagraphAndLink(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> getContentSlotForPageDao().getByUidAndCatalogVersion(UID_HEADER_HOMEPAGE, catalogVersion), //
				() -> ContentSlotForPageModelBuilder.aModel() //
				.withUid(UID_HEADER_HOMEPAGE) //
				.withCatalogVersion(catalogVersion) //
				.withContentSlot(getContentSlotModelMother().createHeaderSlotWithParagraphAndLink(catalogVersion)) //
				.withPage(getContentPageModelMother().homePage(catalogVersion)) //
				.withPosition(ContentSlotNameModelMother.NAME_HEADER) //
				.build());
	}

	public ContentSlotForPageModel HeaderHomepageEurope_ParagraphAndLink(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> getContentSlotForPageDao().getByUidAndCatalogVersion(UID_HEADER_HOMEPAGE_EU, catalogVersion), //
				() -> ContentSlotForPageModelBuilder.aModel() //
				.withUid(UID_HEADER_HOMEPAGE_EU) //
				.withCatalogVersion(catalogVersion) //
				.withContentSlot(getContentSlotModelMother().createHeaderEuropeSlotWithParagraphAndLink(catalogVersion)) //
				.withPage(getContentPageModelMother().homePageEurope(catalogVersion)) //
				.withPosition(ContentSlotNameModelMother.NAME_HEADER) //
				.build());
	}


	public ContentSlotForPageModel HeaderHomePage_ContainerWithParagraphs(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> getContentSlotForPageDao().getByUidAndCatalogVersion(UID_HEADER_HOMEPAGE, catalogVersion), //
				() -> ContentSlotForPageModelBuilder.aModel() //
						.withUid(UID_HEADER_HOMEPAGE) //
						.withCatalogVersion(catalogVersion) //
						.withContentSlot(getContentSlotModelMother().createHeaderSlotWithABTestParagraphsContainer(catalogVersion)) //
						.withPage(getContentPageModelMother().homePage(catalogVersion))
						.withPosition(ContentSlotNameModelMother.NAME_HEADER)
						.build());
	}

	public ContentSlotForPageModel FooterHomepage_Empty(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> getContentSlotForPageDao().getByUidAndCatalogVersion(UID_FOOTER_HOMEPAGE, catalogVersion), //
				() -> ContentSlotForPageModelBuilder.aModel() //
				.withUid(UID_FOOTER_HOMEPAGE) //
				.withCatalogVersion(catalogVersion) //
				.withContentSlot(getContentSlotModelMother().createFooterEmptySlot(catalogVersion)) //
				.withPage(getContentPageModelMother().homePage(catalogVersion)) //
				.withPosition(ContentSlotNameModelMother.NAME_FOOTER) //
				.build());
	}

	public ContentSlotForPageModel FooterHomepage_FlashComponentOnly(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> getContentSlotForPageDao().getByUidAndCatalogVersion(UID_FOOTER_HOMEPAGE, catalogVersion), //
				() -> ContentSlotForPageModelBuilder.aModel() //
						.withUid(UID_FOOTER_HOMEPAGE) //
						.withCatalogVersion(catalogVersion) //
						.withContentSlot(getContentSlotModelMother().createFooterSlotWithFlashComponent(catalogVersion)) //
						.withPage(getContentPageModelMother().homePage(catalogVersion)) //
						.withPosition(ContentSlotNameModelMother.NAME_FOOTER) //
						.build());
	}

	public ContentSlotForPageModel FooterSearchPage_Empty(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> getContentSlotForPageDao().getByUidAndCatalogVersion(UID_FOOTER_SEARCHPAGE, catalogVersion), //
				() -> ContentSlotForPageModelBuilder.aModel() //
				.withUid(UID_FOOTER_SEARCHPAGE) //
				.withCatalogVersion(catalogVersion) //
				.withContentSlot(getContentSlotModelMother().createFooterEmptySlot(catalogVersion)) //
				.withPage(getContentPageModelMother().searchPageFromHomePageTemplate(catalogVersion)) //
				.withPosition(ContentSlotNameModelMother.NAME_FOOTER) //
				.build());
	}

	public ContentSlotForPageDao getContentSlotForPageDao()
	{
		return contentSlotForPageDao;
	}

	@Required
	public void setContentSlotForPageDao(final ContentSlotForPageDao contentSlotForPageDao)
	{
		this.contentSlotForPageDao = contentSlotForPageDao;
	}

	public ContentSlotModelMother getContentSlotModelMother()
	{
		return contentSlotModelMother;
	}

	@Required
	public void setContentSlotModelMother(final ContentSlotModelMother contentSlotModelMother)
	{
		this.contentSlotModelMother = contentSlotModelMother;
	}

	public ContentPageModelMother getContentPageModelMother()
	{
		return contentPageModelMother;
	}

	@Required
	public void setContentPageModelMother(final ContentPageModelMother contentPageModelMother)
	{
		this.contentPageModelMother = contentPageModelMother;
	}

}
