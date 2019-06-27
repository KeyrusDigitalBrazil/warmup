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
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import org.springframework.beans.factory.annotation.Required;

import java.util.function.Predicate;


/**
 * Predicate to check existence of label or id for a ContentPage.
 * <p>
 * Returns <tt>TRUE</tt> if the given label or id exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class ContentPageLabelOrIdExistsPredicate implements Predicate<String>
{
	private CMSPageService cmsPageService;

	@Override
	@SuppressWarnings("squid:S1166")
	public boolean test(String pageLabelOrId)
	{
		try
		{
			getCmsPageService().getPageForLabelOrId(pageLabelOrId);
			return true;
		}
		catch (RuntimeException | CMSItemNotFoundException e)
		{
			return false;
		}
	}

	protected CMSPageService getCmsPageService()
	{
		return cmsPageService;
	}

	@Required
	public void setCmsPageService(CMSPageService cmsPageService)
	{
		this.cmsPageService = cmsPageService;
	}
}
