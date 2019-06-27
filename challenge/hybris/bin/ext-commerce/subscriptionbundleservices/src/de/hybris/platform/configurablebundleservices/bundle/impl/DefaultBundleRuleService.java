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

package de.hybris.platform.configurablebundleservices.bundle.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Default implementation of the bundle rule service {@link BundleRuleService}. It uses Flexible Search queries and Java
 * Code to find the lowest price (based on bundle price rules) for a product that is or will be part of a bundle.
 *
 */
public class DefaultBundleRuleService extends DefaultBundleCommerceRuleService
{

	@Override
	@Nullable
	public ChangeProductPriceBundleRuleModel getChangePriceBundleRuleForOrderEntry(@Nonnull final AbstractOrderEntryModel entry)
	{
		validateParameterNotNullStandardMessage("entry", entry);
		validateParameterNotNull(entry.getBundleTemplate(), "Cart entry model does not have a bundle template");

		final Integer bundleNo = entry.getBundleNo();

		if (bundleNo != null && bundleNo.intValue() != ConfigurableBundleServicesConstants.NO_BUNDLE)
		{
			final AbstractOrderModel order = entry.getOrder();
			final CurrencyModel currency = order.getCurrency();
			final ProductModel targetProduct = entry.getProduct();
			final AbstractOrderModel masterAbstractOrder = (entry.getOrder().getParent() == null) ? entry.getOrder() : entry
					.getOrder().getParent();
			final Set<ProductModel> otherProductsInSameBundle = getCartProductsInSameBundle(masterAbstractOrder, targetProduct,
					bundleNo.intValue());

			return getLowestPriceForTargetProductAndTemplate(entry.getBundleTemplate(), targetProduct, currency,
					otherProductsInSameBundle);
		}
		return null;
	}

	@Override
	public String createMessageForDisableRule(final DisableProductBundleRuleModel disableRule, final ProductModel product)
	{
		final List<ProductModel> productsList = new ArrayList<ProductModel>();

		if (disableRule.getConditionalProducts().contains(product))
		{
			productsList.addAll(disableRule.getTargetProducts());
		}
		else
		{
			productsList.addAll(disableRule.getConditionalProducts());
		}

		final StringBuilder productsBuffer = new StringBuilder();
		for (final ProductModel curProduct : productsList)
		{
			if (productsBuffer.length() == 0)
			{
				productsBuffer.append("'").append(curProduct.getName()).append("'");
			}
			else
			{
				productsBuffer.append(", '").append(curProduct.getName()).append("'");
			}
		}

		return getL10NService().getLocalizedString("bundleservices.validation.disableruleexists", new Object[]
		{ "'" + product.getName() + "'", productsBuffer.toString() });
	}
}
