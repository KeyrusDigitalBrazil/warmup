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
package de.hybris.platform.ordermanagementfacades;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.localization.Localization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract class containing basic facade functionality
 */
public abstract class OmsBaseFacade
{
	private ModelService modelService;
	private GenericDao<OrderModel> orderGenericDao;


	//TODO move to commercefacade to a utility or abstract class (OMSE-1213) //NOSONAR
	/**
	 * Converts the result of {@link de.hybris.platform.commerceservices.search.pagedata.SearchPageData}
	 *
	 * @param source
	 * 		searchPageData containing original results
	 * @param converter
	 * 		converter for converting the searchPageData's results
	 * @param <S>
	 * 		original type of searchPageData's results
	 * @param <T>
	 * 		target type of searchPageData's results
	 * @return converted SearchPageData
	 */
	protected <S, T> SearchPageData<T> convertSearchPageData(final SearchPageData<S> source, final Converter<S, T> converter)
	{
		final SearchPageData<T> result = new SearchPageData<T>();
		result.setPagination(source.getPagination());
		result.setSorts(source.getSorts());
		result.setResults(Converters.convertAll(source.getResults(), converter));
		return result;
	}

	/**
	 * Discards snapshots of orders. i.e: orders with versionID equal to null
	 *
	 * @param orders
	 * 		a list of orders
	 * @return a filtered list of orders
	 */
	protected List<OrderModel> discardOrderSnapshot(final List<OrderModel> orders)
	{
		if (CollectionUtils.isEmpty(orders))
		{
			return new ArrayList<>();
		}
		return orders.stream().filter(order -> Objects.isNull(order.getVersionID())).collect(Collectors.toList());
	}

	/**
	 * Creates a {@link de.hybris.platform.commerceservices.search.pagedata.PaginationData} based on the received PageableData
	 *
	 * @param pageableData
	 * 		contains pageable information
	 * @param totalResults
	 * 		the total number of results returned
	 * @return pagination data object
	 */
	protected PaginationData createPaginationData(final PageableData pageableData, final int totalResults)
	{
		PaginationData paginationData = new PaginationData();
		paginationData.setPageSize(pageableData.getPageSize());
		paginationData.setSort(pageableData.getSort());
		paginationData.setTotalNumberOfResults(totalResults);

		// Calculate the number of pages
		paginationData.setNumberOfPages(
				(int) Math.ceil(((double) paginationData.getTotalNumberOfResults()) / paginationData.getPageSize()));

		// Work out the current page, fixing any invalid page values
		paginationData.setCurrentPage(Math.max(0, Math.min(paginationData.getNumberOfPages(), pageableData.getCurrentPage())));

		return paginationData;
	}

	/**
	 * Finds {@link OrderModel} for the given {@link OrderModel#CODE}
	 *
	 * @param orderCode
	 * 		the order's code
	 * @return the requested order for the given code
	 */
	protected OrderModel getOrderModelForCode(final String orderCode)
	{
		final Map<String, String> params = new HashMap<>();
		params.put(OrderModel.CODE, orderCode);

		final List<OrderModel> resultSet = discardOrderSnapshot(getOrderGenericDao().find(params));

		if (resultSet.isEmpty())
		{
			throw new ModelNotFoundException(
					String.format(Localization.getLocalizedString("ordermanagementfacade.orders.validation.missing.code"), orderCode));
		}
		if (resultSet.size() > 1)
		{
			throw new AmbiguousIdentifierException(
					String.format(Localization.getLocalizedString("ordermanagementfacade.orders.validation.multiple.code"),
							orderCode));
		}
		return resultSet.get(0);
	}


	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected GenericDao<OrderModel> getOrderGenericDao()
	{
		return orderGenericDao;
	}

	@Required
	public void setOrderGenericDao(final GenericDao<OrderModel> orderGenericDao)
	{
		this.orderGenericDao = orderGenericDao;
	}

}
