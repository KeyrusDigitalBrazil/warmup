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
package de.hybris.platform.personalizationservices.variation.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.enums.CxItemStatus;
import de.hybris.platform.personalizationservices.model.CxAbstractActionModel;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.personalizationservices.model.CxCustomizationsGroupModel;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.model.CxSegmentTriggerModel;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.personalizationservices.stub.CxCustomizationModelStub;
import de.hybris.platform.personalizationservices.stub.CxVariationModelStub;
import de.hybris.platform.personalizationservices.trigger.CxTriggerService;
import de.hybris.platform.personalizationservices.variation.dao.CxVariationDao;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;



@UnitTest
public class DefaultCxVariationServiceTest
{
	private final DefaultCxVariationService service = new DefaultCxVariationService();
	private CxCustomizationsGroupModel cxCustomizationGroup;
	@Mock
	private CatalogVersionModel catalogVersionStage;
	@Mock
	private CatalogVersionModel catalogVersionOnline;
	@Mock
	private CxVariationDao variationDao;
	@Mock
	private ModelService modelService;
	@Mock
	private CxTriggerService cxTriggerService;
	private Map<String, CxCustomizationModel> customizationMap;

	@Before
	public void initMocks()
	{
		MockitoAnnotations.initMocks(this);
		service.setCxVariationDao(variationDao);
		service.setModelService(modelService);
		service.setCxTriggerService(cxTriggerService);
		cxCustomizationGroup = new CxCustomizationsGroupModel();
		Mockito.when(catalogVersionStage.getPk()).thenReturn(PK.fromLong(1l));
		Mockito.when(catalogVersionOnline.getPk()).thenReturn(PK.fromLong(2l));
		Mockito.when(cxTriggerService.getVariationsForUser(Mockito.any(), Mockito.any()))
				.thenAnswer((final InvocationOnMock data) -> {
					final UserModel u = (UserModel) (data.getArguments()[0]);
					if (u.getUserToSegments() != null)
					{
						return u.getUserToSegments().stream().map(uts -> uts.getSegment()).map(s -> s.getTriggers())
								.flatMap(t -> t.stream()).map(t -> t.getVariation())
								.filter(v -> v.getCatalogVersion().equals(data.getArguments()[1])).collect(Collectors.toList());
					}
					else
					{
						return Collections.EMPTY_LIST;
					}
				});

		customizationMap = new HashMap<>();
	}

	//Tests for getVariation

	@Test
	public void testGetVariation()
	{
		//given
		final CxVariationModel variation = createVariation("v1", "v1", true, catalogVersionStage);
		final CxCustomizationModel customization = createCustomization(Collections.singletonList(variation), "c1", "c1",
				Integer.valueOf(1));

		Mockito.when(variationDao.findVariationByCode("v1", customization)).thenReturn(Optional.of(variation));

		//when
		final Optional<CxVariationModel> result = service.getVariation("v1", customization);

		//then
		Assert.assertEquals(variation, result.get());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetVariationWithNullCode()
	{
		//given
		final CxCustomizationModel customization = createCustomization(Collections.emptyList(), "c1", "c1", Integer.valueOf(1));

		//when
		service.getVariation(null, customization);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetVariationWithNullCustomization()
	{
		//when
		service.getVariation("v1", null);
	}

	//Tests for getActiveVariations
	@Test
	public void testGetActiveVariations()
	{
		final CxVariationModel variation = createVariation("v1", "v1", true, catalogVersionStage);
		createCustomization(Arrays.asList(variation), "c1", "c1", Integer.valueOf(1));
		final CxSegmentModel segment = createSegment(Arrays.asList(variation), "s1");
		final UserModel user = createUser(segment);

		final List<CxVariationModel> variations = service.getActiveVariations(user, catalogVersionStage);
		Assert.assertEquals(variations.size(), 1);
		Assert.assertEquals(variations.get(0), variation);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetActiveVariationsWithNullSegments()
	{
		//when
		service.getActiveVariations(null, catalogVersionStage);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetActiveVariationsWithNullCatalogVersion()
	{
		//when
		service.getActiveVariations(new UserModel(), null);
	}

	@Test
	public void testGetActiveVariationsForEmptySegmentList()
	{
		//given
		final UserModel user = new UserModel();

		//when
		final List<CxVariationModel> variations = service.getActiveVariations(user, catalogVersionStage);

		//then
		Assert.assertTrue(variations.isEmpty());
	}

	@Test
	public void testGetActiveVariationsForSegmentWithoutVariations()
	{
		//given
		final CxSegmentModel segment = createSegment(Collections.emptyList(), "1");
		final UserModel user = createUser(segment);

		//when
		final List<CxVariationModel> variations = service.getActiveVariations(user, catalogVersionStage);

		//then
		Assert.assertTrue(variations.isEmpty());
	}

	@Test
	public void testGetActiveVariationsForInactiveVariation()
	{
		//given
		final CxVariationModel variation = createVariation("v1", "v1", false, catalogVersionStage);
		createCustomization(Arrays.asList(variation), "c1", "c1", Integer.valueOf(1));
		final CxSegmentModel segment = createSegment(Arrays.asList(variation), "s1");
		final UserModel user = createUser(segment);

		//when
		final List<CxVariationModel> variations = service.getActiveVariations(user, catalogVersionStage);

		//then
		Assert.assertEquals(variations.size(), 0);
	}

	@Test
	public void testGetActiveVariationsForCatalogVersionWithoutVariations()
	{
		//given
		final CxVariationModel variation = createVariation("v1", "v1", true, catalogVersionStage);
		createCustomization(Arrays.asList(variation), "c1", "c1", Integer.valueOf(1));
		final CxSegmentModel segment = createSegment(Arrays.asList(variation), "1");
		final UserModel user = createUser(segment);

		//when
		final List<CxVariationModel> variations = service.getActiveVariations(user, catalogVersionOnline);

		//then
		Assert.assertTrue(variations.isEmpty());
	}

	@Test
	public void testGetActiveVariationsForDifferentCatalogVersions()
	{
		//given
		final CxVariationModel variation1 = createVariation("v1", "v1", true, catalogVersionStage);
		createCustomization(Arrays.asList(variation1), "c1", "c1", Integer.valueOf(1));

		final CxVariationModel variation2 = createVariation("v2", "v2", true, catalogVersionOnline);
		createCustomization(Arrays.asList(variation2), "c2", "c2", Integer.valueOf(2));

		final CxSegmentModel segment = createSegment(Arrays.asList(variation1, variation2), "s1");
		final UserModel user = createUser(segment);

		//when
		final List<CxVariationModel> variations = service.getActiveVariations(user, catalogVersionStage);

		//then
		Assert.assertEquals(variations.size(), 1);
	}

	@Test
	public void shouldReturnFirstVariationWithCustomization()
	{
		//given
		final CxVariationModel v1 = createVariation("v1", "v1", true, catalogVersionStage);
		final CxVariationModel v2 = createVariation("v2", "v2", true, catalogVersionStage);
		createCustomization(Arrays.asList(v1, v2), "c1", "c1", Integer.valueOf(1));

		final CxSegmentModel segment = createSegment(Arrays.asList(v1, v2), "s1");
		final UserModel user = createUser(segment);

		//when
		final List<CxVariationModel> variations = service.getActiveVariations(user, catalogVersionStage);

		//then
		Assert.assertEquals(variations.size(), 1);
	}

	@Test
	public void shouldReturn2VariationsIn2Customizations()
	{
		//given
		final CxVariationModel v1 = createVariation("v1", "v1", true, catalogVersionStage);
		final CxVariationModel v2 = createVariation("v2", "v2", true, catalogVersionStage);
		final CxVariationModel v3 = createVariation("v2", "v2", true, catalogVersionStage);
		final CxCustomizationModel c1 = createCustomization(Arrays.asList(v1, v2), "c1", "c1", Integer.valueOf(1));
		final CxCustomizationModel c2 = createCustomization(Arrays.asList(v3), "c2", "c2", Integer.valueOf(0));
		setCustomizationRank(c1, c2);

		final CxSegmentModel segment = createSegment(Arrays.asList(v1, v2, v3), "s1");
		final UserModel user = createUser(segment);

		//when
		final List<CxVariationModel> variations = service.getActiveVariations(user, catalogVersionStage);

		//then
		Assert.assertEquals(variations.size(), 2);
		Assert.assertEquals(variations.get(0), v1);
		Assert.assertEquals(variations.get(1), v3);
	}

	@Test
	public void shouldReturn3VariationsIn3CustomizationsAnd2Segments()
	{
		//given
		final CxVariationModel v1 = createVariation("v1", "v1", true, catalogVersionStage);
		final CxVariationModel v2 = createVariation("v2", "v2", true, catalogVersionStage);
		final CxVariationModel v3 = createVariation("v3", "v3", true, catalogVersionStage);
		final CxVariationModel v4 = createVariation("v4", "v4", true, catalogVersionStage);
		final CxCustomizationModel c1 = createCustomization(Arrays.asList(v1, v2), "c1", "c1", Integer.valueOf(2));
		final CxCustomizationModel c2 = createCustomization(Arrays.asList(v3), "c2", "c2", Integer.valueOf(1));
		final CxCustomizationModel c3 = createCustomization(Arrays.asList(v4), "c3", "c3", Integer.valueOf(0));
		setCustomizationRank(c1, c2, c3);

		final CxSegmentModel s1 = createSegment(Arrays.asList(v1, v2, v3), "s1");
		final CxSegmentModel s2 = createSegment(Arrays.asList(v4), "s2");
		final UserModel user = createUser(s1, s2);

		//when
		final List<CxVariationModel> variations = service.getActiveVariations(user, catalogVersionStage);

		//then
		Assert.assertEquals(variations.size(), 3);
		Assert.assertEquals(variations.get(0), v1);
		Assert.assertEquals(variations.get(1), v3);
		Assert.assertEquals(variations.get(2), v4);
	}

	@Test
	public void shouldReturn2VariationsIn2CustomizationsAnd2Segments()
	{
		//given
		final CxVariationModel v1 = createVariation("v1", "v1", true, catalogVersionStage);
		final CxVariationModel v2 = createVariation("v2", "v2", true, catalogVersionStage);
		final CxVariationModel v3 = createVariation("v3", "v3", true, catalogVersionStage);
		final CxVariationModel v4 = createVariation("v4", "v4", true, catalogVersionStage);
		final CxCustomizationModel c1 = createCustomization(Arrays.asList(v1, v2), "c1", "c1", Integer.valueOf(2));
		final CxCustomizationModel c2 = createCustomization(Arrays.asList(v4, v3), "c2", "c2", Integer.valueOf(1));
		setCustomizationRank(c1, c2);

		final CxSegmentModel s1 = createSegment(Arrays.asList(v1, v2, v3), "s1");
		final CxSegmentModel s2 = createSegment(Arrays.asList(v4), "s2");
		final UserModel user = createUser(s1, s2);

		//when
		final List<CxVariationModel> variations = service.getActiveVariations(user, catalogVersionStage);

		//then
		Assert.assertEquals(variations.size(), 2);
		Assert.assertEquals(variations.get(0), v1);
		Assert.assertEquals(variations.get(1), v4);
	}

	@Test
	public void testCreateVariation()
	{
		//given
		CxVariationModel variation = createVariation("v1", "v1", true, null);
		final CxCustomizationModel customization = createCustomization(Collections.emptyList(), "c1", "c1", null);
		customization.setCatalogVersion(catalogVersionStage);

		//when
		variation = service.createVariation(variation, customization, null);

		//then
		Assert.assertNotNull(variation);
		Assert.assertEquals("v1", variation.getCode());
		Assert.assertEquals(variation.getCustomization(), customization);
		Assert.assertEquals(catalogVersionStage, variation.getCatalogVersion());
		Assert.assertEquals(0, variation.getRank().intValue());
	}

	@Test
	public void testCreateVariationWithRank()
	{
		//given
		final Integer rank = Integer.valueOf(1);
		CxVariationModel variation = createVariation("v1", "v1", true, null);
		final CxVariationModel existingVariation = createVariation("v2", "v2", true, catalogVersionStage);
		final CxVariationModel existingVariation2 = createVariation("v3", "v3", true, catalogVersionStage);
		final CxCustomizationModel customization = createCustomization(Arrays.asList(existingVariation, existingVariation2), "c1",
				"c1", null);
		customization.setCatalogVersion(catalogVersionStage);

		//when
		variation = service.createVariation(variation, customization, rank);

		//then
		Assert.assertNotNull(variation);
		Assert.assertEquals("v1", variation.getCode());
		Assert.assertEquals(variation.getCustomization(), customization);
		Assert.assertEquals(catalogVersionStage, variation.getCatalogVersion());
		Assert.assertEquals(rank, variation.getRank());
		Assert.assertEquals(rank.intValue(), customization.getVariations().indexOf(variation));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNullVariation()
	{
		//given
		final CxCustomizationModel customization = createCustomization(Collections.emptyList(), "c1", "c1", null);
		customization.setCatalogVersion(catalogVersionStage);

		//when
		service.createVariation(null, customization, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateVariationWithNullCode()
	{
		//given
		CxVariationModel variation = createVariation(null, null, true, null);
		final CxCustomizationModel customization = createCustomization(Collections.emptyList(), "c1", "c1", null);
		customization.setCatalogVersion(catalogVersionStage);

		//when
		variation = service.createVariation(variation, customization, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateVariationWithNullCustomization()
	{
		//given
		final CxVariationModel variation = createVariation("v1", "v1", true, null);

		//when
		service.createVariation(variation, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateVariationWithNullCatalogVersion()
	{
		//given
		CxVariationModel variation = createVariation("v1", "v1", true, null);
		final CxCustomizationModel customization = createCustomization(Collections.emptyList(), "c1", "c1", null);

		//when
		variation = service.createVariation(variation, customization, null);

		//then
		Assert.assertNotNull(variation);
		Assert.assertEquals("v1", variation.getCode());
		Assert.assertEquals(variation.getCustomization(), customization);
		Assert.assertEquals(catalogVersionStage, variation.getCatalogVersion());
		Assert.assertEquals(0, variation.getRank().intValue());
	}


	protected CxCustomizationModel createCustomization(final List<CxVariationModel> variations, final String customizationCode,
			final String customizationName, final Integer customizationPriority)
	{
		final CxCustomizationModel customization = new CxCustomizationModelStub();
		customization.setCode(customizationCode);
		customization.setCode(customizationName);
		customization.setGroup(cxCustomizationGroup);
		customization.setRank(customizationPriority);
		customization.setVariations(variations);
		customization.setStatus(CxItemStatus.ENABLED);
		for (final CxVariationModel variation : variations)
		{
			variation.setCustomization(customization);
		}

		customizationMap.put(customizationCode, customization);
		return customization;
	}

	protected void setCustomizationRank(final CxCustomizationModel... customizations)
	{
		final List<CxCustomizationModel> list = Arrays.asList(customizations);
		cxCustomizationGroup.setCustomizations(list);
	}

	protected CxSegmentModel createSegment(final List<CxVariationModel> variations, final String segmentCode)
	{
		final CxSegmentModel segment = new CxSegmentModel();
		segment.setCode(segmentCode);
		segment.setTriggers(new ArrayList<>());
		for (final CxVariationModel v : variations)
		{
			final CxSegmentTriggerModel trigger = new CxSegmentTriggerModel();
			trigger.setSegments(Collections.singletonList(segment));
			trigger.setVariation(v);
			segment.getTriggers().add(trigger);
		}
		return segment;
	}

	protected UserModel createUser(final CxSegmentModel... segments)
	{
		final UserModel user = new UserModel();
		if (segments != null)
		{
			final List<CxUserToSegmentModel> relation = Arrays.asList(segments).stream().map(s -> {
				final CxUserToSegmentModel u = new CxUserToSegmentModel();
				u.setAffinity(BigDecimal.ONE);
				u.setSegment(s);
				u.setUser(user);
				return u;
			}).collect(Collectors.toList());
			user.setUserToSegments(relation);

		}
		return user;
	}

	protected CxVariationModel createVariation(final String code, final String name, final boolean enabled,
			final CatalogVersionModel catalogVersion, final CxAbstractActionModel... actions)
	{
		final CxVariationModel variation = new CxVariationModelStub();
		variation.setCode(code);
		variation.setName(name);
		variation.setStatus(enabled ? CxItemStatus.ENABLED : CxItemStatus.DISABLED);
		variation.setActions(new ArrayList<>());
		variation.setCatalogVersion(catalogVersion);


		for (final CxAbstractActionModel action : actions)
		{
			variation.getActions().add(action);
		}
		return variation;
	}
}
