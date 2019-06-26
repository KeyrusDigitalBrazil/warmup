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
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;

import java.util.Arrays;
import java.util.Date;

public class ContentSlotModelBuilder {

	private final ContentSlotModel model;

	private ContentSlotModelBuilder()
	{
		model = new ContentSlotModel();
	}

	private ContentSlotModelBuilder(ContentSlotModel model)
	{
		this.model = model;
	}

	protected ContentSlotModel getModel()
	{
		return this.model;
	}

	public static ContentSlotModelBuilder aModel()
	{
		return new ContentSlotModelBuilder();
	}

	public static ContentSlotModelBuilder fromModel(ContentSlotModel model)
	{
		return new ContentSlotModelBuilder(model);
	}

	public ContentSlotModelBuilder withCatalogVersion(CatalogVersionModel model)
	{
		getModel().setCatalogVersion(model);
		return this;
	}

	public ContentSlotModelBuilder withName(String name)
	{
		getModel().setName(name);
		return this;
	}

	public ContentSlotModelBuilder withActiveFrom(Date from)
	{
		getModel().setActiveFrom(from);
		return this;
	}

	public ContentSlotModelBuilder withActiveUntil(Date to)
	{
		getModel().setActiveUntil(to);
		return this;
	}

	public ContentSlotModelBuilder withIsActive(Boolean active)
	{
		getModel().setActive(active);
		return this;
	}

	public ContentSlotModelBuilder withUid(String uid)
	{
		getModel().setUid(uid);
		return this;
	}

	public ContentSlotModelBuilder withCmsComponents(AbstractCMSComponentModel... components)
	{
		getModel().setCmsComponents(Arrays.asList(components));
		return this;
	}

	public ContentSlotModel build()
	{
		return this.getModel();
	}
}
