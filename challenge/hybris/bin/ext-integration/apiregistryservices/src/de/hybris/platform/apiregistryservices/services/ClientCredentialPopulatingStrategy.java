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

package de.hybris.platform.apiregistryservices.services;

import java.util.Map;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.AbstractCredentialModel;


/**
 * Interface responsible for configuration build strategy for concrete Credential type
 */
public interface ClientCredentialPopulatingStrategy
{
    /**
     * Method which populates the config with credential data for client build by Charon.
     * @param credentialModel creds
     * @param config config to be updated
     */
	void populateConfig(AbstractCredentialModel credentialModel, Map<String, String> config) throws CredentialException;
}
