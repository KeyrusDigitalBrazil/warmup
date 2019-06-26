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
package de.hybris.platform.sap.productconfig.facades.impl;


import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.facades.ConfigConsistenceChecker;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationNotFoundException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ConfigurationFacade}.<br>
 */
public class ConfigurationFacadeImpl extends ConfigurationBaseFacadeImpl implements ConfigurationFacade
{
	private static final Logger LOG = Logger.getLogger(ConfigurationFacadeImpl.class);

	private ConfigConsistenceChecker configConsistenceChecker;
	private boolean conflictGroupProcessing = true;
	private ConfigurationProductLinkStrategy configurationProductLinkStrategy;
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;

	/**
	 * @param configurationProductLinkStrategy
	 *           the configurationProductLinkStrategy to set
	 */
	public void setProductLinkStrategy(final ConfigurationProductLinkStrategy configurationProductLinkStrategy)
	{
		this.configurationProductLinkStrategy = configurationProductLinkStrategy;
	}


	/**
	 * This setting is active per default but can be deactivated to ease an upgrade from previous versions.
	 *
	 * @return Are we processing conflict groups (which have been introduced in 6.0)?
	 */
	public boolean isConflictGroupProcessing()
	{
		return conflictGroupProcessing;
	}


	/**
	 * @param configConsistenceChecker
	 *           injects the consistency checker
	 */
	@Required
	public void setConfigConsistenceChecker(final ConfigConsistenceChecker configConsistenceChecker)
	{
		this.configConsistenceChecker = configConsistenceChecker;
	}

	@Override
	public ConfigurationData getConfiguration(final ConfigurationData configData)
	{
		final String configId = configData.getConfigId();
		final String productCode = null != configData.getKbKey() ? configData.getKbKey().getProductCode() : null;
		final long startTime = logFacadeCallStart("GET configuration [CONFIG_ID='%s'; PRODUCT_CODE='%s']", configId, productCode);

		final ConfigModel configModel = getConfigurationService().retrieveConfigurationModel(configId);

		if (configData.getKbKey() == null)
		{
			final KBKey kbKeyModel = configModel.getKbKey();
			final KBKeyData kbKey = new KBKeyData();
			kbKey.setProductCode(kbKeyModel.getProductCode());
			configData.setKbKey(kbKey);
		}
		populateConfigDataFromModel(configData, configModel);
		getConfigConsistenceChecker().checkConfiguration(configData);


		logFacadeCallDone("GET configuration ", startTime);
		return configData;
	}


	@Override
	public ConfigurationData getConfiguration(final KBKeyData kbKey)
	{
		final long startTime = logFacadeCallStart("GET configuration [PRODUCT_CODE='%s']", kbKey.getProductCode());

		ConfigurationData configurationDataResult = null;
		final String configId = getProductLinkStrategy().getConfigIdForProduct(kbKey.getProductCode());
		if (configId != null)
		{
			if (null != getConfigurationAbstractOrderEntryLinkStrategy().getCartEntryForConfigId(configId))
			{
				final String msg = String.format(
						"Inconsistent state detected. Configuration '%s' is linked to cart and product at the same time. Creating new configuration instead.",
						configId);
				LOG.warn(msg);
				configurationDataResult = createConfiguration(kbKey);
			}
			else
			{
				configurationDataResult = getConfigurationWithFallback(kbKey, configId);
			}
		}
		else
		{
			configurationDataResult = createConfiguration(kbKey);
		}

		logFacadeCallDone("GET configuration", startTime);
		return configurationDataResult;
	}


	protected ConfigurationData getConfigurationWithFallback(final KBKeyData kbKey, final String configId)
	{
		ConfigurationData configurationDataResult;
		try
		{
			configurationDataResult = getConfiguration(kbKey, configId);
		}
		catch (final IllegalStateException ex)
		{
			if (ex.getCause() instanceof ConfigurationNotFoundException)
			{
				LOG.info(String.format(
						"Configuration '%s' currently linked to product '%s' not found anymore. Creating default configuration instead.",
						configId, kbKey.getProductCode()));
				configurationDataResult = createConfiguration(kbKey);
			}
			else
			{
				throw ex;
			}
		}
		return configurationDataResult;
	}


	protected ConfigurationData createConfiguration(final KBKeyData kbKey)
	{
		ConfigModel configModel;
		ConfigurationData configurationDataResult;
		configModel = getConfigurationModel(kbKey);
		replaceProductForNotChangeableVariant(kbKey);
		getProductLinkStrategy().setConfigIdForProduct(kbKey.getProductCode(), configModel.getId());
		configurationDataResult = convert(kbKey, configModel);
		return configurationDataResult;
	}

	protected void replaceProductForNotChangeableVariant(final KBKeyData kbKey)
	{
		final ProductModel productModel = getProductService().getProductForCode(kbKey.getProductCode());
		if (getConfigurationVariantUtil().isCPQNotChangeableVariantProduct(productModel))
		{
			final String baseProductCode = ((VariantProductModel) productModel).getBaseProduct().getCode();
			kbKey.setProductCode(baseProductCode);
		}
	}

	protected ConfigurationData getConfiguration(final KBKeyData kbKey, final String configId)
	{
		ConfigurationData configurationDataResult;
		final ConfigurationData configurationDataInput = new ConfigurationData();
		configurationDataInput.setConfigId(configId);
		configurationDataInput.setKbKey(kbKey);
		configurationDataResult = getConfiguration(configurationDataInput);
		return configurationDataResult;
	}

	protected ConfigurationProductLinkStrategy getProductLinkStrategy()
	{
		return this.configurationProductLinkStrategy;
	}


	@Override
	protected ConfigurationData convert(final KBKeyData kbKey, final ConfigModel configModel)
	{
		final ConfigurationData config = super.convert(kbKey, configModel);
		getConfigConsistenceChecker().checkConfiguration(config);

		return config;
	}

	@Override
	public void updateConfiguration(final ConfigurationData configContent)
	{
		final String configId = configContent.getConfigId();
		final String productCode = null != configContent.getKbKey() ? configContent.getKbKey().getProductCode() : null;
		final long startTime = logFacadeCallStart("UPDATE configuration [CONFIG_ID='%s'; PRODUCT_CODE='%s']", configId,
				productCode);

		final ConfigModel configModel = getConfigurationService().retrieveConfigurationModel(configId);
		final PricingData pricingData = getConfigPricing().getPricingData(configModel);
		configContent.setPricing(pricingData);

		final InstanceModel rootInstance = configModel.getRootInstance();

		if (configContent.getGroups() != null)
		{
			for (final UiGroupData uiGroup : configContent.getGroups())
			{
				updateUiGroup(rootInstance, uiGroup);
			}
		}
		getConfigurationService().updateConfiguration(configModel);

		logFacadeCallDone("UPDATE configuration", startTime);
	}


	protected void updateUiGroup(final InstanceModel instance, final UiGroupData uiGroup)
	{

		final GroupType groupType = uiGroup.getGroupType() != null ? uiGroup.getGroupType() : GroupType.INSTANCE;

		switch (groupType)
		{
			case CSTIC_GROUP:
				// cstic group
				updateCsticGroup(instance, uiGroup);
				break;
			case INSTANCE:
				// (sub)instance
				updateSubInstances(instance, uiGroup);
				break;
			case CONFLICT:
				updateConflictGroup(instance, uiGroup);
				break;
			case CONFLICT_HEADER:
				updateConflictHeader(instance, uiGroup);
				break;
			default:
				throw new IllegalArgumentException("Group type not supported: " + groupType);
		}
	}

	protected void updateConflictHeader(final InstanceModel instance, final UiGroupData uiGroup)
	{
		final List<UiGroupData> conflictGroups = uiGroup.getSubGroups();

		if (instance != null && conflictGroups != null)
		{
			for (final UiGroupData uiSubGroup : conflictGroups)
			{
				updateUiGroup(instance, uiSubGroup);
			}
		}
	}

	protected void updateSubInstances(final InstanceModel instance, final UiGroupData uiGroup)
	{
		final InstanceModel subInstance = retrieveRelatedInstanceModel(instance, uiGroup);
		updateConflictHeader(subInstance, uiGroup);
	}


	protected void updateConflictGroup(final InstanceModel instance, final UiGroupData uiGroup)
	{
		//conflict groups might carry no cstics at all in case conflict solver cannot find the conflicting
		//assumptions
		if (!isConflictGroupProcessing() || uiGroup.getCstics() == null)
		{
			return;
		}


		for (final CsticData cstic : uiGroup.getCstics())
		{
			if (cstic.getType() != UiType.NOT_IMPLEMENTED)
			{
				final InstanceModel instanceCarryingTheConflict = getSubInstance(instance, cstic.getInstanceId());
				if (instanceCarryingTheConflict == null)
				{
					throw new IllegalStateException("No instance found for id: " + cstic.getInstanceId());
				}
				updateCsticModelFromCsticData(instanceCarryingTheConflict, cstic);
			}
		}
	}



	InstanceModel getSubInstance(final InstanceModel instance, final String instanceId)
	{
		final String id = instance.getId();
		if (id != null && id.equals(instanceId))
		{
			return instance;
		}
		for (final InstanceModel subInstance : instance.getSubInstances())
		{
			final InstanceModel foundInstance = getSubInstance(subInstance, instanceId);
			if (foundInstance != null)
			{
				return foundInstance;
			}
		}
		return null;
	}



	protected InstanceModel retrieveRelatedInstanceModel(final InstanceModel instance, final UiGroupData uiSubGroup)
	{
		InstanceModel instToReturn = null;
		final String uiGroupId = uiSubGroup.getId();
		if (uiGroupId != null)
		{
			final String instanceId = getUiKeyGenerator().retrieveInstanceId(uiGroupId);
			final List<InstanceModel> subInstances = instance.getSubInstances();
			for (final InstanceModel subInstance : subInstances)
			{
				if (subInstance.getId().equals(instanceId))
				{
					instToReturn = subInstance;
					break;
				}
			}
		}
		return instToReturn;
	}

	protected void updateCsticGroup(final InstanceModel instance, final UiGroupData csticGroup)
	{
		// we need this check for null, in the model the empty lists will be changed to null
		if (csticGroup != null && csticGroup.getCstics() != null)
		{
			for (final CsticData csticData : csticGroup.getCstics())
			{
				if (csticData.getType() != UiType.NOT_IMPLEMENTED)
				{
					updateCsticModelFromCsticData(instance, csticData);
				}
			}
		}
	}

	protected void updateCsticModelFromCsticData(final InstanceModel instance, final CsticData csticData)
	{
		final String csticName = csticData.getName();
		final CsticModel cstic = instance.getCstic(csticName);
		if (cstic == null)
		{
			throw new IllegalStateException("No cstic available at instance " + instance.getId() + " : " + csticName);
		}
		if (cstic.isChangedByFrontend())
		{
			return;
		}
		getCsticTypeMapper().updateCsticModelValuesFromData(csticData, cstic);
	}


	protected ConfigConsistenceChecker getConfigConsistenceChecker()
	{
		return configConsistenceChecker;
	}

	/**
	 * @param b
	 *           Is conflict group processing active?
	 */
	public void setConflictGroupProcessing(final boolean b)
	{
		this.conflictGroupProcessing = b;
	}

	@Override
	public int getNumberOfErrors(final String configId)
	{
		return getConfigurationService().calculateNumberOfIncompleteCsticsAndSolvableConflicts(configId);
	}


	protected ConfigurationAbstractOrderEntryLinkStrategy getConfigurationAbstractOrderEntryLinkStrategy()
	{
		return configurationAbstractOrderEntryLinkStrategy;
	}

	@Required
	public void setConfigurationAbstractOrderEntryLinkStrategy(
			final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy)
	{
		this.configurationAbstractOrderEntryLinkStrategy = configurationAbstractOrderEntryLinkStrategy;
	}
}
