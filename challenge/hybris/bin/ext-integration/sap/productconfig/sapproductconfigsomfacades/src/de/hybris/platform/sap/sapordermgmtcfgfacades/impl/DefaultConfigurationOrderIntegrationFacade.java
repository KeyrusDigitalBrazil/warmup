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
package de.hybris.platform.sap.sapordermgmtcfgfacades.impl;

import de.hybris.platform.sap.productconfig.facades.ConfigurationOrderIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.order.OrderService;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItem;
import de.hybris.platform.store.services.BaseStoreService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ConfigurationOrderIntegrationFacade} for synchronous order management
 */
public class DefaultConfigurationOrderIntegrationFacade implements ConfigurationOrderIntegrationFacade
{


	private OrderService orderService;
	private BaseStoreService baseStoreService;
	private ProductConfigurationService productConfigurationService;
	private ConfigurationOrderIntegrationFacade sapProductConfigCartIntegrationFacade;

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected ConfigurationOrderIntegrationFacade getSapProductConfigCartIntegrationFacade()
	{
		return sapProductConfigCartIntegrationFacade;
	}

	/**
	 * @param sapProductConfigCartIntegrationFacade
	 *           the sapProductConfigCartIntegrationFacade to set
	 */
	@Required
	public void setSapProductConfigCartIntegrationFacade(
			final ConfigurationOrderIntegrationFacade sapProductConfigCartIntegrationFacade)
	{
		this.sapProductConfigCartIntegrationFacade = sapProductConfigCartIntegrationFacade;
	}

	protected OrderService getOrderService()
	{
		return orderService;
	}

	@Override
	public ConfigurationOverviewData getConfiguration(final String code, final int entryNumber)
	{
		if (isSapOrderMgmtEnabled())
		{
			final Item item = orderService.getItemFromOrder(code, String.valueOf(entryNumber));
			final ConfigModel configModel = getConfiguration(item);
			final ConfigurationOverviewData result = new ConfigurationOverviewData();
			result.setId(configModel.getId());
			result.setProductCode(configModel.getRootInstance().getName());
			return result;
		}
		else
		{
			return sapProductConfigCartIntegrationFacade.getConfiguration(code, entryNumber);
		}
	}

	protected ConfigModel getConfiguration(final Item item)
	{
		final CPQItem cpqItem = (CPQItem) item;
		final Configuration productConfigurationExternal = cpqItem.getExternalConfiguration();
		enrichConfigurationFromItem(productConfigurationExternal, cpqItem);
		final ConfigModel configModel = getProductConfigurationService()
				.createConfigurationFromExternalSource(productConfigurationExternal);
		getProductConfigurationService().releaseSession(configModel.getId(), true);
		return configModel;
	}

	protected void enrichConfigurationFromItem(final Configuration productConfigurationExternal, final CPQItem item)
	{
		final KBKey kbKey = new KBKeyImpl(item.getProductId(), "", "", "", item.getKbDate());
		productConfigurationExternal.setKbKey(kbKey);
	}

	/**
	 * @param orderService
	 */
	public void setOrderService(final OrderService orderService)
	{
		this.orderService = orderService;

	}

	/**
	 * Check if synchronous order management SOM is active
	 *
	 * @return true is SOM is active
	 */
	protected boolean isSapOrderMgmtEnabled()
	{
		return getBaseStoreService().getCurrentBaseStore().getSAPConfiguration() != null
				&& getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().isSapordermgmt_enabled();

	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}


	/**
	 * Method to check if order with code is re-orderable
	 *
	 * @param orderCode
	 *           order code
	 * @return boolean is reorderable
	 */
	public boolean isReorderable(final String orderCode)
	{
		if (isSapOrderMgmtEnabled())
		{
			return false;
		}
		else
		{
			return sapProductConfigCartIntegrationFacade.isReorderable(orderCode);
		}
	}

	protected ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	/**
	 * @param productConfigurationService
	 *           the productConfigurationService to set
	 */
	@Required
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

}
