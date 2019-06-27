/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.facades.strategy;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceAddToCartStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;

import org.springframework.beans.factory.annotation.Required;


/**
 * Class implements special treatment for the configurable products
 *
 * Adds an item to the cart for the product with product configuration. Always put the basic product in the cart even if
 * the variant exists. The product configuration does not supports variants handling
 */
public class ProductConfigAddToCartStrategy extends DefaultCommerceAddToCartStrategy
{

	private CPQConfigurableChecker cpqConfigurableChecker;
	private ConfigurationAbstractOrderEntryLinkStrategy abstractOrderEntryLinkStrategy;

	@Override
	protected void validateAddToCart(final CommerceCartParameter parameters) throws CommerceCartModificationException
	{
		final CartModel cartModel = parameters.getCart();
		final ProductModel productModel = parameters.getProduct();

		validateParameterNotNull(cartModel, "Cart model cannot be null");
		validateParameterNotNull(productModel, "Product model cannot be null");

		// First Condition, copied from super implementaion - Hybris does not allow to buy a base product if variants exist (see super implementation)
		// Second Condition, exemption for CPQ - However CPQ allows to buy a ERP base product, even if ERP Varaiants exists.
		if (productModel.getVariantType() != null && !getCpqConfigurableChecker().isCPQConfigurableProduct(productModel))
		{
			throw new CommerceCartModificationException("Choose a variant instead of the base product");
		}

		if (parameters.getQuantity() < 1)
		{
			throw new CommerceCartModificationException("Quantity must not be less than one");
		}

	}

	@Override
	protected CommerceCartModification doAddToCart(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		final CommerceCartModification commerceCartModification = super.doAddToCart(parameter);

		final ProductModel product = commerceCartModification.getEntry().getProduct();

		if (getCpqConfigurableChecker().isCPQConfiguratorApplicableProduct(product))
		{
			getAbstractOrderEntryLinkStrategy().setConfigIdForCartEntry(commerceCartModification.getEntry().getPk().toString(),
					parameter.getConfigId());
		}
		return commerceCartModification;
	}

	protected CPQConfigurableChecker getCpqConfigurableChecker()
	{
		return this.cpqConfigurableChecker;
	}

	/**
	 * Set helper, to check if the related product is CPQ configurable
	 *
	 * @param cpqConfigurableChecker
	 *           configurator checker
	 */
	@Required
	public void setCpqConfigurableChecker(final CPQConfigurableChecker cpqConfigurableChecker)
	{
		this.cpqConfigurableChecker = cpqConfigurableChecker;
	}

	protected ConfigurationAbstractOrderEntryLinkStrategy getAbstractOrderEntryLinkStrategy()
	{
		return abstractOrderEntryLinkStrategy;
	}

	/**
	 * @param abstractOrderEntryLinkStrategy
	 */
	@Required
	public void setAbstractOrderEntryLinkStrategy(final ConfigurationAbstractOrderEntryLinkStrategy abstractOrderEntryLinkStrategy)
	{
		this.abstractOrderEntryLinkStrategy = abstractOrderEntryLinkStrategy;
	}
}
