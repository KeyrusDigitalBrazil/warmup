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

import java.util.Optional;

/**
 * Describes an item type attribute from the integration object item point of view. Provides metadata necessary for conversion
 * of the payloads to/from the {@link de.hybris.platform.core.model.ItemModel}.
 */
public interface TypeAttributeDescriptor
{
	/**
	 * Gets name of the attribute described by this descriptor.
	 * @return name of the attribute.
	 */
	String getAttributeName();

	/**
	 * Determines whether this attribute contains a single value or multiple values (a collection of values).
	 * @return {@code true}, if this attribute contains a collection of primitive or complex type values; {@code false}, if this
	 * attribute contains only a single primitive or complex type value.
	 */
	boolean isCollection();

	/**
	 * Retrieves type of this attribute value(s).
	 * @return type of this attribute value, which may be a primitive type or a complex type referring an item in the type system;
	 * or a type of the referenced element(s) in case, if this attributes contains a collection of values or represents a
	 * one-to-one, one-to-many or many-to-many relationship.
	 * @see #isCollection()
	 */
	TypeDescriptor getAttributeType();

	/**
	 * Retrieves descriptor of the type, this attribute is associated with.
	 * @return descriptor of the item type that contains the attribute described by this attribute descriptor.
	 */
	TypeDescriptor getTypeDescriptor();

	/**
	 * <p>Reverses the relation described by this attribute descriptor and retrieves an attribute descriptor defined in the
	 * {@link #getAttributeType()} type descriptor with {@link #getTypeDescriptor()} attribute type.</p>
	 * <p>For example, type {@code Parent} has attribute {@code children} that refers a collection of type {@code Child}; and
	 * type {@code Child} has an attribute {@code parent} referring back to {@code Parent} type. Then attribute descriptor for
	 * {@code parent} attribute is reverse for {@code children} attribute descriptor.</p>
	 * <p>Another example, type {@code Organization} has attribute {@code addresses} that refers a collection of type
	 * {@code Address}; but type {@code Address} does not refer back to type {@code Organization}. In this case there is no
	 * reverse attribute for {@code addresses} attribute descriptor.</p>
	 * @return an optional containing a descriptor for the attribute in type returned by {@link #getAttributeType()}, which refers
	 * back to the type retrieved by calling {@link #getTypeDescriptor()} on this attribute descriptor; or an empty optional
	 * that attribute type of this descriptor does not refer back to the type containing this attribute descriptor.
	 */
	Optional<TypeAttributeDescriptor> reverse();

	/**
	 * Determines whether the attribute represented by this descriptor can take {@code null} values.
	 * @return {@code true}, if the attribute can take {@code null} values; {@code false}, if the attribute value is required.
	 */
	boolean isNullable();

	/**
	 * Determines whether the item referenced by this attribute should be a part of the attribute's item model or not.
	 * This defines in particular whether the nested referenced item will be created whenever the "container" item is
	 * persisted (part of) or it can and should exist and persist independently (not a part of).
	 * For example, a {@code Car} has an attribute {@code engine}, which refers item {@code Engine}. If the business model
	 * is interested in cars only and does not care about engines outside of the car model, then the attribute should be
	 * defined with {@code partOf == true}. If the engine has independent existence, e.g. engine can be sold without a car,
	 * then the attribute should defined with {@code partOf == false}.
	 * <p>In other words, this attribute descriptor defines a relation between an owner and the owned item. Thus in the example
	 * above {@code Engine} is owned by {@code Car} as it does not have independent existence in the model.</p>
	 * @return {@code true}, if the item referenced by this attribute descriptor is an integral part of its owner model.
	 * @see #isAutoCreate()
	 */
	boolean isPartOf();

	/**
	 * Determines whether the item referenced by this attribute should be persisted when the item with this attribute is persisted.
	 * Unlike {@link #isPartOf()} this method does not require the referenced item to be an integral part of the owner item.
	 * The referenced item may have its independent existence in the domain model and yet it will be persisted together with the
	 * item holding an attribute described by this descriptor.
	 * @return {@code true}, if the attribute's item should be included within the item holding an attribute described by
	 * this descriptor; {@code false}, if the referenced item should not be persisted with the item holding an attribute described
	 * by this descriptor.
	 * @see #isPartOf()
	 */
	boolean isAutoCreate();

	/**
	 * Determines whether the value of this descriptor can be localized
	 * @return true if localized, else false
	 */
	boolean isLocalized();

	/**
	 * Determines whether the value of this descriptor is a primitive type
	 * @return true if it's a primitive type, else false
	 */
	boolean isPrimitive();
}
