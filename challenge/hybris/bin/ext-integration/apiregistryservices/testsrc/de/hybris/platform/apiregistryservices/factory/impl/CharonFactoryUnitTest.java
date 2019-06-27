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
package de.hybris.platform.apiregistryservices.factory.impl;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.factory.client.ProductClient;
import de.hybris.platform.apiregistryservices.factory.client.ProfileDataServiceClient;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedOAuthCredentialModel;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;

import java.util.HashMap;
import java.util.Map;

import com.hybris.charon.CharonBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class CharonFactoryUnitTest
{
	private static final String OAUTH_URL = "oauth.url";
	private static final String OAUTH_URL_TEST = "https://testurl.com/oauth2/v1";
	private static final String CREDENTIAL_ALFA = "credentialAlfa";
	private static final String CREDENTIAL_BRAVO = "credentialBravo";
	private static final String DESTINATION_ALFA = "destinationAlfa";
	private static final String DESTINATION_BRAVO = "destinationBravo";
	private static final String CREDENTIAL = "credential";
	private static final String DESTINATION = "destination";
	private static final String DESTINATION_CHARLIE = "destinationCharlie";
	private static final String DESTINATION_DELTA = "destinationDelta";

	@Mock
	private ApiRegistryClientService apiRegistryClientService;

	@Mock
	private CharonBuilder charonBuilder;

	private CharonFactory charonFactory;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setup()
	{
		charonFactory = new CharonFactory();
	}

	@Test
	public void testClient() throws CredentialException
	{
		final Map<String, String> charonConfig = new HashMap();

		charonConfig.put(OAUTH_URL, OAUTH_URL_TEST);

		when(apiRegistryClientService.buildClientConfig(any(), any())).thenReturn(charonConfig);
		when(charonBuilder.build()).thenReturn(new Object());

		assertNotNull(charonFactory.client("applicationId", ProductClient.class, charonConfig));
	}

	@Test
	public void testBuildCacheKeyWithNullDestination()
	{
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Destination must not be null.");

		charonFactory.buildCacheKey(null);
	}

	@Test
	public void testBuildCacheKeyWithValidDestinationNoCredential()
	{
		final ConsumedDestinationModel consumedDestination = new ConsumedDestinationModel();
		consumedDestination.setId(DESTINATION);

		final String cacheKey = charonFactory.buildCacheKey(consumedDestination);
		assertEquals(cacheKey, DESTINATION);
	}

	@Test
	public void testBuildCacheKeyWithValidDestinationHasCredential()
	{
		final ConsumedOAuthCredentialModel credential = new ConsumedOAuthCredentialModel();
		credential.setId(CREDENTIAL);

		final ConsumedDestinationModel consumedDestination = new ConsumedDestinationModel();
		consumedDestination.setId(DESTINATION);
		consumedDestination.setCredential(credential);

		final String cacheKey = charonFactory.buildCacheKey(consumedDestination);
		assertEquals(cacheKey, "credential#destination");
	}

	@Test
	public void testInvalidateCacheWithNullKey()
	{
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("key must not be null");

		charonFactory.inValidateCache(null);
	}

	@Test
	public void testInvalidateCacheWithDestinationUpdateForOneClientHasNoCredential() throws CredentialException
	{
		final ConsumedDestinationModel consumedDestination = new ConsumedDestinationModel();
		consumedDestination.setId(DESTINATION);

		final String cacheKey = charonFactory.buildCacheKey(consumedDestination);

		prepareClient(ProductClient.class, cacheKey);
		assertThat(charonFactory.getCache().size()).isEqualTo(1);
		assertThat(charonFactory.getCache().keySet()).contains(consumedDestination.getId());

		charonFactory.inValidateCache(consumedDestination.getId());

		assertThat(charonFactory.getCache()).isEmpty();
	}

	@Test
	public void testInvalidateCacheWithDestinationUpdateForTwoClientBothHasNoCredential() throws CredentialException
	{
		final ConsumedDestinationModel consumedDestinationAlfa = new ConsumedDestinationModel();
		consumedDestinationAlfa.setId(DESTINATION_ALFA);

		final ConsumedDestinationModel consumedDestinationBravo = new ConsumedDestinationModel();
		consumedDestinationBravo.setId(DESTINATION_BRAVO);

		prepareClient(ProductClient.class, charonFactory.buildCacheKey(consumedDestinationAlfa));
		prepareClient(ProductClient.class, charonFactory.buildCacheKey(consumedDestinationBravo));

		assertThat(charonFactory.getCache().size()).isEqualTo(2);
		assertThat(charonFactory.getCache().keySet()).contains(consumedDestinationAlfa.getId());
		assertThat(charonFactory.getCache().keySet()).contains(consumedDestinationBravo.getId());

		charonFactory.inValidateCache(consumedDestinationAlfa.getId());

		assertThat(charonFactory.getCache().size()).isEqualTo(1);
		assertFalse(charonFactory.getCache().keySet().contains(consumedDestinationAlfa.getId()));
		assertThat(charonFactory.getCache().keySet()).contains(consumedDestinationBravo.getId());
	}

	@Test
	public void testInvalidateCacheWithDestinationUpdateForOneClientHasCredential() throws CredentialException
	{
		final ConsumedOAuthCredentialModel credential = new ConsumedOAuthCredentialModel();
		credential.setId(CREDENTIAL);

		final ConsumedDestinationModel consumedDestination = new ConsumedDestinationModel();
		consumedDestination.setId(DESTINATION);
		consumedDestination.setCredential(credential);

		final String cacheKey = charonFactory.buildCacheKey(consumedDestination);

		prepareClient(ProductClient.class, cacheKey);
		assertThat(charonFactory.getCache().size()).isEqualTo(1);
		assertThat(charonFactory.getCache().keySet()).contains(cacheKey);

		charonFactory.inValidateCache(cacheKey);

		assertThat(charonFactory.getCache()).isEmpty();
	}

	@Test
	public void testInvalidateCacheWithDestinationUpdateForTwoClientBothHasCredential() throws CredentialException
	{
		final ConsumedOAuthCredentialModel credentialAlfa = new ConsumedOAuthCredentialModel();
		credentialAlfa.setId(CREDENTIAL_ALFA);

		final ConsumedOAuthCredentialModel credentialBravo = new ConsumedOAuthCredentialModel();
		credentialBravo.setId(CREDENTIAL_BRAVO);

		final ConsumedDestinationModel consumedDestinationAlfa = new ConsumedDestinationModel();
		consumedDestinationAlfa.setId(DESTINATION_ALFA);
		consumedDestinationAlfa.setCredential(credentialAlfa);

		final ConsumedDestinationModel consumedDestinationBravo = new ConsumedDestinationModel();
		consumedDestinationBravo.setId(DESTINATION_BRAVO);
		consumedDestinationBravo.setCredential(credentialBravo);

		final String cacheKeyAlfa = charonFactory.buildCacheKey(consumedDestinationAlfa);
		final String cacheKeyBravo = charonFactory.buildCacheKey(consumedDestinationBravo);

		prepareClient(ProductClient.class, cacheKeyAlfa);
		prepareClient(ProductClient.class, cacheKeyBravo);

		assertThat(charonFactory.getCache().size()).isEqualTo(2);
		assertThat(charonFactory.getCache().keySet()).contains(cacheKeyAlfa);
		assertThat(charonFactory.getCache().keySet()).contains(cacheKeyBravo);

		charonFactory.inValidateCache(cacheKeyAlfa);

		assertThat(charonFactory.getCache().size()).isEqualTo(1);
		assertFalse(charonFactory.getCache().keySet().contains(cacheKeyAlfa));
		assertThat(charonFactory.getCache().keySet()).contains(cacheKeyBravo);
	}

	@Test
	public void testInvalidateCacheWithCredentialUpdateForOneClientHasCredential() throws CredentialException
	{
		final ConsumedOAuthCredentialModel credential = new ConsumedOAuthCredentialModel();
		credential.setId(CREDENTIAL);

		final ConsumedDestinationModel consumedDestination = new ConsumedDestinationModel();
		consumedDestination.setId(DESTINATION);
		consumedDestination.setCredential(credential);

		final String cacheKey = charonFactory.buildCacheKey(consumedDestination);

		prepareClient(ProductClient.class, cacheKey);
		assertThat(charonFactory.getCache().size()).isEqualTo(1);
		assertThat(charonFactory.getCache().keySet()).contains(cacheKey);

		charonFactory.inValidateCache(credential.getId());

		assertThat(charonFactory.getCache()).isEmpty();
	}

	@Test
	public void testInvalidateCacheWithCredentialUpdateForTwoClientBothHasCredential() throws CredentialException
	{
		final ConsumedOAuthCredentialModel credentialAlfa = new ConsumedOAuthCredentialModel();
		credentialAlfa.setId(CREDENTIAL_ALFA);

		final ConsumedOAuthCredentialModel credentialBravo = new ConsumedOAuthCredentialModel();
		credentialBravo.setId(CREDENTIAL_BRAVO);

		final ConsumedDestinationModel consumedDestinationAlfa = new ConsumedDestinationModel();
		consumedDestinationAlfa.setId(DESTINATION_ALFA);
		consumedDestinationAlfa.setCredential(credentialAlfa);

		final ConsumedDestinationModel consumedDestinationBravo = new ConsumedDestinationModel();
		consumedDestinationBravo.setId(DESTINATION_BRAVO);
		consumedDestinationBravo.setCredential(credentialBravo);

		final String cacheKeyAlfa = charonFactory.buildCacheKey(consumedDestinationAlfa);
		final String cacheKeyBravo = charonFactory.buildCacheKey(consumedDestinationBravo);

		prepareClient(ProductClient.class, cacheKeyAlfa);
		prepareClient(ProductClient.class, cacheKeyBravo);

		assertThat(charonFactory.getCache().size()).isEqualTo(2);
		assertThat(charonFactory.getCache().keySet()).contains(cacheKeyAlfa);
		assertThat(charonFactory.getCache().keySet()).contains(cacheKeyBravo);

		charonFactory.inValidateCache(credentialAlfa.getId());

		assertThat(charonFactory.getCache().size()).isEqualTo(1);
		assertFalse(charonFactory.getCache().keySet().contains(cacheKeyAlfa));
		assertThat(charonFactory.getCache().keySet()).contains(cacheKeyBravo);
	}


	@Test
	public void testInvalidateCacheWithCredentialUpdateForMultipleClients() throws CredentialException
	{
		final ConsumedOAuthCredentialModel credentialAlfa = new ConsumedOAuthCredentialModel();
		credentialAlfa.setId(CREDENTIAL_ALFA);

		final ConsumedOAuthCredentialModel credentialBravo = new ConsumedOAuthCredentialModel();
		credentialBravo.setId(CREDENTIAL_BRAVO);

		final ConsumedDestinationModel consumedDestinationAlfa = new ConsumedDestinationModel();
		consumedDestinationAlfa.setId(DESTINATION_ALFA);
		consumedDestinationAlfa.setCredential(credentialAlfa);

		final ConsumedDestinationModel consumedDestinationBravo = new ConsumedDestinationModel();
		consumedDestinationBravo.setId(DESTINATION_BRAVO);
		consumedDestinationBravo.setCredential(credentialBravo);

		final ConsumedDestinationModel consumedDestinationCharlie = new ConsumedDestinationModel();
		consumedDestinationCharlie.setId(DESTINATION_CHARLIE);
		consumedDestinationCharlie.setCredential(credentialBravo);

		final ConsumedDestinationModel consumedDestinationDelta = new ConsumedDestinationModel();
		consumedDestinationDelta.setId(DESTINATION_DELTA);

		final String cacheKeyAlfa = charonFactory.buildCacheKey(consumedDestinationAlfa);
		final String cacheKeyBravo = charonFactory.buildCacheKey(consumedDestinationBravo);
		final String cacheKeyCharlie = charonFactory.buildCacheKey(consumedDestinationCharlie);
		final String cacheKeyDelta = charonFactory.buildCacheKey(consumedDestinationDelta);

		prepareClient(ProductClient.class, cacheKeyAlfa);
		prepareClient(ProductClient.class, cacheKeyBravo);
		prepareClient(ProfileDataServiceClient.class, cacheKeyCharlie);
		prepareClient(ProfileDataServiceClient.class, cacheKeyDelta);

		assertThat(charonFactory.getCache().size()).isEqualTo(4);
		assertThat(charonFactory.getCache().keySet()).contains(cacheKeyAlfa);
		assertThat(charonFactory.getCache().keySet()).contains(cacheKeyBravo);
		assertThat(charonFactory.getCache().keySet()).contains(cacheKeyCharlie);
		assertThat(charonFactory.getCache().keySet()).contains(cacheKeyDelta);

		charonFactory.inValidateCache(credentialBravo.getId());

		assertThat(charonFactory.getCache().size()).isEqualTo(2);
		assertThat(charonFactory.getCache().keySet()).contains(cacheKeyAlfa);
		assertFalse(charonFactory.getCache().keySet().contains(cacheKeyBravo));
		assertFalse(charonFactory.getCache().keySet().contains(cacheKeyCharlie));
		assertThat(charonFactory.getCache().keySet()).contains(cacheKeyDelta);
	}

	@Test
	public void testClearCache() throws CredentialException
	{
		final ConsumedDestinationModel consumedDestinationAlfa = new ConsumedDestinationModel();
		consumedDestinationAlfa.setId(DESTINATION_ALFA);

		final ConsumedDestinationModel consumedDestinationBravo = new ConsumedDestinationModel();
		consumedDestinationBravo.setId(DESTINATION_BRAVO);

		prepareClient(ProductClient.class, charonFactory.buildCacheKey(consumedDestinationAlfa));
		prepareClient(ProductClient.class, charonFactory.buildCacheKey(consumedDestinationBravo));

		assertThat(charonFactory.getCache().size()).isEqualTo(2);

		charonFactory.clearCache();

		assertThat(charonFactory.getCache().size()).isEqualTo(0);
	}

	protected void prepareClient(final Class client, final String cacheKey) throws CredentialException
	{
		final Map<String, String> charonConfig = new HashMap();

		charonConfig.put(OAUTH_URL, OAUTH_URL_TEST);

		when(apiRegistryClientService.buildClientConfig(any(), any())).thenReturn(charonConfig);
		when(charonBuilder.build()).thenReturn(new Object());

		charonFactory.client(cacheKey, client, charonConfig);
	}
}