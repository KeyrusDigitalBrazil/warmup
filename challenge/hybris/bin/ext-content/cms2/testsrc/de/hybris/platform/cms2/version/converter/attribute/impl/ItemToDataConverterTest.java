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

import static de.hybris.platform.core.PK.fromLong;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ItemToDataConverterTest
{

	private static final String PK_VALUE = "123";

	@InjectMocks
	private ItemToDataConverter converter;

	@Mock
	private ItemModel itemModel;

	private final PK pk = fromLong(Long.valueOf(PK_VALUE));

	@Test
	public void whenConvertNullValueReturnsNull()
	{
		assertThat(converter.convert(null), nullValue());
	}

	@Test
	public void shouldConvertValidItemAndReturnPK()
	{

		when(itemModel.getPk()).thenReturn(pk);

		assertThat(converter.convert(itemModel), is(pk));
	}


}
