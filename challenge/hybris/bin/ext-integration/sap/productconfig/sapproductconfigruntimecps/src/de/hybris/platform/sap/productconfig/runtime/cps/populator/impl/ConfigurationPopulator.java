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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.runtime.cps.ServiceVersionProvider;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConflict;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class ConfigurationPopulator implements Populator<CPSConfiguration, ConfigModel>
{

	static final String SAPPRODUCTCONFIG_CPS_HEADER_CONFLICT_MESSAGE = "sapproductconfig.cps.header.conflict.message";

	private ContextualConverter<CPSItem, InstanceModel, MasterDataContext> instanceModelConverter;
	private Converter<CPSConflict, SolvableConflictModel> conflictModelConverter;

	private ConfigurationMasterDataService masterDataService;
	private MasterDataContainerResolver masterDataResolver;
	private ServiceVersionProvider versionProvider;

	protected ContextualConverter<CPSItem, InstanceModel, MasterDataContext> getInstanceModelConverter()
	{
		return instanceModelConverter;
	}

	protected Converter<CPSConflict, SolvableConflictModel> getConflictModelConverter()
	{
		return conflictModelConverter;
	}

	/**
	 * @param instanceModelConverter
	 *           the instanceModelConverter to set
	 */
	@Required
	public void setInstanceModelConverter(
			final ContextualConverter<CPSItem, InstanceModel, MasterDataContext> instanceModelConverter)
	{
		this.instanceModelConverter = instanceModelConverter;
	}

	/**
	 * @param conflictModelConverter
	 *           the conflictModelConverter to set
	 */
	@Required
	public void setConflictModelConverter(final Converter<CPSConflict, SolvableConflictModel> conflictModelConverter)
	{
		this.conflictModelConverter = conflictModelConverter;
	}

	@Override
	public void populate(final CPSConfiguration source, final ConfigModel target)
	{
		final MasterDataContext ctxt = new MasterDataContext();
		ctxt.setKbCacheContainer(getMasterDataService().getMasterData(source.getKbId()));

		populateCoreAttributes(source, target, ctxt);
		populateRootItem(source, target, ctxt);
		populateConflicts(source, target);
	}

	protected void populateConflicts(final CPSConfiguration source, final ConfigModel target)
	{
		if (CollectionUtils.isEmpty(source.getConflicts()))
		{
			return;
		}

		final List<SolvableConflictModel> solvableConflicts = new ArrayList<>();
		for (final CPSConflict conflict : source.getConflicts())
		{
			final SolvableConflictModel solvableConflict = getConflictModelConverter().convert(conflict);
			solvableConflicts.add(solvableConflict);
		}

		target.setSolvableConflicts(solvableConflicts);
	}

	protected void populateRootItem(final CPSConfiguration source, final ConfigModel target, final MasterDataContext ctxt)
	{
		final CPSItem rootItem = source.getRootItem();
		target.setRootInstance(getInstanceModelConverter().convertWithContext(rootItem, ctxt));
	}

	protected void populateCoreAttributes(final CPSConfiguration source, final ConfigModel target, final MasterDataContext ctxt)
	{
		target.setComplete(source.isComplete());
		target.setConsistent(source.isConsistent());
		target.setId(source.getId());
		target.setVersion(source.getETag());
		target.setKbId(source.getKbId());
		if (source.getKbKey() != null)
		{
			target.setKbKey(new KBKeyImpl(source.getProductKey(), source.getKbKey().getName(), source.getKbKey().getLogsys(),
					source.getKbKey().getVersion()));
		}
		else
		{
			throw new IllegalStateException("KBKey not present for configuration " + source.getId());
		}
		target.setName(source.getProductKey());

		final boolean multilevel = getMasterDataResolver().isProductMultilevel(ctxt.getKbCacheContainer(), source.getProductKey());
		target.setSingleLevel(!multilevel);
	}

	protected ConfigurationMasterDataService getMasterDataService()
	{
		return masterDataService;
	}

	/**
	 * @param masterDataService
	 *           the masterDataService to set
	 */
	@Required
	public void setMasterDataService(final ConfigurationMasterDataService masterDataService)
	{
		this.masterDataService = masterDataService;
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

	protected ServiceVersionProvider getVersionProvider()
	{
		return versionProvider;
	}

	@Required
	public void setVersionProvider(final ServiceVersionProvider versionProvider)
	{
		this.versionProvider = versionProvider;
	}

}
