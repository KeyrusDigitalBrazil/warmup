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

package de.hybris.platform.odata2services.odata.persistence.validator;

import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;

/**
 * Defines a Validator that is called in the lifecycle of persisting IntegrationObjects.
 * Methods are implemented as empty default so Implementors of the Interface can only overwrite required methods.
 */
public interface CreateItemValidator
{
	/**
	 * Is called immediately before the lookup for a given Item
	 *
	 * @param entityType type of the item to look for
	 * @param oDataEntry item data to look for
	 * @throws EdmException
	 */
	default void beforeItemLookup(EdmEntityType entityType, ODataEntry oDataEntry) throws EdmException
	{
	}

	/**
	 * Is called immediately before the item is created
	 *
	 * @param entityType type of the item to create
	 * @param oDataEntry item data to create
	 * @throws EdmException
	 */
	default void beforeCreateItem(EdmEntityType entityType, ODataEntry oDataEntry) throws EdmException
	{
	}

	/**
	 * Is called immediately before the item is populated with the oDataEntry
	 *
	 * @param entityType type of the item to populate
	 * @param oDataEntry item data to populate
	 * @throws EdmException
	 */
	default void beforePopulateItem(EdmEntityType entityType, ODataEntry oDataEntry) throws EdmException
	{
	}
}
