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
package de.hybris.platform.cmsfacades.types.service.validator;

import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.types.populator.DependsOnComponentTypeAttributePopulator;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.google.common.collect.Sets;


/**
 * Validates the depdendsOn attribute on {@link ComponentTypeStructure}
 */
public class DependsOnAttributePostCreationValidator implements Validator
{
	private static final String ATTRIBUTES = "attributes";

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return ComponentTypeStructure.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		final ComponentTypeStructure componentTypeData = (ComponentTypeStructure) target;

		// validate dependsOnAttribute
		if (!CollectionUtils.isEmpty(componentTypeData.getAttributes()))
		{
			final Set<String> dependsOnSet = componentTypeData.getAttributes() //
					.stream() //
					.flatMap(attribute -> attribute.getPopulators().stream())
					.filter(attributePopulator -> attributePopulator instanceof DependsOnComponentTypeAttributePopulator)
					.map(attributePopulator -> (DependsOnComponentTypeAttributePopulator) attributePopulator)
					.map(attributePopulator -> attributePopulator.getDependsOn()).collect(Collectors.toSet());

			final Set<String> qualifiers = componentTypeData.getAttributes() //
					.stream() //
					.map(ComponentTypeAttributeStructure::getQualifier) //
					.filter(Objects::nonNull) //
					.collect(Collectors.toSet());

			final Set<String> invalidQualifiers = Sets.difference(dependsOnSet, qualifiers);

			if (!invalidQualifiers.isEmpty())
			{
				invalidQualifiers //
						.stream() //
						.forEach(
								qualifier -> errors.rejectValue(ATTRIBUTES, CmsfacadesConstants.TYPES_INVALID_QUALIFIER_ATTR, new Object[]
								{ qualifier }, null));
			}
		}
	}
}
