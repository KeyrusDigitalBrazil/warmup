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
package de.hybris.platform.warehousingfacades.replacement;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.storefinder.impl.StoreFinderStockFacadeIntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.warehousing.constants.WarehousingTestConstants;

import java.io.IOException;


/**
 * Re-implements test {@link StoreFinderStockFacadeIntegrationTest} to provide missing information required when warehousing extensions is present
 */
@IntegrationTest(replaces = StoreFinderStockFacadeIntegrationTest.class)
public class OrderManagementStoreFinderStockFacadeIntegrationTest extends StoreFinderStockFacadeIntegrationTest
{
	@Override
	public void prepare() throws Exception
	{
		super.prepare();
		insertExtraInformation();
	}

	/**
	 * Import impex during setup to add relation between warehouse and delivery mode as well as the default ATP formula for the used basestore.
	 *
	 * @throws IOException
	 * @throws ImpExException
	 */
	private void insertExtraInformation() throws IOException, ImpExException
	{
		importCsv("/warehousingfacades/test/impex/replacement/replacement-store-finder-stock-test-data.impex", WarehousingTestConstants.ENCODING);
		importCsv("/warehousingfacades/test/impex/replacement/replacement-add-formula-teststore.impex", WarehousingTestConstants.ENCODING);
	}
}
