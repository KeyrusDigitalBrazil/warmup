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
package de.hybris.platform.warehousingfacades;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.warehousingfacades.pointofservice.WarehousingPointOfServiceFacade;
import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseCodesDataList;
import de.hybris.platform.warehousingfacades.util.BaseWarehousingFacadeIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@IntegrationTest
public class WarehousingPointOfServiceFacadeIntegrationTest extends BaseWarehousingFacadeIntegrationTest
{
	protected static final String LINE_1 = "line1";
	protected static final String POSTAL_CODE = "postalCode";
	protected static final String ISOCODE = "US";
	@Resource
	protected WarehousingPointOfServiceFacade warehousingPointOfServiceFacade;

	protected PageableData pageableData;

	@Before
	public void setup()
	{
		pageableData = createPageable(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE, DEFAULT_SORT);
		pointsOfService.Boston();
		modelService.saveAll();
	}

	@Test
	public void testGetPointOfServiceByName()
	{
		assertNotNull(warehousingPointOfServiceFacade.getPointOfServiceByName(pointsOfService.NAME_BOSTON));
	}

	@Test(expected = ModelNotFoundException.class)
	public void testGetPointOfServiceByNameWrongCode()
	{
		warehousingPointOfServiceFacade.getPointOfServiceByName("WrongCode");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPointOfServiceByNameNull()
	{
		warehousingPointOfServiceFacade.getPointOfServiceByName(null);
	}

	@Test
	public void testGetListOfWarehousesForPointOfService()
	{
		assertNotNull(warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON));
		assertEquals(1,
				warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON).getResults()
						.size());
	}

	@Test(expected = ModelNotFoundException.class)
	public void testGetListOfWarehousesForPointOfServiceWrongName()
	{
		assertNotNull(warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, "WrongName"));
	}

	@Test
	public void testUpdatePointOfServiceWithWarehouses()
	{

		assertNotNull(warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON));
		// make sure I have one warehouse for boston POS
		assertEquals(1,
				warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON).getResults()
						.size());
		//update boston POS with one more warehouse
		final WarehouseCodesDataList warehouseCodesDataList = new WarehouseCodesDataList();
		warehouseCodesDataList.setCodes(Arrays.asList(warehouses.CODE_MONTREAL));
		assertNotNull(warehousingPointOfServiceFacade.updatePointOfServiceWithWarehouses(pointsOfService.NAME_BOSTON,
				warehouseCodesDataList));
		// make sure we have 2 warehouses for boston POS
		assertEquals(2,
				warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON).getResults()
						.size());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testUpdatePointOfServiceWithEmptyWarehouse()
	{
		assertNotNull(warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON));
		// make sure I have one warehouse for boston POS
		assertEquals(1,
				warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON).getResults()
						.size());
		//update boston POS with one more warehouse
		final WarehouseCodesDataList warehouseCodesDataList = new WarehouseCodesDataList();
		warehouseCodesDataList.setCodes(Arrays.asList(""));
		warehousingPointOfServiceFacade.updatePointOfServiceWithWarehouses(pointsOfService.NAME_BOSTON, warehouseCodesDataList);
	}

	@Test
	public void testUpdatePointOfServiceWithSameWarehouseTwice()
	{

		assertNotNull(warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON));
		// make sure I have one warehouse for boston POS
		assertEquals(1,
				warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON).getResults()
						.size());
		//update boston POS with the same warehouse twice
		final WarehouseCodesDataList warehouseCodesDataList = new WarehouseCodesDataList();
		warehouseCodesDataList.setCodes(Arrays.asList(warehouses.CODE_MONTREAL, warehouses.CODE_MONTREAL));
		assertNotNull(warehousingPointOfServiceFacade
				.updatePointOfServiceWithWarehouses(pointsOfService.NAME_BOSTON, warehouseCodesDataList));
		// make sure we have 2 warehouses for boston POS
		assertEquals(3,
				warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON).getResults()
						.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdatePointOfServiceInvalidWarehouse()
	{
		assertNotNull(warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON));
		// make sure I have one warehouse for boston POS
		assertEquals(1,
				warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON).getResults()
						.size());
		//update boston POS with a list of warehouses one of them being invalid
		final WarehouseCodesDataList warehouseCodesDataList = new WarehouseCodesDataList();
		warehouseCodesDataList.setCodes(Arrays.asList(warehouses.CODE_MONTREAL, null));
		assertNotNull(warehousingPointOfServiceFacade
				.updatePointOfServiceWithWarehouses(pointsOfService.NAME_BOSTON, warehouseCodesDataList));
	}

	@Test
	public void testDeletePointOfServiceWithWarehouses()
	{
		assertNotNull(warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON));
		// make sure I have one warehouse for boston POS
		assertEquals(1,
				warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON).getResults()
						.size());
		//update boston POS with one more warehouse
		final WarehouseCodesDataList warehouseCodesDataList = new WarehouseCodesDataList();
		warehouseCodesDataList.setCodes(Arrays.asList(warehouses.CODE_MONTREAL));
		assertNotNull(warehousingPointOfServiceFacade.updatePointOfServiceWithWarehouses(pointsOfService.NAME_BOSTON, warehouseCodesDataList));
		// make sure we have 2 warehouses for boston POS
		assertEquals(2,
				warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON).getResults()
						.size());
		// delete one montreal warehouse from boston POS
		assertNotNull(
				warehousingPointOfServiceFacade.deleteWarehouseFromPointOfService(pointsOfService.NAME_BOSTON, warehouses.CODE_MONTREAL));
		// make sure we have 1 warehouse for boston POS
		assertEquals(1,
				warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON).getResults()
						.size());
	}

	@Test
	public void testDeletePointOfServiceWithInvalidWarehouse()
	{
		assertNotNull(warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON));
		// make sure I have one warehouse for boston POS
		assertEquals(1,
				warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON).getResults()
						.size());
		//update boston POS with one more warehouse
		final WarehouseCodesDataList warehouseCodesDataList = new WarehouseCodesDataList();
		warehouseCodesDataList.setCodes(Arrays.asList(warehouses.CODE_MONTREAL));
		assertNotNull(warehousingPointOfServiceFacade.updatePointOfServiceWithWarehouses(pointsOfService.NAME_BOSTON, warehouseCodesDataList));
		// make sure we have 2 warehouses for boston POS
		assertEquals(2,
				warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON).getResults()
						.size());
		// delete one toronto warehouse which doesn't belong to boston POS
		assertNotNull(
				warehousingPointOfServiceFacade.deleteWarehouseFromPointOfService(pointsOfService.NAME_BOSTON, warehouses.CODE_TORONTO));
		// make sure we have 1 warehouse for boston POS
		assertEquals(2,
				warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON).getResults()
						.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeletePointOfServiceWithNullWarehouse()
	{
		assertNotNull(warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON));
		// make sure I have one warehouse for boston POS
		assertEquals(1,
				warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON).getResults()
						.size());
		//update boston POS with one more warehouse
		final WarehouseCodesDataList warehouseCodesDataList = new WarehouseCodesDataList();
		warehouseCodesDataList.setCodes(Arrays.asList(warehouses.CODE_MONTREAL));
		assertNotNull(warehousingPointOfServiceFacade.updatePointOfServiceWithWarehouses(pointsOfService.NAME_BOSTON, warehouseCodesDataList));
		// make sure we have 2 warehouses for boston POS
		assertEquals(2,
				warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON).getResults()
						.size());
		//delete warehouse with a null code from boston POS
		warehousingPointOfServiceFacade.deleteWarehouseFromPointOfService(pointsOfService.NAME_BOSTON, null);
		assertEquals(1,
				warehousingPointOfServiceFacade.getWarehousesForPointOfService(pageableData, pointsOfService.NAME_BOSTON).getResults()
						.size());
	}

	@Test
	public void testUpdatePointOfServiceWithAddress()
	{
		final AddressData addressData = createAddressData();

		warehousingPointOfServiceFacade.updatePointOfServiceWithAddress(pointsOfService.NAME_BOSTON, addressData);
		assertEquals(LINE_1,
				warehousingPointOfServiceFacade.getPointOfServiceByName(pointsOfService.NAME_BOSTON).getAddress().getLine1());
		assertEquals(POSTAL_CODE,
				warehousingPointOfServiceFacade.getPointOfServiceByName(pointsOfService.NAME_BOSTON).getAddress().getPostalCode());
		assertEquals(ISOCODE,
				warehousingPointOfServiceFacade.getPointOfServiceByName(pointsOfService.NAME_BOSTON).getAddress().getCountry()
						.getIsocode());
	}

	protected AddressData createAddressData()
	{
		AddressData addressData = new AddressData();
		addressData.setLine1(LINE_1);
		addressData.setPostalCode(POSTAL_CODE);
		CountryData countryData = new CountryData();
		countryData.setIsocode(ISOCODE);
		addressData.setCountry(countryData);
		return addressData;
	}
}
