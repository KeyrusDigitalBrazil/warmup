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
package de.hybris.platform.sap.sapproductconfigsomservices.bolfacade.impl;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.impl.DefaultBolCartFacade;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItem;
import de.hybris.platform.sap.sapproductconfigsomservices.bolfacade.CPQBolCartFacade;

import java.util.List;


/**
 * Configurable product Facade for accessing the cart entity via the BOL
 *
 */
public class CPQDefaultBolCartFacade extends DefaultBolCartFacade implements CPQBolCartFacade
{
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolCartCPQFacade#addConfigurationToCart(de.hybris.platform.
	 * sap.productconfig.runtime.interf.model.ConfigModel)
	 */
	public String addConfigurationToCart(final ConfigModel configModel)
	{
		final String productId = getProductIdFromConfigModel(configModel);
		final CPQItem item = (CPQItem) createNewItem(productId, 1, null);
		setConfigModelForItem(configModel, item);
		updateCart();

		return item.getHandle();
	}



	/**
	 * Set configuration to given item.
	 *
	 * @param configModel
	 * @param item
	 */
	private void setConfigModelForItem(final ConfigModel configModel, final CPQItem item)
	{
		if (configModel != null)
		{
			item.setConfigurable(true);
		}
		item.setProductConfiguration(configModel);
	}

	/**
	 * Retrieves the SAP product ID from a configuration runtime representation. The product ID is taken from the root
	 * instance.
	 *
	 * @param configModel
	 * @return SAP product ID (code of hybris product model)
	 */
	protected String getProductIdFromConfigModel(final ConfigModel configModel)
	{
		final InstanceModel rootInstance = configModel.getRootInstance();
		if (rootInstance == null)
		{
			throw new ApplicationBaseRuntimeException("No root instance");
		}

		return rootInstance.getName();
	}

	@Override
	public String updateConfigurationInCart(final String key, final ConfigModel configModel)
	{
		final CPQItem item = (CPQItem) getCart().getItem(new TechKey(key));
		if (item == null)
		{
			throw new ApplicationBaseRuntimeException("Item not found for key: " + key);
		}
		final String itemHandle = handleUpdate(configModel, item);
		updateCart();
		return itemHandle;
	}



	protected String handleUpdate(final ConfigModel configModel, final CPQItem item)
	{
		final String newProductId = formatProductIdForBOL(getProductIdFromConfigModel(configModel));
		final String oldProductId = item.getProductId();
		if (!newProductId.equals(oldProductId))
		{
			return performVariantReplacement(configModel, item);
		}
		item.setProductConfiguration(configModel);
		item.setProductConfigurationDirty(true);
		return item.getHandle();
	}



	protected String performVariantReplacement(final ConfigModel configModel, final CPQItem item)
	{
		final CPQItem newItem = (CPQItem) createNewItem(getProductIdFromConfigModel(configModel), item.getQuantity().longValue(),
				null);
		setConfigModelForItem(configModel, newItem);
		//mark item holding variant for deletion
		item.setProductId("");
		return newItem.getHandle();
	}

	@Override
	public void addItemsToCart(final List<Item> items)
	{
		for (final Item item : items)
		{
			final CPQItem cpqItem = (CPQItem) item;
			final CPQItem newItem = (CPQItem) createNewItem(cpqItem.getProductId(), cpqItem.getQuantity().longValue(),
					cpqItem.getHandle());
			setConfigModelForItem(cpqItem.getProductConfiguration(), newItem);
		}
		updateCart();
	}
}
