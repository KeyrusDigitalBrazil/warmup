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

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Disables adding potential product to cart if they are affected by disable rules.
 *
 * @param <S>
 *    	CartModel
 * @param <T>
 *    	CartData
 * @deprecated since 6.5 - is not a part of generic bundling functionality. Should be implemented for
 * specific accelerator module if is planned to be used.
 */
@Deprecated
public class BundleCartPotentialProductDisableRulePopulator<S extends CartModel, T extends CartData> extends  // NO SONAR
		AbstractBundleOrderPopulator<S, T>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(BundleCartPotentialProductDisableRulePopulator.class);

	private ProductService productService;

	private BundleCommerceCartService bundleCommerceCartService;

	@Override
	public void populate(final S cartModel, final T cartData)
	{
		validateParameterNotNullStandardMessage("cartData", cartData);

		if (cartData.getEntries() == null)
		{
			return;
		}
		cartData.getEntries().stream()
				.filter(entry -> entry.getBundleNo() == ConfigurableBundleServicesConstants.NO_BUNDLE)
				.filter(OrderEntryData::isAddable)
				.filter(entry -> entry.getProduct() != null)
				.filter(entry -> !entry.getProduct().isDisabled())
				.filter(entry -> entry.getComponent() != null)
				.filter(entry -> {
					final ProductModel productModel = getProductService().getProductForCode(entry.getProduct().getCode());
					final BundleTemplateModel productBundle = getBundleTemplateService().getBundleTemplateForCode(
							entry.getComponent().getId(), entry.getComponent().getVersion());
					final String disableMessage = getBundleCommerceCartService()
							.checkAndGetReasonForDisabledProductInComponent(cartModel, productModel, productBundle,
									entry.getBundleNo(), false);
					return disableMessage != null;
				})
				.forEach(entry -> {
					entry.getProduct().setDisabled(true);
					entry.setAddable(false);
					entry.setRemoveable(false);
				});
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected BundleCommerceCartService getBundleCommerceCartService()
	{
		return bundleCommerceCartService;
	}

	@Required
	public void setBundleCommerceCartService(final BundleCommerceCartService bundleCommerceCartService)
	{
		this.bundleCommerceCartService = bundleCommerceCartService;
	}
}
