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
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;



public class PersistenceConfigurationProductLinkStrategyImpl implements ConfigurationProductLinkStrategy
{
	private static final Logger LOG = Logger.getLogger(PersistenceConfigurationProductLinkStrategyImpl.class);

	private ModelService modelService;
	private ProductService productService;
	private ProductConfigurationPersistenceService persistenceService;
	private UserService userService;

	@Override
	public String getConfigIdForProduct(final String productCode)
	{
		final ProductConfigurationModel configuration = getPersistenceService().getByProductCode(productCode);
		return null == configuration ? null : configuration.getConfigurationId();
	}

	@Override
	public void setConfigIdForProduct(final String productCode, final String configId)
	{
		if (null != getConfigIdForProduct(productCode))
		{
			removeConfigIdForProduct(productCode);
		}
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		final ProductConfigurationModel productConfiguration = getPersistenceService().getByConfigId(configId);
		productConfiguration.setUser(getUserService().getCurrentUser());
		productConfiguration.setProduct(Collections.singletonList(productModel));
		getModelService().save(productConfiguration);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Linking product '" + productCode + "' with config '" + productConfiguration.getConfigurationId() + "'");
		}
	}

	@Override
	public void removeConfigIdForProduct(final String productCode)
	{
		final ProductConfigurationModel productConfiguration = getPersistenceService().getByProductCode(productCode);
		if (null != productConfiguration)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Unlinking product '" + productConfiguration.getProduct().iterator().next().getCode() + "' from config '"
						+ productConfiguration.getConfigurationId() + "'");
			}
			productConfiguration.setProduct(Collections.emptyList());
			getModelService().save(productConfiguration);
		}
	}

	@Override
	public String retrieveProductCode(final String configId)
	{
		final ProductConfigurationModel productConfiguration = getPersistenceService().getByConfigId(configId);

		final Collection<ProductModel> productCollection = productConfiguration.getProduct();
		if (productCollection == null || productCollection.isEmpty())
		{
			return null;
		}
		return productConfiguration.getProduct().iterator().next().getCode();
	}

	protected ProductConfigurationPersistenceService getPersistenceService()
	{
		return persistenceService;
	}

	@Required
	public void setPersistenceService(final ProductConfigurationPersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
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


	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
