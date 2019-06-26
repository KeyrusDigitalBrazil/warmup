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

package de.hybris.platform.odata2services.odata.schema.entity;

import org.apache.olingo.odata2.api.edm.provider.EntitySet;
import org.atteo.evo.inflector.English;

/**
 * Generates {@link EntitySet} names by pluralizing entity type name.
 */
public class PluralizingEntitySetNameGenerator implements EntitySetNameGenerator
{
	@Override
	public String generate(final String entityType)
	{
		return English.plural(entityType);
	}
}