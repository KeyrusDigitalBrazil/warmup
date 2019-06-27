/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.scimfacades.utils;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.scimfacades.ScimUserEmail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class ScimUtilsTest
{

	@Test
	public void testGetPrimaryEmailWhenEmailsDoesntExist()
	{
		Assert.assertNull(ScimUtils.getPrimaryEmail(null));
	}

	@Test
	public void testGetPrimaryEmailWhenEmailsExist()
	{
		final ScimUserEmail email1 = new ScimUserEmail();
		email1.setPrimary(false);
		email1.setValue("value1");

		final ScimUserEmail email2 = new ScimUserEmail();
		email2.setPrimary(true);
		email2.setValue("value2");

		final List<ScimUserEmail> emails = new ArrayList<>();
		emails.add(email1);
		emails.add(email2);

		final ScimUserEmail primaryEmail = ScimUtils.getPrimaryEmail(emails);
		Assert.assertNotNull(primaryEmail);
		Assert.assertEquals(email2, primaryEmail);
	}

}
