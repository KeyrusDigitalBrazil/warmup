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
package de.hybris.platform.cmsfacades.version.validator;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_UID;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.VERSION_REMOVE_INVALID_VERSION_UID;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.CMSVersionData;

import java.util.function.Predicate;

import org.hamcrest.MatcherAssert;
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
public class DeleteCMSVersionValidatorTest
{
	private static final String VERSION_UID = "test-version-uid";

	@InjectMocks
	private DeleteCMSVersionValidator validator;

	@Mock
	private Predicate<String> isLabeledVersionPredicate;
	@Mock
	private Predicate<String> isLabeledVersionNegatePredicate;

	@Mock
	private CMSVersionData versionData;
	private Errors errors;

	@Before
	public void setUp()
	{
		errors = new BeanPropertyBindingResult(versionData, versionData.getClass().getSimpleName());
		when(isLabeledVersionPredicate.negate()).thenReturn(isLabeledVersionNegatePredicate);
	}

	@Test
	public void shouldSupportTypeCMSVersionData()
	{
		final boolean value = validator.supports(CMSVersionData.class);

		assertTrue("Should support CMSVersionData class", value);
	}

	@Test
	public void shouldSupportTypeExtendingCMSVersionData()
	{
		final boolean value = validator.supports(MockVersionData.class);

		assertTrue("Should support classes extending from CMSVersionData", value);
	}

	@Test
	public void shouldNotSupportTypeParentOfCMSVersionData()
	{
		final boolean value = validator.supports(Object.class);

		assertFalse("Should not support classes not assignable from CMSVersionData", value);
	}

	@Test
	public void shouldPassValidationWhenVersionHasLabel()
	{
		when(versionData.getUid()).thenReturn(VERSION_UID);
		when(isLabeledVersionNegatePredicate.test(VERSION_UID)).thenReturn(false);

		validator.validate(versionData, errors);

		assertThat(errors.getErrorCount(), equalTo(0));
	}

	@Test
	public void validationFailsWhenVersionUIDNotProvided()
	{
		when(versionData.getUid()).thenReturn(null);

		validator.validate(versionData, errors);
		MatcherAssert.assertThat(errors.getFieldErrorCount(), greaterThanOrEqualTo(1));
		MatcherAssert.assertThat(errors.getFieldErrors().get(0).getCode(), is(FIELD_REQUIRED));
		MatcherAssert.assertThat(errors.getFieldErrors().get(0).getField(), is(FIELD_UID));
	}

	@Test
	public void shouldFailValidationWhenVersionHasNoLabel()
	{
		when(versionData.getUid()).thenReturn(VERSION_UID);
		when(isLabeledVersionNegatePredicate.test(VERSION_UID)).thenReturn(true);

		validator.validate(versionData, errors);

		assertThat(errors.getErrorCount(), equalTo(1));
		assertThat(errors.getFieldError().getCode(), equalTo(VERSION_REMOVE_INVALID_VERSION_UID));
		assertThat(errors.getFieldError().getField(), equalTo(FIELD_UID));
	}

	class MockVersionData extends CMSVersionData
	{
	}
}
