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

package de.hybris.platform.odata2services.odata.persistence.hook.impl;

import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;

/**
 * A registry which contains all Pre and Post persistence hooks.
 */
public interface PersistenceHookRegistry
{
	/**
	 * Retrieves a {@code PrePersistHook} by the hook name.
	 * @param hookName name of the hook to retrieve. It's up to the implementation what that name means and how hooks are named.
	 * @param integrationKey for the current item that is being created or updated
	 * @return a hook matching the name.
	 */
	PrePersistHook getPrePersistHook(String hookName, String integrationKey);

	/**
	 * Retrieves a {@code PostPersistHook} by the hook name.
	 * @param hookName name of the hook to retrieve. It's up to implementation what that name means and how hooks are named.
	 * @param integrationKey for the current item that is being created or updated
	 * @return a hook matching the name.
	 */
	PostPersistHook getPostPersistHook(String hookName, String integrationKey);
}
