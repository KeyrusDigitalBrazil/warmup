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
package de.hybris.platform.b2b.punchout.services.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test class for {@link SymmetricManager}.
 */
public class SymmetricManagerTest
{
	private final static String TEXT = "Banana";
	private String key;

	@Before
	public void setUp()
	{
		this.key = SymmetricManager.getKey();
	}

	@Test
	public void testEncryptDecrypt()
	{
		final String encrypted = SymmetricManager.encrypt(TEXT, key);
		Assert.assertNotNull(encrypted);
		final String decrypted = SymmetricManager.decrypt(encrypted, key);
		Assert.assertNotNull(decrypted);
		Assert.assertEquals(TEXT, decrypted);
	}
}
