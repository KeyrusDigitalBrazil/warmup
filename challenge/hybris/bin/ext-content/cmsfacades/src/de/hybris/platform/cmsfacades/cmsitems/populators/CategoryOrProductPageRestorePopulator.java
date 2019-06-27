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
package de.hybris.platform.cmsfacades.cmsitems.populators;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_PAGE_REPLACE;

import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverType;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * CategoryOrProductPageRestorePopulator populator for cmsfacades used to replace a product/category page while page
 * restore with replace set to true.
 */
public class CategoryOrProductPageRestorePopulator implements Populator<Map<String, Object>, ItemModel>
{

	private PageVariationResolverTypeRegistry pageVariationResolverTypeRegistry;
	private Predicate<AbstractPageModel> pageCanOnlyHaveOnePrimaryPredicate;

	@Override
	public void populate(final Map<String, Object> source, final ItemModel itemModel) throws ConversionException
	{
		if (itemModel == null)
		{
			throw new ConversionException("Item Model used in the populator should not be null.");
		}
		if (source == null)
		{
			throw new ConversionException("Source map used in the populator should not be null.");
		}

		if (pageBeReplacedWithOnlyOnePrimaryForType(source, itemModel))
		{

			final List<AbstractPageModel> existingPrimaryPagesList = getExistingPrimaryPagesByType(
					(String) source.get(AbstractPageModel.TYPECODE));

			if (isPrimaryPage(source)
					&& primaryPageAlreadyExists((String) source.get(AbstractPageModel.UID), existingPrimaryPagesList))
			{
				existingPrimaryPagesList.get(0).setPageStatus(CmsPageStatus.DELETED);
			}

		}

	}

	/**
	 * Returns active primary pages for a given pageType.
	 */
	protected List<AbstractPageModel> getExistingPrimaryPagesByType(final String pageType)
	{
		final Optional<PageVariationResolverType> pageVariationResolverOptional = getPageVariationResolverTypeRegistry()
				.getPageVariationResolverType(pageType);
		return pageVariationResolverOptional
				.map(pageVariationResolver -> pageVariationResolver.getResolver().findPagesByType(pageType, Boolean.TRUE)).get();
	}

	/**
	 * Checks if it a page restore operation with replace enabled and type that can have only one primary page.
	 */
	protected boolean pageBeReplacedWithOnlyOnePrimaryForType(final Map<String, Object> source, final ItemModel itemModel)
	{
		final Object replace = source.get(FIELD_PAGE_REPLACE);
		return !Objects.isNull(replace) && replace.equals(true)
				&& getPageCanOnlyHaveOnePrimaryPredicate().test((AbstractPageModel) itemModel);
	}

	protected boolean isPrimaryPage(final Map<String, Object> source)
	{
		return (boolean) source.get(AbstractPageModel.DEFAULTPAGE);
	}

	protected boolean primaryPageAlreadyExists(final String uid, final List<AbstractPageModel> existingPages)
	{
		return !existingPages.isEmpty() && existingPages.stream().anyMatch(page -> !page.getUid().equals(uid));
	}

	protected PageVariationResolverTypeRegistry getPageVariationResolverTypeRegistry()
	{
		return pageVariationResolverTypeRegistry;
	}

	@Required
	public void setPageVariationResolverTypeRegistry(final PageVariationResolverTypeRegistry pageVariationResolverTypeRegistry)
	{
		this.pageVariationResolverTypeRegistry = pageVariationResolverTypeRegistry;
	}

	protected Predicate<AbstractPageModel> getPageCanOnlyHaveOnePrimaryPredicate()
	{
		return pageCanOnlyHaveOnePrimaryPredicate;
	}

	@Required
	public void setPageCanOnlyHaveOnePrimaryPredicate(final Predicate<AbstractPageModel> pageCanOnlyHaveOnePrimaryPredicate)
	{
		this.pageCanOnlyHaveOnePrimaryPredicate = pageCanOnlyHaveOnePrimaryPredicate;
	}


}
