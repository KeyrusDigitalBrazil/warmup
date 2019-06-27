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

package de.hybris.platform.integrationservices.asserts;

import de.hybris.platform.integrationservices.util.JsonObject;

/**
 * An extension of {@link org.assertj.core.api.Assertions} to provide convenient methods for asserting content of structures, e.g.
 * XML and JSON
 */
public class StructureAssertions
{
	private StructureAssertions()
	{
		// a utility class should not be instantiable
	}

	public JsonObjectAssertion assertThat(final JsonObject json)
	{
		return JsonObjectAssertion.assertionOf(json);
	}
}
