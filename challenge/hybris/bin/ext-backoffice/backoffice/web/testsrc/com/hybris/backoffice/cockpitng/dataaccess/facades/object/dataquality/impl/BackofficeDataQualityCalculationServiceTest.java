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
package com.hybris.backoffice.cockpitng.dataaccess.facades.object.dataquality.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.validation.coverage.CoverageCalculationService;
import de.hybris.platform.validation.coverage.CoverageInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.dataquality.model.DataQuality;
import com.hybris.cockpitng.dataquality.model.DataQualityProperty;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeDataQualityCalculationServiceTest
{

	private static final double TEST_COVERAGE_INDEX = 1.0;
	private static final String TEST_DOMAIN_ID = "testDomainId";
	private static final String TEST_ITEM_TYPE = "testItemType";
	private static final String TEST_TEMPLATE_CODE = "testTemplateCode";
	private static final String TEST_COVERAGE_DESCRIPTION = "testCoverageDescription";
	private static final List<CoverageInfo.CoveragePropertyInfoMessage> EMPTY_LIST = Collections.emptyList();

	@InjectMocks
	private final BackofficeDataQualityCalculationService dataQualityCalculationService = new BackofficeDataQualityCalculationService();

	@Mock
	private CoverageCalculationService coverageCalculationService;

	@Mock
	private TypeFacade typeFacade;

	@Mock
	private ItemModel item;

	@Mock
	private CoverageInfo coverageInfo;


	@Test
	public void shouldCalculateDataQualityForObjectTemplateCodeAndDomainId()
	{
		// given
		when(coverageCalculationService.calculate(item, TEST_TEMPLATE_CODE, TEST_DOMAIN_ID)).thenReturn(coverageInfo);

		// when
		final Optional<DataQuality> dataQuality = dataQualityCalculationService.calculate(item, TEST_TEMPLATE_CODE, TEST_DOMAIN_ID);

		// then
		assertThat(dataQuality.isPresent()).isTrue();
	}

	@Test
	public void shouldCalculateDataQualityForObjectAndDomainId()
	{
		// given
		when(item.getItemtype()).thenReturn(TEST_ITEM_TYPE);
		when(coverageCalculationService.calculate(item, TEST_ITEM_TYPE, TEST_DOMAIN_ID)).thenReturn(coverageInfo);

		// when
		final Optional<DataQuality> dataQuality = dataQualityCalculationService.calculate(item, TEST_DOMAIN_ID);

		// then
		assertThat(dataQuality.isPresent()).isTrue();
	}

	@Test
	public void shouldCreateDataQualityObject()
	{
		// given
		when(Double.valueOf(coverageInfo.getCoverageIndex())).thenReturn(Double.valueOf(TEST_COVERAGE_INDEX));
		when(coverageInfo.getCoverageDescription()).thenReturn(TEST_COVERAGE_DESCRIPTION);
		when(coverageInfo.getPropertyInfoMessages()).thenReturn(EMPTY_LIST);

		// when
		final Optional<DataQuality> dataQuality = dataQualityCalculationService.convertToDataQuality(coverageInfo);

		// then
		assertThat(dataQuality.isPresent()).isTrue();
		assertThat(dataQuality.get().getDataQualityIndex()).isEqualTo(TEST_COVERAGE_INDEX);
		assertThat(dataQuality.get().getDescription()).isEqualTo(TEST_COVERAGE_DESCRIPTION);
		assertThat(dataQuality.get().getDataQualityProperties()).isEqualTo(EMPTY_LIST);
	}

	@Test
	public void shouldCreateCoverageProperties() throws TypeNotFoundException
	{
		// given
		final String testQualifier = "testQualifier";
		final String testMessage = "testMessage";
		final DataQualityProperty expectedProperty = new DataQualityProperty(testQualifier, testMessage);

		when(typeFacade.load(testQualifier)).thenThrow(new TypeNotFoundException(testQualifier));
		final List<DataQualityProperty> coverageProperties = createCoverageProperties(testQualifier,testMessage);

		// then
		assertThat(coverageProperties.get(0)).isEqualTo(expectedProperty);
	}

	@Test
	public void shouldCreateCoveragePropertiesForLocalize() throws TypeNotFoundException
	{
		// given
		final String type = "Product";
		final String localizeQualifier = "testQualifier[de]";
		final String typeAndQualifier = type.concat(".").concat(localizeQualifier);
		final String testMessage = "testMessage";
		final DataQualityProperty expectedProperty = new DataQualityProperty(localizeQualifier, testMessage);

		final List<DataQualityProperty> coverageProperties = createCoverageProperties(typeAndQualifier,testMessage);

		// then
		assertThat(coverageProperties.get(0)).isEqualTo(expectedProperty);
	}

	@Test
	public void shouldCreteEmptyCoveragePropertiesWhenPropertyInfoMessagesAreEmpty()
	{
		// given
		final List<CoverageInfo.CoveragePropertyInfoMessage> propertyInfoMessages = new ArrayList<>();

		// when
		final List coverageProperties = dataQualityCalculationService.convertToCoverageProperties(propertyInfoMessages);

		// then
		assertThat(coverageProperties).isEmpty();
	}

	@Test
	public void shouldNotCalculateDataQualityWhenObjectIsNotAnInstanceOfTheItemModel()
	{
		// given
		final Object objectWhichIsNotItemModel = new Object();

		// when
		final Optional<DataQuality> dataQuality = dataQualityCalculationService.calculate(objectWhichIsNotItemModel,
				TEST_DOMAIN_ID);

		// then
		assertThat(dataQuality.isPresent()).isFalse();
	}

	@Test
	public void shouldNotCalculateDataQualityWhenObjectIsNotAnInstanceOfTheItemModelWithTemplateCode()
	{
		// given
		final Object objectWhichIsNotItemModel = new Object();

		// when
		final Optional<DataQuality> dataQuality = dataQualityCalculationService.calculate(objectWhichIsNotItemModel,
				TEST_TEMPLATE_CODE, TEST_DOMAIN_ID);

		// then
		assertThat(dataQuality.isPresent()).isFalse();
	}

	@Test
	public void shouldUseWholePathAsPropertyQualifier() throws TypeNotFoundException
	{
		// given
		final String root = "root";
		final String qualifier = "testQualifier";
		final String path = root + "." + qualifier;
		when(typeFacade.load(root)).thenThrow(new TypeNotFoundException(root));

		// when
		final String propertyQualifier = dataQualityCalculationService
				.getPropertyQualifier(new CoverageInfo.CoveragePropertyInfoMessage(path, ""));

		// then
		assertThat(propertyQualifier).isEqualTo(path);
	}

	@Test
	public void shouldRemoveRootFromPropertyQualifier() throws TypeNotFoundException
	{
		// given
		final String root = "root";
		final String qualifier = "testQualifier";
		final String path = root + "." + qualifier;
		when(typeFacade.load(root)).thenReturn(new DataType.Builder(root).build());

		// when
		final String propertyQualifier = dataQualityCalculationService
				.getPropertyQualifier(new CoverageInfo.CoveragePropertyInfoMessage(path, ""));

		// then
		assertThat(propertyQualifier).isEqualTo(qualifier);
	}

	private List<DataQualityProperty> createCoverageProperties(final String qualifier, final String message)
	{
		final List<CoverageInfo.CoveragePropertyInfoMessage> propertyInfoMessages = new ArrayList<>();
		propertyInfoMessages.add(new CoverageInfo.CoveragePropertyInfoMessage(qualifier, message));

		return dataQualityCalculationService.convertToCoverageProperties(propertyInfoMessages);

	}

}
