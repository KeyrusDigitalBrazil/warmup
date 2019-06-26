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

import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if a given page uid maps to an active existing page.
 * <p>
 * Returns <tt>TRUE</tt> if the page exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class PageExistsPredicate implements Predicate<String>
{

	private CMSAdminPageService adminPageService;

	@Override
	public boolean test(final String target)
	{
		boolean result = true;
		try
		{
			getAdminPageService().getPageForIdFromActiveCatalogVersion(target);
		}
		catch (final UnknownIdentifierException e)
		{
			result = false;
		}
		catch (final AmbiguousIdentifierException e)
		{
			result = true;
		}
		return result;
	}

	protected CMSAdminPageService getAdminPageService()
	{
		return adminPageService;
	}

	@Required
	public void setAdminPageService(final CMSAdminPageService adminPageService)
	{
		this.adminPageService = adminPageService;
	}

}
