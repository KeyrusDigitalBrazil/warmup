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
package de.hybris.platform.cmsfacades.pagetypesrestrictiontypes.impl;

import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.RestrictionTypeModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.data.PageTypeRestrictionTypeData;
import de.hybris.platform.cmsfacades.pagetypesrestrictiontypes.PageTypeRestrictionTypeFacade;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of <code>PageTypesRestrictionTypesFacade</code>. This uses {@link CMSAdminPageService} to
 * retrieve page types and restriction types respectively.
 */
public class DefaultPageTypeRestrictionTypeFacade implements PageTypeRestrictionTypeFacade
{
	private CMSAdminPageService adminPageService;

	@Override
	public List<PageTypeRestrictionTypeData> getRestrictionTypesForAllPageTypes()
	{
		final List<PageTypeRestrictionTypeData> results = new ArrayList<>();

		getAdminPageService().getAllPageTypes().forEach(pageType -> {
			pageType.getRestrictionTypes()
			.forEach(restrictionType -> results.add(buildPageTypesRestrictionTypesData(pageType, restrictionType)));
		});
		return results;
	}

	/**
	 * Build a new page restriction dto to hold a single pageType - restrictionType pair.
	 *
	 * @param pageType
	 *           - the page type
	 * @param restrictionType
	 *           - the restriction type
	 * @return a page types - restriction types dto
	 */
	protected PageTypeRestrictionTypeData buildPageTypesRestrictionTypesData(final CMSPageTypeModel pageType,
			final RestrictionTypeModel restrictionType)
	{
		final PageTypeRestrictionTypeData dto = new PageTypeRestrictionTypeData();
		dto.setPageType(pageType.getCode());
		dto.setRestrictionType(restrictionType.getCode());
		return dto;
	}

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
