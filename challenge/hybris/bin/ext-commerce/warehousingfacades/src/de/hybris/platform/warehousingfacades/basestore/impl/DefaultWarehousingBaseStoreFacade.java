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
package de.hybris.platform.warehousingfacades.basestore.impl;

import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.ordermanagementfacades.OmsBaseFacade;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.warehousingfacades.basestore.WarehousingBaseStoreFacade;
import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseData;

import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.Map;


/**
 * Default implementation of {@link de.hybris.platform.warehousingfacades.basestore.WarehousingBaseStoreFacade}
 */
public class DefaultWarehousingBaseStoreFacade extends OmsBaseFacade implements WarehousingBaseStoreFacade
{
	private PagedGenericDao<WarehouseModel> warehousesByBaseStorePagedDao;
	private PagedGenericDao<PointOfServiceModel> pointsOfServicePagedDao;
	private Converter<WarehouseModel, WarehouseData> warehouseConverter;
	private BaseStoreService baseStoreService;
	private Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter;

	@Override
	public SearchPageData<WarehouseData> getWarehousesForBaseStoreId(PageableData pageableData, String uid)
	{
		final BaseStoreModel baseStore = getBaseStoreService().getBaseStoreForUid(uid);

		final Map<String, BaseStoreModel> params = new HashMap<>();
		params.put(WarehouseModel.BASESTORES, baseStore);

		final SearchPageData<WarehouseModel> warehousesSearchPageModel = getWarehousesByBaseStorePagedDao()
				.find(params, pageableData);
		return convertSearchPageData(warehousesSearchPageModel, getWarehouseConverter());
	}

	@Override
	public SearchPageData<PointOfServiceData> getPointsOfServiceForBaseStoreId(PageableData pageableData, String uid)
	{
		final BaseStoreModel baseStore = getBaseStoreService().getBaseStoreForUid(uid);
		final Map<String, BaseStoreModel> params = new HashMap<>();
		params.put(PointOfServiceModel.BASESTORE, baseStore);

		final SearchPageData<PointOfServiceModel> pointsOfServiceSearchPageModel = getPointsOfServicePagedDao()
				.find(params, pageableData);
		return convertSearchPageData(pointsOfServiceSearchPageModel, getPointOfServiceConverter());


	}

	protected Converter<WarehouseModel, WarehouseData> getWarehouseConverter()
	{
		return warehouseConverter;
	}

	@Required
	public void setWarehouseConverter(final Converter<WarehouseModel, WarehouseData> warehouseConverter)
	{
		this.warehouseConverter = warehouseConverter;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected PagedGenericDao<WarehouseModel> getWarehousesByBaseStorePagedDao()
	{
		return this.warehousesByBaseStorePagedDao;
	}

	@Required
	public void setWarehousesByBaseStorePagedDao(final PagedGenericDao<WarehouseModel> warehousesByBaseStorePagedDao)
	{
		this.warehousesByBaseStorePagedDao = warehousesByBaseStorePagedDao;
	}

	protected PagedGenericDao<PointOfServiceModel> getPointsOfServicePagedDao()
	{
		return pointsOfServicePagedDao;
	}

	@Required
	public void setPointsOfServicePagedDao(final PagedGenericDao<PointOfServiceModel> pointsOfServicePagedDao)
	{
		this.pointsOfServicePagedDao = pointsOfServicePagedDao;
	}

	protected Converter<PointOfServiceModel, PointOfServiceData> getPointOfServiceConverter()
	{
		return pointOfServiceConverter;
	}

	@Required
	public void setPointOfServiceConverter(Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter)
	{
		this.pointOfServiceConverter = pointOfServiceConverter;
	}
}
