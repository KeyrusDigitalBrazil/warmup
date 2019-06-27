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
package de.hybris.platform.sap.sapmodel.services.impl;

import com.google.common.collect.Sets;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;


/**
 * Default implementation of SapPlatLogSysOrgService interface to provide business logic for interface
 */
public class DefaultSapPlantLogSysOrgService implements SapPlantLogSysOrgService
{

	private final static Logger LOG = Logger.getLogger(DefaultSapPlantLogSysOrgService.class);

	private ModelService modelService;

	private final BiFunction<Set<SAPPlantLogSysOrgModel>, String, Optional<SAPPlantLogSysOrgModel>> selectEntry = (
			sapPlantLogSysOrgSet, plantCode) -> sapPlantLogSysOrgSet.stream()
					.filter(entry -> entry.getPlant().getCode().contentEquals(plantCode)).findFirst();

	@Override
	public SAPLogicalSystemModel getSapLogicalSystemForPlant(BaseStoreModel baseStoreModel, String plantCode)
	{

		final Optional<SAPPlantLogSysOrgModel> sapPlantLogSysOrg = selectEntry
				.apply(getCurrentSAPConfiguration(baseStoreModel).getSapPlantLogSysOrg(), plantCode);

		if (sapPlantLogSysOrg.isPresent())
		{
			return sapPlantLogSysOrg.get().getLogSys();
		}
		else
		{
			LOG.error(String.format("No SAP logical system is maintained for the base store [%s] and plant [%s]!",
					baseStoreModel.getName(), plantCode));

			return getModelService().create(SAPLogicalSystemModel.class);
		}

	}

	@Override
	public SAPSalesOrganizationModel getSapSalesOrganizationForPlant(BaseStoreModel baseStoreModel, String plantCode)
	{

		final Optional<SAPPlantLogSysOrgModel> sapPlantLogSysOrg = selectEntry
				.apply(getCurrentSAPConfiguration(baseStoreModel).getSapPlantLogSysOrg(), plantCode);

		if (sapPlantLogSysOrg.isPresent())
		{
			return sapPlantLogSysOrg.get().getSalesOrg();
		}
		else
		{
			LOG.error(String.format("No SAP sales organization is maintained for the plant [%s] in base store [%s]!", plantCode,
					baseStoreModel.getName()));

			return getModelService().create(SAPSalesOrganizationModel.class);
		}
	}

	@Override
	public SAPPlantLogSysOrgModel getSapPlantLogSysOrgForPlant(BaseStoreModel baseStoreModel, String plantCode)
	{

		final Optional<SAPPlantLogSysOrgModel> sapPlantLogSysOrg = selectEntry
				.apply(getCurrentSAPConfiguration(baseStoreModel).getSapPlantLogSysOrg(), plantCode);

		if (sapPlantLogSysOrg.isPresent())
		{
			return sapPlantLogSysOrg.get();
		}
		else
		{
			LOG.error(
					String.format("No SAP logical system and sales organization are maintained for the plant [%s] in base store [%s]!",
							plantCode, baseStoreModel.getName()));

			SAPPlantLogSysOrgModel sapPlantLogSysOrgModel = getModelService().create(SAPPlantLogSysOrgModel.class);
			sapPlantLogSysOrgModel.setLogSys(getModelService().create(SAPLogicalSystemModel.class));
			sapPlantLogSysOrgModel.setSalesOrg(getModelService().create(SAPSalesOrganizationModel.class));

			return sapPlantLogSysOrgModel;
		}

	}

	private SAPConfigurationModel getCurrentSAPConfiguration(BaseStoreModel baseStore)
	{

		SAPConfigurationModel sapConfiguration = baseStore.getSAPConfiguration();

		if (sapConfiguration != null)
		{
			return sapConfiguration;
		}
		else
		{
			LOG.error(String.format("No SAP multiple back-ends configuration is maintained for the base store [%s]!",
					baseStore.getName()));
			SAPConfigurationModel sapConfigurationModel = getModelService().create(SAPConfigurationModel.class);
			sapConfigurationModel.setSapPlantLogSysOrg(Sets.newHashSet());
			return sapConfigurationModel;
		}

	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}


}
