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
import de.hybris.platform.site.BaseSiteService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.sap.platform.model.BaseSiteCecServiceMappingModel;
import com.sap.platform.model.CecServiceModel;
import com.sap.platform.model.CecTechnicalUserModel;
import com.sap.platform.sapcpconfiguration.service.CecTechnicalUserLookup;
import com.sap.platform.sapcpconfiguration.service.SapCpConfigurationServices;

public class DefaultCecTechnicalUserLookup implements CecTechnicalUserLookup {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCecTechnicalUserLookup.class);
	private SapCpConfigurationServices sapCpConfigurationService;
	private BaseSiteService baseSiteService;

	public SapCpConfigurationServices getSapCpConfigurationService() {
		return sapCpConfigurationService;
	}

	@Required
	public void setSapCpConfigurationService(final SapCpConfigurationServices sapCpConfigurationService) {
		this.sapCpConfigurationService = sapCpConfigurationService;
	}

	public CecTechnicalUserModel lookup(final CecServiceModel cecServiceMdoel) {
		checkArgument(cecServiceMdoel != null, "cec serviceModel must not be null");
		try {
			final BaseSiteCecServiceMappingModel baseSiteServiceMapping = getSapCpConfigurationService()
					.getBaseSiteCecServiceMappingForId(getCurrentBaseSite(), cecServiceMdoel);
			if (baseSiteServiceMapping.getCecTechnicalUser() != null) {
				return baseSiteServiceMapping.getCecTechnicalUser();
			}
			LOG.info("No Technical User Credential Found for Base Site :" + getCurrentBaseSite());
		} catch (final ModelNotFoundException e) {
			throw new SystemException(
					"No Technical User Credential Found for current basestore :" + getCurrentBaseSite());
		}
		return null;
	}

	private String getCurrentBaseSite() {
		return getBaseSiteService().getCurrentBaseSite().getUid();
	}

	public BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}

}
