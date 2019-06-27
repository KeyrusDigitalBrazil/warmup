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

package de.hybris.platform.yaasconfiguration.service;


import de.hybris.platform.yaasconfiguration.model.AbstractYaasServiceMappingModel;
import de.hybris.platform.yaasconfiguration.model.YaasClientCredentialModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;

import java.util.List;
import java.util.Map;


/**
 * Focuses on methods to retrieve configuration items like yaas application
 */
public interface YaasConfigurationService
{
	/**
	 * Method to build YaaS configuration for the given client credential and client proxy type
	 *
	 * @param clientCredential
	 *           YaaS Client credential information
	 * @param clientType
	 *           YaaS client type (like Product)
	 *
	 * @return <T> YaaS configuration with key value map
	 */
	<T> Map<String, String> buildYaasConfig(YaasClientCredentialModel clientCredential, Class<T> clientType);

	/**
	 * Method to get persisted YaasClientCredentialModel for the given unique id
	 *
	 * @param id
	 *           YaaS Client credential primary key Id
	 *
	 * @return YaasClientCredentialModel YaaS Client credential entity
	 */
	YaasClientCredentialModel getYaasClientCredentialForId(final String id);

	/**
	 * Method to get persisted YaasServiceModel for the given unique id
	 *
	 * @param id
	 *           YaaS Service primary key Id
	 *
	 * @return YaasServiceModel YaaS Service Entity
	 */
	YaasServiceModel getYaasServiceForId(final String id);

	/**
	 * Method to get persisted BaseSiteServiceMappingModel for the given unique id
	 *
	 * @param id
	 *           BaseSiteServiceMapping primary key Id
	 * @param serviceModel
	 *           YaaS Service Entity
	 *
	 * @return AbstractYaasServiceMappingModel which is YaasServiceMapping Entity
	 */
	AbstractYaasServiceMappingModel getBaseSiteServiceMappingForId(final String id, final YaasServiceModel serviceModel);

	/**
	 * Method to get all persisted YaasClientCredentialModel
	 *
	 * @return all configured YaasClientCredential
	 */
	List<YaasClientCredentialModel> getYaasClientCredentials();

}
