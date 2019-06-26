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
import de.hybris.platform.cms2.enums.LinkTargets;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cmsfacades.util.builder.LinkComponentModelBuilder;
import de.hybris.platform.cmsfacades.util.dao.impl.LinkComponentDao;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;


public class LinkComponentModelMother extends AbstractModelMother<CMSLinkComponentModel>
{
	public static final String UID_PRODUCT_LINK = "uid-test-link-component-product";
	public static final String UID_CATEGORY_LINK = "uid-test-link-component-category";
	public static final String UID_CONTENT_PAGE_LINK = "uid-test-link-component-content-page";
	public static final String UID_EXTERNAL_LINK = "uid-test-link-component-external-link";
	public static final String NAME_PRODUCT_LINK = "name-test-link-component-product";
	public static final String NAME_CATEGORY_LINK = "name-test-link-component-category";
	public static final String NAME_CONTENT_PAGE_LINK = "name-test-link-component-content-page";
	public static final String NAME_EXTERNAL_LINK = "name-test-link-component-external-link";

	private LinkComponentDao linkComponentDao;
	private ProductModelMother productModelMother;
	private CategoryModelMother categoryModelMother;
	private ContentPageModelMother contentPageModelMother;
	private CatalogVersionModelMother catalogVersionModelMother;

	public CMSLinkComponentModel createProductLinkComponentModel(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> getLinkComponentDao().getByUidAndCatalogVersion(UID_PRODUCT_LINK, catalogVersion),
				() -> LinkComponentModelBuilder.aModel() //
				.withUid(UID_PRODUCT_LINK) //
				.withCatalogVersion(catalogVersion) //
				.withLinkName(NAME_PRODUCT_LINK, Locale.ENGLISH) //
				.withProduct(getProductModelMother()
						.createMouseProduct(getCatalogVersionModelMother().createLaptopOnlineCatalogVersionModel())) //
				.withExternal(false) //
				.withTarget(LinkTargets.SAMEWINDOW) //
				.build());
	}

	public CMSLinkComponentModel createCategoryLinkComponentModel(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> getLinkComponentDao().getByUidAndCatalogVersion(UID_CATEGORY_LINK, catalogVersion),
				() -> LinkComponentModelBuilder.aModel() //
				.withUid(UID_CATEGORY_LINK) //
				.withCatalogVersion(catalogVersion) //
				.withLinkName(NAME_CATEGORY_LINK, Locale.ENGLISH) //
				.withCategory(getCategoryModelMother()
						.createAccessoriesCategory(getCatalogVersionModelMother().createLaptopOnlineCatalogVersionModel())) //
				.withExternal(false) //
				.withTarget(LinkTargets.SAMEWINDOW) //
				.build());
	}

	public CMSLinkComponentModel createContentPageLinkComponentModel(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> getLinkComponentDao().getByUidAndCatalogVersion(UID_CONTENT_PAGE_LINK, catalogVersion),
				() -> LinkComponentModelBuilder.aModel() //
				.withUid(UID_CONTENT_PAGE_LINK) //
				.withCatalogVersion(catalogVersion) //
				.withLinkName(NAME_CONTENT_PAGE_LINK, Locale.ENGLISH) //
				.withContentPage(contentPageModelMother.homePage(catalogVersion)) //
				.withExternal(false) //
				.withTarget(LinkTargets.SAMEWINDOW) //
				.build());
	}

	public CMSLinkComponentModel createExternalLinkComponentModel(final CatalogVersionModel catalogVersion)
	{
		return getOrSaveAndReturn( //
				() -> getLinkComponentDao().getByUidAndCatalogVersion(UID_EXTERNAL_LINK, catalogVersion),
				() -> LinkComponentModelBuilder.aModel() //
				.withUid(UID_EXTERNAL_LINK) //
				.withCatalogVersion(catalogVersion) //
				.withLinkName(NAME_EXTERNAL_LINK, Locale.ENGLISH) //
				.withUrl("http://google.com") //
				.withExternal(true) //
				.withTarget(LinkTargets.NEWWINDOW) //
				.build());
	}

	protected LinkComponentDao getLinkComponentDao()
	{
		return linkComponentDao;
	}

	@Required
	public void setLinkComponentDao(final LinkComponentDao linkComponentDao)
	{
		this.linkComponentDao = linkComponentDao;
	}

	protected ProductModelMother getProductModelMother()
	{
		return productModelMother;
	}

	@Required
	public void setProductModelMother(final ProductModelMother productModelMother)
	{
		this.productModelMother = productModelMother;
	}

	protected CatalogVersionModelMother getCatalogVersionModelMother()
	{
		return catalogVersionModelMother;
	}

	@Required
	public void setCatalogVersionModelMother(final CatalogVersionModelMother catalogVersionModelMother)
	{
		this.catalogVersionModelMother = catalogVersionModelMother;
	}

	protected CategoryModelMother getCategoryModelMother()
	{
		return categoryModelMother;
	}

	@Required
	public void setCategoryModelMother(final CategoryModelMother categoryModelMother)
	{
		this.categoryModelMother = categoryModelMother;
	}

	protected ContentPageModelMother getContentPageModelMother()
	{
		return contentPageModelMother;
	}

	@Required
	public void setContentPageModelMother(final ContentPageModelMother contentPageModelMother)
	{
		this.contentPageModelMother = contentPageModelMother;
	}

}
