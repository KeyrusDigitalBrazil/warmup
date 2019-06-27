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
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DataToModelAtomicTypePredicateTest
{

	private final String ATOMIC_TYPE_CODE = "atomic.type";
	private final String INVALID_TYPE_CODE = "non.atomic.type";

	@InjectMocks
	private DataToModelAtomicTypePredicate predicate;

	@Mock
	private TypeService typeService;

	private AtomicTypeModel atomicTypeModel = new AtomicTypeModel();

	@Test
	public void testAtomicTypeReturnsTrue()
	{

		//GIVEN
		when(typeService.getAtomicTypeForCode(ATOMIC_TYPE_CODE)).thenReturn(atomicTypeModel);

		// WHEN
		final boolean test = predicate.test(ATOMIC_TYPE_CODE);

		// THEN
		assertThat(test, equalTo(true));
	}

	@Test
	public void testInvalidTypeReturnsFalse()
	{
		//GIVEN
		when(typeService.getAtomicTypeForCode(INVALID_TYPE_CODE)).thenThrow(UnknownIdentifierException.class);

		// WHEN
		final boolean test = predicate.test(INVALID_TYPE_CODE);

		// THEN
		assertThat(test, equalTo(false));
	}

}
