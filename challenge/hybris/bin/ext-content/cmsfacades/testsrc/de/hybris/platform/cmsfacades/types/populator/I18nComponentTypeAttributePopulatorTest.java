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
package de.hybris.platform.cmsfacades.types.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class I18nComponentTypeAttributePopulatorTest
{
	private static final String QUALIFIER = "QUALIFIER";
	private static final String ITEM_TYPE = "ITEM-TYPE";
	private static final String PREFIX = "PREFIX";
	private static final String SUFFIX = "SUFFIX";

	@InjectMocks
	private I18nComponentTypeAttributePopulator i18nComponentTypeAttributePopulator;

	@Mock
	private AttributeDescriptorModel attribute;
	@Mock
	private ComposedTypeModel type;

	private ComponentTypeAttributeData attributeDto;

	@Before
	public void setUp()
	{
		i18nComponentTypeAttributePopulator.setPrefix(PREFIX);
		i18nComponentTypeAttributePopulator.setSuffix(SUFFIX);
		attributeDto = new ComponentTypeAttributeData();

		Mockito.when(attribute.getDeclaringEnclosingType()).thenReturn(type);
		Mockito.when(type.getCode()).thenReturn(ITEM_TYPE);
		Mockito.when(attribute.getQualifier()).thenReturn(QUALIFIER);
	}

	@Test
	public void shouldPopulateI18nKey()
	{
		i18nComponentTypeAttributePopulator.populate(attribute, attributeDto);

		final String value = PREFIX + "." + ITEM_TYPE.toLowerCase() + "." + QUALIFIER + "." + SUFFIX;
		Assert.assertEquals(value.toLowerCase(), attributeDto.getI18nKey());
	}

}
