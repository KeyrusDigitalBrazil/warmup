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
package de.hybris.platform.adaptivesearch.integration.synchronization;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.enums.AsFacetType;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurableSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsFacetConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedFacetModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedFacetValueModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.impex.jalo.ImpExException;

import java.util.ArrayList;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsPromotedFacetSynchronizationTest extends AbstractAsSynchronizationTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SEARCH_PROFILE_CODE = "searchProfile";
	private static final String SEARCH_CONFIGURATION_UID = "searchConfiguration";

	private static final String UID1 = "cde588ec-d453-48bd-a3b1-b9aa00402256";
	private static final String UID2 = "2192804d-96e8-444f-a87f-9e1cd4e9503a";
	private static final String UID3 = "a2513763-c87c-4f21-ae5c-d771b1931432";

	private static final String INDEX_PROPERTY1 = "property1";

	private static final Integer PRIORITY1 = Integer.valueOf(1);

	private static final String VALUE1 = "value1";
	private static final String VALUE2 = "value2";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CatalogSynchronizationService catalogSynchronizationService;

	@Resource
	private AsSearchProfileService asSearchProfileService;

	@Resource
	private AsSearchConfigurationService asSearchConfigurationService;

	@Resource
	private AsConfigurationService asConfigurationService;

	private CatalogVersionModel onlineCatalogVersion;
	private CatalogVersionModel stagedCatalogVersion;
	private AbstractAsSearchProfileModel searchProfile;
	private AbstractAsConfigurableSearchConfigurationModel searchConfiguration;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/asBase.impex", CharEncoding.UTF_8);
		importCsv("/adaptivesearch/test/integration/asSimpleSearchProfile.impex", CharEncoding.UTF_8);
		importCsv("/adaptivesearch/test/integration/asSimpleSearchConfiguration.impex", CharEncoding.UTF_8);

		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final Optional<AbstractAsSearchProfileModel> searchProfileOptional = asSearchProfileService
				.getSearchProfileForCode(stagedCatalogVersion, SEARCH_PROFILE_CODE);
		searchProfile = searchProfileOptional.get();

		final Optional<AbstractAsConfigurableSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(stagedCatalogVersion, SEARCH_CONFIGURATION_UID);
		searchConfiguration = searchConfigurationOptional.get();
	}

	@Test
	public void promotedFacetNotFoundBeforeSynchronization() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(stagedCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);

		final Optional<AsPromotedFacetModel> synchronizedPromotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(synchronizedPromotedFacetOptional.isPresent());
	}

	@Test
	public void synchronizeNewPromotedFacet() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(stagedCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(stagedCatalogVersion);
		promotedValue.setUid(UID2);
		promotedValue.setFacetConfiguration(promotedFacet);
		promotedValue.setValue(VALUE2);

		final AsPromotedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		excludedValue.setCatalogVersion(stagedCatalogVersion);
		excludedValue.setUid(UID3);
		excludedValue.setFacetConfiguration(promotedFacet);
		excludedValue.setValue(VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
		asConfigurationService.saveConfiguration(promotedValue);
		asConfigurationService.saveConfiguration(excludedValue);
		asConfigurationService.refreshConfiguration(promotedFacet);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsPromotedFacetModel> synchronizedPromotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(synchronizedPromotedFacetOptional.isPresent());

		final AsPromotedFacetModel synchronizedPromotedFacet = synchronizedPromotedFacetOptional.get();
		assertSynchronized(promotedFacet, synchronizedPromotedFacet, AbstractAsFacetConfigurationModel.UNIQUEIDX);
	}

	@Test
	public void synchronizeUpdatedPromotedFacet() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(stagedCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(stagedCatalogVersion);
		promotedValue.setUid(UID2);
		promotedValue.setFacetConfiguration(promotedFacet);
		promotedValue.setValue(VALUE2);

		final AsPromotedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		excludedValue.setCatalogVersion(stagedCatalogVersion);
		excludedValue.setUid(UID3);
		excludedValue.setFacetConfiguration(promotedFacet);
		excludedValue.setValue(VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
		asConfigurationService.saveConfiguration(promotedValue);
		asConfigurationService.saveConfiguration(excludedValue);
		asConfigurationService.refreshConfiguration(promotedFacet);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		promotedFacet.setFacetType(AsFacetType.MULTISELECT_OR);
		promotedFacet.setPromotedValues(new ArrayList<>());
		promotedFacet.setExcludedValues(new ArrayList<>());

		asConfigurationService.saveConfiguration(promotedFacet);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsPromotedFacetModel> synchronizedPromotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(synchronizedPromotedFacetOptional.isPresent());

		final AsPromotedFacetModel synchronizedPromotedFacet = synchronizedPromotedFacetOptional.get();
		assertSynchronized(promotedFacet, synchronizedPromotedFacet, AbstractAsFacetConfigurationModel.UNIQUEIDX);
	}

	@Test
	public void synchronizeRemovedPromotedFacet() throws Exception
	{
		// given
		final AsPromotedFacetModel promotedFacet = asConfigurationService.createConfiguration(AsPromotedFacetModel.class);
		promotedFacet.setCatalogVersion(stagedCatalogVersion);
		promotedFacet.setUid(UID1);
		promotedFacet.setSearchConfiguration(searchConfiguration);
		promotedFacet.setIndexProperty(INDEX_PROPERTY1);
		promotedFacet.setFacetType(AsFacetType.REFINE);

		// when
		asConfigurationService.saveConfiguration(promotedFacet);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		asConfigurationService.removeConfiguration(promotedFacet);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsPromotedFacetModel> synchronizedPromotedFacetOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(synchronizedPromotedFacetOptional.isPresent());
	}
}
