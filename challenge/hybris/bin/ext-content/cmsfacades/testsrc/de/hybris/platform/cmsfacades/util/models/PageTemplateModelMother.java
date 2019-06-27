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
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cmsfacades.util.builder.PageTemplateModelBuilder;
import de.hybris.platform.cmsfacades.util.dao.impl.PageTemplateDao;
import de.hybris.platform.core.model.media.MediaModel;

import org.springframework.beans.factory.annotation.Required;


public class PageTemplateModelMother extends AbstractModelMother<PageTemplateModel>
{
	public static final String MEDIA_DESKTOP = "media-desktop";
	public static final String UID_HOME_PAGE = "uid-home-page-template";
	public static final String UID_SEARCH_PAGE = "uid-search-page-template";
	public static final String TEMPLATE_NAME_HOME_PAGE = "template-name-home-page-template";
	public static final String TEMPLATE_NAME_SEARCH_PAGE = "template-name-search-page-template";

	private PageTemplateDao pageTemplateDao;
	private ContentSlotNameModelMother contentSlotNameModelMother;
	private CMSPageTypeModelMother cmsPageTypeModelMother;
	private MediaModelMother mediaModelMother;

	protected PageTemplateModel defaultPageTemplate(final CatalogVersionModel catalogVersion)
	{
		return PageTemplateModelBuilder.aModel() //
				.withActive(Boolean.TRUE) //
				.withCatalogVersion(catalogVersion) //
				.build();
	}

	public PageTemplateModel homepageTemplate(final CatalogVersionModel catalogVersion)
	{
		final MediaModel previewIcon = mediaModelMother.createLogoMediaModelWithCode(catalogVersion, MEDIA_DESKTOP);
		return getOrSaveAndReturn( //
				() -> pageTemplateDao.getByUidAndCatalogVersion(UID_HOME_PAGE, catalogVersion), //
				() -> PageTemplateModelBuilder.fromModel(defaultPageTemplate(catalogVersion)) //
						.withUid(UID_HOME_PAGE) //
						.withFrontendTemplateName(TEMPLATE_NAME_HOME_PAGE) //
						.withPreviewIcon(previewIcon).withRestrictedPageTypes( //
								cmsPageTypeModelMother.ContentPage(), //
								cmsPageTypeModelMother.CatalogPage()) //
						.build());
	}

	public PageTemplateModel searchPageTemplate(final CatalogVersionModel catalogVersion)
	{
		final MediaModel previewIcon = mediaModelMother.createLogoMediaModelWithCode(catalogVersion, MEDIA_DESKTOP);
		return getOrSaveAndReturn( //
				() -> pageTemplateDao.getByUidAndCatalogVersion(UID_SEARCH_PAGE, catalogVersion), //
				() -> PageTemplateModelBuilder.fromModel(defaultPageTemplate(catalogVersion)) //
						.withUid(UID_SEARCH_PAGE) //
						.withFrontendTemplateName(TEMPLATE_NAME_SEARCH_PAGE) //
						.withPreviewIcon(previewIcon).withRestrictedPageTypes( //
								cmsPageTypeModelMother.ContentPage(), //
								cmsPageTypeModelMother.CatalogPage()) //
						.build());
	}

	protected PageTemplateDao getPageTemplateDao()
	{
		return pageTemplateDao;
	}

	@Required
	public void setPageTemplateDao(final PageTemplateDao pageTemplateDao)
	{
		this.pageTemplateDao = pageTemplateDao;
	}

	protected ContentSlotNameModelMother getContentSlotNameModelMother()
	{
		return contentSlotNameModelMother;
	}

	@Required
	public void setContentSlotNameModelMother(final ContentSlotNameModelMother contentSlotNameModelMother)
	{
		this.contentSlotNameModelMother = contentSlotNameModelMother;
	}

	protected CMSPageTypeModelMother getCmsPageTypeModelMother()
	{
		return cmsPageTypeModelMother;
	}

	@Required
	public void setCmsPageTypeModelMother(final CMSPageTypeModelMother cmsPageTypeModelMother)
	{
		this.cmsPageTypeModelMother = cmsPageTypeModelMother;
	}

	@Required
	public void setMediaModelMother(final MediaModelMother mediaModelMother)
	{
		this.mediaModelMother = mediaModelMother;
	}
}
