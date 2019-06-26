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
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.ABTestCMSComponentContainerModel;

import java.util.List;


public class ABTestCMSComponentContainerModelBuilder
{
	private final ABTestCMSComponentContainerModel model;

	private ABTestCMSComponentContainerModelBuilder()
	{
		model = new ABTestCMSComponentContainerModel();
	}

	private ABTestCMSComponentContainerModelBuilder(ABTestCMSComponentContainerModel model)
	{
		this.model = model;
	}

	protected ABTestCMSComponentContainerModel getModel()
	{
		return this.model;
	}

	public static ABTestCMSComponentContainerModelBuilder aModel()
	{
		return new ABTestCMSComponentContainerModelBuilder();
	}

	public static ABTestCMSComponentContainerModelBuilder fromModel(ABTestCMSComponentContainerModel model)
	{
		return new ABTestCMSComponentContainerModelBuilder(model);
	}

	public ABTestCMSComponentContainerModelBuilder withUid(String uid)
	{
		getModel().setUid(uid);
		return this;
	}

	public ABTestCMSComponentContainerModelBuilder withCatalogVersion(CatalogVersionModel cv)
	{
		getModel().setCatalogVersion(cv);
		return this;
	}

	public ABTestCMSComponentContainerModelBuilder withSimpleCMSComponent(final List<SimpleCMSComponentModel> value)
	{
		getModel().setSimpleCMSComponents(value);
		return this;
	}

	public ABTestCMSComponentContainerModelBuilder withName(String name)
	{
		getModel().setName(name);
		return this;
	}

	public ABTestCMSComponentContainerModel build()
	{
		return this.getModel();
	}
}
