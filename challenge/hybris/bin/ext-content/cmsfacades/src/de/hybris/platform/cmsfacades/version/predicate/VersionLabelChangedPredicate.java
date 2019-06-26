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
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Optional;
import java.util.function.BiPredicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if the label field has changed for the cms version
 * <p>
 * Returns <tt>TRUE</tt> if the cms version exists AND the label field has changed; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class VersionLabelChangedPredicate implements BiPredicate<String, String>
{
	private CMSVersionService cmsVersionService;

	@Override
	public boolean test(final String versionUid, final String newLabel)
	{
		try
		{
			final Optional<CMSVersionModel> cmsVersionModel = getCmsVersionService().getVersionByUid(versionUid);

			return cmsVersionModel.isPresent() && ((cmsVersionModel.get().getLabel() == null && newLabel != null)
					|| !cmsVersionModel.get().getLabel().equals(newLabel));
		}
		catch (final UnknownIdentifierException e)
		{
			return false;
		}
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
