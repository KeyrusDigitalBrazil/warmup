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
package de.hybris.platform.b2b.order.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.order.B2BCartFactory;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation of the {@link CartFactory} for B2BCommerce extension
 */
public class DefaultB2BCartFactory implements B2BCartFactory
{
	private ModelService modelService;
	private CartFactory cartFactory;
	private I18NService i18nService;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private UserService userService;
	public static final Logger LOG = Logger.getLogger(DefaultB2BCartFactory.class);

	/**
	 * Creates a cart by delegating to DefaultCartFactory then sets the Cart.unit attribute.
	 * 
	 * @return A new Cart
	 */
	@Override
	public CartModel createCart()
	{
		final CartModel cart = this.getCartFactory().createCart();
		postProcessCart(cart);

		return cart;
	}

	protected void postProcessCart(final CartModel cart)
	{
		cart.setLocale(getI18nService().getCurrentLocale().toString());
		cart.setStatus(OrderStatus.CREATED);
		if (isB2BCart(cart))
		{
			final B2BUnitModel unit = getB2bUnitService().getParent((B2BCustomerModel) cart.getUser());
			Assert.notNull(unit,
					String.format("No B2BUnit associated to cart %s created by %s", cart.getCode(), cart.getUser().getUid()));
			cart.setUnit(unit);

			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Setting B2BUnit %s on Cart %s created by %s", unit.getUid(), cart.getCode(), cart.getUser()));
			}
		}
		getModelService().save(cart);
	}

	@Override
	public boolean isB2BCart(final CartModel cart)
	{
		return cart.getUser() instanceof B2BCustomerModel;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Required
	public void setCartFactory(final CartFactory cartFactory)
	{
		this.cartFactory = cartFactory;
	}

	@Required
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	protected I18NService getI18nService()
	{
		return i18nService;
	}

	protected CartFactory getCartFactory()
	{
		return cartFactory;
	}

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	protected UserService getUserService()
	{
		return userService;
	}
}
