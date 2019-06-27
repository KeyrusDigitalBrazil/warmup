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
package de.hybris.platform.odata2services.odata.persistence;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.processor.ODataContext;

public interface StorageRequestFactory
{
	/**
	 * Get the StorageRequest for persistence processing
	 * @param oDataContext The ODataContext to be used.
	 * @param responseContentType contentType to use.
	 * @param entitySet EdmEntitySet to use.
	 * @param entry ODataEntry to use.
	 * @return The StorageRequest used for persistence
	 */
	StorageRequest create(final ODataContext oDataContext, final String responseContentType, final EdmEntitySet entitySet, final ODataEntry entry);
}
