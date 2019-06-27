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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.QuoteFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.enums.DiscountType;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.OrderQuoteDiscountValuesAccessor;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.DiscountValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


/**
 * Integration test for DefaultQuoteFacadeDiscount
 */
@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:commercefacades/seller-quote-spring-test.xml" })
public class DefaultQuoteFacadeDiscountIntegrationTest extends BaseCommerceBaseTest
{
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private CartFacade cartFacade;
	@Resource
	private QuoteFacade testSellerQuoteFacade;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private UserService userService;
	@Resource
	private CartService cartService;
	@Resource
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
	@Resource
	private OrderQuoteDiscountValuesAccessor orderQuoteDiscountValuesAccessor;
	@Resource
	private ModelService modelService;
	@Resource
	private QuoteService quoteService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createDefaultUsers();

		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
		importCsv("/commerceservices/test/user-groups.impex", "utf-8");
		importCsv("/impex/essentialdata_usergroups.impex", "UTF-8");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		final UserModel user = userService.getUserForUID("john");
		userService.setCurrentUser(user);
	}

	@Test
	public void shouldApplyDiscount() throws CommerceCartModificationException, CalculationException
	{
		cartFacade.addToCart("HW1210-3422", 1);

		final QuoteData quoteData = testSellerQuoteFacade.initiateQuote();
		testSellerQuoteFacade.enableQuoteEdit(quoteData.getCode());

		// Before apply quote discount there are 2 discounts: a. regular discount, which stays and b. quote discount, which should be removed after applying the quote discount
		final List<DiscountValue> discountValues = new ArrayList<>();
		final double regularDiscountAmount = 5;
		final double quoteDiscountAmount = 10;
		final String regularDicountCode = "regularDiscountCode";
		final CartModel cartModel = cartService.getSessionCart();
		final String currencyIsoCode = cartModel.getCurrency().getIsocode();
		final DiscountValue quoteDiscount = DiscountValue.createAbsolute(CommerceServicesConstants.QUOTE_DISCOUNT_CODE,
				Double.valueOf(quoteDiscountAmount), currencyIsoCode);
		final DiscountValue regularDiscount = DiscountValue.createAbsolute(regularDicountCode,
				Double.valueOf(regularDiscountAmount), currencyIsoCode);
		discountValues.add(quoteDiscount);
		discountValues.add(regularDiscount);
		orderQuoteDiscountValuesAccessor.setQuoteDiscountValues(cartModel, Collections.singletonList(quoteDiscount));
		cartModel.setGlobalDiscountValues(discountValues);

		final double originalTotal = 57.95;
		Assert.assertEquals("Quote discount", BigDecimal.valueOf(0.0d), quoteData.getQuoteDiscounts().getValue());
		Assert.assertEquals("Order discount", BigDecimal.valueOf(0.0d), quoteData.getTotalDiscounts().getValue());
		Assert.assertEquals("Cart total", BigDecimal.valueOf(originalTotal), quoteData.getTotalPrice().getValue());

		// apply quote absolute discount
		final double absoluteDiscount = 20.0d;
		testSellerQuoteFacade.applyQuoteDiscount(Double.valueOf(absoluteDiscount), DiscountType.ABSOLUTE.toString());
		Assert.assertEquals("Cart total", BigDecimal.valueOf(originalTotal - absoluteDiscount - regularDiscountAmount),
				cartFacade.getSessionCart().getTotalPrice().getValue());

		// check applied discounts
		Assert.assertEquals("Global discount value size.", 2, cartModel.getGlobalDiscountValues().size());
		Assert.assertEquals("Global discount value code: regular.", regularDicountCode,
				cartModel.getGlobalDiscountValues().get(0).getCode());
		Assert.assertEquals("Global discount value amount: regular.", Double.valueOf(regularDiscountAmount),
				Double.valueOf(cartModel.getGlobalDiscountValues().get(0).getAppliedValue()));
		Assert.assertEquals("Global discount value code: quote.", CommerceServicesConstants.QUOTE_DISCOUNT_CODE,
				cartModel.getGlobalDiscountValues().get(1).getCode());
		Assert.assertEquals("Global discount value amount: quote.", Double.valueOf(absoluteDiscount),
				Double.valueOf(cartModel.getGlobalDiscountValues().get(1).getAppliedValue()));
		Assert.assertEquals("Quote discount value size.", 1,
				orderQuoteDiscountValuesAccessor.getQuoteDiscountValues(cartModel).size());
		Assert.assertEquals("Quote discount value code.", CommerceServicesConstants.QUOTE_DISCOUNT_CODE,
				orderQuoteDiscountValuesAccessor.getQuoteDiscountValues(cartModel).get(0).getCode());
		Assert.assertEquals("Quote discount value code.", Double.valueOf(absoluteDiscount),
				Double.valueOf(orderQuoteDiscountValuesAccessor.getQuoteDiscountValues(cartModel).get(0).getValue()));

		//  after calculationService.calculate() is invoked, regular discount is removed by platform strategy, and quote discounts stay
		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		commerceCartParameter.setEnableHooks(true);
		cartModel.setCalculated(Boolean.FALSE);
		commerceCartParameter.setCart(cartModel);
		commerceCartCalculationStrategy.calculateCart(commerceCartParameter);
		final CartData sessionCartData = cartFacade.getSessionCart();
		Assert.assertEquals("Cart total", BigDecimal.valueOf(originalTotal - absoluteDiscount),
				sessionCartData.getTotalPrice().getValue());
		Assert.assertEquals("Cart quote discount", BigDecimal.valueOf(absoluteDiscount),
				sessionCartData.getQuoteDiscounts().getValue());
		Assert.assertEquals("Global discount value size.", 1, cartModel.getGlobalDiscountValues().size());
		Assert.assertEquals("Global discount value code.", CommerceServicesConstants.QUOTE_DISCOUNT_CODE,
				cartModel.getGlobalDiscountValues().get(0).getCode());
		Assert.assertEquals("Quote discount value size.", 1,
				orderQuoteDiscountValuesAccessor.getQuoteDiscountValues(cartModel).size());
		Assert.assertEquals("Quote discount value code.", CommerceServicesConstants.QUOTE_DISCOUNT_CODE,
				orderQuoteDiscountValuesAccessor.getQuoteDiscountValues(cartModel).get(0).getCode());

		// apply percentage discount
		final int percentDiscount = 20;
		testSellerQuoteFacade.applyQuoteDiscount(Double.valueOf(percentDiscount), DiscountType.PERCENT.toString());
		Assert.assertTrue("Cart total", cartFacade.getSessionCart().getTotalPrice().getValue()
				.subtract(BigDecimal.valueOf(originalTotal - originalTotal * absoluteDiscount / 100)).doubleValue() < 0.01);

		// apply target discount
		final double targetTotal = 20.0d;
		testSellerQuoteFacade.applyQuoteDiscount(Double.valueOf(targetTotal), DiscountType.TARGET.toString());
		Assert.assertEquals("Cart total", BigDecimal.valueOf(targetTotal), cartFacade.getSessionCart().getTotalPrice().getValue());

		// remove discount
		final double discount = 0;
		testSellerQuoteFacade.applyQuoteDiscount(Double.valueOf(discount), DiscountType.ABSOLUTE.toString());
		Assert.assertEquals("Cart total", BigDecimal.valueOf(originalTotal),
				cartFacade.getSessionCart().getTotalPrice().getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotApplyQuoteDiscountIfCartNotCloneFromQuote()
	{
		testSellerQuoteFacade.applyQuoteDiscount(Double.valueOf(20), DiscountType.ABSOLUTE.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotApplyQuoteDiscountIfPercentageTooBig() throws CommerceCartModificationException
	{
		cartFacade.addToCart("HW1210-3422", 1);
		final QuoteData quoteData = testSellerQuoteFacade.initiateQuote();
		testSellerQuoteFacade.enableQuoteEdit(quoteData.getCode());
		testSellerQuoteFacade.applyQuoteDiscount(Double.valueOf(101), DiscountType.PERCENT.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotApplyQuoteDiscountIfPercentageTooSmall() throws CommerceCartModificationException
	{
		cartFacade.addToCart("HW1210-3422", 1);
		final QuoteData quoteData = testSellerQuoteFacade.initiateQuote();
		testSellerQuoteFacade.enableQuoteEdit(quoteData.getCode());
		testSellerQuoteFacade.applyQuoteDiscount(Double.valueOf(-1), DiscountType.PERCENT.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotApplyQuoteDiscountIfAbsoluteTooBig() throws CommerceCartModificationException
	{
		cartFacade.addToCart("HW1210-3422", 1);
		final QuoteData quoteData = testSellerQuoteFacade.initiateQuote();
		testSellerQuoteFacade.enableQuoteEdit(quoteData.getCode());
		final double originalTotal = 57.95;
		Assert.assertEquals("Cart total", BigDecimal.valueOf(originalTotal), quoteData.getTotalPrice().getValue());

		testSellerQuoteFacade.applyQuoteDiscount(Double.valueOf(originalTotal + 1), DiscountType.ABSOLUTE.toString());
	}

	@Test
	public void shouldNotRemoveDiscountsIfQuoteInNonBuyerOfferState() throws CommerceCartModificationException
	{
		// Default QuoteState is SELLER_DRAFT
		cartFacade.addToCart("HW1210-3422", 1);
		final QuoteData quoteData = testSellerQuoteFacade.initiateQuote();
		testSellerQuoteFacade.enableQuoteEdit(quoteData.getCode());

		final double originalTotal = 57.95;

		testSellerQuoteFacade.applyQuoteDiscount(Double.valueOf(20.0d), DiscountType.ABSOLUTE.toString());

		final QuoteData syncedQuoteData = testSellerQuoteFacade.newCart();

		Assert.assertEquals("Cart total", BigDecimal.valueOf(originalTotal - Double.valueOf(20.0d)),
				syncedQuoteData.getTotalPrice().getValue());

		testSellerQuoteFacade.enableQuoteEdit(quoteData.getCode());

		final QuoteModel latestQuoteModel = quoteService.getCurrentQuoteForCode(syncedQuoteData.getCode());
		Assert.assertTrue(CollectionUtils.isNotEmpty(orderQuoteDiscountValuesAccessor.getQuoteDiscountValues(latestQuoteModel)));
	}
}
