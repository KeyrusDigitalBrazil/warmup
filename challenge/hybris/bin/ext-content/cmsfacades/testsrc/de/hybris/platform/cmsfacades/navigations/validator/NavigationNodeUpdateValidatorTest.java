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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NavigationNodeUpdateValidatorTest
{
	private static final String UID = "uid";
	private static final String NAME = "name";
	private static final String PARENT = "parent";
	private static final Integer POSITION = 1;
	@Mock
	private Predicate<String> validateUidPredicate;

	@Mock
	private CMSNavigationService navigationService;

	@InjectMocks
	private NavigationNodeUpdateValidator validator;

	@Before
	public void setup() throws CMSItemNotFoundException
	{
		final CMSNavigationNodeModel navigationNode = mock(CMSNavigationNodeModel.class);
		final CMSNavigationNodeModel parentNode = mock(CMSNavigationNodeModel.class);

		when(navigationService.getNavigationNodeForId(UID)).thenReturn(navigationNode);
		when(navigationNode.getUid()).thenReturn(UID);
		when(navigationNode.getParent()).thenReturn(parentNode);
		when(parentNode.getUid()).thenReturn(PARENT);
		when(validateUidPredicate.test(any())).thenReturn(true);
	}

	@Test
	public void testValidateWhenParentHasntChanged()
	{
		final NavigationNodeData target = new NavigationNodeData();
		target.setUid(UID);
		target.setName(NAME);
		target.setParentUid(PARENT);
		target.setPosition(POSITION);

		final Errors errors = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());

		validator.validate(target, errors);

		assertThat(errors.getErrorCount(), is(0));

	}

	@Test
	public void testValidateWhenParentHasChanged_shouldHaveErrors()
	{
		final NavigationNodeData target = new NavigationNodeData();
		target.setUid(UID);
		target.setName(NAME);
		target.setParentUid("otherParent");
		target.setPosition(POSITION);

		final Errors errors = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());

		validator.validate(target, errors);

		assertThat(errors.getErrorCount(), is(1));

	}


	@Test
	public void testValidateWhenUidIsInvalid_shouldHaveErrors() throws CMSItemNotFoundException
	{
		when(navigationService.getNavigationNodeForId(UID)).thenThrow(new CMSItemNotFoundException(""));

		final NavigationNodeData target = new NavigationNodeData();
		target.setUid(UID);
		target.setName(NAME);
		target.setParentUid("otherParent");
		target.setPosition(POSITION);

		final Errors errors = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());

		validator.validate(target, errors);

		assertThat(errors.getErrorCount(), is(1));

	}
}
