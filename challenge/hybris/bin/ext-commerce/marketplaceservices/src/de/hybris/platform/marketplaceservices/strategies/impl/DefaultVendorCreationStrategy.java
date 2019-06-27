/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.marketplaceservices.strategies.impl;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.marketplaceservices.strategies.VendorCMSStrategy;
import de.hybris.platform.marketplaceservices.strategies.VendorCreationStrategy;
import de.hybris.platform.marketplaceservices.vendor.VendorPromotionRuleEngineContextService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.CatalogVersionToRuleEngineContextMappingModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * A default implementation of {@link VendorCreationStrategy}
 */
public class DefaultVendorCreationStrategy implements VendorCreationStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultVendorCreationStrategy.class);

	private static final String CATALOG_CODE_SUFFIX = "vendor.product.catalog.code.suffix";
	private static final String CATALOG_NAME_SUFFIX = "vendor.product.catalog.name.suffix";
	private static final String CATALOG_VERSION_NAME = "vendor.product.catalog.version";

	private static final String WAREHOUSE_CODE_SUFFIX = "vendor.warehouse.code.suffix";
	private static final String WAREHOUSE_NAME_SUFFIX = "vendor.warehouse.name.suffix";

	private static final String VENDOR_CATALOG_PERMISSION_GROUP_NAME = "vendor.catalog.permission.group.name";
	private static final String VENDOR_CATALOG_PERMISSION_GROUP_ID = "vendor.catalog.permission.group.id";
	private static final String PROMOTION_CONTEXT = "promotions-context";

	private static final String CATALOG_VERSION_READ_PRINCIPALS = "vendor.product.catalog.version.read.principals";

	private ConfigurationService configurationService;
	private BaseStoreService baseStoreService;
	private UserService userService;
	private ModelService modelService;
	private VendorCMSStrategy vendorCmsStrategy;
	private CommerceCommonI18NService commerceCommonI18NService;
	private VendorPromotionRuleEngineContextService vendorPromotionRuleEngineContextService;

	@Override
	public void createVendor(final VendorModel vendor, final boolean useCustomPage)
	{
		createCatalog(vendor);
		createWarehouse(vendor);
		createUserGroupsForVendor(vendor);
		if (useCustomPage)
		{
			createLandingPage(vendor);
		}
		getModelService().save(vendor);
	}

	protected void createCatalog(final VendorModel vendor)
	{
		final String code = vendor.getCode();
		final CatalogModel catalog = getModelService().create(CatalogModel.class);
		catalog.setId(code + getStringForConfiguration(CATALOG_CODE_SUFFIX));
		catalog.setName(code + " " + getStringForConfiguration(CATALOG_NAME_SUFFIX));
		final CatalogVersionModel catalogVersion = getModelService().create(CatalogVersionModel.class);
		catalogVersion.setActive(Boolean.TRUE);
		catalogVersion.setVersion(getStringForConfiguration(CATALOG_VERSION_NAME));
		catalogVersion.setCatalog(catalog);
		catalogVersion.setLanguages(getCommerceCommonI18NService().getAllLanguages());
		catalogVersion.setReadPrincipals(getReadPrincipals(catalogVersion));
		createPromotionRuleEngineContextMapping(catalogVersion);
		catalog.setCatalogVersions(Collections.singleton(catalogVersion));
		catalog.setActiveCatalogVersion(catalogVersion);
		vendor.setCatalog(catalog);
	}

	protected void createPromotionRuleEngineContextMapping(final CatalogVersionModel catalogVersion)
	{
		final AbstractRuleEngineContextModel ruleEngineContext = getVendorPromotionRuleEngineContextService()
				.findVendorRuleEngineContextByName(PROMOTION_CONTEXT);
		final CatalogVersionToRuleEngineContextMappingModel mappingModel = new CatalogVersionToRuleEngineContextMappingModel();
		mappingModel.setCatalogVersion(catalogVersion);
		mappingModel.setContext(ruleEngineContext);
		getModelService().save(mappingModel);
	}

	protected void createWarehouse(final VendorModel vendor)
	{
		final String code = vendor.getCode();
		final WarehouseModel warehouse = getModelService().create(WarehouseModel.class);
		warehouse.setCode(code + getStringForConfiguration(WAREHOUSE_CODE_SUFFIX));
		warehouse.setName(code + " " + getStringForConfiguration(WAREHOUSE_NAME_SUFFIX));
		warehouse.setVendor(vendor);
		warehouse.setBaseStores(getBaseStoreService().getAllBaseStores());
		warehouse.setDefault(true);
		vendor.setWarehouses(Collections.singleton(warehouse));
	}

	protected void createUserGroupsForVendor(final VendorModel vendor)
	{
		final UserGroupModel vendorCatalogPermissionGroup = getModelService().create(UserGroupModel.class);
		vendorCatalogPermissionGroup.setUid(vendor.getCode() + getStringForConfiguration(VENDOR_CATALOG_PERMISSION_GROUP_ID));
		vendorCatalogPermissionGroup.setLocName(vendor.getName() + " "
				+ getStringForConfiguration(VENDOR_CATALOG_PERMISSION_GROUP_NAME));
		vendorCatalogPermissionGroup.setReadableCatalogVersions(new ArrayList<>(vendor.getCatalog().getCatalogVersions()));
		vendorCatalogPermissionGroup.setWritableCatalogVersions(new ArrayList<>(vendor.getCatalog().getCatalogVersions()));
		vendor.setUserGroups(Collections.singleton(vendorCatalogPermissionGroup));
	}

	protected void createLandingPage(final VendorModel vendor)
	{
		getVendorCmsStrategy().prepareLandingPageForVendor(vendor);
	}

	protected String getStringForConfiguration(final String key)
	{
		final String value = getConfigurationService().getConfiguration().getString(key);
		return value == null ? "" : value;
	}

	protected List<PrincipalModel> getReadPrincipals(final CatalogVersionModel catalogVersion)
	{
		final List<PrincipalModel> readPrincipals = CollectionUtils.isEmpty(catalogVersion.getReadPrincipals()) ? new ArrayList()
				: catalogVersion.getReadPrincipals();
		final List<String> configuredUserGroupUidList = getUserGroupUidListForConfiguration();
		for (final String userGroupUid : configuredUserGroupUidList)
		{
			try
			{
				final UserGroupModel usergroup = getUserService().getUserGroupForUID(userGroupUid);
				if (!readPrincipals.contains(usergroup))
				{
					readPrincipals.add(usergroup);
				}
			}
			catch (final UnknownIdentifierException e)
			{
				LOG.warn("Invalid user group uid " + userGroupUid + " for the new vendor.");
			}
		}
		return readPrincipals;
	}

	protected List<String> getUserGroupUidListForConfiguration()
	{
		final List<String> configuredUserGroupUidList = new ArrayList();
		final String configuredUserGroup = getStringForConfiguration(CATALOG_VERSION_READ_PRINCIPALS);

		if (StringUtils.isNotBlank(configuredUserGroup))
		{
			configuredUserGroupUidList.addAll(Arrays.asList(configuredUserGroup.split(",")));
		}
		else
		{
			LOG.warn("Empty user group uid in configuration for the new vendor.");
		}
		return configuredUserGroupUidList;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected VendorCMSStrategy getVendorCmsStrategy()
	{
		return vendorCmsStrategy;
	}

	@Required
	public void setVendorCmsStrategy(final VendorCMSStrategy vendorCmsStrategy)
	{
		this.vendorCmsStrategy = vendorCmsStrategy;
	}

	protected CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	@Required
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

	protected VendorPromotionRuleEngineContextService getVendorPromotionRuleEngineContextService()
	{
		return vendorPromotionRuleEngineContextService;
	}

	public void setVendorPromotionRuleEngineContextService(
			final VendorPromotionRuleEngineContextService vendorPromotionRuleEngineContextService)
	{
		this.vendorPromotionRuleEngineContextService = vendorPromotionRuleEngineContextService;
	}


}
