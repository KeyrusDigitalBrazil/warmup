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
package de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.cache.MasterDataCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ConfigurationMasterDataService}. Accesses a hybris cache via
 * {@link MasterDataCacheAccessService} for providing configuration related master data
 */
public class ConfigurationMasterDataServiceImpl implements ConfigurationMasterDataService
{

	private MasterDataCacheAccessService cacheAccessService;
	private MasterDataContainerResolver masterDataResolver;
	private I18NService i18NService;

	@Override
	public CPSMasterDataKnowledgeBaseContainer getMasterData(final String kbId)
	{
		if (StringUtils.isEmpty(kbId))
		{
			throw new IllegalArgumentException("KbId is not provided, expecting a non-empty string");
		}
		return getCacheAccessService().getKbContainer(kbId, getI18NService().getCurrentLocale().getLanguage());
	}

	@Override
	public String getItemName(final String kbId, final String id, final String type)
	{
		return getMasterDataResolver().getItemName(getMasterData(kbId), id, type);
	}

	@Override
	public boolean isProductMultilevel(final String kbId, final String id)
	{
		return getMasterDataResolver().isProductMultilevel(getMasterData(kbId), id);
	}

	@Override
	public String getGroupName(final String kbId, final String itemKey, final String itemType, final String groupId)
	{
		return getMasterDataResolver().getGroupName(getMasterData(kbId), itemKey, itemType, groupId);
	}

	@Override
	public CPSMasterDataCharacteristicContainer getCharacteristic(final String kbId, final String characteristicId)
	{
		return getMasterDataResolver().getCharacteristic(getMasterData(kbId), characteristicId);
	}

	@Override
	public String getValueName(final String kbId, final String characteristicId, final String valueId)
	{
		return getMasterDataResolver().getValueName(getMasterData(kbId), characteristicId, valueId);

	}

	@Override
	public List<String> getGroupCharacteristicIDs(final String kbId, final String itemKey, final String itemType,
			final String groupId)
	{
		return getMasterDataResolver().getGroupCharacteristicIDs(getMasterData(kbId), itemKey, itemType, groupId);
	}

	@Override
	public String getValuePricingKey(final String kbId, final String productId, final String characteristicId,
			final String valueId)
	{
		return getMasterDataResolver().getValuePricingKey(getMasterData(kbId), productId, characteristicId, valueId);

	}

	@Override
	public Set<String> getSpecificPossibleValueIds(final String kbId, final String productId, final String itemType,
			final String characteristicId)
	{
		return getMasterDataResolver().getSpecificPossibleValueIds(getMasterData(kbId), productId, itemType, characteristicId);
	}

	@Override
	public Set<String> getPossibleValueIds(final String kbId, final String characteristicId)
	{
		return getMasterDataResolver().getPossibleValueIds(getMasterData(kbId), characteristicId);
	}

	@Override
	public boolean isCharacteristicNumeric(final String kbId, final String csticId)
	{
		return getMasterDataResolver().isCharacteristicNumeric(getMasterData(kbId), csticId);
	}

	@Override
	public Integer getKbBuildNumber(final String kbId)
	{
		return getMasterDataResolver().getKbBuildNumber(getMasterData(kbId));
	}

	@Override
	public void removeCachedKb(final String kbId)
	{
		getCacheAccessService().removeKbContainer(kbId, getI18NService().getCurrentLocale().getLanguage());

	}

	/**
	 * @return the cacheAccessService
	 */
	protected MasterDataCacheAccessService getCacheAccessService()
	{
		return cacheAccessService;
	}

	/**
	 * @param cacheAccessService
	 *           the cacheAccessService to set
	 */
	public void setCacheAccessService(final MasterDataCacheAccessService cacheAccessService)
	{
		this.cacheAccessService = cacheAccessService;
	}

	/**
	 * @return the I18N Service
	 */
	protected I18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * @param i18nService
	 *           the i18NService to set
	 */
	@Required
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

	protected MasterDataContainerResolver getMasterDataResolver()
	{
		return masterDataResolver;
	}

	@Required
	public void setMasterDataResolver(final MasterDataContainerResolver masterDataResolver)
	{
		this.masterDataResolver = masterDataResolver;
	}
}
