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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;
import java.util.function.Predicate;


/**
 * Predicate to identify that clone context contains same catalog id and catalog version as active catalog id and catalog version.
 * It also returns true if clone context is undefined.
 */
public class CloneContextSameAsActiveCatalogVersionPredicate implements Predicate<Object>
{
	private CMSAdminSiteService cmsAdminSiteService;

	@Override public boolean test(Object o)
	{
		Map<String, String> cloneContext = getCmsAdminSiteService().getCloneContext();
		CatalogVersionModel targetVersionModel = getCmsAdminSiteService().getActiveCatalogVersion();

		return (cloneContext == null || (
				targetVersionModel.getVersion().equals(cloneContext.get(CmsfacadesConstants.CURRENT_CONTEXT_CATALOG_VERSION)) &&
						targetVersionModel.getCatalog().getId()
								.equals(cloneContext.get(CmsfacadesConstants.CURRENT_CONTEXT_CATALOG))));
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}
}
