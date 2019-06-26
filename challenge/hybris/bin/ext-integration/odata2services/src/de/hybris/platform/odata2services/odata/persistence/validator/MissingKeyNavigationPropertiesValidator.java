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

import static de.hybris.platform.odata2services.odata.EdmAnnotationUtils.isKeyProperty;

import de.hybris.platform.odata2services.odata.persistence.exception.MissingKeyNavigationPropertyException;

import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;

/**
 * The validator to check if the key navigation property is missing before the item lookup.
 */
public class MissingKeyNavigationPropertiesValidator implements CreateItemValidator
{
	@Override
	public void beforeItemLookup(final EdmEntityType entityType, final ODataEntry oDataEntry) throws EdmException
	{
		for (final String navName : entityType.getNavigationPropertyNames())
		{
			final EdmNavigationProperty navProperty = (EdmNavigationProperty) entityType.getProperty(navName);

			if ( !isKeyProperty(navProperty) )
			{
				continue;
			}

			final Object navPropertyEntry = oDataEntry.getProperties().get(navProperty.getName());
			if (navPropertyEntry == null)
			{
				throw new MissingKeyNavigationPropertyException(entityType.getName(), navProperty.getName());
			}
		}
	}
}
