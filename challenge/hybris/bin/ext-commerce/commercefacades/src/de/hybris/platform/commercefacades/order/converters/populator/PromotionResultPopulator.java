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


import static java.util.stream.Collectors.toList;

import de.hybris.platform.commercefacades.coupon.CouponDataFacade;
import de.hybris.platform.commercefacades.order.data.PromotionOrderEntryConsumedData;
import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.promotions.PromotionResultService;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 */
public class PromotionResultPopulator implements Populator<PromotionResultModel, PromotionResultData>
{
	private PromotionResultService promotionResultService;
	private Converter<AbstractPromotionModel, PromotionData> promotionsConverter;
	private Converter<PromotionOrderEntryConsumedModel, PromotionOrderEntryConsumedData> promotionOrderEntryConsumedConverter;
	private CouponDataFacade couponDataFacade;

	protected PromotionResultService getPromotionResultService()
	{
		return promotionResultService;
	}

	@Required
	public void setPromotionResultService(final PromotionResultService promotionResultService)
	{
		this.promotionResultService = promotionResultService;
	}

	protected Converter<AbstractPromotionModel, PromotionData> getPromotionsConverter()
	{
		return promotionsConverter;
	}

	@Required
	public void setPromotionsConverter(final Converter<AbstractPromotionModel, PromotionData> promotionsConverter)
	{
		this.promotionsConverter = promotionsConverter;
	}

	protected Converter<PromotionOrderEntryConsumedModel, PromotionOrderEntryConsumedData> getPromotionOrderEntryConsumedConverter()
	{
		return promotionOrderEntryConsumedConverter;
	}

	@Required
	public void setPromotionOrderEntryConsumedConverter(
			final Converter<PromotionOrderEntryConsumedModel, PromotionOrderEntryConsumedData> promotionOrderEntryConsumedConverter)
	{
		this.promotionOrderEntryConsumedConverter = promotionOrderEntryConsumedConverter;
	}

	@Override
	public void populate(final PromotionResultModel source, final PromotionResultData target)
	{
		target.setDescription(getPromotionResultService().getDescription(source));

		getPromotionResultService().getCouponCodesFromPromotion(source)
				.ifPresent(couponCodes ->
				{
					target.setGiveAwayCouponCodes(couponCodes.stream()
							.map(getCouponDataFacade()::getCouponDetails).filter(Optional::isPresent).map(Optional::get)
							.collect(toList()));


				});
		target.setPromotionData(getPromotionsConverter().convert(source.getPromotion()));
		target.setConsumedEntries(getPromotionOrderEntryConsumedConverter().convertAll(source.getConsumedEntries()));
	}

	protected CouponDataFacade getCouponDataFacade()
	{
		return couponDataFacade;
	}

	@Required
	public void setCouponDataFacade(final CouponDataFacade couponDataFacade)
	{
		this.couponDataFacade = couponDataFacade;
	}
}
