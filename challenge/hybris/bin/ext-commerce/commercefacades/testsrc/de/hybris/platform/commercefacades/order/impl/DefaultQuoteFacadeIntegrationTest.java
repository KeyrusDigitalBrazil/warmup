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
package de.hybris.platform.commercefacades.order.impl;

import static de.hybris.platform.commerceservices.constants.CommerceServicesConstants.QUOTE_REQUEST_INITIATION_THRESHOLD;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commercefacades.comment.data.CommentData;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.QuoteFacade;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.enums.DiscountType;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceQuoteExpirationTimeException;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.commerceservices.order.OrderQuoteDiscountValuesAccessor;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteStateException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.order.impl.DefaultCartService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.localization.Localization;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for DefaultQuoteFacade
 */
@IntegrationTest
public class DefaultQuoteFacadeIntegrationTest extends BaseCommerceBaseTest
{
	private static final String PRODUCT_CODE = "HW1210-3422";
	private static final String TEST_BASESITE_UID = "testSite";
	private static final String SESSION_CART_PARAMETER_NAME = "cart";

	private final Map<String, String> originalConfigs = new HashMap<String, String>();

	@Resource
	private CartFacade cartFacade;
	@Resource
	private CartService cartService;
	@Resource
	private ModelService modelService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private UserService userService;
	@Resource
	private SessionService sessionService;
	@Resource
	private CommerceQuoteService commerceQuoteService;
	@Resource
	private CommerceCartService commerceCartService;
	@Resource
	private QuoteService quoteService;
	@Resource
	private OrderQuoteDiscountValuesAccessor orderQuoteDiscountValuesAccessor;

	private CurrencyModel currency;
	private BaseStoreModel baseStore;
	private UserModel user;
	private BaseSiteModel baseSite;

	@Resource
	private QuoteFacade quoteFacade;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createDefaultUsers();

		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
		importCsv("/commerceservices/test/user-groups.impex", "utf-8");
		importCsv("/commercefacades/test/testCommerceComments.impex", "utf-8");
		importCsv("/impex/essentialdata_usergroups.impex", "UTF-8");
		importCsv("/commercefacades/test/testQuoteFacade.impex", "utf-8");
		importCsv("/commerceservices/test/facadeIntegrationTestQuotes.impex", "utf-8");
		importCsv("/commerceservices/test/testQuoteDiscounts.impex", "utf-8");

		baseSite = baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID);
		baseSiteService.setCurrentBaseSite(baseSite, false);

		user = userService.getUserForUID("john");
		userService.setCurrentUser(user);
		final CartModel sessionCartModel = cartService.getSessionCart();
		currency = sessionCartModel.getCurrency();
		baseStore = sessionCartModel.getStore();

		// set config for site/currency
		originalConfigs.put(QUOTE_REQUEST_INITIATION_THRESHOLD + ".testSite.EUR",
				Config.getParameter(QUOTE_REQUEST_INITIATION_THRESHOLD + ".testSite.EUR"));
		Config.setParameter(QUOTE_REQUEST_INITIATION_THRESHOLD + ".testSite.EUR", "1");
	}

	@After
	public void tearDown()
	{
		Config.setParameter(QUOTE_REQUEST_INITIATION_THRESHOLD + ".testSite.EUR",
				originalConfigs.get(QUOTE_REQUEST_INITIATION_THRESHOLD + ".testSite.EUR"));
	}

	protected QuoteData createQuoteFromSessionCart()
	{
		Assert.assertTrue(cartFacade.hasSessionCart());
		final CartModel sessionCartModel = cartService.getSessionCart();
		sessionCartModel.setSite(baseSite);
		modelService.save(sessionCartModel);

		final QuoteData newQuoteData = quoteFacade.initiateQuote();
		Assert.assertNotNull("There should be a QuoteData object created from session cart.", newQuoteData);

		return newQuoteData;
	}

	protected PageableData createPageableData()
	{
		final PageableData pd = new PageableData();
		pd.setCurrentPage(0);
		pd.setPageSize(20);

		return pd;
	}

	protected QuoteModel createSampleQuote(final String quoteCode, final QuoteState state)
	{
		return createSampleQuote(quoteCode, state, Integer.valueOf(1));
	}

	protected QuoteModel createSampleQuote(final String quoteCode, final QuoteState state, final Integer version)
	{
		final QuoteModel quote = modelService.create(QuoteModel.class);
		quote.setCode(quoteCode);
		quote.setState(state);
		quote.setVersion(version);
		quote.setUser(user);
		quote.setStore(baseStore);
		quote.setSite(baseSite);
		quote.setCurrency(currency);
		quote.setDate(new Date());
		quote.setAssignee(user);
		modelService.save(quote);
		return quote;
	}

	protected QuoteData getQuoteByCode(final String quoteCode)
	{
		final List<QuoteData> quotes = new ArrayList<QuoteData>();
		final SearchPageData<QuoteData> page = quoteFacade.getPagedQuotes(createPageableData());
		for (final QuoteData result : page.getResults())
		{
			Assert.assertNotNull(result.getCode());
			if (result.getCode().equals(quoteCode))
			{
				quotes.add(result);
			}
		}

		if (quotes.size() > 0)
		{
			Assert.assertEquals("More than one quote for the same code & customer & store & state", 1, quotes.size());
			return quotes.get(0);
		}
		else
		{
			return null;
		}
	}

	@Test
	public void shouldInitiateQuote()
	{
		Assert.assertTrue(cartFacade.hasSessionCart());
		final CartModel sessionCartModel = cartService.getSessionCart();
		final String cartDescription = RandomStringUtils.randomAlphabetic(8);
		sessionCartModel.setDescription(cartDescription);
		modelService.save(sessionCartModel);

		final QuoteData newQuoteData = quoteFacade.initiateQuote();

		Assert.assertNotNull("There should be a QuoteData object returned.", newQuoteData);
		Assert.assertTrue("The quoteCode should not be blank.", StringUtils.isNotBlank(newQuoteData.getCode()));
		Assert.assertEquals("The quote version for a new quote should be 1.", Integer.valueOf(1), newQuoteData.getVersion());
		Assert.assertEquals("The quote state for a new quote should be BUYER_DRAFT", QuoteState.BUYER_DRAFT,
				newQuoteData.getState());
		Assert.assertEquals(
				"The cart data should be properly copied to create the quote.  The description field is used to assess that.",
				cartDescription, newQuoteData.getDescription());
	}

	@Test
	public void shouldCreateCartQuote()
	{
		final String quoteCode = "testQuote";
		final QuoteModel quoteModel = createSampleQuote(quoteCode, QuoteState.BUYER_DRAFT);
		final CartData cartData = quoteFacade.createCartFromQuote(quoteCode);
		Assert.assertNotNull(cartData);
		Assert.assertNotNull(cartData.getQuoteData());
		Assert.assertEquals(quoteModel.getCode(), cartData.getQuoteData().getCode());
	}

	@Test(expected = ModelNotFoundException.class)
	public void shouldNotCreateCartForInexistentQuote()
	{
		final String randomString = RandomStringUtils.randomAlphabetic(12);
		quoteFacade.createCartFromQuote(randomString);
	}

	@Test
	public void shouldRequote() throws CommerceCartModificationException
	{
		final Long qty = Long.valueOf(1L);
		cartFacade.addToCart(PRODUCT_CODE, qty.longValue());

		final QuoteData initialQuoteData = createQuoteFromSessionCart();

		quoteFacade.enableQuoteEdit(initialQuoteData.getCode());
		quoteFacade.addComment("comment text");
		quoteFacade.addEntryComment(0, "comment text");

		final QuoteModel quoteModel = commerceQuoteService.getQuoteByCodeAndCustomerAndStore((CustomerModel) user, user, baseStore,
				initialQuoteData.getCode());

		quoteModel.setName("quote name");
		quoteModel.setState(QuoteState.CANCELLED);
		quoteModel.setExpirationTime(DateUtils.addDays(Calendar.getInstance().getTime(), 5));
		modelService.save(quoteModel);

		final QuoteData newQuote = quoteFacade.requote(initialQuoteData.getCode());

		Assert.assertNotNull(newQuote);
		Assert.assertEquals(Localization.getLocalizedString("type.quote.name") + " " + newQuote.getCode(), newQuote.getName());
		Assert.assertNull(newQuote.getDescription());
		Assert.assertNull(newQuote.getExpirationTime());
		Assert.assertEquals(BigDecimal.valueOf(0d), newQuote.getPreviousEstimatedTotal().getValue());
		Assert.assertEquals(QuoteState.BUYER_DRAFT, newQuote.getState());
		Assert.assertEquals(Integer.valueOf(1), newQuote.getVersion());
		Assert.assertTrue(CollectionUtils.isEmpty(newQuote.getComments()));
		Assert.assertTrue(CollectionUtils.isEmpty(newQuote.getEntries().get(0).getComments()));
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldNotRequoteForQuoteStateNotAllowed()
	{
		final String quoteCode = "testQuote";
		createSampleQuote(quoteCode, QuoteState.BUYER_DRAFT);
		quoteFacade.requote(quoteCode);
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldNotApplyQuoteDiscountIfQuoteActionValidationFails()
	{
		final QuoteData quoteData = createQuoteFromSessionCart();
		quoteFacade.enableQuoteEdit(quoteData.getCode());
		quoteFacade.applyQuoteDiscount(Double.valueOf(20), DiscountType.ABSOLUTE.toString());
	}

	@Test
	public void shouldAddComment()
	{
		final CartModel sessionCart = cartService.getSessionCart();
		sessionCart.setQuoteReference(createSampleQuote("testQuote", QuoteState.BUYER_DRAFT));
		modelService.save(sessionCart);

		// add comment to quote
		quoteFacade.addComment("Test Comment");

		// validate comment
		final CartData cartData = cartFacade.getSessionCart();
		Assert.assertNotNull("There should be a CartData object returned.", cartData);
		final List<CommentData> comments = cartData.getComments();
		Assert.assertNotNull("Cart should have comments.", comments);
		Assert.assertEquals("Cart should have 1 comment.", 1, comments.size());
		Assert.assertEquals("Cart comment should match with what was saved.", "Test Comment", comments.get(0).getText());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldValidateQuoteCartWhenAddingComment()
	{
		final CartModel sessionCart = cartService.getSessionCart();
		sessionCart.setQuoteReference(null);
		modelService.save(sessionCart);

		quoteFacade.addComment("  ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddBlankComment()
	{
		quoteFacade.addComment("  ");
	}

	@Test
	public void shouldLoadBuyerDraftQuoteInCart()
	{
		final QuoteData quoteData = createQuoteFromSessionCart();
		sessionService.removeAttribute(SESSION_CART_PARAMETER_NAME);
		final CartData cart = cartFacade.getSessionCart();
		Assert.assertNotNull(cart);
		Assert.assertNull(cart.getQuoteData());
		quoteFacade.enableQuoteEdit(quoteData.getCode());
		final QuoteData sessionQuoteData = cartFacade.getSessionCart().getQuoteData();

		Assert.assertTrue("The quote id should match with previously created quote",
				StringUtils.equals(sessionQuoteData.getCode(), quoteData.getCode()));
		Assert.assertEquals("The quote version should match with previously created quote.", quoteData.getVersion(),
				sessionQuoteData.getVersion());
		Assert.assertEquals("The quote state should match with previously created quote", QuoteState.BUYER_DRAFT,
				sessionQuoteData.getState());

	}

	@Test
	public void shouldLoadOfferQuoteInCartToEdit()
	{
		final QuoteData quoteData = createQuoteFromSessionCart();

		final QuoteModel quoteModel = commerceQuoteService.getQuoteByCodeAndCustomerAndStore((CustomerModel) user, user, baseStore,
				quoteData.getCode());
		quoteModel.setState(QuoteState.BUYER_OFFER);
		modelService.save(quoteModel);
		modelService.refresh(quoteModel);
		final CartData cart = cartFacade.getSessionCart();
		Assert.assertNotNull(cart);
		Assert.assertNull(cart.getQuoteData());
		quoteFacade.enableQuoteEdit(quoteData.getCode());
		final QuoteData sessionQuoteData = cartFacade.getSessionCart().getQuoteData();

		Assert.assertTrue("The quote id should match with previously created quote",
				StringUtils.equals(sessionQuoteData.getCode(), quoteData.getCode()));
		Assert.assertEquals("The quote version should match with previously created quote.", quoteData.getVersion(),
				sessionQuoteData.getVersion());
		Assert.assertEquals("The quote state should match with previously created quote", QuoteState.BUYER_DRAFT,
				sessionQuoteData.getState());
	}

	@Test
	public void shouldBuyerGetPagedQuotes()
	{
		final QuoteData newQuoteData = createQuoteFromSessionCart();

		final SearchPageData<QuoteData> selectedQuotes = quoteFacade.getPagedQuotes(createPageableData());

		final List<QuoteData> selectedQuoteDataList = selectedQuotes.getResults().stream()
				.filter(qd -> qd.getCode().equals(newQuoteData.getCode())).collect(Collectors.toList());

		Assert.assertEquals("There should be a QuoteData object matching the quote created from the session cart.", 1,
				selectedQuoteDataList.size());
		Assert.assertEquals("The quote state for the selected quote should be CREATED", QuoteState.BUYER_DRAFT,
				selectedQuoteDataList.get(0).getState());
	}

	@Test
	public void shouldShowMultipleVersionsOnlyOnce()
	{
		// create 3 versions of the same quote
		final String quoteCode = "testQuote";
		createSampleQuote(quoteCode, QuoteState.BUYER_DRAFT, Integer.valueOf(1));
		createSampleQuote(quoteCode, QuoteState.SELLER_SUBMITTED, Integer.valueOf(2));
		createSampleQuote(quoteCode, QuoteState.BUYER_OFFER, Integer.valueOf(3));

		final SearchPageData<QuoteData> selectedQuotes = quoteFacade.getPagedQuotes(createPageableData());
		Assert.assertNotNull(selectedQuotes);
		Assert.assertNotNull(selectedQuotes.getResults());
		// only one version should be loaded from search, and it should be the latest
		Assert.assertEquals(1L, selectedQuotes.getResults().size());
		Assert.assertEquals(Integer.valueOf(3), selectedQuotes.getResults().get(0).getVersion());
	}

	@Test
	public void shouldSubmitQuote() throws CommerceCartModificationException
	{
		cartService.getSessionCart();
		final Long qty = Long.valueOf(1L);
		cartFacade.addToCart(PRODUCT_CODE, qty.longValue());

		final QuoteData savedQuoteData = createQuoteFromSessionCart();
		final String quoteCode = savedQuoteData.getCode();

		quoteFacade.enableQuoteEdit(quoteCode);
		quoteFacade.addComment("Test Comment");
		quoteFacade.submitQuote(quoteCode);

		// validate quote
		final QuoteData submitedQuote = getQuoteByCode(quoteCode);
		Assert.assertNotNull("There should be a submited quote.", submitedQuote);
		Assert.assertEquals(QuoteState.BUYER_SUBMITTED, submitedQuote.getState());
		Assert.assertEquals(savedQuoteData.getVersion().intValue(), submitedQuote.getVersion().intValue());

		// validate comment
		final List<CommentData> comments = submitedQuote.getComments();
		Assert.assertNotNull("Quote should have comments.", comments);
		Assert.assertEquals("Quote should have 1 comment.", 1, comments.size());
		Assert.assertEquals("Quote comment should match with what was saved.", "Test Comment", comments.get(0).getText());
	}

	@Test(expected = ModelNotFoundException.class)
	public void shouldNotSubmitInexistentQuote()
	{
		final String randomString = RandomStringUtils.randomAlphabetic(12);
		quoteFacade.submitQuote(randomString);
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldNotSubmitAlreadySubmittedQuote() throws CommerceCartModificationException
	{
		final String randomString = RandomStringUtils.randomAlphabetic(12);
		createSampleQuote(randomString, QuoteState.BUYER_SUBMITTED);
		// set subtotal over threshold
		cartService.getSessionCart().setSubtotal(Double.valueOf(2.0));
		// try to submit same quote again (should fail)
		quoteFacade.submitQuote(randomString);
	}

	@Test
	public void shouldUpdateQuantitiesOnSaveQuote() throws CommerceCartModificationException
	{
		CartModel sessionCart = cartService.getSessionCart();
		Long qty = Long.valueOf(1L);
		cartFacade.addToCart(PRODUCT_CODE, qty.longValue());
		final QuoteData initialQuoteData = createQuoteFromSessionCart();
		Assert.assertNotNull(initialQuoteData);
		Assert.assertEquals(1, initialQuoteData.getEntries().size());
		Assert.assertEquals(qty, initialQuoteData.getEntries().get(0).getQuantity());

		quoteFacade.enableQuoteEdit(initialQuoteData.getCode());
		sessionCart = cartService.getSessionCart();
		cartFacade.addToCart(PRODUCT_CODE, qty.longValue());
		modelService.save(sessionCart);
		qty = Long.valueOf(qty.longValue() + 1L);
		sessionCart = cartService.getSessionCart();

		commerceQuoteService.updateQuoteFromCart(sessionCart, user);

		final QuoteData currentQuoteData = getQuoteByCode(initialQuoteData.getCode());
		Assert.assertNotNull(currentQuoteData);
		Assert.assertEquals(qty, currentQuoteData.getEntries().get(0).getQuantity());
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldNotSaveQuoteInInvalidState() throws CommerceCartModificationException
	{
		final QuoteModel quote = createSampleQuote("testQuote", QuoteState.BUYER_ACCEPTED);

		final CartModel sessionCart = cartService.getSessionCart();
		final Long qty = Long.valueOf(1L);
		cartFacade.addToCart(PRODUCT_CODE, qty.longValue());
		sessionCart.setQuoteReference(quote);
		modelService.save(sessionCart);
		commerceQuoteService.updateQuoteFromCart(sessionCart, user);
	}

	@Test
	public void shouldRetrieveAllowedActions()
	{
		final QuoteData quoteData = createQuoteFromSessionCart();
		final Set<QuoteAction> actions = quoteFacade.getAllowedActions(quoteData.getCode());
		Assert.assertNotNull(actions);
		Assert.assertFalse(actions.isEmpty());
	}

	@Test
	public void shouldHaveDistinctAllowedActionsForDifferentStates()
	{
		final String quoteCode1 = "testQuote1";
		createSampleQuote(quoteCode1, QuoteState.BUYER_DRAFT);
		final String quoteCode2 = "testQuote2";
		createSampleQuote(quoteCode2, QuoteState.BUYER_ACCEPTED);

		final Set<QuoteAction> actions1 = quoteFacade.getAllowedActions(quoteCode1);
		Assert.assertNotNull(actions1);
		Assert.assertFalse(actions1.isEmpty());
		final Set<QuoteAction> actions2 = quoteFacade.getAllowedActions(quoteCode2);
		Assert.assertNotNull(actions2);
		Assert.assertFalse(actions2.isEmpty());

		boolean noMatch = false;
		// at least one of the actions should be different
		for (final QuoteAction action1 : actions1)
		{
			if (!actions2.contains(action1))
			{
				noMatch = true;
				break;
			}
		}
		Assert.assertTrue(noMatch);
	}

	@Test
	public void shouldAcceptAndPrepareCheckout() throws CommerceCartModificationException
	{
		final Long qty = Long.valueOf(1L);
		cartFacade.addToCart(PRODUCT_CODE, qty.longValue());
		final QuoteData initialQuoteData = createQuoteFromSessionCart();
		Assert.assertNotNull(initialQuoteData);
		Assert.assertEquals(1, initialQuoteData.getEntries().size());
		Assert.assertEquals(qty, initialQuoteData.getEntries().get(0).getQuantity());
		final QuoteModel quoteModel = commerceQuoteService.getQuoteByCodeAndCustomerAndStore((CustomerModel) user, user, baseStore,
				initialQuoteData.getCode());
		quoteModel.setState(QuoteState.BUYER_OFFER);
		quoteModel.setExpirationTime(DateUtils.addDays(Calendar.getInstance().getTime(), 5));
		modelService.save(quoteModel);
		quoteFacade.acceptAndPrepareCheckout(initialQuoteData.getCode());

		Assert.assertEquals(QuoteState.BUYER_OFFER, quoteModel.getState());
		final CartModel cart = cartService.getSessionCart();
		Assert.assertNotNull("session Cart should not be null.", cart);
		Assert.assertEquals("Session cart should be a quote cart.", quoteModel, cart.getQuoteReference());
	}

	@Test
	public void shouldRemoveAndCreateNewCartForAcceptAndPrepareCheckoutWhereModifiedQuoteCart()
			throws CommerceCartModificationException
	{
		final Long qty = Long.valueOf(1L);
		cartFacade.addToCart(PRODUCT_CODE, qty.longValue());

		final QuoteData initialQuoteData = createQuoteFromSessionCart();
		Assert.assertNotNull(initialQuoteData);
		Assert.assertEquals(1, initialQuoteData.getEntries().size());
		Assert.assertEquals(qty, initialQuoteData.getEntries().get(0).getQuantity());
		final QuoteModel quoteModel = commerceQuoteService.getQuoteByCodeAndCustomerAndStore((CustomerModel) user, user, baseStore,
				initialQuoteData.getCode());
		quoteModel.setState(QuoteState.BUYER_OFFER);
		quoteModel.setExpirationTime(DateUtils.addDays(Calendar.getInstance().getTime(), 5));
		modelService.save(quoteModel);
		quoteFacade.acceptAndPrepareCheckout(initialQuoteData.getCode());
		final String modifiedOfferQuoteCartCode = cartService.getSessionCart().getCode();
		cartFacade.addToCart(PRODUCT_CODE, qty.longValue());
		quoteFacade.acceptAndPrepareCheckout(initialQuoteData.getCode());
		final CartModel newOfferQuoteCart = cartService.getSessionCart();

		Assert.assertNull("Modified offer quote cart should be removed",
				commerceCartService.getCartForCodeAndUser(modifiedOfferQuoteCartCode, user));

		Assert.assertEquals("Cart attached to quote should be a newly created quote offer cart", commerceQuoteService
				.getQuoteByCodeAndCustomerAndStore((CustomerModel) user, user, baseStore, initialQuoteData.getCode())
				.getCartReference(), newOfferQuoteCart);
	}

	@Test(expected = ModelNotFoundException.class)
	public void shouldNotCheckoutNonExistentQuote()
	{
		final String randomString = RandomStringUtils.randomAlphabetic(12);
		quoteFacade.acceptAndPrepareCheckout(randomString);
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldNotCheckoutQuoteInInvalidState()
	{
		final String quoteCode = "testQuote";
		final QuoteModel quoteModel = createSampleQuote(quoteCode, QuoteState.BUYER_ORDERED);
		quoteModel.setExpirationTime(DateUtils.addDays(Calendar.getInstance().getTime(), 5));
		modelService.save(quoteModel);
		quoteFacade.acceptAndPrepareCheckout(quoteCode);
	}

	@Test(expected = CommerceQuoteExpirationTimeException.class)
	public void shouldNotCheckoutExpiredQuote()
	{
		final String quoteCode = "testQuote";
		final QuoteModel quoteModel = createSampleQuote(quoteCode, QuoteState.BUYER_OFFER);
		quoteModel.setExpirationTime(DateUtils.addDays(Calendar.getInstance().getTime(), -5));
		modelService.save(quoteModel);
		quoteFacade.acceptAndPrepareCheckout(quoteCode);
	}

	@Test
	public void shouldCancelQuote()
	{
		final QuoteData savedQuoteData = createQuoteFromSessionCart();
		final String quoteCode = savedQuoteData.getCode();

		quoteFacade.enableQuoteEdit(quoteCode);

		final String editQuoteCartCode = cartService.getSessionCart().getCode();
		quoteFacade.cancelQuote(quoteCode);

		final QuoteData canceledQuoteData = getQuoteForCode(quoteCode);

		Assert.assertEquals("Canceled quote's version should match the one of the initial quote.", savedQuoteData.getVersion(),
				canceledQuoteData.getVersion());
		Assert.assertEquals("Canceled quote should be in canceled state.", QuoteState.CANCELLED, canceledQuoteData.getState());

		Assert.assertNull("Quote cart if any should have been removed",
				commerceCartService.getCartForCodeAndUser(editQuoteCartCode, user));
		Assert.assertNull("Session cart should not be quote cart", cartService.getSessionCart().getQuoteReference());
	}

	protected QuoteData getQuoteForCode(final String quoteCode)
	{
		final List<QuoteData> selectedQuoteDataList = quoteFacade.getPagedQuotes(createPageableData()).getResults().stream()
				.filter(qd -> qd.getCode().equals(quoteCode)).collect(Collectors.toList());
		Assert.assertEquals("There should be a QuoteData object matching the quote.", 1, selectedQuoteDataList.size());

		return selectedQuoteDataList.get(0);
	}

	@Test
	public void shouldCancelNonEditableQuote()
	{
		final CartModel sessionCartModel = cartService.getSessionCart();
		final String quoteCode = RandomStringUtils.randomAlphabetic(12);
		final QuoteModel quoteModel = createSampleQuote(quoteCode, QuoteState.BUYER_OFFER);
		final Integer quoteVersion = quoteModel.getVersion();

		quoteFacade.cancelQuote(quoteCode);

		final QuoteData canceledQuoteData = getQuoteForCode(quoteCode);

		Assert.assertEquals("Canceled quote's version should match the one of the initial quote.", quoteVersion,
				canceledQuoteData.getVersion());
		Assert.assertEquals("Canceled quote should be in canceled state.", QuoteState.CANCELLED, canceledQuoteData.getState());

		Assert.assertTrue("Session cart should be available.", cartService.hasSessionCart());

		final CartModel newSessionCartModel = cartService.getSessionCart();
		Assert.assertEquals("No session cart should have been created.", sessionCartModel.getGuid(), newSessionCartModel.getGuid());
	}

	@Test
	public void shouldCancelQuoteAndUpdateQuoteWithLatestCartContent() throws CommerceCartModificationException
	{
		final QuoteData savedQuoteData = createQuoteFromSessionCart();
		final String quoteCode = savedQuoteData.getCode();
		final long qty = 10L;
		quoteFacade.enableQuoteEdit(quoteCode);

		long productQtyExpected = 0;
		final Optional<OrderEntryData> productEntry = getOrderEntryForProduct(cartFacade.getSessionCart(), PRODUCT_CODE);
		if (productEntry.isPresent())
		{
			productQtyExpected = productQtyExpected + productEntry.get().getQuantity();
		}

		final CartModificationData modificationData = cartFacade.addToCart(PRODUCT_CODE, qty);
		productQtyExpected = productQtyExpected + modificationData.getQuantityAdded();
		final CartData quoteCart = cartFacade.getSessionCart();

		quoteFacade.cancelQuote(quoteCode);

		final QuoteData canceledQuoteData = getQuoteForCode(quoteCode);

		Assert.assertNull("Quote cart should be removed", commerceCartService.getCartForCodeAndUser(quoteCart.getCode(), user));
		final Optional<OrderEntryData> quoteEntry = getOrderEntryForProduct(canceledQuoteData, PRODUCT_CODE);
		Assert.assertTrue("Canceled quote should contain the product added to session cart.", quoteEntry.isPresent());
		Assert.assertEquals("Canceled quote should contain the right quantity for the product added to session cart.",
				productQtyExpected, quoteEntry.get().getQuantity().longValue());
	}

	@Test
	public void testGetQuoteForCode() throws CommerceCartModificationException
	{
		CartModel sessionCart = cartService.getSessionCart();
		Long qty = Long.valueOf(1L);
		cartFacade.addToCart(PRODUCT_CODE, qty.longValue());
		final QuoteData initialQuoteData = createQuoteFromSessionCart();
		final String quoteCode = initialQuoteData.getCode();
		Assert.assertNotNull(initialQuoteData);
		Assert.assertEquals(1, initialQuoteData.getEntries().size());
		Assert.assertEquals(qty, initialQuoteData.getEntries().get(0).getQuantity());

		quoteFacade.enableQuoteEdit(quoteCode);
		sessionCart = cartService.getSessionCart();
		cartFacade.addToCart(PRODUCT_CODE, qty.longValue());
		modelService.save(sessionCart);
		qty = Long.valueOf(qty.longValue() + 1L);
		sessionCart = cartService.getSessionCart();

		quoteFacade.addComment("Test Comment");

		commerceQuoteService.updateQuoteFromCart(sessionCart, user);

		final QuoteData loadedQuoteData = quoteFacade.getQuoteForCode(initialQuoteData.getCode());
		Assert.assertNotNull("There should be a QuoteData object returned.", loadedQuoteData);
		Assert.assertEquals(qty, loadedQuoteData.getEntries().get(0).getQuantity());

		final List<CommentData> comments = loadedQuoteData.getComments();
		Assert.assertNotNull("Quote should have comments.", comments);
		Assert.assertEquals("Quote should have 1 comment.", 1, comments.size());
		Assert.assertEquals("Quote comment should match with what was saved.", "Test Comment", comments.get(0).getText());
	}

	@Test(expected = ModelNotFoundException.class)
	public void testGetQuoteForNonexistentCode()
	{
		final String randomString = RandomStringUtils.randomAlphabetic(12);
		quoteFacade.getQuoteForCode(randomString);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetQuoteForNullCode()
	{
		quoteFacade.getQuoteForCode(null);
	}

	protected Optional<OrderEntryData> getOrderEntryForProduct(final AbstractOrderData orderData, final String productCode)
	{
		final List<OrderEntryData> orderEntryDatas = orderData.getEntries().stream()
				.filter(entry -> entry.getProduct().getCode().equals(PRODUCT_CODE)).collect(Collectors.toList());

		Assert.assertTrue("Abstract order data should only have at most one entry for the searched product.",
				orderEntryDatas.size() <= 1);

		if (orderEntryDatas.isEmpty())
		{
			return Optional.empty();
		}
		else
		{
			return Optional.of(orderEntryDatas.get(0));
		}
	}

	@Test
	public void shouldSyncCartDataIntoQuote() throws CommerceCartModificationException
	{
		final QuoteData savedQuoteData = createQuoteFromSessionCart();
		final Integer latestVersion = savedQuoteData.getVersion();
		final String quoteCode = savedQuoteData.getCode();

		quoteFacade.enableQuoteEdit(quoteCode);

		final CartModel sessionCartModel = cartService.getSessionCart();

		final Long qty = Long.valueOf(1L);
		cartFacade.addToCart(PRODUCT_CODE, qty.longValue());
		modelService.save(sessionCartModel);

		final QuoteData quoteData = quoteFacade.newCart();

		Assert.assertNotNull(quoteData);
		Assert.assertEquals(quoteData.getEntries().get(0).getProduct().getCode(), PRODUCT_CODE);
		Assert.assertEquals(quoteData.getCode(), quoteCode);
		Assert.assertEquals(quoteData.getVersion(), latestVersion);
	}


	@Test
	public void shouldReturnTrueForIsQuoteSessionCartValidForCheckout() throws CommerceCartModificationException
	{
		final Long qty = Long.valueOf(1L);
		cartFacade.addToCart(PRODUCT_CODE, qty.longValue());
		final QuoteData initialQuoteData = createQuoteFromSessionCart();
		Assert.assertNotNull(initialQuoteData);
		Assert.assertEquals(1, initialQuoteData.getEntries().size());
		Assert.assertEquals(qty, initialQuoteData.getEntries().get(0).getQuantity());
		final QuoteModel quoteModel = commerceQuoteService.getQuoteByCodeAndCustomerAndStore((CustomerModel) user, user, baseStore,
				initialQuoteData.getCode());
		quoteModel.setState(QuoteState.BUYER_OFFER);
		quoteModel.setExpirationTime(DateUtils.addDays(Calendar.getInstance().getTime(), 5));
		modelService.save(quoteModel);
		quoteFacade.acceptAndPrepareCheckout(initialQuoteData.getCode());
		Assert.assertTrue("Quote cart should be valid for checkout", quoteFacade.isQuoteSessionCartValidForCheckout());
	}

	@Test
	public void shouldReturnFalseForIsQuoteSessionCartValidForCheckoutWhenNotQuoteCart() throws CommerceCartModificationException
	{
		final Long qty = Long.valueOf(1L);
		cartFacade.addToCart(PRODUCT_CODE, qty.longValue());
		final QuoteData initialQuoteData = createQuoteFromSessionCart();
		Assert.assertNotNull(initialQuoteData);
		Assert.assertEquals(1, initialQuoteData.getEntries().size());
		Assert.assertEquals(qty, initialQuoteData.getEntries().get(0).getQuantity());
		final QuoteModel quoteModel = commerceQuoteService.getQuoteByCodeAndCustomerAndStore((CustomerModel) user, user, baseStore,
				initialQuoteData.getCode());
		quoteModel.setState(QuoteState.BUYER_OFFER);
		quoteModel.setExpirationTime(DateUtils.addDays(Calendar.getInstance().getTime(), 5));
		modelService.save(quoteModel);
		quoteFacade.acceptAndPrepareCheckout(initialQuoteData.getCode());
		cartService.getSessionCart().setQuoteReference(null);
		Assert.assertFalse("Session cart should not be a quote cart", quoteFacade.isQuoteSessionCartValidForCheckout());
	}

	@Test
	public void shouldReturnFalseForIsQuoteSessionCartValidForCheckoutWhenQuoteCartModified()
			throws CommerceCartModificationException
	{
		final Long qty = Long.valueOf(1L);
		cartFacade.addToCart(PRODUCT_CODE, qty.longValue());
		final QuoteData initialQuoteData = createQuoteFromSessionCart();
		Assert.assertNotNull(initialQuoteData);
		Assert.assertEquals(1, initialQuoteData.getEntries().size());
		Assert.assertEquals(qty, initialQuoteData.getEntries().get(0).getQuantity());
		final QuoteModel quoteModel = commerceQuoteService.getQuoteByCodeAndCustomerAndStore((CustomerModel) user, user, baseStore,
				initialQuoteData.getCode());
		quoteModel.setState(QuoteState.BUYER_OFFER);
		quoteModel.setExpirationTime(DateUtils.addDays(Calendar.getInstance().getTime(), 5));
		modelService.save(quoteModel);
		quoteFacade.acceptAndPrepareCheckout(initialQuoteData.getCode());
		cartFacade.addToCart(PRODUCT_CODE, qty.longValue());
		Assert.assertFalse("Session cart should not be modified compared to quote",
				quoteFacade.isQuoteSessionCartValidForCheckout());
	}

	@Test
	public void shouldRemoveQuoteCartAndSessionParam()
	{
		final QuoteData quoteData = createQuoteFromSessionCart();
		final CartData cart = cartFacade.getSessionCart();
		Assert.assertNotNull(cart);
		Assert.assertNull(cart.getQuoteData());
		quoteFacade.enableQuoteEdit(quoteData.getCode());
		final CartData quoteCart = cartFacade.getSessionCart();
		quoteFacade.removeQuoteCart(quoteData.getCode());
		Assert.assertNull("Cart should be un-set in the session",
				sessionService.getAttribute(DefaultCartService.SESSION_CART_PARAMETER_NAME));
		Assert.assertNull("Quote Cart should be removed", commerceCartService.getCartForCodeAndUser(quoteCart.getCode(), user));
		Assert.assertNull("Quote should not have any cart attached",
				commerceQuoteService.getQuoteByCodeAndCustomerAndStore((CustomerModel) user, user, baseStore, quoteData.getCode())
						.getCartReference());
	}

	@Test
	public void shouldRemoveOnlyQuoteCartAndNotSessionCart()
	{
		final QuoteData quoteData = createQuoteFromSessionCart();
		final CartData cart = cartFacade.getSessionCart();
		Assert.assertNotNull(cart);
		Assert.assertNull(cart.getQuoteData());
		quoteFacade.enableQuoteEdit(quoteData.getCode());
		final CartData quoteCart = cartFacade.getSessionCart();
		sessionService.setAttribute(DefaultCartService.SESSION_CART_PARAMETER_NAME, null);
		cartFacade.getSessionCart();
		quoteFacade.removeQuoteCart(quoteData.getCode());

		Assert.assertNull("session cart should not be a quote cart", cartFacade.getSessionCart().getQuoteData());
		Assert.assertNull("Quote Cart should be removed", commerceCartService.getCartForCodeAndUser(quoteCart.getCode(), user));
		Assert.assertNull("Quote should not have any cart attached",
				commerceQuoteService.getQuoteByCodeAndCustomerAndStore((CustomerModel) user, user, baseStore, quoteData.getCode())
						.getCartReference());
	}

	@Test
	public void shouldNotRemoveQuoteCartAndSessionParam()
	{
		final QuoteData quoteData = createQuoteFromSessionCart();
		final CartData cart = cartFacade.getSessionCart();
		Assert.assertNotNull(cart);
		Assert.assertNull(cart.getQuoteData());
		final CartData quoteCart = cartFacade.getSessionCart();
		quoteFacade.removeQuoteCart(quoteData.getCode());
		Assert.assertNull("session cart should not be a quote cart", cartFacade.getSessionCart().getQuoteData());
		Assert.assertNotNull("session cart should not be removed",
				commerceCartService.getCartForGuidAndSiteAndUser(quoteCart.getGuid(), baseSite, user));
		Assert.assertNull("Quote should not have any cart attached",
				commerceQuoteService.getQuoteByCodeAndCustomerAndStore((CustomerModel) user, user, baseStore, quoteData.getCode())
						.getCartReference());
	}

	@Test
	public void shouldGetQuoteCountForCurrentUser()
	{
		createQuoteFromSessionCart();

		Assert.assertEquals("Should get 1 quote", Integer.valueOf(1), quoteFacade.getQuotesCountForCurrentUser());
	}

	@Test
	public void shouldRemoveDiscountsIfQuoteInBuyerOfferState() throws CommerceCartModificationException
	{
		user = userService.getUserForUID("quotecustomer");
		userService.setCurrentUser(user);

		final QuoteModel quoteModel = quoteService.getCurrentQuoteForCode("quote0");

		final double originalTotal = 57.95;

		Assert.assertTrue("Discounts are null",
				CollectionUtils.isNotEmpty(orderQuoteDiscountValuesAccessor.getQuoteDiscountValues(quoteModel)));
		Assert.assertEquals("Discount value should be ",
				Double.valueOf(orderQuoteDiscountValuesAccessor.getQuoteDiscountValues(quoteModel).get(0).getValue()),
				Double.valueOf(20.0d));

		quoteFacade.enableQuoteEdit(quoteModel.getCode());

		Assert.assertFalse("Discounts are not removed",
				CollectionUtils.isNotEmpty(orderQuoteDiscountValuesAccessor.getQuoteDiscountValues(quoteModel)));

		final QuoteData syncedQuoteData = quoteFacade.newCart();

		Assert.assertEquals("Cart total", BigDecimal.valueOf(originalTotal), syncedQuoteData.getTotalPrice().getValue());
	}
}
