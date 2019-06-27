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
import de.hybris.platform.cms2lib.model.components.FlashComponentModel;
import de.hybris.platform.core.model.media.MediaModel;

import java.util.Locale;


public class FlashComponentModelBuilder {

	private final FlashComponentModel model;

	private FlashComponentModelBuilder()
	{
		model = new FlashComponentModel();
	}

	private FlashComponentModelBuilder(final FlashComponentModel model)
	{
		this.model = model;
	}

	protected FlashComponentModel getModel()
	{
		return this.model;
	}

	public static FlashComponentModelBuilder aModel()
	{
		return new FlashComponentModelBuilder();
	}

	public static FlashComponentModelBuilder fromModel(final FlashComponentModel model)
	{
		return new FlashComponentModelBuilder(model);
	}

	public FlashComponentModelBuilder withUid(final String uid)
	{
		getModel().setUid(uid);
		return this;
	}

	public FlashComponentModelBuilder withCatalogVersion(final CatalogVersionModel cv)
	{
		getModel().setCatalogVersion(cv);
		return this;
	}

	public FlashComponentModelBuilder isVisible(final boolean visible)
	{
		getModel().setVisible(visible);
		return this;
	}

	public FlashComponentModelBuilder withName(final String name)
	{
		getModel().setName(name);
		return this;
	}

	public FlashComponentModelBuilder withUrlLink(final String urlLink)
	{
		getModel().setUrlLink(urlLink);
		return this;
	}

	public FlashComponentModelBuilder withExternal(final Boolean external)
	{
		getModel().setExternal(external);
		return this;
	}

	public FlashComponentModelBuilder withMedia(final MediaModel mediaModel)
	{
		getModel().setMedia(mediaModel, Locale.ENGLISH);
		return this;
	}

	public FlashComponentModel build()
	{
		return this.getModel();
	}
}
