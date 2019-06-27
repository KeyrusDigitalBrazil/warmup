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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;


public class ContentSlotForTemplateModelBuilder
{

	private final ContentSlotForTemplateModel model;

	private ContentSlotForTemplateModelBuilder()
	{
		model = new ContentSlotForTemplateModel();
	}

	private ContentSlotForTemplateModelBuilder(ContentSlotForTemplateModel model)
	{
		this.model = model;
	}

	protected ContentSlotForTemplateModel getModel()
	{
		return this.model;
	}

	public static ContentSlotForTemplateModelBuilder aModel()
	{
		return new ContentSlotForTemplateModelBuilder();
	}

	public static ContentSlotForTemplateModelBuilder fromModel(ContentSlotForTemplateModel model)
	{
		return new ContentSlotForTemplateModelBuilder(model);
	}

	public ContentSlotForTemplateModelBuilder withCatalogVersion(CatalogVersionModel model)
	{
		getModel().setCatalogVersion(model);
		return this;
	}

	public ContentSlotForTemplateModelBuilder withContentSlot(ContentSlotModel contentSlot)
	{
		getModel().setContentSlot(contentSlot);
		return this;
	}

	public ContentSlotForTemplateModelBuilder withPageTemplate(PageTemplateModel pageTemplate)
	{
		getModel().setPageTemplate(pageTemplate);
		return this;
	}

	public ContentSlotForTemplateModelBuilder withPosition(String position)
	{
		getModel().setPosition(position);
		return this;
	}

	public ContentSlotForTemplateModelBuilder withUid(String uid)
	{
		getModel().setUid(uid);
		return this;
	}

	public ContentSlotForTemplateModelBuilder withAllowOverwrite(Boolean allowOverwrite)
	{
		getModel().setAllowOverwrite(allowOverwrite);
		return this;
	}

	public ContentSlotForTemplateModel build()
	{
		return this.getModel();
	}
}
