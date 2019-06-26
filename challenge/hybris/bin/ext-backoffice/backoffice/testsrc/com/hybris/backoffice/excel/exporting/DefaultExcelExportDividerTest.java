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
package com.hybris.backoffice.excel.exporting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.hybris.backoffice.excel.data.SelectedAttribute;
import com.hybris.backoffice.excel.template.mapper.ExcelMapper;


@RunWith(MockitoJUnitRunner.class)
public class DefaultExcelExportDividerTest
{

	@Rule
	public JUnitSoftAssertions soft = new JUnitSoftAssertions();

	@Mock
	private ExcelMapper<ComposedTypeModel, AttributeDescriptorModel> mapper;
	@Mock
	private TypeService typeService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private PermissionCRUDService permissionCRUDService;
	@Mock
	private ModelService modelService;
	@Spy
	@InjectMocks
	private DefaultExcelExportDivider divider;

	private static final String PRODUCT = "Product";
	private static final String CATALOG = "Catalog";

	@Before
	public void setUp()
	{
		final LanguageModel lang = mock(LanguageModel.class);
		when(lang.getIsocode()).thenReturn(Locale.ENGLISH.toString());
		given(commonI18NService.getCurrentLanguage()).willReturn(lang);
		when(permissionCRUDService.canReadAttribute(any(AttributeDescriptorModel.class))).thenReturn(true);
	}

	@Test
	public void shouldGroupItemsByType()
	{
		// given
		final int noOfProducts = 2;
		final int noOfCatalogs = 1;
		final List<ItemModel> mocks = generateItemModelMocks(noOfProducts, noOfCatalogs);

		// when
		final Map<String, Set<ItemModel>> map = divider.groupItemsByType(mocks);

		// then
		soft.assertThat(map.size()).isEqualTo(2);
		soft.assertThat(map.keySet()).containsOnly(PRODUCT, CATALOG);
		soft.assertThat(map.get(PRODUCT).size()).isEqualTo(noOfProducts);
		soft.assertThat(map.get(CATALOG).size()).isEqualTo(noOfCatalogs);
	}

	@Test
	public void shouldAvoidAlreadySelectedAttributes()
	{
		// given
		final ComposedTypeModel cp = mock(ComposedTypeModel.class);
		final List<AttributeDescriptorModel> ads = generateAttributeDescriptorMocks(Lists.newArrayList(//
				new Descriptor(false, false, true, "NOT PK", "qual0"), //
				new Descriptor(false, false, true, "ALSO NOT PK", "qual1"), //
				new Descriptor(true, false, true, "Identifier", ProductModel.NAME) //
		));
		given(mapper.apply(cp)).willReturn(ads);

		// when
		final Set<SelectedAttribute> result = divider.getMissingRequiredAndUniqueAttributes(cp, Sets.newHashSet(ProductModel.NAME));

		// then
		soft.assertThat(result.size()).isEqualTo(2);
		soft.assertThat(result.stream().anyMatch(e -> e.getAttributeDescriptor().getName().equals(ItemModel.PK))).isFalse();
	}

	@Test
	public void shouldNotDuplicateTheseSameQualifiers()
	{
		// given
		final ComposedTypeModel cp = mock(ComposedTypeModel.class);
		final List<AttributeDescriptorModel> ads = generateAttributeDescriptorMocks(Lists.newArrayList(//
				new Descriptor(false, false, true, "any", "thesame"), //
				new Descriptor(false, false, true, "any", "thesame"), //
				new Descriptor(false, false, true, "any", "thesame")//
		));
		given(mapper.apply(cp)).willReturn(ads);

		// when
		final Set<SelectedAttribute> result = divider.getMissingRequiredAndUniqueAttributes(cp, new HashSet<>());

		// then
		assertThat(result.size()).isEqualTo(1);
	}

	private List<ItemModel> generateItemModelMocks(final int numberOfProducts, final int numberOfCatalogs)
	{
		return Stream
				.concat(//
						IntStream.range(0, numberOfProducts).mapToObj(idx -> generateItemModelMock(PRODUCT)), //
						IntStream.range(0, numberOfCatalogs).mapToObj(idx -> generateItemModelMock(CATALOG)))//
				.collect(Collectors.toList());
	}

	private ItemModel generateItemModelMock(final String itemType)
	{
		final ItemModel itemModel = mock(ItemModel.class);
		given(itemModel.getItemtype()).willReturn(itemType);
		given(modelService.getModelType(itemModel)).willReturn(itemType);
		return itemModel;
	}

	private List<AttributeDescriptorModel> generateAttributeDescriptorMocks(final List<Descriptor> descriptors)
	{
		return descriptors.stream().map(descriptor -> {
			final AttributeDescriptorModel ad = mock(AttributeDescriptorModel.class);
			given(ad.getName()).willReturn(descriptor.getName());
			given(ad.getLocalized()).willReturn(descriptor.isLocalized());
			given(ad.getOptional()).willReturn(descriptor.isOptional());
			given(ad.getUnique()).willReturn(descriptor.isUnique());
			given(ad.getQualifier()).willReturn(descriptor.getQualifier());
			return ad;
		}).collect(Collectors.toList());
	}

	private class Descriptor
	{
		private final boolean localized;
		private final boolean optional;
		private final boolean unique;
		private final String name;
		private final String qualifier;

		Descriptor(final boolean localized, final boolean optional, final boolean unique, final String name, final String qualifier)
		{
			this.localized = localized;
			this.optional = optional;
			this.unique = unique;
			this.name = name;
			this.qualifier = qualifier;
		}

		boolean isLocalized()
		{
			return localized;
		}

		boolean isOptional()
		{
			return optional;
		}

		boolean isUnique()
		{
			return unique;
		}

		String getName()
		{
			return name;
		}

		String getQualifier()
		{
			return qualifier;
		}
	}

}
