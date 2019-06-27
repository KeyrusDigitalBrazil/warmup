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

import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.EncodedItemComposedKey;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Populates the ItemData object with the CMSVersion model as the source.
 */
public class CMSVersionModelItemDataPopulator implements Populator<CMSVersionModel, ItemData>
{

	@Override
	public void populate(final CMSVersionModel source, final ItemData target) throws ConversionException
	{
		target.setItemId(getUniqueIdentifier(source));
		target.setItemType(source.getItemtype());
		target.setName(source.getLabel());
	}

	/**
	 * Returns the unique identifier using the encoded compose key class. See more details here
	 * {@link EncodedItemComposedKey}.
	 *
	 * @param cmsVersionModel
	 *           the cms version model we want to extract the unique identifier.
	 * @return the encoded unique identifier.
	 * @see EncodedItemComposedKey
	 */
	protected String getUniqueIdentifier(final CMSVersionModel cmsVersionModel)
	{
		final EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey();
		itemComposedKey.setCatalogId(cmsVersionModel.getItemCatalogVersion().getCatalog().getId());
		itemComposedKey.setCatalogVersion(cmsVersionModel.getItemCatalogVersion().getVersion());
		itemComposedKey.setItemId(cmsVersionModel.getUid());

		return itemComposedKey.toEncoded();
	}
}
