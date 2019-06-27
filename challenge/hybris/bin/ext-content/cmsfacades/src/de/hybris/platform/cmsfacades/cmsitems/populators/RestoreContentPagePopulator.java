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
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * RestoreContentPagePopulator populator for cmsfacades used to replace a category page with same label while page
 * restore with replace set to true.
 */
public class RestoreContentPagePopulator implements Populator<Map<String, Object>, ItemModel>
{

	private Predicate<String> primaryPageWithLabelExistsPredicate;

	private PageVariationResolver<ContentPageModel> pageVariationResolver;

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

		final boolean isPageReplace = pageBeReplaced(source);
		final String pageLabel = (String) source.get(ContentPageModel.LABEL);
		final boolean primaryPageWithLabelExists = getPrimaryPageWithLabelExistsPredicate().test(pageLabel);

		if (isPageReplace && primaryPageWithLabelExists)
		{
			getPageVariationResolver()
					.findPagesByType(ContentPageModel._TYPECODE, Boolean.TRUE).stream() //
					.filter(defaultPage -> defaultPage.getLabel().equals(pageLabel)) //
					.filter(ContentPageModel::getDefaultPage)
					.findFirst()
					.ifPresent(contentPage -> {
						contentPage.setPageStatus(CmsPageStatus.DELETED);

						if (contentPage.isHomepage())
						{
							swapHomePage((ContentPageModel)itemModel);
						}
					});
		}

	}

	protected void swapHomePage(final ContentPageModel contentPageModel)
	{
		// Note: There's no need to set the current homepage flag to false since it will be handled by the TrashContentPagePopulator.
		contentPageModel.setHomepage(true);
	}

	protected boolean pageBeReplaced(final Map<String, Object> source)
	{
		final Object replace = source.get(FIELD_PAGE_REPLACE);
		return !Objects.isNull(replace) && replace.equals(Boolean.TRUE);
	}

	protected Predicate<String> getPrimaryPageWithLabelExistsPredicate()
	{
		return primaryPageWithLabelExistsPredicate;
	}

	@Required
	public void setPrimaryPageWithLabelExistsPredicate(final Predicate<String> primaryPageWithLabelExistsPredicate)
	{
		this.primaryPageWithLabelExistsPredicate = primaryPageWithLabelExistsPredicate;
	}

	protected PageVariationResolver<ContentPageModel> getPageVariationResolver()
	{
		return pageVariationResolver;
	}

	@Required
	public void setPageVariationResolver(final PageVariationResolver<ContentPageModel> pageVariationResolver)
	{
		this.pageVariationResolver = pageVariationResolver;
	}

}
