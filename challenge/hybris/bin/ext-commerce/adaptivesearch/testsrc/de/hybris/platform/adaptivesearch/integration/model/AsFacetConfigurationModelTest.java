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
import de.hybris.platform.adaptivesearch.enums.AsFacetType;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurableSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsExcludedFacetModel;
import de.hybris.platform.adaptivesearch.model.AsFacetModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedFacetModel;
import de.hybris.platform.adaptivesearch.model.AsSimpleSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchConfigurationService;
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
public class AsFacetConfigurationModelTest extends ServicelayerTransactionalTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SIMPLE_SEARCH_CONF_UID = "simpleConfiguration";

	private static final String UID1 = "e81de964-b6b8-4031-bf1a-2eeb99b606ac";
	private static final String UID2 = "e3780f3f-5e60-4174-b85d-52c84b34ee38";

	private static final String INDEX_PROPERTY1 = "property1";
	private static final String INDEX_PROPERTY2 = "property2";
	private static final String INDEX_PROPERTY3 = "property3";
	private static final String WRONG_INDEX_PROPERTY = "testPropertyError";

	private static final Integer PRIORITY1 = Integer.valueOf(1);
	private static final Integer PRIORITY2 = Integer.valueOf(2);

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private AsSearchConfigurationService asSearchConfigurationService;

	@Resource
	private AsConfigurationService asConfigurationService;

	private CatalogVersionModel onlineCatalogVersion;
	private AsSimpleSearchConfigurationModel searchConfiguration;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/model/asFacetConfigurationModelTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final Optional<AsSimpleSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(onlineCatalogVersion, SIMPLE_SEARCH_CONF_UID);
		searchConfiguration = searchConfigurationOptional.get();
	}

	@Test
	public void createMultipleFacetConfigurations() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);

		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY2);

		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY3);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
		asConfigurationService.saveConfiguration(facet);
		asConfigurationService.saveConfiguration(excludedFacet);

		// then
		assertEquals(onlineCatalogVersion, promotedFacet.getCatalogVersion());
		assertNotNull(promotedFacet.getUid());
		assertFalse(promotedFacet.getUid().isEmpty());

		assertEquals(onlineCatalogVersion, facet.getCatalogVersion());
		assertNotNull(facet.getUid());
		assertFalse(facet.getUid().isEmpty());

		assertEquals(onlineCatalogVersion, excludedFacet.getCatalogVersion());
		assertNotNull(excludedFacet.getUid());
		assertFalse(excludedFacet.getUid().isEmpty());
	}

	@Test
	public void failToCreateMultipleFacetConfigurationsWithSameUid1() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID1);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY2);
		facet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
		asConfigurationService.saveConfiguration(facet);
	}

	@Test
	public void failToCreateMultipleFacetConfigurationsWithSameUid2() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID1);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setUid(UID1);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY2);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(facet);
		asConfigurationService.saveConfiguration(excludedFacet);
	}

	@Test
	public void failToCreateMultipleFacetConfigurationsWithSameIndexProperty1() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
		asConfigurationService.saveConfiguration(facet);
	}

	@Test
	public void failToCreateMultipleFacetConfigurationsWithSameIndexProperty2() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(facet);
		asConfigurationService.saveConfiguration(excludedFacet);
	}

	@Test
	public void moveFacetConfiguration1() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(onlineCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);

		modelService.refresh(searchConfiguration);

		final boolean result = asConfigurationService.moveConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDFACETS, AbstractAsConfigurableSearchConfigurationModel.FACETS,
				UID1);

		final Optional<AsPromotedFacetModel> promotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);
		final Optional<AsFacetModel> facetOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertTrue(result);
		assertFalse(promotedFacetOptional.isPresent());
		assertTrue(facetOptional.isPresent());

		final AsFacetModel facet = facetOptional.get();
		assertEquals(onlineCatalogVersion, facet.getCatalogVersion());
		assertEquals(UID1, facet.getUid());
		assertEquals(searchConfiguration, facet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, facet.getIndexProperty());
		assertEquals(AsFacetType.REFINE, facet.getFacetType());
	}

	@Test
	public void moveFacetConfiguration2() throws Exception
	{
		// given
		final AsFacetModel facet = asConfigurationService.createConfiguration(AsFacetModel.class);
		facet.setCatalogVersion(onlineCatalogVersion);
		facet.setUid(UID1);
		facet.setSearchConfiguration(searchConfiguration);
		facet.setIndexProperty(INDEX_PROPERTY1);
		facet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(facet);

		modelService.refresh(searchConfiguration);

		final boolean result = asConfigurationService.moveConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.FACETS, AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDFACETS,
				UID1);

		final Optional<AsFacetModel> facetOptional = asConfigurationService.getConfigurationForUid(AsFacetModel.class,
				onlineCatalogVersion, UID1);
		final Optional<AsExcludedFacetModel> excludedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(result);
		assertFalse(facetOptional.isPresent());
		assertTrue(excludedFacetOptional.isPresent());

		final AsExcludedFacetModel excludedFacet = excludedFacetOptional.get();
		assertEquals(onlineCatalogVersion, excludedFacet.getCatalogVersion());
		assertEquals(UID1, excludedFacet.getUid());
		assertEquals(searchConfiguration, excludedFacet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, excludedFacet.getIndexProperty());
		assertEquals(AsFacetType.REFINE, excludedFacet.getFacetType());
	}

	@Test
	public void moveFacetConfiguration3() throws Exception
	{
		// given
		final AsExcludedFacetModel excludedFacet = asConfigurationService.createConfiguration(AsExcludedFacetModel.class);
		excludedFacet.setCatalogVersion(onlineCatalogVersion);
		excludedFacet.setUid(UID1);
		excludedFacet.setSearchConfiguration(searchConfiguration);
		excludedFacet.setIndexProperty(INDEX_PROPERTY1);
		excludedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(excludedFacet);

		modelService.refresh(searchConfiguration);

		final boolean result = asConfigurationService.moveConfiguration(searchConfiguration,
				AbstractAsConfigurableSearchConfigurationModel.EXCLUDEDFACETS,
				AbstractAsConfigurableSearchConfigurationModel.PROMOTEDFACETS, UID1);

		final Optional<AsPromotedFacetModel> promotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);
		final Optional<AsExcludedFacetModel> excludedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(result);
		assertTrue(promotedFacetOptional.isPresent());
		assertFalse(excludedFacetOptional.isPresent());

		final AsPromotedFacetModel promotedFacet = promotedFacetOptional.get();
		assertEquals(onlineCatalogVersion, promotedFacet.getCatalogVersion());
		assertEquals(UID1, promotedFacet.getUid());
		assertEquals(searchConfiguration, promotedFacet.getSearchConfiguration());
		assertEquals(INDEX_PROPERTY1, promotedFacet.getIndexProperty());
		assertEquals(AsFacetType.REFINE, promotedFacet.getFacetType());
	}
}
