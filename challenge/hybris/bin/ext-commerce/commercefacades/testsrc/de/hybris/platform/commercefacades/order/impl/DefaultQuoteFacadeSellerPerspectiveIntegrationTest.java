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
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.QuoteFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.order.CommerceQuoteAssignmentException;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteStateException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceQuoteService;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserTypeIdentificationStrategy;
import de.hybris.platform.commerceservices.order.strategies.impl.DefaultQuoteStateSelectionStrategy;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;


/**
 * Integration test for DefaultQuoteFacadeSellerPerspective
 */
@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:commerceservices/quote-spring-test.xml" })
public class DefaultQuoteFacadeSellerPerspectiveIntegrationTest extends BaseCommerceBaseTest
{
	private static final String TEST_BASESITE_UID = "testSite";
	public static final String SESSION_CART_PARAMETER_NAME = "cart";

	private final Map<String, String> originalConfigs = new HashMap<String, String>();

	@Resource
	private CartFacade cartFacade;
	@Resource
	private CartService cartService;
	@Resource
	private ProductService productService;
	@Resource
	private ModelService modelService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private UserService userService;
	@Resource
	private SessionService sessionService;
	@Resource
	private DefaultCommerceQuoteService commerceQuoteService;
	@Resource
	private DefaultQuoteStateSelectionStrategy quoteStateSelectionStrategy;
	@Resource
	private QuoteUserTypeIdentificationStrategy quoteUserTypeIdentificationStrategy;
	@Mock
	private QuoteUserTypeIdentificationStrategy mockQuoteUserTypeIdentificationStrategy;

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
		MockitoAnnotations.initMocks(this);

		commerceQuoteService.setQuoteUserTypeIdentificationStrategy(mockQuoteUserTypeIdentificationStrategy);
		quoteStateSelectionStrategy.setQuoteUserTypeIdentificationStrategy(mockQuoteUserTypeIdentificationStrategy);

		given(mockQuoteUserTypeIdentificationStrategy.getCurrentQuoteUserType(any())).willReturn(Optional.of(QuoteUserType.SELLER));

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
	public void restore()
	{
		commerceQuoteService.setQuoteUserTypeIdentificationStrategy(quoteUserTypeIdentificationStrategy);
		quoteStateSelectionStrategy.setQuoteUserTypeIdentificationStrategy(quoteUserTypeIdentificationStrategy);
		Config.setParameter(QUOTE_REQUEST_INITIATION_THRESHOLD + ".testSite.EUR",
				originalConfigs.get(QUOTE_REQUEST_INITIATION_THRESHOLD + ".testSite.EUR"));
	}

	protected QuoteData createQuoteFromSessionCart()
	{
		Assert.assertTrue(cartFacade.hasSessionCart());
		final CartModel sessionCartModel = cartService.getSessionCart();
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
		Assert.assertEquals("The quote state for a new quote should be SELLER_DRAFT", QuoteState.SELLER_DRAFT,
				newQuoteData.getState());
		Assert.assertEquals(
				"The cart data should be properly copied to create the quote.  The description field is used to assess that.",
				cartDescription, newQuoteData.getDescription());
	}

	@Test(expected = ModelNotFoundException.class)
	public void shouldNotCreateCartForInexistentQuote()
	{
		final String randomString = RandomStringUtils.randomAlphabetic(12);
		quoteFacade.createCartFromQuote(randomString);
	}

	@Test
	public void shouldLoadSellerDraftQuoteInCart() throws CommerceSaveCartException, CommerceQuoteAssignmentException
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
		Assert.assertEquals("The quote state should match with previously created quote", QuoteState.SELLER_DRAFT,
				sessionQuoteData.getState());

	}

	@Test
	public void shouldSellerGetPagedQuotes()
	{
		final QuoteData newQuoteData = createQuoteFromSessionCart();

		final SearchPageData<QuoteData> selectedQuotes = quoteFacade.getPagedQuotes(createPageableData());

		final List<QuoteData> selectedQuoteDataList = selectedQuotes.getResults().stream()
				.filter(qd -> qd.getCode().equals(newQuoteData.getCode())).collect(Collectors.toList());

		Assert.assertEquals("There should be a QuoteData object matching the quote created from the session cart.", 1,
				selectedQuoteDataList.size());
		Assert.assertEquals("The quote state for the selected quote should be SELLER_DRAFT", QuoteState.SELLER_DRAFT,
				selectedQuoteDataList.get(0).getState());
	}

	@Test
	public void shouldShowMultipleVersionsOnlyOnce()
	{
		// create 3 versions of the same quote
		final String quoteCode = "testQuote";
		createSampleQuote(quoteCode, QuoteState.BUYER_SUBMITTED, Integer.valueOf(1));
		createSampleQuote(quoteCode, QuoteState.SELLER_REQUEST, Integer.valueOf(2));
		createSampleQuote(quoteCode, QuoteState.SELLER_DRAFT, Integer.valueOf(3));

		final SearchPageData<QuoteData> selectedQuotes = quoteFacade.getPagedQuotes(createPageableData());
		Assert.assertNotNull(selectedQuotes);
		Assert.assertNotNull(selectedQuotes.getResults());
		// only one version should be loaded from search, and it should be the latest
		Assert.assertEquals("There should be exactly 1 quote returned", 1L, selectedQuotes.getResults().size());
		Assert.assertEquals(Integer.valueOf(3), selectedQuotes.getResults().get(0).getVersion());
	}

	@Test
	public void shouldSubmitQuoteChangeStatus() throws CommerceSaveCartException, CommerceQuoteAssignmentException
	{
		final QuoteData savedQuoteData = createQuoteFromSessionCart();
		final String quoteCode = savedQuoteData.getCode();

		quoteFacade.enableQuoteEdit(quoteCode);

		// set subtotal over threshold
		cartService.getSessionCart().setSubtotal(Double.valueOf(2.0));

		quoteFacade.submitQuote(quoteCode);
		final QuoteData submitedQuote = getQuoteByCode(quoteCode);
		Assert.assertNotNull("There should be a submited quote.", submitedQuote);
		Assert.assertEquals(QuoteState.SELLER_SUBMITTED, submitedQuote.getState());
		Assert.assertEquals(savedQuoteData.getVersion().intValue(), submitedQuote.getVersion().intValue());
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldNotSubmitAlreadySubmittedQuote() throws CommerceQuoteAssignmentException
	{
		final String randomString = RandomStringUtils.randomAlphabetic(12);
		createSampleQuote(randomString, QuoteState.SELLER_SUBMITTED);
		// set subtotal over threshold
		cartService.getSessionCart().setSubtotal(Double.valueOf(2.0));
		quoteFacade.submitQuote(randomString);
	}

	@Test
	public void shouldUpdateQuantitiesOnSaveQuote() throws CommerceSaveCartException, CommerceQuoteAssignmentException
	{
		CartModel sessionCart = cartService.getSessionCart();
		final ProductModel product = productService.getProductForCode("HW1210-3422");
		Long qty = new Long(1L);
		cartService.addNewEntry(sessionCart, product, qty.longValue(), null);
		final QuoteData initialQuoteData = createQuoteFromSessionCart();
		Assert.assertNotNull(initialQuoteData);
		Assert.assertEquals(1, initialQuoteData.getEntries().size());
		Assert.assertEquals(qty, initialQuoteData.getEntries().get(0).getQuantity());

		quoteFacade.enableQuoteEdit(initialQuoteData.getCode());
		sessionCart = cartService.getSessionCart();
		cartService.addNewEntry(sessionCart, product, 1L, null);
		modelService.save(sessionCart);
		qty = new Long(qty.longValue() + 1L);
		sessionCart = cartService.getSessionCart();

		commerceQuoteService.updateQuoteFromCart(sessionCart, user);

		final QuoteData currentQuoteData = getQuoteByCode(initialQuoteData.getCode());
		Assert.assertNotNull(currentQuoteData);
		Assert.assertEquals(qty, currentQuoteData.getEntries().get(0).getQuantity());
	}

	@Test(expected = IllegalQuoteStateException.class)
	public void shouldNotSaveQuoteInInvalidState() throws CommerceQuoteAssignmentException
	{
		final QuoteModel quote = createSampleQuote("testQuote", QuoteState.SELLER_SUBMITTED);

		final CartModel sessionCart = cartService.getSessionCart();
		final ProductModel product = productService.getProductForCode("HW1210-3422");
		final Long qty = new Long(1L);
		cartService.addNewEntry(sessionCart, product, qty.longValue(), null);
		sessionCart.setQuoteReference(quote);
		modelService.save(sessionCart);
		commerceQuoteService.updateQuoteFromCart(sessionCart, user);
	}
}
