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
package de.hybris.platform.commercefacades.product.converters.populator;

import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.promotions.PromotionResultService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Converter implementation for {@link de.hybris.platform.promotions.model.AbstractPromotionModel} as source and
 * {@link de.hybris.platform.commercefacades.product.data.PromotionData} as target type.
 */
public class PromotionsPopulator implements Populator<AbstractPromotionModel, PromotionData>
{
	private CartService cartService;
	private PromotionsService promotionService;
	private PromotionResultService promotionResultService;

	@Required
	public void setPromotionResultService(final PromotionResultService promotionResultService)
	{
		this.promotionResultService = promotionResultService;
	}

	protected PromotionResultService getPromotionResultService()
	{
		return promotionResultService;
	}

	@Required
	public void setPromotionService(final PromotionsService promotionService)
	{
		this.promotionService = promotionService;
	}

	protected PromotionsService getPromotionService()
	{
		return promotionService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Override
	public void populate(final AbstractPromotionModel source, final PromotionData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		target.setEndDate(source.getEndDate());
		target.setDescription(getPromotionService().getPromotionDescription(source));
		target.setPromotionType(source.getPromotionType());
		processPromotionMessages(source, target);
	}

	protected void processPromotionMessages(final AbstractPromotionModel source, final PromotionData prototype)
	{
		if (getCartService().hasSessionCart())
		{
			final CartModel cartModel = getCartService().getSessionCart();
			if (cartModel != null)
			{

				final PromotionOrderResults promoOrderResults = getPromotionService().getPromotionResults(cartModel);

				if (promoOrderResults != null)
				{
					prototype.setCouldFireMessages(getCouldFirePromotionsMessages(promoOrderResults, source));
					prototype.setFiredMessages(getFiredPromotionsMessages(promoOrderResults, source));
				}
			}
		}
	}

	protected List<String> getCouldFirePromotionsMessages(final PromotionOrderResults promoOrderResults,
			final AbstractPromotionModel promotion)
	{
		final List<String> descriptions = new LinkedList<String>();

		if (promotion instanceof ProductPromotionModel)
		{
			addDescriptions(descriptions,
					filter(getPromotionResultService().getPotentialProductPromotions(promoOrderResults, promotion), promotion));
		}
		else
		{
			addDescriptions(descriptions,
					filter(getPromotionResultService().getPotentialOrderPromotions(promoOrderResults, promotion), promotion));

		}
		return descriptions;
	}

	protected List<String> getFiredPromotionsMessages(final PromotionOrderResults promoOrderResults,
			final AbstractPromotionModel promotion)
	{
		final List<String> descriptions = new LinkedList<String>();

		if (promotion instanceof ProductPromotionModel)
		{
			addDescriptions(descriptions,
					filter(getPromotionResultService().getFiredProductPromotions(promoOrderResults, promotion), promotion));
		}
		else
		{
			addDescriptions(descriptions,
					filter(getPromotionResultService().getFiredOrderPromotions(promoOrderResults, promotion), promotion));
		}

		return descriptions;
	}

	protected void addDescriptions(final List<String> descriptions, final List<PromotionResultModel> promotionResults)
	{
		if (promotionResults != null)
		{
			for (final PromotionResultModel promoResult : promotionResults)
			{
				descriptions.add(getPromotionResultService().getDescription(promoResult));
			}
		}
	}

	protected List<PromotionResultModel> filter(final List<PromotionResultModel> results, final AbstractPromotionModel promotion)
	{
		final List<PromotionResultModel> filteredResults = new LinkedList<PromotionResultModel>();
		for (final PromotionResultModel result : results)
		{
			if (result.getPromotion().equals(promotion))
			{
				filteredResults.add(result);
			}
		}
		return filteredResults;
	}
}
