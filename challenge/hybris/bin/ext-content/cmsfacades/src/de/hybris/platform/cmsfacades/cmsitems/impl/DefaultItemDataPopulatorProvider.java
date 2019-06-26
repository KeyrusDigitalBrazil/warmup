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
package de.hybris.platform.cmsfacades.cmsitems.impl;

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.cmsitems.ItemDataPopulatorProvider;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 *	Default implementation of {@link ItemDataPopulatorProvider) interface.
 *	This class defines a Map that is used to lookup for a list of Populators given a CMSItemModel.
 */
public class DefaultItemDataPopulatorProvider implements ItemDataPopulatorProvider
{
	private Map<Predicate<CMSItemModel>, List<Populator<CMSItemModel, Map<String, Object>>>> itemDataPredicatePopulatorListMap;

	@Override
	public List<Populator<CMSItemModel, Map<String, Object>>> getItemDataPopulators(CMSItemModel itemModel)
	{
		return getItemDataPredicatePopulatorListMap()
				.entrySet()
				.stream()
				.filter(entry -> entry.getKey().test(itemModel))
				.flatMap(entry -> entry.getValue().stream())
				.collect(Collectors.toList());
	}

	protected Map<Predicate<CMSItemModel>, List<Populator<CMSItemModel, Map<String, Object>>>> getItemDataPredicatePopulatorListMap()
	{
		return itemDataPredicatePopulatorListMap;
	}

	@Required
	public void setItemDataPredicatePopulatorListMap(
			Map<Predicate<CMSItemModel>, List<Populator<CMSItemModel, Map<String, Object>>>> itemDataPredicatePopulatorListMap)
	{
		this.itemDataPredicatePopulatorListMap = itemDataPredicatePopulatorListMap;
	}
}
