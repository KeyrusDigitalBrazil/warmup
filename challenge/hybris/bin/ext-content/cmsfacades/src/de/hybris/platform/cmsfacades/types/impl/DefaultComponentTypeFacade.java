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
package de.hybris.platform.cmsfacades.types.impl;

import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.data.StructureTypeMode;
import de.hybris.platform.cmsfacades.types.ComponentTypeFacade;
import de.hybris.platform.cmsfacades.types.ComponentTypeNotFoundException;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructureService;
import de.hybris.platform.cmsfacades.types.service.StructureTypeModeService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * This implementation of the {@link ComponentTypeFacade} will get the
 * {@link de.hybris.platform.core.model.type.ComposedTypeModel} items and convert them to DTOs.
 * <p>
 * The types available are determined by using the {@link ComponentTypeStructureService} to get all registered component
 * types.
 * </p>
 */
public class DefaultComponentTypeFacade implements ComponentTypeFacade
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultComponentTypeFacade.class);
	private static final String COMPONENT = "COMPONENT";

	private ComponentTypeStructureService componentTypeStructureService;
	private StructureTypeModeService structureTypeModeService;
	private Converter<ComponentTypeStructure, ComponentTypeData> componentTypeStructureConverter;
	private TypeService typeService;

	@Override
	public List<ComponentTypeData> getAllComponentTypes()
	{
		return getComponentTypeStructureService().getComponentTypeStructures().stream() //
				.filter(structure -> structure.getTypecode() != null) //
				.map(this::convertComponentStructure) //
				.filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public List<ComponentTypeData> getAllComponentTypes(final String category)
	{
		return getAllComponentTypes().stream() //
				.filter(componentTypeData -> !COMPONENT.equals(componentTypeData.getCategory())
						|| getIsComponentPredicate().test(componentTypeData.getCode()))
				.filter(componentTypeData -> StringUtils.equals(category, componentTypeData.getCategory()))
				.collect(Collectors.toList());
	}

	@Override
	public ComponentTypeData getComponentTypeByCode(final String code) throws ComponentTypeNotFoundException
	{
		final ComponentTypeStructure componentType = getComponentTypeStructureService().getComponentTypeStructure(code);
		if (componentType == null)
		{
			throw new ComponentTypeNotFoundException("Component Type [" + code + "] is not supported.");
		}
		return getComponentTypeStructureConverter().convert(componentType);
	}

	@Override
	public ComponentTypeData getComponentTypeByCodeAndMode(final String code, final String mode)
			throws ComponentTypeNotFoundException
	{
		final ComponentTypeStructure componentType = getComponentTypeStructureService().getComponentTypeStructure(code);
		if (componentType == null)
		{
			throw new ComponentTypeNotFoundException("Component Type [" + code + "] is not supported.");
		}
		return getComponentTypeStructureConverter().convert(getStructureTypeModeService()
				.getComponentTypeByCodeAndMode(componentType.getTypecode(), StructureTypeMode.valueOf(mode)));
	}

	/**
	 * Converts {@link ComponentTypeStructure} to {@link ComponentTypeData}. Returns <tt>null</tt> in case of
	 * {@link TypePermissionException}.
	 *
	 * @param structure
	 *           the {@link ComponentTypeStructure}
	 * @return the {@link ComponentTypeData}
	 */
	protected ComponentTypeData convertComponentStructure(final ComponentTypeStructure structure)
	{
		try
		{
			return getComponentTypeStructureConverter().convert(structure);
		}
		catch (final TypePermissionException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Insufficient type permission", e);
			}
			return null;
		}
	}

	/**
	 * Determines whether a type code refers to a type extending AbstractCMSComponentModel
	 *
	 * @return a boolean
	 */
	protected Predicate<String> getIsComponentPredicate()
	{
		return type -> getTypeService().isAssignableFrom(AbstractCMSComponentModel._TYPECODE, type);
	}

	protected ComponentTypeStructureService getComponentTypeStructureService()
	{
		return componentTypeStructureService;
	}

	@Required
	public void setComponentTypeStructureService(final ComponentTypeStructureService componentTypeStructureService)
	{
		this.componentTypeStructureService = componentTypeStructureService;
	}

	protected Converter<ComponentTypeStructure, ComponentTypeData> getComponentTypeStructureConverter()
	{
		return componentTypeStructureConverter;
	}

	@Required
	public void setComponentTypeStructureConverter(
			final Converter<ComponentTypeStructure, ComponentTypeData> componentTypeStructureConverter)
	{
		this.componentTypeStructureConverter = componentTypeStructureConverter;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected StructureTypeModeService getStructureTypeModeService()
	{
		return structureTypeModeService;
	}

	@Required
	public void setStructureTypeModeService(final StructureTypeModeService structureTypeModeService)
	{
		this.structureTypeModeService = structureTypeModeService;
	}
}
