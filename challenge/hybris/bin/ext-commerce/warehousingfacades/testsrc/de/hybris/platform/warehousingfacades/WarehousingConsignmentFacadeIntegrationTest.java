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
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.warehousing.sourcing.ban.service.SourcingBanService;
import de.hybris.platform.warehousingfacades.order.WarehousingConsignmentFacade;
import de.hybris.platform.warehousingfacades.order.data.PackagingInfoData;
import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseData;
import de.hybris.platform.warehousingfacades.util.BaseWarehousingFacadeIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@IntegrationTest
public class WarehousingConsignmentFacadeIntegrationTest extends BaseWarehousingFacadeIntegrationTest
{
	@Resource
	protected WarehousingConsignmentFacade warehousingConsignmentFacade;
	@Resource
	protected SourcingBanService sourcingBanService;

	protected PageableData pageableData;

	@Before
	public void setup()
	{
		pageableData = createPageable(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE, DEFAULT_SORT);
	}

	@Test
	public void getSourcingLocationsForConsignmentCode_Success_OneLocationAvailable()
	{
		//when
		 createDefaultConsignmentAndOrder();
		//then
		assertEquals(1, warehousingConsignmentFacade.getSourcingLocationsForConsignmentCode("con_0", pageableData).getResults().size());
		assertEquals("boston", warehousingConsignmentFacade.getSourcingLocationsForConsignmentCode("con_0", pageableData).getResults().get(0).getCode());
	}

	@Test
	public void getSourcingLocationsForConsignmentCode_Success_MultipleLocationsAvailable()
	{
		//Given
		createDefaultConsignmentAndOrder();
		stockLevels.Camera(warehouses.Toronto(), 4);
		modelService.save(baseStores.NorthAmerica());

		//When
		final SearchPageData<WarehouseData> result = warehousingConsignmentFacade.getSourcingLocationsForConsignmentCode("con_0", pageableData);

		//then
		assertEquals(2, result.getResults().size());
		assertEquals("boston",result.getResults().get(0).getCode());
		assertEquals("toronto",result.getResults().get(1).getCode());
	}

	@Test
	public void getSourcingLocationsForConsignmentCode_Success_NoLocationsAvailableAfterBan()
	{
		//when
		createDefaultConsignmentAndOrder();
		sourcingBanService.createSourcingBan(warehouses.Boston());
		//then
		assertTrue(warehousingConsignmentFacade.getSourcingLocationsForConsignmentCode("con_0", pageableData).getResults().isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void getSourcingLocationsForConsignmentCode_Fail_NullCode()
	{
		warehousingConsignmentFacade.getSourcingLocationsForConsignmentCode(null, pageableData);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getSourcingLocationsForConsignmentCode_Fail_WrongCode()
	{
		warehousingConsignmentFacade.getSourcingLocationsForConsignmentCode("wrongCode", pageableData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getPackagingInfo_Fail_NullCode()
	{
		warehousingConsignmentFacade.getConsignmentPackagingInformation(null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getPackagingInfo_Fail_WrongCode()
	{
		warehousingConsignmentFacade.getConsignmentPackagingInformation("wrongCode");
	}

	@Test
	public void getPackagingInfo_Success()
	{
		createDefaultConsignmentAndOrder();
		final PackagingInfoData packagingInfoData = warehousingConsignmentFacade.getConsignmentPackagingInformation("con_0");

		assertEquals("0", packagingInfoData.getHeight());
		assertEquals("0", packagingInfoData.getInsuredValue());
		assertEquals("0", packagingInfoData.getLength());
		assertEquals("0", packagingInfoData.getWidth());
		assertEquals("0", packagingInfoData.getGrossWeight());
		assertEquals("kg", packagingInfoData.getWeightUnit());
		assertEquals("cm", packagingInfoData.getDimensionUnit());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updatePackagingInformation_Fail_NullCode()
	{
		warehousingConsignmentFacade.updateConsignmentPackagingInformation(null, createPackagingInfo());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updatePackagingInformation_Fail_NullPackagingInfo()
	{
		warehousingConsignmentFacade.updateConsignmentPackagingInformation("con_0", null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void updatePackagingInformation_Fail_WrongCode()
	{
		warehousingConsignmentFacade.updateConsignmentPackagingInformation("wrongCode", createPackagingInfo());
	}

	@Test
	public void updatePackagingInformation_Success()
	{
		createDefaultConsignmentAndOrder();
		final ConsignmentData consignmentData = warehousingConsignmentFacade
				.updateConsignmentPackagingInformation("con_0", createPackagingInfo("1", "2", "3", "4", "5", "in", "lb"));

		assertEquals("1", consignmentData.getPackagingInfo().getWidth());
		assertEquals("2", consignmentData.getPackagingInfo().getHeight());
		assertEquals("3", consignmentData.getPackagingInfo().getLength());
		assertEquals("4", consignmentData.getPackagingInfo().getGrossWeight());
		assertEquals("5", consignmentData.getPackagingInfo().getInsuredValue());
		assertEquals("in", consignmentData.getPackagingInfo().getDimensionUnit());
		assertEquals("lb", consignmentData.getPackagingInfo().getWeightUnit());
	}
}
