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
package de.hybris.platform.integrationservices.integrationkey;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;

public interface IntegrationKeyMetadataGenerator
{
	/**
	 * Generate a representation of the unique attributes of the given item. This representation is the metadata of how an integrationKey
	 * is generated for a given {@link IntegrationObjectItemModel}.
	 * @param item the item to generate key metadata for
	 * @return a String representing the integrationKey metadata according the the strategy defined in the implementation
	 */
	String generateKeyMetadata(IntegrationObjectItemModel item);
}
