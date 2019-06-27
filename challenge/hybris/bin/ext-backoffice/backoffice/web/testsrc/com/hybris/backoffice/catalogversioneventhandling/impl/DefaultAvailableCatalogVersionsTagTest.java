/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.backoffice.catalogversioneventhandling.impl;

import static org.fest.assertions.Assertions.assertThat;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;


public class DefaultAvailableCatalogVersionsTagTest
{
	private DefaultAvailableCatalogVersionsTag testSubject;

	@Before
	public void setUp()
	{
		testSubject = new DefaultAvailableCatalogVersionsTag();
	}

	@Test
	public void shouldSetRandomTagAfterInitialization()
	{
		//when
		testSubject.refresh();

		//then
		assertThat(testSubject.getTag()).isNotNull();
	}

	@Test
	public void shouldChangeTagAfterCatalogVersionChanged()
	{
		//given
		testSubject.refresh();

		//when
		final UUID tagBeforeChangeCatalogVersions = testSubject.getTag();
		testSubject.refresh();
		final UUID tagAfterChangeCatalogVersions = testSubject.getTag();

		//then
		assertThat(tagAfterChangeCatalogVersions).isNotEqualTo(tagBeforeChangeCatalogVersions);
	}
}
