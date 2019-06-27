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
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.CMSVersionData;

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
public class RollbackCMSVersionValidatorTest
{
	@InjectMocks
	private RollbackCMSVersionValidator validator;

	@Mock
	private CMSVersionData versionData;
	private Errors errors;

	@Before
	public void setUp()
	{
		errors = new BeanPropertyBindingResult(versionData, versionData.getClass().getSimpleName());
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

}
