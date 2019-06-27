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

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.apiregistryservices.model.ExposedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.services.CredentialService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.webservicescommons.interceptors.OAuthClientInterceptor;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;


@IntegrationTest
public class DefaultCredentialServiceIntegrationTest extends ServicelayerTest
{

	private static final String NEW_CLIENT_SECRET = "newClientSecret";

    @Resource
	private FlexibleSearchService flexibleSearchService;

    @Resource
	private CredentialService credentialService;

    @Resource
	private ModelService modelService;

	private OAuthClientInterceptor oauthClientInterceptor;

    @Before
    public void before() throws ImpExException
    {
		oauthClientInterceptor = new OAuthClientInterceptor();
		oauthClientInterceptor.setClientSecretEncoder(new BCryptPasswordEncoder());
        importCsv("/test/credentialService.impex", "UTF-8");
    }

    @Test
	public void resetCredentialTest() throws InterruptedException {
        List<ExposedOAuthCredentialModel> credentials = credentialService.getCredentialsByClientId("oldKymaClientTest");

		credentialService.resetCredentials(credentials, "newClientId", NEW_CLIENT_SECRET, 0);
        credentials.forEach(credential -> {
            modelService.get(credential.getPk());
            final OAuthClientDetailsModel newClient = credential.getOAuthClientDetails();
            assertEquals("newClientId",newClient.getClientId());
            Assert.assertTrue(oauthClientInterceptor.getClientSecretEncoder().matches(NEW_CLIENT_SECRET, newClient.getClientSecret()));

        });
    }
}
