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
package de.hybris.platform.odata2services.odata.processor;

import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;

public interface NavigationSegmentExplorer
{

	ExpandedEntity expandForSingleEntity(ItemLookupRequest lookupRequest, ODataEntry entry) throws EdmException;

	ExpandedEntity expandForEntityList(ItemLookupRequest lookupRequest, ODataEntry entry) throws EdmException;
}
