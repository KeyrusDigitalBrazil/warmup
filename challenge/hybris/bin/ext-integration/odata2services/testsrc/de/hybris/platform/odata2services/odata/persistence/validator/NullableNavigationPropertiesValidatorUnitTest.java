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

package de.hybris.platform.odata2services.odata.persistence.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.persistence.exception.MissingNavigationPropertyException;

import java.util.Arrays;
import java.util.Collections;

import org.apache.olingo.odata2.api.edm.EdmAnnotationAttribute;
import org.apache.olingo.odata2.api.edm.EdmAnnotations;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NullableNavigationPropertiesValidatorUnitTest
{
	@InjectMocks
	private NullableNavigationPropertiesValidator validator;

	@Mock
	private EdmEntityType entityType;
	@Mock
	private ODataEntry oDataEntry;

	@Before
	public void setUp() throws EdmException
	{
		when(entityType.getName()).thenReturn("Type");
	}

	private void createNavigationProperty(final String name, final Boolean nullable) throws EdmException
	{
		final EdmNavigationProperty p = mock(EdmNavigationProperty.class);
		when(entityType.getNavigationPropertyNames()).thenReturn(Collections.singletonList(name));
		when(p.getName()).thenReturn(name);

		when(entityType.getProperty(name)).thenReturn(p);
		final EdmAnnotations annotations = mock(EdmAnnotations.class);
		final EdmAnnotationAttribute nullableAnnotationAttribute = mock(EdmAnnotationAttribute.class);
		if (nullable != null)
		{
			when(nullableAnnotationAttribute.getName()).thenReturn("Nullable");
			when(nullableAnnotationAttribute.getText()).thenReturn(String.valueOf(nullable));
		}

		when(annotations.getAnnotationAttributes()).thenReturn(Collections.singletonList(nullableAnnotationAttribute));
		when(p.getAnnotations()).thenReturn(annotations);
	}

	@Test
	public void checkNullableNavigationProperties() throws EdmException
	{
		when(entityType.getNavigationPropertyNames()).thenReturn(Arrays.asList("n1", "n2"));
		createNavigationProperty("n1", true);
		createNavigationProperty("n2", null);

		validator.beforeCreateItem(entityType, oDataEntry);

		// no exception is thrown
	}

	@Test
	public void checkNonNullableNavigationProperties() throws EdmException
	{
		when(entityType.getNavigationPropertyNames()).thenReturn(Collections.singletonList("n1"));
		createNavigationProperty("n1", false);

		assertThatThrownBy(() -> validator.beforeCreateItem(entityType, oDataEntry))
				.isInstanceOf(MissingNavigationPropertyException.class)
				.hasFieldOrPropertyWithValue("errorCode", "missing_nav_property")
				.hasMessage("NavigationProperty [n1] is required for EntityType [Type].");
	}
}
