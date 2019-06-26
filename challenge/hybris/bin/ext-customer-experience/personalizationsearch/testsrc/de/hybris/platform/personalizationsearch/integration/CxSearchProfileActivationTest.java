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
package de.hybris.platform.personalizationsearch.integration;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContextFactory;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileActivationGroup;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.services.AsSearchProfileActivationService;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileActivationMapping;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileActivationStrategy;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileRegistry;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.customization.CxCustomizationService;
import de.hybris.platform.personalizationservices.model.CxAbstractActionModel;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.personalizationservices.model.CxCustomizationsGroupModel;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.personalizationservices.service.CxService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class CxSearchProfileActivationTest extends ServicelayerTransactionalTest
{
	private static final String CX_USER = "cxuser@hybris.com";

	private static final String SEARCH_PROFILE1_CODE = "searchProfile1";
	private static final String SEARCH_PROFILE2_CODE = "searchProfile2";
	private static final String SEARCH_PROFILE3_CODE = "searchProfile3";
	private static final String SEARCH_PROFILE4_CODE = "searchProfile4";

	private static final String INDEX_CONFIGURATION = "indexConfiguration";
	private static final String INDEX_TYPE = "testIndex";

	private final static String CX_CATALOG_ID = "cxCatalog";
	private final static String SUMMER_CATALOG_ID = "summerCatalog";
	private final static String WINTER_CATALOG_ID = "winterCatalog";

	private final static String VERSION_ONLINE = "Online";

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CxService cxService;

	@Resource
	private CxCustomizationService cxCustomizationService;

	@Resource
	private AsSearchProfileContextFactory asSearchProfileContextFactory;

	@Resource
	private AsSearchProfileActivationService asSearchProfileActivationService;

	@Resource
	private AsSearchProfileActivationStrategy cxSearchProfileActivationStrategy;

	@Resource
	private AsSearchProfileRegistry asSearchProfileRegistry;

	private int mainGroupIndex;

	@Before
	public void setUp() throws Exception
	{
		mainGroupIndex = findMainGroupIndex();

		createCoreData();
		importCsv("/personalizationsearch/test/cxSearchProfileActivationTest.impex", "UTF-8");
	}

	@Test
	public void noActiveSearchProfile() throws Exception
	{
		// when
		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Collections.emptyList());

		// then
		final AsSearchProfileActivationGroup mainGroup = activationGroups.get(mainGroupIndex);
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).isEmpty();
	}

	@Test
	public void noActiveSearchProfileBecauseOfNonMatchingCatalog() throws Exception
	{
		// given
		final CatalogVersionModel summerCatalogVersion = catalogVersionService.getCatalogVersion(SUMMER_CATALOG_ID, VERSION_ONLINE);
		final CatalogVersionModel winterCatalogVersion = catalogVersionService.getCatalogVersion(WINTER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv(
				"/personalizationsearch/test/cxSearchProfileActivationTest_noActiveSearchProfileBecauseOfNonMatchingCatalog.impex",
				"UTF-8");

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(
				Arrays.asList(summerCatalogVersion, winterCatalogVersion));

		// then
		final AsSearchProfileActivationGroup mainGroup = activationGroups.get(mainGroupIndex);
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).isEmpty();
	}

	@Test
	public void noActiveSearchProfileBecauseOfNonMatchingIndexType() throws Exception
	{
		// given
		final CatalogVersionModel summerCatalogVersion = catalogVersionService.getCatalogVersion(SUMMER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv(
				"/personalizationsearch/test/cxSearchProfileActivationTest_noActiveSearchProfileBecauseOfNonMatchingIndexType.impex",
				"UTF-8");

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Arrays.asList(summerCatalogVersion));

		// then
		final AsSearchProfileActivationGroup mainGroup = activationGroups.get(mainGroupIndex);
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).isEmpty();
	}

	@Test
	public void activateSearchProfileForVariation() throws Exception
	{
		// given
		final CatalogVersionModel summerCatalogVersion = catalogVersionService.getCatalogVersion(SUMMER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv("/personalizationsearch/test/cxSearchProfileActivationTest_activateSearchProfileForVariation.impex", "UTF-8");

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Arrays.asList(summerCatalogVersion));

		// then
		final AsSearchProfileActivationGroup mainGroup = activationGroups.get(mainGroupIndex);
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).hasSize(1);

		final AsSearchProfileActivationGroup group1 = mainGroup.getGroups().get(0);
		assertThat(group1.getSearchProfiles()).hasSize(1);
		assertThat(group1.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE1_CODE);
		assertThat(group1.getGroups()).isEmpty();
	}

	@Test
	public void activateMultipleSearchProfilesForVariation() throws Exception
	{
		// given
		final CatalogVersionModel summerCatalogVersion = catalogVersionService.getCatalogVersion(SUMMER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv("/personalizationsearch/test/cxSearchProfileActivationTest_activateMultipleSearchProfilesForVariation.impex",
				"UTF-8");

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Arrays.asList(summerCatalogVersion));

		// then
		final AsSearchProfileActivationGroup mainGroup = activationGroups.get(mainGroupIndex);
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).hasSize(1);

		final AsSearchProfileActivationGroup group1 = mainGroup.getGroups().get(0);
		assertThat(group1.getSearchProfiles()).hasSize(2);
		assertThat(group1.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE1_CODE,
				SEARCH_PROFILE3_CODE);
	}

	@Test
	public void activateMultipleSearchProfilesForMultipleVariations() throws Exception
	{
		// given
		final CatalogVersionModel winterCatalogVersion = catalogVersionService.getCatalogVersion(WINTER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv(
				"/personalizationsearch/test/cxSearchProfileActivationTest_activateMultipleSearchProfilesForMultipleVariations.impex",
				"UTF-8");

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Arrays.asList(winterCatalogVersion));

		// then
		final AsSearchProfileActivationGroup mainGroup = activationGroups.get(mainGroupIndex);
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).hasSize(2);

		final AsSearchProfileActivationGroup group1 = mainGroup.getGroups().get(1);
		assertThat(group1.getSearchProfiles()).hasSize(2);
		assertThat(group1.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE1_CODE,
				SEARCH_PROFILE2_CODE);

		final AsSearchProfileActivationGroup group2 = mainGroup.getGroups().get(0);
		assertThat(group2.getSearchProfiles()).hasSize(2);
		assertThat(group2.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE3_CODE,
				SEARCH_PROFILE4_CODE);
	}

	@Test
	public void activateMultipleSearchProfilesForMultipleVariationsWithCustomCustomizationsRank() throws Exception
	{
		// given
		final CatalogVersionModel cxCatalogVersion = catalogVersionService.getCatalogVersion(CX_CATALOG_ID, VERSION_ONLINE);
		final CatalogVersionModel winterCatalogVersion = catalogVersionService.getCatalogVersion(WINTER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv(
				"/personalizationsearch/test/cxSearchProfileActivationTest_activateMultipleSearchProfilesForMultipleVariations.impex",
				"UTF-8");

		final CxCustomizationsGroupModel customizationGroup = cxCustomizationService.getDefaultGroup(cxCatalogVersion);

		final List<CxCustomizationModel> customizations = new ArrayList<>(customizationGroup.getCustomizations());
		Collections.reverse(customizations);
		customizationGroup.setCustomizations(customizations);

		modelService.save(customizationGroup);

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Arrays.asList(winterCatalogVersion));

		// then
		final AsSearchProfileActivationGroup mainGroup = activationGroups.get(mainGroupIndex);
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).hasSize(2);

		final AsSearchProfileActivationGroup group1 = mainGroup.getGroups().get(1);
		assertThat(group1.getSearchProfiles()).hasSize(2);
		assertThat(group1.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE3_CODE,
				SEARCH_PROFILE4_CODE);

		final AsSearchProfileActivationGroup group2 = mainGroup.getGroups().get(0);
		assertThat(group2.getSearchProfiles()).hasSize(2);
		assertThat(group2.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE1_CODE,
				SEARCH_PROFILE2_CODE);
	}

	@Test
	public void activateMultipleSearchProfilesForMultipleVariationsWithCustomVariationsRank() throws Exception
	{
		// given
		final CatalogVersionModel cxCatalogVersion = catalogVersionService.getCatalogVersion(CX_CATALOG_ID, VERSION_ONLINE);
		final CatalogVersionModel winterCatalogVersion = catalogVersionService.getCatalogVersion(WINTER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv(
				"/personalizationsearch/test/cxSearchProfileActivationTest_activateMultipleSearchProfilesForMultipleVariations.impex",
				"UTF-8");

		final CxCustomizationsGroupModel customizationGroup = cxCustomizationService.getDefaultGroup(cxCatalogVersion);

		for (final CxCustomizationModel customization : customizationGroup.getCustomizations())
		{
			final List<CxVariationModel> variations = new ArrayList<>(customization.getVariations());
			Collections.reverse(variations);
			customization.setVariations(variations);

			modelService.save(customization);
		}

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Arrays.asList(winterCatalogVersion));

		// then
		final AsSearchProfileActivationGroup mainGroup = activationGroups.get(mainGroupIndex);
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).hasSize(2);

		final AsSearchProfileActivationGroup group1 = mainGroup.getGroups().get(1);
		assertThat(group1.getSearchProfiles()).hasSize(2);
		assertThat(group1.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE1_CODE,
				SEARCH_PROFILE2_CODE);

		final AsSearchProfileActivationGroup group2 = mainGroup.getGroups().get(0);
		assertThat(group2.getSearchProfiles()).hasSize(2);
		assertThat(group2.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE3_CODE,
				SEARCH_PROFILE4_CODE);
	}

	@Test
	public void activateMultipleSearchProfilesForMultipleVariationsWithCustomActionsRank() throws Exception
	{
		// given
		final CatalogVersionModel cxCatalogVersion = catalogVersionService.getCatalogVersion(CX_CATALOG_ID, VERSION_ONLINE);
		final CatalogVersionModel winterCatalogVersion = catalogVersionService.getCatalogVersion(WINTER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv(
				"/personalizationsearch/test/cxSearchProfileActivationTest_activateMultipleSearchProfilesForMultipleVariations.impex",
				"UTF-8");

		final CxCustomizationsGroupModel customizationGroup = cxCustomizationService.getDefaultGroup(cxCatalogVersion);

		for (final CxCustomizationModel customization : customizationGroup.getCustomizations())
		{
			for (final CxVariationModel variation : customization.getVariations())
			{
				final List<CxAbstractActionModel> actions = new ArrayList<>(variation.getActions());
				Collections.reverse(actions);
				variation.setActions(actions);

				modelService.save(variation);
			}
		}

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Arrays.asList(winterCatalogVersion));

		// then
		final AsSearchProfileActivationGroup mainGroup = activationGroups.get(mainGroupIndex);
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).hasSize(2);

		final AsSearchProfileActivationGroup group1 = mainGroup.getGroups().get(1);
		assertThat(group1.getSearchProfiles()).hasSize(2);
		assertThat(group1.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE2_CODE,
				SEARCH_PROFILE1_CODE);

		final AsSearchProfileActivationGroup group2 = mainGroup.getGroups().get(0);
		assertThat(group2.getSearchProfiles()).hasSize(2);
		assertThat(group2.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE4_CODE,
				SEARCH_PROFILE3_CODE);
	}

	@Test
	public void activateMultipleSearchProfilesForMultipleVariationsWithCustomRank() throws Exception
	{
		// given
		final CatalogVersionModel cxCatalogVersion = catalogVersionService.getCatalogVersion(CX_CATALOG_ID, VERSION_ONLINE);
		final CatalogVersionModel winterCatalogVersion = catalogVersionService.getCatalogVersion(WINTER_CATALOG_ID, VERSION_ONLINE);

		// when
		importCsv(
				"/personalizationsearch/test/cxSearchProfileActivationTest_activateMultipleSearchProfilesForMultipleVariations.impex",
				"UTF-8");

		final CxCustomizationsGroupModel customizationGroup = cxCustomizationService.getDefaultGroup(cxCatalogVersion);

		final List<CxCustomizationModel> customizations = new ArrayList<>(customizationGroup.getCustomizations());
		Collections.reverse(customizations);
		customizationGroup.setCustomizations(customizations);

		modelService.save(customizationGroup);

		for (final CxCustomizationModel customization : customizations)
		{
			final List<CxVariationModel> variations = new ArrayList<>(customization.getVariations());
			Collections.reverse(variations);
			customization.setVariations(variations);

			modelService.save(customization);

			for (final CxVariationModel variation : variations)
			{
				final List<CxAbstractActionModel> actions = new ArrayList<>(variation.getActions());
				Collections.reverse(actions);
				variation.setActions(actions);

				modelService.save(variation);
			}
		}

		final List<AsSearchProfileActivationGroup> activationGroups = activateSearchProfiles(Arrays.asList(winterCatalogVersion));

		// then
		final AsSearchProfileActivationGroup mainGroup = activationGroups.get(mainGroupIndex);
		assertThat(mainGroup.getSearchProfiles()).isEmpty();
		assertThat(mainGroup.getGroups()).hasSize(2);

		final AsSearchProfileActivationGroup group1 = mainGroup.getGroups().get(1);
		assertThat(group1.getSearchProfiles()).hasSize(2);
		assertThat(group1.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE4_CODE,
				SEARCH_PROFILE3_CODE);

		final AsSearchProfileActivationGroup group2 = mainGroup.getGroups().get(0);
		assertThat(group2.getSearchProfiles()).hasSize(2);
		assertThat(group2.getSearchProfiles()).extracting(AbstractAsSearchProfileModel.CODE).contains(SEARCH_PROFILE2_CODE,
				SEARCH_PROFILE1_CODE);
	}

	protected int findMainGroupIndex()
	{
		// YTODO: we need an id attribute in the activation groups so that we can identify them and remove this workaround
		final List<AsSearchProfileActivationMapping> mappings = asSearchProfileRegistry.getSearchProfileActivationMappings();
		int index = 0;

		for (final AsSearchProfileActivationMapping mapping : mappings)
		{
			if (Objects.equals(mapping.getActivationStrategy(), cxSearchProfileActivationStrategy))
			{
				return index;
			}

			index++;
		}

		return -1;
	}

	protected List<AsSearchProfileActivationGroup> activateSearchProfiles(final List<CatalogVersionModel> catalogVersions)
	{
		final UserModel cxUser = userService.getUserForUID(CX_USER);
		final CatalogVersionModel cxCatalogVersion = catalogVersionService.getCatalogVersion(CX_CATALOG_ID, VERSION_ONLINE);

		final List<CatalogVersionModel> sessionCatalogVersions = new ArrayList<>();
		sessionCatalogVersions.add(cxCatalogVersion);
		CollectionUtils.addAll(sessionCatalogVersions, catalogVersions);

		userService.setCurrentUser(cxUser);
		catalogVersionService.setSessionCatalogVersions(sessionCatalogVersions);

		cxService.calculateAndStorePersonalization(cxUser, cxCatalogVersion);
		cxService.loadPersonalizationInSession(cxUser, Collections.singleton(cxCatalogVersion));

		final AsSearchProfileContext context = asSearchProfileContextFactory.createContext(INDEX_CONFIGURATION, INDEX_TYPE,
				sessionCatalogVersions, Collections.emptyList());

		return asSearchProfileActivationService.getSearchProfileActivationGroupsForContext(context);
	}
}
