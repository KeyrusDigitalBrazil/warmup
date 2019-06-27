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
import static de.hybris.platform.odata2services.odata.EdmAnnotationUtils.isNullable;

import de.hybris.platform.odata2services.odata.persistence.exception.MissingKeyPropertyException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.EdmAnnotatable;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;

import com.google.common.collect.Lists;

public class MissingKeyPropertiesValidator implements CreateItemValidator
{
	@Override
	public void beforeItemLookup(final EdmEntityType entityType, final ODataEntry oDataEntry) throws EdmException
	{
		final Map<String, Object> properties = oDataEntry.getProperties();
		final Optional<String> missingKey = findMissingNonNullableKeyProperties(entityType).stream()
				.filter(name -> !properties.containsKey(name)).findFirst();
		if (missingKey.isPresent())
		{
			throw new MissingKeyPropertyException(entityType.getName(), missingKey.get());
		}
	}

	protected List<String> findMissingNonNullableKeyProperties(final EdmEntityType entityType) throws EdmException
	{
		final List<String> names = Lists.newArrayList();
		for (final String name : entityType.getPropertyNames())
		{
			final EdmTyped property = entityType.getProperty(name);
			if (property instanceof EdmProperty && isKeyProperty(property) && !isNullable((EdmAnnotatable) property))
			{
				names.add(name);
			}
		}
		return names;
	}
}