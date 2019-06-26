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

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;


/**
 * Interface responsible for converting back and forth between an {@link ItemModel} and a serializable {@link Map} of its
 * attribute names and their values.
 */
public interface CMSItemConverter
{

	/**
	 * Converts an {@link ItemModel} to a serializable {@link Map}.
	 * @param source the {@link ItemModel} to convert.
	 * @return the {@link ItemModel} converted to a {@link Map}
	 */
	Map<String, Object> convert(ItemModel source);

	/**
	 * Converts a deserializable {@link Map} to an {@link ItemModel}.
	 * @param source the Map<String, Object> to convert
	 * @return the {@link Map} converted to an {@link ItemModel}
	 * @throws ConversionException if unknown UUID are provided, property types cannot be instantiated or if properties or sub
	 *            properties cannot be converted
	 */
	ItemModel convert(Map<String, Object> source);

}
