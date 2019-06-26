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
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;

import java.util.List;


public class MediaContainerModelBuilder
{

	private final MediaContainerModel model;

	private MediaContainerModelBuilder()
	{
		model = new MediaContainerModel();
	}

	private MediaContainerModelBuilder(MediaContainerModel model)
	{
		this.model = model;
	}

	protected MediaContainerModel getModel()
	{
		return this.model;
	}

	public static MediaContainerModelBuilder aModel()
	{
		return new MediaContainerModelBuilder();
	}

	public static MediaContainerModelBuilder fromModel(MediaContainerModel model)
	{
		return new MediaContainerModelBuilder(model);
	}

	public MediaContainerModelBuilder withCatalogVersion(CatalogVersionModel catalogVersion)
	{
		getModel().setCatalogVersion(catalogVersion);
		return this;
	}

	public MediaContainerModelBuilder withQualifier(String qualifier)
	{
		getModel().setQualifier(qualifier);
		return this;
	}

	public MediaContainerModelBuilder withMediaList(List<MediaModel> mediaList)
	{
		getModel().setMedias(mediaList);
		return this;
	}

	public MediaContainerModel build()
	{
		return this.getModel();
	}

}
