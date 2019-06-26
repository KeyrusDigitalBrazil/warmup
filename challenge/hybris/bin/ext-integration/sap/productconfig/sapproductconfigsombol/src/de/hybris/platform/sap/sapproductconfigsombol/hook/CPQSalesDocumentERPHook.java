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

import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapordermgmtbol.hook.SalesDocumentERPHook;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItem;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.salesdocument.backend.interf.erp.strategy.ProductConfigurationStrategy;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.salesdocument.businessobject.impl.CPQSalesDocumentImpl;

import java.util.ArrayList;
import java.util.List;


/**
 * Hook for SalesDocumentERP for after read and write document.
 */
public class CPQSalesDocumentERPHook implements SalesDocumentERPHook
{

	private ProductConfigurationStrategy productConfigurationStrategy;
	private CPQSalesDocumentImpl cPQSalesDocumentImpl;

	/**
	 * Write product configuration data to the back end.
	 *
	 * @param salesDocument
	 *           Document containing items which might have a configuration attached.
	 */

	@Override
	public void afterWriteDocument(final SalesDocument salesDocument, final JCoConnection aJCoCon)
	{
		for (final Item item : salesDocument.getItemList())
		{
			final CPQItem cpqItem = (CPQItem) item;
			if (!cpqItem.isErroneous())
			{
				final ConfigModel productConfiguration = cpqItem.getProductConfiguration();
				if (productConfiguration != null && salesDocument.isInitialized() && cpqItem.isProductConfigurationDirty())
				{
					cpqItem.setProductConfigurationDirty(false);
					getProductConfigurationStrategy().writeConfiguration(aJCoCon, productConfiguration, cpqItem.getHandle(),
							salesDocument);
					getcPQSalesDocumentImpl().setConfigId(cpqItem.getTechKey(), productConfiguration.getId());
				}
			}
		}

	}

	@Override
	public void afterReadFromBackend(final SalesDocument salesDocument, final JCoConnection aJCoCon)
	{
		//only needed for order history; during cart interaction sales configuration engine is used
		if (isOrder(salesDocument))
		{
			final List<String> configurableItems = determineConfigurableItems(salesDocument);
			if (!configurableItems.isEmpty())
			{
				getProductConfigurationStrategy().readConfiguration(aJCoCon, salesDocument, configurableItems);
			}
		}
	}

	protected boolean isOrder(final SalesDocument salesDocument)
	{
		final String salesDocNumber = salesDocument.getHeader().getSalesDocNumber();
		return salesDocNumber != null && !salesDocNumber.isEmpty();
	}

	/**
	 * @return Stratey for dealing with configurable products
	 */
	public ProductConfigurationStrategy getProductConfigurationStrategy()
	{
		return productConfigurationStrategy;
	}

	/**
	 * @param productConfigurationStrategy
	 *           the productConfigurationStrategy to set
	 */
	public void setProductConfigurationStrategy(final ProductConfigurationStrategy productConfigurationStrategy)
	{
		this.productConfigurationStrategy = productConfigurationStrategy;
	}

	protected List<String> determineConfigurableItems(final SalesDocument salesDocument)
	{
		final List<String> result = new ArrayList<>();
		for (final Item item : salesDocument.getItemList())
		{
			if (item.isConfigurable())
			{
				result.add(item.getHandle());
			}
		}
		return result;
	}

	/**
	 * @return the cPQSalesDocumentImpl
	 */
	public CPQSalesDocumentImpl getcPQSalesDocumentImpl()
	{
		return cPQSalesDocumentImpl;
	}

	/**
	 * @param cPQSalesDocumentImpl
	 *           the cPQSalesDocumentImpl to set
	 */
	public void setcPQSalesDocumentImpl(final CPQSalesDocumentImpl cPQSalesDocumentImpl)
	{
		this.cPQSalesDocumentImpl = cPQSalesDocumentImpl;
	}

}
