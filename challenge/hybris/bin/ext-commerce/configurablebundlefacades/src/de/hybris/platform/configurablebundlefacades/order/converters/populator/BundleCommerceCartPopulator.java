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

package de.hybris.platform.configurablebundlefacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Modify the cart converter to show the first invalid bundle component in the cart (if it exists).
 *
 * @since 6.4
 */
public class BundleCommerceCartPopulator<S extends CartModel, T extends CartData> extends AbstractBundleOrderPopulator<S, T>
{

	/**
	 * Modify populate method to set the first incomplete bundle component in the cart {@link CartModel}
	 */
	@Override
	public void populate(final S source, final T target)
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);

		addEntries(source, target);
		setFirstIncompleteComponent(target);
		addPromotions(source, target);
	}

	/**
	 * Modify addEntries method to add bundle components that belong to a bundle template but not added yet to cart
	 */
	@Override
	protected void addEntries(final AbstractOrderModel source, final AbstractOrderData target)
	{
		final List<OrderEntryData> sortedEntries = getSortedEntryListBasedOnBundleAndComponent(target.getEntries());
		target.setEntries(sortedEntries);
	}

	/**
	 * Find first incomplete bundle component of each package
	 */
	protected void setFirstIncompleteComponent(final T target)
	{
		final Map<Integer, BundleTemplateData> firstInvalids = new HashMap<>();
		target.getEntries().stream()
				.filter(entry -> !entry.isValid())
				.findAny()
				.ifPresent(entry -> firstInvalids.put(entry.getBundleNo(), entry.getComponent()));
		target.setFirstIncompleteBundleComponentsMap(firstInvalids);
	}

	@Override
	protected void addPromotions(final AbstractOrderModel source, final AbstractOrderData target)
	{
		addPromotions(source, getPromotionsService().getPromotionResults(source), target);
	}

	@Override
	protected void addPromotions(final AbstractOrderModel source, final PromotionOrderResults promoOrderResults,
			final AbstractOrderData target)
	{
		if (promoOrderResults != null)
		{
			final CartData cartData = (CartData) target;
			cartData.setPotentialOrderPromotions(getPromotions(promoOrderResults.getPotentialOrderPromotions()));
			cartData.setPotentialProductPromotions(getPromotions(promoOrderResults.getPotentialProductPromotions()));
		}
	}
}
