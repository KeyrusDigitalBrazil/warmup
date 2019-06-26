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
package de.hybris.platform.smarteditwebservices.utils;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.security.UserRightModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.security.permissions.PermissionAssignment;
import de.hybris.platform.servicelayer.security.permissions.PermissionManagementService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utility class to import user global permissions
 */
public class PermissionsImportUtils
{
	private final static Logger LOG = LoggerFactory.getLogger(PermissionsImportUtils.class);

	public static void importGlobalPermission(final String principalUid, final String permissionName)
	{
		final FlexibleSearchService flexibleSearchService = Registry.getApplicationContext().getBean("flexibleSearchService",
				FlexibleSearchService.class);
		final ModelService modelService = Registry.getApplicationContext().getBean("modelService", ModelService.class);
		final PermissionManagementService permissionManagementService = Registry.getApplicationContext().getBean("permissionManagementService",
				PermissionManagementService.class);

		final UserRightModel userRight = new UserRightModel();
		userRight.setCode(permissionName);

		try
		{
			flexibleSearchService.getModelByExample(userRight);
		}
		catch (final ModelNotFoundException e)
		{
			LOG.info("Could not find user right %s . Will be created on the fly.", userRight.getCode());
			modelService.save(userRight);
		}

		final PrincipalModel principalExample = new PrincipalModel();
		principalExample.setUid(principalUid);
		final PrincipalModel principal = flexibleSearchService.getModelByExample(principalExample);

		permissionManagementService.addGlobalPermission(new PermissionAssignment(permissionName, principal));
	}
}
