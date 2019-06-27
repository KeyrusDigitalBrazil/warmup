/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.platform.sapcpconfiguration.service.impl;

import static com.google.common.base.Preconditions.checkArgument;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import com.sap.platform.factory.SapCharonFactory;

import com.sap.platform.model.CecServiceModel;
import com.sap.platform.model.CecTechnicalUserModel;
import com.sap.platform.sapcpconfiguration.service.CecTechnicalUserLookup;
import com.sap.platform.sapcpconfiguration.service.SapCpConfigurationServices;
import com.sap.platform.sapcpconfiguration.service.SapCpServiceFactory;

public class DefaultSapCpServiceFactory implements SapCpServiceFactory {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapCpServiceFactory.class);
	private SapCharonFactory sapCharonFactory;
	private SapCpConfigurationServices sapCpConfigurationService;
	private CecTechnicalUserLookup lookupCredentials;

	@Override
	public <T> T lookupService(final Class<T> serviceType) {
		checkArgument(serviceType != null, "serviceType must not be null");
		LOG.info("Fetching Service Type {}", serviceType.getSimpleName());
		try {
			final CecServiceModel cecService = sapCpConfigurationService
					.getCecServiceForId(serviceType.getSimpleName());
			final CecTechnicalUserModel cecTechnicalUser = lookupCredentials.lookup(cecService);
			if (cecTechnicalUser == null) {
				throw new SystemException("No Techncial User credential configuration for the given serviceType :"
						+ serviceType.getSimpleName());
			}

			final Map<String, String> sapCpConfig = sapCpConfigurationService.buildSapCpConfiguration(cecTechnicalUser,
					serviceType);

			LOG.debug(" Delegating request to charon handler. Tenant = " + cecTechnicalUser.getTenantName()
					+ " and Service = " + serviceType.getName());
			return sapCharonFactory.client(cecTechnicalUser.getTenantName(), serviceType, sapCpConfig,
					builder -> builder.build());
		} catch (final ModelNotFoundException e) {

			throw new SystemException("No Service found for the given serviceType :" + serviceType.getSimpleName(), e);
		}

	}

	public SapCharonFactory getSapCharonFactory() {
		return sapCharonFactory;
	}

	@Required
	public void setSapCharonFactory(final SapCharonFactory sapCharonFactory) {
		this.sapCharonFactory = sapCharonFactory;
	}

	public CecTechnicalUserLookup getLookupCredentials() {
		return lookupCredentials;
	}

	@Required
	public void setLookupCredentials(final CecTechnicalUserLookup lookupCredentials) {
		this.lookupCredentials = lookupCredentials;
	}

	public SapCpConfigurationServices getSapCpConfigurationService() {
		return sapCpConfigurationService;
	}

	@Required
	public void setSapCpConfigurationService(final SapCpConfigurationServices sapCpConfigurationService) {
		this.sapCpConfigurationService = sapCpConfigurationService;
	}

}
