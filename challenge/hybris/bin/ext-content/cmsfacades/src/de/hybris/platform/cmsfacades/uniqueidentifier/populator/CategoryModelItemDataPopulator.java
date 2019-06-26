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
package de.hybris.platform.cmsfacades.uniqueidentifier.populator;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.EncodedItemComposedKey;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populates the ItemData object with the Category model as the source. 
 */
public class CategoryModelItemDataPopulator implements Populator<CategoryModel, ItemData>
{
	
	@Override
	public void populate(final CategoryModel source, final ItemData target) throws ConversionException
	{
		target.setItemId(getUniqueIdentifier(source));
		target.setItemType(source.getItemtype());
		target.setName(source.getName());
	}

	/**
	 * Returns the unique identifier using the encoded compose key class. See more details here {@link EncodedItemComposedKey}. 
	 *
	 * @param categoryModel the category item model we want to extract the unique identifier.
	 * @return the encoded unique identifier. 
	 * @see EncodedItemComposedKey
	 */
	protected String getUniqueIdentifier(final CategoryModel categoryModel)
	{
		EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey();
		itemComposedKey.setCatalogId(categoryModel.getCatalogVersion().getCatalog().getId());
		itemComposedKey.setCatalogVersion(categoryModel.getCatalogVersion().getVersion());
		itemComposedKey.setItemId(categoryModel.getCode());

		return itemComposedKey.toEncoded();
	}
	
}
