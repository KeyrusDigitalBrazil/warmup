/*
* [y] hybris Platform
*
* Copyright (c) 2018 SAP SE or an SAP affiliate company.
* All rights reserved.
*
* This software is the confidential and proprietary information of SAP
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with SAP.
*
*/

package de.hybris.platform.yaasconfiguration.service.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_CLIENT_SCOPE;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_CLIENT_URL;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_OAUTH_CLIENTID;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_OAUTH_CLIENTSECRET;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_OAUTH_URL;
import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_TENANT;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.yaasconfiguration.model.BaseSiteServiceMappingModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientCredentialModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link YaasConfigurationService}
 *
 * @param <T>
 */
public class DefaultYaasConfigurationService implements YaasConfigurationService
{
	private FlexibleSearchService flexibleSearchService;

	@Override
	public Map<String, String> buildYaasConfig(final YaasClientCredentialModel clientCredential, final Class serviceType)
	{
		checkArgument(clientCredential != null, "clientCredential must not be null");
		checkArgument(serviceType != null, "serviceType must not be null");

		final Map<String, String> config = new HashMap();

		buildClientCredentialConfig(clientCredential, config);

		buildServiceConfig(serviceType.getSimpleName(), config);

		return config;
	}

	protected void buildClientCredentialConfig(final YaasClientCredentialModel clientCredential, final Map<String, String> config)
	{
		checkArgument(clientCredential != null, "clientCredential must not be null");
		checkArgument(config != null, "config must not be null");

		config.put(YAAS_OAUTH_URL, clientCredential.getOauthURL());
		config.put(YAAS_OAUTH_CLIENTID, clientCredential.getClientId());
		config.put(YAAS_OAUTH_CLIENTSECRET, clientCredential.getClientSecret());
		config.put(YAAS_TENANT, clientCredential.getYaasProject().getIdentifier());
	}

	/**
	 *
	 * @param serviceId
	 * @param config
	 */
	protected void buildServiceConfig(final String serviceId, final Map<String, String> config)
	{
		checkArgument(serviceId != null, "serviceId must not be null");
		checkArgument(config != null, "config must not be null");

		final YaasServiceModel yaasService = getYaasServiceForId(serviceId);

		config.put(YAAS_CLIENT_URL, yaasService.getServiceURL());
		config.put(YAAS_CLIENT_SCOPE, yaasService.getServiceScope());

		if (MapUtils.isNotEmpty(yaasService.getAdditionalConfigurations()))
		{
			config.putAll(yaasService.getAdditionalConfigurations());
		}
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}


	@Override
	public YaasServiceModel getYaasServiceForId(final String id)
	{
		checkArgument(id != null, "Yaas Servcie configuration must not be null");

		final YaasServiceModel model = new YaasServiceModel();

		model.setIdentifier(id);
		return getFlexibleSearchService().getModelByExample(model);
	}

	@Override
	public YaasClientCredentialModel getYaasClientCredentialForId(final String id)
	{
		checkArgument(id != null, "Yaas Client Credential configuration must not be null");

		final YaasClientCredentialModel model = new YaasClientCredentialModel();

		model.setIdentifier(id);
		return getFlexibleSearchService().getModelByExample(model);
	}

	@Override
	public BaseSiteServiceMappingModel getBaseSiteServiceMappingForId(final String id, final YaasServiceModel serviceModel)
	{
		checkArgument(id != null, "id must not be null");
		checkArgument(serviceModel != null, "serviceModel must not be null");

		final BaseSiteServiceMappingModel model = new BaseSiteServiceMappingModel();

		model.setBaseSite(id);
		model.setYaasService(serviceModel);
		return getFlexibleSearchService().getModelByExample(model);
	}


	@Override
	public List<YaasClientCredentialModel> getYaasClientCredentials()
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery("select {pk} from {" + YaasClientCredentialModel._TYPECODE + "}");
		return getFlexibleSearchService().<YaasClientCredentialModel> search(query).getResult();
	}

}
