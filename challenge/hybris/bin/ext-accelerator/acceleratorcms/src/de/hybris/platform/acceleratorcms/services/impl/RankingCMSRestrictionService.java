/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.acceleratorcms.services.impl;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSRestrictionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;


/**
 * Subclass of the DefaultCMSRestrictionService that selects the Page that matches the most restrictions.
 * 
 * In the DefaultCMSRestrictionService where multiple pages are allowed to be matched due to satisfied restrictions the
 * first matching page is returned. In the RankingCMSRestrictionService this behaviour is changes so that the page that
 * has the most restrictions is returned.
 */
public class RankingCMSRestrictionService extends DefaultCMSRestrictionService
{
	private static final Logger LOG = Logger.getLogger(RankingCMSRestrictionService.class);

	@Override
	public Collection<AbstractPageModel> evaluatePages(final Collection<AbstractPageModel> pages, final RestrictionData data)
	{
		final NavigableMap<Integer, List<AbstractPageModel>> allowedPages = new TreeMap<>();

		final Collection<AbstractPageModel> defaultPages = getDefaultPages(pages);

		for (final AbstractPageModel page : pages)
		{
			if (!defaultPages.contains(page))
			{
				final List<AbstractRestrictionModel> restrictions = page.getRestrictions();

				if (CollectionUtils.isNotEmpty(restrictions))
				{
					evaluatePageRestrictions(data, allowedPages, page, restrictions);
				}
			}
		}

		final List<AbstractPageModel> result = new ArrayList<>();

		if (MapUtils.isNotEmpty(allowedPages))
		{
			// Take the list of pages with the highest count of restrictions
			result.addAll(allowedPages.lastEntry().getValue());
		}
		else if (CollectionUtils.isNotEmpty(defaultPages))
		{
			result.addAll(defaultPages);
		}

		return CollectionUtils.isNotEmpty(defaultPages) && defaultPages.size() > 1 ? getMultiCountryRestrictedPages(result) : result;
	}

	protected void evaluatePageRestrictions(final RestrictionData data,
			final NavigableMap<Integer, List<AbstractPageModel>> allowedPages, final AbstractPageModel page,
			final List<AbstractRestrictionModel> restrictions)
	{
		LOG.debug("Evaluating restrictions for page [" + page.getName() + "].");
		final boolean onlyOneRestrictionMustApply = page.isOnlyOneRestrictionMustApply();
		final boolean allowed = evaluate(restrictions, data, onlyOneRestrictionMustApply);
		if (allowed)
		{
			LOG.debug("Adding page [" + page.getName() + "] to allowed pages");
			final Integer countOfMatchingRestrictions = Integer.valueOf(onlyOneRestrictionMustApply ? 1 : restrictions.size());

			if (allowedPages.containsKey(countOfMatchingRestrictions))
			{
				// Add to existing list
				allowedPages.get(countOfMatchingRestrictions).add(page);
			}
			else
			{
				// Add a new entry
				final List<AbstractPageModel> list = new ArrayList<>();
				list.add(page);
				allowedPages.put(countOfMatchingRestrictions, list);
			}
		}
	}
}
