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
package de.hybris.platform.commerceservices.order;

import de.hybris.platform.commerceservices.enums.DiscountType;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.order.exceptions.QuoteUnderThresholdException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Set;


/**
 * Service interface that provides an API for quote management.
 */
public interface CommerceQuoteService
{
	/**
	 * Creates a new quote based on given cart. Please note that it's the caller's responsibility to persist the new
	 * quote. The cart passed into this method is not affected by its business logic.
	 *
	 * @param cartModel
	 *           the cart model
	 * @param userModel
	 *           the user performing the operation on the quote
	 * @return the new quote model
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null
	 */
	QuoteModel createQuoteFromCart(CartModel cartModel, UserModel userModel);

	/**
	 * Retrieves the paged list of quotes according to the provided selection criteria.
	 *
	 * @param pageableData
	 *           paging information
	 * @param customerModel
	 *           the customer to be used for selecting the quotes
	 * @param quoteUserModel
	 *           the user asking for the list.
	 * @param store
	 *           the store to be used for selecting the quotes
	 * @return the paged search results
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null
	 */
	SearchPageData<QuoteModel> getQuoteList(CustomerModel customerModel, UserModel quoteUserModel, BaseStoreModel store,
			PageableData pageableData);

	/**
	 * Finds a quote with highest version by code, customer and store.
	 *
	 * @param customerModel
	 *           the customer to be used for selecting the quote
	 * @param quoteUserModel
	 *           the user asking for the list.
	 * @param store
	 *           the store to be used for selecting the quote
	 * @param quoteCode
	 *           the code of the quote
	 * @return the quote
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null
	 */
	QuoteModel getQuoteByCodeAndCustomerAndStore(CustomerModel customerModel, UserModel quoteUserModel, BaseStoreModel store,
			String quoteCode);

	/**
	 * Loads the given quote into a new session cart or returns the existing session cart if existing session cart
	 * already has the quote loaded.
	 *
	 * If the quote needs to be loaded into a new session cart, it first checks if the action can be performed for the
	 * quote. If the action cannot be performed, then a runtime exception is thrown. The method also updates the quote
	 * state and saves the existing session cart before replacing it with a new session cart, if needed.
	 *
	 * @param quoteModel
	 *           the quote to process
	 * @param userModel
	 *           the user performing the operation on the quote
	 * @return the new session cart loaded with the given quote or the existing session cart if existing session cart
	 *         already has the quote loaded
	 */
	CartModel loadQuoteAsSessionCart(QuoteModel quoteModel, UserModel userModel);


	/**
	 * Submits a quote from buyer to seller representative or from seller representative to seller approver. A new
	 * version of the same quote will be created on the seller representative or seller approver side.
	 *
	 * @param quoteModel
	 *           the quote to process
	 * @param userModel
	 *           the user performing the operation on the quote
	 * @return the updated quote
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null
	 */
	QuoteModel submitQuote(QuoteModel quoteModel, UserModel userModel);

	/**
	 * Validates if quote meets threshold.
	 *
	 * @param quote
	 *           the quote to validate
	 * @param user
	 *           the user performing the operation on the quote
	 * @param sessionCart
	 *           the cart in session
	 * @throws QuoteUnderThresholdException
	 *            if quote does not meet threshold
	 */
	void validateQuoteThreshold(final QuoteModel quote, final UserModel user, final CartModel sessionCart)
			throws QuoteUnderThresholdException;

	/**
	 * Get quote request threshold defined in properties. It will return the property defined threshold amount for
	 * current site and currency. If the threshold is not defined for the site and currency, it will return global
	 * threshold. If none of these are defined, it will return 0. If quote threshold calculation is not required for the
	 * quote, will return an negative number.
	 *
	 * @param quote
	 * @param user
	 * @param sessionCart
	 *
	 * @return quote request threshold
	 */
	double getQuoteRequestThreshold(final QuoteModel quote, final UserModel user, final CartModel sessionCart);

	/**
	 * Approve a quote from seller representative. Send a quote on submit event.
	 *
	 * @param quoteModel
	 *           the quote to process
	 * @param userModel
	 *           the user performing the operation on the quote
	 * @return the updated quote
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null
	 */
	QuoteModel approveQuote(QuoteModel quoteModel, UserModel userModel);

	/**
	 * Reject a quote from seller representative. Send a quote on submit event.
	 *
	 * @param quoteModel
	 *           the quote to process
	 * @param userModel
	 *           the user performing the operation on the quote
	 * @return the updated quote
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null
	 */
	QuoteModel rejectQuote(QuoteModel quoteModel, UserModel userModel);

	/**
	 * Updates quote with data from cart (and existing quote). Sets the cart as cartReference on to the new quote.
	 *
	 * @param cartModel
	 *           cart that has an association to a quote
	 * @param userModel
	 *           the user performing the operation on the quote
	 * @return the updated quote
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null
	 */
	QuoteModel updateQuoteFromCart(CartModel cartModel, UserModel userModel);

	/**
	 * Get currently allowed actions for a quote.
	 *
	 * @param quoteModel
	 *           the quote to process
	 * @param userModel
	 *           the user for which the allowed actions will be determined
	 * @return the allowed action. Empty set will be returned if none of the actions is allowed.
	 * @throws IllegalArgumentException
	 *            if any of the parameter is null
	 */
	Set<QuoteAction> getAllowedActions(QuoteModel quoteModel, UserModel userModel);

	/**
	 * Accept and prepare checkout. Similar to {@link #loadQuoteAsSessionCart(QuoteModel, UserModel)} but for checkout.
	 *
	 * @param quoteModel
	 *           the quote to process
	 * @param userModel
	 *           the user performing the operation on the quote
	 * @return the cart for checkout
	 * @throws IllegalArgumentException
	 *            if any of parameters is null
	 * @throws CommerceQuoteExpirationTimeException
	 *            if the quote has expired
	 */
	CartModel acceptAndPrepareCheckout(QuoteModel quoteModel, UserModel userModel);

	/**
	 * Applies a quote specific discount to abstract order model given the discount rate and discount type.
	 *
	 * @param abstractOrderModel
	 *           the abstract order model to add a discount to. Must either be a {@link QuoteModel} or a
	 *           {@link CartModel} associated with a quote.
	 * @param userModel
	 *           the user performing the operation on the quote
	 * @param discountRate
	 *           the discount rate to be applied
	 * @param discountType
	 *           the type of discount to be applied
	 *
	 * @throws IllegalArgumentException
	 *            In case
	 *            <ul>
	 *            <li>any of the parameters is null,</li>
	 *            <li>the given order is neither a {@link QuoteModel} nor a {@link CartModel} associated with a quote or</li>
	 *            <li>the discount rate is out of range.</li>
	 *            </ul>
	 * @throws SystemException
	 *            if an error occurs during cart calculation
	 */
	void applyQuoteDiscount(AbstractOrderModel abstractOrderModel, UserModel userModel, Double discountRate,
			DiscountType discountType);

	/**
	 * Cancels a quote. The version of the quote visible to the user will have the state set to canceled.
	 *
	 * @param quoteModel
	 *           the quote to cancel
	 * @param userModel
	 *           the user performing the operation on the quote
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null
	 */
	void cancelQuote(QuoteModel quoteModel, UserModel userModel);

	/**
	 * Assigns quote to an assignee
	 *
	 * @param quote
	 *           the quote to be assigned
	 * @param assignee
	 *           the user to which the quote should be assigned
	 * @param assigner
	 *           the user assigning the quote to the assignee
	 * @throws IllegalArgumentException
	 *            if any of parameters is null
	 * @throws CommerceQuoteAssignmentException
	 *            if the quote is already assigned to a different user
	 */
	void assignQuoteToUser(QuoteModel quote, UserModel assignee, UserModel assigner);

	/**
	 * Unassigns the assignee from the quote
	 *
	 * @param quote
	 *           the quote to be unassigned
	 * @param assigner
	 *           the user un-assigning the quote from the assignee
	 * @throws IllegalArgumentException
	 *            if any of parameters is null
	 * @throws CommerceQuoteAssignmentException
	 *            if the quote is assigned to a different user
	 */
	void unassignQuote(QuoteModel quote, UserModel assigner);

	/**
	 * Checks whether a quote can be auto approved or requires seller approval based on the configured threshold.
	 *
	 * @param quoteModel
	 *           the quote to inspect for auto approval
	 * @return true if the quote should be auto approved, false otherwise
	 */
	boolean shouldAutoApproveTheQuoteForSellerApproval(QuoteModel quoteModel);

	/**
	 * Get the number of quotes for the specified user and store.
	 *
	 * @param customerModel
	 *           the customer to be used for selecting the quotes
	 * @param quoteUserModel
	 *           the user asking for the number of quotes
	 * @param store
	 *           the store to be used for selecting the quotes
	 * @return the total number
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null
	 */
	Integer getQuotesCountForStoreAndUser(CustomerModel customerModel, UserModel quoteUserModel, BaseStoreModel store);

	/**
	 * Create a new quote snapshot for a given quote state and persist the model.
	 *
	 * @param quoteModel
	 *           the quoteModel to create a snapshot from
	 * @param quoteState
	 *           the new quoteState
	 * @return the newly created quote
	 * @throws IllegalArgumentException
	 *            if any of the parameters is null
	 */
	QuoteModel createQuoteSnapshotWithState(QuoteModel quoteModel, QuoteState quoteState);

	/**
	 * Validates if the quote cart can be checked-out or not
	 *
	 * @param cart
	 *           the cart to validate
	 * @return true if quoteCart is valid for check out, false otherwise
	 */
	boolean isQuoteCartValidForCheckout(CartModel cart);

	/**
	 * Removes the cart related to the given quote
	 *
	 * @param quote
	 *           the quote to detach cart from
	 */
	void removeQuoteCart(QuoteModel quote);

	/**
	 * Re-quote from an existing quote to create a new quote
	 *
	 * @param quoteModel
	 *           the existing quoteModel
	 * @param userModel
	 *           the current quote user
	 * @return the new created quoteModel
	 */
	QuoteModel requote(QuoteModel quoteModel, UserModel userModel);
}
