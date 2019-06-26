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

import static de.hybris.platform.odata2services.odata.EdmAnnotationUtils.isNullable;

import de.hybris.platform.odata2services.odata.persistence.exception.MissingNavigationPropertyException;

import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;


/**
 * The visitor to check if the navigation property is missing before the item creation.
 */
public class NullableNavigationPropertiesValidator implements CreateItemValidator
{
	@Override
	public void beforeCreateItem(final EdmEntityType entityType, final ODataEntry oDataEntry) throws EdmException
	{
		for (final String navPropertyName : entityType.getNavigationPropertyNames())
		{
			final EdmNavigationProperty navProperty = (EdmNavigationProperty) entityType.getProperty(navPropertyName);
			final Object navPropertyEntry = oDataEntry.getProperties().get(navProperty.getName());

			if (!isNullable(navProperty) && !oDataEntry.getProperties().containsKey(navPropertyName) && navPropertyEntry == null)
			{
				throw new MissingNavigationPropertyException(entityType.getName(), navPropertyName);
			}
		}
	}
}
