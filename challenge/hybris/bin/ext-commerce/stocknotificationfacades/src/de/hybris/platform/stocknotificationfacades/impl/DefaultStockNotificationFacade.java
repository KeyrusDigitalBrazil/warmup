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
package de.hybris.platform.stocknotificationfacades.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerinterestsservices.productinterest.ProductInterestService;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.stocknotificationfacades.StockNotificationFacade;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Impl of the interface of Flash Buy facade
 */
public class DefaultStockNotificationFacade implements StockNotificationFacade
{
	private ProductInterestService productInterestService;
	private BaseStoreService baseStoreService;
	private BaseSiteService baseSiteService;
	private UserService userService;
	private ProductService productService;


	@Override
	public boolean isWatchingProduct(final ProductData product)
	{
		validateParameterNotNullStandardMessage("product", product);

		final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		final BaseSiteModel baseSite = getBaseSiteService().getCurrentBaseSite();
		final UserModel currentUser = getUserService().getCurrentUser();
		if (currentUser instanceof CustomerModel)
		{
			final CustomerModel customer = (CustomerModel) currentUser;
			return getProductInterestService()
					.getProductInterest(getProductService().getProductForCode(product.getCode()), customer,
							NotificationType.BACK_IN_STOCK, baseStore, baseSite)
					.isPresent();
		}
		return false;
	}

	protected ProductInterestService getProductInterestService()
	{
		return productInterestService;
	}

	@Required
	public void setProductInterestService(final ProductInterestService productInterestService)
	{
		this.productInterestService = productInterestService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
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

}
