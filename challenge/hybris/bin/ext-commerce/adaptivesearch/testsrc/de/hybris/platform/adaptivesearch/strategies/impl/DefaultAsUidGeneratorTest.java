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
package de.hybris.platform.adaptivesearch.strategies.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultAsUidGeneratorTest
{
	private DefaultAsUidGenerator defaultAsUidGenerator;

	@Before
	public void setUp() throws Exception
	{
		defaultAsUidGenerator = new DefaultAsUidGenerator();
	}

	@Test
	public void generateUid()
	{
		// when
		final String uid = defaultAsUidGenerator.generateUid();

		// then
		assertNotNull(uid);
		assertFalse(uid.isEmpty());
	}

	@Test
	public void generateMultipleUids()
	{
		// when
		final String uid1 = defaultAsUidGenerator.generateUid();
		final String uid2 = defaultAsUidGenerator.generateUid();
		final String uid3 = defaultAsUidGenerator.generateUid();

		// then
		assertNotNull(uid1);
		assertFalse(uid1.isEmpty());

		assertNotNull(uid2);
		assertFalse(uid2.isEmpty());

		assertNotNull(uid3);
		assertFalse(uid3.isEmpty());

		assertNotEquals(uid1, uid2);
		assertNotEquals(uid2, uid3);
		assertNotEquals(uid3, uid1);
	}
}
