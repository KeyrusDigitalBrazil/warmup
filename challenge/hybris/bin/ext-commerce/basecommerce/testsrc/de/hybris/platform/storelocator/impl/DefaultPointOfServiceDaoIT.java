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
package de.hybris.platform.storelocator.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.daos.impl.DefaultCountryDao;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.daos.impl.DefaultBaseStoreDao;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultPointOfServiceDaoIT extends ServicelayerTest
{
	private static final int SELECT_ALL_ITEMS = 0;
	private static final int REQUESTED_ITEMS_COUNT = 2;
	private static final int POS_COUNT = 5;
	private static final int POS_COUNT_REGION_BY = 3;
	private static final String COUNTRY_DE = "DE";
	private static final String REGION_BY = "DE-BY";
	private static final String COUNTRY_CA = "CA";
	private static final String REGION_QC = "CA-QC";

	@Resource
	private DefaultPointOfServiceDao pointOfServiceDao;
	@Resource
	private DefaultCountryDao countryDao;
	@Resource
	private DefaultBaseStoreDao baseStoreDao;
	private BaseStoreModel baseStore;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createTestPosEntries();
		baseStore = baseStoreDao.findBaseStoresByUid("test_store").get(0);
	}

	protected void createTestPosEntries() throws Exception
	{
		importCsv("/import/test/PointOfServiceSampleTestData.csv", "UTF-8");
	}

	@Test
	public void shouldSelectAllItemsForGeocoding() throws Exception
	{
		//when
		final Collection<PointOfServiceModel> posToGeocode = pointOfServiceDao.getPosToGeocode(SELECT_ALL_ITEMS);
		//then
		assertThat(posToGeocode).hasSize(6);
	}

	@Test
	public void shouldSelectRequestedNumberOfItemsForGeocoding() throws Exception
	{
		//when
		final Collection<PointOfServiceModel> posToGeocode = pointOfServiceDao.getPosToGeocode(REQUESTED_ITEMS_COUNT);
		//then
		assertThat(posToGeocode).hasSize(REQUESTED_ITEMS_COUNT);
	}

	@Test
	public void shouldHaveEmptyGeocodingTimestampIfQualifiesForGeocoding() throws Exception
	{
		//when
		final Collection<PointOfServiceModel> posToGeocode = pointOfServiceDao.getPosToGeocode(SELECT_ALL_ITEMS);
		//then
		final Long numberOfPosWithGeocodingTimestamp = posToGeocode.stream().map(PointOfServiceModel::getGeocodeTimestamp)
				.filter(Objects::nonNull).count();
		assertThat(numberOfPosWithGeocodingTimestamp).isZero();
	}

	@Test
	public void shouldCountNumberOfPosPerCountry()
	{
		//when
		final Map<CountryModel, Integer> storeCountPerCountry = pointOfServiceDao.getPointOfServiceCountPerCountryForStore(baseStore);

		//then
		assertThat(storeCountPerCountry).hasSize(1);
		final Optional<CountryModel> key = storeCountPerCountry.keySet().stream()
				.filter(countryModel -> countryModel.getName().equalsIgnoreCase("Germany")).findFirst();
		assertThat(storeCountPerCountry.get(key.orElse(null)).compareTo(POS_COUNT)).isEqualTo(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldCountNumberOfPosPerCountryNullStore()
	{
		//when
		pointOfServiceDao.getPointOfServiceCountPerCountryForStore(null);
	}

	@Test
	public void shouldCountPosForCountryInDifferentBaseStores()
	{
		//when
		final Map<CountryModel, Integer> baseStore1countryCountMap = pointOfServiceDao.getPointOfServiceCountPerCountryForStore(baseStore);

		//then
		assertThat(baseStore1countryCountMap).hasSize(1);
		final Optional<CountryModel> germany = baseStore1countryCountMap.keySet().stream()
				.filter(countryModel -> countryModel.getName().equalsIgnoreCase("Germany")).findFirst();
		assertThat(baseStore1countryCountMap.get(germany.orElse(null)).compareTo(POS_COUNT)).isEqualTo(0);

		//given Dao is called with a different BaseStore
		baseStore = baseStoreDao.findBaseStoresByUid("test_store2").get(0);
		//when
		final Map<CountryModel, Integer> baseStore2countryCountMap = pointOfServiceDao.getPointOfServiceCountPerCountryForStore(baseStore);
		//then
		assertThat(baseStore2countryCountMap).hasSize(1);

		final Optional<CountryModel> canada = baseStore2countryCountMap.keySet().stream()
				.filter(countryModel -> countryModel.getName().equalsIgnoreCase("Canada")).findFirst();
		assertThat(baseStore2countryCountMap.get(canada.orElse(null)).compareTo(1)).isEqualTo(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExWhenCountryIsNullAndStoreIsNull() throws Exception
	{
		//when
		pointOfServiceDao.getPointOfServiceRegionCountForACountryAndStore(null, null);
	}

	@Test
	public void shouldCountPosPerRegion() throws Exception
	{
		//given
		final List<CountryModel> countries = countryDao.findCountriesByCode("DE");
		//when
		final Map<RegionModel, Integer> storeCountPerRegion = pointOfServiceDao
				.getPointOfServiceRegionCountForACountryAndStore(countries.stream().findAny().get(), baseStore);
		//then
		assertThat(storeCountPerRegion).hasSize(2);
		assertEquals(1,
				storeCountPerRegion.keySet().stream().filter(regionModel -> regionModel.getIsocode().equalsIgnoreCase("DE-BW"))
						.count());
		assertEquals(1,
				storeCountPerRegion.keySet().stream().filter(regionModel -> regionModel.getIsocode().equalsIgnoreCase("DE-BY"))
						.count());

		final RegionModel dEBW = storeCountPerRegion.keySet().stream()
				.filter(regionModel -> regionModel.getIsocode().equalsIgnoreCase("DE-BW")).findAny().orElse(null);
		final RegionModel dEBY = storeCountPerRegion.keySet().stream()
				.filter(regionModel -> regionModel.getIsocode().equalsIgnoreCase("DE-BY")).findAny().orElse(null);

		assertThat(storeCountPerRegion.get(dEBY).compareTo(3)).isEqualTo(0);
		assertThat(storeCountPerRegion.get(dEBW).compareTo(2)).isEqualTo(0);
	}

	@Test
	public void shouldCountPosPerRegionNoResults() throws Exception
	{
		//given
		final List<CountryModel> countries = countryDao.findCountriesByCode("US");
		//when
		final Map<RegionModel, Integer> storeCountPerRegion = pointOfServiceDao
				.getPointOfServiceRegionCountForACountryAndStore(countries.get(0), baseStore);
		//then
		assertThat(storeCountPerRegion).hasSize(0);
	}

	@Test
	public void retrievePosPerCountrySuccess()
	{
		//when
		final List<PointOfServiceModel> result = pointOfServiceDao.getPosForCountry(COUNTRY_DE, baseStore);

		//then
		assertThat(result.size() == POS_COUNT).isTrue();
		assertThat(result.stream().allMatch(pos -> COUNTRY_DE.equals(pos.getAddress().getCountry().getIsocode()))).isTrue();
	}

	@Test
	public void retrievePosPerCountryNoResults()
	{
		//when
		final List<PointOfServiceModel> result = pointOfServiceDao.getPosForCountry(COUNTRY_CA, baseStore);

		//then
		assertThat(result.size() == 0).isTrue();
	}

	@Test
	public void retrievePosPerRegionSuccess()
	{
		//when
		final List<PointOfServiceModel> result = pointOfServiceDao.getPosForRegion(COUNTRY_DE, REGION_BY, baseStore);

		//then
		assertThat(result.size() == POS_COUNT_REGION_BY).isTrue();
		assertThat(result.stream().allMatch(pos -> REGION_BY.equals(pos.getAddress().getRegion().getIsocode()))).isTrue();
	}

	@Test
	public void retrievePosPerRegionNoResults()
	{
		//when
		final List<PointOfServiceModel> result = pointOfServiceDao.getPosForRegion(COUNTRY_CA, REGION_QC, baseStore);

		//then
		assertThat(result.size() == 0).isTrue();
	}

	@Test(expected = IllegalArgumentException.class)
	public void retrievePosPerCountryNullCheck()
	{
		pointOfServiceDao.getPosForCountry(null, baseStore);
	}

	@Test(expected = IllegalArgumentException.class)
	public void retrievePosPerRegionNullCountry()
	{
		pointOfServiceDao.getPosForRegion(null, REGION_BY, baseStore);
	}

	@Test(expected = IllegalArgumentException.class)
	public void retrievePosPerRegionNullRegion()
	{
		pointOfServiceDao.getPosForRegion(COUNTRY_DE, null, baseStore);
	}
}
