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

import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.data.StructureTypeCategory;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Default implementation of <code>ComponentTypeStructure</code>.
 *
 * <p>
 * The attributes should be populated by {@link #getAttributes()}, then using {@link Set#add(Object)} or
 * {@link Set#addAll(java.util.Collection)}.
 * </p>
 * <p>
 * For backward compatibility purposes, default category value is assigned to enum type
 * {@link StructureTypeCategory#COMPONENT}.
 * </p>
 */
public class DefaultComponentTypeStructure implements ComponentTypeStructure
{
	private String typecode;
	private StructureTypeCategory category = StructureTypeCategory.COMPONENT;
	private Class typeDataClass;
	private final Set<ComponentTypeAttributeStructure> attributes = new LinkedHashSet<>();
	private List<Populator<ComposedTypeModel, ComponentTypeData>> populators = new LinkedList<>();

	public DefaultComponentTypeStructure()
	{
	}

	public DefaultComponentTypeStructure(final ComponentTypeStructure type)
	{
		this.typecode = type.getTypecode();
		this.typeDataClass = type.getTypeDataClass();
		this.category = type.getCategory();
		this.attributes.addAll(type.getAttributes());
		this.populators = new LinkedList<>(type.getPopulators());
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
	public StructureTypeCategory getCategory()
	{
		return category;
	}

	@Override
	public void setCategory(final StructureTypeCategory category)
	{
		this.category = category;
	}

	@Override
	public Class getTypeDataClass()
	{
		return typeDataClass;
	}

	@Override
	public void setTypeDataClass(final Class typeDataClass)
	{
		this.typeDataClass = typeDataClass;
	}

	@Override
	public Set<ComponentTypeAttributeStructure> getAttributes()
	{
		return attributes;
	}

	@Override
	public List<Populator<ComposedTypeModel, ComponentTypeData>> getPopulators()
	{
		return populators;
	}

	@Override
	public void setPopulators(final List<Populator<ComposedTypeModel, ComponentTypeData>> populators)
	{
		this.populators = populators;
	}

}
