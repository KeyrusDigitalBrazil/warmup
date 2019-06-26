package de.hybris.platform.subscriptionservices.order.hook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.impl.DefaultCartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;
import de.hybris.platform.subscriptionservices.subscription.BillingTimeService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;

import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit test suite for {@link SubscriptionAddToCartMethodHook}
 */
@UnitTest
public class SubscriptionAddToCartMethodHookTest
{
	private static final String PAYNOW = "paynow";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@InjectMocks
	private final SubscriptionAddToCartMethodHook subscriptionAddToCartMethodHook = new SubscriptionAddToCartMethodHook();

	private CommerceCartParameter commerceCartParameter;
	private CommerceCartModification commerceCartModification;
	private CartEntryModel masterCartEntryModel;
	private CartEntryModel childCartEntryModel;

	@Mock
	private CartModel masterCartModel;
	@Mock
	private CartModel childCartModelMonthly;
	@Mock
	private ProductModel productModel;
	@Mock
	private UnitModel unitModel;
	@Mock
	private BillingTimeModel billingFrequencyModelPayNow;
	@Mock
	private BillingFrequencyModel billingFrequencyModelMonthly;
	@Mock
	private SubscriptionTermModel subscriptionTermModel;
	@Mock
	private BillingTimeService billingTimeService;
	@Mock
	private SubscriptionCommerceCartService subscriptionCommerceCartService;
	@Mock
	private SubscriptionProductService subscriptionProductService;
	@Mock
	private ModelService modelService;
	@Mock
	private DefaultCartService defaultCartService;
	@Mock
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		masterCartEntryModel = new CartEntryModel();
		masterCartEntryModel.setOrder(masterCartModel);

		childCartEntryModel = new CartEntryModel();

		commerceCartParameter = new CommerceCartParameter();
		commerceCartParameter.setCart(masterCartModel);
		commerceCartParameter.setProduct(productModel);
		commerceCartParameter.setUnit(unitModel);
		commerceCartParameter.setQuantity(1);
		commerceCartParameter.setXmlProduct("<xml product>");

		commerceCartModification = new CommerceCartModification();
		commerceCartModification.setEntry(masterCartEntryModel);
		commerceCartModification.setQuantityAdded(1);

		when(subscriptionCommerceCartService.getMasterCartBillingTimeCode()).thenReturn(PAYNOW);
		when(billingTimeService.getBillingTimeForCode(PAYNOW)).thenReturn(billingFrequencyModelPayNow);
	}

	@Test
	public void testBeforeAddToCartNullParameter() throws CommerceCartModificationException
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter parameters can not be null");

		subscriptionAddToCartMethodHook.beforeAddToCart(null);
	}

	@Test
	public void testBeforeAddToCartIsNotSubscriptionProduct() throws CommerceCartModificationException
	{
		commerceCartParameter.setCreateNewEntry(false);
		commerceCartParameter.setQuantity(1);
		when(subscriptionProductService.isSubscription(productModel)).thenReturn(Boolean.FALSE);

		subscriptionAddToCartMethodHook.beforeAddToCart(commerceCartParameter);

		assertFalse(commerceCartParameter.isCreateNewEntry());
		verify(subscriptionCommerceCartService).checkMasterCart(masterCartModel, billingFrequencyModelPayNow);
		verify(subscriptionCommerceCartService, never()).checkQuantityToAdd(1);
	}

	@Test
	public void testBeforeAddToCartIsSubscriptionProduct() throws CommerceCartModificationException
	{
		commerceCartParameter.setCreateNewEntry(false);
		commerceCartParameter.setQuantity(1);
		when(subscriptionProductService.isSubscription(productModel)).thenReturn(Boolean.TRUE);

		subscriptionAddToCartMethodHook.beforeAddToCart(commerceCartParameter);

		assertTrue(commerceCartParameter.isCreateNewEntry());
		verify(subscriptionCommerceCartService).checkMasterCart(masterCartModel, billingFrequencyModelPayNow);
		verify(subscriptionCommerceCartService, times(1)).checkQuantityToAdd(1);
	}

	@Test
	public void testAfterAddToCartNoChildCartNoSubscriptionTerm() throws CommerceCartModificationException
	{
		productModel.setSubscriptionTerm(null);
		commerceCartParameter.setXmlProduct(null);

		when(subscriptionCommerceCartService.getBillingFrequenciesForMasterEntry(masterCartEntryModel)).thenReturn(
				Collections.emptyList());
		when(subscriptionProductService.isSubscription(productModel)).thenReturn(Boolean.TRUE);

		subscriptionAddToCartMethodHook.afterAddToCart(commerceCartParameter, commerceCartModification);

		assertTrue(masterCartModel.getChildren().isEmpty());
		verify(subscriptionCommerceCartService, never()).getChildCartForBillingTime(masterCartModel, billingFrequencyModelPayNow);
		verifyZeroInteractions(defaultCartService);
		assertNull(masterCartEntryModel.getXmlProduct());
	}

	@Test
	public void testAfterAddToCart() throws CommerceCartModificationException
	{
		when(productModel.getSubscriptionTerm()).thenReturn(subscriptionTermModel);

		when(subscriptionProductService.isSubscription(productModel)).thenReturn(Boolean.TRUE);
		when(subscriptionCommerceCartService.getBillingFrequenciesForMasterEntry(masterCartEntryModel)).thenReturn(
				Collections.singletonList(billingFrequencyModelMonthly));
		when(billingFrequencyModelMonthly.getCartAware()).thenReturn(Boolean.TRUE);
		when(subscriptionCommerceCartService.getChildCartForBillingTime(masterCartModel, billingFrequencyModelMonthly)).thenReturn(
				childCartModelMonthly);
		when(defaultCartService.addNewEntry(childCartModelMonthly, productModel, 1, unitModel, -1, false)).thenReturn(
				childCartEntryModel);

		subscriptionAddToCartMethodHook.afterAddToCart(commerceCartParameter, commerceCartModification);

		assertEquals(masterCartEntryModel, childCartEntryModel.getMasterEntry());
		verify(subscriptionCommerceCartService, times(1)).getChildCartForBillingTime(masterCartModel, billingFrequencyModelMonthly);
		assertEquals(commerceCartParameter.getXmlProduct(), masterCartEntryModel.getXmlProduct());
	}
}
