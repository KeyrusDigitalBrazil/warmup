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

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_DOES_NOT_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_ITEM_UUID;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_UID;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.VERSION_DOES_NOT_BELONG_TO_CMS_ITEM;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.data.CMSVersionData;

import java.util.function.BiPredicate;
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
public class BaseCMSVersionValidatorTest
{

	private static final String VALID_ITEM_UUID = "valid-item-uuid";
	private static final String INVALID_ITEM_UUID = "invalid-item-uuid";
	private static final String VALID_VERSION_UID = "valid-version-uid";
	private static final String INVALID_VERSION_UID = "invalid-version-uid";
	private static final String LABEL = "somelabel";

	@InjectMocks
	private BaseCMSVersionValidator validator;

	@Mock
	private BiPredicate<String, Class<?>> itemModelExistsPredicate;
	@Mock
	private BiPredicate<String, Class<?>> itemModelDoesNotExistPredicate;
	@Mock
	private BiPredicate<String, String> cmsVersionBelongsToCMSItemPredicate;
	@Mock
	private BiPredicate<String, String> cmsVersionDoesNotBelongToCMSItemPredicate;
	@Mock
	private Predicate<String> cmsVersionExistsPredicate;
	@Mock
	private Predicate<String> cmsVersionDoesNotExistsPredicate;

	@Before
	public void setup()
	{
		when(itemModelExistsPredicate.negate()).thenReturn(itemModelDoesNotExistPredicate);
		when(cmsVersionExistsPredicate.negate()).thenReturn(cmsVersionDoesNotExistsPredicate);
		when(cmsVersionBelongsToCMSItemPredicate.negate()).thenReturn(cmsVersionDoesNotBelongToCMSItemPredicate);
	}

	@Test
	public void validationFailsWhenItemUUIDNotProvided()
	{
		final CMSVersionData cmsVersionData = createCMSVersionData(null, null, null);
		final Errors errors = new BeanPropertyBindingResult(cmsVersionData, cmsVersionData.getClass().getSimpleName());

		validator.validate(cmsVersionData, errors);
		MatcherAssert.assertThat(errors.getFieldErrorCount(), greaterThanOrEqualTo(1));
		MatcherAssert.assertThat(errors.getFieldErrors().get(0).getCode(), is(FIELD_REQUIRED));
		MatcherAssert.assertThat(errors.getFieldErrors().get(0).getField(), is(FIELD_ITEM_UUID));
	}

	@Test
	public void validationFailsWhenCMSItemDoesNotExist()
	{
		when(itemModelDoesNotExistPredicate.test(INVALID_ITEM_UUID, CMSItemModel.class)).thenReturn(true);

		final CMSVersionData cmsVersionData = createCMSVersionData(INVALID_ITEM_UUID, null, null);
		final Errors errors = new BeanPropertyBindingResult(cmsVersionData, cmsVersionData.getClass().getSimpleName());

		validator.validate(cmsVersionData, errors);
		MatcherAssert.assertThat(errors.getFieldErrorCount(), greaterThanOrEqualTo(1));
		MatcherAssert.assertThat(errors.getFieldErrors().get(0).getCode(), is(FIELD_DOES_NOT_EXIST));
		MatcherAssert.assertThat(errors.getFieldErrors().get(0).getField(), is(FIELD_ITEM_UUID));
	}

	@Test
	public void validationFailsWhenVersionUidIsProvidedButDoesnotExist()
	{
		when(itemModelDoesNotExistPredicate.test(VALID_ITEM_UUID, CMSItemModel.class)).thenReturn(false);
		when(cmsVersionDoesNotBelongToCMSItemPredicate.test(VALID_ITEM_UUID, INVALID_VERSION_UID)).thenReturn(false);
		when(cmsVersionDoesNotExistsPredicate.test(INVALID_VERSION_UID)).thenReturn(true);

		final CMSVersionData cmsVersionData = createCMSVersionData(VALID_ITEM_UUID, INVALID_VERSION_UID, LABEL);
		final Errors errors = new BeanPropertyBindingResult(cmsVersionData, cmsVersionData.getClass().getSimpleName());

		validator.validate(cmsVersionData, errors);
		MatcherAssert.assertThat(errors.getFieldErrorCount(), greaterThanOrEqualTo(1));
		MatcherAssert.assertThat(errors.getFieldErrors().get(0).getCode(), is(FIELD_DOES_NOT_EXIST));
		MatcherAssert.assertThat(errors.getFieldErrors().get(0).getField(), is(FIELD_UID));
	}

	@Test
	public void validationFailsWhenCMSVersionDoesNotBelongToCMSItem()
	{
		when(itemModelDoesNotExistPredicate.test(VALID_ITEM_UUID, CMSItemModel.class)).thenReturn(false);
		when(cmsVersionDoesNotBelongToCMSItemPredicate.test(VALID_ITEM_UUID, VALID_VERSION_UID)).thenReturn(true);
		when(cmsVersionDoesNotExistsPredicate.test(VALID_VERSION_UID)).thenReturn(false);

		final CMSVersionData cmsVersionData = createCMSVersionData(VALID_ITEM_UUID, VALID_VERSION_UID, LABEL);
		final Errors errors = new BeanPropertyBindingResult(cmsVersionData, cmsVersionData.getClass().getSimpleName());

		validator.validate(cmsVersionData, errors);
		MatcherAssert.assertThat(errors.getFieldErrorCount(), greaterThanOrEqualTo(1));
		MatcherAssert.assertThat(errors.getFieldErrors().get(0).getCode(), is(VERSION_DOES_NOT_BELONG_TO_CMS_ITEM));
		MatcherAssert.assertThat(errors.getFieldErrors().get(0).getField(), is(FIELD_UID));
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
