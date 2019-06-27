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
 *
 */
package de.hybris.platform.warehousingwebservices.warehousingwebservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.warehousing.util.models.Orders;
import de.hybris.platform.warehousingwebservices.constants.WarehousingwebservicesConstants;
import de.hybris.platform.warehousingwebservices.warehousingwebservices.util.BaseWarehousingWebservicesIntegrationTest;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.ws.rs.core.Response;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;


@NeedsEmbeddedServer(webExtensions = { WarehousingwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class OrdersControllerIntegrationTest extends BaseWarehousingWebservicesIntegrationTest
{
	private OrderModel order;

	@Before
	public void setup()
	{
		super.setup();
		order = createFailedSourcedOrder();
	}

	@Test
	public void postResource()
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 5);
		stockLevels.Camera(warehouses.Boston(), 4);

		// When
		final Response result = postResourceOrder(Orders.CODE_CAMERA_SHIPPED);

		//Then
		assertResponse(Response.Status.OK, Optional.empty(), result);
	}

	@Test
	public void postOnHold()
	{
		//Given
		order = createShippedConsignmentAndOrder();
		order.setStatus(OrderStatus.READY);
		order.getConsignments().forEach(consignment ->
		{
			consignment.setStatus(ConsignmentStatus.READY);
			getModelService().save(consignment);
		});
		getModelService().save(order);
		// When
		final Response result = postPutOrderOnHold(Orders.CODE_CAMERA_SHIPPED);
		//Then
		assertResponse(Response.Status.OK, Optional.empty(), result);
	}


	@Test
	public void postFailedOnHold()
	{
		// When
		final Response result = postPutOrderOnHold(createShippedConsignmentAndOrder().getCode());

		//Then
		assertResponse(Response.Status.BAD_REQUEST, Optional.empty(), result);
	}
}
