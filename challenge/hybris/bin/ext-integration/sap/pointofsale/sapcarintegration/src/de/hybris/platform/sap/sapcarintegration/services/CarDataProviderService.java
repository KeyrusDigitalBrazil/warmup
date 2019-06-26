/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapcarintegration.services;

import java.util.Date;

import org.apache.olingo.odata2.api.ep.feed.ODataFeed;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;


/**
 * Data provider service
 *
 */
public interface CarDataProviderService
{


	/**
	 * 
	 * @param customerNumber
	 * @param paginationData
	 * @return {@link ODataFeed}
	 */
	ODataFeed readHeaderFeed(String customerNumber, PaginationData paginationData);


	/**
	 * read point of sales order header for a given transaction, use pos order key
	 * (businessDayDate,storeId,transactionIndex)
	 * 
	 * @param businessDayDate
	 * @param storeId
	 * @param transactionIndex
	 * @param customerNumber
	 * @return ODataFeed
	 */
	ODataFeed readHeaderFeed(String businessDayDate, String storeId, Integer transactionIndex, String customerNumber);


	/**
	 * read point of sales transaction items for a given transaction
	 * 
	 * @param businessDayDate
	 * @param storeId
	 * @param transactionIndex
	 * @param customerNumber
	 * @return ODataFeed
	 */
	ODataFeed readItemFeed(String businessDayDate, String storeId, Integer transactionIndex, String customerNumber);


	/**
	 * read store location information
	 * 
	 * @param location
	 * @return ODataFeed
	 */
	ODataFeed readLocaltionFeed(String location);









}
