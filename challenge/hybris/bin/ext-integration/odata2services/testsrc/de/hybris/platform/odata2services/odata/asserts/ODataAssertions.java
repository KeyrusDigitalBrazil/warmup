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

package de.hybris.platform.odata2services.odata.asserts;

import de.hybris.platform.integrationservices.asserts.JsonObjectAssertion;
import de.hybris.platform.integrationservices.util.JsonObject;

import org.apache.olingo.odata2.api.processor.ODataResponse;

/**
 * An extension of {@link org.assertj.core.api.Assertions} to provide convenient methods around OData object assertions.
 */
public class ODataAssertions
{
	private ODataAssertions()
	{
		// Contains utility methods and should not be instantiated.
	}

	public static ODataResponseAssertion assertThat(final ODataResponse actual)
	{
		return ODataResponseAssertion.assertionOf(actual);
	}

	public static JsonObjectAssertion assertThat(final JsonObject json)
	{
		return JsonObjectAssertion.assertionOf(json);
	}
}
