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
package de.hybris.platform.cmsfacades.types.modepredicates;

import de.hybris.platform.cmsfacades.data.StructureTypeMode;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.function.BiPredicate;

import org.springframework.beans.factory.annotation.Required;

/**
 * BiPredicate that tests if the {@link StructureTypeMode} is the same as defined for this instance of predicate. 
 */
public class EqualsModeAttributeBiPredicate implements BiPredicate<AttributeDescriptorModel, StructureTypeMode>
{
	private StructureTypeMode mode;
	
	@Override
	public boolean test(final AttributeDescriptorModel attributeDescriptorModel, final StructureTypeMode structureTypeMode)
	{
		return structureTypeMode == mode;
	}

	protected StructureTypeMode getMode()
	{
		return mode;
	}

	@Required
	public void setMode(final StructureTypeMode mode)
	{
		this.mode = mode;
	}
}
