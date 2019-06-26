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

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.LocalizedFeature;
import de.hybris.platform.classification.features.UnlocalizedFeature;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.seed.ClassificationSystemTestDataCreator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Lists;



public class FeatureTestBuilder
{
	public <T extends Builder> Optional<T> of(final Class<T> t, final ModelService modelService,
			final ClassificationClassModel classificationClassModel,
			final ClassificationSystemVersionModel classificationSystemVersionModel, final String classificationAttributeName)
	{
		try
		{
			return Optional.of(t.getDeclaredConstructor(FeatureTestBuilder.class, ModelService.class, ClassificationClassModel.class,
					ClassificationSystemVersionModel.class, String.class).newInstance(new FeatureTestBuilder(), modelService,
							classificationClassModel, classificationSystemVersionModel, classificationAttributeName));
		}
		catch (final Exception e)
		{
			return Optional.empty();
		}
	}

	private static ClassAttributeAssignmentModel createClassificationAssignment(final ModelService modelService,
			final ClassificationClassModel classificationClassModel,
			final ClassificationSystemVersionModel classificationSystemVersionModel, final String classificationAttributeName)
	{
		final ClassificationAttributeModel attributeModel = new ClassificationSystemTestDataCreator(modelService)
				.createClassificationAttribute(classificationAttributeName, classificationSystemVersionModel);
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = modelService
				.create(ClassAttributeAssignmentModel.class);
		classAttributeAssignmentModel.setClassificationClass(classificationClassModel);
		classAttributeAssignmentModel.setClassificationAttribute(attributeModel);
		modelService.save(classAttributeAssignmentModel);
		return classAttributeAssignmentModel;
	}

	private static <T> Feature createLocalizedFeature(final ClassAttributeAssignmentModel attributeAssignmentModel,
			final Locale currentLocale, final Collection<Pair<Locale, List<T>>> localizedValues)
	{
		final Supplier<Map<Locale, List<FeatureValue>>> localizedFeatures = () -> CollectionUtils.emptyIfNull(localizedValues)
				.stream() //
				.collect(Collectors.toMap( //
						Pair::getLeft, //
						pair -> pair.getRight().stream().map(FeatureValue::new).collect(Collectors.toList()) //
		));

		return new LocalizedFeature(attributeAssignmentModel, localizedFeatures.get(), currentLocale);
	}

	private static <T> Feature createUnlocalizedFeature(final ClassAttributeAssignmentModel attributeAssignmentModel,
			final Collection<T> unlocalizedValues)
	{
		return createUnlocalizedFeature(attributeAssignmentModel, unlocalizedValues, null);
	}

	private static <T> Feature createUnlocalizedFeature(final ClassAttributeAssignmentModel attributeAssignmentModel,
			final Collection<T> unlocalizedValues, final ClassificationAttributeUnitModel unit)
	{
		final Supplier<List<FeatureValue>> unlocalizedFeatures = () -> CollectionUtils.emptyIfNull(unlocalizedValues).stream() //
				.map(value -> unit != null ? new FeatureValue(value, StringUtils.EMPTY, unit) : new FeatureValue(value)) //
				.collect(Collectors.toList());

		return new UnlocalizedFeature(attributeAssignmentModel, unlocalizedFeatures.get());
	}

	public interface Builder
	{
		Feature build();
	}

	public class ReferenceBuilder implements Builder
	{
		private ClassAttributeAssignmentModel classAttributeAssignmentModel;

		private final ClassificationAttributeTypeEnum attributeTypeEnum = ClassificationAttributeTypeEnum.REFERENCE;
		private ComposedTypeModel composedTypeModel;
		private boolean localized = false;
		private boolean multivalue = false;
		private Locale currentLocale;
		private Collection<Object> unlocalizedValues = new ArrayList<>();
		private Collection<Pair<Locale, List<Object>>> localizedValues = new ArrayList<>();

		public ReferenceBuilder(final ModelService modelService, final ClassificationClassModel classificationClassModel,
				final ClassificationSystemVersionModel classificationSystemVersionModel, final String classificationAttributeName)
		{
			this.classAttributeAssignmentModel = createClassificationAssignment(modelService, classificationClassModel,
					classificationSystemVersionModel, classificationAttributeName);
		}

		public ReferenceBuilder referenceType(final ComposedTypeModel composedTypeModel)
		{
			this.composedTypeModel = composedTypeModel;
			return this;
		}

		public ReferenceBuilder localized(final Locale currentLocale)
		{
			this.localized = true;
			this.currentLocale = currentLocale;
			return this;
		}

		public ReferenceBuilder multivalue()
		{
			this.multivalue = true;
			return this;
		}

		public ReferenceBuilder localizedValues(final Pair<Locale, List<Object>>... values)
		{
			this.localizedValues = Lists.newArrayList(values);
			return this;
		}

		public ReferenceBuilder unlocalizedValues(final Object... values)
		{
			this.unlocalizedValues = Lists.newArrayList(values);
			return this;
		}

		@Override
		public Feature build()
		{
			classAttributeAssignmentModel.setAttributeType(attributeTypeEnum);
			classAttributeAssignmentModel.setMultiValued(multivalue);
			classAttributeAssignmentModel.setLocalized(localized);
			classAttributeAssignmentModel.setReferenceType(composedTypeModel);
			return localized ? createLocalizedFeature(classAttributeAssignmentModel, currentLocale, localizedValues)
					: createUnlocalizedFeature(classAttributeAssignmentModel, unlocalizedValues);
		}
	}

	public class BooleanBuilder implements Builder
	{
		private final ClassAttributeAssignmentModel classAttributeAssignmentModel;

		private final ClassificationAttributeTypeEnum attributeTypeEnum = ClassificationAttributeTypeEnum.BOOLEAN;
		private boolean multivalue = false;
		private Collection<Boolean> values = new ArrayList<>();

		public BooleanBuilder(final ModelService modelService, final ClassificationClassModel classificationClassModel,
				final ClassificationSystemVersionModel classificationSystemVersionModel, final String classificationAttributeName)
		{
			this.classAttributeAssignmentModel = createClassificationAssignment(modelService, classificationClassModel,
					classificationSystemVersionModel, classificationAttributeName);
		}

		public BooleanBuilder multivalue()
		{
			this.multivalue = true;
			return this;
		}

		public BooleanBuilder values(final Boolean... values)
		{
			this.values = Lists.newArrayList(values);
			return this;
		}

		private Feature createFeature(final ClassAttributeAssignmentModel attributeAssignmentModel)
		{
			final Supplier<List<FeatureValue>> unlocalizedFeatures = () -> CollectionUtils.emptyIfNull(values).stream() //
					.map(FeatureValue::new) //
					.collect(Collectors.toList());

			return new UnlocalizedFeature(attributeAssignmentModel, unlocalizedFeatures.get());
		}

		@Override
		public Feature build()
		{
			classAttributeAssignmentModel.setAttributeType(attributeTypeEnum);
			classAttributeAssignmentModel.setMultiValued(multivalue);
			return createFeature(classAttributeAssignmentModel);
		}
	}

	public class StringBuilder implements Builder
	{
		private final ClassAttributeAssignmentModel classAttributeAssignmentModel;

		private final ClassificationAttributeTypeEnum attributeTypeEnum = ClassificationAttributeTypeEnum.STRING;
		private boolean localized = false;
		private boolean multivalue = false;
		private Locale currentLocale;
		private Collection<Object> unlocalizedValues = new ArrayList<>();
		private Collection<Pair<Locale, List<Object>>> localizedValues = new ArrayList<>();

		public StringBuilder(final ModelService modelService, final ClassificationClassModel classificationClassModel,
				final ClassificationSystemVersionModel classificationSystemVersionModel, final String classificationAttributeName)
		{
			this.classAttributeAssignmentModel = createClassificationAssignment(modelService, classificationClassModel,
					classificationSystemVersionModel, classificationAttributeName);
		}

		public StringBuilder localized(final Locale currentLocale)
		{
			this.localized = true;
			this.currentLocale = currentLocale;
			return this;
		}

		public StringBuilder multivalue()
		{
			this.multivalue = true;
			return this;
		}

		public StringBuilder localizedValues(final Pair<Locale, List<Object>>... values)
		{
			this.localizedValues = Lists.newArrayList(values);
			return this;
		}

		public StringBuilder unlocalizedValues(final Object... values)
		{
			this.unlocalizedValues = Lists.newArrayList(values);
			return this;
		}

		@Override
		public Feature build()
		{
			classAttributeAssignmentModel.setAttributeType(attributeTypeEnum);
			classAttributeAssignmentModel.setMultiValued(multivalue);
			classAttributeAssignmentModel.setLocalized(localized);
			return localized ? createLocalizedFeature(classAttributeAssignmentModel, currentLocale, localizedValues)
					: createUnlocalizedFeature(classAttributeAssignmentModel, unlocalizedValues);
		}
	}

	public class EnumBuilder implements Builder
	{
		private final ClassAttributeAssignmentModel classAttributeAssignmentModel;

		private final ClassificationAttributeTypeEnum attributeTypeEnum = ClassificationAttributeTypeEnum.ENUM;
		private boolean multivalue = false;
		private Collection<Object> values = new ArrayList<>();

		public EnumBuilder(final ModelService modelService, final ClassificationClassModel classificationClassModel,
				final ClassificationSystemVersionModel classificationSystemVersionModel, final String classificationAttributeName)
		{
			this.classAttributeAssignmentModel = createClassificationAssignment(modelService, classificationClassModel,
					classificationSystemVersionModel, classificationAttributeName);
		}

		public EnumBuilder multivalue()
		{
			this.multivalue = true;
			return this;
		}

		public EnumBuilder values(final Object... values)
		{
			this.values = Lists.newArrayList(values);
			return this;
		}

		@Override
		public Feature build()
		{
			classAttributeAssignmentModel.setAttributeType(attributeTypeEnum);
			classAttributeAssignmentModel.setMultiValued(multivalue);
			return createUnlocalizedFeature(classAttributeAssignmentModel, values);
		}
	}

	public class DateBuilder implements Builder
	{
		private final ClassAttributeAssignmentModel classAttributeAssignmentModel;

		private final ClassificationAttributeTypeEnum attributeTypeEnum = ClassificationAttributeTypeEnum.DATE;
		private boolean range = false;
		private boolean multivalue = false;
		private Collection<Date> values = new ArrayList<>();

		public DateBuilder(final ModelService modelService, final ClassificationClassModel classificationClassModel,
				final ClassificationSystemVersionModel classificationSystemVersionModel, final String classificationAttributeName)
		{
			this.classAttributeAssignmentModel = createClassificationAssignment(modelService, classificationClassModel,
					classificationSystemVersionModel, classificationAttributeName);
		}

		public DateBuilder range()
		{
			this.range = true;
			return this;
		}

		public DateBuilder multivalue()
		{
			this.multivalue = true;
			return this;
		}

		public DateBuilder values(final Date... values)
		{
			this.values = Lists.newArrayList(values);
			return this;
		}

		@Override
		public Feature build()
		{
			classAttributeAssignmentModel.setAttributeType(attributeTypeEnum);
			classAttributeAssignmentModel.setMultiValued(multivalue);
			classAttributeAssignmentModel.setRange(range);
			return createUnlocalizedFeature(classAttributeAssignmentModel, values);
		}
	}

	public class NumberBuilder implements Builder
	{
		private final ClassAttributeAssignmentModel classAttributeAssignmentModel;

		private final ClassificationAttributeTypeEnum attributeTypeEnum = ClassificationAttributeTypeEnum.NUMBER;
		private boolean range = false;
		private boolean multivalue = false;
		private ClassificationAttributeUnitModel unit;
		private Collection<Number> values = new ArrayList<>();

		public NumberBuilder(final ModelService modelService, final ClassificationClassModel classificationClassModel,
				final ClassificationSystemVersionModel classificationSystemVersionModel, final String classificationAttributeName)
		{
			this.classAttributeAssignmentModel = createClassificationAssignment(modelService, classificationClassModel,
					classificationSystemVersionModel, classificationAttributeName);
		}

		public NumberBuilder range()
		{
			this.range = true;
			return this;
		}

		public NumberBuilder multivalue()
		{
			this.multivalue = true;
			return this;
		}

		public NumberBuilder values(final Number... values)
		{
			this.values = Lists.newArrayList(values);
			return this;
		}

		public NumberBuilder unit(final ClassificationAttributeUnitModel unit)
		{
			this.unit = unit;
			return this;
		}

		@Override
		public Feature build()
		{
			classAttributeAssignmentModel.setAttributeType(attributeTypeEnum);
			classAttributeAssignmentModel.setMultiValued(multivalue);
			classAttributeAssignmentModel.setRange(range);
			classAttributeAssignmentModel.setUnit(unit);
			return createUnlocalizedFeature(classAttributeAssignmentModel, values, unit);
		}
	}

}
