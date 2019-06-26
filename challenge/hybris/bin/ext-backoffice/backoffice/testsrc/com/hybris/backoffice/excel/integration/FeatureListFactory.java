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
package com.hybris.backoffice.excel.integration;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.collect.Lists;


public class FeatureListFactory
{

	/**
	 * Classification attributes names
	 */
	static final String SINGLE_NUMBER_WITHOUT_UNIT = "singleNumberWithoutUnit";
	static final String SINGLE_BOOLEAN = "singleBoolean";
	static final String MULTIPLE_BOOLEAN = "multipleBoolean";
	static final String SINGLE_NUMBER_WITH_UNIT = "singleNumberWithUnit";
	static final String MULTIPLE_NUMBER_WITH_UNIT = "multipleNumberWithUnit";
	static final String RANGE_SINGLE_NUMBER_WITHOUT_UNIT = "rangeSingleNumberWithoutUnit";
	static final String RANGE_MULTIPLE_NUMBER_WITH_UNIT = "rangeMultipleNumberWithUnit";
	static final String SINGLE_DATE = "singleDate";
	static final String SINGLE_RANGE_DATE = "singleRangeDate";
	static final String SINGLE_STRING = "singleString";
	static final String MULTI_STRING = "multiString";
	static final String SINGLE_LOCALIZED_STRING = "singleLocalizedString";
	static final String SINGLE_ENUM = "singleEnum";
	static final String SINGLE_REFERENCE = "singleReference";

	private FeatureListFactory()
	{
	}

	public static FeatureList create(final ModelService modelService, final TypeService typeService,
			final ClassificationClassModel classificationClass, final CatalogVersionModel catalogVersionModel,
			final ClassificationSystemVersionModel classificationSystemVersionModel)
	{
		return getFeatureList(modelService, typeService, classificationClass, catalogVersionModel,
				classificationSystemVersionModel);
	}

	private static <T extends FeatureTestBuilder.Builder> T getBuilder(final FeatureTestBuilder builder,
			final Class<T> builderClass, final String attributeName, final ModelService modelService,
			final ClassificationClassModel classificationClass,
			final ClassificationSystemVersionModel classificationSystemVersionModel)
	{
		return builder //
				.of(builderClass, modelService, classificationClass, classificationSystemVersionModel, attributeName) //
				.orElseThrow(InstantiationError::new);
	}

	private static FeatureList getFeatureList(final ModelService modelService, final TypeService typeService,
			final ClassificationClassModel classificationClass, final CatalogVersionModel catalogVersionModel,
			final ClassificationSystemVersionModel classificationSystemVersionModel)
	{
		final FeatureTestBuilder builder = new FeatureTestBuilder();

		final Feature singleBoolean = getBuilder(builder, FeatureTestBuilder.BooleanBuilder.class, SINGLE_BOOLEAN, modelService,
				classificationClass, classificationSystemVersionModel) //
						.values(true) //
						.build();

		final Feature multiBoolean = getBuilder(builder, FeatureTestBuilder.BooleanBuilder.class, MULTIPLE_BOOLEAN, modelService,
				classificationClass, classificationSystemVersionModel) //
						.multivalue() //
						.values(true, false, true) //
						.build();

		final Feature singleNumberWithoutUnit = getBuilder(builder, FeatureTestBuilder.NumberBuilder.class,
				SINGLE_NUMBER_WITHOUT_UNIT, modelService, classificationClass, classificationSystemVersionModel) //
						.values(3.53) //
						.build();

		final Feature singleNumberWithUnit = getBuilder(builder, FeatureTestBuilder.NumberBuilder.class, SINGLE_NUMBER_WITH_UNIT,
				modelService, classificationClass, classificationSystemVersionModel) //
						.values(4.53) //
						.unit(createUnit(modelService, classificationSystemVersionModel, "kg")) //
						.build();

		final Feature multipleNumberWithUnit = getBuilder(builder, FeatureTestBuilder.NumberBuilder.class,
				MULTIPLE_NUMBER_WITH_UNIT, modelService, classificationClass, classificationSystemVersionModel) //
						.multivalue() //
						.values(4.53, 3.276, 3.21) //
						.unit(createUnit(modelService, classificationSystemVersionModel, "g")) //
						.build();

		final Feature rangeSingleNumberWithoutUnit = getBuilder(builder, FeatureTestBuilder.NumberBuilder.class,
				RANGE_SINGLE_NUMBER_WITHOUT_UNIT, modelService, classificationClass, classificationSystemVersionModel) //
						.range() //
						.values(2.53, 3.77) //
						.build();

		final Feature rangeMultipleNumberWithUnit = getBuilder(builder, FeatureTestBuilder.NumberBuilder.class,
				RANGE_MULTIPLE_NUMBER_WITH_UNIT, modelService, classificationClass, classificationSystemVersionModel) //
						.multivalue() //
						.range() //
						.values(1.53, 1.58, 2.01, 2.53) //
						.unit(createUnit(modelService, classificationSystemVersionModel, "m")) //
						.build();

		final Feature singleDate = getBuilder(builder, FeatureTestBuilder.DateBuilder.class, SINGLE_DATE, modelService,
				classificationClass, classificationSystemVersionModel) //
						.values(Date.from(LocalDateTime.of(2018, 3, 3, 10, 0).toInstant(ZoneOffset.UTC))) //
						.build();

		final Feature singleRangeDate = getBuilder(builder, FeatureTestBuilder.DateBuilder.class, SINGLE_RANGE_DATE, modelService,
				classificationClass, classificationSystemVersionModel) //
						.range() //
						.values( //
								Date.from(LocalDateTime.of(2018, 3, 3, 10, 0).toInstant(ZoneOffset.UTC)), // from
								Date.from(LocalDateTime.of(2019, 3, 3, 12, 0).toInstant(ZoneOffset.UTC)) // to
						) //
						.build();

		final Feature singleString = getBuilder(builder, FeatureTestBuilder.StringBuilder.class, SINGLE_STRING, modelService,
				classificationClass, classificationSystemVersionModel) //
						.unlocalizedValues("some string") //
						.build();

		final Feature multiString = getBuilder(builder, FeatureTestBuilder.StringBuilder.class, MULTI_STRING, modelService,
				classificationClass, classificationSystemVersionModel) //
						.multivalue() //
						.unlocalizedValues("x1", "x2", "x3") //
						.build();

		final Feature singleLocalizedString = getBuilder(builder, FeatureTestBuilder.StringBuilder.class, SINGLE_LOCALIZED_STRING,
				modelService, classificationClass, classificationSystemVersionModel) //
						.localized(Locale.ENGLISH) //
						.localizedValues( //
								ImmutablePair.of(Locale.ENGLISH, Lists.newArrayList("thanks")), //
								ImmutablePair.of(Locale.GERMAN, Lists.newArrayList("danke")) //
						) //
						.build();

		final Feature singleEnum = getBuilder(builder, FeatureTestBuilder.EnumBuilder.class, SINGLE_ENUM, modelService,
				classificationClass, classificationSystemVersionModel) //
						.values(ArticleApprovalStatus.CHECK) //
						.build();

		final ProductModel productRef = new ProductModel();
		productRef.setCode("productRef");
		productRef.setCatalogVersion(catalogVersionModel);
		modelService.save(productRef);

		final Feature singleReference = getBuilder(builder, FeatureTestBuilder.ReferenceBuilder.class, SINGLE_REFERENCE,
				modelService, classificationClass, classificationSystemVersionModel) //
						.referenceType(typeService.getComposedTypeForCode(ProductModel._TYPECODE)) //
						.unlocalizedValues(productRef) //
						.build();

		return new FeatureList(singleBoolean, multiBoolean, singleNumberWithoutUnit, singleNumberWithUnit, multipleNumberWithUnit,
				rangeSingleNumberWithoutUnit, rangeMultipleNumberWithUnit, singleDate, singleRangeDate, singleString, multiString,
				singleLocalizedString, singleEnum, singleReference);
	}

	private static ClassificationAttributeUnitModel createUnit(final ModelService modelService,
			final ClassificationSystemVersionModel classificationSystemVersionModel, final String symbol)
	{
		final ClassificationAttributeUnitModel unit = modelService.create(ClassificationAttributeUnitModel.class);
		unit.setCode(symbol);
		unit.setName(symbol);
		unit.setSymbol(symbol);
		unit.setUnitType(symbol);
		unit.setSystemVersion(classificationSystemVersionModel);
		modelService.save(unit);
		return unit;
	}

}
