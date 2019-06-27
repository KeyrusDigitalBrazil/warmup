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

package de.hybris.platform.dataimportcommons.facades;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

/**
 * A unit test for <code>ItemImportResult</code>
 */
@UnitTest
public class DataItemImportResultUnitTest
{
	private DataItemImportResult importResult;

	@Before
	public void setUp()
	{
		importResult = new DataItemImportResult();
	}

	@Test
	public void testDoesNotContainImportErrorsBeforeTheyAdded()
	{
		final Collection<DataImportError> rejected = importResult.getExportErrorDatas();
		assertThat(rejected).isNotNull();
		assertThat(rejected).isEmpty();
	}

	@Test
	public void testAllAddedErrorsCanBeReadBack()
	{
		final DataImportError err1 = DataImportTestUtils.error("Missing attribute");
		final DataImportError err2 = DataImportTestUtils.error("Unresolved attribute");
		importResult.addErrors(Arrays.asList(err1, err2));

		final Collection<DataImportError> errors = importResult.getExportErrorDatas();

		assertThat(errors).hasSize(2)
				.contains(err1, err2);
	}

	@Test
	public void testSameResultIsReturnedAfterAddingAnErrorToIt()
	{
		final DataItemImportResult orig = new DataItemImportResult();
		final DataItemImportResult returned = orig.addErrors(DataImportTestUtils.errors("some error"));

		assertThat(returned).isEqualTo(orig);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddErrorsDoesNotExpectNullBePassedForTheErrorCollection()
	{
		new DataItemImportResult().addErrors(null);
	}

	@Test
	public void testReportedExceptionCanBeReadBack()
	{
		final Exception ex = new Exception("Import exception has occurred");

		final DataItemImportResult res = new DataItemImportResult(ex);
		assertThat(res.getImportExceptionMessage()).isEqualTo(ex.getMessage());
	}

	@Test
	public void testReportedExceptionWithoutMessageCanBeReadBack()
	{
		final Exception ex = new Exception();

		final DataItemImportResult res = new DataItemImportResult(ex);
		assertThat(res.getImportExceptionMessage()).isEqualTo(ex.getClass().getCanonicalName());
	}
}
