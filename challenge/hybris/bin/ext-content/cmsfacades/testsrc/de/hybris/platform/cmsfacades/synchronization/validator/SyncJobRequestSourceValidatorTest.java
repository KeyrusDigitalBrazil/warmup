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
package de.hybris.platform.cmsfacades.synchronization.validator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.common.predicate.CatalogExistsPredicate;
import de.hybris.platform.cmsfacades.common.predicate.CatalogVersionExistsPredicate;
import de.hybris.platform.cmsfacades.data.SyncRequestData;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;


@UnitTest
@RunWith(Parameterized.class)
public class SyncJobRequestSourceValidatorTest
{

	private static final Boolean HAS_ERRORS = Boolean.TRUE;
	private static final Boolean NO_ERRORS = Boolean.FALSE;
	private static final Boolean CATALOG_NOT_FINDABLE = Boolean.FALSE;
	private static final Boolean CATALOG_FINDABLE = Boolean.TRUE;
	private static final String BAD_SOURCE_VERSION = null;
	private static final String GOOD_SOURCE_VERSION = "staged";
	private static final String CATALOG_VERSION = "electronics";

	private final Boolean isCatalogFindable;
	private final String testSourceVersion;
	private final Boolean expectsErrors;

	private SyncRequestData target;
	private Errors errors;

	@InjectMocks
	private SyncJobRequestSourceValidator validator;

	@Mock
	private CatalogVersionExistsPredicate catalogVersionExistsPredicate;

	@Mock
	private CatalogExistsPredicate catalogExistsPredicate;

	@Parameterized.Parameters(name = "{index}: validate(CatalogFindable: {0}, SourceVersion: {1}) => HasErrors: {2} Is Valid:{3}")
	public static Iterable<Object[]> data()
	{
		return Arrays.asList(new Object[][]
				{
						{ CATALOG_FINDABLE, GOOD_SOURCE_VERSION, NO_ERRORS },
						{ CATALOG_FINDABLE, BAD_SOURCE_VERSION, HAS_ERRORS },
						{ CATALOG_NOT_FINDABLE, GOOD_SOURCE_VERSION, NO_ERRORS },
						{ CATALOG_NOT_FINDABLE, BAD_SOURCE_VERSION, NO_ERRORS }
				});
	}

	public SyncJobRequestSourceValidatorTest(final Boolean isCatalogFindable, final String testSourceVersion,
			final Boolean expectsErrors)
	{
		this.isCatalogFindable = isCatalogFindable;
		this.testSourceVersion = testSourceVersion;
		this.expectsErrors = expectsErrors;
	}

	@Test
	public void shouldValidateAllPropertiesWhenSyncJobRequestDataIsPassed()
	{
		setUpRequest();

		target.setCatalogId(CATALOG_VERSION);
		target.setSourceVersionId(testSourceVersion);

		validator.validate(target, errors);
		assertEquals(errors.toString(), errors.hasErrors(), expectsErrors);
	}

	protected void setUpRequest()
	{
		MockitoAnnotations.initMocks(this);

		target = new SyncRequestData();
		errors = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());
		when(catalogVersionExistsPredicate.test(CATALOG_VERSION, testSourceVersion)).thenReturn(isCatalogVersionFindable());
		when(catalogExistsPredicate.test(CATALOG_VERSION)).thenReturn(isCatalogFindable);
	}

	public boolean isCatalogVersionFindable()
	{
		boolean isFindable = false;
		if (isCatalogFindable)
		{
			isFindable = testSourceVersion != null && testSourceVersion.equals(GOOD_SOURCE_VERSION);
		}

		return isFindable;
	}
}

