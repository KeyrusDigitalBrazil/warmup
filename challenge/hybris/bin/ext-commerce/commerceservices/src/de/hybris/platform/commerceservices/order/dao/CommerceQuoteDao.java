/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commerceservices.order.dao;

import de.hybris.platform.commerceservices.enums.QuoteNotificationType;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Date;
import java.util.Set;


/**
 * DAO interface for handling {@link QuoteModel}
 */
public interface CommerceQuoteDao extends GenericDao<QuoteModel>
{

	/**
	 * Returns a paged list of maximum version (i.e. active quotes) of each quote for the specified user & store,
	 * filtered by accessible quote states.
	 *
	 * @param customerModel
	 *           the customer to retrieve quotes for
	 * @param store
	 *           the store to retrieve quotes for
	 * @param pageableData
	 *           the pagination settings
	 * @param quoteStates
	 *           the quote states the user can access
	 * @return the paged search result
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null or the set of quote states is empty
	 */
	SearchPageData<QuoteModel> findQuotesByCustomerAndStore(CustomerModel customerModel, BaseStoreModel store,
			PageableData pageableData, Set<QuoteState> quoteStates);

	/**
	 * Returns a list of active (last version) quotes filtered by given statuses which will expire by the specified date
	 *
	 * @param expiredAfter
	 *           the date after which the quote is going to expire
	 * @param expiredBy
	 *           the date before which the quote is going to expire
	 * @param quoteStates
	 *           the quote states the user can access
	 * @param quoteNotificationType
	 *           the quote notification, for which we are getting quotes (quotes which have this notification set will be
	 *           excluded from the results)
	 * @return the paged search result
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null or the set of quote states is empty
	 */
	SearchResult<QuoteModel> findQuotesSoonToExpire(final Date expiredAfter, final Date expiredBy,
			final QuoteNotificationType quoteNotificationType, final Set<QuoteState> quoteStates);

	/**
	 * Returns a list of active (last version) quotes filtered by given statuses which are expired
	 *
	 * @param currentDate
	 *           the current date
	 * @param quoteStates
	 *           the quote states the user can access
	 * @param quoteNotificationType
	 *           the quote notification, for which we are getting quotes (quotes which have this notification set will be
	 *           excluded from the results)
	 * @return the paged search result
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null or the set of quote states is empty
	 */
	SearchResult<QuoteModel> findQuotesExpired(final Date currentDate, final QuoteNotificationType quoteNotificationType,
			final Set<QuoteState> quoteStates);

	/**
	 * Returns a unique quote of maximum version (i.e. active quote) for the specified user, store & code, filtered by
	 * accessible quote states.
	 *
	 * @param customerModel
	 *           the customer to retrieve quotes for
	 * @param store
	 *           the store to retrieve quotes for
	 * @param quoteCode
	 *           the quote code to search for
	 * @param quoteStates
	 *           the quote states the user can access
	 * @return the unique quote matching the search parameters
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null or the set of quote states is empty
	 * @throws ModelNotFoundException
	 *            if no results were found
	 * @throws AmbiguousIdentifierException
	 *            if more than one quote matches the search parameters
	 */
	QuoteModel findUniqueQuoteByCodeAndCustomerAndStore(CustomerModel customerModel, BaseStoreModel store, String quoteCode,
			Set<QuoteState> quoteStates);

	/**
	 * Returns the total number of quotes for the specified user and store.
	 *
	 * @param customerModel
	 *           the customer to get the quote count for
	 * @param store
	 *           the store to get the quote count for
	 * @param quoteStates
	 *           the list of states the user can access
	 * @return the quote count
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null or the set of quote states is empty
	 */
	Integer getQuotesCountForCustomerAndStore(CustomerModel customerModel, BaseStoreModel store, Set<QuoteState> quoteStates);
}
