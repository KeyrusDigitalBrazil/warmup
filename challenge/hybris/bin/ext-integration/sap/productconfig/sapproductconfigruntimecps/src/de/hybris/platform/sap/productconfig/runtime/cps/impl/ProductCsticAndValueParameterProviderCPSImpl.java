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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ConfiguratorSettingsService;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonKbDeterminationFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.interf.ProductCsticAndValueParameterProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameterWithValues;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ValueParameter;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Required;


/**
 * Provider to deliver the list of cstics, with all possible values, needed for the backoffice rule editor. This
 * implementation is based on the CPS configuration engine.
 */
public class ProductCsticAndValueParameterProviderCPSImpl implements ProductCsticAndValueParameterProvider
{
	private ConfigurationMasterDataService configurationMasterDataService;
	private CharonKbDeterminationFacade charonKbDeterminationFacade;
	private BaseSiteService baseSiteService;
	private CPSBaseSiteProvider cpsBaseSiteProvider;

	private ConfiguratorSettingsService configuratorSettingsService;
	private ProductDao productDao;


	@Override
	public Map<String, CsticParameterWithValues> retrieveProductCsticsAndValuesParameters(final String productCode)
	{
		try
		{
			setCurrentBaseSite();

			if (checkCPQConfigurable(productCode))
			{

				final Integer kbId = getCharonKbDeterminationFacade().getCurrentKbIdForProduct(productCode);
				if (kbId == null)
				{
					throw new IllegalStateException("No master data for the product '" + productCode + "' found");
				}
				final Map<String, CPSMasterDataCharacteristicContainer> characteristics = getCharacteristcs(kbId.toString());

				return characteristics.values().stream()
						.collect(Collectors.toMap(CPSMasterDataCharacteristicContainer::getId, this::getCsticParameters));
			}
			else
			{
				return new HashMap<>();
			}
		}
		finally
		{
			resetCurrentBaseSite();
		}
	}

	private void resetCurrentBaseSite()
	{
		getBaseSiteService().setCurrentBaseSite((BaseSiteModel) null, false);
	}

	private void setCurrentBaseSite()
	{
		final BaseSiteModel baseSite = getCpsBaseSiteProvider().getConfiguredBaseSite();

		if ((baseSite == null))
		{
			throw new IllegalStateException("No BaseSite defined for the rule editor");
		}

		getBaseSiteService().setCurrentBaseSite(baseSite, false);
	}

	protected CsticParameterWithValues getCsticParameters(final CPSMasterDataCharacteristicContainer cstic)
	{
		final CsticParameter csticParameter = new CsticParameter();
		csticParameter.setCsticName(cstic.getId());
		csticParameter.setCsticDescription(cstic.getName());

		final List<ValueParameter> values = getValuesForCstic(cstic.getPossibleValueGlobals());

		final CsticParameterWithValues csticParameterWithValues = new CsticParameterWithValues();
		csticParameterWithValues.setCstic(csticParameter);
		csticParameterWithValues.setValues(values);

		return csticParameterWithValues;
	}

	protected List<ValueParameter> getValuesForCstic(final Map<String, CPSMasterDataPossibleValue> csticValues)
	{
		final List<ValueParameter> values = new ArrayList<>();
		for (final CPSMasterDataPossibleValue valueModel : csticValues.values())
		{
			final ValueParameter value = new ValueParameter();

			value.setValueName(valueModel.getId());
			value.setValueDescription(valueModel.getName());

			values.add(value);
		}
		return values;
	}

	protected Map<String, CPSMasterDataCharacteristicContainer> getCharacteristcs(final String kbId)
	{
		final CPSMasterDataKnowledgeBaseContainer masterData = getConfigurationMasterDataService().getMasterData(kbId);
		if (masterData == null)
		{
			throw new IllegalStateException("No master data for the product with kbId '" + kbId + "' found");
		}
		return masterData.getCharacteristics();
	}

	/**
	 * Set the conifg master data service
	 *
	 * @param configurationMasterDataService
	 *           An instance of the master data service
	 */
	@Required
	public void setConfigMasterDataService(final ConfigurationMasterDataService configurationMasterDataService)
	{
		this.configurationMasterDataService = configurationMasterDataService;
	}

	protected ConfigurationMasterDataService getConfigurationMasterDataService()
	{
		return this.configurationMasterDataService;
	}

	/**
	 * Set the kb determination facade
	 *
	 * @param charonKbDeterminationFacade
	 *           An instance of the determination facade
	 */
	@Required
	public void setCharonKbDeterminationFacade(final CharonKbDeterminationFacade charonKbDeterminationFacade)
	{
		this.charonKbDeterminationFacade = charonKbDeterminationFacade;
	}

	protected CharonKbDeterminationFacade getCharonKbDeterminationFacade()
	{
		return charonKbDeterminationFacade;
	}

	/**
	 * Set the baseSiteService
	 *
	 * @param baseSiteService
	 *           Instance of the base site service
	 */
	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	protected CPSBaseSiteProvider getCpsBaseSiteProvider()
	{
		return cpsBaseSiteProvider;
	}

	/**
	 * Set the basesite provider for CPS rules
	 *
	 * @param cpsBaseSiteProvider
	 *           BaseSite assigned to the CPS rules
	 */
	@Required
	public void setCpsBaseSiteProvider(final CPSBaseSiteProvider cpsBaseSiteProvider)
	{
		this.cpsBaseSiteProvider = cpsBaseSiteProvider;
	}


	protected ProductDao getProductDao()
	{
		return productDao;
	}

	/**
	 * @param productDao
	 *           product DAO
	 */
	@Required
	public void setProductDao(final ProductDao productDao)
	{
		this.productDao = productDao;
	}

	protected ConfiguratorSettingsService getConfiguratorSettingsService()
	{
		return configuratorSettingsService;
	}

	/**
	 * @param configuratorSettingsService
	 *           configurator settings service
	 */
	@Required
	public void setConfiguratorSettingsService(final ConfiguratorSettingsService configuratorSettingsService)
	{
		this.configuratorSettingsService = configuratorSettingsService;
	}

	protected boolean checkCPQConfigurable(final String productCode)
	{
		boolean configurable = false;
		final List<ProductModel> products = productDao.findProductsByCode(productCode);
		if (!products.isEmpty())
		{
			final ProductModel productModel = products.get(0);
			configurable = isCPQConfigurableProduct(productModel);
		}
		return configurable;
	}

	protected boolean isCPQConfigurableProduct(@Nonnull final ProductModel product)
	{
		validateParameterNotNullStandardMessage("product", product);

		if (product instanceof VariantProductModel)
		{
			return false;
		}

		return getConfiguratorSettingsService().getConfiguratorSettingsForProduct(product).stream()
				.anyMatch(this::isCPQConfigurator);
	}

	protected boolean isCPQConfigurator(final AbstractConfiguratorSettingModel configService)
	{
		return configService.getConfiguratorType() == ConfiguratorType.CPQCONFIGURATOR;
	}

}
