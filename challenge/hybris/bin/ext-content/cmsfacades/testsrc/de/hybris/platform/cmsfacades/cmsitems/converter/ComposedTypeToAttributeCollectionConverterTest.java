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
package de.hybris.platform.cmsfacades.cmsitems.converter;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ComposedTypeToAttributeCollectionConverterTest
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private final String TYPE_1 = "TYPE_1";
	private final String TYPE_2 = "TYPE_2";
	private final String TYPE_3 = "TYPE_3";
	private final String ANCESTOR_ATTRIBUTE_1_NAME = "ancestorAttribute1";
	private final String ANCESTOR_ATTRIBUTE_2_NAME = "ancestorAttribute2";
	private final String ANCESTOR_ATTRIBUTE_3_NAME = "ancestorAttribute3";
	private final String PARENT_ATTRIBUTE_1_NAME = "parentAttribute1";
	private final String PARENT_ATTRIBUTE_2_NAME = "parentAttribute2";
	private final String ATTRIBUTE_1_NAME = "attribute1";
	private final String ATTRIBUTE_2_NAME = "attribute2";
	private final String ATTRIBUTE_3_NAME = "attribute3";

	@Mock
	private AttributeDescriptorModel ancestorAttribute1;

	@Mock
	private AttributeDescriptorModel ancestorAttribute2;

	@Mock
	private AttributeDescriptorModel ancestorAttribute3;

	@Mock
	private AttributeDescriptorModel parentAttribute1;

	@Mock
	private AttributeDescriptorModel parentAttribute2;

	@Mock
	private AttributeDescriptorModel attribute1;

	@Mock
	private AttributeDescriptorModel attribute2;

	@Mock
	private AttributeDescriptorModel attribute3;

	@Mock
	private ComposedTypeModel ancestorComposedTypeModel;

	@Mock
	private ComposedTypeModel parentComposedTypeModel;

	@Mock
	private ComposedTypeModel childComposedTypeModel;

	private Set<String> blacklistedTypes;
	private Map<String, String> typeBlacklistedAttributeMap;

	@InjectMocks
	private ComposedTypeToAttributeCollectionConverter collectorFunction;

	// --------------------------------------------------------------------------
	// Test Setup
	// --------------------------------------------------------------------------
	@Before
	public void setUp()
	{
		blacklistedTypes = new HashSet<>();
		typeBlacklistedAttributeMap = new HashMap<>();
		collectorFunction.setBlacklistedTypes(blacklistedTypes);
		collectorFunction.setTypeBlacklistedAttributeMap(typeBlacklistedAttributeMap);

		// Composed Type Models
		// -- ANCESTOR
		when(ancestorComposedTypeModel.getCode()).thenReturn(TYPE_1);
		when(ancestorComposedTypeModel.getAllSuperTypes()).thenReturn(Collections.emptyList());
		when(ancestorComposedTypeModel.getDeclaredattributedescriptors())
				.thenReturn(Arrays.asList(ancestorAttribute1, ancestorAttribute2, ancestorAttribute3));

		when(ancestorAttribute1.getQualifier()).thenReturn(ANCESTOR_ATTRIBUTE_1_NAME);
		when(ancestorAttribute2.getQualifier()).thenReturn(ANCESTOR_ATTRIBUTE_2_NAME);
		when(ancestorAttribute3.getQualifier()).thenReturn(ANCESTOR_ATTRIBUTE_3_NAME);

		// -- PARENT
		when(parentComposedTypeModel.getCode()).thenReturn(TYPE_2);
		when(parentComposedTypeModel.getDeclaredattributedescriptors())
				.thenReturn(Arrays.asList(parentAttribute1, parentAttribute2));

		when(parentAttribute1.getQualifier()).thenReturn(PARENT_ATTRIBUTE_1_NAME);
		when(parentAttribute2.getQualifier()).thenReturn(PARENT_ATTRIBUTE_2_NAME);

		// -- CHILD
		when(childComposedTypeModel.getCode()).thenReturn(TYPE_3);
		when(childComposedTypeModel.getAllSuperTypes()).thenReturn(Arrays.asList(parentComposedTypeModel, ancestorComposedTypeModel));
		when(childComposedTypeModel.getDeclaredattributedescriptors())
				.thenReturn(Arrays.asList(attribute1, attribute2, attribute3));

		when(attribute1.getQualifier()).thenReturn(ATTRIBUTE_1_NAME);
		when(attribute2.getQualifier()).thenReturn(ATTRIBUTE_2_NAME);
		when(attribute3.getQualifier()).thenReturn(ATTRIBUTE_3_NAME);
	}

	// --------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------
	@Test
	public void givenEmptyComposedTypeModel_WhenCalled_ThenItReturnsAnEmptyList()
	{
		// WHEN
		Collection<AttributeDescriptorModel> result = collectorFunction.convert(null);

		// THEN
		assertThat(result, nullValue());
	}

	@Test
	public void givenTopLevelComposedTypeModel_WhenCalled_ThenItReturnsAFilledList()
	{
		// GIVEN
		setUpBlackListMap();

		// WHEN
		Collection<AttributeDescriptorModel> result = collectorFunction.convert(ancestorComposedTypeModel);

		// THEN
		assertThat(result, containsInAnyOrder(ancestorAttribute1, ancestorAttribute2, ancestorAttribute3));
	}

	@Test
	public void givenTopLevelComposedTypeModel_WhenCalled_ThenItReturnsAFilledListWithoutBlacklistedItems()
	{
		// GIVEN
		blackListAttributes(ancestorComposedTypeModel, ANCESTOR_ATTRIBUTE_2_NAME);
		setUpBlackListMap();

		// WHEN
		Collection<AttributeDescriptorModel> result = collectorFunction.convert(ancestorComposedTypeModel);

		// THEN
		assertThat(result, containsInAnyOrder(ancestorAttribute1, ancestorAttribute3));
	}

	@Test
	public void givenTopLevelBlacklistedModel_WhenCalled_ThenItReturnsAnEmptyList()
	{
		// GIVEN
		blackListType(ancestorComposedTypeModel);
		setUpBlackListMap();

		// WHEN
		Collection<AttributeDescriptorModel> result = collectorFunction.convert(ancestorComposedTypeModel);

		// THEN
		assertThat(result.isEmpty(), is(true));
	}

	@Test
	public void givenComposedTypeModel_WhenCalled_ThenItReturnsListWithSelfAndParentsNonBlacklistedAttributes()
	{
		// GIVEN
		blackListType(parentComposedTypeModel);
		blackListAttributes(ancestorComposedTypeModel, ancestorAttribute3.getQualifier());
		blackListAttributes(childComposedTypeModel, attribute1.getQualifier());
		setUpBlackListMap();

		// WHEN
		Collection<AttributeDescriptorModel> result = collectorFunction.convert(childComposedTypeModel);

		// THEN
		assertThat(result, containsInAnyOrder(ancestorAttribute1, ancestorAttribute2, attribute2, attribute3));
	}

	// --------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------
	protected void blackListAttributes(ComposedTypeModel composedTypeModel, String... attributes)
	{
		StringBuilder blackListedAttributesString = new StringBuilder();
		for(String attr : attributes)
		{
			blackListedAttributesString.append(attr);
			blackListedAttributesString.append(", ");
		}

		typeBlacklistedAttributeMap.put(composedTypeModel.getCode(), blackListedAttributesString.toString());
	}

	protected void blackListType(ComposedTypeModel composedTypeModel)
	{
		blacklistedTypes.add(composedTypeModel.getCode());
	}

	protected void setUpBlackListMap()
	{
		try
		{
			collectorFunction.afterPropertiesSet();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
