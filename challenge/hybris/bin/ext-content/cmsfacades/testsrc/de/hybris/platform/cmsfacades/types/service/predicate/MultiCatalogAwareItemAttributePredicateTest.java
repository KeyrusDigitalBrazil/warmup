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
package de.hybris.platform.cmsfacades.types.service.predicate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MultiCatalogAwareItemAttributePredicateTest
{

	private static final String SOME_TYPE_CODE = "SomeTypeCode";

	@InjectMocks
	private MultiCatalogAwareItemAttributePredicate multiCatalogAwareItemAttributePredicateTest;

	@Mock
	private Predicate<AttributeDescriptorModel> isCollectionPredicate;

	@Mock
	private AssignableFromAttributePredicate assignableFromAttributePredicate;

	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private TypeModel attributeType;

	@Before
	public void setup()
	{

		when(attributeDescriptor.getAttributeType()).thenReturn(attributeType);
		when(attributeType.getCode()).thenReturn(SOME_TYPE_CODE);

	}

	@Test
	public void whenAttributeIsACollectionOfProvidedTypeWillReturnTrue()
	{

		//GIVEN
		when(isCollectionPredicate.test(attributeDescriptor)).thenReturn(true);
		when(assignableFromAttributePredicate.test(attributeDescriptor)).thenReturn(true);

		//WHEN
		final boolean test = multiCatalogAwareItemAttributePredicateTest.test(attributeDescriptor);

		//THEN
		assertThat(test, is(true));
		verify(assignableFromAttributePredicate, times(1)).test(attributeDescriptor);

	}

	@Test
	public void whenAttributeIsACollectionOfNonExistingTypeWillReturnFalse()
	{

		//GIVEN
		when(isCollectionPredicate.test(attributeDescriptor)).thenReturn(true);
		when(assignableFromAttributePredicate.test(attributeDescriptor)).thenReturn(false);

		//WHEN
		final boolean test = multiCatalogAwareItemAttributePredicateTest.test(attributeDescriptor);

		//THEN
		assertThat(test, is(false));

	}

	@Test
	public void whenAttributeIsNotACollectionOfProvidedTypeWillReturnFalse()
	{

		//GIVEN
		when(isCollectionPredicate.test(attributeDescriptor)).thenReturn(false);
		when(assignableFromAttributePredicate.test(attributeDescriptor)).thenReturn(true);

		//WHEN
		final boolean test = multiCatalogAwareItemAttributePredicateTest.test(attributeDescriptor);

		//THEN
		assertThat(test, is(false));

	}

	@Test
	public void whenAttributeIsNotACollectionOfNonExistingTypeWillReturnFalse()
	{

		//GIVEN
		when(isCollectionPredicate.test(attributeDescriptor)).thenReturn(false);
		when(assignableFromAttributePredicate.test(attributeDescriptor)).thenReturn(false);

		//WHEN
		final boolean test = multiCatalogAwareItemAttributePredicateTest.test(attributeDescriptor);

		//THEN
		assertThat(test, is(false));

	}


}
