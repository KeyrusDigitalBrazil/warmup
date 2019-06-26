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
package de.hybris.platform.commerceservices.organization.cronjob;

import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.services.OrgUnitHierarchyService;
import de.hybris.platform.commerceservices.organization.services.impl.OrgUnitHierarchyException;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.JobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * {@link JobPerformable} that generates path values for all OrgUnit instances in the system.
 */
public class GenerateOrgUnitPathsJob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(GenerateOrgUnitPathsJob.class);
	private OrgUnitHierarchyService orgUnitHierarchyService;
	private ConfigurationService configurationService;

	private final Class<? extends OrgUnitModel> type;

	/**
	 * Constructor setting the type to generate unit paths for
	 *
	 * @param type
	 *           the type to generate path values for
	 */
	public GenerateOrgUnitPathsJob(final Class<? extends OrgUnitModel> type)
	{
		this.type = type;
	}

	@Override
	public PerformResult perform(final CronJobModel cronJob)
	{
		CronJobResult result = CronJobResult.SUCCESS;
		final boolean isPathGenerationEnabled = getConfigurationService().getConfiguration()
				.getBoolean(CommerceServicesConstants.ORG_UNIT_PATH_GENERATION_ENABLED, true);
		if (!isPathGenerationEnabled)
		{
			LOG.info("Skipping generating unit paths for {}s.", type.getSimpleName());
			return new PerformResult(result, CronJobStatus.FINISHED);
		}

		try
		{
			getOrgUnitHierarchyService().generateUnitPaths(type);
		}
		catch (final OrgUnitHierarchyException e)
		{
			LOG.error("Generation of unit paths failed", e);
			result = CronJobResult.FAILURE;
		}
		return new PerformResult(result, CronJobStatus.FINISHED);
	}

	protected OrgUnitHierarchyService getOrgUnitHierarchyService()
	{
		return orgUnitHierarchyService;
	}

	@Required
	public void setOrgUnitHierarchyService(final OrgUnitHierarchyService orgUnitHierarchyService)
	{
		this.orgUnitHierarchyService = orgUnitHierarchyService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
