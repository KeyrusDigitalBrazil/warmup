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

package de.hybris.platform.configurablebundlefacades.order.impl;


import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundlefacades.order.BundleCartFacade;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionfacades.order.SubscriptionCartFacade;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants.NEW_BUNDLE;


/**
 * Legacy implementation of {@link BundleCartFacade}.
 *
 * @deprecated since 6.4. Use {@link DefaultBundleCommerceCartFacade} instead.
 */
@Deprecated
public class DefaultBundleCartFacade extends DefaultBundleCommerceCartFacade
{
	private BundleCommerceCartService bundleCommerceCartService;
	private Converter<CommerceCartModification, CartModificationData> cartModificationConverter;
	private SubscriptionCartFacade subscriptionCartFacade;

	@Override
	@Nonnull
	public List<CartModificationData> addToCart(@Nonnull final String productCode, final long quantity, final int bundleNo,
			@Nullable final String bundleTemplateId, final boolean removeCurrentProducts) throws CommerceCartModificationException
	{
		if(bundleNo < NEW_BUNDLE)
		{
			throw new CommerceCartModificationException("The bundleNo must not be lower then '-1', given bundleNo: " + bundleNo);
		}
		final CartModel cartModel = getCartService().getSessionCart();
		final ProductModel product = getProductService().getProductForCode(productCode);
		
		final String xml = getSubscriptionCartFacade().getProductAsXML(product);
		
		BundleTemplateModel bundleTemplate = null;
		if (StringUtils.isNotEmpty(bundleTemplateId))
		{
			if (bundleNo > 0)
			{
				final List<CartEntryModel> entries = getBundleCommerceCartService().getCartEntriesForBundle(cartModel, bundleNo);
				if (CollectionUtils.isEmpty(entries) || entries.get(0).getBundleTemplate() == null)
				{
					throw new CommerceCartModificationException("Can't determine parentBundleTemplateModel");
				}
				final BundleTemplateModel parentModel = entries.get(0).getBundleTemplate().getParentTemplate();

				bundleTemplate = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId, parentModel.getVersion());
			}
			else
			{
				bundleTemplate = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId);
			}
		}

		getEntryGroupNumber(cartModel, bundleNo, bundleTemplateId);

		final List<CommerceCartModification> modifications = getBundleCommerceCartService().addToCart(cartModel, product, quantity,
				product.getUnit(), false, bundleNo, bundleTemplate, removeCurrentProducts, xml);
		return Converters.convertAll(modifications, getCartModificationConverter());
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

	@Override
	protected Converter<CommerceCartModification, CartModificationData> getCartModificationConverter()
	{
		return cartModificationConverter;
	}

	@Required
	public void setCartModificationConverter(
			final Converter<CommerceCartModification, CartModificationData> cartModificationConverter)
	{
		this.cartModificationConverter = cartModificationConverter;
	}
	
	protected SubscriptionCartFacade getSubscriptionCartFacade()
	{
		return subscriptionCartFacade;
	}

	@Required
	public void setSubscriptionCartFacade(final SubscriptionCartFacade defaultSubscriptionCartFacade)
	{
		this.subscriptionCartFacade = defaultSubscriptionCartFacade;
	}
}
