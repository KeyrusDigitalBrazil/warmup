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
package de.hybris.platform.cmsfacades.cmsitems;

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.converters.Populator;

import java.util.List;
import java.util.Map;


/**
 * Interface to provide a list of custom {@link Populator} populators for a given {@link CMSItemModel}.
 */
public interface ItemDataPopulatorProvider
{
	/**
	 * Interface method to return a list of {@link Populator} populators given the {@link CMSItemModel}.
	 * @param itemModel the CMSItemModel
	 * @return the list of Populators registered for the {@link CMSItemModel}.
	 */
	List<Populator<CMSItemModel, Map<String, Object>>> getItemDataPopulators(final CMSItemModel itemModel);
}
