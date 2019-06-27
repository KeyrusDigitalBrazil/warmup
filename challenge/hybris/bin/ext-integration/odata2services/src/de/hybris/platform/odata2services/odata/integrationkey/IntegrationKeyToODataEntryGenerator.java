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
package de.hybris.platform.odata2services.odata.integrationkey;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;

public interface IntegrationKeyToODataEntryGenerator
{
	/**
	 * Generates a OData entry representing the Alias for the EntityType integrationKey property references.
	 * @param entitySet - EntitySet that is being posted to
	 * @param integrationKey - integrationKey string for the item
	 * @return oDataEntry - represents the oDataEntry
	 */
	ODataEntry generate(EdmEntitySet entitySet, String integrationKey) throws EdmException;
}
