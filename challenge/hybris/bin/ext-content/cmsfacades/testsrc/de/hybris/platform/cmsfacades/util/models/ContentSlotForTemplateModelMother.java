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
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.cmsfacades.util.builder.ContentSlotForTemplateModelBuilder;
import de.hybris.platform.cmsfacades.util.dao.impl.ContentSlotForTemplateDao;

import org.springframework.beans.factory.annotation.Required;


public class ContentSlotForTemplateModelMother extends AbstractModelMother<ContentSlotForTemplateModel>
{
	public static final String UID_HEADER_HOMEPAGE = "uid-header-homepage-template-relation";
	public static final String UID_FOOTER_HOMEPAGE = "uid-footer-homepage-template-relation";
	public static final String UID_FOOTER_SEARCH_PAGE = "uid-footer-search-template-relation";

	private ContentSlotForTemplateDao contentSlotForTemplateDao;
	private ContentSlotModelMother contentSlotModelMother;
	private PageTemplateModelMother pageTemplateModelMother;

	public ContentSlotForTemplateModel FooterHomepage(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> contentSlotForTemplateDao.getByUidAndCatalogVersion(UID_FOOTER_HOMEPAGE, catalogVersion), //
				() -> ContentSlotForTemplateModelBuilder.aModel() //
				.withUid(UID_FOOTER_HOMEPAGE) //
				.withCatalogVersion(catalogVersion) //
				.withAllowOverwrite(Boolean.TRUE) //
				.withContentSlot(contentSlotModelMother.createFooterEmptySlot(catalogVersion)) //
				.withPageTemplate(pageTemplateModelMother.homepageTemplate(catalogVersion)) //
				.withPosition(ContentSlotNameModelMother.NAME_FOOTER) //
				.build());
	}

	public ContentSlotForTemplateModel FooterSearchPage(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> contentSlotForTemplateDao.getByUidAndCatalogVersion(UID_FOOTER_SEARCH_PAGE, catalogVersion), //
				() -> ContentSlotForTemplateModelBuilder.aModel() //
				.withUid(UID_FOOTER_SEARCH_PAGE) //
				.withCatalogVersion(catalogVersion) //
				.withAllowOverwrite(Boolean.TRUE) //
				.withContentSlot(contentSlotModelMother.createFooterEmptySlot(catalogVersion)) //
				.withPageTemplate(pageTemplateModelMother.searchPageTemplate(catalogVersion)) //
				.withPosition(ContentSlotNameModelMother.NAME_FOOTER) //
				.build());
	}

	protected ContentSlotForTemplateDao getContentSlotForTemplateDao()
	{
		return contentSlotForTemplateDao;
	}

	@Required
	public void setContentSlotForTemplateDao(final ContentSlotForTemplateDao contentSlotForTemplateDao)
	{
		this.contentSlotForTemplateDao = contentSlotForTemplateDao;
	}

	protected ContentSlotModelMother getContentSlotModelMother()
	{
		return contentSlotModelMother;
	}

	@Required
	public void setContentSlotModelMother(final ContentSlotModelMother contentSlotModelMother)
	{
		this.contentSlotModelMother = contentSlotModelMother;
	}

	protected PageTemplateModelMother getPageTemplateModelMother()
	{
		return pageTemplateModelMother;
	}

	@Required
	public void setPageTemplateModelMother(final PageTemplateModelMother pageTemplateModelMother)
	{
		this.pageTemplateModelMother = pageTemplateModelMother;
	}

}
