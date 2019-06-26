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
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationMessageMapper;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticTypeMapper;
import de.hybris.platform.sap.productconfig.facades.FirstOrLastGroupType;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.facades.populator.SolvableConflictPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.ConfigurationVariantUtil;
import de.hybris.platform.sap.productconfig.services.analytics.intf.AnalyticsService;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationClassificationCacheStrategy;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Base functions for configuration facades, e.g. capabilities to create DTO representation of a configuration from the
 * model representation. <br>
 * Also see {@link ConfigurationFacadeImpl} and {@link ConfigurationCartIntegrationFacadeImpl}
 */
public class ConfigurationBaseFacadeImpl
{
	private static final Logger LOG = Logger.getLogger(ConfigurationBaseFacadeImpl.class);

	/**
	 * the property is defined in the sapproductconfigfacades project.properties to make the offering of the variant
	 * flexible, dependent on project
	 */
	private boolean offerVariantSearch = true;
	private ConfigPricing configPricing;
	private ProductDao productDao;
	private ProductService productService;
	private CsticTypeMapper csticTypeMapper;
	private SolvableConflictPopulator conflictPopulator;
	private ProductConfigurationService configurationService;
	private ConfigurationVariantUtil configurationVariantUtil;
	private UniqueUIKeyGenerator uiKeyGenerator;
	private PricingService pricingService;
	private AnalyticsService analyticsService;
	private ConfigurationClassificationCacheStrategy configurationClassificationCacheStrategy;
	private ConfigurationMessageMapper messagesMapper;

	protected static final String INTERNAL_CODE_ERP_VARIANT_PRODUCT = "ERPVariantProduct";

	/**
	 * Converts a configuration model to its DTO representation
	 *
	 * @param kbKey
	 * @param configModel
	 * @return DTO representation of model
	 */
	protected ConfigurationData convert(final KBKeyData kbKey, final ConfigModel configModel)
	{
		final ConfigurationData configData = new ConfigurationData();
		configData.setKbKey(kbKey);
		configData.setConfigId(configModel.getId());

		populateConfigDataFromModel(configData, configModel);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Configuration has " + configData.getGroups().size() + " group(s)");
		}

		return configData;
	}

	/**
	 * Populates the configuration DTO from our model. <br>
	 * <br>
	 * Consider to override in case e.g. the support for analytics is desired for multilevel products on root item level
	 * (on sub item level, analytics won't be displayed in any case). In this case change
	 * {@link ConfigurationData#setAnalyticsEnabled(boolean)}.
	 *
	 * @param configData
	 * @param configModel
	 */
	protected void populateConfigDataFromModel(final ConfigurationData configData, final ConfigModel configModel)
	{
		final List<UiGroupData> csticGroupsFlat = new ArrayList<>();
		final List<UiGroupData> csticGroups = getCsticGroupsFromModel(configModel, csticGroupsFlat);
		configData.setGroups(csticGroups);
		configData.setCsticGroupsFlat(csticGroupsFlat);
		finalizeUiGroups(configData, configModel);

		configData.setConsistent(configModel.isConsistent());
		configData.setComplete(configModel.isComplete());
		configData.setSingleLevel(configModel.isSingleLevel());
		configData.setShowLegend(isShowLegend(configData.getGroups()));
		configData.setAsyncPricingMode(getPricingService().isActive());
		configData.setPricingError(configModel.hasPricingError());
		configData.setAnalyticsEnabled(getAnalyticsService().isActive() && configModel.isSingleLevel());
		final PricingData pricingData = getConfigPricing().getPricingData(configModel);
		configData.setPricing(pricingData);

		configData.setShowVariants(showVariants(configData.getKbKey().getProductCode()));

		getMessagesMapper().mapMessagesFromModelToData(configData, configModel);

	}


	protected void finalizeUiGroups(final ConfigurationData configData, final ConfigModel configModel)
	{
		applyAdditionalPopulators(configData, configModel);
		// Mark the first and last group or the only one.
		markFirstAndLastGroup(configData.getCsticGroupsFlat());
	}

	/**
	 * This method is used to apply populators which translate {@link ConfigModel} into {@link ConfigurationData}. In
	 * this default implementation, {@link SolvableConflictPopulator } is applied.
	 *
	 * @param configData
	 * @param configModel
	 */
	protected void applyAdditionalPopulators(final ConfigurationData configData, final ConfigModel configModel)
	{
		getConflictPopulator().populate(configModel, configData);
	}

	/**
	 * Reads characteristic groups from model representation of configuration
	 *
	 * @param configModel
	 *           Configuration
	 * @param csticGroupsFlat
	 *           flat list of cstic groups in correct order
	 * @return List of UI group DTO representations
	 */
	protected List<UiGroupData> getCsticGroupsFromModel(final ConfigModel configModel, final List<UiGroupData> csticGroupsFlat)
	{
		final Map<String, ClassificationSystemCPQAttributesContainer> hybrisNamesMap = getClassificationCacheStrategy()
				.getCachedNameMap(configModel);
		return getGroupsFromInstance(configModel.getRootInstance(), hybrisNamesMap, csticGroupsFlat, 0);
	}

	/**
	 * Marks the first and last cstic-group of the whole model. <br/>
	 * If only one group exists, mark the group as "only one".
	 *
	 * @param csticGroupsFlat
	 */
	protected void markFirstAndLastGroup(final List<UiGroupData> csticGroupsFlat)
	{
		if (csticGroupsFlat != null && !csticGroupsFlat.isEmpty())
		{
			final int numberOfCsticGroups = csticGroupsFlat.size();
			if (numberOfCsticGroups == 1)
			{
				// Only one group exists
				csticGroupsFlat.get(0).setFirstOrLastGroup(FirstOrLastGroupType.ONLYONE);
			}
			else
			{
				// More than one group exists
				csticGroupsFlat.get(0).setFirstOrLastGroup(FirstOrLastGroupType.FIRST);
				csticGroupsFlat.get(numberOfCsticGroups - 1).setFirstOrLastGroup(FirstOrLastGroupType.LAST);
			}
		}
	}

	/**
	 * @param groups
	 *           List of UI groups, DTO representation
	 * @return true is at least one mandatory cstic exists
	 */
	protected boolean isShowLegend(final List<UiGroupData> groups)
	{
		if (groups == null || groups.isEmpty())
		{
			return false;
		}

		for (final UiGroupData group : groups)
		{
			for (final CsticData cstic : group.getCstics())
			{
				if (cstic.isRequired())
				{
					return true;
				}
			}

			if (isShowLegend(group.getSubGroups()))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Reads groups per instance
	 *
	 * @param instance
	 *           Instance model
	 * @param nameMap
	 *           cache
	 * @param csticGroupsFlat
	 *           flat list of cstic groups in correct order
	 * @param level
	 * @return List of UI groups
	 */
	protected List<UiGroupData> getGroupsFromInstance(final InstanceModel instance,
			final Map<String, ClassificationSystemCPQAttributesContainer> nameMap, final List<UiGroupData> csticGroupsFlat,
			final int level)
	{
		final int nextLevel = level + 1;
		final List<UiGroupData> csticGroups = new ArrayList<>();

		final List<CsticGroup> csticModelGroups = instance.retrieveCsticGroupsWithCstics();
		for (final CsticGroup csticModelGroup : csticModelGroups)
		{
			final UiGroupData csticDataGroup = createCsticGroup(csticModelGroup, instance, nameMap);
			if (csticDataGroup.getCstics() == null || csticDataGroup.getCstics().isEmpty())
			{
				continue;
			}
			csticDataGroup.setConfigurable(true);
			csticGroups.add(csticDataGroup);
			csticGroupsFlat.add(csticDataGroup);
		}

		final List<InstanceModel> subInstances = instance.getSubInstances();
		for (final InstanceModel subInstance : subInstances)
		{
			final UiGroupData uiGroup = createUiGroup(subInstance, nameMap, csticGroupsFlat, nextLevel);
			csticGroups.add(uiGroup);
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("get subgroups for instance [ID='" + instance.getId() + "';NAME='" + instance.getName() + "';NUM_GROUPS='"
					+ csticGroups.size() + "']");
		}
		return csticGroups;
	}

	/**
	 * Creates a new UI group based on the characteristic group model
	 *
	 * @param csticModelGroup
	 *           Model representation of characteristic group
	 * @param nameMap
	 * @return UI group
	 */
	protected UiGroupData createCsticGroup(final CsticGroup csticModelGroup, final InstanceModel instance,
			final Map<String, ClassificationSystemCPQAttributesContainer> nameMap)
	{
		final UiGroupData uiGroupData = new UiGroupData();

		// cstic group name is unique (inside an instance), there is no cstic group id
		// For ui groups we can use the cstic group name as ui group id as well (additional to the ui group name)
		final String csticGroupName = csticModelGroup.getName();
		final String groupKey = getUiKeyGenerator().generateGroupIdForGroup(instance, csticModelGroup);
		uiGroupData.setId(groupKey);
		uiGroupData.setName(csticGroupName);

		uiGroupData.setDescription(csticModelGroup.getDescription());
		uiGroupData.setGroupType(GroupType.CSTIC_GROUP);
		uiGroupData.setFirstOrLastGroup(FirstOrLastGroupType.INTERJACENT);

		uiGroupData.setCstics(getListOfCsticData(csticModelGroup.getCstics(), groupKey, nameMap));
		if (LOG.isDebugEnabled())
		{
			LOG.debug("create UI group for csticGroup [NAME='" + csticModelGroup.getName() + "';GROUP_PREFIX='" + groupKey
					+ "';CSTICS_IN_GROUP='" + uiGroupData.getCstics().size() + "']");
		}

		return uiGroupData;
	}

	/**
	 * Creates an UI group from an instance model.
	 *
	 * @param instance
	 *           Model representation of an instance
	 * @param nameMap
	 * @param csticGroupsFlat
	 *           flat list of cstic groups in correct order
	 * @param level
	 * @return UI group, as transformation result of the (sub) instance
	 */
	protected UiGroupData createUiGroup(final InstanceModel instance,
			final Map<String, ClassificationSystemCPQAttributesContainer> nameMap, final List<UiGroupData> csticGroupsFlat,
			final int level)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("create UI group for instance [ID='" + instance.getId() + "';NAME='" + instance.getName() + "']");
		}

		final UiGroupData uiGroup = new UiGroupData();

		final String groupId = getUiKeyGenerator().generateGroupIdForInstance(instance);
		final String groupName = instance.getName();
		uiGroup.setId(groupId);
		uiGroup.setName(groupName);
		uiGroup.setDescription(instance.getLanguageDependentName());
		uiGroup.setCollapsed(true);
		uiGroup.setCollapsedInSpecificationTree(level > 1);

		//retrieve (sub)instance product description from catalog if available
		final List<ProductModel> products = getProductDao().findProductsByCode(groupName);

		if (products != null && products.size() == 1)
		{
			final ProductModel product = products.get(0);
			final String productName = product.getName();
			if (productName != null && !productName.isEmpty())
			{
				uiGroup.setDescription(productName);
			}
			final String summaryText = product.getSummary();
			uiGroup.setSummaryText(summaryText);
		}

		// if no group (subinstance) language dependent description available at all, use the subinstance name
		if (uiGroup.getDescription() == null || uiGroup.getDescription().isEmpty())
		{
			uiGroup.setDescription("[" + groupName + "]");
		}

		uiGroup.setGroupType(GroupType.INSTANCE);

		final List<UiGroupData> subGroups = getGroupsFromInstance(instance, nameMap, csticGroupsFlat, level);
		uiGroup.setSubGroups(subGroups);
		uiGroup.setCstics(new ArrayList<CsticData>());
		uiGroup.setConfigurable(isUiGroupConfigurable(subGroups));
		final boolean oneSubGroupConfigurable = isOneSubGroupConfigurable(subGroups);
		checkAdoptSubGroup(uiGroup, subGroups, oneSubGroupConfigurable);
		uiGroup.setOneConfigurableSubGroup(oneSubGroupConfigurable);
		return uiGroup;
	}

	protected ConfigModel getConfigurationModel(final KBKeyData kbKey)
	{
		final ConfigModel configModel;
		final ProductModel productModel = getProductService().getProductForCode(kbKey.getProductCode());
		if (getConfigurationVariantUtil().isCPQVariantProduct(productModel))
		{
			// case 1a: product is a variant
			final String baseProductCode = ((VariantProductModel) productModel).getBaseProduct().getCode();
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Switching from variantProduct '%s' to baseProduct '%s'", productModel.getCode(),
						baseProductCode));
			}
			configModel = getConfigurationService().createConfigurationForVariant(baseProductCode, productModel.getCode());
		}
		else
		{
			//case 1b: product is NOT a variant
			configModel = getConfigurationService().createDefaultConfiguration(
					new KBKeyImpl(kbKey.getProductCode(), kbKey.getKbName(), kbKey.getKbLogsys(), kbKey.getKbVersion()));
		}
		return configModel;
	}


	/**
	 * Adapts sub group if it is the only configurable child of the current group, and if no other child exists
	 *
	 * @param uiGroup
	 * @param subGroups
	 * @param oneSubGroupConfigurable
	 */
	void checkAdoptSubGroup(final UiGroupData uiGroup, final List<UiGroupData> subGroups, final boolean oneSubGroupConfigurable)
	{
		if (oneSubGroupConfigurable && subGroups.size() == 1)
		{
			final UiGroupData childGroup = subGroups.get(0);
			//We need to change the description, as it should carry the one from the parent
			childGroup.setDescription(uiGroup.getDescription());
			//We need to change the name as well, as it is used to tell whether a group is a 'general' group with specific treatment
			childGroup.setName(uiGroup.getName());
		}
	}

	protected List<CsticData> getListOfCsticData(final List<CsticModel> csticModelList, final String prefix,
			final Map<String, ClassificationSystemCPQAttributesContainer> nameMap)
	{
		final List<CsticData> cstics = new ArrayList<>();
		for (final CsticModel model : csticModelList)
		{
			if (model.isConsistent() && !model.isVisible())
			{
				continue;
			}

			final CsticData csticData = csticTypeMapper.mapCsticModelToData(model, prefix, nameMap);
			getMessagesMapper().mapMessagesFromModelToData(csticData, model);
			cstics.add(csticData);
		}
		return cstics;
	}

	protected boolean isUiGroupConfigurable(final List<UiGroupData> subGroups)
	{
		if (subGroups == null || subGroups.isEmpty())
		{
			return false;
		}

		for (final UiGroupData uiGroup : subGroups)
		{
			if (uiGroup.isConfigurable())
			{
				return true;
			}

		}
		return false;
	}

	protected boolean isOneSubGroupConfigurable(final List<UiGroupData> subGroups)
	{
		if (subGroups == null || subGroups.isEmpty())
		{
			return false;
		}

		int numberOfConfigurableGroups = 0;

		for (final UiGroupData uiGroup : subGroups)
		{
			if (uiGroup.isConfigurable() && ++numberOfConfigurableGroups > 1)
			{
				return false;
			}

		}
		return numberOfConfigurableGroups == 1;
	}

	protected boolean isAnyVariantExisting(final String productCode)
	{
		final ProductModel product = productService.getProductForCode(productCode);
		return getConfigurationVariantUtil().isCPQBaseProduct(product);
	}

	protected void logFacadeCallDone(final String operation, final long startTime)
	{
		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug(String.format("%s in FACADE took  %s ms", operation, duration));
		}
	}

	protected long logFacadeCallStart(final String format, final Object... args)
	{
		long startTime = 0;
		if (LOG.isDebugEnabled())
		{
			startTime = System.currentTimeMillis();
			LOG.debug(String.format(format, args));
		}
		return startTime;
	}


	protected boolean showVariants(final String productCode)
	{
		return isOfferVariantSearch() && isAnyVariantExisting(productCode);
	}

	protected ProductConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the cpq config service, which is the service counter part for this facade
	 */
	@Required
	public void setConfigurationService(final ProductConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected SolvableConflictPopulator getConflictPopulator()
	{
		return conflictPopulator;
	}

	/**
	 * @param conflictsPopulator
	 *           populator for conflicts
	 */
	@Required
	public void setConflictPopulator(final SolvableConflictPopulator conflictsPopulator)
	{
		this.conflictPopulator = conflictsPopulator;
	}

	protected ConfigPricing getConfigPricing()
	{
		return configPricing;
	}

	/**
	 * @param configPricing
	 *           contains pricing data
	 */
	@Required
	public void setConfigPricing(final ConfigPricing configPricing)
	{
		this.configPricing = configPricing;
	}

	protected ProductDao getProductDao()
	{
		return productDao;
	}

	/**
	 * @param productDao
	 *           for accessing product master data
	 */
	@Required
	public void setProductDao(final ProductDao productDao)
	{
		this.productDao = productDao;
	}

	protected CsticTypeMapper getCsticTypeMapper()
	{
		return csticTypeMapper;
	}

	/**
	 * @param csticTypeMapper
	 *           used to map the cstic model(serice layer object) to cstsic data (facade layer object) and vice versa
	 */
	@Required
	public void setCsticTypeMapper(final CsticTypeMapper csticTypeMapper)
	{
		this.csticTypeMapper = csticTypeMapper;
	}


	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           accessing product master data related services
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected boolean isOfferVariantSearch()
	{
		return offerVariantSearch;
	}

	/**
	 * @param offerVariantSearch
	 *           only if <code>true</code> similar variants will be searched during interactive configuration on the UI
	 */
	public void setOfferVariantSearch(final boolean offerVariantSearch)
	{
		this.offerVariantSearch = offerVariantSearch;
	}

	protected ConfigurationVariantUtil getConfigurationVariantUtil()
	{
		return configurationVariantUtil;
	}

	/**
	 * @param configurationVariantUtil
	 *           for accessing variant related services
	 */
	@Required
	public void setConfigurationVariantUtil(final ConfigurationVariantUtil configurationVariantUtil)
	{
		this.configurationVariantUtil = configurationVariantUtil;
	}

	protected UniqueUIKeyGenerator getUiKeyGenerator()
	{
		return uiKeyGenerator;
	}

	/**
	 * @param uiKeyGenerator
	 *           for generating uniqueKeys
	 */
	@Required
	public void setUiKeyGenerator(final UniqueUIKeyGenerator uiKeyGenerator)
	{
		this.uiKeyGenerator = uiKeyGenerator;
	}

	protected PricingService getPricingService()
	{
		return pricingService;
	}


	/**
	 * @param pricingService
	 *           the pricingService to set
	 */
	@Required
	public void setPricingService(final PricingService pricingService)
	{
		this.pricingService = pricingService;
	}

	protected AnalyticsService getAnalyticsService()
	{
		return analyticsService;
	}

	/**
	 * @param analyticsService
	 *           the analytics service to set
	 */
	@Required
	public void setAnalyticsService(final AnalyticsService analyticsService)
	{
		this.analyticsService = analyticsService;
	}

	protected ConfigurationMessageMapper getMessagesMapper()
	{
		return messagesMapper;
	}

	/**
	 *
	 * @param messagesMapper
	 *           maps the messages from model to data for the configuration, characteristics and characteristics values
	 */
	@Required
	public void setMessagesMapper(final ConfigurationMessageMapper messagesMapper)
	{
		this.messagesMapper = messagesMapper;
	}

	protected ConfigurationClassificationCacheStrategy getClassificationCacheStrategy()
	{
		return configurationClassificationCacheStrategy;
	}

	@Required
	public void setClassificationCacheStrategy(
			final ConfigurationClassificationCacheStrategy configurationClassificationCacheStrategy)
	{
		this.configurationClassificationCacheStrategy = configurationClassificationCacheStrategy;
	}



}
