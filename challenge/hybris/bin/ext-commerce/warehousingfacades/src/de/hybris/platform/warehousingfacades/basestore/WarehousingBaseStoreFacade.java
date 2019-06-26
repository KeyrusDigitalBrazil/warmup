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
package de.hybris.platform.warehousingfacades.basestore;

import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseData;


/**
 * Warehousing facade exposing CRUD operations on {@link de.hybris.platform.store.BaseStoreModel}
 */
public interface WarehousingBaseStoreFacade
{
	/**
	 * API to get all {@link de.hybris.platform.ordersplitting.model.WarehouseModel} in the system, for the given
	 * {@link de.hybris.platform.store.BaseStoreModel#UID}
	 *
	 * @param pageableData
	 * 		pageable object that contains info on the number or pages and how many items in each page in addition
	 * 		the sorting info
	 * @param uid
	 * 		the uid of the BaseStore for which warehouses are being retrieved
	 * @return searchPageData result which includes the search results of warehouses for the baseStore, the pagination data, and the available sort options.
	 */
	SearchPageData<WarehouseData> getWarehousesForBaseStoreId(PageableData pageableData, String uid);

	/**
	 * API to get all {@link PointOfServiceData} in the system, for the given
	 * {@link de.hybris.platform.store.BaseStoreModel#UID}
	 *
	 * @param pageableData
	 * 		pageable object that contains info on the number or pages and how many items in each page in addition
	 * 		the sorting info
	 * @param uid
	 * 		the uid of the BaseStore for which warehouses are being retrieved
	 * @return searchPageData result which includes the search results of points of service for the baseStore, the pagination data, and the available sort options.
	 */
	SearchPageData<PointOfServiceData> getPointsOfServiceForBaseStoreId(PageableData pageableData, String uid);
}
