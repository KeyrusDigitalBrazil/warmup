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
package com.sap.hybris.saprevenuecloudproduct.inbound;

import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.subscriptionservices.jalo.OneTimeChargeEntry;
import de.hybris.platform.subscriptionservices.model.BillingEventModel;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;


/**
 * Translator used to update paynow price for One time charges
 * 
 * @deprecated This class is deprecated since 1811. This class was used to replicate {@code OneTimeChargeEntryModel} with {@code BillingEventModel}'onetime'.
 *  Same functionality is now achieved in SAP Cloud Platform Integration Message Mapping feature.
 */
@Deprecated
public class SapRevenueCloudCreateUpdatePayNowChargeTranslator extends AbstractSpecialValueTranslator
{

	private SapRevenueCloudProductInboudHelper sapRevenueCloudProductInboudHelper;
	private static final String SAP_REVENUE_CLOUD_PRODUCT_INBOUND_HELPER = "defaultSapRevenueCloudProductInboudHelper";

	/**
	 * Sets the endTime for all the OneTime charges specific for a Subscription price Plan
	 *
	 * @param cellValue
	 *           - price plan ID
	 *
	 * @param processedItem
	 *
	 *           - current import item
	 *
	 * @return {@link Object}
	 *
	 * @throws JaloInvalidParameterException
	 */
	@Override
	public void performImport(final String pricePlanId, final Item processedItem) throws ImpExException
	{
		setSapRevenueCloudProductInboudHelper(sapRevenueCloudProductInboudHelper);
		final CatalogVersion cv = (CatalogVersion) getSapRevenueCloudProductInboudHelper().getAttributeValue(processedItem,
				SubscriptionPricePlanModel.CATALOGVERSION);

		if (StringUtils.isBlank(pricePlanId) || null == cv)
		{
			throw new IllegalArgumentException("Subscription price plan id or catalog information is missing");
		}


		//Create or update One Time Charge Entry with billing event as Paynow and associate it to the subscription price plan
		getSapRevenueCloudProductInboudHelper().createUpdatePayNowChargeEntry(pricePlanId, cv);

	}

	/**
	 * No export supported. Throws {@link ImpExException}
	 *
	 * @param obj
	 *           - imput object
	 *
	 * @return {@link String}
	 *
	 * @throws ImpExException
	 */
	@Override
	public String performExport(final Item item) throws ImpExException
	{
		throw new ImpExException("The export functionality is not supported by translator " + this.getClass().getName());
	}

	/**
	 * @return the sapRevenueCloudProductInboudHelper
	 */
	public SapRevenueCloudProductInboudHelper getSapRevenueCloudProductInboudHelper()
	{
		return sapRevenueCloudProductInboudHelper;
	}

	/**
	 * @param sapRevenueCloudProductInboudHelper
	 *           the sapRevenueCloudProductInboudHelper to set
	 */
	public void setSapRevenueCloudProductInboudHelper(final SapRevenueCloudProductInboudHelper sapRevenueCloudProductInboudHelper)
	{
		this.sapRevenueCloudProductInboudHelper = Optional.ofNullable(sapRevenueCloudProductInboudHelper)
				.orElseGet(() -> (SapRevenueCloudProductInboudHelper) Registry.getApplicationContext()
						.getBean(SAP_REVENUE_CLOUD_PRODUCT_INBOUND_HELPER));
	}


}
