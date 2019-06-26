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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminComponentService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ComponentExistsPredicateTest
{
	private static final String VALID_COMPONENT_UID = "valid-component-uid";
	private static final String INVALID_COMPONENT_UID = "invalid-component-uid";

	@InjectMocks
	private final Predicate<String> predicate = new ComponentExistsPredicate();

	@Mock
	private CMSAdminComponentService cmsAdminComponentService;
	@Mock
	private AbstractCMSComponentModel component;

	private String target;

	@Before
	public void setUp()
	{
		target = VALID_COMPONENT_UID;
		when(cmsAdminComponentService.getCMSComponentForId(target)).thenReturn(component);
	}

	@Test
	public void shouldFail_ComponentNotFound()
	{
		target = INVALID_COMPONENT_UID;
		when(cmsAdminComponentService.getCMSComponentForId(target)).thenThrow(new UnknownIdentifierException("exception"));

		final boolean result = predicate.test(target);
		assertFalse(result);
	}

	@Test
	public void shouldFail_AmbiguousComponent()
	{
		target = INVALID_COMPONENT_UID;
		when(cmsAdminComponentService.getCMSComponentForId(target)).thenThrow(new AmbiguousIdentifierException("exception"));

		final boolean result = predicate.test(target);
		assertFalse(result);
	}

	@Test
	public void shouldPass_ComponentExists()
	{
		final boolean result = predicate.test(target);
		assertTrue(result);
	}
}
