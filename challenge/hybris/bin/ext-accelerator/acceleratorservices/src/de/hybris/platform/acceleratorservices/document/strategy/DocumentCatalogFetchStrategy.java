/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.acceleratorservices.document.strategy;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;


/**
 * Strategy for fetching CatalogVersionModel.
 */
public interface DocumentCatalogFetchStrategy
{

	/**
	 * @param businessProcessModel
	 * 		businessProcessModel for which the catalogVersion should be fetched
	 * @return {@link CatalogVersionModel} instance
	 */
	CatalogVersionModel fetch(final BusinessProcessModel businessProcessModel);

}
