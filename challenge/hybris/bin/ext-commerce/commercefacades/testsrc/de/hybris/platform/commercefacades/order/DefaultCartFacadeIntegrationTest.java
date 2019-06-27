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

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.CommerceCartMetadata;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.CommerceSaveCartService;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.commerceservices.util.GuidKeyGenerator;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


@IntegrationTest
public class DefaultCartFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String TEST_BASESITE_UID = "testSite";
	private static final String TEST_BASESTORE_UID = "testStore";

	@Resource
	private CartFacade cartFacade;
	@Resource
	private CartService cartService;
	@Resource
	private CommerceSaveCartService commerceSaveCartService;
	@Resource
	private ModelService modelService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private UserService userService;
	@Resource
	private GuidKeyGenerator guidKeyGenerator;
	@Resource
	private BaseStoreService baseStoreService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private CommerceCartService commerceCartService;

	private CurrencyModel currency;


	@BeforeClass
	public static void tenantStuff()
	{
		Registry.setCurrentTenantByID("junit");
	}

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
		currency = commonI18NService.getCurrency("EUR");
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);

		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		assertNotNull(catalogVersionModel);
		catalogVersionService.setSessionCatalogVersions(Collections.singletonList(catalogVersionModel));

		final UserModel user = createUser("user");

		setupEmptyCart(user);
		userService.setCurrentUser(user);
	}

	private void setupEmptyCart(final UserModel user)
	{
		final CartModel cartModel = modelService.create(CartModel.class);
		final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid(TEST_BASESTORE_UID);
		final BaseSiteModel baseSite = baseSiteService.getCurrentBaseSite();

		baseStore.setExternalTaxEnabled(Boolean.FALSE);
		baseStore.setUid(TEST_BASESTORE_UID);
		cartModel.setStore(baseStore);
		cartModel.setCurrency(currency);
		cartModel.setDate(new Date());
		cartModel.setNet(Boolean.TRUE);
		cartModel.setUser(user);
		cartModel.setGuid(guidKeyGenerator.generate().toString());
		cartModel.setSite(baseSite);
		modelService.save(cartModel);
		cartService.setSessionCart(cartModel);
		userService.setCurrentUser(user);
	}

	private UserModel createUser(final String uid)
	{
		final CustomerModel user = modelService.create(CustomerModel.class);
		user.setUid(uid);
		user.setName(uid);
		modelService.save(user);
		return user;
	}

	@Test
	public void testGetSessionCart()
	{
		final CartData cart = cartFacade.getSessionCart();
		Assert.assertNotNull(cart);
	}

	@Test
	public void testGetSessionCartNull()
	{
		cartService.removeSessionCart();
		commonI18NService.setCurrentCurrency(currency);
		final CartData cart = cartFacade.getSessionCart();
		Assert.assertNotNull(cart);
	}

	@Test
	public void testHasSessionCartTrue()
	{
		final boolean hasCart = cartFacade.hasSessionCart();
		Assert.assertTrue(hasCart);
	}

	@Test
	public void testHasSessionCartFalse()
	{
		cartService.removeSessionCart();
		final boolean hasCart = cartFacade.hasSessionCart();
		Assert.assertTrue(!hasCart);
	}

	@Test
	public void testGetMiniCart()
	{
		final CartData cart = cartFacade.getMiniCart();
		Assert.assertNotNull(cart);
	}

	@Test
	public void testGetMiniCartEmpty()
	{
		cartService.removeSessionCart();
		commonI18NService.setCurrentCurrency(currency);
		final CartData cart = cartFacade.getMiniCart();
		Assert.assertNotNull(cart);
	}

	@Test
	public void testAddToCart() throws CommerceCartModificationException
	{
		cartFacade.addToCart("HW1210-3423", 1);
		final CartModel cart = cartService.getSessionCart();
		Assert.assertEquals("HW1210-3423", cart.getEntries().iterator().next().getProduct().getCode());
	}

	@Test
	public void testAddToCartNoStock() throws CommerceCartModificationException
	{
		final CartModificationData cartModificationData = cartFacade.addToCart("HW1210-3425", 1);
		Assert.assertEquals(CommerceCartModificationStatus.NO_STOCK, cartModificationData.getStatusCode());
		Assert.assertEquals(0L, cartModificationData.getQuantityAdded());
		Assert.assertEquals("HW1210-3425", cartModificationData.getEntry().getProduct().getCode());
		Assert.assertEquals(null, cartModificationData.getEntry().getBasePrice());
		Assert.assertEquals(null, cartModificationData.getEntry().getTotalPrice());
	}

	@Test
	public void testUpdateCartEntry() throws CommerceCartModificationException
	{
		cartFacade.addToCart("HW1210-3423", 1);
		final CartModel cart = cartService.getSessionCart();
		Assert.assertEquals("HW1210-3423", cart.getEntries().iterator().next().getProduct().getCode());

		final CartModificationData cartModificationData = cartFacade.updateCartEntry(0, 0);
		Assert.assertEquals(CommerceCartModificationStatus.SUCCESS, cartModificationData.getStatusCode());
		Assert.assertEquals(0L, cartModificationData.getQuantity());
		Assert.assertEquals("HW1210-3423", cartModificationData.getEntry().getProduct().getCode());
		Assert.assertEquals(null, cartModificationData.getEntry().getBasePrice());
		Assert.assertEquals(null, cartModificationData.getEntry().getTotalPrice());
	}

	@Test
	public void testValidateCart() throws CommerceCartModificationException, CommerceCartRestorationException,
			InterruptedException
	{
		cartService.removeSessionCart();

		final UserModel promoted = userService.getUserForUID("promoted");
		userService.setCurrentUser(promoted);
		final CartModel promotedStoredCart = commerceCartService.getCartForCodeAndUser("promotedStored", promoted);
		cartService.setSessionCart(promotedStoredCart);

		final List<CartModificationData> cartModificationDataList = cartFacade.validateCartData();
		Assert.assertEquals(1, cartModificationDataList.size());

		final CartModificationData cartModificationData = cartModificationDataList.get(0);
		Assert.assertEquals(CommerceCartModificationStatus.NO_STOCK, cartModificationData.getStatusCode());
		Assert.assertEquals(0L, cartModificationData.getQuantityAdded());
		Assert.assertEquals("HW1210-3425", cartModificationData.getEntry().getProduct().getCode());
		Assert.assertEquals(null, cartModificationData.getEntry().getBasePrice());
		Assert.assertEquals(null, cartModificationData.getEntry().getTotalPrice());
	}

	@Test
	public void testAddToCartLowerStock() throws CommerceCartModificationException
	{
		final long actualAdded = cartFacade.addToCart("HW1210-3423", 12).getQuantityAdded();
		final CartModel cart = cartService.getSessionCart();
		Assert.assertEquals(10, actualAdded);
		Assert.assertEquals("HW1210-3423", cart.getEntries().iterator().next().getProduct().getCode());
	}

	@Test
	public void testAnonymousCartRestoration() throws CommerceCartModificationException, CommerceCartRestorationException
	{
		final CustomerModel anonymous = userService.getAnonymousUser();


		//set current user as built in anonymous user
		setupEmptyCart(anonymous);

		final String productCode = "HW1210-3423";
		final long actualAdded = cartFacade.addToCart(productCode, 10).getQuantityAdded();
		final String anonymousCartGuid = cartService.getSessionCart().getGuid();

		Assert.assertEquals(10, actualAdded);
		Assert.assertEquals(anonymous, cartService.getSessionCart().getUser());

		final UserModel regularUser = createUser("regularUser");

		//set current user as new regular user
		//first setup a new cart, otherwise calling setCurrentUser would replace user in current cart
		setupEmptyCart(regularUser);

		final String regularUsersCartGuid = cartService.getSessionCart().getGuid();
		Assert.assertNotSame(anonymousCartGuid, regularUsersCartGuid);

		//try to restore previously created cart and set its user as currently operating regular user
		cartFacade.restoreAnonymousCartAndTakeOwnership(anonymousCartGuid);
		Assert.assertEquals(regularUser, cartService.getSessionCart().getUser());

		final AbstractOrderEntryModel aoem = cartService.getSessionCart().getEntries().get(0);
		Assert.assertEquals(10, aoem.getQuantity().longValue());
		Assert.assertEquals(productCode, aoem.getProduct().getCode());
	}

	@Test
	public void testAnonymousCartRestorationOfNonAnonymousCartShouldFail() throws CommerceCartModificationException,
			CommerceCartRestorationException
	{
		final UserModel regularUser1 = createUser("regularUser1");
		final UserModel regularUser2 = createUser("regularUser2");

		//set current user as built in anonymous user
		setupEmptyCart(regularUser1);
		userService.setCurrentUser(regularUser1);

		final long actualAdded = cartFacade.addToCart("HW1210-3423", 10).getQuantityAdded();
		final String regularUser1CartGuid = cartService.getSessionCart().getGuid();

		Assert.assertEquals(10, actualAdded);
		Assert.assertEquals(regularUser1, cartService.getSessionCart().getUser());

		//set current user as new regular user
		//first setup a new cart, otherwise calling setCurrentUser would replace user in current cart
		setupEmptyCart(regularUser2);
		userService.setCurrentUser(regularUser2);

		//try to restore previously created cart, should throw an exception
		try
		{
			cartFacade.restoreAnonymousCartAndTakeOwnership(regularUser1CartGuid);
			Assert.fail();
		}
		catch (final CommerceCartRestorationException ccre)
		{
			Assert.assertTrue(ccre.getMessage().contains("not found"));
		}
	}

	@Test
	public void testGetMostRecentCartForUser() throws InterruptedException
	{
		cartService.removeSessionCart();
		final UserModel john = userService.getUserForUID("john");
		userService.setCurrentUser(john);
		final Collection<CartModel> johnsCarts = john.getCarts();
		Assert.assertEquals(1, johnsCarts.size());

		final String mostRecentCartGuidForJohn = cartFacade.getMostRecentCartGuidForUser(Arrays.asList(johnsCarts.iterator().next()
				.getGuid()));
		Assert.assertNull(mostRecentCartGuidForJohn);

		cartService.removeSessionCart();
		final UserModel lesley = userService.getUserForUID("lesley");
		userService.setCurrentUser(lesley);
		final Collection<CartModel> lesleysCarts = lesley.getCarts();
		Assert.assertEquals(2, lesleysCarts.size());
		final CartModel lesleysNewCart = commerceCartService.getCartForCodeAndUser("lesleysNewCart", lesley);

		TimeUnit.SECONDS.sleep(1);

		lesleysNewCart.setCalculated(Boolean.TRUE);
		modelService.save(lesleysNewCart);

		final String firstMostRecentCartGuidForLesley = cartFacade.getMostRecentCartGuidForUser(Collections.EMPTY_LIST);
		Assert.assertEquals("lesleysNewCart", firstMostRecentCartGuidForLesley);

		final String secondMostRecentCartGuidForLesley = cartFacade.getMostRecentCartGuidForUser(Arrays.asList(lesleysNewCart
				.getGuid()));
		Assert.assertEquals("lesleysOldCart", secondMostRecentCartGuidForLesley);
	}

	@Ignore
	@Test
	public void testAddToCartWithPromotion() throws CommerceCartModificationException
	{
		final CartModificationData modif = cartFacade.addToCart("HW1210-3425promo", 1);
		Assert.assertNotNull(modif);
		final CartData sessionCart = cartFacade.getSessionCart();
		Assert.assertNotNull(sessionCart);
		Assert.assertEquals(Integer.valueOf(sessionCart.getEntries().size()), Integer.valueOf(1));
		Assert.assertEquals(sessionCart.getProductDiscounts().getValue(), BigDecimal.valueOf(18.05));
		Assert.assertEquals(sessionCart.getEntries().get(0).getQuantity(), Long.valueOf(1));
		Assert.assertEquals(sessionCart.getEntries().get(0).getTotalPrice().getValue(), BigDecimal.valueOf(76.95));
		Assert.assertEquals(Integer.valueOf(sessionCart.getAppliedProductPromotions().size()), Integer.valueOf(1));
		Assert.assertEquals(sessionCart.getAppliedProductPromotions().get(0).getDescription(),
				"19% Discount.<br>You saved E18.05.");
	}

	@Test
	public void testRestoreSavedCartNoGuid()
			throws CommerceSaveCartException, CommerceCartRestorationException, InterruptedException
	{
		final UserModel userModel = userService.getCurrentUser();

		final CartData cart = cartFacade.getSessionCart();
		Assert.assertNotNull("Initial session cart should not be null", cart);

		setupEmptyCart(userModel);
		final CartData sessionCart = cartFacade.getSessionCart();
		Assert.assertNotNull("Temporary session cart should not be null", sessionCart);

		Assert.assertNotEquals("Previous and new session cart should not be the same", cart.getGuid(), sessionCart.getGuid());
		TimeUnit.MILLISECONDS.sleep(100);

		final CommerceSaveCartParameter commerceSaveCartParameter = new CommerceSaveCartParameter();
		commerceSaveCartParameter.setName("Saved_Cart_Name");
		commerceSaveCartParameter.setDescription("Saved_Cart_Description");

		final CartModel cartModel = modelService.create(CartModel.class);
		cartModel.setSite(baseSiteService.getCurrentBaseSite());
		cartModel.setUser(userService.getCurrentUser());
		cartModel.setCode("Saved_Cart_Code");
		cartModel.setCurrency(currency);
		cartModel.setDate(new Date());
		cartModel.setTotalPrice(new Double(commerceSaveCartParameter.getName().length()));
		commerceSaveCartParameter.setCart(cartModel);

		final CommerceSaveCartResult savedCartResult = commerceSaveCartService.saveCart(commerceSaveCartParameter);
		Assert.assertNotNull("Saved cart should not be null", savedCartResult);

		cartService.removeSessionCart();
		final CartRestorationData cartRestorationData = cartFacade.restoreSavedCart(null);
		Assert.assertNotNull("Cart restoration data should not be null", cartRestorationData);

		final CartData restoredCart = cartFacade.getSessionCart();
		Assert.assertNotNull("Restored session cart should not be null", cart);

		Assert.assertEquals("Restored session cart should be the same with initial session cart", cart.getGuid(),
				restoredCart.getGuid());
	}

	@Test
	public void testUpdateCartMetadata()
	{
		final CommerceCartMetadata commerceCartMetadata = new CommerceCartMetadata();
		final Date currentDate = new Date();

		commerceCartMetadata.setName(Optional.of("myQuoteName"));
		commerceCartMetadata.setDescription(Optional.of("myQuoteDescription"));
		commerceCartMetadata.setExpirationTime(Optional.of(currentDate));
		commerceCartMetadata.setRemoveExpirationTime(false);

		cartFacade.updateCartMetadata(commerceCartMetadata);

		CartModel cartModel = cartService.getSessionCart();

		Assert.assertEquals("Quote name should be updated", cartModel.getName(), "myQuoteName");
		Assert.assertEquals("Quote description should be updated", cartModel.getDescription(), "myQuoteDescription");
		Assert.assertEquals("Quote expiration time should be updated", cartModel.getExpirationTime(), currentDate);
	}
}
