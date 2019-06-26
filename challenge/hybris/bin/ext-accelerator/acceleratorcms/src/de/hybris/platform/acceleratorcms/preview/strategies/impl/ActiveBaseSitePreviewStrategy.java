/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.acceleratorcms.preview.strategies.impl;

import de.hybris.platform.acceleratorcms.preview.strategies.PreviewContextInformationLoaderStrategy;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.site.BaseSiteService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class ActiveBaseSitePreviewStrategy implements PreviewContextInformationLoaderStrategy
{
	private static final Logger LOG = Logger.getLogger(ActiveBaseSitePreviewStrategy.class);
	private BaseSiteService baseSiteService;

	@Override
	public void initContextFromPreview(final PreviewDataModel preview)
	{
		if (preview.getActiveSite() == null)
		{
			LOG.warn("Could not set active site. Reason: No active site was selected!");
		}
		else
		{
			getBaseSiteService().setCurrentBaseSite(preview.getActiveSite(), true);
		}
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}
}
