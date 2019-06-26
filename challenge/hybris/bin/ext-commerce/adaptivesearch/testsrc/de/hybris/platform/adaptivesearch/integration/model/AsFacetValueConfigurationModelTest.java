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
package de.hybris.platform.adaptivesearch.integration.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.model.AbstractAsFacetConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsExcludedFacetValueModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedFacetValueModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsFacetValueConfigurationModelTest extends ServicelayerTransactionalTest
{
	private static final String CATALOG_ID = "hwcatalog";
	private static final String VERSION_STAGED = "Staged";
	private static final String VERSION_ONLINE = "Online";

	private static final String UID1 = "e81de964-b6b8-4031-bf1a-2eeb99b606ac";
	private static final String UID2 = "e3780f3f-5e60-4174-b85d-52c84b34ee38";

	private static final String FACET_VALUE1 = "FacetValue1";
	private static final String FACET_VALUE2 = "FacetValue2";

	private static final String FACET_CONFIGURATION_ID = "facetConfigurationID";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private AsConfigurationService asConfigurationService;

	private CatalogVersionModel onlineCatalogVersion;
	private AbstractAsFacetConfigurationModel facetConfiguration;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/model/asFacetValueConfigurationModelTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final Optional<AbstractAsFacetConfigurationModel> facetConfigurationOptional = asConfigurationService
				.getConfigurationForUid(AbstractAsFacetConfigurationModel.class, onlineCatalogVersion, FACET_CONFIGURATION_ID);
		facetConfiguration = facetConfigurationOptional.get();
	}

	@Test
	public void createMultipleFacetValueConfigurations()
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setFacetConfiguration(facetConfiguration);
		promotedValue.setValue(FACET_VALUE1);

		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setFacetConfiguration(facetConfiguration);
		excludedValue.setValue(FACET_VALUE2);

		// when
		asConfigurationService.saveConfiguration(promotedValue);
		asConfigurationService.saveConfiguration(excludedValue);

		// then
		assertEquals(onlineCatalogVersion, promotedValue.getCatalogVersion());
		assertNotNull(promotedValue.getUid());
		assertFalse(promotedValue.getUid().isEmpty());

		assertEquals(onlineCatalogVersion, excludedValue.getCatalogVersion());
		assertNotNull(excludedValue.getUid());
		assertFalse(excludedValue.getUid().isEmpty());
	}

	@Test
	public void failToCreateMultipleFacetValueConfigurationsWithSameUid()
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(facetConfiguration);
		promotedValue.setValue(FACET_VALUE1);

		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setUid(UID1);
		excludedValue.setFacetConfiguration(facetConfiguration);
		excludedValue.setValue(FACET_VALUE2);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedValue);
		asConfigurationService.saveConfiguration(excludedValue);
	}

	@Test
	public void failToCreateMultipleFacetValueConfigurationsWithSameValue()
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(facetConfiguration);
		promotedValue.setValue(FACET_VALUE1);

		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setUid(UID2);
		excludedValue.setFacetConfiguration(facetConfiguration);
		excludedValue.setValue(FACET_VALUE1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedValue);
		asConfigurationService.saveConfiguration(excludedValue);
	}

	@Test
	public void moveFacetValueConfiguration1() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(facetConfiguration);
		promotedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedValue);

		modelService.refresh(facetConfiguration);

		final boolean result = asConfigurationService.moveConfiguration(facetConfiguration,
				AbstractAsFacetConfigurationModel.PROMOTEDVALUES, AbstractAsFacetConfigurationModel.EXCLUDEDVALUES, UID1);

		final Optional<AsPromotedFacetValueModel> promotedValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);
		final Optional<AsExcludedFacetValueModel> excludedValueOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(result);
		assertFalse(promotedValueOptional.isPresent());
		assertTrue(excludedValueOptional.isPresent());

		final AsExcludedFacetValueModel excludedValue = excludedValueOptional.get();
		assertEquals(onlineCatalogVersion, excludedValue.getCatalogVersion());
		assertEquals(UID1, excludedValue.getUid());
		assertEquals(facetConfiguration, excludedValue.getFacetConfiguration());
		assertEquals(FACET_VALUE1, excludedValue.getValue());
	}

	@Test
	public void moveFacetValueConfiguration2() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setUid(UID1);
		excludedValue.setFacetConfiguration(facetConfiguration);
		excludedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(excludedValue);

		modelService.refresh(facetConfiguration);

		final boolean result = asConfigurationService.moveConfiguration(facetConfiguration,
				AbstractAsFacetConfigurationModel.EXCLUDEDVALUES, AbstractAsFacetConfigurationModel.PROMOTEDVALUES, UID1);

		final Optional<AsPromotedFacetValueModel> promotedValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);
		final Optional<AsExcludedFacetValueModel> excludedValueOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(result);
		assertTrue(promotedValueOptional.isPresent());
		assertFalse(excludedValueOptional.isPresent());

		final AsPromotedFacetValueModel promotedValue = promotedValueOptional.get();
		assertEquals(onlineCatalogVersion, promotedValue.getCatalogVersion());
		assertEquals(UID1, promotedValue.getUid());
		assertEquals(facetConfiguration, promotedValue.getFacetConfiguration());
		assertEquals(FACET_VALUE1, promotedValue.getValue());
	}
}
