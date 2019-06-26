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
package de.hybris.platform.cmsfacades.types.service.impl;

import static com.google.common.collect.Lists.newArrayList;

import de.hybris.platform.cmsfacades.types.service.StructureTypeModeAttributeFilter;
import de.hybris.platform.cmsfacades.data.StructureTypeMode;

import java.util.List;
import java.util.function.BiPredicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link StructureTypeModeAttributeFilter}.
 */
public class DefaultStructureTypeModeAttributeFilter implements StructureTypeModeAttributeFilter
{
	
	private BiPredicate<String, StructureTypeMode> constrainedBy; 
	private List<String> includes = newArrayList();
	private List<String> excludes = newArrayList();
	private List<String> order = newArrayList();

	@Override
	public List<String> getIncludes()
	{
		return includes;
	}

	public void setIncludes(final List<String> includes)
	{
		this.includes = includes;
	}

	@Override
	public List<String> getExcludes()
	{
		return excludes;
	}

	public void setExcludes(final List<String> excludes)
	{
		this.excludes = excludes;
	}

	@Override
	public List<String> getOrder()
	{
		return order;
	}

	public void setOrder(final List<String> order)
	{
		this.order = order;
	}

	@Override
	public BiPredicate<String, StructureTypeMode> getConstrainedBy()
	{
		return constrainedBy;
	}

	@Required
	public void setConstrainedBy(final BiPredicate<String, StructureTypeMode> constrainedBy)
	{
		this.constrainedBy = constrainedBy;
	}
}
