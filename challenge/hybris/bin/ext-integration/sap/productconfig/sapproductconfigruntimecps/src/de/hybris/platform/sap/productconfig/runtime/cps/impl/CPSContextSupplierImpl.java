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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.CPSContextSupplier;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.common.CPSContextInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingConfigurationParameterCPS;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationParameterB2B;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * Provide context for configuration service calls.
 */
public class CPSContextSupplierImpl implements CPSContextSupplier
{

	private PricingConfigurationParameterCPS pricingConfigurationParameter;
	private ConfigurationParameterB2B configurationParameterB2B;

	@Override
	public List<CPSContextInfo> retrieveContext(final String productCode)
	{
		final List<CPSContextInfo> context = new ArrayList<>();

		if (getConfigurationParameterB2B() != null && getConfigurationParameterB2B().isSupported())
		{
			addCustomerData(context);
		}

		addSalesOrgData(context);

		final String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		context.add(createContextInfo(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBAK_ERDAT, date));

		context.add(createContextInfo(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBAP_MATNR, productCode));
		context.add(createContextInfo(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBAP_KWMENG, "1"));

		return context;
	}

	protected void addCustomerData(final List<CPSContextInfo> context)
	{
		final String customerNumber = getConfigurationParameterB2B().getCustomerNumber();
		final String country = getConfigurationParameterB2B().getCountrySapCode();

		if (StringUtils.isNotEmpty(customerNumber))
		{
			context.add(createContextInfo(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBAK_KUNNR, customerNumber));
			context.add(createContextInfo(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBPA_AG_KUNNR, customerNumber));
			context.add(createContextInfo(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBPA_RG_KUNNR, customerNumber));
		}

		if (StringUtils.isNotEmpty(country))
		{
			context.add(createContextInfo(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBPA_AG_LAND1, country));
			context.add(createContextInfo(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBPA_RG_LAND1, country));
		}
	}

	protected void addSalesOrgData(final List<CPSContextInfo> context)
	{
		final String salesOrganization = getPricingConfigurationParameter().getSalesOrganization();
		final String distributionChannel = getPricingConfigurationParameter().getDistributionChannelForConditions();
		final String division = getPricingConfigurationParameter().getDivisionForConditions();

		if (StringUtils.isNotEmpty(salesOrganization))
		{
			context.add(createContextInfo(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBAK_VKORG, salesOrganization));
		}

		if (StringUtils.isNotEmpty(distributionChannel))
		{
			context.add(createContextInfo(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBAK_VTWEG, distributionChannel));
		}

		if (StringUtils.isNotEmpty(division))
		{
			context.add(createContextInfo(SapproductconfigruntimecpsConstants.CONTEXT_ATTRIBUTE_VBAK_SPART, division));
		}
	}

	protected CPSContextInfo createContextInfo(final String name, final String value)
	{
		final CPSContextInfo contextInfo = new CPSContextInfo();
		contextInfo.setName(name);
		contextInfo.setValue(value);
		return contextInfo;
	}

	/**
	 * @return the pricingConfigurationParameter
	 */
	protected PricingConfigurationParameterCPS getPricingConfigurationParameter()
	{
		return pricingConfigurationParameter;
	}

	/**
	 * @param pricingConfigurationParameter
	 *           the pricingConfigurationParameter to set
	 */
	public void setPricingConfigurationParameter(final PricingConfigurationParameterCPS pricingConfigurationParameter)
	{
		this.pricingConfigurationParameter = pricingConfigurationParameter;
	}

	/**
	 * @return the configurationParameterB2B
	 */
	protected ConfigurationParameterB2B getConfigurationParameterB2B()
	{
		return configurationParameterB2B;
	}

	/**
	 * @param configurationParameterB2B
	 *           the configurationParameterB2B to set
	 */
	public void setConfigurationParameterB2B(final ConfigurationParameterB2B configurationParameterB2B)
	{
		this.configurationParameterB2B = configurationParameterB2B;
	}
}
