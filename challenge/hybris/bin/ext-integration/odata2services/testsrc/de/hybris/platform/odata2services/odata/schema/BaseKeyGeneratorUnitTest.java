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
package de.hybris.platform.odata2services.odata.schema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.provider.Key;
import org.apache.olingo.odata2.api.edm.provider.PropertyRef;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;
import org.junit.Before;
import org.junit.Test;

public abstract class BaseKeyGeneratorUnitTest<T extends KeyGenerator>
{
	private static final String ANOTHER_ATTRIBUTE = "anotherAttribute";
	private static final String ANOTHER_ATTRIBUTE2 = "anotherAttribute2";
	private T keyGenerator;
	private String keyPropertyName;

	protected abstract T createKeyGenerator();
	protected abstract String getKeyPropertyName();

	@Before
	public void setUp() {
		keyGenerator = createKeyGenerator();
		keyPropertyName = getKeyPropertyName();
	}

	@Test
	public void testGenerateSingletonPropertyListOfTypeString()
	{
		final Optional<Key> key = keyGenerator.generate(Collections.singletonList(givenProperty(keyPropertyName, EdmSimpleTypeKind.String)));

		assertTrue(key.isPresent());
		assertEquals(1, key.get().getKeys().size());
		assertThat(key.get().getKeys())
				.usingElementComparatorOnFields("name")
				.containsExactlyInAnyOrder(
						expectedPropertyRef(keyPropertyName)
				);
	}

	@Test
	public void testGenerateSingletonPropertyListOfTypeBoolean()
	{
		final Optional<Key> key = keyGenerator.generate(Collections.singletonList(givenProperty(keyPropertyName, EdmSimpleTypeKind.Boolean)));

		assertFalse(key.isPresent());
	}

	@Test
	public void testGenerateMultipleValidProperties()
	{
		final Optional<Key> key = keyGenerator.generate(
				Arrays.asList(
						givenProperty(ANOTHER_ATTRIBUTE, EdmSimpleTypeKind.String),
						givenProperty(keyPropertyName, EdmSimpleTypeKind.String)
				));

		assertTrue(key.isPresent());
		assertEquals(1, key.get().getKeys().size());
		assertThat(key.get().getKeys())
				.usingElementComparatorOnFields("name")
				.containsExactlyInAnyOrder(
						expectedPropertyRef(keyPropertyName)
				);
	}

	@Test
	public void testGenerateMultipleValidPropertiesNoKeyProperty()
	{
		final Optional<Key> key = keyGenerator.generate(
				Arrays.asList(
						givenProperty(ANOTHER_ATTRIBUTE, EdmSimpleTypeKind.String),
						givenProperty(ANOTHER_ATTRIBUTE2, EdmSimpleTypeKind.String)
				));

		assertFalse(key.isPresent());
	}

	@Test
	public void testGenerateNullPropertyList()
	{
		assertThatThrownBy(() -> keyGenerator.generate(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	protected static SimpleProperty givenProperty(final String propertyName, final EdmSimpleTypeKind typeKind)
	{
		final SimpleProperty property = mock(SimpleProperty.class);
		when(property.getType()).thenReturn(typeKind);
		when(property.getName()).thenReturn(propertyName);
		return property;
	}

	protected static PropertyRef expectedPropertyRef(final String name)
	{
		return new PropertyRef().setName(name);
	}
}
