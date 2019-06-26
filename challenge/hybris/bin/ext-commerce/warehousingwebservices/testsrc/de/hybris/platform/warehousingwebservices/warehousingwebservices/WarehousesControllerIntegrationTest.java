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
package de.hybris.platform.warehousingwebservices.warehousingwebservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.warehousing.util.models.Warehouses;
import de.hybris.platform.warehousingwebservices.constants.WarehousingwebservicesConstants;
import de.hybris.platform.warehousingwebservices.dto.store.WarehouseWsDto;
import de.hybris.platform.warehousingwebservices.warehousingwebservices.util.BaseWarehousingWebservicesIntegrationTest;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


@NeedsEmbeddedServer(webExtensions = { WarehousingwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class WarehousesControllerIntegrationTest extends BaseWarehousingWebservicesIntegrationTest
{
	@Before
	public void setup()
	{
		super.setup();
	}

	@Test
	public void getWarehouseByCode()
	{
		//When
		final WarehouseWsDto warehouse = getWarehouseByDefault(Warehouses.CODE_BOSTON);
		//then
		assertEquals(Warehouses.CODE_BOSTON, warehouse.getCode());
	}
}
