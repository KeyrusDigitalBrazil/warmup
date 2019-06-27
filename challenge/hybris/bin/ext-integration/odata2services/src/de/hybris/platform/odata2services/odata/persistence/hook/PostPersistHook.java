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

/**
 * A procedure to be executed after persisting an item model. Name of this procedure must be submitted with a POST request in
 * request header named Post-Persist-Hook.
 */
public interface PostPersistHook
{
	/**
	 * Executes this hook after persisting the given item.
	 * @param item an item to execute this hook with
	 */
	void execute(ItemModel item);
}
