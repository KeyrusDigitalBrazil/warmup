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
package de.hybris.platform.commerceservices.organization.listeners.impl;

import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.services.OrgUnitHierarchyService;
import de.hybris.platform.commerceservices.organization.services.impl.OrgUnitHierarchyException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.events.AfterInitializationEndEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Event listener for the {@link AfterInitializationEndEvent} which triggers the generation of hierarchy paths for
 * organizational units of the configured <b><code>unitType</code></b> after initialization has finished.
 *
 * @see OrgUnitHierarchyService#generateUnitPaths(Class)
 */
public class OrgUnitAfterInitializationEndEventListener extends AbstractEventListener<AfterInitializationEndEvent>
{
	private static final Logger LOG = LoggerFactory.getLogger(OrgUnitAfterInitializationEndEventListener.class);

	private OrgUnitHierarchyService orgUnitHierarchyService;
	private final Class<? extends OrgUnitModel> unitType;
	private ConfigurationService configurationService;

	/**
	 * Constructor setting the type to generate unit paths for
	 *
	 * @param unitType
	 *           the unit type to generate path values for
	 */
	public OrgUnitAfterInitializationEndEventListener(final Class<? extends OrgUnitModel> unitType)
	{
		this.unitType = unitType;
	}

	@Override
	protected void onEvent(final AfterInitializationEndEvent event)
	{
		final boolean isPathGenerationEnabled = getConfigurationService().getConfiguration()
				.getBoolean(CommerceServicesConstants.ORG_UNIT_PATH_GENERATION_ENABLED, true);
		if (!isPathGenerationEnabled)
		{
			LOG.info("Skipping generating unit paths for {}s.", unitType.getSimpleName());
			return;
		}

		try
		{
			LOG.info("Generating unit paths for {}s after initialization.", unitType.getSimpleName());
			getOrgUnitHierarchyService().generateUnitPaths(unitType);
		}
		catch (final OrgUnitHierarchyException e)
		{
			LOG.error("Generating unit paths failed!", e);
		}
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
