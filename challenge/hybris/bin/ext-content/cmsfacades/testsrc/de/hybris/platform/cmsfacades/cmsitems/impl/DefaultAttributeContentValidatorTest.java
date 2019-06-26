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
package de.hybris.platform.cmsfacades.cmsitems.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.cmsitems.AttributeContentValidator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAttributeContentValidatorTest
{
	
	
	private DefaultAttributeContentValidator attributeContentValidator = new DefaultAttributeContentValidator();
	
	private Map<Predicate<AttributeDescriptorModel>, AttributeContentValidator> baseValidatorMap = new HashMap<>();
	
	@Mock
	private Predicate<AttributeDescriptorModel> predicate1;
	
	@Mock
	private Predicate<AttributeDescriptorModel> predicate2;
	
	@Mock
	private AttributeContentValidator validator1;

	@Mock
	private AttributeContentValidator validator2;

	@Mock
	private AttributeContentValidator validator3;

	@Mock
	private AttributeContentValidator validator4;

	@Mock
	private AttributeDescriptorModel attributeDescriptorModel;
	
	@Mock
	private Object object;

	private Map mapObject = new HashMap();

	private Collection collectionObject = new ArrayList();
	
	@Mock
	private TypeModel attributeType;


	@Before
	public void setup()
	{
		baseValidatorMap.put(predicate1, validator1);
		baseValidatorMap.put(predicate2, validator2);

		attributeContentValidator.setValidatorMap(baseValidatorMap);

		when(attributeType.getItemtype()).thenReturn("java.lang.String");
		when(attributeDescriptorModel.getAttributeType()).thenReturn(attributeType);
	}
	
	@Test
	public void testWhenAttributeIsTrueForBothPredicates_shouldPerformValidationWithAllValidators()
	{
		when(predicate1.test(attributeDescriptorModel)).thenReturn(true);
		when(predicate2.test(attributeDescriptorModel)).thenReturn(true);
		attributeContentValidator.validate(object, attributeDescriptorModel);

		verify(validator1).validate(object, attributeDescriptorModel);
		verify(validator2).validate(object, attributeDescriptorModel);

	}

	@Test
	public void testWhenAttributeIsTrueForOnePredicates_shouldPerformValidationWithOneValidators()
	{
		when(predicate1.test(attributeDescriptorModel)).thenReturn(true);
		when(predicate2.test(attributeDescriptorModel)).thenReturn(false);
		attributeContentValidator.validate(object, attributeDescriptorModel);

		verify(validator1).validate(object, attributeDescriptorModel);
		verifyZeroInteractions(validator2);
	}

	@Test
	public void testWhenAttributeIsTrueForOnePredicatesCollection_shouldPerformValidationWithOneValidators()
	{
		when(attributeType.getItemtype()).thenReturn(DefaultAttributeContentValidator.COLLECTION_TYPE);
		
		when(predicate1.test(attributeDescriptorModel)).thenReturn(true);
		when(predicate2.test(attributeDescriptorModel)).thenReturn(false);
		attributeContentValidator.validate(collectionObject, attributeDescriptorModel);

		verify(validator1).validate(collectionObject, attributeDescriptorModel);
		verifyZeroInteractions(validator3);
	}

	@Test
	public void testWhenAttributeIsTrueForOnePredicatesMap_shouldPerformValidationWithOneValidators()
	{
		when(attributeDescriptorModel.getLocalized()).thenReturn(true);
		
		when(predicate1.test(attributeDescriptorModel)).thenReturn(true);
		when(predicate2.test(attributeDescriptorModel)).thenReturn(false);
		attributeContentValidator.validate(mapObject, attributeDescriptorModel);

		verify(validator1).validate(mapObject, attributeDescriptorModel);
		verifyZeroInteractions(validator3);
	}
}
