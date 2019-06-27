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

import de.hybris.platform.cms2.model.CMSComponentTypeModel;
import de.hybris.platform.cms2.model.ComponentTypeGroupModel;
import de.hybris.platform.cms2.model.contents.ContentSlotNameModel;

import java.util.Arrays;

import com.google.common.collect.Sets;


public class CMSComponentTypeModelBuilder
{

	private final CMSComponentTypeModel model;

	private CMSComponentTypeModelBuilder()
	{
		model = new CMSComponentTypeModel();
	}

	private CMSComponentTypeModelBuilder(CMSComponentTypeModel model)
	{
		this.model = model;
	}

	protected CMSComponentTypeModel getModel()
	{
		return this.model;
	}

	public static CMSComponentTypeModelBuilder aModel()
	{
		return new CMSComponentTypeModelBuilder();
	}

	public static CMSComponentTypeModelBuilder fromModel(CMSComponentTypeModel model)
	{
		return new CMSComponentTypeModelBuilder(model);
	}

	public CMSComponentTypeModelBuilder withCode(String code)
	{
		getModel().setCode(code);
		return this;
	}

	public CMSComponentTypeModelBuilder withName(String name)
	{
		getModel().setName(name);
		return this;
	}

	public CMSComponentTypeModelBuilder withComponentTypeGroups(ComponentTypeGroupModel... componentTypeGroups)
	{
		getModel().setComponentTypeGroups(Sets.newHashSet(Arrays.asList(componentTypeGroups)));
		return this;
	}

	public CMSComponentTypeModelBuilder withContentSlotNames(ContentSlotNameModel... contentSlotNames)
	{
		getModel().setContentSlotNames(Sets.newHashSet(Arrays.asList(contentSlotNames)));
		return this;
	}

	public CMSComponentTypeModel build()
	{
		return this.getModel();
	}
}
