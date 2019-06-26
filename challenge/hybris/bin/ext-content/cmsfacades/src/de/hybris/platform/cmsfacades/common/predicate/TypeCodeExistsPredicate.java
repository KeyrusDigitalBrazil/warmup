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
package de.hybris.platform.cmsfacades.common.predicate;

import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if a given type code maps to an existing composed type.
 * <p>
 * Returns <tt>TRUE</tt> if the composed type exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class TypeCodeExistsPredicate implements Predicate<String>
{

	private TypeService typeService;

	@Override
	public boolean test(String target)
	{
		boolean result = true;
		try
		{
			getTypeService().getComposedTypeForCode(target);
		}
		catch (final UnknownIdentifierException e)
		{
			result = false;
		}
		return result;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(TypeService typeService)
	{
		this.typeService = typeService;
	}

}
