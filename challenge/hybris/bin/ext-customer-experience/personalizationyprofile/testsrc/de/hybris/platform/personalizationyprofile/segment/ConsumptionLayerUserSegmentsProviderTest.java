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
package de.hybris.platform.personalizationyprofile.segment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationintegration.mapping.MappingData;
import de.hybris.platform.personalizationintegration.mapping.SegmentMappingData;
import de.hybris.platform.personalizationintegration.service.CxIntegrationMappingService;
import de.hybris.platform.personalizationyprofile.mapper.CxConsumptionLayerProfileMapper;
import de.hybris.platform.personalizationyprofile.strategy.CxProfileIdentifierStrategy;
import de.hybris.platform.personalizationyprofile.yaas.Profile;
import de.hybris.platform.personalizationyprofile.yaas.client.CxProfileServiceClient;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;
import com.hybris.charon.exp.HttpException;
import com.hybris.charon.exp.NotFoundException;


@UnitTest
public class ConsumptionLayerUserSegmentsProviderTest
{
	private static final String FIELD_1 = "field1";
	private static final String FIELD_2 = "field2";
	private static final String FIELD_3 = "field1.field3";
	private static final String FIELD_1_WITH_SPACE = " field1 ";

	private final ConsumptionLayerUserSegmentsProvider consumptionLayerUserSegmentsProvider = new ConsumptionLayerUserSegmentsProvider();

	@Mock
	private CxProfileIdentifierStrategy cxProfileIdentifierStrategy;

	@Mock
	private CxProfileServiceClient cxProfileServiceClient;

	@Mock
	private CxIntegrationMappingService cxIntegrationMappingService;

	@Mock
	private UserModel user;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		consumptionLayerUserSegmentsProvider.setCxProfileServiceClient(cxProfileServiceClient);
		consumptionLayerUserSegmentsProvider.setCxProfileIdentifierStrategy(cxProfileIdentifierStrategy);
		consumptionLayerUserSegmentsProvider.setCxIntegrationMappingService(cxIntegrationMappingService);
		consumptionLayerUserSegmentsProvider.setMappers(Collections.singletonList(new TestCxConsumptionLayerProfileMapper(
				Collections.singleton(FIELD_1))));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetUserSegmentsWithNullUser()
	{
		//when
		consumptionLayerUserSegmentsProvider.getUserSegments(null);
	}

	@Test
	public void testGetUserSegmentsWhenNoMappers()
	{
		//given
		consumptionLayerUserSegmentsProvider.setMappers(null);

		//when
		final List<SegmentMappingData> result = consumptionLayerUserSegmentsProvider.getUserSegments(user);

		//then
		assertNull("Segment mapping list should be null", result);
	}

	@Test
	public void testGetUserSegmentsWhenNoRequieredFields()
	{
		//given
		final Set<String> requiredFields = null;
		consumptionLayerUserSegmentsProvider.setMappers(Collections.singletonList(new TestCxConsumptionLayerProfileMapper(
				requiredFields)));

		//when
		final List<SegmentMappingData> result = consumptionLayerUserSegmentsProvider.getUserSegments(user);

		//then
		assertNull("Segment mapping list should be null", result);

	}

	@Test
	public void testGetUserSegmentsWhenNoProfileId()
	{
		//given
		when(cxProfileIdentifierStrategy.getProfileIdentifier(user)).thenReturn(null);


		//when
		final List<SegmentMappingData> result = consumptionLayerUserSegmentsProvider.getUserSegments(user);

		//then
		verify(cxProfileIdentifierStrategy).getProfileIdentifier(user);
		assertNull("Segment mapping list should be null", result);
	}

	@Test
	public void testGetUserSegmentsWhenProfileNotFound()
	{
		//given
		when(cxProfileIdentifierStrategy.getProfileIdentifier(user)).thenReturn("profileId");
		when(cxProfileServiceClient.getProfile(Mockito.eq("profileId"), Mockito.anyString())).thenThrow(
				new NotFoundException(Integer.valueOf(404), "Not found"));

		//when
		final List<SegmentMappingData> result = consumptionLayerUserSegmentsProvider.getUserSegments(user);

		//then
		verify(cxProfileServiceClient).getProfile(Mockito.eq("profileId"), Mockito.anyString());
		assertNull("Segment mapping list should be null", result);
	}

	@Test
	public void testGetUserSegmentsWhenCharonHttpException()
	{
		//given
		when(cxProfileIdentifierStrategy.getProfileIdentifier(user)).thenReturn("profileId");
		when(cxProfileServiceClient.getProfile(Mockito.eq("profileId"), Mockito.anyString())).thenThrow(
				new HttpException(Integer.valueOf(400), "Bad request"));

		//when
		final List<SegmentMappingData> result = consumptionLayerUserSegmentsProvider.getUserSegments(user);

		//then
		verify(cxProfileServiceClient).getProfile(Mockito.eq("profileId"), Mockito.anyString());
		assertNull("Segment mapping list should be null", result);
	}

	@Test
	public void testSetMappers()
	{
		//given
		final Set<String> requiredFields1 = Sets.newHashSet(FIELD_1, FIELD_2);
		final Set<String> requiredFields2 = Collections.singleton(FIELD_3);
		final List<CxConsumptionLayerProfileMapper> mappers = Arrays.asList(
				new TestCxConsumptionLayerProfileMapper(requiredFields1), new TestCxConsumptionLayerProfileMapper(requiredFields2));

		//when
		consumptionLayerUserSegmentsProvider.setMappers(mappers);

		//then
		assertNotNull("Mappers should not be null", consumptionLayerUserSegmentsProvider.getMappers());
		assertEquals("Mappers should be equal", mappers, consumptionLayerUserSegmentsProvider.getMappers());

		final List<String> cretedFields = Arrays.asList(consumptionLayerUserSegmentsProvider.getProfileFields().split(
				ConsumptionLayerUserSegmentsProvider.FIELD_SEPARATOR));
		assertTrue("Generated fields are incorrenct", cretedFields.size() == 3);
		assertTrue("Missing field", cretedFields.contains(FIELD_1));
		assertTrue("Missing field", cretedFields.contains(FIELD_2));
		assertTrue("Missing field", cretedFields.contains(FIELD_3));
	}

	@Test
	public void testSetMappersWithDuplicatedFields()
	{
		//given
		final Set<String> requiredFields1 = Sets.newHashSet(FIELD_1, FIELD_2);
		final Set<String> requiredFields2 = Sets.newHashSet(FIELD_1, FIELD_3);
		final List<CxConsumptionLayerProfileMapper> mappers = Arrays.asList(
				new TestCxConsumptionLayerProfileMapper(requiredFields1), new TestCxConsumptionLayerProfileMapper(requiredFields2));

		//when
		consumptionLayerUserSegmentsProvider.setMappers(mappers);

		//then
		final List<String> cretedFields = Arrays.asList(consumptionLayerUserSegmentsProvider.getProfileFields().split(
				ConsumptionLayerUserSegmentsProvider.FIELD_SEPARATOR));
		assertTrue("Generated fields are incorrenct", cretedFields.size() == 3);
		assertTrue("Missing field", cretedFields.contains(FIELD_1));
		assertTrue("Missing field", cretedFields.contains(FIELD_2));
		assertTrue("Missing field", cretedFields.contains(FIELD_3));
	}

	@Test
	public void testSetMappersWithFieldsContainingSpace()
	{
		//given
		final Set<String> requiredFields1 = Sets.newHashSet(FIELD_1, FIELD_2);
		final Set<String> requiredFields2 = Sets.newHashSet(FIELD_1_WITH_SPACE);
		final List<CxConsumptionLayerProfileMapper> mappers = Arrays.asList(
				new TestCxConsumptionLayerProfileMapper(requiredFields1), new TestCxConsumptionLayerProfileMapper(requiredFields2));

		//when
		consumptionLayerUserSegmentsProvider.setMappers(mappers);

		//then
		final List<String> cretedFields = Arrays.asList(consumptionLayerUserSegmentsProvider.getProfileFields().split(
				ConsumptionLayerUserSegmentsProvider.FIELD_SEPARATOR));
		assertTrue("Generated fields are incorrenct", cretedFields.size() == 2);
		assertTrue("Missing field", cretedFields.contains(FIELD_1));
		assertTrue("Missing field", cretedFields.contains(FIELD_2));
	}

	protected class TestCxConsumptionLayerProfileMapper implements CxConsumptionLayerProfileMapper
	{
		private final Set<String> requiredFields;

		public TestCxConsumptionLayerProfileMapper(final Set<String> requiredFields)
		{
			this.requiredFields = requiredFields;
		}

		@Override
		public void populate(final Profile source, final MappingData target) throws ConversionException
		{
			// only test implementation
		}

		@Override
		public Set<String> getRequiredFields()
		{
			return requiredFields;
		}
	}
}
