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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.DiscountType;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.event.QuoteSellerApprovalSubmitEvent;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceQuoteExpirationTimeException;
import de.hybris.platform.commerceservices.order.CommerceSaveCartService;
import de.hybris.platform.commerceservices.order.OrderQuoteDiscountValuesAccessor;
import de.hybris.platform.commerceservices.order.RequoteStrategy;
import de.hybris.platform.commerceservices.order.UpdateQuoteFromCartStrategy;
import de.hybris.platform.commerceservices.order.dao.CommerceQuoteDao;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteStateException;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteSubmitException;
import de.hybris.platform.commerceservices.order.exceptions.QuoteUnderThresholdException;
import de.hybris.platform.commerceservices.order.strategies.QuoteActionValidationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteCartValidationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteExpirationTimeValidationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteMetadataValidationStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteStateSelectionStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUpdateExpirationTimeStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUpdateStateStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserTypeIdentificationStrategy;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
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
import de.hybris.platform.order.impl.DefaultCartService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.DiscountValue;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCommerceQuoteServiceTest
{
	@InjectMocks
	DefaultCommerceQuoteService defaultCommerceQuoteService = new DefaultCommerceQuoteService();

	@Mock
	private CommerceQuoteDao commerceQuoteDao;
	@Mock
	private CartService cartService;
	@Mock
	private ModelService modelService;
	@Mock
	private CommerceSaveCartService commerceSaveCartService;
	@Mock
	private SessionService sessionService;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private QuoteStateSelectionStrategy quoteStateSelectionStrategy;
	@Mock
	private QuoteActionValidationStrategy quoteActionValidationStrategy;
	@Mock
	private UpdateQuoteFromCartStrategy updateQuoteFromCartStrategy;
	@Mock
	private QuoteUpdateStateStrategy quoteUpdateStateStrategy;
	@Mock
	private RequoteStrategy requoteStrategy;
	@Mock
	private QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;
	@Mock
	private QuoteService quoteService;
	@Mock
	private CalculationService calculationService;
	@Mock
	private Map<QuoteState, QuoteState> quoteSnapshotStateTransitionMap;
	@Mock
	private UserModel userModel;
	@Mock
	private EventService eventService;
	@Mock
	private QuoteUpdateExpirationTimeStrategy quoteUpdateExpirationTimeStrategy;
	@Mock
	private QuoteMetadataValidationStrategy quoteMetadataValidationStrategy;
	@Mock
	private QuoteCartValidationStrategy quoteCartValidationStrategy;
	@Mock
	private OrderQuoteDiscountValuesAccessor orderQuoteDiscountValuesAccessor;
	@Mock
	private UserService userService;
	@Mock
	private CommerceQuoteUtils commerceQuoteUtils;
	@Mock
	private QuoteExpirationTimeValidationStrategy quoteExpirationTimeValidationStrategy;

	@Test
	public void shouldCreateQuoteFromCart()
	{
		final CartModel cartModel = mock(CartModel.class);
		final QuoteModel quote = defaultCommerceQuoteService.createQuoteFromCart(cartModel, userModel);
		verify(quoteService).createQuoteFromCart(cartModel);
		verify(quoteUpdateStateStrategy).updateQuoteState(QuoteAction.CREATE, quote, userModel);
		verifySaveQuote(quote);
	}

	@Test
	public void shouldRequote()
	{
		final QuoteModel quoteModel = mock(QuoteModel.class);
		final QuoteModel newQuote = defaultCommerceQuoteService.requote(quoteModel, userModel);

		verify(requoteStrategy).requote(quoteModel);
		verify(modelService).save(newQuote);
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldRequoteThrowExceptionWhenActionNotAllowed()
	{
		doThrow(new IllegalQuoteStateException(null, null, null, null, "message"))
				.when(defaultCommerceQuoteService.getQuoteActionValidationStrategy()).validate(any(), any(), any());

		defaultCommerceQuoteService.requote(mock(QuoteModel.class), userModel);
	}

	protected void verifySaveQuote(final QuoteModel quoteModel)
	{
		verify(modelService).save(quoteModel);
		verify(modelService).refresh(quoteModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCreateQuoteFromCartIfCartIsNull()
	{
		defaultCommerceQuoteService.createQuoteFromCart(null, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCreateQuoteFromCartIfUserIsNull()
	{
		defaultCommerceQuoteService.createQuoteFromCart(mock(CartModel.class), null);
	}

	@Test
	public void shouldApplyQuoteDiscount()
	{
		final CartModel cartModel = mock(CartModel.class);
		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(cartModel.getQuoteReference()).willReturn(quoteModel);
		given(cartModel.getSubtotal()).willReturn(Double.valueOf(10));

		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		given(currencyModel.getIsocode()).willReturn("USD");
		given(cartModel.getCurrency()).willReturn(currencyModel);
		defaultCommerceQuoteService.applyQuoteDiscount(cartModel, userModel, Double.valueOf(5), DiscountType.ABSOLUTE);

		verify(orderQuoteDiscountValuesAccessor).getQuoteDiscountValues(cartModel);
		verify(commerceQuoteUtils).removeExistingQuoteDiscount(cartModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotApplyQuoteDiscountIfAbstractOrderIsNull()
	{
		defaultCommerceQuoteService.applyQuoteDiscount(null, userModel, Double.valueOf(5), DiscountType.ABSOLUTE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotApplyQuoteDiscountIfUserIsNull()
	{
		defaultCommerceQuoteService.applyQuoteDiscount(mock(AbstractOrderModel.class), null, Double.valueOf(5),
				DiscountType.ABSOLUTE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotApplyQuoteDiscountIfDiscountRateIsNull()
	{
		defaultCommerceQuoteService.applyQuoteDiscount(mock(AbstractOrderModel.class), userModel, null, DiscountType.ABSOLUTE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotApplyQuoteDiscountIfDiscountTypeIsNull()
	{
		defaultCommerceQuoteService.applyQuoteDiscount(mock(AbstractOrderModel.class), userModel, Double.valueOf(5), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotApplyQuoteDiscountIfNotQuoteModelNeitherCartNotCloneFromQuote()
	{
		defaultCommerceQuoteService.applyQuoteDiscount(mock(AbstractOrderModel.class), userModel, Double.valueOf(5),
				DiscountType.ABSOLUTE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotApplyQuoteDiscountIfCartNotCloneFromQuote()
	{
		defaultCommerceQuoteService.applyQuoteDiscount(mock(CartModel.class), userModel, Double.valueOf(5), DiscountType.ABSOLUTE);
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldNotApplyQuoteDiscountIfQuoteActionValidationFail()
	{
		final CartModel cartModel = mock(CartModel.class);
		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(cartModel.getQuoteReference()).willReturn(quoteModel);
		doThrow(new IllegalQuoteStateException(null, null, null, null, "message")).when(quoteActionValidationStrategy)
				.validate(QuoteAction.DISCOUNT, quoteModel, userModel);
		defaultCommerceQuoteService.applyQuoteDiscount(cartModel, userModel, Double.valueOf(5), DiscountType.ABSOLUTE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotApplyQuoteDiscountIfPercentageTooBig()
	{
		final CartModel cartModel = mock(CartModel.class);
		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(cartModel.getQuoteReference()).willReturn(quoteModel);

		defaultCommerceQuoteService.applyQuoteDiscount(cartModel, userModel, Double.valueOf(101), DiscountType.PERCENT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotApplyQuoteDiscountIfPercentageTooSmall()
	{
		final CartModel cartModel = mock(CartModel.class);
		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(cartModel.getQuoteReference()).willReturn(quoteModel);

		defaultCommerceQuoteService.applyQuoteDiscount(cartModel, userModel, Double.valueOf(-1), DiscountType.PERCENT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotApplyQuoteDiscountIfTargetTooSmall()
	{
		final CartModel cartModel = mock(CartModel.class);
		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(cartModel.getQuoteReference()).willReturn(quoteModel);

		defaultCommerceQuoteService.applyQuoteDiscount(cartModel, userModel, Double.valueOf(-1), DiscountType.TARGET);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotApplyQuoteDiscountIfAbsoluteTooBig()
	{
		final CartModel cartModel = mock(CartModel.class);
		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(cartModel.getQuoteReference()).willReturn(quoteModel);
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(10));

		defaultCommerceQuoteService.applyQuoteDiscount(cartModel, userModel, Double.valueOf(11), DiscountType.ABSOLUTE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotApplyQuoteDiscountIfAbsoluteTooSmall()
	{
		final CartModel cartModel = mock(CartModel.class);
		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(cartModel.getQuoteReference()).willReturn(quoteModel);
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(10));

		defaultCommerceQuoteService.applyQuoteDiscount(cartModel, userModel, Double.valueOf(-1), DiscountType.ABSOLUTE);
	}

	@Test
	public void shouldCreateDiscountValue()
	{
		final DiscountValue discountValue1 = defaultCommerceQuoteService
				.createDiscountValue(Double.valueOf(99), DiscountType.ABSOLUTE, "USD").get();
		assertEquals(Double.valueOf(99), Double.valueOf(discountValue1.getValue()));
		final DiscountValue discountValue2 = defaultCommerceQuoteService
				.createDiscountValue(Double.valueOf(10), DiscountType.PERCENT, "USD").get();
		assertEquals(Double.valueOf(10), Double.valueOf(discountValue2.getValue()));
	}

	@Test
	public void shouldUpdateQuoteFromCart() throws Exception
	{
		final CartModel cartModel = mock(CartModel.class);
		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(cartModel.getQuoteReference()).willReturn(quoteModel);
		doNothing().when(quoteActionValidationStrategy).validate(QuoteAction.SAVE, quoteModel, userModel);
		given(updateQuoteFromCartStrategy.updateQuoteFromCart(cartModel)).willReturn(quoteModel);

		//call updateQuoteFromCart
		final QuoteModel updatedQuote = defaultCommerceQuoteService.updateQuoteFromCart(cartModel, userModel);

		// verify
		assertNotNull("Quote is null", updatedQuote);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateQuoteFromCartIfCartIsNull()
	{
		defaultCommerceQuoteService.updateQuoteFromCart(null, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateQuoteFromCartIfCartHasNoQuoteReferece()
	{
		final CartModel cartModel = mock(CartModel.class);
		given(cartModel.getQuoteReference()).willReturn(null);

		defaultCommerceQuoteService.updateQuoteFromCart(cartModel, userModel);
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldNotUpdateQuoteFromCartIfQuoteSaveActionIsNotAllowed()
	{
		final CartModel cartModel = mock(CartModel.class);
		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(cartModel.getQuoteReference()).willReturn(quoteModel);
		doThrow(new IllegalQuoteStateException(null, null, null, null, "message")).when(quoteActionValidationStrategy)
				.validate(any(), any(), any());

		defaultCommerceQuoteService.updateQuoteFromCart(cartModel, userModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldGetQuoteListValidateNullArgumentCustomerModel()
	{
		defaultCommerceQuoteService.getQuoteList(null, mock(UserModel.class), mock(BaseStoreModel.class), mock(PageableData.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldGetQuoteListValidateNullArgumentBaseStoreModel()
	{
		defaultCommerceQuoteService.getQuoteList(mock(CustomerModel.class), mock(UserModel.class), null, mock(PageableData.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldGetQuoteListValidateNullArgumentPageableData()
	{
		defaultCommerceQuoteService.getQuoteList(mock(CustomerModel.class), mock(UserModel.class), mock(BaseStoreModel.class),
				null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldGetQuoteListValidateNullArgumentQuoteUser()
	{
		defaultCommerceQuoteService.getQuoteList(mock(CustomerModel.class), null, mock(BaseStoreModel.class),
				mock(PageableData.class));
	}

	@Test
	public void shouldPerformGetQuoteListFromValidArguments()
	{
		defaultCommerceQuoteService.getQuoteList(mock(CustomerModel.class), mock(UserModel.class), mock(BaseStoreModel.class),
				mock(PageableData.class));

		verify(defaultCommerceQuoteService.getCommerceQuoteDao()).findQuotesByCustomerAndStore(any(), any(), any(), any());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldQuoteByCodeAndCustomerAndStoreValidateNullArgumentCustomerModel()
	{
		defaultCommerceQuoteService.getQuoteByCodeAndCustomerAndStore(null, mock(UserModel.class), mock(BaseStoreModel.class), "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldQuoteByCodeAndCustomerAndStoreValidateNullArgumentBaseStoreModel()
	{
		defaultCommerceQuoteService.getQuoteByCodeAndCustomerAndStore(mock(CustomerModel.class), mock(UserModel.class), null, "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldQuoteByCodeAndCustomerAndStoreValidateNullArgumentQuoteCode()
	{
		defaultCommerceQuoteService.getQuoteByCodeAndCustomerAndStore(mock(CustomerModel.class), mock(UserModel.class),
				mock(BaseStoreModel.class), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldQuoteByCodeAndCustomerAndStoreValidateNullArgumentQuoteUser()
	{
		defaultCommerceQuoteService.getQuoteByCodeAndCustomerAndStore(mock(CustomerModel.class), null, mock(BaseStoreModel.class),
				"");
	}

	@Test
	public void shouldPerformGetQuoteByCodeAndCustomerAndStoreFromValidArguments()
	{
		defaultCommerceQuoteService.getQuoteByCodeAndCustomerAndStore(mock(CustomerModel.class), mock(UserModel.class),
				mock(BaseStoreModel.class), "");

		verify(defaultCommerceQuoteService.getCommerceQuoteDao()).findUniqueQuoteByCodeAndCustomerAndStore(any(), any(), any(),
				any());
	}

	@Test
	public void shouldHaveQuoteInSessionCart()
	{
		final CartModel mockedSessionCart = mock(CartModel.class);
		final QuoteModel mockedQuoteModel = mock(QuoteModel.class);
		given(mockedSessionCart.getQuoteReference()).willReturn(mockedQuoteModel);
		given(Boolean.valueOf(defaultCommerceQuoteService.getCartService().hasSessionCart())).willReturn(Boolean.TRUE);
		given(defaultCommerceQuoteService.getCartService().getSessionCart()).willReturn(mockedSessionCart);

		Assert.assertTrue("There should be a quote in the session cart.", defaultCommerceQuoteService.hasQuoteInSessionCart());
	}

	@Test
	public void shouldNotHaveQuoteInSessionCartWhenQuoteReferenceIsNull()
	{
		final CartModel mockedSessionCart = mock(CartModel.class);
		given(mockedSessionCart.getQuoteReference()).willReturn(null);
		given(Boolean.valueOf(defaultCommerceQuoteService.getCartService().hasSessionCart())).willReturn(Boolean.TRUE);
		given(defaultCommerceQuoteService.getCartService().getSessionCart()).willReturn(mockedSessionCart);

		Assert.assertFalse("There should not be a quote in session cart when quoteReference is null.",
				defaultCommerceQuoteService.hasQuoteInSessionCart());
	}

	@Test
	public void shouldNotHaveQuoteInSessionCartWhenThereIsNoSessionCart()
	{
		given(Boolean.valueOf(defaultCommerceQuoteService.getCartService().hasSessionCart())).willReturn(Boolean.FALSE);

		Assert.assertFalse("There should not be a quote in session cart when there is no session cart.",
				defaultCommerceQuoteService.hasQuoteInSessionCart());
	}

	@Test
	public void shouldGetQuoteFromSessionCart()
	{
		final CartModel mockedSessionCart = mock(CartModel.class);
		final QuoteModel mockedQuoteModel = mock(QuoteModel.class);
		given(mockedSessionCart.getQuoteReference()).willReturn(mockedQuoteModel);
		given(Boolean.valueOf(defaultCommerceQuoteService.getCartService().hasSessionCart())).willReturn(Boolean.TRUE);
		given(defaultCommerceQuoteService.getCartService().getSessionCart()).willReturn(mockedSessionCart);

		final Optional<QuoteModel> quoteModelOptional = defaultCommerceQuoteService.getQuoteFromSessionCart();

		Assert.assertTrue("A quote should be retrieved from the session cart.", quoteModelOptional.isPresent());
	}

	@Test
	public void shouldLoadQuoteAsNewSessionCart()
	{
		final CartModel sessionCart = mock(CartModel.class);

		final QuoteModel clonedQuoteModel = mock(QuoteModel.class);
		given(clonedQuoteModel.getCode()).willReturn("300");
		given(clonedQuoteModel.getVersion()).willReturn(Integer.valueOf(1));

		final CartModel cartCreatedFromQuote = mock(CartModel.class);

		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(quoteModel.getCode()).willReturn("400");
		given(quoteModel.getVersion()).willReturn(Integer.valueOf(1));

		given(sessionCart.getQuoteReference()).willReturn(clonedQuoteModel);
		given(Boolean.valueOf(defaultCommerceQuoteService.getCartService().hasSessionCart())).willReturn(Boolean.TRUE);
		given(defaultCommerceQuoteService.getCartService().getSessionCart()).willReturn(sessionCart);
		doNothing().when(defaultCommerceQuoteService.getQuoteActionValidationStrategy()).validate(any(), eq(quoteModel),
				eq(userModel));
		doNothing().when(defaultCommerceQuoteService.getQuoteMetadataValidationStrategy()).validate(any(), any(), any());
		given(defaultCommerceQuoteService.getCartService().createCartFromQuote(quoteModel)).willReturn(cartCreatedFromQuote);
		given((defaultCommerceQuoteService.getQuoteUpdateExpirationTimeStrategy()).updateExpirationTime(any(), eq(quoteModel),
				any())).willReturn(quoteModel);
		given((defaultCommerceQuoteService.getQuoteUpdateStateStrategy()).updateQuoteState(any(), eq(quoteModel), any()))
				.willReturn(quoteModel);

		final CartModel cartToCheck = defaultCommerceQuoteService.loadQuoteAsSessionCart(quoteModel, userModel);

		Assert.assertNotNull("A cart should be returned by a valid call to loadQuoteAsSessionCart.", cartToCheck);
		Assert.assertEquals("The cart created from quote should match the returned cart.", cartCreatedFromQuote, cartToCheck);
		verify(quoteUpdateStateStrategy).updateQuoteState(QuoteAction.EDIT, quoteModel, userModel);
		verifySaveQuote(quoteModel);
	}

	@Test
	public void shouldAcceptAndPrepareCheckoutWithNewQuoteCart()
	{
		final CartModel sessionCart = mock(CartModel.class);
		final CartModel cartCreatedFromQuote = mock(CartModel.class);
		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(quoteModel.getCode()).willReturn("400");
		given(quoteModel.getVersion()).willReturn(Integer.valueOf(1));
		given(quoteModel.getCartReference()).willReturn(null);
		given(sessionCart.getQuoteReference()).willReturn(quoteModel);
		given(Boolean.valueOf(defaultCommerceQuoteService.getCartService().hasSessionCart())).willReturn(Boolean.TRUE);
		given(defaultCommerceQuoteService.getCartService().getSessionCart()).willReturn(sessionCart);
		doNothing().when(defaultCommerceQuoteService.getQuoteActionValidationStrategy()).validate(any(), eq(quoteModel),
				eq(userModel));
		given(defaultCommerceQuoteService.getCartService().createCartFromQuote(quoteModel)).willReturn(cartCreatedFromQuote);
		given((defaultCommerceQuoteService.getQuoteUpdateExpirationTimeStrategy()).updateExpirationTime(any(), eq(quoteModel),
				any())).willReturn(quoteModel);
		given((defaultCommerceQuoteService.getQuoteUpdateStateStrategy()).updateQuoteState(any(), eq(quoteModel), any()))
				.willReturn(quoteModel);

		given(Boolean.valueOf(quoteExpirationTimeValidationStrategy.hasQuoteExpired(quoteModel))).willReturn(Boolean.FALSE);

		defaultCommerceQuoteService.acceptAndPrepareCheckout(quoteModel, userModel);
		verify(quoteUpdateStateStrategy).updateQuoteState(QuoteAction.CHECKOUT, quoteModel, userModel);
		verify(cartService).createCartFromQuote(quoteModel);
		verifySaveQuote(quoteModel);
		verify(quoteUpdateStateStrategy, never()).updateQuoteState(QuoteAction.EXPIRED, quoteModel, userModel);
	}


	@Test
	public void shouldCheckPreviousEstimatedTotalForOfferReject()
	{
		final CartModel sessionCart = mock(CartModel.class);
		final CartModel cartCreatedFromQuote = mock(CartModel.class);
		final QuoteModel quoteModel = mock(QuoteModel.class);

		given(quoteModel.getCode()).willReturn("700");
		given(quoteModel.getVersion()).willReturn(Integer.valueOf(1));
		given(quoteModel.getCartReference()).willReturn(null);
		given(quoteModel.getTotalPrice()).willReturn(Double.valueOf(25000.89d));
		given(sessionCart.getQuoteReference()).willReturn(quoteModel);

		given(Boolean.valueOf(defaultCommerceQuoteService.getCartService().hasSessionCart())).willReturn(Boolean.TRUE);
		given(defaultCommerceQuoteService.getCartService().getSessionCart()).willReturn(sessionCart);
		doNothing().when(defaultCommerceQuoteService.getQuoteActionValidationStrategy()).validate(any(), eq(quoteModel),
				eq(userModel));
		given(defaultCommerceQuoteService.getCartService().createCartFromQuote(quoteModel)).willReturn(cartCreatedFromQuote);
		given((defaultCommerceQuoteService.getQuoteUpdateExpirationTimeStrategy()).updateExpirationTime(any(), eq(quoteModel),
				any())).willReturn(quoteModel);
		given((defaultCommerceQuoteService.getQuoteUpdateStateStrategy()).updateQuoteState(any(), eq(quoteModel), any()))
				.willReturn(quoteModel);
		given(Boolean.valueOf(quoteActionValidationStrategy.isValidAction(QuoteAction.CHECKOUT, quoteModel, userModel)))
				.willReturn(Boolean.TRUE);
		given(Boolean.valueOf(quoteExpirationTimeValidationStrategy.hasQuoteExpired(quoteModel))).willReturn(Boolean.FALSE);

		defaultCommerceQuoteService.loadQuoteAsSessionCart(quoteModel, userModel);
		verify(quoteModel).setPreviousEstimatedTotal(Double.valueOf(25000.89d));
		verify(quoteUpdateStateStrategy).updateQuoteState(QuoteAction.EDIT, quoteModel, userModel);
		verify(cartService).createCartFromQuote(quoteModel);
		verify(quoteUpdateStateStrategy, never()).updateQuoteState(QuoteAction.EXPIRED, quoteModel, userModel);
	}

	@Test(expected = CommerceQuoteExpirationTimeException.class)
	public void shouldNotAcceptAndCheckoutWithInvalidQuoteExpirationTime()
	{
		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(Boolean.valueOf(quoteExpirationTimeValidationStrategy.hasQuoteExpired(quoteModel))).willReturn(Boolean.TRUE);
		given(defaultCommerceQuoteService.getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.EXPIRED, quoteModel,
				userModel)).willReturn(quoteModel);
		defaultCommerceQuoteService.acceptAndPrepareCheckout(quoteModel, userModel);
	}

	@Test
	public void shouldSaveExpiredQuote()
	{
		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(Boolean.valueOf(defaultCommerceQuoteService.getQuoteExpirationTimeValidationStrategy().hasQuoteExpired(quoteModel)))
				.willReturn(Boolean.TRUE);
		given(defaultCommerceQuoteService.getQuoteUpdateStateStrategy().updateQuoteState(QuoteAction.EXPIRED, quoteModel,
				userModel)).willReturn(quoteModel);

		try
		{
			defaultCommerceQuoteService.acceptAndPrepareCheckout(quoteModel, userModel);
			Assert.fail("Should not be able to accept and checkout an expired quote.");
		}
		catch (final CommerceQuoteExpirationTimeException e)
		{
			//empty
		}

		verifySaveQuote(quoteModel);
	}

	@Test
	public void shouldAcceptAndPrepareCheckoutWithValidQuoteCart()
	{
		final CartModel sessionCart = mock(CartModel.class);

		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(quoteModel.getCode()).willReturn("400");
		given(quoteModel.getVersion()).willReturn(Integer.valueOf(1));
		given(quoteModel.getCartReference()).willReturn(sessionCart);
		given(sessionCart.getQuoteReference()).willReturn(quoteModel);
		given(Boolean.valueOf(defaultCommerceQuoteService.getCartService().hasSessionCart())).willReturn(Boolean.TRUE);
		given(defaultCommerceQuoteService.getCartService().getSessionCart()).willReturn(sessionCart);
		doNothing().when(defaultCommerceQuoteService.getQuoteActionValidationStrategy()).validate(any(), eq(quoteModel),
				eq(userModel));
		given((defaultCommerceQuoteService.getQuoteUpdateExpirationTimeStrategy()).updateExpirationTime(any(), eq(quoteModel),
				any())).willReturn(quoteModel);
		given((defaultCommerceQuoteService.getQuoteUpdateStateStrategy()).updateQuoteState(any(), eq(quoteModel), any()))
				.willReturn(quoteModel);
		given(Boolean.valueOf(defaultCommerceQuoteService.getQuoteCartValidationStrategy().validate(quoteModel, sessionCart)))
				.willReturn(Boolean.TRUE);

		given(Boolean.valueOf(quoteExpirationTimeValidationStrategy.hasQuoteExpired(quoteModel))).willReturn(Boolean.FALSE);

		defaultCommerceQuoteService.acceptAndPrepareCheckout(quoteModel, userModel);
		verify(quoteUpdateStateStrategy).updateQuoteState(QuoteAction.CHECKOUT, quoteModel, userModel);
		verify(cartService, never()).createCartFromQuote(quoteModel);
		verifySaveQuote(quoteModel);
		verify(quoteUpdateStateStrategy, never()).updateQuoteState(QuoteAction.EXPIRED, quoteModel, userModel);
	}

	@Test
	public void shouldLoadQuoteAsSessionCartReturnExistingQuoteCart()
	{
		final CartModel sessionCart = mock(CartModel.class);

		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(quoteModel.getCode()).willReturn("300");
		given(quoteModel.getVersion()).willReturn(Integer.valueOf(1));

		given(sessionCart.getQuoteReference()).willReturn(quoteModel);
		given(quoteModel.getCartReference()).willReturn(sessionCart);
		given(Boolean.valueOf(defaultCommerceQuoteService.getCartService().hasSessionCart())).willReturn(Boolean.FALSE);
		doNothing().when(defaultCommerceQuoteService.getQuoteActionValidationStrategy()).validate(any(), eq(quoteModel), any());

		given(Boolean
				.valueOf(defaultCommerceQuoteService.getQuoteActionValidationStrategy().isValidAction(any(), eq(quoteModel), any())))
						.willReturn(Boolean.FALSE);
		given((defaultCommerceQuoteService.getQuoteUpdateExpirationTimeStrategy()).updateExpirationTime(any(), eq(quoteModel),
				any())).willReturn(quoteModel);
		given((defaultCommerceQuoteService.getQuoteUpdateStateStrategy()).updateQuoteState(any(), eq(quoteModel), any()))
				.willReturn(quoteModel);

		final CartModel cartReturned = defaultCommerceQuoteService.loadQuoteAsSessionCart(quoteModel, userModel);
		Assert.assertNotNull("A cart should be returned by a valid call to loadQuoteAsSessionCart.", cartReturned);
		Assert.assertEquals("Session cart should match returned cart.", sessionCart, cartReturned);
		verifySaveQuote(quoteModel);
		verify(defaultCommerceQuoteService.getCartService(), never()).createCartFromQuote(quoteModel);
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldThrowExceptionWhenActionNotAllowedToLoadQuoteAsSessionCart()
	{
		final QuoteModel quoteModel = mock(QuoteModel.class);
		doThrow(new IllegalQuoteStateException(null, null, null, null, "message"))
				.when(defaultCommerceQuoteService.getQuoteActionValidationStrategy()).validate(any(), eq(quoteModel), any());

		defaultCommerceQuoteService.loadQuoteAsSessionCart(quoteModel, userModel);
	}

	@Test
	public void testShouldSubmitQuoteChangeQuoteState()
	{
		doNothing().when(defaultCommerceQuoteService.getQuoteActionValidationStrategy()).validate(any(), any(), any());
		doNothing().when(defaultCommerceQuoteService.getQuoteMetadataValidationStrategy()).validate(any(), any(), any());

		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(quoteModel.getState()).willReturn(QuoteState.SELLER_REQUEST);
		given(Boolean.valueOf(quoteSnapshotStateTransitionMap.containsKey((QuoteState.SELLER_REQUEST)))).willReturn(Boolean.TRUE);
		given(quoteSnapshotStateTransitionMap.get(QuoteState.SELLER_REQUEST)).willReturn(QuoteState.SELLERAPPROVER_PENDING);

		given((defaultCommerceQuoteService.getQuoteUpdateExpirationTimeStrategy()).updateExpirationTime(any(), eq(quoteModel),
				any())).willReturn(quoteModel);
		given((defaultCommerceQuoteService.getQuoteUpdateStateStrategy()).updateQuoteState(any(), eq(quoteModel), any()))
				.willReturn(quoteModel);
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.SELLER));

		defaultCommerceQuoteService.submitQuote(quoteModel, userModel);

		verify(quoteUpdateStateStrategy).updateQuoteState(QuoteAction.SUBMIT, quoteModel, userModel);
		verifySaveQuote(quoteModel);
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldSubmitQuoteThrowExceptionWhenActionNotAllowed()
	{
		doThrow(new IllegalQuoteStateException(null, null, null, null, "message"))
				.when(defaultCommerceQuoteService.getQuoteActionValidationStrategy()).validate(any(), any(), any());

		defaultCommerceQuoteService.submitQuote(mock(QuoteModel.class), userModel);
	}

	@Test
	public void shouldPerformValidationStrategyCallWhenGettingAllowedActions()
	{
		final QuoteModel quoteModel = mock(QuoteModel.class);
		defaultCommerceQuoteService.getAllowedActions(quoteModel, userModel);

		verify(quoteStateSelectionStrategy).getAllowedActionsForState(quoteModel.getState(), userModel);
	}

	@Test
	public void testSubmitQuoteForBuyer()
	{
		final QuoteModel quoteModel = mock(QuoteModel.class);
		final QuoteModel updatedQuoteModel = mock(QuoteModel.class);

		given(quoteModel.getVersion()).willReturn(Integer.valueOf(1));
		given(quoteModel.getState()).willReturn(QuoteState.BUYER_DRAFT);
		given(updatedQuoteModel.getState()).willReturn(QuoteState.BUYER_DRAFT);

		final CartModel mockedSessionCart = mock(CartModel.class);
		given(Boolean.valueOf(defaultCommerceQuoteService.getCartService().hasSessionCart())).willReturn(Boolean.TRUE);
		given(defaultCommerceQuoteService.getCartService().getSessionCart()).willReturn(mockedSessionCart);
		given(mockedSessionCart.getQuoteReference()).willReturn(quoteModel);

		given(Boolean.valueOf(quoteSnapshotStateTransitionMap.containsKey((QuoteState.BUYER_DRAFT)))).willReturn(Boolean.TRUE);
		given(quoteSnapshotStateTransitionMap.get(QuoteState.BUYER_DRAFT)).willReturn(QuoteState.BUYER_SUBMITTED);
		given(defaultCommerceQuoteService.createQuoteSnapshot(quoteModel)).willReturn(updatedQuoteModel);

		doNothing().when(defaultCommerceQuoteService.getQuoteMetadataValidationStrategy()).validate(any(), any(), any());
		given((defaultCommerceQuoteService.getQuoteUpdateExpirationTimeStrategy()).updateExpirationTime(any(),
				eq(updatedQuoteModel), any())).willReturn(updatedQuoteModel);
		given((defaultCommerceQuoteService.getQuoteUpdateStateStrategy()).updateQuoteState(any(), eq(updatedQuoteModel), any()))
				.willReturn(updatedQuoteModel);

		final DefaultCommerceQuoteService commerceQuoteServiceSpy = spy(defaultCommerceQuoteService);
		doReturn(updatedQuoteModel).when(commerceQuoteServiceSpy).saveUpdate(any(), any(), any());
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.BUYER));

		commerceQuoteServiceSpy.submitQuote(quoteModel, userModel);
		verify(quoteUpdateStateStrategy).updateQuoteState(QuoteAction.SUBMIT, updatedQuoteModel, userModel);
		verifySaveQuote(updatedQuoteModel);
	}

	@Test(expected = IllegalQuoteSubmitException.class)
	public void shouldNotSubmitQuoteIfQuoteTotalIsNegative()
	{
		final QuoteModel quoteModel = mock(QuoteModel.class);
		final QuoteModel updatedQuoteModel = mock(QuoteModel.class);

		given(quoteModel.getTotalPrice()).willReturn(Double.valueOf(-10));
		given(quoteModel.getVersion()).willReturn(Integer.valueOf(1));
		given(quoteModel.getState()).willReturn(QuoteState.BUYER_DRAFT);
		given(updatedQuoteModel.getState()).willReturn(QuoteState.BUYER_DRAFT);

		final DefaultCommerceQuoteService commerceQuoteServiceSpy = spy(defaultCommerceQuoteService);
		commerceQuoteServiceSpy.submitQuote(quoteModel, userModel);
	}

	@Test
	public void testSubmitQuoteForSellerDirectSubmit()
	{
		final QuoteModel quoteModel = mock(QuoteModel.class);

		given(quoteModel.getVersion()).willReturn(Integer.valueOf(1));
		given(quoteModel.getState()).willReturn(QuoteState.SELLER_REQUEST);

		given(Boolean.valueOf(defaultCommerceQuoteService.getCartService().hasSessionCart())).willReturn(Boolean.FALSE);
		given(defaultCommerceQuoteService.getCartService().getSessionCart()).willReturn(null);

		given(Boolean.valueOf(quoteSnapshotStateTransitionMap.containsKey((QuoteState.SELLER_REQUEST)))).willReturn(Boolean.TRUE);
		given(quoteSnapshotStateTransitionMap.get(QuoteState.SELLER_REQUEST)).willReturn(QuoteState.SELLER_SUBMITTED);

		doNothing().when(defaultCommerceQuoteService.getQuoteMetadataValidationStrategy()).validate(any(), any(), any());

		given((defaultCommerceQuoteService.getQuoteUpdateExpirationTimeStrategy()).updateExpirationTime(any(), eq(quoteModel),
				any())).willReturn(quoteModel);
		given((defaultCommerceQuoteService.getQuoteUpdateStateStrategy()).updateQuoteState(any(), eq(quoteModel), any()))
				.willReturn(quoteModel);

		final DefaultCommerceQuoteService commerceQuoteServiceSpy = spy(defaultCommerceQuoteService);
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel)).willReturn(Optional.of(QuoteUserType.SELLER));

		commerceQuoteServiceSpy.submitQuote(quoteModel, userModel);

		//For direct submission from seller check if snapshotted quote model not used.
		verify(quoteUpdateStateStrategy).updateQuoteState(QuoteAction.SUBMIT, quoteModel, userModel);
		verifySaveQuote(quoteModel);
	}

	@Test
	public void testApproveQuote()
	{
		final QuoteModel quoteModel = mock(QuoteModel.class);

		given(quoteModel.getVersion()).willReturn(Integer.valueOf(1));
		given(quoteModel.getState()).willReturn(QuoteState.SELLERAPPROVER_PENDING);

		given(Boolean.valueOf(defaultCommerceQuoteService.getCartService().hasSessionCart())).willReturn(Boolean.FALSE);
		given(defaultCommerceQuoteService.getCartService().getSessionCart()).willReturn(null);

		given(Boolean.valueOf(quoteSnapshotStateTransitionMap.containsKey((QuoteState.SELLERAPPROVER_PENDING))))
				.willReturn(Boolean.TRUE);
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel))
				.willReturn(Optional.of(QuoteUserType.SELLERAPPROVER));
		given(quoteSnapshotStateTransitionMap.get(QuoteState.SELLERAPPROVER_PENDING))
				.willReturn(QuoteState.SELLERAPPROVER_APPROVED);
		given((defaultCommerceQuoteService.getQuoteUpdateStateStrategy()).updateQuoteState(any(), eq(quoteModel), any()))
				.willReturn(quoteModel);

		final DefaultCommerceQuoteService commerceQuoteServiceSpy = spy(defaultCommerceQuoteService);
		commerceQuoteServiceSpy.approveQuote(quoteModel, userModel);

		verify(quoteUpdateStateStrategy).updateQuoteState(QuoteAction.APPROVE, quoteModel, userModel);
		verifySaveQuote(quoteModel);
		verify(eventService, times(1)).publishEvent(any(QuoteSellerApprovalSubmitEvent.class));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testApproveQuoteWhenSuppliedUserIsNull()
	{
		defaultCommerceQuoteService.approveQuote(mock(QuoteModel.class), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testApproveQuoteWhenSuppliedQuoteIsNull()
	{
		defaultCommerceQuoteService.rejectQuote(null, mock(UserModel.class));
	}

	@Test
	public void testRejectQuote()
	{
		final QuoteModel quoteModel = mock(QuoteModel.class);

		given(quoteModel.getVersion()).willReturn(Integer.valueOf(1));
		given(quoteModel.getState()).willReturn(QuoteState.SELLERAPPROVER_PENDING);

		given(Boolean.valueOf(defaultCommerceQuoteService.getCartService().hasSessionCart())).willReturn(Boolean.FALSE);
		given(defaultCommerceQuoteService.getCartService().getSessionCart()).willReturn(null);

		given(Boolean.valueOf(quoteSnapshotStateTransitionMap.containsKey((QuoteState.SELLERAPPROVER_PENDING))))
				.willReturn(Boolean.TRUE);
		given(quoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(userModel))
				.willReturn(Optional.of(QuoteUserType.SELLERAPPROVER));
		given(quoteSnapshotStateTransitionMap.get(QuoteState.SELLERAPPROVER_PENDING))
				.willReturn(QuoteState.SELLERAPPROVER_REJECTED);
		given((defaultCommerceQuoteService.getQuoteUpdateStateStrategy()).updateQuoteState(any(), eq(quoteModel), any()))
				.willReturn(quoteModel);

		final DefaultCommerceQuoteService commerceQuoteServiceSpy = spy(defaultCommerceQuoteService);
		commerceQuoteServiceSpy.rejectQuote(quoteModel, userModel);

		verify(quoteUpdateStateStrategy).updateQuoteState(QuoteAction.REJECT, quoteModel, userModel);
		verifySaveQuote(quoteModel);
		verify(eventService, times(1)).publishEvent(any(QuoteSellerApprovalSubmitEvent.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRejectQuoteWhenSuppliedUserIsNull()
	{
		defaultCommerceQuoteService.rejectQuote(mock(QuoteModel.class), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRejectQuoteWhenSuppliedQuoteIsNull()
	{
		defaultCommerceQuoteService.rejectQuote(null, mock(UserModel.class));
	}

	@Test
	public void shouldRemoveQuoteCartAndSessionParam()
	{
		final CartModel sessionCart = mock(CartModel.class);

		final QuoteModel quoteModel = mock(QuoteModel.class);
		given(quoteModel.getCode()).willReturn("400");
		given(quoteModel.getVersion()).willReturn(Integer.valueOf(1));
		given(quoteModel.getCartReference()).willReturn(sessionCart);

		given(sessionCart.getQuoteReference()).willReturn(quoteModel);
		given(Boolean.valueOf(defaultCommerceQuoteService.getCartService().hasSessionCart())).willReturn(Boolean.TRUE);
		given(defaultCommerceQuoteService.getCartService().getSessionCart()).willReturn(sessionCart);

		defaultCommerceQuoteService.removeQuoteCart(quoteModel);
		verify(sessionService).removeAttribute(DefaultCartService.SESSION_CART_PARAMETER_NAME);
		verify(modelService).remove(sessionCart);
		verify(modelService).refresh(quoteModel);
	}

	@Test
	public void shouldNotRemoveQuoteCartAndSessionParam()
	{
		final QuoteModel quoteModel = mock(QuoteModel.class);
		final CartModel sessionCart = mock(CartModel.class);
		given(quoteModel.getCartReference()).willReturn(null);

		defaultCommerceQuoteService.removeQuoteCart(quoteModel);
		verify(sessionService, never()).removeAttribute(DefaultCartService.SESSION_CART_PARAMETER_NAME);
		verify(modelService, never()).remove(sessionCart);
		verify(modelService, never()).refresh(quoteModel);
	}

	@Test
	public void shouldReturnTrueForIsQuoteCartValidForCheckout()
	{
		final QuoteModel quoteModel = mock(QuoteModel.class);
		final CartModel sessionCart = mock(CartModel.class);
		given(sessionCart.getQuoteReference()).willReturn(quoteModel);

		given(Boolean.valueOf(defaultCommerceQuoteService.getQuoteCartValidationStrategy().validate(sessionCart, quoteModel)))
				.willReturn(Boolean.TRUE);

		Assert.assertTrue("Quote cart should be valid for checkout",
				defaultCommerceQuoteService.isQuoteCartValidForCheckout(sessionCart));
	}

	@Test
	public void shouldReturnFalseForIsQuoteCartValidForCheckout()
	{
		final QuoteModel quoteModel = mock(QuoteModel.class);
		final CartModel sessionCart = mock(CartModel.class);
		given(sessionCart.getQuoteReference()).willReturn(quoteModel);

		given(Boolean.valueOf(defaultCommerceQuoteService.getQuoteCartValidationStrategy().validate(sessionCart, quoteModel)))
				.willReturn(Boolean.FALSE);

		Assert.assertFalse("Quote cart should not be valid for checkout",
				defaultCommerceQuoteService.isQuoteCartValidForCheckout(sessionCart));
	}

	@Test
	public void shouldReturnFalseForIsQuoteCartValidForCheckoutWhenNotQuoteCart()
	{
		final QuoteModel quoteModel = mock(QuoteModel.class);
		final CartModel sessionCart = mock(CartModel.class);
		given(sessionCart.getQuoteReference()).willReturn(null);

		Assert.assertFalse("Quote cart should not be valid for checkout",
				defaultCommerceQuoteService.isQuoteCartValidForCheckout(sessionCart));
		verify(quoteCartValidationStrategy, never()).validate(sessionCart, quoteModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldGetQuotesCountValidateNullCustomer()
	{
		final UserModel quoteUser = mock(UserModel.class);
		final BaseStoreModel store = mock(BaseStoreModel.class);

		defaultCommerceQuoteService.getQuotesCountForStoreAndUser(null, quoteUser, store);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldGetQuotesCountValidateNullQuoteUser()
	{
		final CustomerModel customerModel = mock(CustomerModel.class);
		final BaseStoreModel store = mock(BaseStoreModel.class);

		defaultCommerceQuoteService.getQuotesCountForStoreAndUser(customerModel, null, store);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldGetQuotesCountValidateNullStore()
	{
		final CustomerModel customerModel = mock(CustomerModel.class);
		final UserModel quoteUserModel = mock(UserModel.class);

		defaultCommerceQuoteService.getQuotesCountForStoreAndUser(customerModel, quoteUserModel, null);
	}

	@Test
	public void shouldGetQuotesCountForAnonymousUser()
	{
		final CustomerModel customerModel = mock(CustomerModel.class);
		final UserModel quoteUserModel = mock(UserModel.class);
		final BaseStoreModel store = mock(BaseStoreModel.class);

		given(Boolean.valueOf(userService.isAnonymousUser(customerModel))).willReturn(Boolean.TRUE);

		Assert.assertEquals("Should return 0 quote count for anonymous user", Integer.valueOf(0),
				defaultCommerceQuoteService.getQuotesCountForStoreAndUser(customerModel, quoteUserModel, store));
	}

	@Test
	public void shouldGetQuotesCountForUser()
	{
		final CustomerModel customerModel = mock(CustomerModel.class);
		final UserModel quoteUserModel = mock(UserModel.class);
		final BaseStoreModel store = mock(BaseStoreModel.class);
		final Set<QuoteState> quoteStates = new HashSet<>();
		quoteStates.add(QuoteState.BUYER_OFFER);
		quoteStates.add(QuoteState.BUYER_DRAFT);

		given(Boolean.valueOf(userService.isAnonymousUser(customerModel))).willReturn(Boolean.FALSE);
		given(quoteStateSelectionStrategy.getAllowedStatesForAction(QuoteAction.VIEW, quoteUserModel)).willReturn(quoteStates);
		given(commerceQuoteDao.getQuotesCountForCustomerAndStore(customerModel, store, quoteStates)).willReturn(Integer.valueOf(3));

		Assert.assertEquals("Should return 3 quote count for user", Integer.valueOf(3),
				defaultCommerceQuoteService.getQuotesCountForStoreAndUser(customerModel, quoteUserModel, store));
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldCancelQuoteThrowExceptionWhenActionNotAllowed()
	{
		doThrow(new IllegalQuoteStateException(null, null, null, null, "message"))
				.when(defaultCommerceQuoteService.getQuoteActionValidationStrategy()).validate(eq(QuoteAction.CANCEL), any(), any());

		defaultCommerceQuoteService.cancelQuote(mock(QuoteModel.class), userModel);
	}

	@Test
	public void shouldPassValidationWhenOverThreshold()
	{
		final QuoteModel quote = mock(QuoteModel.class);
		given(quote.getVersion()).willReturn(Integer.valueOf(1));
		final UserModel user = mock(UserModel.class);
		final CartModel cart = mock(CartModel.class);
		given(cartService.getSessionCart()).willReturn(cart);
		given(cart.getUser()).willReturn(user);
		// cart has sub-total 3, which is higher than threshold (2)
		given(cart.getSubtotal()).willReturn(Double.valueOf(3.0));

		final DefaultCommerceQuoteService serviceSpy = spy(defaultCommerceQuoteService);
		doReturn(Double.valueOf(2.0)).when(serviceSpy).getQuoteRequestThreshold(quote, user, cart);
		serviceSpy.validateQuoteThreshold(quote, user, cart);
	}

	@Test(expected = QuoteUnderThresholdException.class)
	public void shouldFailValidationWhenUnderThreshold()
	{
		final QuoteModel quote = mock(QuoteModel.class);
		given(quote.getSubtotal()).willReturn(Double.valueOf(1.0));
		given(quote.getVersion()).willReturn(Integer.valueOf(1));

		final UserModel user = mock(UserModel.class);
		final CartModel cart = mock(CartModel.class);
		given(cartService.getSessionCart()).willReturn(cart);
		given(cart.getUser()).willReturn(user);
		// cart has sub-total 1, which is lower than threshold (2)
		given(cart.getSubtotal()).willReturn(Double.valueOf(1.0));

		final DefaultCommerceQuoteService serviceSpy = spy(defaultCommerceQuoteService);
		doReturn(Double.valueOf(2.0)).when(serviceSpy).getQuoteRequestThreshold(quote, user, cart);
		serviceSpy.validateQuoteThreshold(quote, user, cart);
	}

	@Test
	public void shouldPassValidationWhenNotFirstVersion()
	{
		// quote-cart has sub-total (1) lower threshold (2), but is not the first version
		final QuoteModel quote = mock(QuoteModel.class);
		given(quote.getVersion()).willReturn(Integer.valueOf(2));

		final UserModel user = mock(UserModel.class);
		final CartModel cart = mock(CartModel.class);
		given(cartService.getSessionCart()).willReturn(cart);
		given(cart.getUser()).willReturn(user);
		given(cart.getSubtotal()).willReturn(Double.valueOf(1.0));

		final DefaultCommerceQuoteService serviceSpy = spy(defaultCommerceQuoteService);
		doReturn(Boolean.FALSE).when(serviceSpy).isRequestThresholdRequired(quote, user, cart);
		serviceSpy.validateQuoteThreshold(quote, user, cart);
	}
}
