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
package de.hybris.platform.cmsfacades.common.service.impl;

import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cmsfacades.rendering.RestrictionContextProvider;
import de.hybris.platform.cmsfacades.common.service.RestrictionAwareService;
import org.springframework.beans.factory.annotation.Required;

import java.util.function.Supplier;

/**
 * Default implementation of {@link RestrictionAwareService}. Sets restriction information in the session during the execution of a
 * supplier.
 */
public class DefaultRestrictionAwareService implements RestrictionAwareService
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private RestrictionContextProvider restrictionContextProvider;

	// --------------------------------------------------------------------------
	// Public API
	// --------------------------------------------------------------------------
	@Override
	public <T> T execute(RestrictionData restrictionData, Supplier<T> supplier)
	{
		try
		{
			getRestrictionContextProvider().setRestrictionInContext(restrictionData);
			return supplier.get();
		}
		finally
		{
			getRestrictionContextProvider().removeRestrictionFromContext();
		}
	}

	// --------------------------------------------------------------------------
	// Getters/Setters
	// --------------------------------------------------------------------------
	protected RestrictionContextProvider getRestrictionContextProvider()
	{
		return restrictionContextProvider;
	}

	@Required
	public void setRestrictionContextProvider(RestrictionContextProvider restrictionContextProvider)
	{
		this.restrictionContextProvider = restrictionContextProvider;
	}

}
