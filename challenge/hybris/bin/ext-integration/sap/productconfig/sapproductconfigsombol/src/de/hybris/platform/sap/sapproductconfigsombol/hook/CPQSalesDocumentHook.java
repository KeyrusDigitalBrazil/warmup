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
package de.hybris.platform.sap.sapproductconfigsombol.hook;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.bol.logging.LogCategories;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.sapordermgmtbol.hook.SalesDocumentHook;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.salesdocument.businessobject.impl.CPQSalesDocumentImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.sap.tc.logging.Severity;


/**
 * Hook implementation for SalesDocumentImpl provide release configuration after delete item.
 */
public class CPQSalesDocumentHook implements SalesDocumentHook
{
	private static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(CPQSalesDocumentHook.class.getName());
	private CPQSalesDocumentImpl cPQSalesDocumentImpl;
	private ProductConfigurationService productConfigurationService;

	@Override
	public void afterDeleteItemInBackend(final TechKey techKey)
	{
		final String configId = getcPQSalesDocumentImpl().getConfigId(techKey);
		if (configId != null)
		{
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("Release configuration: " + configId);
			}
			getProductConfigurationService().releaseSession(configId);
		}
		else
		{
			sapLogger.log(Severity.ERROR, LogCategories.APPLICATIONS,
					"Item handle: " + techKey + " not found in session, no configuration was released");
		}

	}


	@Override
	public void afterDeleteItemInBackend(final ItemList itemList)
	{
		itemList.stream().filter(item -> item.isConfigurable()).forEach(item -> afterDeleteItemInBackend(item.getTechKey()));

	}

	protected CPQSalesDocumentImpl getcPQSalesDocumentImpl()
	{
		return cPQSalesDocumentImpl;
	}


	/**
	 * @param cPQSalesDocumentImpl
	 *           the cPQSalesDocumentImpl to set
	 */
	@Required
	public void setcPQSalesDocumentImpl(final CPQSalesDocumentImpl cPQSalesDocumentImpl)
	{
		this.cPQSalesDocumentImpl = cPQSalesDocumentImpl;
	}


	@Override
	public void afterDeleteItemInBackend(final List<TechKey> itemsToDelete)
	{
		for (final TechKey techKey : itemsToDelete)
		{
			afterDeleteItemInBackend(techKey);
		}

	}


	@Override
	public void afterUpdateItemInBackend(final List<TechKey> itemsToDelete)
	{
		for (final TechKey techKey : itemsToDelete)
		{
			afterDeleteItemInBackend(techKey);
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
