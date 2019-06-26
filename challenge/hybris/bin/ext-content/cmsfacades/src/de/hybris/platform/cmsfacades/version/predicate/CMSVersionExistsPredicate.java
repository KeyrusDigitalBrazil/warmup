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
package de.hybris.platform.cmsfacades.version.predicate;

import de.hybris.platform.cms2.version.service.CMSVersionService;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if a cms version exists.
 * <p>
 * Returns <tt>TRUE</tt> if the cms version exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class CMSVersionExistsPredicate implements Predicate<String>
{
	private CMSVersionService cmsVersionService;

	@Override
	public boolean test(final String uid)
	{
		return getCmsVersionService().getVersionByUid(uid).isPresent();
	}

	protected CMSVersionService getCmsVersionService()
	{
		return cmsVersionService;
	}

	@Required
	public void setCmsVersionService(final CMSVersionService cmsVersionService)
	{
		this.cmsVersionService = cmsVersionService;
	}
}
