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

package de.hybris.platform.integrationservices.model.impl;

import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.simpleAttributeBuilder;
import static de.hybris.platform.integrationservices.model.MockIntegrationObjectItemModelBuilder.itemModelBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.MockIntegrationObjectItemModelBuilder;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.Collection;

import org.junit.Test;
import org.mockito.Mockito;

@UnitTest
public class ItemTypeDescriptorUnitTest
{
	@Test
	public void testCreate()
	{
		final TypeDescriptor descriptor = ItemTypeDescriptor.create(itemModelBuilder().build());
		assertThat(descriptor).isNotNull();
	}

	@Test
	public void testCreateWithNullModel()
	{
		assertThatThrownBy(() -> ItemTypeDescriptor.create(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testGetTypeCode()
	{
		final ItemTypeDescriptor descriptor = typeDescriptor(itemModelBuilder().withCode("Product"));
		assertThat(descriptor.getTypeCode()).isEqualTo("Product");
	}

	@Test
	public void testIsPrimitive()
	{
		final ItemTypeDescriptor descriptor = typeDescriptor(itemModelBuilder());
		assertThat(descriptor.isPrimitive()).isFalse();
	}

	@Test
	public void testGetAttributesWhenTypeDoesNotHaveAttributes()
	{
		final ItemTypeDescriptor descriptor = typeDescriptor(itemModelBuilder());
		assertThat(descriptor.getAttributes()).isEmpty();
	}

	@Test
	public void testGetAttributesWhenTypeHasAttributes()
	{
		final ItemTypeDescriptor descriptor = typeDescriptor(itemModelBuilder()
				.withAttribute(simpleAttributeBuilder().withName("Attribute 1"))
				.withAttribute(simpleAttributeBuilder().withName("Attribute 2")));
		assertThat(descriptor.getAttributes())
				.extracting("attributeName")
				.containsExactlyInAnyOrder("Attribute 1", "Attribute 2");
	}

	@Test
	public void testGetAttributesModificationsDoNotAffectTheState()
	{
		final ItemTypeDescriptor descriptor = typeDescriptor(itemModelBuilder().withAttribute(simpleAttributeBuilder()));
		final Collection<TypeAttributeDescriptor> attributes = descriptor.getAttributes();
		attributes.clear();

		assertThat(descriptor.getAttributes()).isNotEmpty();
	}

	@Test
	public void testGetNotExistingAttribute()
	{
		final ItemTypeDescriptor descriptor = typeDescriptor(itemModelBuilder());
		assertThat(descriptor.getAttribute("someAttribute")).isEmpty();
	}

	@Test
	public void testGetExistingAttribute()
	{
		final ItemTypeDescriptor descriptor = typeDescriptor(itemModelBuilder()
				.withAttribute(simpleAttributeBuilder().withName("One"))
				.withAttribute(simpleAttributeBuilder().withName("Two")));

		final TypeAttributeDescriptor attribute = descriptor.getAttribute("Two").orElse(null);
		assertThat(attribute)
				.isNotNull()
				.hasFieldOrPropertyWithValue("attributeName", "Two");
	}

	@Test
	public void testEquals()
	{
		final ItemTypeDescriptor sample = typeDescriptor("Inbound", "Item");
		assertThat(sample)
				.isNotEqualTo(null)
				.isNotEqualTo(Mockito.mock(TypeDescriptor.class))
				.isNotEqualTo(typeDescriptor("Outbound", "Item"))
				.isNotEqualTo(typeDescriptor("Inbound", "Primitive"))
				.isEqualTo(sample)
				.isEqualTo(typeDescriptor("Inbound", "Item"));
	}

	@Test
	public void testHashCode()
	{
		final ItemTypeDescriptor sample = typeDescriptor("Inbound", "Item");
		assertThat(sample.hashCode())
				.isNotEqualTo(Mockito.mock(TypeDescriptor.class).hashCode())
				.isNotEqualTo(typeDescriptor("Outbound", "Item").hashCode())
				.isNotEqualTo(typeDescriptor("Inbound", "Primitive").hashCode())
				.isEqualTo(typeDescriptor("Inbound", "Item").hashCode());
	}

	private ItemTypeDescriptor typeDescriptor(final String object, final String item)
	{
		return typeDescriptor(itemModelBuilder().withIntegrationObject(object).withCode(item));
	}

	private ItemTypeDescriptor typeDescriptor(final MockIntegrationObjectItemModelBuilder modelBuilder)
	{
		return new ItemTypeDescriptor(modelBuilder.build());
	}
}