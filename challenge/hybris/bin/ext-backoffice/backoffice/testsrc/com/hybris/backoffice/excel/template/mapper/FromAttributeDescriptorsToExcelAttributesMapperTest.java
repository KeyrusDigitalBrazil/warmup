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
package com.hybris.backoffice.excel.template.mapper;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelAttributeDescriptorAttribute;
import com.hybris.backoffice.excel.template.filter.ExcelFilter;


@RunWith(MockitoJUnitRunner.class)
public class FromAttributeDescriptorsToExcelAttributesMapperTest extends AbstractExcelMapperTest
{
	@Mock
	private CommonI18NService commonI18NService;
	private FromAttributeDescriptorsToExcelAttributesMapper mapper = new FromAttributeDescriptorsToExcelAttributesMapper();

	@Before
	public void setUp()
	{
		mapper.setCommonI18NService(commonI18NService);
	}

	@Test
	public void shouldReturnCollectionOfExcelAttributeDescriptors()
	{
		// given
		final String en = "en";
		final String de = "de";

		final AttributeDescriptorModel localizedAttributeDescriptor = mockAttributeDescriptorLocalized(true);
		final AttributeDescriptorModel unlocalizedAttributeDescriptor = mockAttributeDescriptorLocalized(false);

		mockCommonI18NService(en, de);

		// when
		final Collection<ExcelAttributeDescriptorAttribute> excelAttributes = mapper
				.apply(Lists.newArrayList(localizedAttributeDescriptor, unlocalizedAttributeDescriptor));

		// then
		assertThat(excelAttributes.size()).isEqualTo(3);
		assertThat(excelAttributes.stream().map(ExcelAttributeDescriptorAttribute::getIsoCode).collect(Collectors.toSet()))
				.containsOnly(en, de, null);
	}

	@Test
	public void shouldReturnedCollectionBeFiltered()
	{
		// given
		final String en = "en";
		final String de = "de";

		final AttributeDescriptorModel localizedAttributeDescriptor = mockAttributeDescriptorLocalized(true);
		final AttributeDescriptorModel unlocalizedAttributeDescriptor = mockAttributeDescriptorLocalized(false);

		mockCommonI18NService(en, de);

		final ExcelFilter<ExcelAttributeDescriptorAttribute> filter = attr -> StringUtils.equals(attr.getIsoCode(), en);
		mapper.setFilters(Lists.newArrayList(filter));

		// when
		final Collection<ExcelAttributeDescriptorAttribute> excelAttributes = mapper
				.apply(Lists.newArrayList(localizedAttributeDescriptor, unlocalizedAttributeDescriptor));

		// then
		assertThat(excelAttributes.size()).isEqualTo(1);
		assertThat(excelAttributes.stream().map(ExcelAttributeDescriptorAttribute::getIsoCode).collect(Collectors.toSet()))
				.containsOnly(en);
	}

	protected void mockCommonI18NService(final String... isoCodes)
	{
		final List<LanguageModel> languageModels = new ArrayList<>(isoCodes.length);
		Lists.newArrayList(isoCodes).forEach(isoCode -> {
			final LanguageModel language = mock(LanguageModel.class);
			given(language.getActive()).willReturn(true);
			given(language.getIsocode()).willReturn(isoCode);
			languageModels.add(language);
		});
		given(commonI18NService.getAllLanguages()).willReturn(languageModels);
	}

}
