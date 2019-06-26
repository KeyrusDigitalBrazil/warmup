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

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_UUID;

/**
 * This populator sets unique identifiers to CMSItemModels.
 */
public class CMSItemUniqueIdentifierAttributePopulator implements Populator<ItemModel, Map<String, Object>>
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	// --------------------------------------------------------------------------
	// Public Methods
	// --------------------------------------------------------------------------
	@Override
	public void populate(ItemModel source, Map<String, Object> objectMap)
	{
		getUniqueItemIdentifierService().getItemData(source) //
				.ifPresent(itemData -> objectMap.put(FIELD_UUID, itemData.getItemId()));
	}

	// --------------------------------------------------------------------------
	// Getters/Setters
	// --------------------------------------------------------------------------
	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

}
