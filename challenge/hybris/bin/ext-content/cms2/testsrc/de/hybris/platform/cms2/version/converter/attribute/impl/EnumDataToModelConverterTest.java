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
package de.hybris.platform.cms2.version.converter.attribute.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.enums.CmsApprovalStatus;
import de.hybris.platform.cms2.version.converter.attribute.data.VersionPayloadDescriptor;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.enumeration.EnumerationService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class EnumDataToModelConverterTest
{

	private final String ENUM_VALUE = "CHECK";
	private final String ENUM_TYPE = "de.hybris.platform.cms2.enums.CmsApprovalStatus";

	@InjectMocks
	private EnumDataToModelConverter converter;

	@Mock
	private EnumerationService enumerationService;

	@Mock
	private HybrisEnumValue enumValue;

	private final VersionPayloadDescriptor payloadDescriptor = new VersionPayloadDescriptor(ENUM_TYPE, ENUM_VALUE);

	@Test
	public void shouldConvertHybrisEnumValueRepresentedByStringToHybrisEnumValue()
	{

		// GIVEN
		doReturn(enumValue).when(enumerationService).getEnumerationValue((Class) CmsApprovalStatus.class, ENUM_VALUE.toLowerCase());

		// WHEN
		final Object value = converter.convert(payloadDescriptor);

		// THEN
		assertThat(value, is(enumValue));

	}

}
