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
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurableSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsFacetConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsSortConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsPromotedFacetValueModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchConfigurationService;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.impex.jalo.ImpExException;

import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsPromotedFacetValueSynchronizationTest extends AbstractAsSynchronizationTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SEARCH_PROFILE_CODE = "searchProfile";
	private static final String SEARCH_CONFIGURATION_UID = "searchConfiguration";
	private static final String FACET_CONFIGURATION_UID = "facet";

	private static final String UID1 = "cde588ec-d453-48bd-a3b1-b9aa00402256";

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
	private AbstractAsFacetConfigurationModel facetConfiguration;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/asBase.impex", CharEncoding.UTF_8);
		importCsv("/adaptivesearch/test/integration/asSimpleSearchProfile.impex", CharEncoding.UTF_8);
		importCsv("/adaptivesearch/test/integration/asSimpleSearchConfiguration.impex", CharEncoding.UTF_8);
		importCsv("/adaptivesearch/test/integration/asFacets.impex", CharEncoding.UTF_8);

		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);
		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		final Optional<AbstractAsSearchProfileModel> searchProfileOptional = asSearchProfileService
				.getSearchProfileForCode(stagedCatalogVersion, SEARCH_PROFILE_CODE);
		searchProfile = searchProfileOptional.get();

		final Optional<AbstractAsConfigurableSearchConfigurationModel> searchConfigurationOptional = asSearchConfigurationService
				.getSearchConfigurationForUid(stagedCatalogVersion, SEARCH_CONFIGURATION_UID);
		searchConfiguration = searchConfigurationOptional.get();

		final Optional<AbstractAsFacetConfigurationModel> facetConfigurationOptional = asConfigurationService
				.getConfigurationForUid(AbstractAsFacetConfigurationModel.class, stagedCatalogVersion, FACET_CONFIGURATION_UID);
		facetConfiguration = facetConfigurationOptional.get();
	}

	@Test
	public void promotedFacetValueNotFoundBeforeSynchronization() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(stagedCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(facetConfiguration);
		promotedValue.setValue(VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedValue);

		final Optional<AsPromotedFacetValueModel> synchronizedPromotedValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(synchronizedPromotedValueOptional.isPresent());
	}

	@Test
	public void synchronizeNewPromotedFacetValue() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(stagedCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(facetConfiguration);
		promotedValue.setValue(VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedValue);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsPromotedFacetValueModel> synchronizedPromotedValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(synchronizedPromotedValueOptional.isPresent());

		final AsPromotedFacetValueModel synchronizedPromotedValue = synchronizedPromotedValueOptional.get();
		assertSynchronized(promotedValue, synchronizedPromotedValue, AbstractAsSortConfigurationModel.UNIQUEIDX);
	}

	@Test
	public void synchronizeUpdatedPromotedFacetValue() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(stagedCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(facetConfiguration);
		promotedValue.setValue(VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedValue);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		promotedValue.setValue(VALUE2);

		asConfigurationService.saveConfiguration(promotedValue);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsPromotedFacetValueModel> synchronizedPromotedValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(synchronizedPromotedValueOptional.isPresent());

		final AsPromotedFacetValueModel synchronizedPromotedValue = synchronizedPromotedValueOptional.get();
		assertSynchronized(promotedValue, synchronizedPromotedValue, AbstractAsSortConfigurationModel.UNIQUEIDX);
	}

	@Test
	public void synchronizeRemovedPromotedFacetValue() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(stagedCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(facetConfiguration);
		promotedValue.setValue(VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedValue);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		asConfigurationService.removeConfiguration(promotedValue);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsPromotedFacetValueModel> synchronizedPromotedValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(synchronizedPromotedValueOptional.isPresent());
	}
}
