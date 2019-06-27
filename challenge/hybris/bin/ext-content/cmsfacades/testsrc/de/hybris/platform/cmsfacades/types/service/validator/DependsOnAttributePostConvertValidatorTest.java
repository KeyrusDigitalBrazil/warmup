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
package de.hybris.platform.cmsfacades.types.service.validator;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.types.populator.DependsOnComponentTypeAttributePopulator;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DependsOnAttributePostConvertValidatorTest
{

	private static final String ATTR_1 = "attr1";
	private static final String ATTR_2 = "attr2";
	private final DependsOnAttributePostCreationValidator validator = new DependsOnAttributePostCreationValidator();

	private final ComponentTypeStructure target = mock(ComponentTypeStructure.class);
	private final Errors errors = mock(Errors.class);
	private final ComponentTypeAttributeStructure attr1 = mock(ComponentTypeAttributeStructure.class);
	private final ComponentTypeAttributeStructure attr2 = mock(ComponentTypeAttributeStructure.class);

	@Before
	public void setup()
	{
		final Set<ComponentTypeAttributeStructure> attributes = new HashSet<>();
		attributes.add(attr1);
		attributes.add(attr2);
		when(attr1.getQualifier()).thenReturn(ATTR_1);
		when(attr2.getQualifier()).thenReturn(ATTR_2);
		when(target.getAttributes()).thenReturn(attributes);
	}

	@Test
	public void testWhenAttributesHaveNoDepedencies_shouldHaveNoErrors()
	{
		validator.validate(target, errors);
		verifyZeroInteractions(errors);
	}

	@Test
	public void testWhenAttributesHaveValidDepedencies_shouldHaveNoErrors()
	{
		final List<Populator<AttributeDescriptorModel, ComponentTypeAttributeData>> populators = new LinkedList<>();
		final DependsOnComponentTypeAttributePopulator dependsOnPopulator = mock(DependsOnComponentTypeAttributePopulator.class);
		when(dependsOnPopulator.getDependsOn()).thenReturn(ATTR_1);
		populators.add(dependsOnPopulator);
		when(attr2.getPopulators()).thenReturn(populators);
		validator.validate(target, errors);
		verifyZeroInteractions(errors);
	}

	@Test
	public void testWhenAttributesHaveInvalidDepedencies_shouldHaveOneError()
	{
		final List<Populator<AttributeDescriptorModel, ComponentTypeAttributeData>> populators = new ArrayList<>();
		final DependsOnComponentTypeAttributePopulator dependsOnPopulator = mock(DependsOnComponentTypeAttributePopulator.class);
		when(dependsOnPopulator.getDependsOn()).thenReturn("invalid");
		populators.add(dependsOnPopulator);
		when(attr2.getPopulators()).thenReturn(populators);
		validator.validate(target, errors);
		verify(errors).rejectValue(anyString(), anyString(), anyObject(), any());
	}
}
