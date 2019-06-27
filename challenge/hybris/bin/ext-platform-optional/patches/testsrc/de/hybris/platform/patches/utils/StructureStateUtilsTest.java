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
package de.hybris.platform.patches.utils;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.patches.organisation.ImportLanguage;
import de.hybris.platform.patches.organisation.ImportOrganisationUnit;
import de.hybris.platform.patches.organisation.StructureState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * Test for {@link StructureStateUtils}.
 */
@UnitTest
public class StructureStateUtilsTest
{
	static
	{
		// thanks to that there is guarantee that constructor of ShopOrganisation is used and these objects are set as
		// parents for CountryOrganisations.
		CountryTestOrganisation.values();
		ShopTestOrganisation.values();
	}

	//
	// getNewGlobalLanguages tests
	//

	@Test
	public void getNewGlobalLanguages_withOneCountry()
	{
		final CountryTestOrganisation[] input =
		{ CountryTestOrganisation.TEST_COUNTRY_1 };
		final Set<ImportLanguage> output = createSet(TestLanguage.TEST_LANGUAGE_1);
		final String error_msg = "There should be one language ( {%s} ) returned for given test country";

		assertThat(StructureStateUtils.getNewGlobalLanguages(input, TestStructureState.V1))
				.as(error_msg, TestLanguage.TEST_LANGUAGE_1).isEqualTo(output);
	}

	@Test
	public void getNewGlobalLanguages_withFutureCountry()
	{
		final CountryTestOrganisation[] input =
		{ CountryTestOrganisation.TEST_COUNTRY_4 };
		final Set<ImportLanguage> output = new HashSet<>();
		final String error_msg = "There should be no languages returned because passed country is from future comparing to used version";

		assertThat(StructureStateUtils.getNewGlobalLanguages(input, TestStructureState.V1)).as(error_msg).isEqualTo(output);
	}

	@Test
	public void getNewGlobalLanguages_withMixedCountriesV1()
	{
		final CountryTestOrganisation[] input =
		{ CountryTestOrganisation.TEST_COUNTRY_2, CountryTestOrganisation.TEST_COUNTRY_4 };
		final Set<ImportLanguage> output = createSet(TestLanguage.TEST_LANGUAGE_2);
		final String error_msg = "There should be one language %s returned because only country 2 is from given version";

		assertThat(StructureStateUtils.getNewGlobalLanguages(input, TestStructureState.V1)).as(error_msg, output).isEqualTo(output);
	}

	@Test
	public void getNewGlobalLanguages_withMixedCountriesV3()
	{
		final CountryTestOrganisation[] input =
		{ CountryTestOrganisation.TEST_COUNTRY_2, CountryTestOrganisation.TEST_COUNTRY_4 };
		final Set<ImportLanguage> output = createSet(TestLanguage.TEST_LANGUAGE_4);
		final String error_msg = "There should be one language %s returned because only country 4 is from given version";

		assertThat(StructureStateUtils.getNewGlobalLanguages(input, TestStructureState.V3)).as(error_msg, output).isEqualTo(output);
	}

	@Test
	public void getNewGlobalLanguages_withShopPartiallyInProperVersion()
	{
		final ShopTestOrganisation[] input =
		{ ShopTestOrganisation.TEST_SHOP_2 };
		final Set<ImportLanguage> output = createSet(TestLanguage.TEST_LANGUAGE_2);
		final String error_msg = "There should be one language %s returned for given test shop because only one country is valid in this version";

		assertThat(StructureStateUtils.getNewGlobalLanguages(input, TestStructureState.V1)).as(error_msg, output).isEqualTo(output);
	}

	@Test
	public void getNewGlobalLanguages_withShopProperVersion()
	{
		final ShopTestOrganisation[] input =
		{ ShopTestOrganisation.TEST_SHOP_2 };
		final Set<ImportLanguage> output = createSet(TestLanguage.TEST_LANGUAGE_4);
		final String error_msg = "There should be one language %s returned for given test shop because only one country is valid in this version";

		assertThat(StructureStateUtils.getNewGlobalLanguages(input, TestStructureState.V3)).as(error_msg, output).isEqualTo(output);
	}

	//
	// getAllGlobalLanguages tests
	//

	@Test
	public void getAllGlobalLanguages_withCountriesV1()
	{
		final CountryTestOrganisation[] input = CountryTestOrganisation.values();
		final Set<ImportLanguage> output = createSet(TestLanguage.TEST_LANGUAGE_1, TestLanguage.TEST_LANGUAGE_2);
		final String error_msg = "There should be two languages %s returned because only these are defined for countries in version 1";

		assertThat(StructureStateUtils.getNewGlobalLanguages(input, TestStructureState.V1)).as(error_msg, output).isEqualTo(output);
	}

	@Test
	public void getAllGlobalLanguages_withCountriesV2()
	{
		final CountryTestOrganisation[] input = CountryTestOrganisation.values();
		final Set<ImportLanguage> output = createSet(TestLanguage.TEST_LANGUAGE_3, TestLanguage.TEST_LANGUAGE_5,
				TestLanguage.TEST_LANGUAGE_6);
		final String error_msg = "There should be three languages %s returned because only these are defined for countries in version 2. Please notice that language test_5 and test_6 are V2 because of shop that they belong to.";

		assertThat(StructureStateUtils.getNewGlobalLanguages(input, TestStructureState.V2)).as(error_msg, output).isEqualTo(output);
	}

	@Test
	public void getAllGlobalLanguages_withCountriesV3()
	{
		final CountryTestOrganisation[] input = CountryTestOrganisation.values();
		final Set<ImportLanguage> output = createSet(TestLanguage.TEST_LANGUAGE_4);
		final String error_msg = "There should be one language %s returned because only these are defined for countries in version 3. Please notice that language test_2 was already used in context of different country";

		assertThat(StructureStateUtils.getNewGlobalLanguages(input, TestStructureState.V3)).as(error_msg, output).isEqualTo(output);
	}

	@Test
	public void getAllGlobalLanguages_withShops()
	{
		final ShopTestOrganisation[] input = ShopTestOrganisation.values();
		final Set<ImportLanguage> output = createSet(TestLanguage.TEST_LANGUAGE_4);
		final String error_msg = "There should be one language %s returned because only these are defined for countries in version 3. Please notice that language test_2 was already used in context of different country";

		assertThat(StructureStateUtils.getNewGlobalLanguages(input, TestStructureState.V3)).as(error_msg, output).isEqualTo(output);
	}

	//
	// getVersionGap tests
	//
	@Test
	public void getVersionGap_V2_V1()
	{
		final Collection<StructureState> output = createList(TestStructureState.V2);
		final String error_msg = "Output should be: %s";

		assertThat(StructureStateUtils.getStructureStateGap(TestStructureState.V2, TestStructureState.V1)).as(error_msg, output)
				.isEqualTo(output);
	}

	@Test
	public void getVersionGap_V3_V1()
	{
		final Collection<StructureState> output = createList(TestStructureState.V2, TestStructureState.V3);
		final String error_msg = "Output should be: %s";

		assertThat(StructureStateUtils.getStructureStateGap(TestStructureState.V3, TestStructureState.V1)).as(error_msg, output)
				.isEqualTo(output);
	}

	@Test
	public void getVersionGap_V1_V2()
	{
		final String error_msg = "Output should be empty because version V2 is after V1";

		assertThat(StructureStateUtils.getStructureStateGap(TestStructureState.V1, TestStructureState.V2)).as(error_msg)
				.isEqualTo(Collections.EMPTY_LIST);
	}

	/**
	 * Test Shop Organisation Unit
	 */
	private enum ShopTestOrganisation implements ImportOrganisationUnit<CountryTestOrganisation, ShopTestOrganisation>
	{
		// @formatter:off
		TEST_SHOP_1("TS_1", "Test Shop 1", new CountryTestOrganisation[]
		{ CountryTestOrganisation.TEST_COUNTRY_1, CountryTestOrganisation.TEST_COUNTRY_3 },
				TestStructureState.V1), TEST_SHOP_2("TS_2", "Test Shop 2", new CountryTestOrganisation[]
		{ CountryTestOrganisation.TEST_COUNTRY_2, CountryTestOrganisation.TEST_COUNTRY_4 },
						TestStructureState.V1), TEST_SHOP_3("TS_3", "Test Shop 3", new CountryTestOrganisation[]
		{ CountryTestOrganisation.TEST_COUNTRY_5 }, TestStructureState.V2);
		// @formatter:on

		private static final String FOLDER_NAME = "shops";
		private static final String COMMON_FOLDER_NAME = "_commonShops";

		private final String code;
		private final String name;
		private final Collection<CountryTestOrganisation> children;
		private final Collection<ImportLanguage> languages;
		private final StructureState version;

		ShopTestOrganisation(final String code, final String name, final CountryTestOrganisation[] children,
				final StructureState version)
		{
			this.code = code;
			this.name = name;
			this.children = Arrays.asList(children);
			this.version = version;
			final Collection<ImportLanguage> childLanguages = new LinkedHashSet<>();
			for (final CountryTestOrganisation child : children)
			{
				child.setParent(this);
				childLanguages.addAll(child.getLanguages());
			}
			this.languages = childLanguages;
		}

		@Override
		public String getCode()
		{
			return this.code;
		}

		@Override
		public String getName()
		{
			return this.name;
		}

		@Override
		public String getFolderName()
		{
			return FOLDER_NAME;
		}

		@Override
		public String getCommonFolderName()
		{
			return COMMON_FOLDER_NAME;
		}

		@Override
		public Collection<CountryTestOrganisation> getChildren()
		{
			return this.children;
		}

		@Override
		public Collection<ImportLanguage> getLanguages()
		{
			return this.languages;
		}

		@Override
		public ShopTestOrganisation getParent()
		{
			return null;
		}

		@Override
		public void setParent(final ShopTestOrganisation parent)
		{
			throw new UnsupportedOperationException("Parent can't be set for ShopTestOrganisation");
		}

		@Override
		public StructureState getStructureState()
		{
			return this.version;
		}
	}

	/**
	 * Test Organisation unit that is child of ShopTestOrganisationUnit
	 */
	private enum CountryTestOrganisation implements ImportOrganisationUnit<CountryTestOrganisation, ShopTestOrganisation>
	{
		// @formatter:off
		TEST_COUNTRY_1("TC_1", "Test Country 1", new ImportLanguage[]
		{ TestLanguage.TEST_LANGUAGE_1 }), TEST_COUNTRY_2("TC_2", "Test Country 2", new ImportLanguage[]
		{ TestLanguage.TEST_LANGUAGE_2 }, TestStructureState.V1), TEST_COUNTRY_3("TC_3", "Test Country 3", new ImportLanguage[]
		{ TestLanguage.TEST_LANGUAGE_3 }, TestStructureState.V2), TEST_COUNTRY_4("TC_4", "Test Country 4", new ImportLanguage[]
		{ TestLanguage.TEST_LANGUAGE_2, TestLanguage.TEST_LANGUAGE_4 }, TestStructureState.V3), TEST_COUNTRY_5("TC_5",
				"Test Country 5", new ImportLanguage[]
				{ TestLanguage.TEST_LANGUAGE_5, TestLanguage.TEST_LANGUAGE_6 });
		// @formatter:on

		private static final Logger LOG = Logger.getLogger(CountryTestOrganisation.class.getName());

		static
		{
			// thanks to that there is guarantee that constructor of ShopTestOrganisation is used and these objects are
			// set as parents for CountryTestOrganisations.
			CountryTestOrganisation.values();
			ShopTestOrganisation.values();
		}

		private static final String COMMON_FOLDER_NAME = "_commonCountries";
		private static final String FOLDER_NAME = "countries";
		private final String code;
		private final String name;
		private final Collection<ImportLanguage> languages;
		private ShopTestOrganisation parent;
		private StructureState version;

		/**
		 * Constructor without version - version from parent organization will be taken
		 *
		 * @param code
		 * @param name
		 * @param languages
		 */
		CountryTestOrganisation(final String code, final String name, final ImportLanguage[] languages)
		{
			this.code = code;
			this.name = name;
			this.languages = Arrays.asList(languages);
		}

		CountryTestOrganisation(final String code, final String name, final ImportLanguage[] languages,
				final StructureState version)
		{
			this.code = code;
			this.name = name;
			this.languages = Arrays.asList(languages);
			this.version = version;
		}

		@Override
		public void setParent(final ShopTestOrganisation parent)
		{
			if (this.parent != null)
			{
				throw new IllegalArgumentException("Try to set parent for CountryTestOrganisation second time");
			}
			this.parent = parent;
			final StructureState parentVersion = parent.getStructureState();
			if (this.version == null)
			{
				this.version = parentVersion;
			}
			if (parentVersion.isAfter(this.version))
			{
				this.version = parentVersion;
				LOG.warn("CountryOrganization with name " + this.getName()
						+ " have version set that is before store where this country belongs too");
			}
		}

		@Override
		public ShopTestOrganisation getParent()
		{
			return this.parent;
		}

		@Override
		public String getCode()
		{
			return this.code;
		}

		@Override
		public String getName()
		{
			return this.name;
		}

		@Override
		public String getFolderName()
		{
			return CountryTestOrganisation.FOLDER_NAME;
		}

		@Override
		public String getCommonFolderName()
		{
			return CountryTestOrganisation.COMMON_FOLDER_NAME;
		}

		@Override
		public Collection<CountryTestOrganisation> getChildren()
		{
			return null;
		}

		@Override
		public Collection<ImportLanguage> getLanguages()
		{
			return this.languages;
		}

		@Override
		public StructureState getStructureState()
		{
			return this.version;
		}
	}

	public enum TestLanguage implements ImportLanguage
	{
		// @formatter:off
		TEST_LANGUAGE_1("test_1"), TEST_LANGUAGE_2("test_2"), TEST_LANGUAGE_3("test_3"), TEST_LANGUAGE_4("test_4"), TEST_LANGUAGE_5(
				"test_5"), TEST_LANGUAGE_6("test_6");
		// @formatter:on
		private final String code;

		TestLanguage(final String code)
		{
			this.code = code;
		}

		@Override
		public String getCode()
		{
			return this.code;
		}

		@Override
		public String toString()
		{
			return this.code;
		}
	}

	public enum TestStructureState implements StructureState
	{
		V1, V2, V3, V4, LAST;

		@Override
		public boolean isAfter(final de.hybris.platform.patches.organisation.StructureState structureState)
		{
			if (this == structureState)
			{
				return false;
			}
			for (final de.hybris.platform.patches.organisation.StructureState iterateValue : values())
			{
				if (structureState.equals(iterateValue))
				{
					return true;
				}
				if (this.equals(iterateValue))
				{
					return false;
				}
			}
			return false;
		}
	}

	/**
	 * Utility method that will create set of ImportLanguages in nice way.
	 *
	 * @param languages
	 * @return set of {@link ImportLanguage}
	 */
	private Set<ImportLanguage> createSet(final TestLanguage... languages)
	{
		return new HashSet<>(Arrays.asList(languages));
	}

	/**
	 * Utility method that will create list of StructureStates in nice way.
	 *
	 * @param structureStates
	 * @return list of {@link StructureState}
	 */
	private List<StructureState> createList(final StructureState... structureStates)
	{
		return new ArrayList<>(Arrays.asList(structureStates));
	}
}
