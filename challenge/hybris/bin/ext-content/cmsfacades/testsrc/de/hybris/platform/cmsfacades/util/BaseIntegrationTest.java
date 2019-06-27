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
package de.hybris.platform.cmsfacades.util;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.SpringCustomContextLoader;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;


@Ignore("Test base class. @Ignore annotation required for inheritance in test classes.")
@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/cmsfacades-spring-test-context.xml" })
public class BaseIntegrationTest extends ServicelayerTest
{
	private static Logger LOG = Logger.getLogger(BaseIntegrationTest.class);

	@Resource
	protected UserService userService;

	protected static SpringCustomContextLoader springCustomContextLoader = null;

	public BaseIntegrationTest()
	{
		if (springCustomContextLoader == null)
		{
			try
			{
				springCustomContextLoader = new SpringCustomContextLoader(getClass());
				springCustomContextLoader.loadApplicationContexts((GenericApplicationContext) Registry.getCoreApplicationContext());
				springCustomContextLoader
						.loadApplicationContextByConvention((GenericApplicationContext) Registry.getCoreApplicationContext());
			}
			catch (final Exception e)
			{
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	/**
	 * CmsManager has CRUD permissions to all attributes of CMSItem types.
	 */
	protected void setCurrentUserCmsManager()
	{
		setCurrentUser("cmsmanager");
	}

	/**
	 * CmsEditor has CRUD permissions to all attributes of CMSItem types, but only has READ permission to the NAME
	 * attribute for CMSItem types. CmsEditor does not have any permission to the styleAttribute attribute for
	 * CMSLinkComponent.
	 */
	protected void setCurrentUserCmsEditor()
	{
		setCurrentUser("cmseditor");
	}

	protected void setCurrentUser(final String userId)
	{
		try
		{
			importCsv("/cmsfacades/test/impex/userGroupsTestData.impex", "utf-8");
			final UserModel user = userService.getUserForUID(userId);
			userService.setCurrentUser(user);
		}
		catch (final ImpExException e )
		{
			LOG.error("Failed to set current user to session!", e);
		}
	}
}
