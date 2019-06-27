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
import de.hybris.platform.odata2services.odata.persistence.exception.MissingPropertyException;

import java.util.Arrays;
import java.util.Collections;

import org.apache.olingo.odata2.api.edm.EdmAnnotationAttribute;
import org.apache.olingo.odata2.api.edm.EdmAnnotations;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NullablePropertiesValidatorUnitTest
{
	@InjectMocks
	private NullablePropertiesValidator validator;

	@Mock
	private EdmEntityType entityType;
	@Mock
	private ODataEntry oDataEntry;

	@Before
	public void setUp() throws EdmException
	{
		when(entityType.getName()).thenReturn("Type");
	}

	private void createProperty(final String name, final Boolean nullable) throws EdmException
	{
		final EdmProperty p = mock(EdmProperty.class);
		when(entityType.getProperty(name)).thenReturn(p);
		final EdmAnnotations annotations = mock(EdmAnnotations.class);
		final EdmAnnotationAttribute annotationAttribute = mock(EdmAnnotationAttribute.class);
		if (nullable != null)
		{
			when(annotationAttribute.getName()).thenReturn("Nullable");
			when(annotationAttribute.getText()).thenReturn(String.valueOf(nullable));
		}
		when(annotations.getAnnotationAttributes()).thenReturn(Collections.singletonList(annotationAttribute));
		when(p.getAnnotations()).thenReturn(annotations);
	}

	@Test
	public void checkNullableProperties() throws EdmException
	{
		when(entityType.getPropertyNames()).thenReturn(Arrays.asList("p1", "p2"));
		createProperty("p1", true);
		createProperty("p3", null);

		validator.beforeCreateItem(entityType, oDataEntry);

		// no exception is thrown
	}

	@Test
	public void checkNonNullableProperties() throws EdmException
	{
		when(entityType.getPropertyNames()).thenReturn(Collections.singletonList("p1"));
		createProperty("p1", false);

		assertThatThrownBy(() -> validator.beforeCreateItem(entityType, oDataEntry))
				.isInstanceOf(MissingPropertyException.class)
				.hasMessage("Property [p1] is required for EntityType [Type].")
				.hasFieldOrPropertyWithValue("errorCode", "missing_property");
	}
}
