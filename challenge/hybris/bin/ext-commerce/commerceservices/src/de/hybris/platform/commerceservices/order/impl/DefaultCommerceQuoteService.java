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
package de.hybris.platform.commerceservices.order.impl;

import static de.hybris.platform.commerceservices.constants.CommerceServicesConstants.QUOTE_REQUEST_INITIATION_THRESHOLD;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.enums.DiscountType;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.event.QuoteBuyerSubmitEvent;
import de.hybris.platform.commerceservices.event.QuoteCancelEvent;
import de.hybris.platform.commerceservices.event.QuoteSalesRepSubmitEvent;
import de.hybris.platform.commerceservices.event.QuoteSellerApprovalSubmitEvent;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceQuoteExpirationTimeException;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.commerceservices.order.CommerceSaveCartService;
import de.hybris.platform.commerceservices.order.OrderQuoteDiscountValuesAccessor;
import de.hybris.platform.commerceservices.order.RequoteStrategy;
import de.hybris.platform.commerceservices.order.UpdateQuoteFromCartStrategy;
import de.hybris.platform.commerceservices.order.dao.CommerceQuoteDao;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteSubmitException;
import de.hybris.platform.commerceservices.order.exceptions.QuoteUnderThresholdException;
import de.hybris.platform.commerceservices.order.strategies.QuoteActionValidationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteAssignmentValidationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteCartValidationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteExpirationTimeValidationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteMetadataValidationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteSellerApproverAutoApprovalStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteStateSelectionStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUpdateExpirationTimeStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUpdateStateStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserTypeIdentificationStrategy;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.util.CommerceQuoteUtils;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCartService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.tx.TransactionBody;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.DiscountValue;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.math.DoubleMath;


/**
 * Default implementation for {@link CommerceQuoteService}.
 */
public class DefaultCommerceQuoteService implements CommerceQuoteService
{
	private static final Logger LOG = Logger.getLogger(DefaultCommerceQuoteService.class);

	protected static final String DATE_TIME_FORMAT = "MMM dd, yyyy h:mm a";
	protected static final String ABSTRACT_ORDER_NAME = "Cart %s %s";
	protected static final String ABSTRACT_ORDER_DESCRIPTION = "The system %s this %s automatically when you started editing a saved quote.";

	private CommerceQuoteDao commerceQuoteDao;
	private CartService cartService;
	private ModelService modelService;
	private CommerceSaveCartService commerceSaveCartService;
	private SessionService sessionService;
	private CommerceCartService commerceCartService;
	private QuoteStateSelectionStrategy quoteStateSelectionStrategy;
	private QuoteActionValidationStrategy quoteActionValidationStrategy;
	private QuoteUpdateStateStrategy quoteUpdateStateStrategy;
	private UpdateQuoteFromCartStrategy updateQuoteFromCartStrategy;
	private RequoteStrategy requoteStrategy;
	private Map<QuoteState, QuoteState> quoteSnapshotStateTransitionMap;
	private QuoteService quoteService;
	private CalculationService calculationService;
	private QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;
	private EventService eventService;
	private QuoteAssignmentValidationStrategy quoteAssignmentValidationStrategy;
	private QuoteSellerApproverAutoApprovalStrategy quoteSellerApproverAutoApprovalStrategy;
	private QuoteCartValidationStrategy quoteCartValidationStrategy;
	private QuoteExpirationTimeValidationStrategy quoteExpirationTimeValidationStrategy;
	private QuoteUpdateExpirationTimeStrategy quoteUpdateExpirationTimeStrategy;
	private QuoteMetadataValidationStrategy quoteMetadataValidationStrategy;
	private OrderQuoteDiscountValuesAccessor orderQuoteDiscountValuesAccessor;
	private UserService userService;
	private CommerceQuoteUtils commerceQuoteUtils;

	private static final double EPSILON = 0.0001d;


	@Override
	public QuoteModel createQuoteFromCart(final CartModel cartModel, final UserModel userModel)
	{
		final QuoteModel quoteModel = createQuoteFromCartInternal(cartModel, userModel);
		getModelService().save(quoteModel);
		getModelService().refresh(quoteModel);

		return quoteModel;
	}

	protected QuoteModel createQuoteFromCartInternal(final CartModel cartModel, final UserModel userModel)
	{
		validateParameterNotNullStandardMessage("CartModel", cartModel);
		validateParameterNotNullStandardMessage("UserModel", userModel);

		QuoteModel quoteModel = getQuoteService().createQuoteFromCart(cartModel);
		quoteModel = getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.CREATE, quoteModel, userModel);

		return quoteModel;
	}

	protected boolean hasQuoteInSessionCart()
	{
		return getCartService().hasSessionCart() && getCartService().getSessionCart().getQuoteReference() != null;
	}

	@Override
	public CartModel loadQuoteAsSessionCart(final QuoteModel quoteModel, final UserModel userModel)
	{
		getQuoteActionValidationStrategy().validate(QuoteAction.EDIT, quoteModel, userModel);

		// if the quote is in offer state, remove quote related cart & quote discounts
		if (getQuoteActionValidationStrategy().isValidAction(QuoteAction.CHECKOUT, quoteModel, userModel))
		{
			removeQuoteCart(quoteModel);
			quoteModel.setPreviousEstimatedTotal(quoteModel.getTotalPrice());
			getCommerceQuoteUtils().removeExistingQuoteDiscount(quoteModel);
			getModelService().save(quoteModel);
		}

		return updateAndLoadQuoteCartWithAction(quoteModel, QuoteAction.EDIT, userModel);
	}

	@Override
	public void removeQuoteCart(final QuoteModel quote)
	{
		if (quote.getCartReference() != null)
		{
			if (isSessionQuoteSameAsRequestedQuote(quote))
			{
				getSessionService().removeAttribute(DefaultCartService.SESSION_CART_PARAMETER_NAME);
			}
			getModelService().remove(quote.getCartReference());
			getModelService().refresh(quote);
		}
	}

	protected CartModel updateAndLoadQuoteCartWithAction(final QuoteModel quoteModel, final QuoteAction quoteAction,
			final UserModel userModel)
	{
		// load quote to cart
		QuoteModel updatedQuoteModel = getQuoteUpdateExpirationTimeStrategy().updateExpirationTime(quoteAction, quoteModel,
				userModel);
		updatedQuoteModel = getQuoteUpdateStateStrategy().updateQuoteState(quoteAction, updatedQuoteModel, userModel);
		getModelService().save(updatedQuoteModel);
		getModelService().refresh(updatedQuoteModel);

		return (updatedQuoteModel.getCartReference() != null) ? updatedQuoteModel.getCartReference()
				: getCartService().createCartFromQuote(updatedQuoteModel);
	}

	protected String getCurrentDateTimeFormatted(final String format)
	{
		final DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern(format);
		return LocalDateTime.now().format(ofPattern);
	}

	protected Optional<QuoteModel> getQuoteFromSessionCart()
	{
		if (hasQuoteInSessionCart())
		{
			return Optional.of(getCartService().getSessionCart().getQuoteReference());
		}
		return Optional.empty();
	}

	@Override
	public QuoteModel submitQuote(final QuoteModel quoteModel, final UserModel userModel)
	{
		validateParameterNotNullStandardMessage("quoteModel", quoteModel);
		validateParameterNotNullStandardMessage("userModel", userModel);

		getQuoteActionValidationStrategy().validate(QuoteAction.SUBMIT, quoteModel, userModel);

		QuoteModel updatedQuoteModel = isSessionQuoteSameAsRequestedQuote(quoteModel)
				? updateQuoteFromCart(getCartService().getSessionCart(), userModel) : quoteModel;

		validateQuoteTotal(updatedQuoteModel);

		getQuoteMetadataValidationStrategy().validate(QuoteAction.SUBMIT, updatedQuoteModel, userModel);

		updatedQuoteModel = getQuoteUpdateExpirationTimeStrategy().updateExpirationTime(QuoteAction.SUBMIT, updatedQuoteModel,
				userModel);
		updatedQuoteModel = getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.SUBMIT, updatedQuoteModel, userModel);
		getModelService().save(updatedQuoteModel);
		getModelService().refresh(updatedQuoteModel);

		final QuoteUserType quoteUserType = getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(userModel).get();
		if (QuoteUserType.BUYER.equals(quoteUserType))
		{
			final QuoteBuyerSubmitEvent quoteBuyerSubmitEvent = new QuoteBuyerSubmitEvent(updatedQuoteModel, userModel,
					quoteUserType);
			getEventService().publishEvent(quoteBuyerSubmitEvent);
		}
		else if (QuoteUserType.SELLER.equals(quoteUserType))
		{
			final QuoteSalesRepSubmitEvent quoteSalesRepSubmitEvent = new QuoteSalesRepSubmitEvent(updatedQuoteModel, userModel,
					quoteUserType);
			getEventService().publishEvent(quoteSalesRepSubmitEvent);
		}

		return updatedQuoteModel;
	}

	@Override
	public void validateQuoteThreshold(final QuoteModel quote, final UserModel user, final CartModel sessionCart)
			throws QuoteUnderThresholdException
	{
		validateParameterNotNullStandardMessage("quote", quote);
		validateParameterNotNullStandardMessage("user", user);
		validateParameterNotNullStandardMessage("sessionCart", sessionCart);

		// only check first version of quote and if the customer is the current quote user
		// then if quote-cart does not meet threshold, throw exception
		if (DoubleMath.fuzzyCompare(sessionCart.getSubtotal().doubleValue(), getQuoteRequestThreshold(quote, user, sessionCart),
				EPSILON) < 0)
		{
			throw new QuoteUnderThresholdException(quote.getCode(), quote.getVersion());
		}
	}

	/**
	 * Checks if quote is in a state that requires checking the request threshold.
	 *
	 * @param quote
	 * @param user
	 * @param sessionCart
	 * @return true is request threshold is required.
	 */
	protected boolean isRequestThresholdRequired(final QuoteModel quote, final UserModel user, final CartModel sessionCart)
	{
		// only check first version of quote and if the customer is the current quote user
		return (quote.getVersion().intValue() == 1) && (user.equals(sessionCart.getUser()));
	}

	@Override
	public double getQuoteRequestThreshold(final QuoteModel quote, final UserModel user, final CartModel sessionCart)
	{
		validateParameterNotNullStandardMessage("quote", quote);
		validateParameterNotNullStandardMessage("user", user);
		validateParameterNotNullStandardMessage("sessionCart", sessionCart);

		double threshold = -1;

		if (isRequestThresholdRequired(quote, user, sessionCart))
		{
			// Global quote request threshold regardless of currency
			threshold = Config.getDouble(QUOTE_REQUEST_INITIATION_THRESHOLD, 0);

			// threshold per site and currency
			final BaseSiteModel site = quote.getSite();
			final CurrencyModel currency = quote.getCurrency();
			if (site != null && StringUtils.isNotBlank(site.getUid()) && currency != null
					&& StringUtils.isNotBlank(currency.getIsocode()))
			{
				final String siteQuoteThresholdWithCurrency = QUOTE_REQUEST_INITIATION_THRESHOLD.concat(".").concat(site.getUid())
						.concat(".").concat(currency.getIsocode());
				// Quote request threshold with respect to site and currency
				threshold = Config.getDouble(siteQuoteThresholdWithCurrency, threshold);
			}
		}

		return threshold;
	}

	protected void validateQuoteTotal(final QuoteModel quoteModel)
	{
		if (DoubleMath.fuzzyCompare(quoteModel.getTotalPrice().doubleValue(), 0, EPSILON) < 0)
		{
			throw new IllegalQuoteSubmitException(quoteModel.getCode(), quoteModel.getState(), quoteModel.getVersion(), String
					.format("Can't submit quote because that the total is negative. [Quote total : %s]", quoteModel.getTotalPrice()));
		}
	}

	@Override
	public QuoteModel approveQuote(final QuoteModel quoteModel, final UserModel userModel)
	{
		validateParameterNotNullStandardMessage("quoteModel", quoteModel);
		validateParameterNotNullStandardMessage("userModel", userModel);

		getQuoteActionValidationStrategy().validate(QuoteAction.APPROVE, quoteModel, userModel);
		getQuoteMetadataValidationStrategy().validate(QuoteAction.APPROVE, quoteModel, userModel);

		final QuoteModel updatedQuoteModel = getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.APPROVE, quoteModel,
				userModel);
		getModelService().save(updatedQuoteModel);
		getModelService().refresh(updatedQuoteModel);

		final QuoteSellerApprovalSubmitEvent quoteSellerApprovalSubmitEvent = new QuoteSellerApprovalSubmitEvent(updatedQuoteModel,
				userModel, getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(userModel).get());
		getEventService().publishEvent(quoteSellerApprovalSubmitEvent);

		return updatedQuoteModel;
	}

	@Override
	public QuoteModel rejectQuote(final QuoteModel quoteModel, final UserModel userModel)
	{
		validateParameterNotNullStandardMessage("quoteModel", quoteModel);
		validateParameterNotNullStandardMessage("userModel", userModel);

		getQuoteActionValidationStrategy().validate(QuoteAction.REJECT, quoteModel, userModel);

		final QuoteModel updatedQuoteModel = getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.REJECT, quoteModel,
				userModel);
		getModelService().save(updatedQuoteModel);
		getModelService().refresh(updatedQuoteModel);

		final QuoteSellerApprovalSubmitEvent quoteSellerApprovalSubmitEvent = new QuoteSellerApprovalSubmitEvent(updatedQuoteModel,
				userModel, getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(userModel).get());
		getEventService().publishEvent(quoteSellerApprovalSubmitEvent);

		return updatedQuoteModel;
	}

	protected QuoteModel createQuoteSnapshot(final QuoteModel quoteModel)
	{
		final QuoteState currentQuoteState = quoteModel.getState();
		if (!getQuoteSnapshotStateTransitionMap().containsKey(currentQuoteState))
		{
			throw new IllegalArgumentException(String.format(
					"Unable to create Quote Snapshot for Quote [Quote Code : %s],"
							+ " because Snapshot transition state was not found for current quote state : %s ",
					quoteModel.getCode(), currentQuoteState));
		}

		return getQuoteService().createQuoteSnapshot(quoteModel, getQuoteSnapshotStateTransitionMap().get(currentQuoteState));
	}

	@Override
	public QuoteModel updateQuoteFromCart(final CartModel cartModel, final UserModel userModel)
	{
		validateParameterNotNullStandardMessage("cartModel", cartModel);
		validateParameterNotNullStandardMessage("userModel", userModel);
		validateQuoteCart(cartModel);

		final QuoteModel outdatedQuote = cartModel.getQuoteReference();
		getQuoteActionValidationStrategy().validate(QuoteAction.SAVE, outdatedQuote, userModel);

		final QuoteModel updatedQuote = updateQuoteFromCartInternal(cartModel);
		removeQuoteCart(updatedQuote);
		return updatedQuote;
	}

	@Override
	public QuoteModel requote(final QuoteModel quote, final UserModel user)
	{
		validateParameterNotNullStandardMessage("quoteModel", quote);

		getQuoteActionValidationStrategy().validate(QuoteAction.REQUOTE, quote, user);

		final QuoteModel quoteModel = getRequoteStrategy().requote(quote);
		getModelService().save(quoteModel);
		getModelService().refresh(quoteModel);

		return quoteModel;
	}

	@Override
	public SearchPageData<QuoteModel> getQuoteList(final CustomerModel customerModel, final UserModel quoteUserModel,
			final BaseStoreModel store, final PageableData pageableData)
	{
		validateParameterNotNullStandardMessage("customerModel", customerModel);
		validateParameterNotNullStandardMessage("quoteUserModel", quoteUserModel);
		validateParameterNotNullStandardMessage("store", store);
		validateParameterNotNullStandardMessage("pageableData", pageableData);
		return getCommerceQuoteDao().findQuotesByCustomerAndStore(customerModel, store, pageableData,
				getQuoteStateSelectionStrategy().getAllowedStatesForAction(QuoteAction.VIEW, quoteUserModel));
	}

	@Override
	public QuoteModel getQuoteByCodeAndCustomerAndStore(final CustomerModel customerModel, final UserModel quoteUserModel,
			final BaseStoreModel store, final String quoteCode)
	{
		validateParameterNotNullStandardMessage("customerModel", customerModel);
		validateParameterNotNullStandardMessage("quoteUserModel", quoteUserModel);
		validateParameterNotNullStandardMessage("quoteCode", quoteCode);
		validateParameterNotNullStandardMessage("store", store);

		return getCommerceQuoteDao().findUniqueQuoteByCodeAndCustomerAndStore(customerModel, store, quoteCode,
				getQuoteStateSelectionStrategy().getAllowedStatesForAction(QuoteAction.VIEW, quoteUserModel));
	}

	protected void validateListNotEmpty(final Collection paramToCheck, final String unknownIdException)
	{
		if (CollectionUtils.isEmpty(paramToCheck))
		{
			throw new UnknownIdentifierException(unknownIdException);
		}
	}

	@Override
	public Set<QuoteAction> getAllowedActions(final QuoteModel quoteModel, final UserModel userModel)
	{
		validateParameterNotNullStandardMessage("quoteModel", quoteModel);
		return getQuoteStateSelectionStrategy().getAllowedActionsForState(quoteModel.getState(), userModel);
	}

	@Override
	public CartModel acceptAndPrepareCheckout(final QuoteModel quoteModel, final UserModel userModel)
	{
		validateParameterNotNullStandardMessage("quoteModel", quoteModel);
		validateParameterNotNullStandardMessage("userModel", quoteModel);
		if (getQuoteExpirationTimeValidationStrategy().hasQuoteExpired(quoteModel))
		{
			final QuoteModel expiredQuoteModel = getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.EXPIRED, quoteModel,
					userModel);
			getModelService().save(expiredQuoteModel);
			getModelService().refresh(expiredQuoteModel);

			throw new CommerceQuoteExpirationTimeException(
					String.format("Quote has expired. Quote Code : [%s].", expiredQuoteModel.getCode()));
		}
		getQuoteActionValidationStrategy().validate(QuoteAction.CHECKOUT, quoteModel, userModel);

		if (quoteModel.getCartReference() != null
				&& !getQuoteCartValidationStrategy().validate(quoteModel, quoteModel.getCartReference()))
		{
			removeQuoteCart(quoteModel);
		}
		return updateAndLoadQuoteCartWithAction(quoteModel, QuoteAction.CHECKOUT, userModel);
	}

	@Override
	public void applyQuoteDiscount(final AbstractOrderModel abstractOrderModel, final UserModel userModel,
			final Double discountRate, final DiscountType discountType)
	{
		validateParameterNotNullStandardMessage("abstractOrderModel", abstractOrderModel);
		validateParameterNotNullStandardMessage("user", userModel);
		validateParameterNotNullStandardMessage("discountRate", discountRate);
		validateParameterNotNullStandardMessage("discountType", discountType);

		QuoteModel quoteModel = null;
		if (abstractOrderModel instanceof CartModel)
		{
			validateQuoteCart((CartModel) abstractOrderModel);
			quoteModel = ((CartModel) abstractOrderModel).getQuoteReference();
		}
		else if (abstractOrderModel instanceof QuoteModel)
		{
			quoteModel = (QuoteModel) abstractOrderModel;
		}
		else
		{
			throw new IllegalArgumentException(
					"The abstract order model is neither a quote model nor a cart model created from quote model.");
		}

		getQuoteActionValidationStrategy().validate(QuoteAction.DISCOUNT, quoteModel, userModel);
		validateDiscountRate(discountRate, discountType, abstractOrderModel);
		boolean isCalculationRequired = CollectionUtils
				.isNotEmpty(getOrderQuoteDiscountValuesAccessor().getQuoteDiscountValues(abstractOrderModel));

		final List<DiscountValue> discountList = getCommerceQuoteUtils().removeExistingQuoteDiscount(abstractOrderModel);

		try
		{
			if (discountRate.doubleValue() > EPSILON)
			{
				isCalculationRequired = true;
				final DiscountValue discountValue = createDiscountValue(discountRate, discountType,
						abstractOrderModel.getCurrency().getIsocode())
								.orElseThrow(() -> new IllegalArgumentException("Discount type cannot be created or supported"));

				discountList.add(discountValue);
				abstractOrderModel.setGlobalDiscountValues(discountList);
				getOrderQuoteDiscountValuesAccessor().setQuoteDiscountValues(abstractOrderModel,
						Collections.singletonList(discountValue)); // keep track of the quote discount
			}

			// calculate if existing ones have been removed and/or new ones have been added
			if (isCalculationRequired)
			{
				getCalculationService().calculateTotals(abstractOrderModel, true);
				getModelService().save(abstractOrderModel);
			}
		}
		catch (final CalculationException e)
		{
			LOG.error("Failed to calculate cart [" + abstractOrderModel.getCode() + "]", e);
			throw new SystemException("Could not calculate cart [" + abstractOrderModel.getCode() + "] due to : " + e.getMessage(),
					e);
		}
	}

	protected void validateDiscountRate(final Double discountRate, final DiscountType discountType,
			final AbstractOrderModel abstractOrderModel)
	{
		final double rate = discountRate.doubleValue();

		if (DoubleMath.fuzzyCompare(rate, 0, EPSILON) < 0)
		{
			throw new IllegalArgumentException("The discount rate is less then 0!");
		}
		if (DiscountType.PERCENT.equals(discountType) && (DoubleMath.fuzzyCompare(rate, 100, EPSILON) > 0))
		{
			throw new IllegalArgumentException("Discount type is percent, but the discount rate is greater than 100!");
		}
		if (DiscountType.ABSOLUTE.equals(discountType)
				&& DoubleMath.fuzzyCompare(rate, abstractOrderModel.getSubtotal().doubleValue(), EPSILON) > 0)
		{
			throw new IllegalArgumentException(String.format(
					"Discount type is absolute, but the discont rate is greater than cart total [%s]!",
					abstractOrderModel.getTotalPrice()));
		}
	}

	@Override
	public void cancelQuote(final QuoteModel quoteModel, final UserModel userModel)
	{
		QuoteModel quoteToCancel = quoteModel;
		validateParameterNotNullStandardMessage("quoteModel", quoteToCancel);
		validateParameterNotNullStandardMessage("userModel", userModel);

		getQuoteActionValidationStrategy().validate(QuoteAction.CANCEL, quoteToCancel, userModel);

		if (isSessionQuoteSameAsRequestedQuote(quoteToCancel))
		{
			final Optional<CartModel> optionalCart = Optional.ofNullable(getCartService().getSessionCart());
			if (optionalCart.isPresent())
			{
				quoteToCancel = updateQuoteFromCartInternal(optionalCart.get());
				removeQuoteCart(quoteToCancel);
			}
		}

		quoteToCancel = getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.CANCEL, quoteToCancel, userModel);
		getModelService().save(quoteToCancel);
		getModelService().refresh(quoteToCancel);

		getEventService().publishEvent(new QuoteCancelEvent(quoteToCancel, userModel,
				getQuoteUserTypeIdentificationStrategy().getCurrentQuoteUserType(userModel).get()));
	}

	@Override
	public void assignQuoteToUser(final QuoteModel quote, final UserModel assignee, final UserModel assigner)
	{
		validateParameterNotNullStandardMessage("quote", quote);
		validateParameterNotNullStandardMessage("assignee", assignee);
		validateParameterNotNullStandardMessage("assigner", assigner);

		getQuoteAssignmentValidationStrategy().validateQuoteAssignment(quote, assignee, assigner);

		final String errorMsg = String.format("An exception occured, could not assign quote code:%s to user:%s", quote.getCode(),
				assignee.getUid());
		executeQuoteAssignment(quote, assignee, errorMsg);
	}

	@Override
	public void unassignQuote(final QuoteModel quote, final UserModel assigner)
	{
		validateParameterNotNullStandardMessage("quote", quote);
		validateParameterNotNullStandardMessage("assigner", assigner);

		getQuoteAssignmentValidationStrategy().validateQuoteUnassignment(quote, assigner);

		final String errorMsg = String.format("An exception occured, could not un-assign quote code:%s", quote.getCode());
		executeQuoteAssignment(quote, null, errorMsg);
	}

	@Override
	public boolean shouldAutoApproveTheQuoteForSellerApproval(final QuoteModel quoteModel)
	{
		return getQuoteSellerApproverAutoApprovalStrategy().shouldAutoApproveQuote(quoteModel);
	}

	@Override
	public Integer getQuotesCountForStoreAndUser(final CustomerModel customerModel, final UserModel quoteUserModel,
			final BaseStoreModel store)
	{
		validateParameterNotNullStandardMessage("customerModel", customerModel);
		validateParameterNotNullStandardMessage("quoteUserModel", quoteUserModel);
		validateParameterNotNullStandardMessage("store", store);
		return getUserService().isAnonymousUser(quoteUserModel) ? Integer.valueOf(0)
				: getCommerceQuoteDao().getQuotesCountForCustomerAndStore(customerModel, store,
						getQuoteStateSelectionStrategy().getAllowedStatesForAction(QuoteAction.VIEW, quoteUserModel));
	}

	@Override
	public boolean isQuoteCartValidForCheckout(final CartModel cart)
	{
		if (cart.getQuoteReference() == null)
		{
			return false;
		}
		return getQuoteCartValidationStrategy().validate(cart, cart.getQuoteReference());
	}

	@Override
	public QuoteModel createQuoteSnapshotWithState(final QuoteModel quoteModel, final QuoteState quoteState)
	{
		final QuoteModel updatedQuote = getQuoteService().createQuoteSnapshot(quoteModel, quoteState);
		getModelService().save(updatedQuote);
		return updatedQuote;
	}

	protected QuoteModel updateQuoteFromCartInternal(final CartModel cartModel)
	{
		final QuoteModel outdatedQuote = cartModel.getQuoteReference();
		final QuoteModel updatedQuote = getUpdateQuoteFromCartStrategy().updateQuoteFromCart(cartModel);

		return saveUpdate(cartModel, outdatedQuote, updatedQuote);
	}

	protected void executeQuoteAssignment(final QuoteModel quote, final UserModel assignee, final String errorMsg)
	{
		if (Config.isHSQLDBUsed())
		{
			setAssigneeOnQuote(quote, assignee);
		}
		else
		{
			try
			{
				Transaction.current().execute(new TransactionBody()
				{
					@Override
					public Object execute() throws Exception
					{
						getModelService().lock(quote.getPk());
						setAssigneeOnQuote(quote, assignee);
						return null;
					}
				});
			}
			catch (final Exception e)
			{
				throw new IllegalStateException(errorMsg, e);
			}
		}

	}

	protected void setAssigneeOnQuote(final QuoteModel quote, final UserModel assignee)
	{
		quote.setAssignee(assignee);
		getModelService().save(quote);
	}

	protected QuoteModel saveUpdate(final CartModel cart, final QuoteModel outdatedQuote, final QuoteModel updatedQuote)
	{
		try
		{
			final Transaction tx = Transaction.current();
			tx.setTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
			return (QuoteModel) tx.execute(new TransactionBody()
			{
				@Override
				public QuoteModel execute() throws Exception
				{
					getModelService().remove(outdatedQuote);
					getModelService().saveAll(updatedQuote, cart);
					return updatedQuote;
				}
			});
		}
		catch (final Exception e)
		{
			throw new SystemException(String.format("Updating quote with code [%s] and version [%s] from cart [%s] failed.",
					outdatedQuote.getCode(), outdatedQuote.getVersion(), cart.getCode()), e);
		}
	}

	protected Optional<DiscountValue> createDiscountValue(final Double discountRate, final DiscountType discountType,
			final String currencyIsoCode)
	{
		DiscountValue discountValue = null;

		if (DiscountType.PERCENT.equals(discountType))
		{
			discountValue = DiscountValue.createRelative(CommerceServicesConstants.QUOTE_DISCOUNT_CODE, discountRate);
		}
		else if (DiscountType.ABSOLUTE.equals(discountType))
		{
			discountValue = DiscountValue.createAbsolute(CommerceServicesConstants.QUOTE_DISCOUNT_CODE, discountRate,
					currencyIsoCode);
		}
		else if (DiscountType.TARGET.equals(discountType))
		{
			discountValue = DiscountValue.createTargetPrice(CommerceServicesConstants.QUOTE_DISCOUNT_CODE, discountRate,
					currencyIsoCode);
		}

		return (discountType == null || discountValue == null) ? Optional.empty() : Optional.of(discountValue);
	}

	protected boolean isSessionQuoteSameAsRequestedQuote(final QuoteModel quoteModel)
	{
		final Optional<QuoteModel> quoteFromSessionCart = getQuoteFromSessionCart();
		return quoteFromSessionCart.isPresent() && StringUtils.equals(quoteFromSessionCart.get().getCode(), quoteModel.getCode())
				&& quoteFromSessionCart.get().getVersion().equals(quoteModel.getVersion());
	}

	protected void validateQuoteCart(final CartModel cartModel)
	{
		if (cartModel.getQuoteReference() == null)
		{
			throw new IllegalArgumentException("The cart is not associated to a quote.");
		}
	}

	protected CommerceQuoteDao getCommerceQuoteDao()
	{
		return commerceQuoteDao;
	}

	@Required
	public void setCommerceQuoteDao(final CommerceQuoteDao commerceQuoteDao)
	{
		this.commerceQuoteDao = commerceQuoteDao;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
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

	protected CommerceSaveCartService getCommerceSaveCartService()
	{
		return commerceSaveCartService;
	}

	@Required
	public void setCommerceSaveCartService(final CommerceSaveCartService commerceSaveCartService)
	{
		this.commerceSaveCartService = commerceSaveCartService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected QuoteStateSelectionStrategy getQuoteStateSelectionStrategy()
	{
		return quoteStateSelectionStrategy;
	}

	@Required
	public void setQuoteStateSelectionStrategy(final QuoteStateSelectionStrategy quoteStateSelectionStrategy)
	{
		this.quoteStateSelectionStrategy = quoteStateSelectionStrategy;
	}

	protected QuoteActionValidationStrategy getQuoteActionValidationStrategy()
	{
		return quoteActionValidationStrategy;
	}

	@Required
	public void setQuoteActionValidationStrategy(final QuoteActionValidationStrategy quoteActionValidationStrategy)
	{
		this.quoteActionValidationStrategy = quoteActionValidationStrategy;
	}

	protected QuoteUpdateStateStrategy getQuoteUpdateStateStrategy()
	{
		return quoteUpdateStateStrategy;
	}

	@Required
	public void setQuoteUpdateStateStrategy(final QuoteUpdateStateStrategy quoteUpdateStateStrategy)
	{
		this.quoteUpdateStateStrategy = quoteUpdateStateStrategy;
	}

	protected CalculationService getCalculationService()
	{
		return calculationService;
	}

	@Required
	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}

	protected QuoteService getQuoteService()
	{
		return quoteService;
	}

	@Required
	public void setQuoteService(final QuoteService quoteService)
	{
		this.quoteService = quoteService;
	}

	protected UpdateQuoteFromCartStrategy getUpdateQuoteFromCartStrategy()
	{
		return updateQuoteFromCartStrategy;
	}

	@Required
	public void setUpdateQuoteFromCartStrategy(final UpdateQuoteFromCartStrategy updateQuoteFromCartStrategy)
	{
		this.updateQuoteFromCartStrategy = updateQuoteFromCartStrategy;
	}

	protected QuoteAssignmentValidationStrategy getQuoteAssignmentValidationStrategy()
	{
		return quoteAssignmentValidationStrategy;
	}

	@Required
	public void setQuoteAssignmentValidationStrategy(final QuoteAssignmentValidationStrategy quoteAssignmentValidationStrategy)
	{
		this.quoteAssignmentValidationStrategy = quoteAssignmentValidationStrategy;
	}

	protected Map<QuoteState, QuoteState> getQuoteSnapshotStateTransitionMap()
	{
		return quoteSnapshotStateTransitionMap;
	}

	@Required
	public void setQuoteSnapshotStateTransitionMap(final Map<QuoteState, QuoteState> quoteSnapshotStateTransitionMap)
	{
		this.quoteSnapshotStateTransitionMap = quoteSnapshotStateTransitionMap;
	}

	@Required
	protected QuoteUserTypeIdentificationStrategy getQuoteUserTypeIdentificationStrategy()
	{
		return quoteUserTypeIdentificationStrategy;
	}

	public void setQuoteUserTypeIdentificationStrategy(
			final QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy)
	{
		this.quoteUserTypeIdentificationStrategy = quoteUserTypeIdentificationStrategy;
	}

	protected EventService getEventService()
	{
		return eventService;
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	protected QuoteSellerApproverAutoApprovalStrategy getQuoteSellerApproverAutoApprovalStrategy()
	{
		return quoteSellerApproverAutoApprovalStrategy;
	}

	@Required
	public void setQuoteSellerApproverAutoApprovalStrategy(
			final QuoteSellerApproverAutoApprovalStrategy quoteSellerApproverAutoApprovalStrategy)
	{
		this.quoteSellerApproverAutoApprovalStrategy = quoteSellerApproverAutoApprovalStrategy;
	}

	protected QuoteCartValidationStrategy getQuoteCartValidationStrategy()
	{
		return quoteCartValidationStrategy;
	}

	@Required
	public void setQuoteCartValidationStrategy(final QuoteCartValidationStrategy quoteCartValidationStrategy)
	{
		this.quoteCartValidationStrategy = quoteCartValidationStrategy;
	}

	protected OrderQuoteDiscountValuesAccessor getOrderQuoteDiscountValuesAccessor()
	{
		return orderQuoteDiscountValuesAccessor;
	}

	@Required
	public void setOrderQuoteDiscountValuesAccessor(final OrderQuoteDiscountValuesAccessor orderQuoteDiscountValuesAccessor)
	{
		this.orderQuoteDiscountValuesAccessor = orderQuoteDiscountValuesAccessor;
	}

	protected QuoteUpdateExpirationTimeStrategy getQuoteUpdateExpirationTimeStrategy()
	{
		return quoteUpdateExpirationTimeStrategy;
	}

	@Required
	public void setQuoteUpdateExpirationTimeStrategy(final QuoteUpdateExpirationTimeStrategy quoteUpdateExpirationTimeStrategy)
	{
		this.quoteUpdateExpirationTimeStrategy = quoteUpdateExpirationTimeStrategy;
	}

	protected QuoteMetadataValidationStrategy getQuoteMetadataValidationStrategy()
	{
		return quoteMetadataValidationStrategy;
	}

	@Required
	public void setQuoteMetadataValidationStrategy(final QuoteMetadataValidationStrategy quoteMetadataValidationStrategy)
	{
		this.quoteMetadataValidationStrategy = quoteMetadataValidationStrategy;
	}

	protected QuoteExpirationTimeValidationStrategy getQuoteExpirationTimeValidationStrategy()
	{
		return quoteExpirationTimeValidationStrategy;
	}

	@Required
	public void setQuoteExpirationTimeValidationStrategy(
			final QuoteExpirationTimeValidationStrategy quoteExpirationTimeValidationStrategy)
	{
		this.quoteExpirationTimeValidationStrategy = quoteExpirationTimeValidationStrategy;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CommerceQuoteUtils getCommerceQuoteUtils()
	{
		return commerceQuoteUtils;
	}

	@Required
	public void setCommerceQuoteUtils(final CommerceQuoteUtils commerceQuoteUtils)
	{
		this.commerceQuoteUtils = commerceQuoteUtils;
	}

	protected RequoteStrategy getRequoteStrategy()
	{
		return requoteStrategy;
	}

	@Required
	public void setRequoteStrategy(final RequoteStrategy requoteStrategy)
	{
		this.requoteStrategy = requoteStrategy;
	}
}
