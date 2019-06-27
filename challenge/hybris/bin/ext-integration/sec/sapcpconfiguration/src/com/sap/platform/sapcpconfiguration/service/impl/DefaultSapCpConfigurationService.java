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
import static com.sap.platform.constants.SapcpconfigurationConstants.SAPCP_CLIENT_SCOPE;
import static com.sap.platform.constants.SapcpconfigurationConstants.SAPCP_CLIENT_URL;
import static com.sap.platform.constants.SapcpconfigurationConstants.SAPCP_OAUTH_CLIENTID;
import static com.sap.platform.constants.SapcpconfigurationConstants.SAPCP_OAUTH_CLIENTSECRET;
import static com.sap.platform.constants.SapcpconfigurationConstants.SAPCP_OAUTH_URL;
import static com.sap.platform.constants.SapcpconfigurationConstants.SAPCP_TENANT;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Required;

import com.sap.platform.enums.ServiceClient;
import com.sap.platform.model.BaseSiteCecServiceMappingModel;
import com.sap.platform.model.CecServiceModel;
import com.sap.platform.model.CecTechnicalUserModel;
import com.sap.platform.sapcpconfiguration.service.SapCpConfigurationServices;


public class DefaultSapCpConfigurationService implements SapCpConfigurationServices {

	private ModelService modelService;
	private FlexibleSearchService flexibleSearchService;

	public ModelService getModelService() {
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService) {
		this.modelService = modelService;
	}

	public FlexibleSearchService getFlexibleSearchService() {
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
		this.flexibleSearchService = flexibleSearchService;
	}

	@Override
	public <T> Map<String, String> buildSapCpConfiguration(final CecTechnicalUserModel clientCredential,
			final Class<T> clientType) {
		checkArgument(clientCredential != null, "Technical User credential must not be null");
		checkArgument(clientType != null, "serviceType must not be null");

		final Map<String, String> config = new HashMap();

		buildTechnicalUserCredentialConfig(clientCredential, config);

		buildServiceConfig(clientType.getSimpleName(), config);

		return config;

	}

	private void buildServiceConfig(final String simpleName, final Map<String, String> config) {
		checkArgument(simpleName != null, "serviceId must not be null");
		checkArgument(config != null, "config must not be null");

		final CecServiceModel cecService = getCecServiceForId(simpleName);

		config.put(SAPCP_CLIENT_URL, cecService.getServiceURL());

		if (MapUtils.isNotEmpty(cecService.getAdditionalConfigurations())) {
			config.putAll(cecService.getAdditionalConfigurations());
		}

	}

	private static void buildTechnicalUserCredentialConfig(final CecTechnicalUserModel clientCredential,
			final Map<String, String> config) {
		checkArgument(clientCredential != null, "Technical User credential must not be null");
		checkArgument(config != null, "config must not be null");

		config.put(SAPCP_OAUTH_URL, clientCredential.getOauthURL());
		config.put(SAPCP_OAUTH_CLIENTID, clientCredential.getTechnicalUser());
		config.put(SAPCP_OAUTH_CLIENTSECRET, clientCredential.getPassword());
		config.put(SAPCP_TENANT, clientCredential.getTenantName());
		config.put(SAPCP_CLIENT_SCOPE, clientCredential.getTenantName()); // Needing it for authentication

	}

	@Override
	public CecServiceModel getCecServiceForId(final String id) {

		checkArgument(id != null, "Cec Servcie configuration must not be null");
		final CecServiceModel cecService = getModelService().create(CecServiceModel.class);
		cecService.setIdentifier(ServiceClient.valueOf(id.toUpperCase()));
		return flexibleSearchService.getModelByExample(cecService);

	}

	@Override
	public BaseSiteCecServiceMappingModel getBaseSiteCecServiceMappingForId(final String id,
			final CecServiceModel serviceModel) {
		checkArgument(id != null, "basesite id must not be null");
		checkArgument(serviceModel != null, "serviceModel must not be null");

		final BaseSiteCecServiceMappingModel model = modelService.create(BaseSiteCecServiceMappingModel.class);

		model.setBaseSite(id);
		model.setCecService(serviceModel);
		return getFlexibleSearchService().getModelByExample(model);
	}

}
