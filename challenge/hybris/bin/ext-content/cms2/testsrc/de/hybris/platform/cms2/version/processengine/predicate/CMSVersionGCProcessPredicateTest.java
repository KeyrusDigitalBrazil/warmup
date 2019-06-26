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
package de.hybris.platform.cms2.version.processengine.predicate;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.processing.CMSVersionGCProcessModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSVersionGCProcessPredicateTest
{
	private static final int VALID_INTEGER = 1;
	private static final int INVALID_INTEGER = -1;

	private final CMSVersionGCProcessPredicate predicate = new CMSVersionGCProcessPredicate();

	@Mock
	private CMSVersionGCProcessModel cmsVersionGCProcessModel;

	@Test
	public void givenValidMaxAgeDaysThenPredicateReturnsTrue()
	{
		// GIVEN
		when(cmsVersionGCProcessModel.getMaxAgeDays()).thenReturn(VALID_INTEGER);

		// WHEN
		final boolean result = predicate.test(cmsVersionGCProcessModel);

		// THEN
		assertThat(result, equalTo(true));
	}

	@Test
	public void givenInvalidMaxAgeDaysThenPredicateReturnsFalse()
	{
		// GIVEN
		when(cmsVersionGCProcessModel.getMaxAgeDays()).thenReturn(INVALID_INTEGER);

		// WHEN
		final boolean result = predicate.test(cmsVersionGCProcessModel);

		// THEN
		assertThat(result, equalTo(false));
	}

	@Test
	public void givenNullMaxAgeDaysThenPredicateReturnsFalse()
	{
		// GIVEN
		when(cmsVersionGCProcessModel.getMaxAgeDays()).thenReturn(null);

		// WHEN
		final boolean result = predicate.test(cmsVersionGCProcessModel);

		// THEN
		assertThat(result, equalTo(false));
	}

	@Test
	public void givenValidMaxNumberVersionsThenPredicateReturnsTrue()
	{
		// GIVEN
		when(cmsVersionGCProcessModel.getMaxNumberVersions()).thenReturn(VALID_INTEGER);

		// WHEN
		final boolean result = predicate.test(cmsVersionGCProcessModel);

		// THEN
		assertThat(result, equalTo(true));
	}

	@Test
	public void givenInvalidMaxNumberVersionsThenPredicateReturnsFalse()
	{
		// GIVEN
		when(cmsVersionGCProcessModel.getMaxNumberVersions()).thenReturn(INVALID_INTEGER);

		// WHEN
		final boolean result = predicate.test(cmsVersionGCProcessModel);

		// THEN
		assertThat(result, equalTo(false));
	}

	@Test
	public void givenNullMaxNumberVersionsThenPredicateReturnsFalse()
	{
		// GIVEN
		when(cmsVersionGCProcessModel.getMaxNumberVersions()).thenReturn(null);

		// WHEN
		final boolean result = predicate.test(cmsVersionGCProcessModel);

		// THEN
		assertThat(result, equalTo(false));
	}
}
