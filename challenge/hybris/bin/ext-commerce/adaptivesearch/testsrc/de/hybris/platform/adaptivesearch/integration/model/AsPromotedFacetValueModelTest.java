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
import de.hybris.platform.adaptivesearch.model.AsPromotedFacetValueModel;
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
public class AsPromotedFacetValueModelTest extends ServicelayerTransactionalTest
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
	public void getNonExistingPromotedFacetValue() throws Exception
	{
		// when
		final Optional<AsPromotedFacetValueModel> promotedValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(promotedValueOptional.isPresent());
	}

	@Test
	public void createPromotedFacetValueWithoutUid() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setFacetConfiguration(facetConfiguration);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedValue);

		// then
		assertNotNull(promotedValue.getUid());
		assertFalse(promotedValue.getUid().isEmpty());
	}

	@Test
	public void createPromotedFacetValue() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(facetConfiguration);
		promotedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedValue);

		final Optional<AsPromotedFacetValueModel> createdPromotedValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(createdPromotedValueOptional.isPresent());

		final AsPromotedFacetValueModel createdPromotedValue = createdPromotedValueOptional.get();
		assertEquals(onlineCatalogVersion, createdPromotedValue.getCatalogVersion());
		assertEquals(UID1, createdPromotedValue.getUid());
		assertEquals(facetConfiguration, createdPromotedValue.getFacetConfiguration());
		assertEquals(FACET_VALUE1, createdPromotedValue.getValue());
	}

	@Test
	public void failToCreatePromotedFacetValueWithWrongCatalogVersion() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(stagedCatalogVersion);
		promotedValue.setFacetConfiguration(facetConfiguration);
		promotedValue.setValue(FACET_VALUE1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedValue);
	}

	@Test
	public void failToCreatePromotedFacetValueWithoutFacetConfiguration() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setValue(FACET_VALUE1);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedValue);
	}

	@Test
	public void failToCreatePromotedFacetValueWithoutValue() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(stagedCatalogVersion);
		promotedValue.setFacetConfiguration(facetConfiguration);

		// expect
		expectedException.expect(ModelSavingException.class);

		// when
		asConfigurationService.saveConfiguration(promotedValue);
	}

	@Test
	public void createMultiplePromotedFacetValues() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue1 = asConfigurationService
				.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue1.setCatalogVersion(onlineCatalogVersion);
		promotedValue1.setUid(UID1);
		promotedValue1.setFacetConfiguration(facetConfiguration);
		promotedValue1.setValue(FACET_VALUE1);

		final AsPromotedFacetValueModel promotedValue2 = asConfigurationService
				.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue2.setCatalogVersion(onlineCatalogVersion);
		promotedValue2.setUid(UID2);
		promotedValue2.setFacetConfiguration(facetConfiguration);
		promotedValue2.setValue(FACET_VALUE2);

		// when
		asConfigurationService.saveConfiguration(promotedValue1);
		asConfigurationService.saveConfiguration(promotedValue2);

		modelService.refresh(facetConfiguration);

		// then
		assertThat(facetConfiguration.getPromotedValues()).containsExactly(promotedValue1, promotedValue2);
	}

	@Test
	public void updatePromotedFacetValue() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(facetConfiguration);
		promotedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedValue);

		final Optional<AsPromotedFacetValueModel> createdPromotedValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);

		final AsPromotedFacetValueModel createdPromotedValue = createdPromotedValueOptional.get();
		createdPromotedValue.setValue(FACET_VALUE2);
		asConfigurationService.saveConfiguration(createdPromotedValue);

		final Optional<AsPromotedFacetValueModel> updatedPromotedValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertTrue(updatedPromotedValueOptional.isPresent());

		final AsPromotedFacetValueModel updatedPromotedValue = updatedPromotedValueOptional.get();
		assertEquals(onlineCatalogVersion, updatedPromotedValue.getCatalogVersion());
		assertEquals(UID1, updatedPromotedValue.getUid());
		assertEquals(facetConfiguration, updatedPromotedValue.getFacetConfiguration());
		assertEquals(FACET_VALUE2, updatedPromotedValue.getValue());
	}

	@Test
	public void clonePromotedFacetValue() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(facetConfiguration);
		promotedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedValue);

		final AsPromotedFacetValueModel clonedFacetValue = asConfigurationService.cloneConfiguration(promotedValue);
		clonedFacetValue.setValue(FACET_VALUE2);
		asConfigurationService.saveConfiguration(clonedFacetValue);

		// then
		assertEquals(promotedValue.getCatalogVersion(), clonedFacetValue.getCatalogVersion());
		assertNotEquals(promotedValue.getUid(), clonedFacetValue.getUid());
		assertEquals(promotedValue.getFacetConfiguration(), clonedFacetValue.getFacetConfiguration());
	}

	@Test
	public void removePromotedFacetValue() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setCatalogVersion(onlineCatalogVersion);
		promotedValue.setUid(UID1);
		promotedValue.setFacetConfiguration(facetConfiguration);
		promotedValue.setValue(FACET_VALUE1);

		// when
		asConfigurationService.saveConfiguration(promotedValue);

		final Optional<AsPromotedFacetValueModel> createdPromotedFacetValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);

		final AsPromotedFacetValueModel createdPromotedValue = createdPromotedFacetValueOptional.get();
		asConfigurationService.removeConfiguration(createdPromotedValue);

		final Optional<AsPromotedFacetValueModel> removedPromotedFacetValueOptional = asConfigurationService
				.getConfigurationForUid(AsPromotedFacetValueModel.class, onlineCatalogVersion, UID1);

		// then
		assertFalse(removedPromotedFacetValueOptional.isPresent());
	}

	@Test
	public void promotedFacetValueIsNotCorrupted() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue = asConfigurationService.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue.setFacetConfiguration(facetConfiguration);
		promotedValue.setCatalogVersion(stagedCatalogVersion);
		promotedValue.setValue(FACET_VALUE1);

		// when
		final boolean corrupted = promotedValue.isCorrupted();

		// then
		assertFalse(corrupted);
	}

	@Test
	public void rankAfterPromotedFacetValue() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue1 = asConfigurationService
				.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue1.setCatalogVersion(onlineCatalogVersion);
		promotedValue1.setUid(UID1);
		promotedValue1.setFacetConfiguration(facetConfiguration);
		promotedValue1.setValue(FACET_VALUE1);

		final AsPromotedFacetValueModel promotedValue2 = asConfigurationService
				.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue2.setCatalogVersion(onlineCatalogVersion);
		promotedValue2.setUid(UID2);
		promotedValue2.setFacetConfiguration(facetConfiguration);
		promotedValue2.setValue(FACET_VALUE2);

		// when
		asConfigurationService.saveConfiguration(promotedValue1);
		asConfigurationService.saveConfiguration(promotedValue2);

		modelService.refresh(facetConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankAfterConfiguration(facetConfiguration,
				AbstractAsFacetConfigurationModel.PROMOTEDVALUES, UID2, UID1);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID1, rankChange.getUid());
		assertEquals(Integer.valueOf(0), rankChange.getOldRank());
		assertEquals(Integer.valueOf(1), rankChange.getNewRank());

		assertThat(facetConfiguration.getPromotedValues()).containsExactly(promotedValue2, promotedValue1);
	}

	@Test
	public void rankBeforePromotedFacetValue() throws Exception
	{
		// given
		final AsPromotedFacetValueModel promotedValue1 = asConfigurationService
				.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue1.setCatalogVersion(onlineCatalogVersion);
		promotedValue1.setUid(UID1);
		promotedValue1.setFacetConfiguration(facetConfiguration);
		promotedValue1.setValue(FACET_VALUE1);

		final AsPromotedFacetValueModel promotedValue2 = asConfigurationService
				.createConfiguration(AsPromotedFacetValueModel.class);
		promotedValue2.setCatalogVersion(onlineCatalogVersion);
		promotedValue2.setUid(UID2);
		promotedValue2.setFacetConfiguration(facetConfiguration);
		promotedValue2.setValue(FACET_VALUE2);

		// when
		asConfigurationService.saveConfiguration(promotedValue1);
		asConfigurationService.saveConfiguration(promotedValue2);

		modelService.refresh(facetConfiguration);

		final List<AsRankChange> rankChanges = asConfigurationService.rankBeforeConfiguration(facetConfiguration,
				AbstractAsFacetConfigurationModel.PROMOTEDVALUES, UID1, UID2);

		// then
		assertThat(rankChanges).hasSize(1);

		final AsRankChange rankChange = rankChanges.get(0);
		assertEquals(AsRankChangeType.MOVE, rankChange.getType());
		assertEquals(UID2, rankChange.getUid());
		assertEquals(Integer.valueOf(1), rankChange.getOldRank());
		assertEquals(Integer.valueOf(0), rankChange.getNewRank());

		assertThat(facetConfiguration.getPromotedValues()).containsExactly(promotedValue2, promotedValue1);
	}
}
