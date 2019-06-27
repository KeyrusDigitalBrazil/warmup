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

import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructureRegistry;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;


/**
 * Default implementation of the <code>ComponentTypeStructureRegistry</code>. This implementation uses autowire-by-type
 * to inject all beans implementing {@link ComponentTypeStructure} and {@link ComponentTypeAttributeStructure}.
 *
 * <p>
 * All <code>ComponentTypeStructure</code> and <code>ComponentTypeAttributeStructure</code> elements will be merged into
 * a single set of elements that will represent the single source of truth for the Structure API.
 * </p>
 */
public class DefaultComponentTypeStructureRegistry implements ComponentTypeStructureRegistry, InitializingBean
{
	@Autowired
	private Set<ComponentTypeStructure> allComponentTypeStructures;

	private FacadeValidationService facadeValidationService;
	private Validator structureTypesPostCreationValidator;
	private TypeService typeService;
	private Set<String> cmsSupportedAbstractTypecodes;

	@Autowired
	private Set<ComponentTypeAttributeStructure> allComponentTypeAttributeStructures;

	private final Map<String, ComponentTypeStructure> componentTypeStructures = new HashMap<>();

	@Override
	public Optional<ComponentTypeAttributeStructure> getComponentTypeAttributeStructure(final String typecode, final String qualifier)
	{
		return Optional.ofNullable(Optional.ofNullable(getComponentTypeStructure(typecode))).orElse(Optional.empty())
				.map(type -> type.getAttributes().stream()) //
				.flatMap(stream -> stream.filter(attribute -> attribute.getQualifier().equals(qualifier)).findAny());
	}

	@Override
	public ComponentTypeStructure getComponentTypeStructure(final String typecode)
	{
		return getComponentTypeStructureMap().get(typecode);
	}

	@Override
	public ComponentTypeStructure getAbstractComponentTypeStructure(final String itemType)
	{
		return getCmsSupportedAbstractTypecodes().stream() //
				.filter(abstractTypeCode -> matchesComposedType(abstractTypeCode, itemType)) //
				.findFirst() //
				.map(abstractTypeCode -> getComponentTypeStructureMap().get(abstractTypeCode)).orElse(null);
	}

	/**
	 * Verifies that the given typecode has the same <code>ComposedType</code> as the provided itemtype.
	 *
	 * @param abstractTypeCode
	 *           - the typecode which <code>ComposedType</code> will be compared
	 * @param itemType
	 *           - the itemtype used for comparison
	 * @return <tt>TRUE</tt> if the <code>ComposedType</code> of the given typecode matches the itemtype specified;
	 *         <tt>FALSE</tt> otherwise
	 */
	protected boolean matchesComposedType(final String abstractTypeCode, final String itemType)
	{
		final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(abstractTypeCode);
		return composedType.getItemtype().equals(itemType);
	}

	@Override
	public Collection<ComponentTypeStructure> getComponentTypeStructures()
	{
		return getComponentTypeStructureMap().values();
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		getAllComponentTypeStructures().stream().forEach(type -> putOrUpdateStructureType(type));

		getAllComponentTypeAttributeStructures().stream() //
				.filter(attr -> attr.getClass().equals(DefaultComponentTypeAttributeStructure.class)) //
				.forEach(attribute -> updateAttributes(attribute));
		// post-validates the structure type
		allComponentTypeStructures.stream().forEach(
				componentType -> getFacadeValidationService().validate(getStructureTypesPostCreationValidator(), componentType));
	}

	/**
	 * If the map of component type structures is empty, then add this element to the map. Otherwise, update the set of
	 * populators for the element found in the map by adding the populators from the given element.
	 *
	 * @param type
	 *           - the component type structure to process
	 */
	protected void putOrUpdateStructureType(final ComponentTypeStructure type)
	{
		final Optional<ComponentTypeStructure> mapType = Optional
				.ofNullable(getComponentTypeStructureMap().get(type.getTypecode()));
		mapType.ifPresent(internal -> internal.getPopulators().addAll(type.getPopulators()));

		if (!mapType.isPresent())
		{
			getComponentTypeStructureMap().put(type.getTypecode(), type);
		}
	}

	/**
	 * If the component type structure matching the typecode given in the attribute does not have any attributes with the
	 * same qualifier as the given attribute, then add the attribute to the collection of attributes of the component
	 * type structure. Otherwise, if there is an attribute with the same qualifier already in the collection of
	 * attributes, then we add the populators of the given attribute to that attribute.
	 *
	 * @param attribute
	 *           - the attribute to process
	 * @throws IllegalArgumentException
	 *            when the typecode provided in the attributes does not match any component type structure in the map.
	 */
	protected void updateAttributes(final ComponentTypeAttributeStructure attribute) throws IllegalArgumentException
	{
		final ComponentTypeStructure type = getOrCreateComponentTypeStructure(attribute.getTypecode());

		final Optional<ComponentTypeAttributeStructure> attributeInType = type.getAttributes().stream()
				.filter(attr -> attr.getQualifier().equals(attribute.getQualifier())) //
				.findFirst();

		if (attributeInType.isPresent())
		{
			attributeInType.get().getPopulators().addAll(attribute.getPopulators());
		}
		else
		{
			type.getAttributes().add(attribute);
		}
	}

	/**
	 * Gets or Creates a new Component Type Structure.  
	 * @param typeCode the type code used to get the 
	 * @return the {@link ComponentTypeStructure} instance. 
	 */
	protected ComponentTypeStructure getOrCreateComponentTypeStructure(final String typeCode)
	{
		return getComponentTypeStructureMap().computeIfAbsent(typeCode, key -> {
			final DefaultComponentTypeStructure componentTypeStructure = new DefaultComponentTypeStructure();
			componentTypeStructure.setTypecode(typeCode);
			return componentTypeStructure;
		});
	}

	protected Map<String, ComponentTypeStructure> getComponentTypeStructureMap()
	{
		return componentTypeStructures;
	}

	protected Set<ComponentTypeStructure> getAllComponentTypeStructures()
	{
		return allComponentTypeStructures;
	}

	public void setAllComponentTypeStructures(final Set<ComponentTypeStructure> allComponentTypeStructures)
	{
		this.allComponentTypeStructures = allComponentTypeStructures;
	}

	protected Set<ComponentTypeAttributeStructure> getAllComponentTypeAttributeStructures()
	{
		return allComponentTypeAttributeStructures;
	}

	public void setAllComponentTypeAttributeStructures(
			final Set<ComponentTypeAttributeStructure> allComponentTypeAttributeStructures)
	{
		this.allComponentTypeAttributeStructures = allComponentTypeAttributeStructures;
	}

	protected FacadeValidationService getFacadeValidationService()
	{
		return facadeValidationService;
	}

	@Required
	public void setFacadeValidationService(final FacadeValidationService facadeValidationService)
	{
		this.facadeValidationService = facadeValidationService;
	}

	protected Validator getStructureTypesPostCreationValidator()
	{
		return structureTypesPostCreationValidator;
	}

	@Required
	public void setStructureTypesPostCreationValidator(final Validator structureTypesPostCreationValidator)
	{
		this.structureTypesPostCreationValidator = structureTypesPostCreationValidator;
	}

	protected Set<String> getCmsSupportedAbstractTypecodes()
	{
		return cmsSupportedAbstractTypecodes;
	}

	@Required
	public void setCmsSupportedAbstractTypecodes(final Set<String> cmsSupportedAbstractTypecodes)
	{
		this.cmsSupportedAbstractTypecodes = cmsSupportedAbstractTypecodes;
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

}
