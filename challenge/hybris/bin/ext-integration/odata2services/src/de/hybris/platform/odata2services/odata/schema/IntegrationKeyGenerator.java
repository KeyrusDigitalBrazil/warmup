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
package de.hybris.platform.odata2services.odata.schema;

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROPERTY_NAME;

import java.util.Collections;

import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.provider.Key;
import org.apache.olingo.odata2.api.edm.provider.PropertyRef;
import org.apache.olingo.odata2.api.edm.provider.SimpleProperty;

public class IntegrationKeyGenerator extends KeyGenerator
{
	@Override
	protected Key createKey()
	{
		return new Key().setKeys(Collections.singletonList(new PropertyRef().setName(INTEGRATION_KEY_PROPERTY_NAME)));
	}

	@Override
	protected boolean isKey(final SimpleProperty property)
	{
		return INTEGRATION_KEY_PROPERTY_NAME.equals(property.getName()) && EdmSimpleTypeKind.String.equals(property.getType());
	}
}
