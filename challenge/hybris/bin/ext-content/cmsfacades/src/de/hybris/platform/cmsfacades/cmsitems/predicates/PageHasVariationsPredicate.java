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

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.pages.PageFacade;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;

import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to identify if the page has variations associated with it.
 *
 * <p>
 * Returns <tt>TRUE</tt> if the page has variations; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class PageHasVariationsPredicate implements Predicate<AbstractPageModel>
{
	private PageVariationResolverTypeRegistry pageVariationResolverTypeRegistry;

	@Override
	public boolean test(final AbstractPageModel page)
	{
		return CollectionUtils.isNotEmpty(getPageVariationResolver(page.getItemtype()).findVariationPages(page));
	}

	protected PageVariationResolver<AbstractPageModel> getPageVariationResolver(final String typeCode)
	{
		return getPageVariationResolverTypeRegistry().getPageVariationResolverType(typeCode).get().getResolver();
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
}
