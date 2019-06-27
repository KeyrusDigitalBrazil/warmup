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

package com.hybris.datahub.core.facades;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;

/**
 * A unit test for <code>ItemImportResult</code>
 */
@UnitTest
public class ItemImportResultUnitTest
{
	private ItemImportResult importResult;

	@Before
	public void setUp()
	{
		importResult = new ItemImportResult();
	}

	@Test
	public void testSuccessfulByDefault()
	{
		assertThat(importResult.isSuccessful()).isTrue();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testImportErrorCollectionCannotBeModifiedFromOutside()
	{
		final ImportError errorBeingAddedAroundItemImportResult = ImportTestUtils.error("Item 1 is rejected");
		importResult.getErrors().add(errorBeingAddedAroundItemImportResult);
	}

	@Test
	public void testResultIsUnsuccessfulWhenAtLeastOneImportErrorWasAdded()
	{
		importResult.addErrors(ImportTestUtils.errors("Some problem"));

		assertThat(importResult.isSuccessful()).isFalse();
	}

	@Test
	public void toStringContainsSUCCESSWhenResultIsSuccessful()
	{
		final ItemImportResult res = new ItemImportResult();
		assert res.isSuccessful() : "Result with no errors or exception should be successful";

		assertThat(res.toString()).contains("SUCCESS");
	}

	@Test
	public void toStringContainsERRORWhenResultIsNotSuccessful()
	{
		final ItemImportResult res = (ItemImportResult) new ItemImportResult().addErrors(ImportTestUtils.errors("an error"));
		assert !res.isSuccessful() : "Result with errors should be unsuccessful";

		assertThat(res.toString()).contains("ERROR");
	}

	@Test
	public void toStringPrintsOutExceptionWhenItIsPresentInTheResult()
	{
		final String res = new ItemImportResult((new IOException("cannot read file"))).toString();

		assertThat(res).contains("ERROR")
					   .contains("cannot read file");
	}
}
