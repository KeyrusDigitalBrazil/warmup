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
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;


/**
 * Set the end date for {@code SubscriptionPricePlanModel}
 * 
 * @deprecated This class is deprecated since 1811. Use the date function is SAP Cloud Platform Message Mapping
 * {@link SapRevenueCloudSubscriptionPricePlanPostPersistenceHook} can be used for setting the end date for {@code SubscriptionPricePlanModel}
 */
@Deprecated
public class SapRevenueCloudSubscriptionPricePlanEndDateTranslator extends AbstractSpecialValueTranslator
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
	public void performImport(final String cellValue, final Item processedItem) throws ImpExException
	{
		setSapRevenueCloudProductInboudHelper(sapRevenueCloudProductInboudHelper);
		final CatalogVersion cv = (CatalogVersion) getSapRevenueCloudProductInboudHelper().getAttributeValue(processedItem,
				SubscriptionPricePlanModel.CATALOGVERSION);
		if (StringUtils.isBlank(cellValue) || null == cv)
		{
			throw new IllegalArgumentException("Subscription price plan id or catalog information is missing");
		}

		//Set the end date as current date for all the existing Subscription price plans (except the current one which is importing) associated to the specific product
		getSapRevenueCloudProductInboudHelper().processSubscriptionPricePlanEndDate(cellValue, cv);

		//Set the current subscription price plan's end date as a future date. By default set the end date as one year from now.
		try
		{
			final Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, 1);
			final Date pricePlanEndDate = cal.getTime();
			processedItem.setAttribute(SubscriptionPricePlanModel.ENDTIME, pricePlanEndDate);
		}
		catch (final JaloInvalidParameterException | JaloBusinessException e)
		{
			throw new ImpExException(e);
		}

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
