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
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.facades.VariantConfigurationInfoProvider;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class AbstractOrderProductVariantPopulator
{
	private static final Logger LOG = Logger.getLogger(AbstractOrderProductVariantPopulator.class);
	private CPQConfigurableChecker cpqConfigurableChecker;
	private VariantConfigurationInfoProvider variantConfigurationInfoProvider;

	/**
	 * Transfers product variant related attributes from order entry into its DTO representation
	 *
	 * @param targetList
	 *           Order DTO entries, used to get the cart entry DTO via searching for key
	 * @param entry
	 *           Order entry model
	 */
	protected void populateAbstractOrderData(final AbstractOrderEntryModel entry, final List<OrderEntryData> targetList)
	{
		final ProductModel product = entry.getProduct();
		if (getCpqConfigurableChecker().isCPQNotChangeableVariantProduct(product))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Item " + entry.getItemtype() + " with PK " + entry.getPk() + " is a product variant ==> populating DTO.");
			}
			writeToTargetEntry(targetList, entry);
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("CartItem with PK " + entry.getPk() + " is NOT a product variant ==> skipping population of DTO.");
			}
		}
	}

	/**
	 * Writes result to target entry DTO
	 *
	 * @param targetList
	 *           Order DTO entries, used to get the cart entry DTO via searching for key
	 * @param sourceEntry
	 *           Order entry model
	 * @param features
	 *           List of features
	 */
	protected void writeToTargetEntry(final List<OrderEntryData> targetList, final AbstractOrderEntryModel sourceEntry)
	{
		final OrderEntryData targetEntry = targetList.stream() //
				.filter(entry -> entry.getEntryNumber().equals(sourceEntry.getEntryNumber())) //
				.findFirst() //
				.orElse(null);
		if (targetEntry == null)
		{
			throw new IllegalArgumentException("Target items do not match source items");
		}
		adjustTargetEntryForVariant(sourceEntry, targetEntry);
	}

	protected void adjustTargetEntryForVariant(final AbstractOrderEntryModel sourceEntry, final OrderEntryData targetEntry)
	{
		targetEntry.setItemPK(sourceEntry.getPk().toString());
		targetEntry.setConfigurationInfos(
				getVariantConfigurationInfoProvider().retrieveVariantConfigurationInfo(sourceEntry.getProduct()));
		targetEntry.getProduct().getBaseOptions().clear();
	}

	protected CPQConfigurableChecker getCpqConfigurableChecker()
	{
		return this.cpqConfigurableChecker;
	}

	/**
	 * Set helper, to check if the related product is a not-changeable product variant
	 *
	 * @param cpqConfigurableChecker
	 *           configurable checker
	 */
	@Required
	public void setCpqConfigurableChecker(final CPQConfigurableChecker cpqConfigurableChecker)
	{
		this.cpqConfigurableChecker = cpqConfigurableChecker;
	}

	protected VariantConfigurationInfoProvider getVariantConfigurationInfoProvider()
	{
		return variantConfigurationInfoProvider;
	}

	@Required
	public void setVariantConfigurationInfoProvider(final VariantConfigurationInfoProvider variantConfigurationInfoProvider)
	{
		this.variantConfigurationInfoProvider = variantConfigurationInfoProvider;
	}

}
