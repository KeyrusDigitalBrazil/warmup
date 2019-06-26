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
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;

import java.util.Map;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to identify if the restore context contains replace boolean set to true for the give active page.
 *
 * <p>
 * Returns <tt>TRUE</tt> if for the active page, the replace is set to true; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class PageRestoreWithReplacePredicate implements Predicate<AbstractPageModel>
{
	private CMSAdminSiteService cmsAdminSiteService;

	@Override
	public boolean test(final AbstractPageModel page)
	{
		final Map<String, Object> restoreContext = getCmsAdminSiteService().getRestoreContext();
		return restoreContext != null && restoreContext.get(CmsfacadesConstants.FIELD_PAGE_REPLACE) != null
				&& restoreContext.get(CmsfacadesConstants.FIELD_PAGE_REPLACE).equals(Boolean.TRUE)
				&& page.getPageStatus().equals(CmsPageStatus.ACTIVE);
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}

}
