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
package de.hybris.platform.cmsfacades.namedquery.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.InvalidNamedQueryException;
import de.hybris.platform.cms2.namedquery.service.NamedQueryFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NamedQueryExistsPredicateTest
{
	private static final String NAMED_QUERY = "named-query";

	@InjectMocks
	private NamedQueryExistsPredicate namedQueryExistsPredicate;

	@Mock
	private NamedQueryFactory namedQueryFactory;

	@Before
	public void setUp() throws InvalidNamedQueryException
	{
		when(namedQueryFactory.getNamedQuery(NAMED_QUERY)).thenReturn(Mockito.anyString());
	}

	@Test
	public void shouldPass()
	{
		final boolean result = namedQueryExistsPredicate.test(NAMED_QUERY);
		assertTrue(result);
	}

	@Test
	public void shouldFail() throws InvalidNamedQueryException
	{
		when(namedQueryFactory.getNamedQuery(NAMED_QUERY)).thenThrow(new InvalidNamedQueryException(NAMED_QUERY));

		final boolean result = namedQueryExistsPredicate.test(NAMED_QUERY);
		assertFalse(result);
	}

}
