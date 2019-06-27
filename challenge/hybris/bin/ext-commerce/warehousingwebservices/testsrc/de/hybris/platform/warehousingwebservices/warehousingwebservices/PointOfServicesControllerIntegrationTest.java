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
import de.hybris.platform.commercewebservicescommons.dto.store.PointOfServiceWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.warehousing.util.models.PointsOfService;
import de.hybris.platform.warehousing.util.models.Warehouses;
import de.hybris.platform.warehousingwebservices.constants.WarehousingwebservicesConstants;
import de.hybris.platform.warehousingwebservices.dto.store.WarehouseCodesWsDto;
import de.hybris.platform.warehousingwebservices.dto.store.WarehouseSearchPageWsDto;
import de.hybris.platform.warehousingwebservices.warehousingwebservices.util.BaseWarehousingWebservicesIntegrationTest;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@NeedsEmbeddedServer(webExtensions = { WarehousingwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PointOfServicesControllerIntegrationTest extends BaseWarehousingWebservicesIntegrationTest
{
	@Before
	public void setup()
	{
		super.setup();
	}

	@Test
	public void getPointOfServiceByName()
	{
		//When
		final PointOfServiceWsDTO result = getPointOfServiceByDefault(PointsOfService.NAME_MONTREAL_DOWNTOWN);
		//then
		assertEquals(result.getName(), PointsOfService.NAME_MONTREAL_DOWNTOWN);
	}

	@Test
	public void getWarehouseForPointOfService()
	{
		//When
		final WarehouseSearchPageWsDto result = getWarehouseForPointOfServiceByDefault(PointsOfService.NAME_MONTREAL_DOWNTOWN);
		final List<String> warehouses = Arrays.asList(Warehouses.CODE_MONTREAL, Warehouses.CODE_GRIFFINTOWN);

		//then
		assertEquals(2, result.getWarehouses().size());
		assertTrue(result.getWarehouses().stream().anyMatch(warehouse -> warehouses.contains(warehouse.getCode())));
	}

	@Test
	public void updatePointOfServiceWarehouses()
	{
		//When
		final WarehouseCodesWsDto warehouseCodesWsDto = new WarehouseCodesWsDto();
		warehouseCodesWsDto.setCodes(Collections.singletonList(Warehouses.CODE_BOSTON));
		final PointOfServiceWsDTO result = postUpdatePointOfServiceWarehouses(PointsOfService.NAME_MONTREAL_DOWNTOWN,
				warehouseCodesWsDto);
		final List<String> warehouses = Arrays
				.asList(Warehouses.CODE_BOSTON, Warehouses.CODE_MONTREAL, Warehouses.CODE_GRIFFINTOWN);
		//then
		assertEquals(3, result.getWarehouseCodes().size());
		assertTrue(result.getWarehouseCodes().stream().anyMatch(warehouses::contains));
	}

	@Test
	public void deleteWarehousesFromPointOfService()
	{
		//When
		final Response result = deleteWarehousesFromPointOfService(PointsOfService.NAME_MONTREAL_DOWNTOWN,
				Warehouses.CODE_MONTREAL);
		//then
		assertEquals(result.getStatus(), 204);
	}

	@Test
	public void updatePointOfServiceAddress()
	{
		//When
		final PointOfServiceWsDTO result = putUpdatePointOfServiceAddress(PointsOfService.NAME_MONTREAL_DOWNTOWN,
				createUsAddress());
		//then
		assertEquals("5th Avenue", result.getAddress().getLine1());
		assertEquals("79777", result.getAddress().getPostalCode());
	}

	@Test(expected = BadRequestException.class)
	public void updatePointOfServiceWithInvalidAddress()
	{
		//When
		final AddressWsDTO address = createUsAddress();
		address.setCountry(null);
		try
		{
			putUpdatePointOfServiceAddress(PointsOfService.NAME_MONTREAL_DOWNTOWN, address);
		}
		catch (final BadRequestException | InternalServerErrorException e)
		{
			throw new BadRequestException();
		}
	}
}
