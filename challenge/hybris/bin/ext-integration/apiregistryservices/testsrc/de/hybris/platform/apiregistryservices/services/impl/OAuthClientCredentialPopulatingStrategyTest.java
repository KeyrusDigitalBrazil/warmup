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
package de.hybris.platform.apiregistryservices.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.BasicCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@UnitTest
public class OAuthClientCredentialPopulatingStrategyTest
{
	private OAuthClientCredentialPopulatingStrategy oAuthClientCredentialPopulatingStrategy = new OAuthClientCredentialPopulatingStrategy();

	@Test
	public void successfulPopulateConfig() throws Exception
	{
        final String clientId = "clientId";
        final String clientSecret = "clientSecret";
        final String url = "url";
        final ConsumedOAuthCredentialModel cred = mock(ConsumedOAuthCredentialModel.class);
        when(cred.getClientId()).thenReturn(clientId);
        when(cred.getOAuthUrl()).thenReturn(url);
        when(cred.getClientSecret()).thenReturn(clientSecret);

        final HashMap config = new HashMap<String, String>();
        oAuthClientCredentialPopulatingStrategy.populateConfig(cred, config);

        assertTrue(config.containsValue(clientId));
        assertTrue(config.containsValue(clientSecret));
        assertTrue(config.containsValue(url));
	}

    @Test(expected = CredentialException.class)
    public void populateConfigWithWrongCredentialsType() throws Exception
    {
        final BasicCredentialModel cred = mock(BasicCredentialModel.class);

        final HashMap config = new HashMap<String, String>();
        oAuthClientCredentialPopulatingStrategy.populateConfig(cred, config);
    }
}

