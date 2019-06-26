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
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.commercefacades.comment.data.CommentData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.order.CommerceOrderService;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.promotions.PromotionsService;

import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class QuotePopulatorTest
{
	private static final Date QUOTE_CREATION_TIME = new Date();
	private static final Date QUOTE_EXPIRATION_TIME = new Date();
	private static final Date QUOTE_UPDATED_TIME = new Date();
	private static final Integer QUOTE_VERSION = Integer.valueOf(1);
	private static final QuoteState QUOTE_STATE = QuoteState.BUYER_DRAFT;
	private static final String QUOTE_CODE = "code";
	private static final String ORDER_CODE = "orderCode";
	private static final Double estimatedTotal = Double.valueOf(25000.89);

	@InjectMocks
	private final QuotePopulator quotePopulator = new QuotePopulator();

	@Mock
	private AbstractPopulatingConverter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
	@Mock
	private AbstractPopulatingConverter<CommentModel, CommentData> orderCommentConverter;
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private PromotionsService promotionsService;
	@Mock
	private CommerceOrderService commerceOrderService;
	@Mock
	private QuoteModel source;
	@Mock
	private PriceData priceData;

	private QuoteData target;

	@Before
	public void setup()
	{
		target = new QuoteData();
	}

	@Test
	public void shouldPopulate()
	{
		given(source.getCode()).willReturn(QUOTE_CODE);
		given(source.getCurrency()).willReturn(mock(CurrencyModel.class));
		given(source.getComments()).willReturn(Collections.singletonList(mock(CommentModel.class)));
		given(source.getCreationtime()).willReturn(QUOTE_CREATION_TIME);
		given(source.getEntries()).willReturn(Collections.singletonList(mock(AbstractOrderEntryModel.class)));
		given(source.getExpirationTime()).willReturn(QUOTE_EXPIRATION_TIME);
		given(source.getState()).willReturn(QUOTE_STATE);
		given(source.getModifiedtime()).willReturn(QUOTE_UPDATED_TIME);
		given(source.getVersion()).willReturn(QUOTE_VERSION);
		given(source.getCartReference()).willReturn(mock(CartModel.class));
		given(source.getPreviousEstimatedTotal()).willReturn(estimatedTotal);
		final OrderModel orderModel = mock(OrderModel.class);
		given(commerceOrderService.getOrderForQuote(source)).willReturn(orderModel);
		given(orderModel.getCode()).willReturn(ORDER_CODE);
		given(quotePopulator.createPrice(source, estimatedTotal)).willReturn(priceData);

		given(orderCommentConverter.convertAll(source.getComments())).willReturn(Collections.singletonList(new CommentData()));

		quotePopulator.populate(source, target);

		Assert.assertEquals(QUOTE_CODE, target.getCode());
		Assert.assertEquals(QUOTE_CREATION_TIME, target.getCreationTime());
		Assert.assertEquals(QUOTE_EXPIRATION_TIME, target.getExpirationTime());
		Assert.assertEquals(QUOTE_STATE, target.getState());
		Assert.assertEquals(QUOTE_UPDATED_TIME, target.getUpdatedTime());
		Assert.assertEquals(QUOTE_VERSION, target.getVersion());
		Assert.assertEquals(1, target.getComments().size());
		Assert.assertEquals(ORDER_CODE, target.getOrderCode());
		Assert.assertEquals(priceData, target.getPreviousEstimatedTotal());
		Assert.assertTrue(target.getHasCart().booleanValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenSourceIsNull()
	{
		quotePopulator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenTargetIsNull()
	{
		quotePopulator.populate(source, null);
	}
}
