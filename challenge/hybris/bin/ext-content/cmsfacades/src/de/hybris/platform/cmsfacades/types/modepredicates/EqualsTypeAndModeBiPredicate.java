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

import java.util.function.BiPredicate;

import org.springframework.beans.factory.annotation.Required;

/***
 * BiPredicate implementation that checks if the typeCode and Mode are the same as the as defined for this Predicate instance. 
 */
public class EqualsTypeAndModeBiPredicate implements BiPredicate<String, StructureTypeMode>
{
	
	private String typeCode;
	private StructureTypeMode mode;
	
	@Override
	public boolean test(final String typeCode, final StructureTypeMode mode)
	{
		return getTypeCode().equals(typeCode) && getMode().equals(mode);
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

	protected String getTypeCode()
	{
		return typeCode;
	}

	@Required
	public void setTypeCode(final String typeCode)
	{
		this.typeCode = typeCode;
	}
}

