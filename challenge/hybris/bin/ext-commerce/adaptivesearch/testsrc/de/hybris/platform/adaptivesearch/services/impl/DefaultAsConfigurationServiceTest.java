/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.adaptivesearch.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.daos.AsConfigurationDao;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurationModel;
import de.hybris.platform.adaptivesearch.strategies.AsCloneStrategy;
import de.hybris.platform.adaptivesearch.strategies.AsValidationStrategy;
import de.hybris.platform.catalog.model.CatalogVersionModel;

import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAsConfigurationServiceTest
{
	private static final String CONFIGURATION_UID = "configuration";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private AsConfigurationDao asConfigurationDao;

	@Mock
	private AsCloneStrategy asCloneStrategy;

	@Mock
	private AsValidationStrategy asValidationStrategy;

	private DefaultAsConfigurationService defaultAsConfigurationService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		defaultAsConfigurationService = new DefaultAsConfigurationService();
		defaultAsConfigurationService.setAsConfigurationDao(asConfigurationDao);
		defaultAsConfigurationService.setAsCloneStrategy(asCloneStrategy);
	}

	@Test
	public void getByUid()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		final Optional<AbstractAsConfigurationModel> expectedConfiguration = Optional.of(mock(AbstractAsConfigurationModel.class));

		when(asConfigurationDao.findConfigurationByUid(AbstractAsConfigurationModel.class, catalogVersion, CONFIGURATION_UID))
				.thenReturn(expectedConfiguration);

		// when
		final Optional<AbstractAsConfigurationModel> configuration = defaultAsConfigurationService
				.getConfigurationForUid(AbstractAsConfigurationModel.class, catalogVersion, CONFIGURATION_UID);

		// then
		assertSame(expectedConfiguration, configuration);
	}

	@Test
	public void cannotGetByUid()
	{
		// given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);

		when(asConfigurationDao.findConfigurationByUid(AbstractAsConfigurationModel.class, catalogVersion, CONFIGURATION_UID))
				.thenReturn(Optional.empty());

		// when
		final Optional<AbstractAsConfigurationModel> configuration = defaultAsConfigurationService
				.getConfigurationForUid(AbstractAsConfigurationModel.class, catalogVersion, CONFIGURATION_UID);

		// then
		assertFalse(configuration.isPresent());
	}

	@Test
	public void cloneConfiguration()
	{
		//given
		final AbstractAsConfigurationModel configuration = mock(AbstractAsConfigurationModel.class);
		final AbstractAsConfigurationModel clonedConfiguration = mock(AbstractAsConfigurationModel.class);
		when(asCloneStrategy.clone(configuration)).thenReturn(clonedConfiguration);

		//when
		final AbstractAsConfigurationModel clone = asCloneStrategy.clone(configuration);

		//then
		assertSame(clonedConfiguration, clone);
	}

	@Test
	public void isValidConfiguration()
	{
		//given
		final AbstractAsConfigurationModel configuration = mock(AbstractAsConfigurationModel.class);
		when(asValidationStrategy.isValid(configuration)).thenReturn(true);

		//when
		final boolean valid = asValidationStrategy.isValid(configuration);

		//then
		assertTrue(valid);
	}

	@Test
	public void isNotValidConfiguration()
	{
		//given
		final AbstractAsConfigurationModel configuration = mock(AbstractAsConfigurationModel.class);
		when(asValidationStrategy.isValid(configuration)).thenReturn(false);

		//when
		final boolean valid = asValidationStrategy.isValid(configuration);

		//then
		assertFalse(valid);
	}
}
