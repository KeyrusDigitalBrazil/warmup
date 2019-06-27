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
package de.hybris.platform.cmsfacades.util.builder;

import static java.util.Locale.ENGLISH;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.enums.CmsApprovalStatus;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.core.model.media.MediaModel;


public class ProductPageModelBuilder
{
	private final ProductPageModel model;

	private ProductPageModelBuilder()
	{
		model = new ProductPageModel();
	}

	private ProductPageModelBuilder(final ProductPageModel model)
	{
		this.model = model;
	}

	protected ProductPageModel getModel()
	{
		return this.model;
	}

	public static ProductPageModelBuilder aModel()
	{
		return new ProductPageModelBuilder();
	}

	public static ProductPageModelBuilder fromModel(final ProductPageModel model)
	{
		return new ProductPageModelBuilder(model);
	}

	public ProductPageModelBuilder withCatalogVersion(final CatalogVersionModel model)
	{
		getModel().setCatalogVersion(model);
		return this;
	}

	public ProductPageModelBuilder withUid(final String uid)
	{
		getModel().setUid(uid);
		return this;
	}

	public ProductPageModelBuilder withDefaultPage(final Boolean isDefaultPage)
	{
		getModel().setDefaultPage(isDefaultPage);
		return this;
	}

	public ProductPageModelBuilder withMasterTemplate(final PageTemplateModel template)
	{
		getModel().setMasterTemplate(template);
		return this;
	}

	public ProductPageModelBuilder withApprovalStatus(final CmsApprovalStatus approvalStatus)
	{
		getModel().setApprovalStatus(approvalStatus);
		return this;
	}

	public ProductPageModelBuilder withName(final String name)
	{
		getModel().setName(name);
		return this;
	}

	public ProductPageModelBuilder withThumbnail(final MediaModel thumbnail)
	{
		getModel().setPreviewImage(thumbnail);
		return this;
	}

	public ProductPageModelBuilder withEnglishTitle(final String title)
	{
		getModel().setTitle(title, ENGLISH);
		return this;
	}

	public ProductPageModelBuilder withOnlyOneRestrictionMustApply(final Boolean onlyOneRestrictionMustApply)
	{
		getModel().setOnlyOneRestrictionMustApply(onlyOneRestrictionMustApply);
		return this;
	}

	public ProductPageModelBuilder withPageStatus(final CmsPageStatus pageStatus)
	{
		getModel().setPageStatus(pageStatus);
		return this;
	}

	public ProductPageModel build()
	{
		return this.getModel();
	}
}
