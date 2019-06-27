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

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminRestrictionService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if a given restriction uid maps to an existing restriction.
 * <p>
 * Returns <tt>TRUE</tt> if the restriction exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class RestrictionExistsPredicate implements Predicate<String>
{
	private CMSAdminRestrictionService adminRestrictionService;

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
			getAdminRestrictionService().getRestriction(target);
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException | CMSItemNotFoundException e)
		{
			result = false;
		}
		return result;
	}

	protected CMSAdminRestrictionService getAdminRestrictionService()
	{
		return adminRestrictionService;
	}

	@Required
	public void setAdminRestrictionService(final CMSAdminRestrictionService adminRestrictionService)
	{
		this.adminRestrictionService = adminRestrictionService;
	}
}
