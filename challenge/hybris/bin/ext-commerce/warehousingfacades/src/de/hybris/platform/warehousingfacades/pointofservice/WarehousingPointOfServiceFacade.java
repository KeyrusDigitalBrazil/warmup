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
package de.hybris.platform.warehousingfacades.pointofservice;

import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseCodesDataList;
import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseData;


/**
 * Warehousing facade exposing CRUD operations on {@link de.hybris.platform.storelocator.model.PointOfServiceModel}
 */
public interface WarehousingPointOfServiceFacade
{
	/**
	 * API to get a point of service by posName
	 *
	 * @param posName
	 * 		the point of service's posName
	 * @return the point of service
	 */
	PointOfServiceData getPointOfServiceByName(String posName);

	/**
	 * API to get all {@link de.hybris.platform.ordersplitting.model.WarehouseModel} in the system, for the given
	 * {@link de.hybris.platform.storelocator.model.PointOfServiceModel#NAME}
	 *
	 * @param pageableData
	 * 		pageable object that contains info on the number or pages and how many items in each page in addition
	 * 		the sorting info
	 * @param posName
	 * 		the posName of the PointOfService for which warehouses are being retrieved
	 * @return the list of warehouses for the given PointOfService
	 */
	SearchPageData<WarehouseData> getWarehousesForPointOfService(PageableData pageableData, String posName);

	/**
	 * API to update a {@link de.hybris.platform.storelocator.model.PointOfServiceModel} with a list of warehouses
	 *
	 * @param posName
	 * 		the posName of the PointOfService for which warehouses will be deleted
	 * @param warehouseCodes
	 * 		the object containing the list of warehouse codes to add to the point of service
	 * @return the updated {@link PointOfServiceData} without the specified warehouses
	 */

	PointOfServiceData updatePointOfServiceWithWarehouses(String posName, WarehouseCodesDataList warehouseCodes);

	/**
	 * API to delete warehouses from {@link de.hybris.platform.storelocator.model.PointOfServiceModel}
	 *
	 * @param posName
	 * 		the posName of the PointOfService for which warehouses will be deleted
	 * @param warehouseCode
	 * 		the warehouse which has to be removed from the point of service.
	 * @return the updated {@link PointOfServiceData} without the specified warehouses
	 */
	PointOfServiceData deleteWarehouseFromPointOfService(String posName, String warehouseCode);

	/**
	 * API to update a {@link de.hybris.platform.storelocator.model.PointOfServiceModel}
	 *
	 * @param posName
	 * 		the posName of POS that we want to update
	 * @param addressData
	 * 		the address which we want to update the pos with
	 * @return the {@link PointOfServiceData} updated with the passed address and converted from the newly updated {@link de.hybris.platform.storelocator.model.PointOfServiceModel}
	 */
	PointOfServiceData updatePointOfServiceWithAddress(String posName, AddressData addressData);

}
