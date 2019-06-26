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

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminRestrictionService;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.OptionData;
import de.hybris.platform.cmsfacades.page.DisplayCondition;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
public class DefaultPageVariationResolver implements PageVariationResolver<AbstractPageModel>
{
	private CMSAdminPageService adminPageService;
	private CMSAdminRestrictionService adminRestrictionService;
	private TypeService typeService;

	@Override
	public List<AbstractPageModel> findPagesByType(final String typeCode, final boolean isDefaultPage)
	{
		final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(typeCode);
		List<AbstractPageModel> results = (List<AbstractPageModel>) getAdminPageService().findPagesByType(composedType,
				isDefaultPage);
		if (isDefaultPage && results.isEmpty())
		{
			// find variant page with no restrictions
			// filter for pages that have zero restriction assigned and pick the first result if multiple pages exists
			final Optional<AbstractPageModel> defaultPage = getAdminPageService().findPagesByType(composedType, Boolean.FALSE)
					.stream().filter(page -> getAdminRestrictionService().getRestrictionsForPage(page).isEmpty()) //
					.findFirst();

			results = defaultPage.map(page -> Arrays.asList(defaultPage.get())).orElse(Collections.emptyList());
		}
		return results;
	}

	@Override
	public List<AbstractPageModel> findDefaultPages(final AbstractPageModel pageModel)
	{
		List<AbstractPageModel> results;
		if (isDefaultPage(pageModel))
		{
			results = Collections.emptyList();
		}
		else
		{
			results = findPagesByType(pageModel.getItemtype(), Boolean.TRUE);
		}
		return results;
	}

	@Override
	public List<AbstractPageModel> findVariationPages(final AbstractPageModel pageModel)
	{
		List<AbstractPageModel> results;
		if (!isDefaultPage(pageModel))
		{
			results = Collections.emptyList();
		}
		else
		{
			results = findPagesByType(pageModel.getItemtype(), Boolean.FALSE);
		}
		return results;
	}

	/*
	 * Suppress sonar warning (squid:S2583 | Conditions should not unconditionally evaluate to "TRUE" or to "FALSE") :
	 * The condition is does not always evaluate to "TRUE" or to "FALSE".
	 */
	@SuppressWarnings("squid:S2583")
	@Override
	public boolean isDefaultPage(final AbstractPageModel pageModel)
	{
		final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(pageModel.getItemtype());
		final Optional<AbstractPageModel> defaultPageOptional = getAdminPageService().findPagesByType(composedType, Boolean.TRUE)
				.stream().findFirst();

		return pageModel.getDefaultPage() || !defaultPageOptional.isPresent()
				|| (defaultPageOptional.isPresent() && defaultPageOptional.get().getUid().equals(pageModel.getUid()));
	}

	@Override
	public List<OptionData> findDisplayConditions(final String typeCode)
	{
		final List<AbstractPageModel> defaultPages = findPagesByType(typeCode, Boolean.TRUE);

		final List<OptionData> options = new ArrayList<>();
		if (defaultPages.isEmpty())
		{
			createOptionData(DisplayCondition.PRIMARY.name(), CmsfacadesConstants.PAGE_DISPLAY_CONDITION_PRIMARY, options);
		}
		else
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

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

}
