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
import de.hybris.platform.adaptivesearch.enums.AsSortOrder;
import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurableSearchConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsSortConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsSortExpressionModel;
import de.hybris.platform.adaptivesearch.model.AsSortModel;
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
public class AsSortSynchronizationTest extends AbstractAsSynchronizationTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_STAGED = "Staged";
	private final static String VERSION_ONLINE = "Online";

	private static final String SEARCH_PROFILE_CODE = "searchProfile";
	private static final String SEARCH_CONFIGURATION_UID = "searchConfiguration";

	private static final String UID1 = "cde588ec-d453-48bd-a3b1-b9aa00402256";
	private static final String UID2 = "2192804d-96e8-444f-a87f-9e1cd4e9503a";

	private static final String CODE1 = "code1";
	private static final String NAME1 = "name1";

	private static final String INDEX_PROPERTY1 = "property1";

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
	public void sortNotFoundBeforeSynchronization() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(stagedCatalogVersion);
		sort.setUid(UID1);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE1);
		sort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(sort);

		final Optional<AsSortModel> synchronizedSortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertFalse(synchronizedSortOptional.isPresent());
	}

	@Test
	public void synchronizeNewSort() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(stagedCatalogVersion);
		sort.setUid(UID1);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE1);
		sort.setName(NAME1);

		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(stagedCatalogVersion);
		sortExpression.setUid(UID2);
		sortExpression.setSortConfiguration(sort);
		sortExpression.setExpression(INDEX_PROPERTY1);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// when
		asConfigurationService.saveConfiguration(sort);
		asConfigurationService.saveConfiguration(sortExpression);
		asConfigurationService.refreshConfiguration(sort);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsSortModel> synchronizedSortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertTrue(synchronizedSortOptional.isPresent());

		final AsSortModel synchronizedSort = synchronizedSortOptional.get();
		assertSynchronized(sort, synchronizedSort, AbstractAsSortConfigurationModel.UNIQUEIDX);
	}

	@Test
	public void synchronizeUpdatedSort() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(stagedCatalogVersion);
		sort.setUid(UID1);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE1);
		sort.setName(NAME1);

		final AsSortExpressionModel sortExpression = asConfigurationService.createConfiguration(AsSortExpressionModel.class);
		sortExpression.setCatalogVersion(stagedCatalogVersion);
		sortExpression.setUid(UID2);
		sortExpression.setSortConfiguration(sort);
		sortExpression.setExpression(INDEX_PROPERTY1);
		sortExpression.setOrder(AsSortOrder.ASCENDING);

		// when
		asConfigurationService.saveConfiguration(sort);
		asConfigurationService.saveConfiguration(sortExpression);
		asConfigurationService.refreshConfiguration(sort);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		sort.setPriority(Integer.valueOf(200));
		sort.setExpressions(new ArrayList<>());

		asConfigurationService.saveConfiguration(sort);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsSortModel> synchronizedSortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertTrue(synchronizedSortOptional.isPresent());

		final AsSortModel synchronizedSort = synchronizedSortOptional.get();
		assertSynchronized(sort, synchronizedSort, AbstractAsSortConfigurationModel.UNIQUEIDX);
	}

	@Test
	public void synchronizeRemovedSort() throws Exception
	{
		// given
		final AsSortModel sort = asConfigurationService.createConfiguration(AsSortModel.class);
		sort.setCatalogVersion(stagedCatalogVersion);
		sort.setUid(UID1);
		sort.setSearchConfiguration(searchConfiguration);
		sort.setCode(CODE1);
		sort.setName(NAME1);

		// when
		asConfigurationService.saveConfiguration(sort);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		asConfigurationService.removeConfiguration(sort);
		catalogSynchronizationService.synchronizeFully(stagedCatalogVersion, onlineCatalogVersion);

		final Optional<AsSortModel> synchronizedSortOptional = asConfigurationService.getConfigurationForUid(AsSortModel.class,
				onlineCatalogVersion, UID1);

		// then
		assertFalse(synchronizedSortOptional.isPresent());
	}
}
