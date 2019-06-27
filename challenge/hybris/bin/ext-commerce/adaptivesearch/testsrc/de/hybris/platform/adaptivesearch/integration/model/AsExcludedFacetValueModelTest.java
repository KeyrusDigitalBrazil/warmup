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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.data.AsRankChange;
import de.hybris.platform.adaptivesearch.data.AsRankChangeType;
import de.hybris.platform.adaptivesearch.model.AbstractAsFacetConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AsExcludedFacetValueModel;
import de.hybris.platform.adaptivesearch.services.AsConfigurationService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class AsExcludedFacetValueModelTest extends ServicelayerTransactionalTest
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
	private CatalogVersionModel stagedCatalogVersion;
	private AbstractAsFacetConfigurationModel facetConfiguration;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/adaptivesearch/test/integration/model/asFacetValueConfigurationModelTest.impex", CharEncoding.UTF_8);

		onlineCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);
		stagedCatalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_STAGED);

		final Optional<AbstractAsFacetConfigurationModel> facetConfigurationOptional = asConfigurationService
				.getConfigurationForUid(AbstractAsFacetConfigurationModel.class, onlineCatalogVersion, FACET_CONFIGURATION_ID);
		facetConfiguration = facetConfigurationOptional.get();
	}

	@Test
	public void getNonExistingExcludedFacetValue() throws Exception
	{
		// when
		final Optional<AsExcludedFacetValueModel> excludedValueOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(excludedValueOptional.isPresent());
	}

	@Test
	public void createExcludedFacetValueWithoutUid() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setFacetConfiguration(facetConfiguration);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(excludedValue);

		// then
		assertNotNull(excludedValue.getUid());
		assertFalse(excludedValue.getUid().isEmpty());
	}

	@Test
	public void createExcludedFacetValue() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setUid(UID1);
		excludedValue.setFacetConfiguration(facetConfiguration);
		excludedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(excludedValue);

		final Optional<AsExcludedFacetValueModel> createdExcludedValueOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(createdExcludedValueOptional.isPresent());

		final AsExcludedFacetValueModel createdExcludedFacetValue = createdExcludedValueOptional.get();
		assertEquals(onlineCatalogVersion, createdExcludedFacetValue.getCatalogVersion());
		assertEquals(UID1, createdExcludedFacetValue.getUid());
		assertEquals(facetConfiguration, createdExcludedFacetValue.getFacetConfiguration());
		assertEquals(FACET_VALUE1, createdExcludedFacetValue.getValue());
	}

	@Test
	public void failToCreateExcludedFacetValueWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(stagedCatalogVersion);
		excludedValue.setFacetConfiguration(facetConfiguration);
		excludedValue.setValue(FACET_VALUE1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedValue);
	}

	@Test
	public void failToCreateExcludedFacetValueWithoutFacetConfiguration() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(stagedCatalogVersion);
		excludedValue.setFacetConfiguration(facetConfiguration);
		excludedValue.setValue(FACET_VALUE1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedValue);
	}

	@Test
	public void failToCreateExcludedFacetValueWithoutValue() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(stagedCatalogVersion);
		excludedValue.setFacetConfiguration(facetConfiguration);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(excludedValue);
	}

	@Test
	public void createMultipleExcludedFacetValues() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue1 = asConfigurationService
				.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue1.setCatalogVersion(onlineCatalogVersion);
		excludedValue1.setUid(UID1);
		excludedValue1.setFacetConfiguration(facetConfiguration);
		excludedValue1.setValue(FACET_VALUE1);

		final AsExcludedFacetValueModel excludedValue2 = asConfigurationService
				.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue2.setCatalogVersion(onlineCatalogVersion);
		excludedValue2.setUid(UID2);
		excludedValue2.setFacetConfiguration(facetConfiguration);
		excludedValue2.setValue(FACET_VALUE2);

		// when
		asConfigurationService.saveConfiguration(excludedValue1);
		asConfigurationService.saveConfiguration(excludedValue2);

		modelService.refresh(facetConfiguration);

		// then
		assertThat(facetConfiguration.getExcludedValues()).containsExactly(excludedValue1, excludedValue2);
	}

	@Test
	public void updateExcludedFacetValue() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setUid(UID1);
		excludedValue.setFacetConfiguration(facetConfiguration);
		excludedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(excludedValue);

		final Optional<AsExcludedFacetValueModel> createdExcludedValueOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetValueModel.class, onlineCatalogVersion, UID1);

		final AsExcludedFacetValueModel createdExcludedValue = createdExcludedValueOptional.get();
		createdExcludedValue.setValue(FACET_VALUE2);
		asConfigurationService.saveConfiguration(createdExcludedValue);

		final Optional<AsExcludedFacetValueModel> updatedExcludedValueOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(updatedExcludedValueOptional.isPresent());

		final AsExcludedFacetValueModel updatedExcludedValue = updatedExcludedValueOptional.get();
		assertEquals(onlineCatalogVersion, updatedExcludedValue.getCatalogVersion());
		assertEquals(UID1, updatedExcludedValue.getUid());
		assertEquals(facetConfiguration, updatedExcludedValue.getFacetConfiguration());
		assertEquals(FACET_VALUE2, updatedExcludedValue.getValue());
	}

	@Test
	public void cloneExcludedFacetValue() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setUid(UID1);
		excludedValue.setFacetConfiguration(facetConfiguration);
		excludedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(excludedValue);

		final AsExcludedFacetValueModel clonedExcludedValue = asConfigurationService.cloneConfiguration(excludedValue);
		clonedExcludedValue.setValue(FACET_VALUE2);
		asConfigurationService.saveConfiguration(clonedExcludedValue);

		// then
		assertEquals(excludedValue.getCatalogVersion(), clonedExcludedValue.getCatalogVersion());
		assertNotEquals(excludedValue.getUid(), clonedExcludedValue.getUid());
		assertEquals(excludedValue.getFacetConfiguration(), clonedExcludedValue.getFacetConfiguration());
	}

	@Test
	public void removeExcludedFacetValue() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setUid(UID1);
		excludedValue.setFacetConfiguration(facetConfiguration);
		excludedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(excludedValue);

		final Optional<AsExcludedFacetValueModel> createdExcludedFacetValueOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetValueModel.class, onlineCatalogVersion, UID1);

		final AsExcludedFacetValueModel createdExcludedValue = createdExcludedFacetValueOptional.get();
		asConfigurationService.removeConfiguration(createdExcludedValue);

		final Optional<AsExcludedFacetValueModel> removedExcludedFacetValueOptional = asConfigurationService
				.getConfigurationForUid(AsExcludedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(removedExcludedFacetValueOptional.isPresent());
	}

	@Test
	public void excludedFacetValueIsNotCorrupted() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue = asConfigurationService.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue.setCatalogVersion(onlineCatalogVersion);
		excludedValue.setUid(UID1);
		excludedValue.setFacetConfiguration(facetConfiguration);
		excludedValue.setValue(FACET_VALUE1);

		// when
		final boolean corrupted = excludedValue.isCorrupted();

		// then
		assertFalse(corrupted);
	}

	@Test
	public void rankAfterExcludedFacetValue() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue1 = asConfigurationService
				.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue1.setCatalogVersion(onlineCatalogVersion);
		excludedValue1.setUid(UID1);
		excludedValue1.setFacetConfiguration(facetConfiguration);
		excludedValue1.setValue(FACET_VALUE1);

		final AsExcludedFacetValueModel excludedValue2 = asConfigurationService
				.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue2.setCatalogVersion(onlineCatalogVersion);
		excludedValue2.setUid(UID2);
		excludedValue2.setFacetConfiguration(facetConfiguration);
		excludedValue2.setValue(FACET_VALUE2);

		// when
		asConfigurationService.saveConfiguration(excludedValue1);
		asConfigurationService.saveConfiguration(excludedValue2);

		modelService.refresh(facetConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(facetConfiguration,
				AbstractAsFacetConfigurationModel.EXCLUDEDVALUES, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(facetConfiguration.getExcludedValues()).containsExactly(excludedValue2, excludedValue1);
	}

	@Test
	public void rankBeforeExcludedFacetValue() throws Exception
	{
		// given
		final AsExcludedFacetValueModel excludedValue1 = asConfigurationService
				.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue1.setCatalogVersion(onlineCatalogVersion);
		excludedValue1.setUid(UID1);
		excludedValue1.setFacetConfiguration(facetConfiguration);
		excludedValue1.setValue(FACET_VALUE1);

		final AsExcludedFacetValueModel excludedValue2 = asConfigurationService
				.createConfiguration(AsExcludedFacetValueModel.class);
		excludedValue2.setCatalogVersion(onlineCatalogVersion);
		excludedValue2.setUid(UID2);
		excludedValue2.setFacetConfiguration(facetConfiguration);
		excludedValue2.setValue(FACET_VALUE2);

		// when
		asConfigurationService.saveConfiguration(excludedValue1);
		asConfigurationService.saveConfiguration(excludedValue2);

		modelService.refresh(facetConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(facetConfiguration,
				AbstractAsFacetConfigurationModel.EXCLUDEDVALUES, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(facetConfiguration.getExcludedValues()).containsExactly(excludedValue2, excludedValue1);
	}
}
