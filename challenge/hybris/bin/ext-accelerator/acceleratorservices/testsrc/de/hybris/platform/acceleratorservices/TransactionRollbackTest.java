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
package de.hybris.platform.acceleratorservices;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


@IntegrationTest
public class TransactionRollbackTest extends ServicelayerTransactionalTest
{
	@Resource
	private UserService userService;
	@Resource
	private ModelService modelService;

	private final String userId = "TransactionRollbackTestUser" + new Date().getTime();

	@Test(expected = UnknownIdentifierException.class)
	public void runFirsthouldBeNotFoundBeforeTheTest()
	{
		userService.getUserForUID(userId);
	}

	@Test
	public void runSecondShouldInsertUser()
	{
		final UserModel user = modelService.create(UserModel.class);
		user.setUid(userId);
		modelService.save(user);
		Assert.assertNotNull(userService.getUserForUID(userId));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void runLasThouldBeNotFoundAfterTheTest()
	{
		userService.getUserForUID(userId);
	}
}
