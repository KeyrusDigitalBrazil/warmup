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
package de.hybris.platform.assistedserviceyprofilefacades.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedserviceyprofilefacades.data.CategoryAffinityData;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.yaasyprofileconnect.yaas.Affinity;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;


@UnitTest
public class CategoryAffinityPopulatorTest extends AbstractProfileAffinityTest
{
	@InjectMocks
	private final CategoryAffinityPopulator categoryPopulator = new CategoryAffinityPopulator<>();

	@InjectMocks
	private final AffinityPopulator affinityPopulator = new AffinityPopulator();

	@Mock
	private Converter<CategoryModel, CategoryData> categoryUrlConverter;
	@Mock
	private CategoryService categoryService;

	@Test
	public void getAffinityTest()
	{
		final CategoryModel categoryModel = Mockito.mock(CategoryModel.class);
		final CategoryData categoryData = Mockito.mock(CategoryData.class);
		final ImageData imageData = Mockito.mock(ImageData.class);

		final List<Map.Entry<String, Affinity>> affinityList = affinityProfile.getInsights().getAffinities().getCategories().entrySet().parallelStream().collect(Collectors.toList());


        assertEquals(1, affinityList.size());

		final Map.Entry<String, Affinity> categoryAffinity = affinityList.get(0);

		Mockito
				.when(categoryService
						.getCategoryForCode(categoryAffinity.getKey()))
				.thenReturn(categoryModel);
		Mockito.when(categoryUrlConverter.convert(categoryModel)).thenReturn(categoryData);
		Mockito.when(categoryData.getImage()).thenReturn(imageData);

		final CategoryAffinityData categoryAffinityData = new CategoryAffinityData();

		categoryPopulator.populate(categoryAffinity, categoryAffinityData);
		affinityPopulator.populate(categoryAffinity, categoryAffinityData);

		assertEquals(categoryData, categoryAffinityData.getCategoryData());
		assertEquals(imageData, categoryAffinityData.getImage());
		assertEquals(0.1123124, categoryAffinityData.getRecentScore().doubleValue(),0.0000001);
		assertEquals((Integer) 2, categoryAffinityData.getRecentViewCount());
		assertEquals(0.3010299956639812, categoryAffinityData.getScore().doubleValue(),0.0000001);
	}
}
