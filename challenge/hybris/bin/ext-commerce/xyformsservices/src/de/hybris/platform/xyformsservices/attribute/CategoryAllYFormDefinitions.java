/*
* [y] hybris Platform
*
* Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
*
* This software is the confidential and proprietary information of SAP
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with SAP.
*/
package de.hybris.platform.xyformsservices.attribute;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import de.hybris.platform.xyformsservices.helpers.YFormDefinitionHelper;
import de.hybris.platform.xyformsservices.model.YFormDefinitionModel;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Supports return all the {@link YFormDefinitionModel}s from the Category hierarchy
 */
public class CategoryAllYFormDefinitions
		implements DynamicAttributeHandler<List<YFormDefinitionModel>, CategoryModel>, Serializable
{
	private transient YFormDefinitionHelper yFormDefinitionHelper;

	/**
	 * Returns all the {@link YFormDefinitionModel}s that are assigned to the category and its supercategories
	 */
	@Override
	public List<YFormDefinitionModel> get(final CategoryModel category)
	{
		return getYFormDefinitionHelper().getAllYFormDefinitions(category);
	}

	@Override
	public void set(final CategoryModel category, final List<YFormDefinitionModel> value)
	{
		throw new UnsupportedOperationException();
	}

	protected YFormDefinitionHelper getYFormDefinitionHelper()
	{
		return yFormDefinitionHelper;
	}

	@Required
	public void setYFormDefinitionHelper(final YFormDefinitionHelper yFormDefinitionHelper)
	{
		this.yFormDefinitionHelper = yFormDefinitionHelper;
	}

}
