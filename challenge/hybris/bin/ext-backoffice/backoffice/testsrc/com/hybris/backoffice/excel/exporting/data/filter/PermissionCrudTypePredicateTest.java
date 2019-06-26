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
package com.hybris.backoffice.excel.exporting.data.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PermissionCrudTypePredicateTest
{
	@Mock
	PermissionCRUDService mockedPermissionCRUDService;
	@Spy
	@InjectMocks
	PermissionCrudTypePredicate permissionCrudTypePredicate;

	@Test
	public void shouldNotIncludeTypesToWhichTheUserHasNoReadAccess()
	{
		// given
		final String typeCode = "product";
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		given(composedTypeModel.getCode()).willReturn(typeCode);

		given(mockedPermissionCRUDService.canReadType(composedTypeModel)).willReturn(false);

		// when
		final boolean result = permissionCrudTypePredicate.test(composedTypeModel);

		// then
		assertThat(result).isFalse();
	}

	@Test
	public void shouldIncludeTypesToWhichTheUserHasReadAccess()
	{
		// given
		final String typeCode = "product";
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		given(composedTypeModel.getCode()).willReturn(typeCode);

		given(mockedPermissionCRUDService.canReadType(composedTypeModel)).willReturn(true);

		// when
		final boolean result = permissionCrudTypePredicate.test(composedTypeModel);

		// then
		assertThat(result).isTrue();
	}
}
