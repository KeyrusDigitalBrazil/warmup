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
package de.hybris.platform.cmsfacades.navigations.populator.data;

import static de.hybris.platform.cms2.constants.Cms2Constants.ROOT;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This populator will populate the {@link CMSNavigationNodeModel}'s the parent node if it has changed.
 */
public class NavigationNodeDataToModelParentPopulator implements Populator<NavigationNodeData, CMSNavigationNodeModel>
{

	private CMSNavigationService navigationService;

	private CMSAdminSiteService adminSiteService;

	@Override
	public void populate(final NavigationNodeData source, final CMSNavigationNodeModel target) throws ConversionException
	{
		// this logic is to be executed when the parent node has changed.
		if (!StringUtils.equals(source.getParentUid(), target.getParent().getUid()))
		{
			final CMSNavigationNodeModel parentNode;
			if (StringUtils.equals(ROOT, source.getParentUid()))
			{
				final CatalogVersionModel catalogVersionModel = getAdminSiteService().getActiveCatalogVersion();
				parentNode = getNavigationService().getSuperRootNavigationNode(catalogVersionModel);
			}
			else
			{
				try
				{
					parentNode = getNavigationService().getNavigationNodeForId(source.getParentUid());
				}
				catch (final CMSItemNotFoundException e)
				{
					throw new ConversionException("Error populating CMSNavigationNodeModel. Parent does not exist.", e);
				}
			}
			// update parent and parent's children if the uid is not the same between the source and target
			getNavigationService().move(target, parentNode);
		}
	}

	protected CMSNavigationService getNavigationService()
	{
		return navigationService;
	}

	@Required
	public void setNavigationService(final CMSNavigationService navigationService)
	{
		this.navigationService = navigationService;
	}

	protected CMSAdminSiteService getAdminSiteService()
	{
		return adminSiteService;
	}

	@Required
	public void setAdminSiteService(final CMSAdminSiteService adminSiteService)
	{
		this.adminSiteService = adminSiteService;
	}
}
