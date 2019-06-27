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
package de.hybris.platform.commercefacades.order.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CommerceEntryGroupUtils;
import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.commercefacades.order.VirtualEntryGroupStrategy;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.ZoneDeliveryModeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.DiscountValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fest.assertions.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class AbstractOrderPopulatorTest
{
	private static final String CART_CODE = "cartCode";
	private static final String ISOCODE = "isoCode";
	private static final String CART_NAME = "cartName";
	private static final String CART_DESCRIPTION = "cartDescription";
	private static double PRICE = 35.87;
	private static double TAX = 1.0;
	private static double EPSILON = 0.001;

	@Mock
	private ModelService modelService;
	@Mock
	private PromotionsService promotionsService;
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private AbstractPopulatingConverter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
	@Mock
	private AbstractPopulatingConverter<AddressModel, AddressData> addressConverter;
	@Mock
	private AbstractPopulatingConverter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;
	@Mock
	private AbstractPopulatingConverter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter;
	@Mock
	private AbstractPopulatingConverter<ZoneDeliveryModeModel, ZoneDeliveryModeData> zoneDeliveryModeConverter;
	@Mock
	private AbstractPopulatingConverter<PromotionResultModel, PromotionResultData> promotionResultConverter;
	@Mock
	private AbstractPopulatingConverter<EntryGroup, EntryGroupData> entryGroupConverter;
	@Mock
	private EntryGroupService entryGroupService;
	@Mock
	private CommerceEntryGroupUtils commerceEntryGroupUtils;
	@Mock
	private VirtualEntryGroupStrategy virtualEntryGroupStrategy;

	private final CartPopulator<CartData> cartPopulator = new CartPopulator<CartData>();

	private CartModel cartModel;
	private CartData cartData;
	private CurrencyModel currencyModel;
	private PriceData priceData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		cartPopulator.setAddressConverter(addressConverter);
		cartPopulator.setCreditCardPaymentInfoConverter(creditCardPaymentInfoConverter);
		cartPopulator.setDeliveryModeConverter(deliveryModeConverter);
		cartPopulator.setOrderEntryConverter(orderEntryConverter);
		cartPopulator.setModelService(modelService);
		cartPopulator.setPriceDataFactory(priceDataFactory);
		cartPopulator.setPromotionResultConverter(promotionResultConverter);
		cartPopulator.setPromotionsService(promotionsService);
		cartPopulator.setZoneDeliveryModeConverter(zoneDeliveryModeConverter);
		cartPopulator.setEntryGroupConverter(entryGroupConverter);
		cartPopulator.setEntryGroupService(entryGroupService);
		cartPopulator.setCommerceEntryGroupUtils(commerceEntryGroupUtils);
		cartPopulator.setVirtualEntryGroupStrategy(virtualEntryGroupStrategy);

		cartModel = mock(CartModel.class);
		cartData = new CartData();

		currencyModel = mock(CurrencyModel.class);
		priceData = mock(PriceData.class);
	}

	@Test
	public void testAddCommon()
	{
		final AbstractOrderEntryModel abstractOrderEntryModel = mock(AbstractOrderEntryModel.class);
		given(cartModel.getCode()).willReturn(CART_CODE);
		given(cartModel.getName()).willReturn(CART_NAME);
		given(cartModel.getDescription()).willReturn(CART_DESCRIPTION);
		given(cartModel.getEntries()).willReturn(Collections.singletonList(abstractOrderEntryModel));
		cartPopulator.addCommon(cartModel, cartData);
		Assert.assertEquals(CART_CODE, cartData.getCode());
		Assert.assertEquals(CART_NAME, cartData.getName());
		Assert.assertEquals(CART_DESCRIPTION, cartData.getDescription());
		Assert.assertEquals(Integer.valueOf(1), cartData.getTotalItems());
	}

	@Test
	public void testAddTotals()
	{
		final DeliveryModeModel deliveryMode = mock(DeliveryModeModel.class);
		final DiscountValue discountValue = mock(DiscountValue.class);
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(1.2));
		given(cartModel.getTotalTax()).willReturn(Double.valueOf(1.3));
		given(cartModel.getSubtotal()).willReturn(Double.valueOf(1.2));
		given(cartModel.getGlobalDiscountValues()).willReturn(Collections.singletonList(discountValue));
		given(Double.valueOf(discountValue.getAppliedValue())).willReturn(Double.valueOf(.2));
		given(cartModel.getDeliveryMode()).willReturn(deliveryMode);
		given(cartModel.getDeliveryCost()).willReturn(Double.valueOf(3.4));
		given(cartModel.getCurrency()).willReturn(currencyModel);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(1.0), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(1.2), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(1.3), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(3.4), currencyModel)).willReturn(priceData);
		cartPopulator.addTotals(cartModel, cartData);
		Assert.assertEquals(priceData, cartData.getTotalPrice());
		Assert.assertEquals(priceData, cartData.getTotalTax());
		Assert.assertEquals(priceData, cartData.getSubTotal());
		Assert.assertEquals(priceData, cartData.getDeliveryCost());
	}

	@Test
	public void testAddTotalsNoDeliveryMode()
	{
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(1.2));
		given(cartModel.getTotalTax()).willReturn(Double.valueOf(1.3));
		given(cartModel.getSubtotal()).willReturn(Double.valueOf(1.2));
		given(cartModel.getDeliveryCost()).willReturn(Double.valueOf(3.4));
		given(cartModel.getCurrency()).willReturn(currencyModel);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(1.2), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(1.3), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(3.4), currencyModel)).willReturn(priceData);
		cartPopulator.addTotals(cartModel, cartData);
		Assert.assertEquals(priceData, cartData.getTotalPrice());
		Assert.assertEquals(priceData, cartData.getTotalTax());
		Assert.assertEquals(priceData, cartData.getSubTotal());
		Assert.assertNull(cartData.getDeliveryCost());
	}

	@Test
	public void testAddTotalsNetCart()
	{
		final DeliveryModeModel deliveryMode = mock(DeliveryModeModel.class);
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(1.2));
		given(cartModel.getTotalTax()).willReturn(Double.valueOf(1.3));
		given(cartModel.getSubtotal()).willReturn(Double.valueOf(1.2));
		given(cartModel.getDeliveryMode()).willReturn(deliveryMode);
		given(cartModel.getDeliveryCost()).willReturn(Double.valueOf(3.4));
		given(cartModel.getCurrency()).willReturn(currencyModel);
		given(cartModel.getNet()).willReturn(Boolean.TRUE);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(1.2), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(1.3), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(3.4), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(2.5), currencyModel)).willReturn(priceData);
		cartPopulator.addTotals(cartModel, cartData);
		Assert.assertEquals(priceData, cartData.getTotalPrice());
		Assert.assertEquals(priceData, cartData.getTotalTax());
		Assert.assertEquals(priceData, cartData.getSubTotal());
		Assert.assertEquals(priceData, cartData.getDeliveryCost());
		Assert.assertEquals(priceData, cartData.getTotalPriceWithTax());
	}

	@Test
	public void testAddEntries()
	{
		final AbstractOrderEntryModel abstractOrderEntryModel = mock(AbstractOrderEntryModel.class);
		final OrderEntryData entryData = mock(OrderEntryData.class);
		given(cartModel.getEntries()).willReturn(Collections.singletonList(abstractOrderEntryModel));
		given(orderEntryConverter.convertAll(Collections.singletonList(abstractOrderEntryModel))).willReturn(
				Collections.singletonList(entryData));
		cartPopulator.addEntries(cartModel, cartData);
		Assert.assertEquals(entryData, cartData.getEntries().iterator().next());
	}

	@Test
	public void testAddPromotions()
	{
		final SessionContext sessioncontext = mock(SessionContext.class);
		final AbstractOrder abstractOrder = mock(AbstractOrder.class);
		final PromotionResult promotionResult = mock(PromotionResult.class);
		final PromotionOrderResults promotionOrderResults = new PromotionOrderResults(sessioncontext, abstractOrder,
				Collections.singletonList(promotionResult), 2.1);
		final DiscountValue discountValue = mock(DiscountValue.class);
		final AbstractOrderEntryModel abstractOrderEntryModel = mock(AbstractOrderEntryModel.class);
		final PromotionResultModel promotionResultModel = mock(PromotionResultModel.class);
		final List<PromotionResultModel> promotionResultModelList = new ArrayList<>();
		promotionResultModelList.add(promotionResultModel);
		final AbstractPromotionModel abstractPromotionModel = mock(AbstractPromotionModel.class);
		final PromotionOrderEntryConsumedModel promotionOrderEntryConsumedModel = mock(PromotionOrderEntryConsumedModel.class);
		final PromotionResultData promotionResultData = mock(PromotionResultData.class);

		given(cartModel.getCurrency()).willReturn(currencyModel);
		given(abstractOrderEntryModel.getDiscountValues()).willReturn(Collections.singletonList(discountValue));
		given(cartModel.getEntries()).willReturn(Collections.singletonList(abstractOrderEntryModel));
		given(promotionsService.getPromotionResults(cartModel)).willReturn(promotionOrderResults);
		given(cartModel.getGlobalDiscountValues()).willReturn(Collections.singletonList(discountValue));
		given(Double.valueOf(discountValue.getAppliedValue())).willReturn(Double.valueOf(2.3));
		given(modelService.getAll(Mockito.anyCollection(), Mockito.anyCollection())).willReturn(promotionResultModelList);
		given(promotionResultModel.getPromotion()).willReturn(abstractPromotionModel);
		given(promotionResultModel.getConsumedEntries()).willReturn(Collections.singletonList(promotionOrderEntryConsumedModel));
		given(promotionResultConverter.convertAll(Collections.singletonList(promotionResultModel))).willReturn(
				Collections.singletonList(promotionResultData));

		cartPopulator.addPromotions(cartModel, cartData);
		Assert.assertEquals(promotionResultData, cartData.getPotentialOrderPromotions().iterator().next());
		Assert.assertEquals(promotionResultData, cartData.getPotentialProductPromotions().iterator().next());
		Assert.assertEquals(promotionResultData, cartData.getAppliedOrderPromotions().iterator().next());
		Assert.assertEquals(promotionResultData, cartData.getAppliedProductPromotions().iterator().next());
	}

	@Test
	public void testAddPaymentInformation()
	{
		final CreditCardPaymentInfoModel creditCardPaymentInfoModel = mock(CreditCardPaymentInfoModel.class);
		final CCPaymentInfoData ccPaymentInfoData = mock(CCPaymentInfoData.class);
		given(cartModel.getPaymentInfo()).willReturn(creditCardPaymentInfoModel);
		given(creditCardPaymentInfoConverter.convert(creditCardPaymentInfoModel)).willReturn(ccPaymentInfoData);
		cartPopulator.addPaymentInformation(cartModel, cartData);
		Assert.assertEquals(ccPaymentInfoData, cartData.getPaymentInfo());
	}

	@Test
	public void testAddDeliveryAddress()
	{
		final AddressModel addressModel = mock(AddressModel.class);
		final AddressData addressData = mock(AddressData.class);
		given(cartModel.getDeliveryAddress()).willReturn(addressModel);
		given(addressConverter.convert(addressModel)).willReturn(addressData);
		cartPopulator.addDeliveryAddress(cartModel, cartData);
		Assert.assertEquals(addressData, cartData.getDeliveryAddress());
	}

	@Test
	public void testAddDeliveryMethod()
	{
		final DeliveryModeModel deliveryModeModel = mock(DeliveryModeModel.class);
		final DeliveryModeData deliveryModeData = mock(DeliveryModeData.class);
		given(currencyModel.getIsocode()).willReturn(ISOCODE);
		given(cartModel.getDeliveryMode()).willReturn(deliveryModeModel);
		given(deliveryModeConverter.convert(deliveryModeModel)).willReturn(deliveryModeData);
		given(cartModel.getCurrency()).willReturn(currencyModel);
		cartPopulator.addDeliveryMethod(cartModel, cartData);
		Assert.assertEquals(deliveryModeData, cartData.getDeliveryMode());
	}

	@Test
	public void testAddDeliveryMethodZone()
	{
		final ZoneDeliveryModeModel zoneDeliveryModeModel = mock(ZoneDeliveryModeModel.class);
		final ZoneDeliveryModeData zoneDeliveryModeData = mock(ZoneDeliveryModeData.class);
		given(cartModel.getDeliveryMode()).willReturn(zoneDeliveryModeModel);
		given(cartModel.getDeliveryCost()).willReturn(Double.valueOf(3.3));
		given(zoneDeliveryModeConverter.convert(Mockito.any(ZoneDeliveryModeModel.class))).willReturn(zoneDeliveryModeData);
		given(currencyModel.getIsocode()).willReturn(ISOCODE);
		given(cartModel.getCurrency()).willReturn(currencyModel);
		cartPopulator.addDeliveryMethod(cartModel, cartData);
		Assert.assertEquals(zoneDeliveryModeData, cartData.getDeliveryMode());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreatePriceSourceNull()
	{
		cartPopulator.createPrice(null, null);
		Assert.fail(" IllegalArgumentException should be thrown. ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreatePriceCurrencyNull()
	{
		cartPopulator.createPrice(cartModel, null);
		Assert.fail(" IllegalArgumentException should be thrown. ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void getTotalWithTaxShouldFailOnNullOrder()
	{
		cartPopulator.calcTotalWithTax(null);
	}

	@Test
	public void shouldReturnZeroForNullTotalPrice()
	{
		given(cartModel.getTotalPrice()).willReturn(null);
		Assert.assertEquals(0.0d, cartPopulator.calcTotalWithTax(cartModel).doubleValue(), EPSILON);
	}

	@Test
	public void shouldAddNoTaxForNonNetSource()
	{
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(PRICE));
		Assert.assertEquals(PRICE, cartPopulator.calcTotalWithTax(cartModel).doubleValue(), EPSILON);
	}

	@Test
	public void shouldAddNoTaxForFreeOrder()
	{
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(0.0d));
		given(cartModel.getNet()).willReturn(Boolean.TRUE);
		Assert.assertEquals(0.0d, cartPopulator.calcTotalWithTax(cartModel).doubleValue(), EPSILON);
	}

	@Test
	public void shouldAddNoTaxForOrderWithNullTax()
	{
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(PRICE));
		given(cartModel.getNet()).willReturn(Boolean.TRUE);
		given(cartModel.getTotalTax()).willReturn(null);
		Assert.assertEquals(PRICE, cartPopulator.calcTotalWithTax(cartModel).doubleValue(), EPSILON);
	}

	@Test
	public void shouldAddTaxToOrder()
	{
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(PRICE));
		given(cartModel.getNet()).willReturn(Boolean.TRUE);
		given(cartModel.getTotalTax()).willReturn(Double.valueOf(TAX));
		Assert.assertEquals(PRICE + TAX, cartPopulator.calcTotalWithTax(cartModel).doubleValue(), EPSILON);
	}


	@Test
	public void testAddEntryGroupsNoGroupsNoEntries()
	{
		final AbstractOrderModel orderModel = new AbstractOrderModel();
		final AbstractOrderData orderData = new AbstractOrderData();
		given(entryGroupConverter.convertAll(any())).willReturn(Collections.emptyList());

		cartPopulator.addEntryGroups(orderModel, orderData);

		Assertions.assertThat(orderModel.getEntries()).isNull();
		Assertions.assertThat(orderData.getEntries()).isEmpty();
		Assertions.assertThat(orderModel.getEntryGroups()).isNull();
		Assertions.assertThat(orderData.getRootGroups()).isEmpty();
	}

	@Test
	public void testAddEntryGroupsNoEntries()
	{
		final EntryGroup entryGroup = new EntryGroup();
		entryGroup.setGroupNumber(Integer.valueOf(1));
		final EntryGroupData entryGroupCopy = new EntryGroupData();
		entryGroupCopy.setGroupNumber(Integer.valueOf(1));
		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setEntryGroups(Collections.singletonList(entryGroup));
		final AbstractOrderData orderData = new AbstractOrderData();
		given(entryGroupConverter.convertAll(any())).willReturn(Collections.singletonList(entryGroupCopy));
		given(commerceEntryGroupUtils.getNestedGroups(entryGroupCopy)).willReturn(Collections.singletonList(entryGroupCopy));

		cartPopulator.addEntryGroups(orderModel, orderData);

		Assertions.assertThat(orderModel.getEntries()).isNull();
		Assertions.assertThat(orderData.getEntries()).isEmpty();
		Assertions.assertThat(orderModel.getEntryGroups()).containsExactly(entryGroup);
		Assertions.assertThat(orderData.getRootGroups()).containsExactly(entryGroupCopy);
	}

	@Test
	public void testAddEntryGroupsNoGroups()
	{
		final AbstractOrderEntryModel entryModel = new AbstractOrderEntryModel();
		final OrderEntryData entryData = new OrderEntryData();
		entryModel.setEntryNumber(Integer.valueOf(1));
		entryData.setEntryNumber(Integer.valueOf(1));
		entryData.setEntryGroupNumbers(Collections.singletonList(Integer.valueOf(-1)));
		final AbstractOrderModel orderModel = new AbstractOrderModel();
		final AbstractOrderData orderData = new AbstractOrderData();
		orderModel.setEntries(Collections.singletonList(entryModel));
		orderData.setEntries(Collections.singletonList(entryData));
		given(entryGroupConverter.convertAll(any())).willReturn(Collections.emptyList());
		doAnswer(invocationOnMock -> {
			final AbstractOrderData order = (AbstractOrderData) invocationOnMock.getArguments()[0];
			return order.getRootGroups().get(0);
		}).when(commerceEntryGroupUtils).getGroup(any(), any());
		doAnswer(invocationOnMock -> {
			final List<EntryGroupData> rootGroups = (List<EntryGroupData>) invocationOnMock.getArguments()[0];
			rootGroups.add(new EntryGroupData());
			return null;
		}).when(virtualEntryGroupStrategy).createGroup(any(), any());

		cartPopulator.addEntryGroups(orderModel, orderData);

		Assertions.assertThat(orderModel.getEntries()).containsExactly(entryModel);
		Assertions.assertThat(orderData.getEntries()).containsExactly(entryData);
		Assertions.assertThat(orderModel.getEntryGroups()).isNull();
		Assertions.assertThat(orderData.getRootGroups()).hasSize(1);
		final EntryGroupData unassignedEntryGroup = orderData.getRootGroups().get(0);
		Assertions.assertThat(unassignedEntryGroup.getChildren()).isNull();

		verifyNoMoreInteractions(entryGroupConverter);
	}

	@Test
	public void testAddEntryGroupsNoChildren()
	{
		final AbstractOrderEntryModel entryModel = new AbstractOrderEntryModel();
		final OrderEntryData entryData = new OrderEntryData();
		entryModel.setEntryNumber(Integer.valueOf(1));
		entryData.setEntryNumber(Integer.valueOf(1));
		entryModel.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(2))));
		final EntryGroup entryGroup = new EntryGroup();
		entryGroup.setGroupNumber(Integer.valueOf(2));
		final EntryGroupData entryGroupCopy = new EntryGroupData();
		entryGroupCopy.setGroupNumber(Integer.valueOf(2));
		entryGroupCopy.setRootGroup(entryGroupCopy);
		final AbstractOrderModel orderModel = new AbstractOrderModel();
		final AbstractOrderData orderData = new AbstractOrderData();
		orderModel.setEntryGroups(Collections.singletonList(entryGroup));
		orderModel.setEntries(Collections.singletonList(entryModel));
		orderData.setEntries(Collections.singletonList(entryData));
		given(entryGroupService.getNestedGroups(entryGroup)).willReturn(Collections.singletonList(entryGroup));
		given(entryGroupConverter.convertAll(any())).willReturn(Collections.singletonList(entryGroupCopy));
		given(commerceEntryGroupUtils.getGroup(any(), any())).willReturn(entryGroupCopy);

		cartPopulator.addEntryGroups(orderModel, orderData);

		Assertions.assertThat(orderModel.getEntries()).containsExactly(entryModel);
		Assertions.assertThat(orderData.getEntries()).containsExactly(entryData);
		Assertions.assertThat(orderModel.getEntryGroups()).containsExactly(entryGroup);
		Assertions.assertThat(orderData.getRootGroups()).containsExactly(entryGroupCopy);
		Assertions.assertThat(entryGroupCopy.getOrderEntries()).containsExactly(entryData);

		verify(entryGroupConverter).convertAll(Collections.singletonList(entryGroup));
		verifyNoMoreInteractions(entryGroupConverter);
	}

	@Test
	public void testAddEntryGroupsWithChild()
	{
		final AbstractOrderEntryModel entryModel = new AbstractOrderEntryModel();
		final OrderEntryData entryData = new OrderEntryData();
		entryModel.setEntryNumber(Integer.valueOf(1));
		entryData.setEntryNumber(Integer.valueOf(1));
		entryModel.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(2))));
		final EntryGroup entryGroup = new EntryGroup();
		entryGroup.setGroupNumber(Integer.valueOf(1));
		final EntryGroup childEntryGroup = new EntryGroup();
		childEntryGroup.setGroupNumber(Integer.valueOf(2));
		entryGroup.setChildren(Collections.singletonList(childEntryGroup));
		final EntryGroupData entryGroupData = new EntryGroupData();
		final EntryGroupData childEntryGroupData = new EntryGroupData();
		entryGroupData.setGroupNumber(Integer.valueOf(1));
		childEntryGroupData.setGroupNumber(Integer.valueOf(2));
		entryGroupData.setChildren(Collections.singletonList(childEntryGroupData));
		final AbstractOrderModel orderModel = new AbstractOrderModel();
		final AbstractOrderData orderData = new AbstractOrderData();
		orderModel.setEntryGroups(Collections.singletonList(entryGroup));
		orderModel.setEntries(Collections.singletonList(entryModel));
		orderData.setEntries(Collections.singletonList(entryData));
		given(entryGroupService.getNestedGroups(entryGroup)).willReturn(Arrays.asList(entryGroup, childEntryGroup));
		given(entryGroupConverter.convertAll(any())).willReturn(Arrays.asList(entryGroupData, childEntryGroupData));

		cartPopulator.addEntryGroups(orderModel, orderData);

		Assertions.assertThat(orderModel.getEntries()).containsExactly(entryModel);
		Assertions.assertThat(orderData.getEntries()).containsExactly(entryData);
		Assertions.assertThat(orderModel.getEntryGroups()).containsExactly(entryGroup);
		Assertions.assertThat(orderData.getRootGroups()).containsExactly(entryGroupData);
		Assertions.assertThat(entryGroupData.getOrderEntries()).isEmpty();
		Assertions.assertThat(orderData.getRootGroups().get(0).getChildren()).containsExactly(childEntryGroupData);
		Assertions.assertThat(childEntryGroupData.getOrderEntries()).containsExactly(entryData);
		Assertions.assertThat(childEntryGroupData.getChildren()).isNullOrEmpty();

		verify(entryGroupConverter).convertAll(Arrays.asList(entryGroup, childEntryGroup));
		verifyNoMoreInteractions(entryGroupConverter);
	}

	@Test
	public void testAddStandaloneEntry()
	{
		final AbstractOrderData orderData = new AbstractOrderData();
		final OrderEntryData entryData = new OrderEntryData();
		orderData.setRootGroups(Collections.emptyList());
		orderData.setEntries(Collections.singletonList(entryData));
		final AbstractOrderModel orderModel = new AbstractOrderModel();
		final AbstractOrderEntryModel entryModel = new AbstractOrderEntryModel();
		orderModel.setEntryGroups(Collections.emptyList());
		orderModel.setEntries(Collections.singletonList(entryModel));
		doAnswer(invocationOnMock -> {
			final AbstractOrderData order = (AbstractOrderData) invocationOnMock.getArguments()[0];
			return order.getRootGroups().get(0);
		}).when(commerceEntryGroupUtils).getGroup(any(), any());
		doAnswer(invocationOnMock -> {
			final List<EntryGroupData> rootGroups = (List<EntryGroupData>) invocationOnMock.getArguments()[0];
			rootGroups.add(new EntryGroupData());
			return null;
		}).when(virtualEntryGroupStrategy).createGroup(any(), any());

		cartPopulator.addEntryGroups(orderModel, orderData);

		Assertions.assertThat(orderData.getRootGroups()).hasSize(1);
		Assert.assertEquals(orderData.getRootGroups().get(0), orderData.getRootGroups().get(0).getRootGroup());
		Assert.assertNull(orderData.getRootGroups().get(0).getParent());
	}

	@Test
	public void testAddEntryGroups()
	{
		final EntryGroup childEntryGroup = entryGroup(Integer.valueOf(11));
		final EntryGroup entryGroup = entryGroup(Integer.valueOf(1), childEntryGroup);
		final EntryGroup secondEntryGroup = entryGroup(Integer.valueOf(2));

		final AbstractOrderEntryModel entryModel = new AbstractOrderEntryModel();
		entryModel.setEntryNumber(Integer.valueOf(1));
		entryModel.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(11))));

		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setEntryGroups(Arrays.asList(entryGroup, secondEntryGroup));
		orderModel.setEntries(Collections.singletonList(entryModel));

		final EntryGroupData childEntryGroupData = entryGroupData(Integer.valueOf(11));
		final EntryGroupData entryGroupData = entryGroupData(Integer.valueOf(1), childEntryGroupData);
		final EntryGroupData secondEntryGroupData = entryGroupData(Integer.valueOf(2));

		final OrderEntryData entryData = new OrderEntryData();
		entryData.setEntryNumber(Integer.valueOf(1));

		final AbstractOrderData orderData = new AbstractOrderData();
		orderData.setEntries(Collections.singletonList(entryData));

		given(entryGroupService.getNestedGroups(entryGroup)).willReturn(Arrays.asList(entryGroup, childEntryGroup));
		given(entryGroupService.getNestedGroups(secondEntryGroup)).willReturn(Collections.singletonList(secondEntryGroup));
		given(entryGroupConverter.convertAll(Arrays.asList(entryGroup, childEntryGroup)))
				.willReturn(Arrays.asList(entryGroupData, childEntryGroupData));
		given(entryGroupConverter.convertAll(Collections.singletonList(secondEntryGroup)))
				.willReturn(Collections.singletonList(secondEntryGroupData));

		cartPopulator.addEntryGroups(orderModel, orderData);

		Assertions.assertThat(orderModel.getEntries()).containsExactly(entryModel);
		Assertions.assertThat(orderData.getEntries()).containsExactly(entryData);
		Assertions.assertThat(orderModel.getEntryGroups()).containsExactly(entryGroup, secondEntryGroup);
		Assertions.assertThat(orderData.getRootGroups()).containsExactly(entryGroupData, secondEntryGroupData);
		Assertions.assertThat(entryGroupData.getOrderEntries()).isEmpty();
		Assertions.assertThat(orderData.getRootGroups().get(0).getChildren()).containsExactly(childEntryGroupData);
		Assertions.assertThat(childEntryGroupData.getOrderEntries()).containsExactly(entryData);
		Assertions.assertThat(childEntryGroupData.getChildren()).isNullOrEmpty();

		verify(entryGroupConverter).convertAll(Arrays.asList(entryGroup, childEntryGroup));
		verify(entryGroupConverter).convertAll(Collections.singletonList(secondEntryGroup));
		verifyNoMoreInteractions(entryGroupConverter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailFastOnBrokenData()
	{
		final AbstractOrderData orderData = new AbstractOrderData();
		final AbstractOrderModel orderModel = new AbstractOrderModel();
		final AbstractOrderEntryModel entryModel = new AbstractOrderEntryModel();
		orderModel.setEntryGroups(Collections.emptyList());
		orderModel.setEntries(Collections.singletonList(entryModel));

		cartPopulator.addEntryGroups(orderModel, orderData);
	}

	protected EntryGroup entryGroup(final Integer number, final EntryGroup... children)
	{
		final EntryGroup result = new EntryGroup();
		result.setGroupNumber(number);
		result.setChildren(Stream.of(children)
				.collect(Collectors.toList()));
		return result;
	}

	protected EntryGroupData entryGroupData(final Integer number, final EntryGroupData... children)
	{
		final EntryGroupData result = new EntryGroupData();
		result.setGroupNumber(number);
		result.setChildren(Stream.of(children)
				 .collect(Collectors.toList()));
		return result;
	}

}
