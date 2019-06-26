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

import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.LinkedList;
import java.util.List;


/**
 * Default implementation of <code>ComponentTypeAttributeStructure</code>. This is a simple POJO implementation.
 */
public class DefaultComponentTypeAttributeStructure implements ComponentTypeAttributeStructure
{
	private String qualifier;
	private String typecode;
	private List<Populator<AttributeDescriptorModel, ComponentTypeAttributeData>> populators = new LinkedList<>();
	private Integer position = Integer.MAX_VALUE;

	public DefaultComponentTypeAttributeStructure()
	{
	}

	public DefaultComponentTypeAttributeStructure(final ComponentTypeAttributeStructure attribute)
	{
		this.qualifier = attribute.getQualifier();
		this.typecode = attribute.getTypecode();
		this.populators = new LinkedList<>(attribute.getPopulators());
		this.position = attribute.getPosition();
	}

	@Override
	public String getQualifier()
	{
		return qualifier;
	}

	@Override
	public void setQualifier(final String qualifier)
	{
		this.qualifier = qualifier;
	}

	@Override
	public String getTypecode()
	{
		return typecode;
	}

	@Override
	public void setTypecode(final String typecode)
	{
		this.typecode = typecode;
	}

	@Override
	public List<Populator<AttributeDescriptorModel, ComponentTypeAttributeData>> getPopulators()
	{
		return populators;
	}

	@Override
	public void setPopulators(final List<Populator<AttributeDescriptorModel, ComponentTypeAttributeData>> populators)
	{
		this.populators = populators;
	}

	@Override
	public Integer getPosition()
	{
		return position;
	}

	public void setPosition(final Integer position)
	{
		this.position = position;
	}
	
}
