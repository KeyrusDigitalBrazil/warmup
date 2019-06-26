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

package de.hybris.platform.odata2services.odata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.persistence.InvalidDataException;

import java.util.Collections;

import org.apache.olingo.odata2.api.edm.EdmAnnotatable;
import org.apache.olingo.odata2.api.edm.EdmAnnotationAttribute;
import org.apache.olingo.odata2.api.edm.EdmAnnotations;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmType;
import org.apache.olingo.odata2.api.edm.EdmTypeKind;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class EdmAnnotationUtilsUnitTest
{
	private static final String NULLABLE = "Nullable";
	private static final String ALIAS = "s:Alias";
	private static final String IS_PART_OF = "s:IsPartOf";
	private static final String IS_UNIQUE = "s:IsUnique";
	private static final String IS_AUTO_CREATE = "s:IsAutoCreate";

	@Test
	public void testEntityIsPartOf() throws EdmException
	{
		final EdmProperty property = givenEdmPropertyWithAnnotationAndValue(IS_PART_OF, true);

		assertThat(EdmAnnotationUtils.isPartOf(property)).isTrue();
	}

	@Test
	public void testEntityIsNotPartOf() throws EdmException
	{
		final EdmProperty property = givenEdmPropertyWithAnnotationAndValue(IS_PART_OF, false);

		assertThat(EdmAnnotationUtils.isPartOf(property)).isFalse();
	}

	@Test
	public void testEntityIsPartOf_WithoutAnnotation() throws EdmException
	{
		final EdmProperty property = givenEdmPropertyWithAnnotationAndValue(IS_PART_OF, null);

		assertThat(EdmAnnotationUtils.isPartOf(property)).isFalse();
	}

	@Test
	public void testEntityIsAutoCreate() throws EdmException
	{
		final EdmProperty property = givenEdmPropertyWithAnnotationAndValue(IS_AUTO_CREATE, true);

		assertThat(EdmAnnotationUtils.isAutoCreate(property)).isTrue();
	}

	@Test
	public void testEntityIsNotAutoCreate() throws EdmException
	{
		final EdmProperty property = givenEdmPropertyWithAnnotationAndValue(IS_AUTO_CREATE, false);

		assertThat(EdmAnnotationUtils.isAutoCreate(property)).isFalse();
	}

	@Test
	public void testEntityIsAutoCreate_WithoutAnnotation() throws EdmException
	{
		final EdmProperty property = givenEdmPropertyWithAnnotationAndValue(IS_AUTO_CREATE, null);

		assertThat(EdmAnnotationUtils.isAutoCreate(property)).isFalse();
	}

	@Test
	public void testEntityIsKeyNavigationProperty() throws EdmException
	{
		final EdmNavigationProperty property = givenProperty(EdmNavigationProperty.class, "a");
		prepareAnnotation(property, IS_UNIQUE, true);

		assertThat(EdmAnnotationUtils.isKeyProperty(property)).isTrue();
	}

	@Test
	public void testEntityIsNotKeyNavigationProperty() throws EdmException
	{
		final EdmNavigationProperty property = givenProperty(EdmNavigationProperty.class, "b");
		prepareAnnotation(property, IS_UNIQUE, false);

		assertThat(EdmAnnotationUtils.isKeyProperty(property)).isFalse();
	}

	@Test
	public void testEntityIsKeyNavigationProperty_WithoutAnnotation() throws EdmException
	{
		final EdmNavigationProperty property = givenProperty(EdmNavigationProperty.class, "c");
		prepareAnnotation(property, IS_UNIQUE, null);

		assertThat(EdmAnnotationUtils.isKeyProperty(property)).isFalse();
	}

	@Test
	public void testEntityIsNullable() throws EdmException
	{
		final EdmProperty property = givenEdmPropertyWithAnnotationAndValue(NULLABLE, true);

		assertThat(EdmAnnotationUtils.isNullable(property)).isTrue();
	}

	@Test
	public void testEntityIsNotNullable() throws EdmException
	{
		final EdmProperty property = givenEdmPropertyWithAnnotationAndValue(NULLABLE, false);

		assertThat(EdmAnnotationUtils.isNullable(property)).isFalse();
	}

	@Test
	public void testEntityIsNullable_WithoutAnnotation() throws EdmException
	{
		final EdmProperty property = givenEdmPropertyWithAnnotationAndValue(NULLABLE, null);

		assertThat(EdmAnnotationUtils.isNullable(property)).isTrue();
	}

	@Test
	public void testIsKeySimpleProperty() throws EdmException
	{
		final EdmProperty property = givenEdmPropertyWithAnnotationAndValue(IS_UNIQUE, true);

		assertThat(EdmAnnotationUtils.isKeyProperty(property)).isTrue();
	}

	@Test
	public void testIsSimplePropertyNotKey() throws EdmException
	{
		final EdmProperty property = givenEdmPropertyWithAnnotationAndValue(IS_UNIQUE, null);

		assertThat(EdmAnnotationUtils.isKeyProperty(property)).isFalse();
	}

	@Test
	public void testIsSimplePropertyNotNullable() throws EdmException
	{
		final EdmProperty property = givenEdmPropertyWithAnnotationAndValue(IS_UNIQUE, false);

		assertThat(EdmAnnotationUtils.isKeyProperty(property)).isFalse();
	}

	@Test
	public void testGetAliasTextIfPresent() throws EdmException
	{
		final EdmProperty property = givenProperty(EdmProperty.class, "a");
		when(property.isSimple()).thenReturn(true);
		prepareAnnotation(property, ALIAS, "SomeText");

		assertThat(EdmAnnotationUtils.getAliasTextIfPresent(Collections.singletonList(property))).isEqualTo("SomeText");
	}

	@Test
	public void testGetAliasTextIfPresent_noAliasFound() throws EdmException
	{
		final EdmProperty property = givenProperty(EdmProperty.class, "a");
		when(property.isSimple()).thenReturn(true);
		prepareAnnotation(property, "SomeDifferentAnnotation", "SomeText");

		assertThat(EdmAnnotationUtils.getAliasTextIfPresent(Collections.singletonList(property))).isEqualTo("");
	}

	@Test
	public void testGetAliasTextIfPresent_notSimple() throws EdmException
	{
		final EdmProperty property = givenProperty(EdmProperty.class, "a");
		when(property.isSimple()).thenReturn(false);
		prepareAnnotation(property, ALIAS, "SomeText");

		assertThatThrownBy(() -> EdmAnnotationUtils.getAliasTextIfPresent(Collections.singletonList(property)))
				.isInstanceOf(InvalidDataException.class)
				.hasMessage("There is no valid key defined for the current entityType.")
				.hasFieldOrPropertyWithValue("errorCode", "invalid_key_definition");
	}

	@Test
	public void testGetAliasTextIfPresent_empty()
	{
		assertThatThrownBy(() -> EdmAnnotationUtils.getAliasTextIfPresent(Collections.emptyList()))
				.isInstanceOf(InvalidDataException.class)
				.hasMessage("There is no valid key defined for the current entityType.")
				.hasFieldOrPropertyWithValue("errorCode", "invalid_key_definition");
	}

	private EdmProperty givenEdmPropertyWithAnnotationAndValue(final String annotation, final Boolean value) throws EdmException
	{
		final EdmProperty property = givenProperty(EdmProperty.class, "myPropertyName");
		prepareAnnotation(property, annotation, value);
		return property;
	}

	private <T extends EdmTyped> T givenProperty(final Class<T> klazz, final String name) throws EdmException
	{
		final T property = mock(klazz);

		final EdmType edmType = mock(EdmType.class);
		when(edmType.getKind()).thenReturn(EdmTypeKind.SIMPLE);
		when(edmType.getName()).thenReturn(name);

		when(property.getType()).thenReturn(edmType);

		return property;
	}

	private void prepareAnnotation(final EdmAnnotatable edmTyped, final String name, final Object value) throws EdmException
	{
		final EdmAnnotations edmAnnotations = mock(EdmAnnotations.class);
		final EdmAnnotationAttribute attributes = mock(EdmAnnotationAttribute.class);
		when(edmTyped.getAnnotations()).thenReturn(edmAnnotations);

		when(edmAnnotations.getAnnotationAttributes()).thenReturn(Collections.singletonList(attributes));
		if (value != null)
		{
			when(attributes.getName()).thenReturn(name);
			when(attributes.getText()).thenReturn(value.toString());
		}
	}
}
