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
package de.hybris.platform.odata2services.odata.persistence.populator;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;

/**
 * Converts and populates Items based on EdmEntity information.
 */
public interface EntityModelPopulator
{
	/**
	 * Populate ItemModel with the given info
	 * @param item The ItemModel to populate
	 * @param storageRequest The request context
	 * @throws EdmException -
	 */
	void populateItem(ItemModel item, StorageRequest storageRequest) throws EdmException;

	/**
	 * Populate oDataEntry with the given info
	 * @param oDataEntry The OData entry
	 * @param conversionRequest the request context
	 * @throws EdmException -
	 */
	void populateEntity(ODataEntry oDataEntry, ItemConversionRequest conversionRequest) throws EdmException;
}
