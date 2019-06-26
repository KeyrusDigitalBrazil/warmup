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
package de.hybris.platform.commercefacades.search.converters.populator;

import de.hybris.platform.commercefacades.search.data.AutocompleteSuggestionData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.AutocompleteSuggestion;
import de.hybris.platform.converters.Populator;


/**
 */
public class AutocompleteSuggestionPopulator implements Populator<AutocompleteSuggestion, AutocompleteSuggestionData>
{

	@Override
	public void populate(final AutocompleteSuggestion source, final AutocompleteSuggestionData target)
	{
		target.setTerm(source.getTerm());
	}
}
