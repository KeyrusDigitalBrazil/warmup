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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.commercefacades.comment.data.CommentData;
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
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CartPopulatorTest
{
	private static final String CART_CODE = "cartCode";

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
	private AbstractPopulatingConverter<QuoteModel, QuoteData> quoteConverter;
	@Mock
	private AbstractPopulatingConverter<CommentModel, CommentData> orderCommentConverter;
	@Mock
	private AbstractPopulatingConverter<EntryGroup, EntryGroup> entryGroupConverter;
	@Mock
	private EntryGroupService entryGroupService;
	@Mock
	private CommerceEntryGroupUtils commerceEntryGroupUtils;
	@Mock
	private VirtualEntryGroupStrategy virtualEntryGroupStrategy;

	@InjectMocks
	private final CartPopulator cartPopulator = new CartPopulator();

	@Test
	public void testConvert()
	{
		final CartModel cartModel = mock(CartModel.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final QuoteModel quoteModel = mock(QuoteModel.class);
		final AbstractOrderEntryModel abstractOrderEntryModel = mock(AbstractOrderEntryModel.class);
		final CommentModel commentModel = mock(CommentModel.class);
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(1.2));
		given(cartModel.getTotalTax()).willReturn(Double.valueOf(1.3));
		given(cartModel.getSubtotal()).willReturn(Double.valueOf(1.4));
		given(cartModel.getDeliveryCost()).willReturn(Double.valueOf(3.4));
		given(cartModel.getCurrency()).willReturn(currencyModel);
		given(cartModel.getCode()).willReturn(CART_CODE);
		given(cartModel.getEntries()).willReturn(Collections.singletonList(abstractOrderEntryModel));
		given(cartModel.getNet()).willReturn(Boolean.TRUE);
		given(cartModel.getComments()).willReturn(Collections.singletonList(commentModel));
		given(currencyModel.getIsocode()).willReturn("isoCode");
		given(abstractOrderEntryModel.getEntryNumber()).willReturn(null);
		given(orderEntryConverter.convertAll(cartModel.getEntries())).willReturn(Collections.singletonList(new OrderEntryData()));
		given(orderCommentConverter.convertAll(cartModel.getComments())).willReturn(Collections.singletonList(new CommentData()));
		given(cartModel.getQuoteReference()).willReturn(quoteModel);
		given(quoteConverter.convert(quoteModel)).willReturn(new QuoteData());
		given(entryGroupConverter.convertAll(cartModel.getEntryGroups())).willReturn(Collections.emptyList());
		given(commerceEntryGroupUtils.getGroup(any(), any())).willAnswer(invocationOnMock -> {
			final AbstractOrderData order = (AbstractOrderData) invocationOnMock.getArguments()[0];
			final Integer number = (Integer) invocationOnMock.getArguments()[1];
			return order.getRootGroups().stream().filter(group -> Objects.equals(number, group.getGroupNumber())).findAny()
					.orElseThrow(() -> new IllegalArgumentException("Unknown group #" + number));
		});
		doAnswer(invocationOnMock -> {
			final List<EntryGroupData> rootGroups = (List<EntryGroupData>) invocationOnMock.getArguments()[0];
			rootGroups.add(new EntryGroupData());
			return null;
		}).when(virtualEntryGroupStrategy).createGroup(any(), any());

		final CartData cartData = new CartData();
		cartPopulator.populate(cartModel, cartData);

		Assert.assertEquals(CART_CODE, cartData.getCode());
		Assert.assertEquals(Integer.valueOf(1), cartData.getTotalItems());
		Assert.assertEquals(1, cartData.getEntries().size());
		Assert.assertEquals(1, cartData.getComments().size());
		Assert.assertNotNull(cartData.getQuoteData());
		Assert.assertEquals(1, cartData.getRootGroups().size());
		verify(priceDataFactory).create(PriceDataType.BUY, BigDecimal.valueOf(1.2), currencyModel);
		verify(promotionsService).getPromotionResults(cartModel);
	}
}
