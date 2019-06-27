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
import de.hybris.platform.commercefacades.coupon.CouponDataFacade;
import de.hybris.platform.commercefacades.coupon.data.CouponData;
import de.hybris.platform.commercefacades.order.data.PromotionOrderEntryConsumedData;
import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.promotions.PromotionResultService;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;




@UnitTest
public class PromotionResultPopulatorTest
{
	private static final String PROMOTION_DESCRIPTION = "promoDesc";
	private static final String GIVE_AWAY_COUPON_CODE = "BUYMORE16";

	@Mock
	private PromotionResultService promotionResultService;
	@Mock
	private Converter<AbstractPromotionModel, PromotionData> promotionsConverter;
	@Mock
	private Converter<PromotionOrderEntryConsumedModel, PromotionOrderEntryConsumedData> promotionOrderEntryConsumedConverter;
	@Mock
	private CouponDataFacade couponDataFacade;

	private final PromotionResultPopulator promotionResultPopulator = new PromotionResultPopulator();

	private AbstractPopulatingConverter<PromotionResultModel, PromotionResultData> promotionResultConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		promotionResultPopulator.setPromotionOrderEntryConsumedConverter(promotionOrderEntryConsumedConverter);
		promotionResultPopulator.setPromotionResultService(promotionResultService);
		promotionResultPopulator.setPromotionsConverter(promotionsConverter);
		promotionResultPopulator.setCouponDataFacade(couponDataFacade);

		promotionResultConverter = new ConverterFactory<PromotionResultModel, PromotionResultData, PromotionResultPopulator>()
				.create(PromotionResultData.class, promotionResultPopulator);
	}


	@Test
	public void testConvert()
	{
		final CouponData coupondata = new CouponData();
		coupondata.setCouponCode(GIVE_AWAY_COUPON_CODE);
		coupondata.setCouponId(GIVE_AWAY_COUPON_CODE);
		coupondata.setActive(true);
		final PromotionResultModel source = mock(PromotionResultModel.class);
		final ProductPromotionModel productPromotionModel = mock(ProductPromotionModel.class);
		final PromotionData promotionData = mock(PromotionData.class);
		final PromotionOrderEntryConsumedModel consumedModel = mock(PromotionOrderEntryConsumedModel.class);
		final PromotionOrderEntryConsumedData consumedData = mock(PromotionOrderEntryConsumedData.class);
		final Set<String> giveAwayCouponCodeList = new HashSet<>();
		giveAwayCouponCodeList.add(GIVE_AWAY_COUPON_CODE);

		given(source.getConsumedEntries()).willReturn(Collections.singleton(consumedModel));
		given(promotionOrderEntryConsumedConverter.convert(consumedModel)).willReturn(consumedData);
		given(promotionResultService.getDescription(source)).willReturn(PROMOTION_DESCRIPTION);
		given(promotionResultService.getCouponCodesFromPromotion(source)).willReturn(Optional.of(giveAwayCouponCodeList));
		given(couponDataFacade.getCouponDetails(Matchers.anyString())).willReturn(Optional.of(coupondata));
		given(source.getPromotion()).willReturn(productPromotionModel);
		given(promotionsConverter.convert(productPromotionModel)).willReturn(promotionData);
		given(promotionOrderEntryConsumedConverter.convertAll(source.getConsumedEntries()))
				.willReturn(Collections.singletonList(consumedData));

		final PromotionResultData result = promotionResultConverter.convert(source);

		Assert.assertEquals(PROMOTION_DESCRIPTION, result.getDescription());
		Assert.assertEquals(1, result.getGiveAwayCouponCodes().size());
		Assert.assertEquals(GIVE_AWAY_COUPON_CODE, result.getGiveAwayCouponCodes().get(0).getCouponCode());
		Assert.assertEquals(1, result.getConsumedEntries().size());
		Assert.assertEquals(promotionData, result.getPromotionData());
		Assert.assertEquals(consumedData, result.getConsumedEntries().get(0));
	}

	@Test
	public void testConvertWhenCouponDataIsEmpty()
	{
		final PromotionResultModel source = mock(PromotionResultModel.class);
		final ProductPromotionModel productPromotionModel = mock(ProductPromotionModel.class);
		final PromotionData promotionData = mock(PromotionData.class);
		final PromotionOrderEntryConsumedModel consumedModel = mock(PromotionOrderEntryConsumedModel.class);
		final PromotionOrderEntryConsumedData consumedData = mock(PromotionOrderEntryConsumedData.class);
		final Set<String> giveAwayCouponCodeList = new HashSet<>();
		giveAwayCouponCodeList.add(GIVE_AWAY_COUPON_CODE);

		given(source.getConsumedEntries()).willReturn(Collections.singleton(consumedModel));
		given(promotionOrderEntryConsumedConverter.convert(consumedModel)).willReturn(consumedData);
		given(promotionResultService.getDescription(source)).willReturn(PROMOTION_DESCRIPTION);
		given(promotionResultService.getCouponCodesFromPromotion(source)).willReturn(Optional.of(giveAwayCouponCodeList));
		given(couponDataFacade.getCouponDetails(Matchers.anyString())).willReturn(Optional.empty());
		given(source.getPromotion()).willReturn(productPromotionModel);
		given(promotionsConverter.convert(productPromotionModel)).willReturn(promotionData);
		given(promotionOrderEntryConsumedConverter.convertAll(source.getConsumedEntries()))
				.willReturn(Collections.singletonList(consumedData));

		final PromotionResultData result = promotionResultConverter.convert(source);

		Assert.assertEquals(PROMOTION_DESCRIPTION, result.getDescription());
		Assert.assertEquals(Collections.emptyList(), result.getGiveAwayCouponCodes());
		Assert.assertEquals(1, result.getConsumedEntries().size());
		Assert.assertEquals(promotionData, result.getPromotionData());
		Assert.assertEquals(consumedData, result.getConsumedEntries().get(0));
	}
}
