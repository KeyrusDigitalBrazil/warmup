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
package de.hybris.platform.cmsfacades.common.predicate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ItemModelExistsPredicateTest
{
	@InjectMocks
	private ItemModelExistsPredicate predicate;

	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@Captor
	private ArgumentCaptor<String> userGroupKeyCaptor;

	@Captor
	private ArgumentCaptor<Class<?>> classCaptor;

	private final String invalidKey = "invalid-key";
	private final String validKey = "valid-key";

	@Test
	public void shouldReturnFalseBecauseOfThrownUnknownIdentifierException()
	{
		//prepare
		when(uniqueItemIdentifierService.getItemModel(invalidKey, UserGroupModel.class))
		.thenThrow(new UnknownIdentifierException("unknown exception"));

		//execute
		final boolean result = predicate.test(invalidKey, UserGroupModel.class);

		//assert
		assertThat("UserGroupExistsPredicateTest should return false after UnknownIdentifierException", result, is(false));
	}

	@Test
	public void shouldReturnFalseBecauseOfThrownConversionException()
	{
		//prepare
		when(uniqueItemIdentifierService.getItemModel(invalidKey, UserGroupModel.class))
		.thenThrow(new ConversionException("conversion exception"));

		//execute
		final boolean result = predicate.test(invalidKey, UserGroupModel.class);

		//assert
		assertThat("UserGroupExistsPredicateTest should return false after ConversionException", result, is(false));
	}

	@Test
	public void shouldReturnFalseBecauseOfNoSuchElementException()
	{
		//prepare
		when(uniqueItemIdentifierService.getItemModel(invalidKey, UserGroupModel.class))
		.thenThrow(new NoSuchElementException("no such element exception"));

		//execute
		final boolean result = predicate.test(invalidKey, UserGroupModel.class);

		//assert
		assertThat("UserGroupExistsPredicateTest should return false after NoSuchElementException", result, is(false));
	}

	@Test
	public void shouldPassWithValidKey()
	{
		//prepare
		when(uniqueItemIdentifierService.getItemModel(validKey, UserGroupModel.class)).thenReturn(Optional.of(new UserGroupModel()));

		//execute
		final boolean result = predicate.test(validKey, UserGroupModel.class);

		//assert
		assertThat("UserGroupExistsPredicateTest should pass because of valid key", result, is(true));
		verify(uniqueItemIdentifierService, times(1)).getItemModel(userGroupKeyCaptor.capture(), classCaptor.capture());

		assertEquals("UserGroupExistsPredicateTest uniqueItemIdentifierService.getItemModel " +
				"should use argument with proper user group key", validKey, userGroupKeyCaptor.getValue());
		assertEquals("UserGroupExistsPredicateTest uniqueItemIdentifierService.getItemModel " +
				"should use argument with proper UserGroupModel class", UserGroupModel.class, classCaptor.getValue());

	}

}
