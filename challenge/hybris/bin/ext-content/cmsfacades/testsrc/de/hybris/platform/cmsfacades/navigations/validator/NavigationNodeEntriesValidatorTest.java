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
package de.hybris.platform.cmsfacades.navigations.validator;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.NavigationEntryData;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.cmsfacades.navigations.service.NavigationEntryConverterRegistry;
import de.hybris.platform.cmsfacades.navigations.service.NavigationEntryItemModelConverter;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NavigationNodeEntriesValidatorTest
{

	@Mock
	private NavigationEntryConverterRegistry navigationEntryConverterRegistry;

	@Mock
	private Predicate<ItemModel> validEntryItemModelPredicate;

	@InjectMocks
	private NavigationNodeEntriesValidator validator;

	@Test
	public void testWhenIdAndTypeAreEmpty_shouldAddErrors()
	{
		final NavigationNodeData target = new NavigationNodeData();
		final Errors errors = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());

		final NavigationEntryData entry1 = new NavigationEntryData();
		target.setEntries(Arrays.asList(entry1));
		validator.validate(target, errors);

		Assert.assertThat(errors.getErrorCount(), Matchers.is(2));
	}

	@Test
	public void testWhenTypeConverterDoesNotExist_shouldAddErrors()
	{
		final NavigationNodeData target = new NavigationNodeData();
		final Errors errors = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());

		when(navigationEntryConverterRegistry.getNavigationEntryItemModelConverter(any())).thenReturn(Optional.empty());
		final NavigationEntryData entry1 = new NavigationEntryData();
		entry1.setItemId("1");
		entry1.setItemSuperType("Type");
		target.setEntries(Arrays.asList(entry1));
		validator.validate(target, errors);

		Assert.assertThat(errors.getErrorCount(), Matchers.is(1));
	}


	@Test
	public void testWhenConverterThrowsException_shouldAddErrors()
	{
		final NavigationNodeData target = new NavigationNodeData();
		final Errors errors = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());

		final NavigationEntryItemModelConverter itemModelConverter = mock(NavigationEntryItemModelConverter.class);
		final Function<NavigationEntryData, ItemModel> conversionFunction = mock(Function.class);

		when(itemModelConverter.getConverter()).thenReturn(conversionFunction);
		when(navigationEntryConverterRegistry.getNavigationEntryItemModelConverter(any()))
		.thenReturn(Optional.of(itemModelConverter));
		when(conversionFunction.apply(any())).thenThrow(new ConversionException(""));
		final NavigationEntryData entry1 = new NavigationEntryData();
		entry1.setItemId("1");
		entry1.setItemSuperType("Type");
		target.setEntries(Arrays.asList(entry1));
		validator.validate(target, errors);

		Assert.assertThat(errors.getErrorCount(), Matchers.is(1));
	}

	@Test
	public void testWhenPredicateFails_shouldAddErrors()
	{
		final NavigationNodeData target = new NavigationNodeData();
		final Errors errors = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());

		final NavigationEntryItemModelConverter itemModelConverter = mock(NavigationEntryItemModelConverter.class);
		final Function<NavigationEntryData, ItemModel> conversionFunction = mock(Function.class);

		when(itemModelConverter.getConverter()).thenReturn(conversionFunction);
		when(navigationEntryConverterRegistry.getNavigationEntryItemModelConverter(any()))
		.thenReturn(Optional.of(itemModelConverter));
		when(validEntryItemModelPredicate.test(any())).thenReturn(false);

		final NavigationEntryData entry1 = new NavigationEntryData();
		entry1.setItemId("1");
		entry1.setItemSuperType("Type");
		target.setEntries(Arrays.asList(entry1));
		validator.validate(target, errors);

		Assert.assertThat(errors.getErrorCount(), Matchers.is(1));
	}

}
