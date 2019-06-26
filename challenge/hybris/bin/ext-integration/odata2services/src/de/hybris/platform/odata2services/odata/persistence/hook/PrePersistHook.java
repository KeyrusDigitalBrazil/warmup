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
 * A procedure to be executed before persisting an item model. Name of this procedure must be submitted with a POST request in
 * request header named Pre-Persist-Hook.
 */
public interface PrePersistHook
{
	/**
	 * Executes this hook before persisting the given item.
	 * @param item an item to execute this hook with.
	 * @return result of processing/modification of the item by this hook to be persisted.
	 * If empty, that means there is no item to persist.
	 */
	Optional<ItemModel> execute(ItemModel item);
}

