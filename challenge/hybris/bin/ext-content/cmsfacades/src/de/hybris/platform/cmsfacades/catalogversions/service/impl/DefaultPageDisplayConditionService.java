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
package de.hybris.platform.cmsfacades.catalogversions.service.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.catalogversions.service.PageDisplayConditionService;
import de.hybris.platform.cmsfacades.data.DisplayConditionData;
import de.hybris.platform.cmsfacades.data.OptionData;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link PageDisplayConditionService} to retrieve all display conditions available for a
 * given catalog version. A display condition determines if a page is: <br>
 * - a PRIMARY page ({@link AbstractPageModel#getDefaultPage()} {@code = true}) or <br>
 * - a VARIATION page ({@link AbstractPageModel#getDefaultPage()} {@code = false}).
 */
public class DefaultPageDisplayConditionService implements PageDisplayConditionService
{
	private CMSAdminPageService cmsAdminPageService;
	private CMSAdminSiteService cmsAdminSiteService;
	private Map<String, List<String>> cmsItemSearchTypeBlacklistMap;
	private PageVariationResolverTypeRegistry cmsPageVariationResolverTypeRegistry;

	@Override
	public List<DisplayConditionData> getDisplayConditions()
	{
		final Map<String, List<OptionData>> optionDataMap = getCmsSupportedPages()
				.collect(Collectors.toMap(Function.identity(), this::getDisplayCondition));
		return optionDataMap.entrySet().stream().map(this::convertToDisplayConditionData).collect(Collectors.toList());
	}

	@Override
	public List<DisplayConditionData> getDisplayConditions(final CatalogVersionModel catalogVersion)
	{
		getCmsAdminSiteService().setActiveCatalogVersion(catalogVersion);
		return getDisplayConditions();
	}

	protected List<OptionData> getDisplayCondition(final String typecode)
	{
		return getCmsPageVariationResolverTypeRegistry().getPageVariationResolverType(typecode).get().getResolver()
				.findDisplayConditions(typecode);
	}

	protected DisplayConditionData convertToDisplayConditionData(final Entry<String, List<OptionData>> optionEntry)
	{
		final DisplayConditionData displayCondition = new DisplayConditionData();
		displayCondition.setTypecode(optionEntry.getKey());
		displayCondition.setOptions(optionEntry.getValue());
		return displayCondition;
	}

	protected Stream<String> getCmsSupportedPages()
	{
		final List<String> blacklistedPageTypes = getCmsItemSearchTypeBlacklistMap().get(AbstractPageModel._TYPECODE);
		final Predicate<CMSPageTypeModel> isNotBlacklisted = //
				pageTypeModel -> !blacklistedPageTypes.contains(pageTypeModel.getCode());

		return getCmsAdminPageService().getAllPageTypes().stream() //
				.filter(isNotBlacklisted) //
				.map(CMSPageTypeModel::getCode);
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

	protected PageVariationResolverTypeRegistry getCmsPageVariationResolverTypeRegistry()
	{
		return cmsPageVariationResolverTypeRegistry;
	}

	@Required
	public void setCmsPageVariationResolverTypeRegistry(
			final PageVariationResolverTypeRegistry cmsPageVariationResolverTypeRegistry)
	{
		this.cmsPageVariationResolverTypeRegistry = cmsPageVariationResolverTypeRegistry;
	}

	protected Map<String, List<String>> getCmsItemSearchTypeBlacklistMap()
	{
		return cmsItemSearchTypeBlacklistMap;
	}

	@Required
	public void setCmsItemSearchTypeBlacklistMap(final Map<String, List<String>> cmsItemSearchTypeBlacklistMap)
	{
		this.cmsItemSearchTypeBlacklistMap = cmsItemSearchTypeBlacklistMap;
	}

	protected CMSAdminPageService getCmsAdminPageService()
	{
		return cmsAdminPageService;
	}

	@Required
	public void setCmsAdminPageService(final CMSAdminPageService cmsAdminPageService)
	{
		this.cmsAdminPageService = cmsAdminPageService;
	}
}
