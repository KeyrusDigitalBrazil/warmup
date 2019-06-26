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
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.sapmodel.services.SalesAreaService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;
import com.google.common.base.Preconditions;



/**
 * Default implementation of {@link PricingConfigurationParameter}
 */
public class DefaultPricingConfigurationParameter implements PricingConfigurationParameter
{

	private SalesAreaService commonSalesAreaService;
	private BaseStoreService baseStoreService;

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param commonSalesAreaService
	 *           common sales area service
	 */
	@Required
	public void setCommonSalesAreaService(final SalesAreaService commonSalesAreaService)
	{
		this.commonSalesAreaService = commonSalesAreaService;
	}

	@Override
	public boolean isPricingSupported()
	{
		return isTrue(getSAPConfiguration().getSapproductconfig_enable_pricing());
	}

	@Override
	public String getSalesOrganization()
	{
		return commonSalesAreaService.getSalesOrganization();
	}

	@Override
	public String getDistributionChannelForConditions()
	{
		return commonSalesAreaService.getDistributionChannelForConditions();
	}

	@Override
	public String getDivisionForConditions()
	{
		return commonSalesAreaService.getDivisionForConditions();
	}

	@Override
	public String retrieveCurrencySapCode(final CurrencyModel currencyModel)
	{
		return Optional.ofNullable(currencyModel).map(CurrencyModel::getSapCode).orElse(null);
	}

	@Override
	public String retrieveUnitSapCode(final UnitModel unitModel)
	{
		return Optional.ofNullable(unitModel).map(UnitModel::getSapCode).orElse(null);
	}

	@Override
	public boolean showBasePriceAndSelectedOptions()
	{
		return isTrue(getSAPConfiguration().getSapproductconfig_show_baseprice_and_options());
	}

	@Override
	public boolean showDeltaPrices()
	{
		return isTrue(getSAPConfiguration().getSapproductconfig_show_deltaprices());
	}

	@Override
	public String retrieveCurrencyIsoCode(final CurrencyModel currencyModel)
	{
		return Optional.ofNullable(currencyModel).map(CurrencyModel::getIsocode).orElse(null);
	}

	@Override
	public String retrieveUnitIsoCode(final UnitModel unitModel)
	{
		return Optional.ofNullable(unitModel).map(UnitModel::getCode).orElse(null);
	}


	/**
	 * @param baseStoreService
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;

	}

	protected SAPConfigurationModel getSAPConfiguration()
	{
		Preconditions.checkNotNull(getBaseStoreService(), "No baseStoreService available");
		final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		Preconditions.checkNotNull(baseStore, "No baseStore available");
		final SAPConfigurationModel sapConfiguration = baseStore.getSAPConfiguration();
		Preconditions.checkNotNull(sapConfiguration, "No SAPConfiguration available");
		return sapConfiguration;

	}

	protected boolean isTrue(final Boolean evaluate)
	{
		if (evaluate != null)
		{
			return evaluate.booleanValue();
		}
		return false;
	}

}
