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

import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.version.service.CMSVersionService;

import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Strings;


/**
 * Predicate to test if a cms version has a label or not.
 * <p>
 * Only labeled versions are exposed to the user. All unlabeled cms version entries are considered "revisions". A page
 * version will consist of multiple revision entries.
 * <p>
 * Note: Users should not manipulate revisions because they are internal states managed by the labeled version.
 * <p>
 * Returns <tt>TRUE</tt> if the cms version has a label; <tt>FALSE</tt> otherwise.
 */
public class VersionHasLabelPredicate implements Predicate<String>
{
	private CMSVersionService cmsVersionService;

	@Override
	public boolean test(final String uid)
	{
		boolean result = false;
		final Optional<CMSVersionModel> versionOptional = getCmsVersionService().getVersionByUid(uid);
		if (versionOptional.isPresent())
		{
			result = Strings.isNullOrEmpty(versionOptional.get().getLabel()) ? false : true;
		}
		return result;
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
