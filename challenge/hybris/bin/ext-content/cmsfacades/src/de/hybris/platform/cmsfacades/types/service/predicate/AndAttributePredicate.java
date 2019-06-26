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

import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;

/**
 * Predicate to test that a list of {@link Predicate<AttributeDescriptorModel>} are matched
 */
public class AndAttributePredicate implements Predicate<AttributeDescriptorModel>
{

	private List<Predicate<AttributeDescriptorModel>> predicates;

	@Override
	public boolean test(final AttributeDescriptorModel attributeDescriptor)
	{
		//check that there is NO predicate that returns false
		return getPredicates().stream().map(predicate -> predicate.test(attributeDescriptor)).allMatch(isSuccessful -> isSuccessful == true);
	}

 
	protected List<Predicate<AttributeDescriptorModel>> getPredicates()
	{
		return predicates;
	}
	
	@Required
	public void setPredicates(List<Predicate<AttributeDescriptorModel>> predicates)
	{
		this.predicates = predicates;
	}
}
