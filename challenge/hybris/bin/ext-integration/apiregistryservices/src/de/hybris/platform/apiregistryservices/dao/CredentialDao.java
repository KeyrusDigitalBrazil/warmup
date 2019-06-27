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
package de.hybris.platform.apiregistryservices.dao;

import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;

import java.util.List;


/**
 * DAO for the {@link ExposedOAuthCredentialModel}
 */
public interface CredentialDao
{
	/**
	 * Find the list of ExposedOAuthCredentials for specific clientId
	 *
	 * @param clientId
	 *           The clientId of OAuthClientDetails
	 * @return a List of ExposedOAuthCredentials by the clientId
	 */
	List<ExposedOAuthCredentialModel> getAllExposedOAuthCredentialsByClientId(String clientId);
}
