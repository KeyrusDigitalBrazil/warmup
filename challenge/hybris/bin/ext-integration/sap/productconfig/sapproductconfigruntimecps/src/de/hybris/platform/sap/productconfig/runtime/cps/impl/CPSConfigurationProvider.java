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

import de.hybris.platform.sap.productconfig.runtime.cps.CPSConfigurationChangeAdapter;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonKbDeterminationFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.ConfigurationModificationHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.ExternalConfigurationFromVariantStrategy;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import java.util.Date;



/**
 * Communication to cloud engine
 */
public class CPSConfigurationProvider implements ConfigurationProvider
{
	private Converter<CPSConfiguration, ConfigModel> configModelConverter;
	private CharonFacade charonFacade;
	private CharonKbDeterminationFacade charonKbDeterminationFacade;
	private ExternalConfigurationFromVariantStrategy externalConfigurationFromVariantStrategy;
	private CPSConfigurationChangeAdapter configurationChangeAdapter;
	private ConfigurationModificationHandler configurationModificationHandler;


	protected ExternalConfigurationFromVariantStrategy getExternalConfigurationFromVariantStrategy()
	{
		return externalConfigurationFromVariantStrategy;
	}

	@Override
	public ConfigModel createDefaultConfiguration(final KBKey kbKey)
	{
		final CPSConfiguration cloudEngineDefaultConfiguration = charonFacade.createDefaultConfiguration(kbKey);
		return getConfigModelConverter().convert(cloudEngineDefaultConfiguration);
	}

	@Override
	public boolean updateConfiguration(final ConfigModel model) throws ConfigurationEngineException
	{
		throw new UnsupportedOperationException("CPS requires version handling - use changeConfiguration instead.");
	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String configId) throws ConfigurationEngineException
	{
		return retrieveConfigurationModel(configId, null);
	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String configId, final ConfigurationRetrievalOptions options)
			throws ConfigurationEngineException
	{
		final CPSConfiguration cloudEngineConfigurationState = getCharonFacade().getConfiguration(configId);
		final ConfigModel configModel = getConfigModelConverter().convert(cloudEngineConfigurationState);
		if (options != null && options.getDiscountList() != null)
		{
			getConfigurationModificationHandler().adjustVariantConditions(configModel, options);
		}
		return configModel;
	}

	@Override
	public String retrieveExternalConfiguration(final String configId) throws ConfigurationEngineException
	{
		return getCharonFacade().getExternalConfiguration(configId);
	}



	@Override
	public ConfigModel createConfigurationFromExternalSource(final KBKey kbKey, final String extConfig)
	{
		final CPSConfiguration configuration = getCharonFacade().createConfigurationFromExternal(extConfig, kbKey.getProductCode());
		return convertToModelRepresentation(configuration);
	}

	@Override
	public void releaseSession(final String configId)
	{
		throw new UnsupportedOperationException("CPS needs version handling - use releaseSession with version in signature.");
	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
	{
		throw new UnsupportedOperationException("CPS does not support SOM scenario with typed external configuration.");
	}

	/**
	 * This method is only intended for situations where product and date are provided as part of the KB key. If this is
	 * not the case, an exception is thrown
	 *
	 * @param kbKey
	 * @return KB ID
	 */
	protected Integer findKbId(final KBKey kbKey)
	{
		if (kbKey == null || kbKey.getProductCode() == null || kbKey.getDate() == null)
		{
			throw new IllegalArgumentException("Either kbKey, product or date are not provided");
		}
		return getCharonKbDeterminationFacade().readKbIdForDate(kbKey.getProductCode(), kbKey.getDate());
	}

	/**
	 * Set converter, target: (engine independent) models
	 *
	 * @param configModelConverter
	 */
	@Required
	public void setConfigModelConverter(final Converter<CPSConfiguration, ConfigModel> configModelConverter)
	{
		this.configModelConverter = configModelConverter;
	}

	protected Converter<CPSConfiguration, ConfigModel> getConfigModelConverter()
	{
		return configModelConverter;
	}

	protected CharonFacade getCharonFacade()
	{
		return charonFacade;
	}

	/**
	 * @param charonFacade
	 *           the charonFacade to set
	 */
	@Required
	public void setCharonFacade(final CharonFacade charonFacade)
	{
		this.charonFacade = charonFacade;
	}

	@Override
	public boolean isKbForDateExists(final String productCode, final Date kbDate)
	{
		return getCharonKbDeterminationFacade().hasKbForDate(productCode, kbDate);
	}


	@Override
	public boolean isKbVersionExists(final KBKey kbKey)
	{
		return getCharonKbDeterminationFacade().hasKBForKey(kbKey);
	}

	@Override
	public boolean isKbVersionValid(final KBKey kbKey)
	{
		return getCharonKbDeterminationFacade().hasValidKBForKey(kbKey);
	}

	/**
	 * @param charonKbDeterminationFacade
	 *           the charonKbDeterminationFacade to set
	 */
	@Required
	public void setCharonKbDeterminationFacade(final CharonKbDeterminationFacade charonKbDeterminationFacade)
	{
		this.charonKbDeterminationFacade = charonKbDeterminationFacade;
	}

	protected CharonKbDeterminationFacade getCharonKbDeterminationFacade()
	{
		return this.charonKbDeterminationFacade;
	}

	@Override
	public boolean isConfigureVariantSupported()
	{
		return true;
	}

	/**
	 * @param externalConfigurationFromVariantStrategy
	 */
	public void setExternalConfigurationFromVariantStrategy(
			final ExternalConfigurationFromVariantStrategy externalConfigurationFromVariantStrategy)
	{
		this.externalConfigurationFromVariantStrategy = externalConfigurationFromVariantStrategy;
	}

	@Override
	public ConfigModel retrieveConfigurationFromVariant(final String baseProductCode, final String variantProductCode)
	{
		final Integer currentKbIdForProduct = getCharonKbDeterminationFacade().getCurrentKbIdForProduct(baseProductCode);
		final CPSExternalConfiguration createExternalConfiguration = getExternalConfigurationFromVariantStrategy()
				.createExternalConfiguration(variantProductCode, String.valueOf(currentKbIdForProduct.intValue()));
		final CPSConfiguration runtimeConfiguration = getCharonFacade().createConfigurationFromExternal(createExternalConfiguration,
				variantProductCode);
		return convertToModelRepresentation(runtimeConfiguration);
	}

	protected ConfigModel convertToModelRepresentation(final CPSConfiguration runtimeConfiguration)
	{
		return getConfigModelConverter().convert(runtimeConfiguration);
	}

	protected CPSConfigurationChangeAdapter getConfigurationChangeAdapter()
	{
		return configurationChangeAdapter;
	}

	/**
	 * @param configurationChangeAdapter
	 *           the configurationChangeAdapter to set
	 */
	@Required
	public void setConfigurationChangeAdapter(final CPSConfigurationChangeAdapter configurationChangeAdapter)
	{
		this.configurationChangeAdapter = configurationChangeAdapter;
	}

	@Override
	public String changeConfiguration(final ConfigModel model) throws ConfigurationEngineException
	{
		final CPSConfiguration changedConfiguration = getConfigurationChangeAdapter().prepareChangedConfiguration(model);
		return getCharonFacade().updateConfiguration(changedConfiguration);
	}

	@Override
	public void releaseSession(final String configId, final String version)
	{
		getCharonFacade().releaseSession(configId, version);
	}

	@Override
	public KBKey extractKbKey(final String productCode, final String externalConfig)
	{
		return getCharonKbDeterminationFacade().parseKBKeyFromExtConfig(productCode, externalConfig);
	}

	protected ConfigurationModificationHandler getConfigurationModificationHandler()
	{
		return configurationModificationHandler;
	}

	/**
	 * @param configurationModificationHandler
	 *           the configurationModificationHandler to set
	 */
	@Required
	public void setConfigurationModificationHandler(final ConfigurationModificationHandler configurationModificationHandler)
	{
		this.configurationModificationHandler = configurationModificationHandler;
	}
}
