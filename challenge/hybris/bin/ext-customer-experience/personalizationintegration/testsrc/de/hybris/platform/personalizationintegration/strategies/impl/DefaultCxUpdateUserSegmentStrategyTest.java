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
package de.hybris.platform.personalizationintegration.strategies.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationintegration.mapping.MappingData;
import de.hybris.platform.personalizationintegration.mapping.SegmentMappingData;
import de.hybris.platform.personalizationintegration.segment.UserSegmentsProvider;
import de.hybris.platform.personalizationintegration.service.CxIntegrationMappingService;
import de.hybris.platform.personalizationservices.CxCalculationContext;
import de.hybris.platform.personalizationservices.configuration.CxConfigurationService;
import de.hybris.platform.personalizationservices.consent.CxConsentService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCxUpdateUserSegmentStrategyTest
{
	private static String PROVIDER_ID = "provider";
	private static String ANOTHER_PROVIDER_ID = "anotherProvider";

	private final DefaultCxUpdateUserSegmentStrategy updateUserSegmentStrategy = new DefaultCxUpdateUserSegmentStrategy();
	@Mock
	private CxIntegrationMappingService cxIntegrationMappingService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	CxConfigurationService cxConfigurationService;
	@Mock
	Configuration configuration;
	@Mock
	UserSegmentsProvider provider;
	@Mock
	UserSegmentsProvider anotherProvider;

	private List<UserSegmentsProvider> providers;
	@Mock
	CxConsentService cxConsentService;
	@Mock
	UserModel user;
	List<SegmentMappingData> segmentMappingList;
	MappingData expectedMappingData;

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);
		expectedMappingData = new MappingData();
		providers = new ArrayList<>();
		providers.add(provider);
		Mockito.when(provider.getProviderId()).thenReturn(PROVIDER_ID);
		Mockito.when(anotherProvider.getProviderId()).thenReturn(ANOTHER_PROVIDER_ID);

		updateUserSegmentStrategy.setProviders(Optional.of(providers));
		updateUserSegmentStrategy.setConfigurationService(configurationService);
		updateUserSegmentStrategy.setCxIntegrationMappingService(cxIntegrationMappingService);
		updateUserSegmentStrategy.setCxConfigurationService(cxConfigurationService);
		updateUserSegmentStrategy.setCxConsentService(cxConsentService);
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(cxConfigurationService.getConfiguration()).thenReturn(Optional.empty());
		Mockito.when(cxConfigurationService.getConfiguration(Mockito.any())).thenReturn(Optional.empty());
		Mockito.when(Boolean.valueOf(cxConsentService.userHasActiveConsent(user))).thenReturn(Boolean.TRUE);
	}

	@Test
	public void updateUserSegmentTest()
	{
		//given
		configureSegmentMappingForProvider("segment1", "segment2");

		//when
		updateUserSegmentStrategy.updateUserSegments(user);

		//then
		Mockito.verify(cxIntegrationMappingService, Mockito.times(1)).assignSegmentsToUser(Mockito.eq(user),
				Mockito.argThat(new MappingDataMatcher(expectedMappingData)), Mockito.eq(false));
	}

	@Test
	public void updateUserSegmentWithDuplicatedSegmensTest()
	{
		//given
		configureSegmentMappingForProvider("segment1", "segment1", "segment2", "segment1", "segment3", "segment2");
		final List<SegmentMappingData> expectedList = createSegmentMappingList(PROVIDER_ID, "segment1", "segment2", "segment3");
		expectedMappingData.setSegments(expectedList);

		//when
		updateUserSegmentStrategy.updateUserSegments(user);

		//then
		Mockito.verify(cxIntegrationMappingService, Mockito.times(1)).assignSegmentsToUser(Mockito.eq(user),
				Mockito.argThat(new MappingDataMatcher(expectedMappingData)), Mockito.eq(false));
	}

	@Test
	public void updateUserSegmentWhenNullDataFromProviderTest()
	{
		//given
		Mockito.when(provider.getUserSegments(user)).thenReturn(null);

		//when
		updateUserSegmentStrategy.updateUserSegments(user);

		//then
		Mockito.verify(cxIntegrationMappingService, Mockito.times(0)).assignSegmentsToUser(Mockito.eq(user), Mockito.any(),
				Mockito.eq(false));
	}

	@Test
	public void updateUserSegmentWithTwoProvidersTest()
	{
		//given
		configureSegmentMappingForProvider("segment1", "segment2");

		providers.add(anotherProvider);
		List<SegmentMappingData> anotherSegmentMappingList = createSegmentMappingList(ANOTHER_PROVIDER_ID, "segment1", "segment2");
		Mockito.when(anotherProvider.getUserSegments(user)).thenReturn(anotherSegmentMappingList);
		expectedMappingData.getSegments().addAll(anotherSegmentMappingList);

		//when
		updateUserSegmentStrategy.updateUserSegments(user);

		//then
		Mockito.verify(cxIntegrationMappingService, Mockito.times(1)).assignSegmentsToUser(Mockito.eq(user),
				Mockito.argThat(new MappingDataMatcher(expectedMappingData)), Mockito.eq(false));
	}

	@Test
	public void testReturnBiggestAffinity()
	{
		//given
		segmentMappingList = createSegmentMappingList(PROVIDER_ID, "segment1", "segment1");
		segmentMappingList.get(0).setAffinity(BigDecimal.valueOf(0, 5));
		segmentMappingList.get(1).setAffinity(BigDecimal.ONE);
		Mockito.when(provider.getUserSegments(user)).thenReturn(segmentMappingList);
		final List<SegmentMappingData> expectedList = createSegmentMappingList(PROVIDER_ID, "segment1");
		expectedMappingData.setSegments(expectedList);
		final ArgumentCaptor<MappingData> mappingDataArgument = ArgumentCaptor.forClass(MappingData.class);

		//when
		updateUserSegmentStrategy.updateUserSegments(user);

		//then
		Mockito.verify(cxIntegrationMappingService, Mockito.times(1)).assignSegmentsToUser(Mockito.eq(user),
				mappingDataArgument.capture(), Mockito.eq(false));
		final MappingData returnedMappingData = mappingDataArgument.getValue();
		Assert.assertNotNull(returnedMappingData);
		Assert.assertNotNull(returnedMappingData.getSegments());
		Assert.assertTrue(returnedMappingData.getSegments().size() == 1);
		Assert.assertEquals("segment1", returnedMappingData.getSegments().get(0).getCode());
		Assert.assertEquals(BigDecimal.ONE, returnedMappingData.getSegments().get(0).getAffinity());
	}

	@Test
	public void dontUpdateUserSegmentForEmptyProviderListTest()
	{
		//given
		updateUserSegmentStrategy.setProviders(Optional.empty());

		//when
		updateUserSegmentStrategy.updateUserSegments(user);

		//then
		Mockito.verifyZeroInteractions(cxIntegrationMappingService);
	}

	@Test
	public void cleanUserSegmentIfNotGivenConsentTest()
	{
		//given
		Mockito.when(Boolean.valueOf(cxConsentService.userHasActiveConsent(user))).thenReturn(Boolean.FALSE);
		expectedMappingData.setSegments(Collections.emptyList());

		//when
		updateUserSegmentStrategy.updateUserSegments(user);

		//then
		Mockito.verify(cxIntegrationMappingService, Mockito.times(1)).assignSegmentsToUser(Mockito.eq(user),
				Mockito.argThat(new MappingDataMatcher(expectedMappingData)), Mockito.eq(false));
	}

	@Test
	public void updateUserSegmentWhenOneProviderThrowExceptionTest()
	{
		//given
		configureSegmentMappingForProvider("segment1", "segment2");

		providers.add(anotherProvider);
		Mockito.when(anotherProvider.getUserSegments(user)).thenThrow(new RuntimeException("Provider error"));

		//when
		updateUserSegmentStrategy.updateUserSegments(user);

		//then
		Mockito.verify(cxIntegrationMappingService, Mockito.times(1)).assignSegmentsToUser(Mockito.eq(user),
				Mockito.argThat(new MappingDataMatcher(expectedMappingData)), Mockito.eq(false));
	}

	@Test
	public void updateUserSegmentWithNullContextTest()
	{
		//given
		configureSegmentMappingForProvider("segment1", "segment2");

		//when
		updateUserSegmentStrategy.updateUserSegments(user, null);

		//then
		Mockito.verify(cxIntegrationMappingService, Mockito.times(1)).assignSegmentsToUser(Mockito.eq(user),
				Mockito.argThat(new MappingDataMatcher(expectedMappingData)), Mockito.eq(false));
	}

	@Test
	public void updateUserSegmentWithEmptyContextTest()
	{
		//given
		configureSegmentMappingForProvider("segment1", "segment2");
		CxCalculationContext context = createCalculationContext(PROVIDER_ID);

		//when
		updateUserSegmentStrategy.updateUserSegments(user, context);

		//then
		Mockito.verify(cxIntegrationMappingService, Mockito.times(1)).assignSegmentsToUser(Mockito.eq(user),
				Mockito.argThat(new MappingDataMatcher(expectedMappingData)), Mockito.eq(false), Mockito.eq(context));
	}

	@Test
	public void updateUserSegmentForSelectedProviderTest()
	{
		//given
		configureSegmentMappingForProvider("segment1", "segment2");
		CxCalculationContext context = createCalculationContext(PROVIDER_ID);

		providers.add(anotherProvider);
		List<SegmentMappingData> anotherSegmentMappingList = createSegmentMappingList(ANOTHER_PROVIDER_ID, "segment1", "segment2");
		Mockito.when(anotherProvider.getUserSegments(user)).thenReturn(anotherSegmentMappingList);

		//when
		updateUserSegmentStrategy.updateUserSegments(user, context);

		//then
		Mockito.verify(cxIntegrationMappingService, Mockito.times(1)).assignSegmentsToUser(Mockito.eq(user),
				Mockito.argThat(new MappingDataMatcher(expectedMappingData)), Mockito.eq(false), Mockito.eq(context));
	}

	@Test
	public void addProviderPrefixForSegmentWhenPropertySetTest()
	{
		//given
		String providerSegmentPrefixPropertyKey = String.format("personalizationintegration.provider.%s.prefix", provider.getProviderId());
		String prefix = "testPrefix";
		Mockito.when(configurationService.getConfiguration().getString(providerSegmentPrefixPropertyKey)).thenReturn(prefix);

		configureSegmentMappingForProvider("segment1", "segment2");

		//when
		List<SegmentMappingData> segmentsFromProvider = updateUserSegmentStrategy.getSegmentsFromProvider(user, provider);

		//then
		for (SegmentMappingData segment : segmentsFromProvider)
		{
			Assert.assertTrue(segment.getCode().startsWith(prefix + " "));
		}
	}

	@Test
	public void dontAddProviderPrefixForSegmentWhenPropertyEmptyTest()
	{
		//given
		String segmentCode = "segment";

		String providerSegmentPrefixPropertyKey = String.format("personalizationintegration.provider.%s.prefix", provider.getProviderId());
		Mockito.when(configurationService.getConfiguration().getString(providerSegmentPrefixPropertyKey)).thenReturn(StringUtils.EMPTY);

		configureSegmentMappingForProvider(segmentCode);

		//when
		List<SegmentMappingData> segmentsFromProvider = updateUserSegmentStrategy.getSegmentsFromProvider(user, provider);

		//then
		Assert.assertEquals(1, segmentsFromProvider.size());
		Assert.assertTrue(segmentsFromProvider.get(0).getCode().equals(segmentCode));
	}

	protected void configureSegmentMappingForProvider(final String... segments)
	{
		segmentMappingList = createSegmentMappingList(PROVIDER_ID, segments);
		Mockito.when(provider.getUserSegments(user)).thenReturn(segmentMappingList);

		expectedMappingData.setSegments(new ArrayList<>());
		expectedMappingData.getSegments().addAll(segmentMappingList);
	}

	protected List<SegmentMappingData> createSegmentMappingList(final String providerId, final String... segments)
	{
		final List<SegmentMappingData> segmentMappingList = new ArrayList();
		for (final String segment : segments)
		{
			segmentMappingList.add(createSegmentMapping(providerId, segment, BigDecimal.ONE));
		}
		return segmentMappingList;
	}

	protected SegmentMappingData createSegmentMapping(final String providerId, final String segmentCode, final BigDecimal affinity)
	{
		final SegmentMappingData segmentMapping = new SegmentMappingData();
		segmentMapping.setCode(segmentCode);
		segmentMapping.setAffinity(affinity);
		segmentMapping.setProvider(providerId);
		return segmentMapping;
	}

	protected CxCalculationContext createCalculationContext(String... providers)
	{
		CxCalculationContext context = new CxCalculationContext();
		context.setSegmentUpdateProviders(Stream.of(providers).collect(Collectors.toSet()));
		return context;
	}

    protected class MappingDataMatcher extends ArgumentMatcher<MappingData>
	{
		MappingData expectedData;

		public MappingDataMatcher(final MappingData expectedData)
		{
			super();
			this.expectedData = expectedData;
		}

		@Override
		public boolean matches(final Object object)
		{
			if (object instanceof MappingData)
			{
				final MappingData mappingData = (MappingData) object;
				if (expectedData.getSegments() == mappingData.getSegments()
						|| (CollectionUtils.isEmpty(mappingData.getSegments()) && CollectionUtils.isEmpty(expectedData.getSegments())))
				{
					return true;
				}

				if (expectedData.getSegments() == null || mappingData.getSegments() == null
						|| expectedData.getSegments().size() != mappingData.getSegments().size())
				{
					return false;
				}
				return expectedData.getSegments().stream()//
						.map(s -> checkIfContains(mappingData, s))//
						.allMatch(contains -> contains == Boolean.TRUE);
			}

			return false;
		}

		private Boolean checkIfContains(final MappingData mappingData, final SegmentMappingData segmentMapping)
		{
			return Boolean.valueOf(mappingData.getSegments().stream()//
					.anyMatch(s -> StringUtils.equals(s.getCode(), segmentMapping.getCode())
							&& StringUtils.equals(s.getProvider(), segmentMapping.getProvider())));
		}
	}
}
