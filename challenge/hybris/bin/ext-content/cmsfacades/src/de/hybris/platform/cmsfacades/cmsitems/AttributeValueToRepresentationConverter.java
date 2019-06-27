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

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.function.Function;

/**
 * This interface is used to transform an attribute, described by its {@link de.hybris.platform.core.model.type.AttributeDescriptorModel},
 * leveraging a transformation function.
 *
 * This interface can be useful to transform lists or other types of collections.
 *
 * @param <T> the type of the attribute to convert.
 * @param <S> the type of the output to produce.
 */
@FunctionalInterface
public interface AttributeValueToRepresentationConverter<T, S>
{
	/**
	 * Converts an item of type {@code T}, described by the provided {@link de.hybris.platform.core.model.type.AttributeDescriptorModel},
	 * into a new object of type {@code S} by applying the provided transformation function.
	 *
	 * This function can be useful to transform lists or other types of collections, where the transformation function has to be applied
	 * individually to each element of the collection.
	 *
	 * @param attribute A model describing the attribute
	 * @param item The item to convert
	 * @param transformationFunction Function that will be used to transform the provided item.
	 * @return an instance of {@code S}, converted from {@code T} by leveraging the provided transformationFunction.
	 */
	S convert(AttributeDescriptorModel attribute, T item, Function<Object, Object> transformationFunction);
}
