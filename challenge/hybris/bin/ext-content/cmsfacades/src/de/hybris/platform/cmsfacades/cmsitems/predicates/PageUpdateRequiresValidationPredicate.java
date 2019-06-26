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

import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.springframework.beans.factory.annotation.Required;

import java.util.Arrays;
import java.util.function.Predicate;


/**
 * Predicate to test if a given page must be validated during an update.
 * A page must be validated for an update when the page already exists and the page is going to end up
 * in an ACTIVE state after the update (regardless of its previous state).
 *
 * <p>
 *    Returns <tt>TRUE</tt> if the page requires to be validated; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class PageUpdateRequiresValidationPredicate implements Predicate<AbstractPageModel>
{
	private CMSAdminPageService adminPageService;

	// ---------------------------------------------------------------------------------------------------
	// Public API
	// ---------------------------------------------------------------------------------------------------
	@Override
	public boolean test(final AbstractPageModel pageToUpdate)
	{
		return pageAlreadyExists(pageToUpdate) && pageToUpdate.getPageStatus().equals(CmsPageStatus.ACTIVE);
	}

	// ---------------------------------------------------------------------------------------------------
	// Helper Methods
	// ---------------------------------------------------------------------------------------------------
	protected boolean pageAlreadyExists(final AbstractPageModel pageToEvaluate)
	{
		try
		{
			getAdminPageService().getPageForIdFromActiveCatalogVersionByPageStatuses(pageToEvaluate.getUid(),
					Arrays.asList(CmsPageStatus.values()));
			return true;
		}
		catch (UnknownIdentifierException e)
		{
			return false;
		}
	}

	// ---------------------------------------------------------------------------------------------------
	// Getters/Setters
	// ---------------------------------------------------------------------------------------------------
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
