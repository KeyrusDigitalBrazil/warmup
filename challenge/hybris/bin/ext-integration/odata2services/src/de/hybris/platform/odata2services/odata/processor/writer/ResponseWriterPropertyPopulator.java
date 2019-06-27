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
package de.hybris.platform.odata2services.odata.processor.writer;

import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult;

import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;

public interface ResponseWriterPropertyPopulator
{
	boolean isApplicable(ItemLookupRequest itemLookupRequest);

	/**
	 * {@inheritDoc}
	 * <p>This method generates a builder with a specific property populated.</p>
	 *
	 * @param properties existing properties object already populated by previous builders
	 * @return a builder containing the populated property for a specific implementation of the interface
	 */
	EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder populate(
			EntityProviderWriteProperties properties,
			ItemLookupRequest itemLookupRequest,
			ItemLookupResult result);
}
                                                                      