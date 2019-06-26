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

import com.google.common.collect.Sets;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.contents.ContentSlotNameModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.core.model.media.MediaModel;

import java.util.Arrays;


public class PageTemplateModelBuilder
{

	private final PageTemplateModel model;

	private PageTemplateModelBuilder()
	{
		model = new PageTemplateModel();
	}

	private PageTemplateModelBuilder(PageTemplateModel model)
	{
		this.model = model;
	}

	protected PageTemplateModel getModel()
	{
		return this.model;
	}

	public static PageTemplateModelBuilder aModel()
	{
		return new PageTemplateModelBuilder();
	}

	public static PageTemplateModelBuilder fromModel(PageTemplateModel model)
	{
		return new PageTemplateModelBuilder(model);
	}

	public PageTemplateModelBuilder withUid(String uid)
	{
		getModel().setUid(uid);
		return this;
	}

	public PageTemplateModelBuilder withFrontendTemplateName(String frontendTemplateName)
	{
		getModel().setFrontendTemplateName(frontendTemplateName);
		return this;
	}

	public PageTemplateModelBuilder withCatalogVersion(CatalogVersionModel cv)
	{
		getModel().setCatalogVersion(cv);
		return this;
	}

	public PageTemplateModelBuilder withActive(Boolean value)
	{
		getModel().setActive(value);
		return this;
	}

	public PageTemplateModelBuilder withPreviewIcon(MediaModel previewIcon)
	{
		getModel().setPreviewIcon(previewIcon);
		return this;
	}

	public PageTemplateModelBuilder withContentSlots(ContentSlotNameModel... slots)
	{
		getModel().setAvailableContentSlots(Arrays.asList(slots));
		return this;
	}

	public PageTemplateModelBuilder withRestrictedPageTypes(CMSPageTypeModel... restrictedPageTypes)
	{
		getModel().setRestrictedPageTypes(Sets.newHashSet(Arrays.asList(restrictedPageTypes)));
		return this;
	}

	public PageTemplateModel build()
	{
		return this.getModel();
	}


}
