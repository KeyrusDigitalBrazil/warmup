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


import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Objects;
import java.util.function.BiPredicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if a given catalog and version exist.
 * <p>
 * Returns <tt>TRUE</tt> if the given catalog version exists in the provided catalog; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class CatalogVersionExistsPredicate implements BiPredicate<String, String>
{
	private CatalogVersionService catalogVersionService;
	private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;

	@Override
	public boolean test(final String catalogId, final String catalogVersionId)
	{
		boolean result;

		try
		{
			result = getSessionSearchRestrictionsDisabler().execute(() -> {
				return Objects.nonNull(getCatalogVersionService().getCatalogVersion(catalogId, catalogVersionId));
			});
		}
		catch (final UnknownIdentifierException e)
		{
			result = false;
		}
		return result;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected SessionSearchRestrictionsDisabler getSessionSearchRestrictionsDisabler()
	{
		return sessionSearchRestrictionsDisabler;
	}

	@Required
	public void setSessionSearchRestrictionsDisabler(final SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler)
	{
		this.sessionSearchRestrictionsDisabler = sessionSearchRestrictionsDisabler;
	}
}
