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
package de.hybris.platform.cmsfacades.cmsitems.converter;

import com.google.common.collect.Sets;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;


/**
 * This {@link Converter} is used to retrieve the list of attributes to convert. The returned list only
 * contains attributes that haven't been blacklisted.
 */
public class ComposedTypeToAttributeCollectionConverter implements Converter<ComposedTypeModel, Collection<AttributeDescriptorModel>>,
		InitializingBean
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private Map<String, String> typeBlacklistedAttributeMap;
	private Map<String, Set<String>> blacklistedAttributes;
	private Set<String> blacklistedTypes;

	// --------------------------------------------------------------------------
	// Public API
	// --------------------------------------------------------------------------
	@Override
	public Collection<AttributeDescriptorModel> convert(ComposedTypeModel typeModel)
	{
		if( typeModel == null )
		{
			return null;
		}

		Collection<ComposedTypeModel> typeModels = new ArrayList<>(typeModel.getAllSuperTypes());
		typeModels.add(typeModel);

		return typeModels.stream()
				.filter(type -> !getBlacklistedTypes().contains(type.getCode()))
				.map(this::collectAttributeDescriptors)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		blacklistedAttributes = getTypeBlacklistedAttributeMap()
				.entrySet().stream() //
				.collect(toMap(entry -> entry.getKey(), entry -> {
					final String[] attributes = entry.getValue().replaceAll("^[,\\s]+", "").split("[,\\s]+");
					return Sets.newHashSet(attributes);
				}));
	}

	// --------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------
	/**
	 * Collects attributes of a given type that is not blacklisted until it reaches the root type.
	 * @param composedType the type which the attributes are collected from.
	 */
	protected Collection<AttributeDescriptorModel> collectAttributeDescriptors(ComposedTypeModel composedType)
	{
		Set<String> blacklistedAttributes = Optional
				.ofNullable(getBlacklistedAttributes().get(composedType.getCode()))
				.orElse(new HashSet<>());

		return composedType.getDeclaredattributedescriptors().stream()
				.filter(attr -> !blacklistedAttributes.contains(attr.getQualifier()))
				.collect(Collectors.toList());
	}

	// --------------------------------------------------------------------------
	// Getters/Setters
	// --------------------------------------------------------------------------
	protected Map<String, String> getTypeBlacklistedAttributeMap()
	{
		return typeBlacklistedAttributeMap;
	}

	@Required
	public void setTypeBlacklistedAttributeMap(Map<String, String> typeBlacklistedAttributeMap)
	{
		this.typeBlacklistedAttributeMap = typeBlacklistedAttributeMap;
	}

	protected Map<String, Set<String>> getBlacklistedAttributes()
	{
		return blacklistedAttributes;
	}

	protected Set<String> getBlacklistedTypes()
	{
		return blacklistedTypes;
	}

	@Required
	public void setBlacklistedTypes(Set<String> blacklistedTypes)
	{
		this.blacklistedTypes = blacklistedTypes;
	}
}
