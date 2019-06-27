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
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if a given content slot uid maps to an existing content slot.
 * <p>
 * Returns <tt>TRUE</tt> if the content slot exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class ContentSlotExistsInCatalogVersionsPredicate implements Predicate<String>
{
	private CMSAdminContentSlotService cmsAdminContentSlotService;
	private CatalogVersionService catalogVersionService;

	/**
	 * Suppress sonar warning (squid:S1166 | Exception handlers should preserve the original exception) : The exception
	 * is correctly handled in the catch clause.
	 */
	@SuppressWarnings("squid:S1166")
	@Override
	public boolean test(final String target)
	{
		boolean result = true;
		try
		{
			getCmsAdminContentSlotService().getContentSlotForIdAndCatalogVersions(target,
					getCatalogVersionService().getSessionCatalogVersions());
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			result = false;
		}
		return result;
	}

	protected CMSAdminContentSlotService getCmsAdminContentSlotService()
	{
		return cmsAdminContentSlotService;
	}

	@Required
	public void setCmsAdminContentSlotService(final CMSAdminContentSlotService cmsAdminContentSlotService)
	{
		this.cmsAdminContentSlotService = cmsAdminContentSlotService;
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
}
