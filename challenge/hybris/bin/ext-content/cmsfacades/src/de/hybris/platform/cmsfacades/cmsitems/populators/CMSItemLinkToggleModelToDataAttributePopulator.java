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

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.*;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.HashMap;
import java.util.Map;

/**
 * Populator that prepares the linkToggle field and removes old external and urlLink fields
 */
public class CMSItemLinkToggleModelToDataAttributePopulator implements Populator<ItemModel, Map<String, Object>>
{
	@Override
	public void populate(ItemModel itemModel, Map<String, Object> itemMap) throws ConversionException
	{
		Map<String, Object> linkToggle = new HashMap<>();

		linkToggle.put(FIELD_EXTERNAL_NAME, itemMap.get(FIELD_EXTERNAL_NAME));
		linkToggle.put(FIELD_URL_LINK_NAME, itemMap.get(FIELD_URL_LINK_NAME));
		itemMap.put(FIELD_LINK_TOGGLE_NAME, linkToggle);

		itemMap.remove(FIELD_EXTERNAL_NAME);
		itemMap.remove(FIELD_URL_LINK_NAME);
	}
}
