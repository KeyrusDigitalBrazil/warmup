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

import de.hybris.platform.catalog.CatalogService;
import org.springframework.beans.factory.annotation.Required;

import java.util.function.Predicate;


/**
 * Predicate to check existence of catalog code.
 * <p>
 * Returns <tt>TRUE</tt> if the given catalog code exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class CatalogCodeExistsPredicate implements Predicate<String>
{
	private CatalogService catalogService;

	@Override
	@SuppressWarnings("squid:S1166")
	public boolean test(String catalogCode)
	{
		try
		{
			getCatalogService().getCatalogForId(catalogCode);
			return true;
		}
		catch (RuntimeException e)
		{
			return false;
		}
	}

	protected CatalogService getCatalogService()
	{
		return catalogService;
	}

	@Required
	public void setCatalogService(CatalogService catalogService)
	{
		this.catalogService = catalogService;
	}
}
