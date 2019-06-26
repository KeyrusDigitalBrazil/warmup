/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.integrationservices.model;

import java.util.Collection;
import java.util.Optional;

import javax.validation.constraints.NotNull;

/**
 * Describes platform type in the context of an integration object item type. It may represent type of an integration object item
 * or a primitive, or other types, which integration object item attributes may have.
 * This metadata can be used for converting a custom payload to and from the platform's
 * {@link de.hybris.platform.core.model.ItemModel}
 */
public interface TypeDescriptor
{
	/**
	 * Reads value of the integration object item type code.
	 * @return integration object item type code. Keep in mind this value may be different from the type code of the item the
	 * integration object item represents. For example, type code of the item in the platform may be "Product" but integration
	 * object item type could be called "IntegrationProduct".
	 */
	@NotNull
	String getTypeCode();

	/**
	 * Retrieves descriptor of the specified attribute.
	 * @param attrName name of the integration object item attribute to be retrieved. Keep in mind that name of the attribute
	 * in an integration object item may be different from the name of the corresponding attribute in the type system.
	 * @return attribute descriptor for the given name or {@code Optional.empty()}, if the item type does not have an attribute
	 * with such name.
	 */
	@NotNull
	Optional<TypeAttributeDescriptor> getAttribute(String attrName);

	/**
	 * Retrieves all attributes defined in an integration object item.
	 * @return a collection of all attributes in the integration object item or an empty collection, if this description is for
	 * a primitive type or the item does not have attributes.
	 */
	@NotNull
	Collection<TypeAttributeDescriptor> getAttributes();

	/**
	 * Deermines whether this descriptor is for type representing integration object item or for a primitive type.
	 * @return {@code true}, if this descriptor is for a primitive type, e.g. Integer, String, etc; {@code false}, otherwise.
	 */
	boolean isPrimitive();
}
