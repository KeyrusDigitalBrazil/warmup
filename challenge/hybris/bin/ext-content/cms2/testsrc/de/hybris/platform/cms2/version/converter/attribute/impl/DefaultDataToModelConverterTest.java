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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.version.converter.attribute.data.VersionPayloadDescriptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
public class DefaultDataToModelConverterTest
{
	private final String PAYLOAD_VALUE = "CHECK";
	private final String PAYLOAD_TYPE = "java.lang.String";

	private final DefaultDataToModelConverter converter = new DefaultDataToModelConverter();

	private final VersionPayloadDescriptor payloadDescriptor = new VersionPayloadDescriptor(PAYLOAD_TYPE, PAYLOAD_VALUE);

	@Test
	public void shouldConvertPrimitiveValueRepresentedByStringToObjectOfSpecifiedType()
	{
		// WHEN
		final Object value = converter.convert(payloadDescriptor);

		// THEN
		assertThat(value, is(PAYLOAD_VALUE));
		assertEquals(value.getClass(), String.class);
	}
}
