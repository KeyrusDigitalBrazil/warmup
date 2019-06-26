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
package de.hybris.platform.cmsfacades.media.validator.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ValidFileTypePredicateTest
{
	private static final String SUPPORTED_TYPES = "gif,png,jpeg,pdf";
	private static final String VALID_TYPE = "png";
	private static final String VALID_UPPER_CASE_TYPE = "PNG";
	private static final String INVALID_TYPE = "invalid";

	private final ValidFileTypePredicate predicate = new ValidFileTypePredicate();

	@Before
	public void setUp()
	{
		predicate.setSupportedTypes(SUPPORTED_TYPES);
	}

	@Test
	public void shouldSupportFileType()
	{
		final boolean result = predicate.test(VALID_TYPE);
		assertTrue(result);
	}

	@Test
	public void shouldNotSupportFileType()
	{
		final boolean result = predicate.test(INVALID_TYPE);
		assertFalse(result);
	}

	@Test
	public void shouldSupportFileTypeCaseInsensitive()
	{
		final boolean result = predicate.test(VALID_UPPER_CASE_TYPE);
		assertTrue(result);
	}

}
