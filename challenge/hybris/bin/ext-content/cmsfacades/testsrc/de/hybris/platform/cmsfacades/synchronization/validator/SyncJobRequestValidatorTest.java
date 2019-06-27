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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;



@UnitTest
@RunWith(Parameterized.class)
public class SyncJobRequestValidatorTest
{
	private static final String BAD_CATALOG = null;
	private static final String BAD_TARGET_VERSION = null;

	private static final String GOOD_CATALOG = "electronic";
	private static final String GOOD_TARGET_VERSION = "online";
	private static final Boolean ACCEPTABLE = Boolean.FALSE;
	private static final Boolean NOTACCEPTABLE = Boolean.TRUE;

	private final String testCatalog;
	private final String testTargetVersion;
	private final Boolean expected;

	@InjectMocks
	@Spy
	private SyncJobRequestValidator validator;

	@Mock
	private CatalogVersionExistsPredicate catalogVersionExistsPredicate;

	@Mock
	private CatalogExistsPredicate catalogExistsPredicate;

	private SyncRequestData target;
	private Errors errors;


	@Parameters(name = "{index}: validate(Catalog: {0}, TargetVersion: {1}) => Expected:{2}")
	public static Iterable<Object[]> data()
	{
		return Arrays.asList(new Object[][]
				{
						{ GOOD_CATALOG, BAD_TARGET_VERSION, NOTACCEPTABLE },
						{ GOOD_CATALOG, GOOD_TARGET_VERSION, ACCEPTABLE },
						{ BAD_CATALOG, GOOD_TARGET_VERSION, NOTACCEPTABLE } });
	}



	public SyncJobRequestValidatorTest(final String testCatalog, final String testTargetVersion,
			final Boolean expected)
	{
		this.testCatalog = testCatalog;
		this.testTargetVersion = testTargetVersion;
		this.expected = expected;
	}

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		target = new SyncRequestData();
		errors = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());
		when(catalogVersionExistsPredicate.test(testCatalog, testTargetVersion)).thenReturn(isFindable(testTargetVersion));
		when(catalogExistsPredicate.test(testCatalog)).thenReturn(isFindable(testCatalog));
	}



	@Test
	public void shouldTestValidation()
	{

		target.setCatalogId(testCatalog);
		target.setTargetVersionId(testTargetVersion);

		validator.validate(target, errors);
		assertEquals(errors.toString(), errors.hasErrors(), expected);

	}



	protected Boolean isFindable(final String object)
	{
		if (object == null)
		{
			return Boolean.FALSE;
		}
		return (!object.isEmpty());
	}
}
