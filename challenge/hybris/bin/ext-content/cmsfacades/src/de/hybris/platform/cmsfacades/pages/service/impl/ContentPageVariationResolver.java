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
package de.hybris.platform.cmsfacades.pages.service.impl;

import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminRestrictionService;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.OptionData;
import de.hybris.platform.cmsfacades.page.DisplayCondition;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the <code>PageVariationResolver</code>. This is used for retrieving default and variation
 * pages.
 * <p>
 * A page is considered "default": <br>
 * - when its <code>defaultPage</code> flag is set to <tt>TRUE</tt> or <br>
 * - when no default page exists for a given page type and exactly one variation page that has no restrictions exists,
 * that variation page will be used as the default page.
 * </p>
 * A page is considered "variation" when the page has at least one restrictions assigned to it.
 */
public class ContentPageVariationResolver implements PageVariationResolver<ContentPageModel>
{
	private CMSAdminPageService adminPageService;
	private CMSAdminRestrictionService adminRestrictionService;

	@Override
	public List<ContentPageModel> findPagesByType(final String typeCode, final boolean isDefaultPage)
	{
		List<ContentPageModel> results = (List<ContentPageModel>) getAdminPageService()
				.getAllContentPages(Arrays.asList(getAdminPageService().getActiveCatalogVersion()));

		final List<ContentPageModel> defaultPages = findDefaultPages(results);
		if (isDefaultPage)
		{
			results = defaultPages;
		}
		else
		{
			// filter out all default pages
			results = results.stream().filter(page -> !defaultPages.contains(page)).collect(Collectors.toList());
		}
		return results;
	}

	protected List<ContentPageModel> findDefaultPages(final List<ContentPageModel> contentPages)
	{
		// find all pages where defaultPage is TRUE
		final List<ContentPageModel> defaultPages = contentPages.stream().filter(page -> Boolean.TRUE.equals(page.getDefaultPage()))
				.collect(Collectors.toList());
		// find all default page labels where defaultPage is TRUE
		final List<String> defaultPageLabels = defaultPages.stream().map(page -> page.getLabel()).collect(Collectors.toList());

		// find all variation pages that have no restrictions
		final List<ContentPageModel> variationPages = contentPages.stream()
				.filter(page -> Boolean.FALSE.equals(page.getDefaultPage())).collect(Collectors.toList());
		final Map<String, ContentPageModel> unrestrictedPages = findUnrestrictedPages(variationPages);
		// unrestricted pages where no default page exists for the given page label is considered a default page
		unrestrictedPages.entrySet().stream().filter(entry -> !defaultPageLabels.contains(entry.getKey()))
		.map(entry -> entry.getValue()).forEach(page -> defaultPages.add(page));

		return defaultPages;
	}

	protected Map<String, ContentPageModel> findUnrestrictedPages(final List<ContentPageModel> contentPages)
	{
		// group all variation pages by page label
		final Map<String, List<ContentPageModel>> resultMap = contentPages.stream()
				.collect(Collectors.groupingBy(ContentPageModel::getLabel));

		// find pages where exactly one unrestricted page exists for a given label
		final Map<String, ContentPageModel> filteredResultMap = resultMap.entrySet().stream()
				.filter(entry -> entry.getValue().size() == 1).map(entry -> entry.getValue().get(0))
				.filter(page -> getAdminRestrictionService().getRestrictionsForPage(page).isEmpty())
				.collect(Collectors.toMap(ContentPageModel::getLabel, Function.identity()));

		return filteredResultMap;
	}

	@Override
	public List<ContentPageModel> findDefaultPages(final ContentPageModel pageModel)
	{
		List<ContentPageModel> results;
		if (isDefaultPage(pageModel))
		{
			results = Collections.emptyList();
		}
		else
		{
			final List<ContentPageModel> contentPages = (List<ContentPageModel>) getAdminPageService()
					.getAllContentPages(Arrays.asList(getAdminPageService().getActiveCatalogVersion()));

			results = findDefaultPages(contentPages).stream().filter(page -> page.getLabel().equals(pageModel.getLabel()))
					.collect(Collectors.toList());
		}
		return results;
	}

	@Override
	public List<ContentPageModel> findVariationPages(final ContentPageModel pageModel)
	{
		List<ContentPageModel> results;
		if (!isDefaultPage(pageModel))
		{
			results = Collections.emptyList();
		}
		else
		{
			results = findPagesByType(pageModel.getItemtype(), Boolean.FALSE).stream()
					.filter(page -> page.getLabel().equals(pageModel.getLabel())).collect(Collectors.toList());
		}
		return results;
	}

	@Override
	public boolean isDefaultPage(final ContentPageModel pageModel)
	{
		final List<ContentPageModel> contentPages = (List<ContentPageModel>) getAdminPageService()
				.getAllContentPages(Arrays.asList(getAdminPageService().getActiveCatalogVersion()));

		return pageModel.getDefaultPage() || findDefaultPages(contentPages).contains(pageModel);
	}

	@Override
	public List<OptionData> findDisplayConditions(final String typeCode)
	{
		final List<ContentPageModel> defaultPages = findPagesByType(typeCode, Boolean.TRUE);

		final List<OptionData> options = new ArrayList<>();
		createOptionData(DisplayCondition.PRIMARY.name(), CmsfacadesConstants.PAGE_DISPLAY_CONDITION_PRIMARY, options);
		if (!defaultPages.isEmpty())
		{
			createOptionData(DisplayCondition.VARIATION.name(), CmsfacadesConstants.PAGE_DISPLAY_CONDITION_VARIATION, options);
		}
		return options;
	}

	protected void createOptionData(final String id, final String label, final List<OptionData> options)
	{
		final OptionData optionData = new OptionData();
		optionData.setId(id);
		optionData.setLabel(label);
		options.add(optionData);
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

	protected CMSAdminRestrictionService getAdminRestrictionService()
	{
		return adminRestrictionService;
	}

	@Required
	public void setAdminRestrictionService(final CMSAdminRestrictionService adminRestrictionService)
	{
		this.adminRestrictionService = adminRestrictionService;
	}

}
