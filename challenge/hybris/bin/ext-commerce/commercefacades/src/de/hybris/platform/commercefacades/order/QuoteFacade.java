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
package de.hybris.platform.commercefacades.order;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.quote.data.DiscountTypeData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.List;
import java.util.Set;


/**
 * Facade interface providing an API for performing various quote management operations.
 */
public interface QuoteFacade
{
	/**
	 * Creates a new Quote for the current user, based on what is on the session.
	 *
	 * @return the newly created quote
	 */
	QuoteData initiateQuote();

	/**
	 * Submits an existing quote.
	 *
	 * @param quoteCode
	 *           the code of the quote to process
	 */
	void submitQuote(String quoteCode);

	/**
	 * Gets quote threshold configured for the initial request, and the quote requires threshold validation.
	 *
	 * @param quoteCode
	 * @return The (positive) threshold value; 0, if none configured; or a negative number, if quote does not need
	 *         threshold validation.
	 */
	double getQuoteRequestThreshold(final String quoteCode);

	/**
	 * Approve an existing quote.
	 *
	 * @param quoteCode
	 *           the code of the quote to process
	 */
	void approveQuote(String quoteCode);

	/**
	 * Reject an existing quote.
	 *
	 * @param quoteCode
	 *           the code of the quote to process
	 */
	void rejectQuote(String quoteCode);

	/**
	 * Prepare a quote to make checkout.
	 *
	 * @param quoteCode
	 *           the code of the quote to process
	 */
	void acceptAndPrepareCheckout(String quoteCode);

	/**
	 * Retrieves the paged list of quotes for the current user and the current base store.
	 *
	 * @param pageableData
	 *           paging information
	 * @return the paged search results
	 */
	SearchPageData<QuoteData> getPagedQuotes(PageableData pageableData);

	/**
	 * Creates a new cart data from an existing quote. Cart is linked to the quote.
	 *
	 * @param quoteCode
	 *           the code of the quote that will generate the cart
	 * @return the newly created cart
	 */
	CartData createCartFromQuote(String quoteCode);

	/**
	 * Activate quote and set it to edit mode.
	 *
	 * @param quoteCode
	 *           the code of the quote to process
	 */
	void enableQuoteEdit(String quoteCode);

	/**
	 * Sync quote and close edit mode.
	 *
	 * @return the updated QuoteData object
	 */
	QuoteData newCart();

	/**
	 * Adds a comment to the session cart.
	 *
	 * @param text
	 *           the comment to be added to the quote
	 */
	void addComment(String text);

	/**
	 * Method for adding a comment to a quote entry
	 *
	 * @param entryNumber
	 *           the entry number
	 * @param comment
	 *           the comment to be added to the order entry
	 */
	void addEntryComment(long entryNumber, String comment);

	/**
	 * Gets the set of allowed actions for the given quote.
	 *
	 * @param quoteCode
	 *           the code of the quote
	 * @return the set of actions allowed for the quote. Empty set will be returned if none of the action is allowed.
	 */
	Set<QuoteAction> getAllowedActions(String quoteCode);

	/**
	 * Applies the discount to the session cart given the discount and it's type.
	 *
	 * @param discountRate
	 *           the discount rate to be applied
	 * @param discountTypeCode
	 *           the code of the discount type to be applied
	 */
	void applyQuoteDiscount(Double discountRate, String discountTypeCode);

	/**
	 * Get discount types ready to be applied for discount.
	 *
	 * @return the list of discount types to be applied for quote. Empty list will be returned if nothing can be applied.
	 *
	 * @deprecated since 6.4 This method is not used anywhere
	 */
	@Deprecated
	List<DiscountTypeData> getDiscountTypes();

	/**
	 * Cancels a quote. The version of the quote visible to the user will have the state set to canceled.
	 *
	 * @param quoteCode
	 *           the code of the quote to cancel
	 */
	void cancelQuote(String quoteCode);

	/**
	 * Gets the number of quotes for the current user
	 *
	 * @return the number of quotes
	 */
	Integer getQuotesCountForCurrentUser();

	/**
	 * Validates if the session quote cart can be checked-out or not
	 *
	 * @return true if it's valid for checkout, false otherwise
	 */
	boolean isQuoteSessionCartValidForCheckout();

	/**
	 * Removes the cart related to the given quote
	 *
	 * @param quoteCode
	 *           code of the quote to detach cart from
	 */
	void removeQuoteCart(String quoteCode);

	/**
	 * Retrieve the latest snapshot (version) of a quote by its code, for the current user and current base store.
	 *
	 * @param quoteCode
	 *           The code of the quote
	 * @return the latest snapshot of the quote
	 */
	QuoteData getQuoteForCode(String quoteCode);

	/**
	 * Re-quote from the existing quote to create a new quote
	 *
	 * @param quoteCode
	 *           The code of the quote to be re-quoted
	 * @return the new quote
	 */
	QuoteData requote(String quoteCode);
}
