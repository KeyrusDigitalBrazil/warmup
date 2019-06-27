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
package de.hybris.platform.cmsfacades.types.service.predicate;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.validation.model.constraints.NotEmptyConstraintModel;
import de.hybris.platform.validation.model.constraints.jsr303.NotNullConstraintModel;

import java.util.HashSet;
import java.util.function.Predicate;

import com.google.common.collect.Sets;

/**
 * Predicate to test if a given attribute {@link AttributeDescriptorModel} is required.  
 */
public class RequiredStructureTypeAttributePredicate implements Predicate<AttributeDescriptorModel>
{
	@Override
	public boolean test(final AttributeDescriptorModel attributeDescriptor)
	{
		final HashSet<String> constraints = Sets.newHashSet(NotNullConstraintModel._TYPECODE, NotEmptyConstraintModel._TYPECODE);
		
		return attributeDescriptor.getConstraints() //
				.stream() //
				.anyMatch(attributeConstraintModel -> constraints.contains(attributeConstraintModel.getItemtype()));
	}

}
