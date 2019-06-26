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
package de.hybris.platform.commercefacades.groovy

import de.hybris.platform.commercefacades.ObjectXStreamAliasConverter

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartParameterData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartResultData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.dao.SaveCartDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.core.model.product.UnitModel
import de.hybris.platform.core.model.user.CustomerModel
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.model.PriceRowModel
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification;
import de.hybris.platform.servicelayer.i18n.CommonI18NService
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.springframework.stereotype.Component;


/**
 *
 * Keywords from the old ATDD tests - now called from Spock tests
 *
 */
@Component
public abstract class AbstractCommerceFacadesSpockTest extends ServicelayerTransactionalSpockSpecification {
	private static final Logger LOG = Logger.getLogger(AbstractCommerceFacadesSpockTest.class);
	private static final double DELTA = 0.01;
	private static final String ANONYMOUS_UID = "anonymous";
	public static final String SESSION_CART_PARAMETER_NAME = "cart";
	public static final String SITE = "testSite";

	@Resource
	protected ObjectXStreamAliasConverter xStreamAliasConverter;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private UnitService unitService;

	@Resource
	private CheckoutFacade checkoutFacade;

	@Resource
	private UserFacade userFacade;

	@Resource
	private CartService cartService;

	@Resource
	private CartFacade cartFacade;

	@Resource
	private UserService userService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private ModelService modelService;

	@Resource
	private I18NService i18nService;

	@Resource
	private SessionService sessionService;

	@Resource
	private SaveCartFacade saveCartFacade;

	@Resource
	private CommerceCartService commerceCartService;

	@Resource
	private SaveCartDao saveCartDao;

	@Resource
	private CustomerFacade customerFacade;

	private final Random random = new Random();

	public CustomerModel createCustomer(final String id) {
		final CustomerModel user = modelService.create(CustomerModel.class);
		user.setUid(id);
		user.setSessionCurrency(commonI18NService.getCurrency("USD"));
		user.setSessionLanguage(commonI18NService.getLanguage("en"));
		modelService.save(user);
		return user;
	}

	public ProductModel createProduct(final String code, final String unit, final String currency, final String price) {
		final UnitModel unitModel = unitService.getUnitForCode(unit);

		final ProductModel productModel = modelService.create(ProductModel.class);
		productModel.setCode(code);
		productModel.setUnit(unitModel);
		modelService.save(productModel);

		final PriceRowModel priceModel = modelService.create(PriceRowModel.class);
		priceModel.setProduct(productModel);
		priceModel.setCurrency(commonI18NService.getCurrency(currency));
		priceModel.setPrice(Double.parseDouble(price));
		priceModel.setUnit(unitModel)
		modelService.save(priceModel);

		return productModel;
	}

	public ProductModel createProduct(final String code) {
		final ProductModel productModel = modelService.create(ProductModel.class);
		productModel.setCode(code);
		modelService.save(productModel);
		return productModel;
	}


	public void updateUserDetails() throws DuplicateUidException {
		final Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
		calendar.setTime(new Date());

		final AddressData address = new AddressData();
		address.setId(UUID.randomUUID().toString());
		address.setShippingAddress(true);
		address.setDefaultAddress(true);
		address.setBillingAddress(true);
		address.setVisibleInAddressBook(true);

		final CCPaymentInfoData creditCard = new CCPaymentInfoData();
		creditCard.setId(String.valueOf(random.nextInt(Integer.MAX_VALUE)));
		creditCard.setAccountHolderName("HOLDER NAME");
		creditCard.setIssueNumber("123");
		creditCard.setBillingAddress(address);
		creditCard.setCardNumber("1111111111111111");
		creditCard.setCardType("visa");
		creditCard.setDefaultPaymentInfo(true);
		creditCard.setExpiryMonth(String.valueOf(calendar.get(Calendar.MONTH)));
		creditCard.setExpiryYear(String.valueOf(calendar.get(Calendar.YEAR) + 3));

		userFacade.setDefaultAddress(address);
		userFacade.setDefaultPaymentInfo(creditCard);
	}

	def boolean resultCartDataMatchesSessionCartData(CartData cartData)  {
		CartModel sessionCart = getCurrentSessionCart()
		cartData.code == sessionCart.code &&
				cartData.name == sessionCart.name &&
				cartData.description == sessionCart.description &&
				cartData.saveTime == sessionCart.saveTime &&
				cartData.expirationTime == sessionCart.expirationTime &&
				cartData.savedBy.uid == sessionCart.savedBy.uid
	}

	public OrderData doCheckout() throws InvalidCartException {
		baseSiteService.setCurrentBaseSite("testSite", true);
		final OrderData order = checkoutFacade.placeOrder();
		assertNotNull("CheckoutFacade#placeOrder returned null", order);
		return order;
	}

	public void validateSessionCart() throws InvalidCartException, CommerceCartModificationException {
		final List<CartModificationData> cartModificationData = cartFacade.validateCartData();
		if (CollectionUtils.isNotEmpty(cartModificationData)) {
			throw new InvalidCartException("Cart is not valid:\n");
		}
	}

	protected String getProductCode(final CartModificationData data) {
		if (data.getEntry() == null) {
			return "null entry";
		}
		if (data.getEntry().getProduct() == null) {
			return "null";
		}
		return data.getEntry().getProduct().getCode();
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>add product to cart once</i>
	 * <p>
	 *
	 * @param productCode
	 *           the code of the product to add
	 */
	public void addProductToCartOnce(final String productCode) {
		addProductToCart(productCode, 1);
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>add product to cart</i>
	 * <p>
	 *
	 * @param productCode
	 *           the code of the product to add
	 *
	 * @param quantity
	 *           the number of units to add
	 */
	public void addProductToCart(final String productCode, final long quantity) {
		try {
			assertEquals(quantity, cartFacade.addToCart(productCode, quantity).getQuantity());
		}
		catch (final Exception e) {
			// catch any exceptions that would get swallowed by the robot framework and log them
			LOG.error("An exception occured while adding a product to cart", e);
			fail(e.getMessage());
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>delete cart entry</i>
	 * <p>
	 *
	 * @param entryNumber
	 *           the entry number to delete
	 */
	public void deleteCartEntry(final long entryNumber) {
		try {
			assertEquals(0, cartFacade.updateCartEntry(entryNumber, 0).getQuantity());
		}
		catch (final CommerceCartModificationException e) {
			LOG.error("An exception occured while deleting a cart entry", e);
			fail(e.getMessage());
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>remove product from cart</i>
	 * <p>
	 *
	 * @param productCode
	 *           the code of the product to remove
	 */
	public void removeProductFromCart(final String productCode) {
		for (final OrderEntryData entry : cartFacade.getSessionCart().getEntries()) {
			if (productCode.equals(entry.getProduct().getCode())) {
				deleteCartEntry(entry.getEntryNumber().longValue());
				break;
			}
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>verify cart total</i>
	 * <p>
	 *
	 * @param expectedTotal
	 *           the expected order total for the billing event
	 */
	public void verifyCartTotal(final double expectedTotal) {
		final CartModel sessionCart = cartService.getSessionCart();
		assertNotNull("The session cart is null", sessionCart);

		try {
			final double orderTotal = sessionCart.getTotalPrice().doubleValue();
			assertEquals("The order total for does not match the expected value", expectedTotal, orderTotal, DELTA);
		}
		catch (final Exception e) {
			LOG.error("An exception occured while calculating the order total", e);
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>set current base site</i>
	 * <p>
	 *
	 * @param baseSiteUid
	 *           the unique base site ID
	 */
	public void setCurrentBaseSite(final String baseSiteUid) {
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(baseSiteUid);
		baseSiteService.setCurrentBaseSite(baseSite, true);
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>prepare session</i>
	 * <p>
	 *
	 * @param baseSiteUid
	 *           the unique base site ID
	 */
	public void prepareSession(final String baseSiteUid) {
		setCurrentBaseSite(baseSiteUid);
		setCartUser(ANONYMOUS_UID);

		final Locale locale = i18nService.getCurrentLocale();
		sessionService.setAttribute("ATDD-Locale", locale);
		i18nService.setCurrentLocale(Locale.US);
	}

	/**
	 * Java implementation of the robot keyword <br/>
	 * <p>
	 * <i>reset system attributes</i>
	 * </p>
	 */
	public void resetSystemAttributes() {
		Locale locale = sessionService.getAttribute("ATDD-locale");

		if (locale == null) {
			locale = Locale.US;
		}

		i18nService.setCurrentLocale(locale);
		sessionService.removeAttribute("ATDD-Locale");
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>login</i>
	 * <p>
	 *
	 * @param userUID
	 *           the unique user ID
	 */
	public void login(final String userUID) {
		setCartUser(userUID);
	}

	/**
	 * Sets the cart user and the session currency
	 *
	 * @param userUID
	 *           the unique user ID
	 */
	protected void setCartUser(final String userUID) {
		final UserModel user = userService.getUserForUID(userUID);
		final CurrencyModel currency = setCurrencyAndBaseSite(user);

		// avoids that the cart is calculated for the wrong user
		//modelService.refresh(cartService.getSessionCart());
		final CartModel cartModel = cartService.getSessionCart();
		modelService.refresh(cartModel);

		// FIXME: this workaround updates the cart assigning to it the current user.
		// the previous invocation "cartService.changeCurrentCartUser(user)" should have done this, but it doesn't.
		cartModel.setUser(user);
		modelService.save(cartModel);

		// adding currency to session manually is only a workaround
		JaloSession.getCurrentSession().getSessionContext().setCurrency((Currency) modelService.toPersistenceLayer(currency));
		JaloSession.getCurrentSession().getSessionContext().setUser((User) modelService.getSource(user));

	}

	protected CurrencyModel setCurrencyAndBaseSite(final UserModel user) {
		BaseSiteModel baseSite = baseSiteService.getCurrentBaseSite();
		CurrencyModel currency = user.getSessionCurrency();
		if (baseSite == null) {
			final Collection<BaseSiteModel> allBaseSites = baseSiteService.getAllBaseSites();
			assertTrue("No base site was found. Please review your sample data!", CollectionUtils.isNotEmpty(allBaseSites));
			baseSite = allBaseSites.iterator().next(); //NOSONAR
			setCurrentBaseSite(baseSite.getUid());
		}
		if (currency == null) {
			final List<BaseStoreModel> baseStores = baseSite.getStores();
			assertTrue("No base store was found. Please review your sample data!", CollectionUtils.isNotEmpty(baseStores));
			final BaseStoreModel baseStore = baseStores.iterator().next(); //NOSONAR
			final Set<CurrencyModel> allCurrencies = baseStore.getCurrencies();
			assertTrue(
					String.format("No currency was found for base store: %s. Please review your sample data!", baseStore.getUid()),
					CollectionUtils.isNotEmpty(allCurrencies));
			currency = allCurrencies.iterator().next(); //NOSONAR
		}
		assertNotNull("No currency was found. Please review your sample data!", currency);

		LOG.info(String.format("Setting cart user [%s] and currency [%s]", user.getUid(), currency.getIsocode()));

		cartService.changeCurrentCartUser(user);
		cartService.changeSessionCartCurrency(currency);
		return currency;
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>createCustomerWithHybrisApi</i>
	 * <p>
	 *
	 * @param login
	 *           the unique user ID
	 * @param password
	 *           password
	 * @param titleCode
	 *           title code
	 * @param firstName
	 *           first name
	 * @param lastName
	 *           lastName
	 */
	public void createCustomerWithHybrisApi(final String login, final String password, final String titleCode,
			final String firstName, final String lastName) {
		final UserModel user = userService.getCurrentUser();
		final CurrencyModel currency = setCurrencyAndBaseSite(user);

		// adding currency to session manually is only a workaround
		JaloSession.getCurrentSession().getSessionContext().setCurrency((Currency) modelService.toPersistenceLayer(currency));
		JaloSession.getCurrentSession().getSessionContext().setUser((User) modelService.getSource(user));

		final RegisterData registerData = new RegisterData();
		registerData.setFirstName(firstName);
		registerData.setLastName(lastName);
		registerData.setLogin(login);
		registerData.setPassword(password);
		registerData.setTitleCode(titleCode);

		try
		{
			customerFacade.register(registerData);
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>save cart with name and description</i>
	 * <p>
	 *
	 * @param name
	 *           name of the cart to be saved
	 * @param description
	 *           description of the cart to be saved
	 * @return the saved cart
	 * @throws CommerceSaveCartException
	 */
	public CartData saveCartWithNameAndDescription(final String name, final String description) throws CommerceSaveCartException
	{
		return saveGivenCartWithNameAndDescription(null, name, description);
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>save given cart with name and description</i>
	 * <p>
	 *
	 * @param cartId
	 *           id/code of the cart to be saved
	 * @param name
	 *           name of the cart to be saved
	 * @param description
	 *           description of the cart to be saved
	 * @return the saved cart
	 * @throws CommerceSaveCartException
	 */
	public CartData saveGivenCartWithNameAndDescription(final String cartId, final String name, final String description)
			throws CommerceSaveCartException
	{
		final CommerceSaveCartParameterData parameters = new CommerceSaveCartParameterData();
		if (StringUtils.isEmpty(cartId))
		{
			getCurrentSessionCart().setSite(baseSiteService.getBaseSiteForUID("testSite"));
		}
		parameters.setCartId(cartId);
		parameters.setName(name);
		parameters.setDescription(description);
		parameters.setEnableHooks(true);
		final CommerceSaveCartResultData saveCartResultData = saveCartFacade.saveCart(parameters);
		final CartData savedCart = saveCartResultData.getSavedCartData();

		return savedCart;
	}

	/**
	 * Keyword implementation for flagging a saved cart for deletion
	 *
	 * @param cartToBeFlagged
	 *           the saved cart to be flagged for deletion
	 * @return cart data
	 * @throws CommerceSaveCartException
	 */
	public CartData flagForDeletion(final CartData cartToBeFlagged) throws CommerceSaveCartException
	{
		return flagCartForDeletion(cartToBeFlagged.getCode());
	}

	/**
	 * Keyword implementation for flagging a saved cart for deletion
	 *
	 * @param cartCode
	 *           the code of the saved cart to be flagged for deletion
	 * @return cart data
	 * @throws CommerceSaveCartException
	 */
	public CartData flagCartForDeletion(final String cartCode) throws CommerceSaveCartException
	{
		return saveCartFacade.flagForDeletion(cartCode).getSavedCartData();
	}

	/**
	 * Keyword implementation for retrieve saved cart with cart code
	 *
	 * @param code
	 *           the cart code to be retrieved
	 * @return a cart data
	 * @throws CommerceSaveCartException
	 */
	public CartData retrieveSavedCartWithCode(final String code) throws CommerceSaveCartException
	{
		final CommerceSaveCartParameterData parameters = new CommerceSaveCartParameterData();
		parameters.setCartId(code);
		parameters.setEnableHooks(true);
		return saveCartFacade.getCartForCodeAndCurrentUser(parameters).getSavedCartData();
	}


	/**
	 * Keyword implementation for retrieve saved cart with cart code
	 *
	 * @param code
	 *           the cart code to be retrieved
	 * @return a cart restoration data
	 * @throws CommerceSaveCartException
	 */
	public CartRestorationData restoreSavedCartWithCode(final String code) throws CommerceSaveCartException
	{
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID("testSite"), false);

		final CommerceSaveCartParameterData parameters = new CommerceSaveCartParameterData();
		parameters.setCartId(code);
		parameters.setEnableHooks(true);
		return saveCartFacade.restoreSavedCart(parameters);
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>get session cart</i>
	 * <p>
	 *
	 * @return the session cart
	 */
	public CartModel getCurrentSessionCart()
	{
		return cartService.getSessionCart();
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>create new session cart</i>
	 * <p>
	 *
	 * @return the new session cart
	 */
	public CartModel removeAndCreateNewSessionCart()
	{
		sessionService.removeAttribute(SESSION_CART_PARAMETER_NAME);
		return cartService.getSessionCart();
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>verify that date1 is n days later than date2</i>
	 * <p>
	 *
	 * @param date1
	 *           date that is expected to be n days later than date2
	 * @param days
	 *           number of days
	 * @param date2
	 *           base date
	 */
	public void verifyThatDate1IsNDaysLaterThanDate2(final Date date1, final int days, final Date date2)
	{
		final Date expectedDate = new DateTime(date2).plusDays(days).toDate();
		assertEquals(expectedDate, date1);
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>get saved cart from list</i>
	 * </p>
	 *
	 * @param savedCartList
	 */
	public CartData getSavedCartFromList(final ArrayList<CartData> savedCartList, final String code)
	{
		if (savedCartList != null && savedCartList.size() > 0)
		{
			for (final CartData savedCart : savedCartList)
			{
				if (StringUtils.equals(code, savedCart.getCode()))
				{
					return savedCart;
				}
			}
		}

		return null;
	}

	/**
	 * Implementation of the robot keyword <br>
	 * <p>
	 * <i>verify equal list size</i>
	 * <p>
	 *
	 * Compares the size of the given list to an expected value
	 *
	 * @param list
	 *           the list whose size should be compared
	 * @param expectedSize
	 *           expected value
	 */
	public void verifyListSizeEquals(final List<Object> list, final int expectedSize)
	{
		final String errorMsg = "Number of elements in given list %d does not match expected value %d";
		assertEquals(String.format(errorMsg, Integer.valueOf(list.size()), Integer.valueOf(expectedSize)), expectedSize,
				list.size());
	}

	/**
	 * Implementation of the robot keyword <br>
	 * <p>
	 * <i>get saved carts to remove</i>
	 * </p>
	 *
	 * Determines the saved carts whose expiration date exceeds the given value.
	 *
	 * @return a list of saved carts to remove
	 */
	public List<CartModel> getSavedCartsToRemove()
	{
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("testSite");
		return saveCartDao.getSavedCartsForRemovalForSite(baseSite);
	}

	/**
	 * Implementation of the robot keyword <br>
	 * <p>
	 * <i>decrease saved carts expiration date</i>
	 * </p>
	 *
	 * For a saved cart the expiration date is set to a value which causes a finding in the unsaveDao.
	 *
	 */
	public void decreaseSavedCartsExpirationDate()
	{
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("testSite");
		final UserModel user = userService.getCurrentUser();
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(100);
		pageableData.setCurrentPage(0);
		final List<CartModel> cartModels = saveCartDao.getSavedCartsForSiteAndUser(pageableData, baseSite, user, null).getResults();

		for (final CartModel cartModel : cartModels)
		{
			final DateTime date = new DateTime(cartModel.getExpirationTime());

			cartModel.setExpirationTime(date.minusDays(31).toDate());
			modelService.save(cartModel);
		}
	}

	/**
	 * Implementation of the robot keyword <br>
	 * <p>
	 * <i>get saved carts for current user</i>
	 * </p>
	 *
	 * Lists all saved carts for the current user.
	 *
	 * @return lists of saved carts
	 */
	public List<CartData> getSavedCartsForCurrentUser()
	{
		return getListOfSavedCarts("");
	}

	public List<CartData> getListOfSavedCarts(final String statusList)
	{
		setCurrentBaseSite("testSite");
		final List<OrderStatus> orderStatus = new ArrayList<>();
		if (StringUtils.isNotEmpty(statusList))
		{
			for (final String status : statusList.split(","))
			{
				orderStatus.add(OrderStatus.valueOf(status.trim()));
			}
		}
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(100);
		pageableData.setCurrentPage(0);
		return saveCartFacade.getSavedCartsForCurrentUser(pageableData, orderStatus).getResults();
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>clone saved cart with code</i>
	 * <p>
	 */
	public CartData cloneSavedCart(final String cartCode) {
		return cloneSavedCart(cartCode, null, null);
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>clone saved cart with code</i>
	 * <p>
	 */
	public CartData cloneSavedCart(final String cartCode, final String name, final String description)
			throws CommerceSaveCartException
	{
		final CommerceSaveCartParameterData commerceSaveCartParameterData = new CommerceSaveCartParameterData();
		commerceSaveCartParameterData.setCartId(cartCode);
		commerceSaveCartParameterData.setName(name);
		commerceSaveCartParameterData.setDescription(description);
		commerceSaveCartParameterData.setEnableHooks(true);
		return saveCartFacade.cloneSavedCart(commerceSaveCartParameterData).getSavedCartData();
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>verify cart clone</i>
	 * <p>
	 */
	public void verifyCartClone(final String originalCartCode, final String clonedCartCode)
	{
		try
		{
			assertFalse("Cloned cart has the same [code] as the original cart", originalCartCode.equals(clonedCartCode));

			final CartModel originalCartModel = commerceCartService.getCartForCodeAndUser(originalCartCode,
					userService.getCurrentUser());
			final CartModel clonedCartModel = commerceCartService.getCartForCodeAndUser(clonedCartCode,
					userService.getCurrentUser());

			assertEquals("Cloned cart does not have the same [user] as the original cart", originalCartModel.getUser().getUid(),
					clonedCartModel.getUser().getUid());
			assertEquals("Cloned cart does not have the same [currency] as the original cart", originalCartModel.getCurrency(),
					clonedCartModel.getCurrency());
			assertEquals("Cloned cart does not have the same [totalPrice] as the original cart", originalCartModel.getTotalPrice(),
					clonedCartModel.getTotalPrice(), 0.0);
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>verify cloned cart entries</i>
	 * <p>
	 */
	public void verifyClonedCartEntries(final String originalCartCode, final String clonedCartCode)
	{
		try
		{
			final CartModel originalCartModel = commerceCartService.getCartForCodeAndUser(originalCartCode,
					userService.getCurrentUser());
			final CartModel clonedCartModel = commerceCartService.getCartForCodeAndUser(clonedCartCode,
					userService.getCurrentUser());

			if (originalCartModel.getEntries() == null)
			{
				assertNull(clonedCartModel.getEntries());
				return;
			}

			assertEquals("Cloned cart does not have the same number of cart entries as the original cart.",
					originalCartModel.getEntries().size(), clonedCartModel.getEntries().size());


			for (int i = 0; i < clonedCartModel.getEntries().size(); i++)
			{
				final CartEntryModel originalCartEntryModel = (CartEntryModel) originalCartModel.getEntries().get(i);
				final CartEntryModel clonedCartEntryModel = (CartEntryModel) clonedCartModel.getEntries().get(i);

				// verify entry number is equal
				assertEquals("Cloned cart entry does not have the same [entry numer] as the original cart entry",
						originalCartEntryModel.getEntryNumber(), clonedCartEntryModel.getEntryNumber());

				// verify PK is not equal
				assertFalse(
						String.format("Cart entry with number %d has not been deep cloned", originalCartEntryModel.getEntryNumber()),
						originalCartEntryModel.getPk().getLongValue() == clonedCartEntryModel.getPk().getLongValue());

				// verify product is the same
				assertEquals(
						String.format("Cloned cart entry with number %d does not have the same [product] as the original cart entry",
						originalCartEntryModel.getEntryNumber()),
						originalCartEntryModel.getProduct().getCode(), clonedCartEntryModel.getProduct().getCode());

				// verify unit is the same
				assertEquals(
						String.format("Cloned cart entry with number %d does not have the same [unit] as the original cart entry",
						originalCartEntryModel.getEntryNumber()),
						originalCartEntryModel.getUnit().getCode(), clonedCartEntryModel.getUnit().getCode());

				// verify quantity is equal
				assertEquals(
						String.format("Cloned cart entry with number %d does not have the same [quantity] as the original cart entry",
						originalCartEntryModel.getEntryNumber()),
						originalCartEntryModel.getQuantity(), clonedCartEntryModel.getQuantity());

				// verify base price is equal
				assertEquals(
						String.format("Cloned cart entry with number %d does not have the same [base price] as the original cart entry",
						originalCartEntryModel.getEntryNumber()),
						originalCartEntryModel.getBasePrice(), clonedCartEntryModel.getBasePrice());

				// verify total price is equal
				assertEquals(
						String.format(
						"Cloned cart entry with number %d does not have the same [total price] as the original cart entry",
						originalCartEntryModel.getEntryNumber()),
						originalCartEntryModel.getTotalPrice(), clonedCartEntryModel.getTotalPrice());

				// verify delivery address is the same
				assertEquals(
						String.format(
						"Cloned cart entry with number %d does not have the same [delivery address] as the original cart entry",
						originalCartEntryModel.getEntryNumber()),
						originalCartEntryModel.getDeliveryAddress(), clonedCartEntryModel.getDeliveryAddress());

				// verify delivery mode is the same
				assertEquals(
						String.format(
						"Cloned cart entry with number %d does not have the same [delivery mode] as the original cart entry",
						originalCartEntryModel.getEntryNumber()),
						originalCartEntryModel.getDeliveryMode(), clonedCartEntryModel.getDeliveryMode());
			}
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	//	/**
	//	 * Java implementation of the robot keyword <br>
	//	 * <p>
	//	 * <i>get configurations for order entry</i>
	//	 * <p>
	//	 */
	//	public List<ConfigurationInfoData> getConfigurationsForOrderEntry(final Integer entryNumber, final OrderData order)
	//	{
	//
	//		assertNotNull("Order must not be null", order);
	//		assertTrue("Entry number must not be a negative number", entryNumber.intValue() >= 0);
	//
	//		assertFalse("Order should not be empty", order.getEntries().isEmpty());
	//		assertTrue("Order entry number (" + entryNumber + ") must not be greater than number of orderEntries in the order ("
	//				+ order.getEntries().size() + ")", order.getEntries().size() > entryNumber.intValue());
	//
	//		final List<OrderEntryData> orderEntries = order.getEntries();
	//		final Optional<OrderEntryData> first = orderEntries.stream().filter(entry -> entryNumber.equals(entry.getEntryNumber()))
	//				.findFirst();
	//		if (first.isPresent())
	//		{
	//			return first.get().getConfigurationInfos();
	//		}
	//		fail("No entry with given number in the order");
	//		return null;
	//	}

	/**
	 * Java implementation of {@code get configuration for entry ...}.
	 *
	 * @param entry
	 *           order entry
	 * @param label
	 *           configuration info label, e.g. fontsize
	 * @return configuration info object or null if info was not found
	 */
	public ConfigurationInfoData getConfigurationForOrderEntry(final OrderEntryData entry, final String label)
	{
		assertNotNull("Order must not be null", entry);
		assertNotNull("Label must not be null", label);
		final ConfigurationInfoData result = getConfigurationByLabel(entry, label);
		if (result == null)
		{
			throw new IllegalArgumentException("Configuration with label '" + label + "' was not found.");
		}
		return result;
	}

	/**
	 * Find entry by entry number.
	 *
	 * @param order
	 *           order data
	 * @param entryNumber
	 *           entry number
	 * @return entry or null
	 */
	public OrderEntryData getEntryByNumber(final AbstractOrderData order, final Integer entryNumber)
	{
		final List<OrderEntryData> entries = order.getEntries();
		if (entries != null)
		{
			for (final OrderEntryData entry : entries)
			{
				if (entryNumber.equals(entry.getEntryNumber()))
				{
					return entry;
				}
			}
		}
		return null;
	}

	/**
	 * Get session cart. There is similar method
	 * {@link de.hybris.platform.CommerceFacadesTestFixtures.keywords.CommerceServicesKeywordLibrary#getCurrentSessionCart()}
	 * , but we need {@code CartData} rather that {@code CartModel}.
	 *
	 * @return session cart.
	 */
	public CartData getCartDTO()
	{
		final CartData sessionCart = cartFacade.getSessionCart();
		assertNotNull("No session cart", sessionCart);
		return sessionCart;
	}

	/**
	 * Change value of single {@code ConfigurationInfoData}.
	 *
	 * @param entry
	 *           entry that hold configurations (must be a part of session cart)
	 * @param label
	 *           configuration label
	 * @param value
	 *           new value to set to configuration
	 * @throws CommerceCartModificationException
	 * @throws IllegalArgumentException
	 *            if there is no configuration with given label
	 */
	public void updateConfigurationInfo(final OrderEntryData entry, final String label, final String value)
			throws CommerceCartModificationException
	{
		assertNotNull("Order entry must not be null", entry);
		assertNotNull("Label must not be null", label);

		for (final ConfigurationInfoData info : entry.getConfigurationInfos())
		{
			if (label.equals(info.getConfigurationLabel()))
			{
				info.setConfigurationValue(value);
				cartFacade.updateCartEntry(entry);
				return;
			}
		}

		throw new IllegalArgumentException("Configuration with label '" + label + "' was not found.");
	}

	protected ConfigurationInfoData getConfigurationByLabel(final OrderEntryData entry, final String label)
	{
		final List<ConfigurationInfoData> configurationInfos = entry.getConfigurationInfos();
		if (configurationInfos != null)
		{
			for (final ConfigurationInfoData info : configurationInfos)
			{
				if (label.equals(info.getConfigurationLabel()))
				{
					return info;
				}
			}
		}
		return null;
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>pickup product to cart</i>
	 * <p>
	 *
	 * @param productCode
	 *           the code of the product to add
	 *
	 * @param quantity
	 *           the number of units to add *
	 * @param storeId
	 *           id of store to pick up
	 */
	public void pickupProductToCart(final String productCode, final long quantity, final String storeId)
	{
		try
		{
			assertEquals(quantity, cartFacade.addToCart(productCode, quantity, storeId).getQuantity());
		}
		catch (final Exception e)
		{
			// catch any exceptions that would get swallowed by the robot framework and log them
			LOG.error("An exception occurred while adding a product to cart", e);
			fail(e.getMessage());
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>update point of service</i>
	 * <p>
	 *
	 * @param entry
	 *           order entry to update
	 *
	 * @param storeId
	 *           id of store to select for order entry
	 */
	public void updateEntryWithPointOfService(final OrderEntryData entry, final String storeId)
	{
		try
		{
			assertNotNull("Order entry must not be null", entry);
			assertNotNull("Order entry number must not be null", entry.getEntryNumber());
			cartFacade.updateCartEntry(entry.getEntryNumber().longValue(), storeId);
		}
		catch (final Exception e)
		{
			// catch any exceptions that would get swallowed by the robot framework and log them
			LOG.error("An exception occurred while adding a product to cart", e);
			fail(e.getMessage());
		}
	}

	/**
	 * Java implementation of the robot keyword <br>
	 * <p>
	 * <i>set entry quantity</i>
	 * <p>
	 *
	 * @param entryNumber
	 *           entry number
	 * @param quantity
	 *           quantity to update entry with
	 */
	public void setEntryQuantity(final int entryNumber, final long quantity)
	{
		try
		{
			final OrderEntryData entry = getEntryByNumber(cartFacade.getSessionCart(), Integer.valueOf(entryNumber));
			if (entry == null)
			{
				throw new IllegalArgumentException("Entry #" + entryNumber + " was not found in cart");
			}

			entry.setQuantity(Long.valueOf(quantity));
			cartFacade.updateCartEntry(entry);
		}
		catch (final Exception e)
		{
			// catch any exceptions that would get swallowed by the robot framework and log them
			LOG.error("An exception occurred while adding a product to cart", e);
			fail(e.getMessage());
		}
	}
}
