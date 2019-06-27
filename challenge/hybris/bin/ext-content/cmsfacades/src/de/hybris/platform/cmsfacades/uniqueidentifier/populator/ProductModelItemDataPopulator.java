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

import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.EncodedItemComposedKey;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Populates the ItemData object with the Product model as the source.
 */
public class ProductModelItemDataPopulator implements Populator<ProductModel, ItemData>
{
	
	@Override
	public void populate(final ProductModel source, final ItemData target) throws ConversionException
	{
		target.setItemId(getUniqueIdentifier(source));
		target.setItemType(source.getItemtype());
		target.setName(source.getName());
	}

	/**
	 * Returns the unique identifier using the encoded compose key class. See more details here {@link EncodedItemComposedKey}. 
	 *
	 * @param productModel the product item model we want to extract the unique identifier.
	 * @return the encoded unique identifier. 
	 * @see EncodedItemComposedKey
	 */
	protected String getUniqueIdentifier(final ProductModel productModel)
	{
		EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey();
		itemComposedKey.setCatalogId(productModel.getCatalogVersion().getCatalog().getId());
		itemComposedKey.setCatalogVersion(productModel.getCatalogVersion().getVersion());
		itemComposedKey.setItemId(productModel.getCode());

		return itemComposedKey.toEncoded();
	}
}
