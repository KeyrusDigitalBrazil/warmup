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
package de.hybris.platform.cms2.version.predicate;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.version.converter.attribute.data.VersionAttributeDescriptor;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.TypeModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AtomicTypePredicateTest
{
	private final String NOT_ATOMIC_TYPE = "NOT_ATOMIC_TYPE";

	private final AtomicTypePredicate predicate = new AtomicTypePredicate();

	@Mock
	private VersionAttributeDescriptor versionAttributeDescriptor;

	@Mock
	private TypeModel someType;

	@Before
	public void setup()
	{
		when(versionAttributeDescriptor.getType()).thenReturn(someType);
	}

	@Test
	public void givenAttributeOfTypeAtomicThenPredicatesReturnsTrue()
	{
		// GIVEN
		when(someType.getItemtype()).thenReturn(AtomicTypeModel._TYPECODE);

		// WHEN
		final boolean result = predicate.test(versionAttributeDescriptor);

		// THEN
		assertThat(result, equalTo(true));
	}

	@Test
	public void givenAttributeOfNotAtomicThenPredicatesReturnsFalse()
	{
		// GIVEN
		when(someType.getItemtype()).thenReturn(NOT_ATOMIC_TYPE);

		// WHEN
		final boolean result = predicate.test(versionAttributeDescriptor);

		// THEN
		assertThat(result, equalTo(false));
	}

}
