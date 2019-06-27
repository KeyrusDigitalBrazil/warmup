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

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
public class DataToModelPKPredicateTest
{
	private final DataToModelPKPredicate predicate = new DataToModelPKPredicate();

	@Test
	public void returnsTrueWhenTypeIsPK()
	{

		// WHEN
		final boolean test = predicate.test("de.hybris.platform.core.PK");

		// THEN
		assertThat(test, equalTo(true));
	}

	@Test
	public void returnsFalseWhenTypeIsNotPK()
	{

		// WHEN
		final boolean test = predicate.test("some.other.type");

		// THEN
		assertThat(test, equalTo(false));
	}

}
