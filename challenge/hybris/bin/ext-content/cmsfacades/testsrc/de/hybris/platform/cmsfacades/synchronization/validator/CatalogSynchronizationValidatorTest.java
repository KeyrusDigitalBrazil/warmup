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

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.ACTIVE_SYNC_JOB_REQUIRED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.UNAUTHORIZED_SYNCHRONIZATION_INSUFFICIENT_ACCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;

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
public class CatalogSynchronizationValidatorTest
{
	private static final String SOURCE_CATALOG_VERSION_NAME = "sourceCatalogVersionName";
	private static final String CATALOG_ID = "catalogId";
	private static final String TARGET_CATALOG_VERSION_NAME1 = "targetCatalogVersionName1";
	private static final String TARGET_CATALOG_VERSION_NAME2 = "targetCatalogVersionName2";
	private static final String PRINCIPAL_NAME = "principalName";

	@Mock
	private UserService userService;
	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private UserModel principal;
	@Mock
	private CatalogVersionModel sourceCatalogVersion;
	@Mock
	private SyncItemJobModel syncItemJobModel1;
	@Mock
	private SyncItemJobModel syncItemJobModel2;
	@Mock
	private CatalogVersionModel targetCatalogVersion1;
	@Mock
	private CatalogVersionModel targetCatalogVersion2;
	@Mock
	private CatalogSynchronizationService catalogSynchronizationService;

	@InjectMocks
	private CatalogSynchronizationValidator catalogSynchronizationValidator;

	private SyncRequestData syncRequestData;
	private Errors errors;

	@Before
	public void setup()
	{
		syncRequestData = new SyncRequestData();
		syncRequestData.setCatalogId(CATALOG_ID);
		syncRequestData.setSourceVersionId(SOURCE_CATALOG_VERSION_NAME);
		syncRequestData.setTargetVersionId(TARGET_CATALOG_VERSION_NAME2);

		errors = createErrors();

		when(userService.getCurrentUser()).thenReturn(principal);
		when(catalogVersionService.getCatalogVersion(CATALOG_ID, SOURCE_CATALOG_VERSION_NAME)).thenReturn(sourceCatalogVersion);
		when(sourceCatalogVersion.getSynchronizations()).thenReturn(Arrays.asList(syncItemJobModel1, syncItemJobModel2));

		when(syncItemJobModel1.getTargetVersion()).thenReturn(targetCatalogVersion1);
		when(syncItemJobModel2.getTargetVersion()).thenReturn(targetCatalogVersion2);
		when(syncItemJobModel1.getActive()).thenReturn(false);
		when(syncItemJobModel2.getActive()).thenReturn(true);

		when(targetCatalogVersion1.getVersion()).thenReturn(TARGET_CATALOG_VERSION_NAME1);
		when(targetCatalogVersion2.getVersion()).thenReturn(TARGET_CATALOG_VERSION_NAME2);

		when(principal.getName()).thenReturn(PRINCIPAL_NAME);
	}

	@Test
	public void shouldNotSupportValidationOfWrongClass()
	{
		// WHEN
		final boolean supports = catalogSynchronizationValidator.supports(String.class);

		// THEN
		assertFalse("Should not support validation of wrong class", supports);
	}

	@Test
	public void shouldNotReturnAnyError()
	{
		// GIVEN
		when(catalogSynchronizationService.canSynchronize(syncItemJobModel2, principal)).thenReturn(true);

		// WHEN
		catalogSynchronizationValidator.validate(syncRequestData, errors);

		// THEN
		assertFalse(
				"There should be no validation errors if there is active job AND syncPrincipalsOnly is true AND principal is in the list of syncPrincipals",
				errors.hasErrors());
	}

	@Test
	public void shouldAddNoActiveJobErrorIfNoActiveJobsToSynchronizeCatalog()
	{
		// GIVEN
		when(syncItemJobModel1.getActive()).thenReturn(false);
		when(syncItemJobModel2.getActive()).thenReturn(false);

		// WHEN
		catalogSynchronizationValidator.validate(syncRequestData, errors);

		// THEN
		assertEquals("Should add 'no active job' validation error if there is no job to synchronize catalog",
				ACTIVE_SYNC_JOB_REQUIRED,
				errors.getFieldErrors().get(0).getCode());
	}

	@Test
	public void shouldAddUnauthorizedSynchronizationIfCanSynchronizeIsFalse()
	{
		// GIVEN
		when(catalogSynchronizationService.canSynchronize(syncItemJobModel2, principal)).thenReturn(false);

		// WHEN
		catalogSynchronizationValidator.validate(syncRequestData, errors);

		// THEN
		assertEquals("Should add 'unauthorized synchronization error' if job's syncPrincipalsOnly is false",
				UNAUTHORIZED_SYNCHRONIZATION_INSUFFICIENT_ACCESS,
				errors.getFieldErrors().get(0).getCode());
	}

	protected Errors createErrors()
	{
		return new BeanPropertyBindingResult(syncRequestData, syncRequestData.getClass().getSimpleName());
	}
}
