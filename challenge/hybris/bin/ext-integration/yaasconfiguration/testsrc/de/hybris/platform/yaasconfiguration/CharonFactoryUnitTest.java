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
package de.hybris.platform.yaasconfiguration;

import static de.hybris.platform.yaasconfiguration.constants.YaasconfigurationConstants.YAAS_OAUTH_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.platform.yaasconfiguration.client.ProductClient;
import de.hybris.platform.yaasconfiguration.client.TargetProductClient;
import de.hybris.platform.yaasconfiguration.service.YaasConfigurationService;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.charon.CharonBuilder;


@RunWith(MockitoJUnitRunner.class)
public class CharonFactoryUnitTest extends YaasConfigurationTestUtils
{

	@Mock
	private YaasConfigurationService yaasConfigurationService;

	@Mock
	private CharonBuilder charonBuilder;

	private CharonFactory charonFactory;

	@Before
	public void setup()
	{
		charonFactory = new CharonFactory();
	}

	@Test
	public void testClient()
	{
		final Map<String, String> yaasConfig = new HashMap();

		yaasConfig.put(YAAS_OAUTH_URL, "https://api.yaas.io/hybris/oauth2/v1");

		when(yaasConfigurationService.buildYaasConfig(any(), any())).thenReturn(yaasConfig);
		when(charonBuilder.build()).thenReturn(new Object());

		assertNotNull(charonFactory.client("applicationId", ProductClient.class, yaasConfig, builder -> builder.build()));

	}


	@Test
	public void testInValidateCache_validateParameter()
	{
		errorMustBeReported("key must not be null");
		charonFactory.inValidateCache(null);

	}

	@Test
	public void testInValidateCache_withDifferentApplicationId()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		prepareClient(ProductClient.class, "applicationId");
		prepareClient(ProductClient.class, "applicationId2");

		//Expected to create 2 client configuration, because of 2 different applicationId
		assertThat(getCharonChache().size()).isEqualTo(2);

		charonFactory.inValidateCache("applicationId");

		//Expected to remove the cache corresponding to given applicationId.
		assertThat(getCharonChache().size()).isEqualTo(1);

	}

	@Test
	public void testInValidateCache_sameApplicationIdWithDifferentClient()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		prepareClient(ProductClient.class, "applicationId");
		prepareClient(TargetProductClient.class, "applicationId");

		//Expected to create 2 client configuration, because of 2 different client
		assertThat(getCharonChache().size()).isEqualTo(2);

		charonFactory.inValidateCache("applicationId");

		//Expected to remove all, if it request to invalidate all the client for the given appId
		assertThat(getCharonChache().size()).isEqualTo(0);

	}

	@Test
	public void testInValidateCache_sameApplicationId()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		prepareClient(ProductClient.class, "applicationId");
		prepareClient(ProductClient.class, "applicationId");

		//Expected to create only one configuration, because of same client and applicationId
		assertThat(getCharonChache().size()).isEqualTo(1);

		charonFactory.inValidateCache("applicationId");

		assertThat(getCharonChache().size()).isEqualTo(0);

	}

	@Test
	public void testInValidateCache_clientId()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		prepareClient(ProductClient.class, "applicationId");
		prepareClient(TargetProductClient.class, "applicationId");

		//Expected to create 2 client configuration, because of 2 different client
		assertThat(getCharonChache().size()).isEqualTo(2);

		charonFactory.inValidateCache("applicationId#ProductClient");

		//Expected to remove only for the given client : it should not behave similar to testInValidateCache_sameApplicationIdWithDifferentClient
		assertThat(getCharonChache().size()).isEqualTo(1);

	}

	@Test
	public void testBuildKey()
	{
		assertEquals("applicationId#ProductClient", charonFactory.buildCacheKey("applicationId", ProductClient.class.getName()));
	}

	protected void prepareClient(final Class client, final String appId)
	{

		final Map<String, String> yaasConfig = new HashMap();

		yaasConfig.put(YAAS_OAUTH_URL, "https://api.yaas.io/hybris/oauth2/v1");

		when(yaasConfigurationService.buildYaasConfig(any(), any())).thenReturn(yaasConfig);
		when(charonBuilder.build()).thenReturn(new Object());

		charonFactory.client(appId, client, yaasConfig, builder -> builder.build());

	}


	protected ConcurrentHashMap<String, Object> getCharonChache()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{

		final Field field = charonFactory.getClass().getDeclaredField("cache"); //NoSuchFieldException
		field.setAccessible(true);

		return (ConcurrentHashMap) field.get(charonFactory);
	}



}
