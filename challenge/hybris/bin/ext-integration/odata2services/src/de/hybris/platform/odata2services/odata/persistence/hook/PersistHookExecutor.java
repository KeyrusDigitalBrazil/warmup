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
package de.hybris.platform.odata2services.odata.persistence.hook;

import de.hybris.platform.core.model.ItemModel;

import java.util.Optional;


/**
 * A service for executing persistence hooks.
 */
public interface PersistHookExecutor
{
	/**
	 * Executes a {@link PrePersistHook} on an item model
	 * @param hookName name of the {@code PrePersistHook} Spring bean in the application context to execute.
	 * @param item an item to execute the {@code PrePersistHook} with.
	 * @param integrationKey for the current item that is being created or updated
	 * @return item modified by the hook or empty.
	 * @see PrePersistHook#execute(ItemModel)
	 */
	Optional<ItemModel> runPrePersistHook(String hookName, ItemModel item, String integrationKey);

	/**
	 * Executes a {@link PostPersistHook} on an item model
	 * @param hookName name of the {@code PostPersistHook} Spring bean in the application context to execute.
	 * @param item an item to execute the {@code PostPersistHook} with.
	 * @param integrationKey for the current item that is being created or updated
	 * @see PostPersistHook#execute(ItemModel)
	 */
	void runPostPersistHook(String hookName, ItemModel item, String integrationKey);
}