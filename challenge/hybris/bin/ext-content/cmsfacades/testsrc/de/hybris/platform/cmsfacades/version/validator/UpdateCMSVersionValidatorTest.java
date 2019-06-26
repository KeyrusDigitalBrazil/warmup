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

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_ALREADY_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_LABEL;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_UID;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.CMSVersionData;

import java.util.function.BiPredicate;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateCMSVersionValidatorTest
{

	private static final String VALID_ITEM_UUID = "valid-item-uuid";
	private static final String INVALID_ITEM_UUID = "invalid-item-uuid";
	private static final String VALID_VERSION_UID = "valid-version-uid";
	private static final String INVALID_VERSION_UID = "invalid-version-uid";
	private static final String LABEL = "somelabel";

	@InjectMocks
	private UpdateCMSVersionValidator validator;

	@Mock
	private BiPredicate<String, String> versionLabelChangedPredicate;
	@Mock
	private BiPredicate<String, String> labelExistsInCMSVersionsPredicate;

	@Test
	public void validationPassesWhenValidDTOIsProvided()
	{
		when(versionLabelChangedPredicate.test(VALID_VERSION_UID, LABEL)).thenReturn(true);
		when(labelExistsInCMSVersionsPredicate.test(VALID_ITEM_UUID, LABEL)).thenReturn(false);

		final CMSVersionData cmsVersionData = createCMSVersionData(VALID_ITEM_UUID, VALID_VERSION_UID, LABEL);
		final Errors errors = new BeanPropertyBindingResult(cmsVersionData, cmsVersionData.getClass().getSimpleName());

		validator.validate(cmsVersionData, errors);
		MatcherAssert.assertThat(errors.getFieldErrorCount(), is(0));
	}

	@Test
	public void validationFailsWhenVersionUIDNotProvided()
	{
		final CMSVersionData cmsVersionData = createCMSVersionData(VALID_ITEM_UUID, null, LABEL);
		final Errors errors = new BeanPropertyBindingResult(cmsVersionData, cmsVersionData.getClass().getSimpleName());

		validator.validate(cmsVersionData, errors);
		MatcherAssert.assertThat(errors.getFieldErrorCount(), greaterThanOrEqualTo(1));
		MatcherAssert.assertThat(errors.getFieldErrors().get(0).getCode(), is(FIELD_REQUIRED));
		MatcherAssert.assertThat(errors.getFieldErrors().get(0).getField(), is(FIELD_UID));
	}

	@Test
	public void validationFailsWhenLabelNotProvided()
	{
		final CMSVersionData cmsVersionData = createCMSVersionData(VALID_ITEM_UUID, VALID_VERSION_UID, null);
		final Errors errors = new BeanPropertyBindingResult(cmsVersionData, cmsVersionData.getClass().getSimpleName());

		validator.validate(cmsVersionData, errors);
		MatcherAssert.assertThat(errors.getFieldErrorCount(), greaterThanOrEqualTo(1));
		MatcherAssert.assertThat(errors.getFieldErrors().get(0).getCode(), is(FIELD_REQUIRED));
		MatcherAssert.assertThat(errors.getFieldErrors().get(0).getField(), is(FIELD_LABEL));
	}

	@Test
	public void validationFailsWhenLabelExists()
	{
		when(versionLabelChangedPredicate.test(VALID_VERSION_UID, LABEL)).thenReturn(true);
		when(labelExistsInCMSVersionsPredicate.test(VALID_ITEM_UUID, LABEL)).thenReturn(true);

		final CMSVersionData cmsVersionData = createCMSVersionData(VALID_ITEM_UUID, VALID_VERSION_UID, LABEL);
		final Errors errors = new BeanPropertyBindingResult(cmsVersionData, cmsVersionData.getClass().getSimpleName());

		validator.validate(cmsVersionData, errors);
		MatcherAssert.assertThat(errors.getFieldErrorCount(), greaterThanOrEqualTo(1));
		MatcherAssert.assertThat(errors.getFieldErrors().get(0).getCode(), is(FIELD_ALREADY_EXIST));
		MatcherAssert.assertThat(errors.getFieldErrors().get(0).getField(), is(FIELD_LABEL));
	}

	protected CMSVersionData createCMSVersionData(final String itemUUID, final String versionUid, final String label)
	{
		final CMSVersionData cmsVersionData = new CMSVersionData();
		cmsVersionData.setUid(versionUid);
		cmsVersionData.setItemUUID(itemUUID);
		cmsVersionData.setLabel(label);
		return cmsVersionData;
	}

}
