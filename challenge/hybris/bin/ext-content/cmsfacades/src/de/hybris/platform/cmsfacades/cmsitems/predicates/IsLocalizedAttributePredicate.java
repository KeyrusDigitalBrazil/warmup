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
package de.hybris.platform.cmsfacades.cmsitems.predicates;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.function.Predicate;

/**
 * Returns true if this attribute is localized as per our platform type system.
 */
public class IsLocalizedAttributePredicate implements Predicate<AttributeDescriptorModel>
{
	@Override
	public boolean test(final AttributeDescriptorModel attribute)
	{
		return attribute.getLocalized();
	}
}
