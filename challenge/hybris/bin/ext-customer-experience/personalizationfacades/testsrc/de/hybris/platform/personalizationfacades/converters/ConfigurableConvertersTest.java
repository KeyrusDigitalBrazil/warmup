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
package de.hybris.platform.personalizationfacades.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:personalizationfacades/test/converters-test-spring.xml")
public class ConfigurableConvertersTest
{
	private static final String SEGMENT = "segment";
	private static final String VARIATION = "variation";
	private static final String CLASSIFICATION = "classification";
	private static final String TRIGGER = "trigger";
	private static final String SEGMENT_TRIGGER = "segment_trigger";
	private static final String CL_TRIGGER = "classification_trigger";

	@Autowired
	ConfigurableConverter<VariationModel, VariationDto, String> variationConverter;
	@Autowired
	ConfigurableConverter<TriggerModel, TriggerDto, String> triggerConverter;
	@Autowired
	ConfigurableConverter<SegmentModel, SegmentDto, String> segmentConverter;
	@Autowired
	ConfigurableConverter<ClassificationModel, ClassificationDto, String> classificationConverter;

	private VariationModel variation;
	private TriggerModel trigger;
	private SegmentTriggerModel segmentTrigger;
	private ClassificationTriggerModel classificationTrigger;
	private SegmentModel segment;
	private ClassificationModel classification;

	@Before
	public void init()
	{
		variation = new VariationModel();
		trigger = new TriggerModel();
		segmentTrigger = new SegmentTriggerModel();
		classificationTrigger = new ClassificationTriggerModel();
		segment = new SegmentModel();
		classification = new ClassificationModel();

		variation.code = VARIATION;
		variation.triggers = new ArrayList<>();
		variation.triggers.add(trigger);
		variation.triggers.add(segmentTrigger);
		variation.triggers.add(classificationTrigger);

		segment.code = SEGMENT;
		segment.trigger = segmentTrigger;

		classification.code = CLASSIFICATION;
		classification.trigger = classificationTrigger;

		trigger.code = TRIGGER;
		trigger.variation = variation;

		segmentTrigger.code = SEGMENT_TRIGGER;
		segmentTrigger.segment = segment;
		segmentTrigger.variation = variation;

		classificationTrigger.code = CL_TRIGGER;
		classificationTrigger.classsification = classification;
		classificationTrigger.variation = variation;

	}

	@org.junit.Test
	public void fullVariationTest()
	{
		final VariationDto variationDto = variationConverter.convert(variation, Collections.singleton("FULL"));

		assertEquals(variation.code, variationDto.code);
		assertNotNull(variationDto.triggers);
		assertEquals(3, variationDto.triggers.size());

		final Iterator<TriggerDto> iterator = variationDto.triggers.iterator();
		TriggerDto trigger = iterator.next();
		assertNotNull(trigger);
		assertTrue(trigger.getClass() == TriggerDto.class);
		assertEquals(TRIGGER, trigger.code);
		assertNull(trigger.variation);

		trigger = iterator.next();
		assertNotNull(trigger);
		assertTrue(trigger.getClass() == SegmentTriggerDto.class);
		assertEquals(SEGMENT_TRIGGER, trigger.code);
		assertNull(trigger.variation);
		assertNotNull(((SegmentTriggerDto) trigger).segment);

		trigger = iterator.next();
		assertNotNull(trigger);
		assertTrue(trigger.getClass() == ClassificationTriggerDto.class);
		assertEquals(CL_TRIGGER, trigger.code);
		assertNull(trigger.variation);
		assertNotNull(((ClassificationTriggerDto) trigger).classsification);
	}

	@org.junit.Test
	public void defaultVariationTest()
	{
		final VariationDto variationDto = variationConverter.convert(variation);

		assertEquals(variation.code, variationDto.code);
		assertNull(variationDto.triggers);
	}

	@org.junit.Test
	public void baseVariationTest()
	{
		final VariationDto variationDto = variationConverter.convert(variation, Collections.singleton("BASE"));

		assertEquals(variation.code, variationDto.code);
		assertNull(variationDto.triggers);
	}

	public static interface TriggerPopulators
	{
		//nothing
	}

	public abstract static class Base
	{
		public String code;
	}

	public static class VariationDto extends Base
	{
		public Collection<TriggerDto> triggers;
	}

	public static class TriggerDto extends Base
	{
		public VariationDto variation;
	}

	public static class SegmentDto extends Base
	{
		public SegmentTriggerDto trigger;
	}

	public static class ClassificationDto extends Base
	{
		public ClassificationTriggerDto trigger;
	}

	public static class SegmentTriggerDto extends TriggerDto
	{
		public SegmentDto segment;
	}

	public static class ClassificationTriggerDto extends TriggerDto
	{
		public ClassificationDto classsification;
	}


	public static class VariationModel extends Base
	{
		public Collection<TriggerModel> triggers;
	}

	public static class TriggerModel extends Base
	{
		public VariationModel variation;
	}

	public static class SegmentModel extends Base
	{
		public SegmentTriggerModel trigger;
	}

	public static class ClassificationModel extends Base
	{
		public ClassificationTriggerModel trigger;
	}

	public static class SegmentTriggerModel extends TriggerModel
	{
		public SegmentModel segment;
	}

	public static class ClassificationTriggerModel extends TriggerModel
	{
		public ClassificationModel classsification;
	}


	public static class BasePopulator implements Populator<Base, Base>
	{
		@Override
		public void populate(final Base source, final Base target) throws ConversionException
		{
			target.code = source.code;
		}
	}

	public static class VariationPopulator implements Populator<VariationModel, VariationDto>
	{
		ConfigurableConverter<TriggerModel, TriggerDto, String> triggerConverter;

		@Override
		public void populate(final VariationModel source, final VariationDto target) throws ConversionException
		{
			target.triggers = source.triggers.stream().map(t -> triggerConverter.convert(t, Collections.singleton("FROM_VARIATION")))
					.collect(Collectors.toList());
		}

		public void setTriggerConverter(final ConfigurableConverter<TriggerModel, TriggerDto, String> triggerConverter)
		{
			this.triggerConverter = triggerConverter;
		}
	}

	public static class SegmentPopulator implements Populator<SegmentModel, SegmentDto>
	{
		ConfigurableConverter<SegmentTriggerModel, SegmentTriggerDto, String> segmentTriggerConverter;

		@Override
		public void populate(final SegmentModel source, final SegmentDto target) throws ConversionException
		{
			target.trigger = segmentTriggerConverter.convert(source.trigger, Collections.singleton("FROM_SEGMENT"));
		}

		public void setSegmentTriggerConverter(
				final ConfigurableConverter<SegmentTriggerModel, SegmentTriggerDto, String> segmentTriggerConverter)
		{
			this.segmentTriggerConverter = segmentTriggerConverter;
		}
	}

	public static class ClassificationPopulator implements Populator<ClassificationModel, ClassificationDto>
	{
		ConfigurableConverter<ClassificationTriggerModel, ClassificationTriggerDto, String> classificationTriggerConverter;

		@Override
		public void populate(final ClassificationModel source, final ClassificationDto target) throws ConversionException
		{
			target.trigger = classificationTriggerConverter.convert(source.trigger, Collections.singleton("FROM_CLASSIFICATION"));
		}

		public void setClassificationTriggerConverter(
				final ConfigurableConverter<ClassificationTriggerModel, ClassificationTriggerDto, String> classificationTriggerConverter)
		{
			this.classificationTriggerConverter = classificationTriggerConverter;
		}
	}

	public static class TriggerPopulator implements Populator<TriggerModel, TriggerDto>
	{
		ConfigurableConverter<VariationModel, VariationDto, String> variationConverter;

		@Override
		public void populate(final TriggerModel source, final TriggerDto target) throws ConversionException
		{
			target.variation = variationConverter.convert(source.variation, Collections.singleton("FROM_TRIGGER"));
		}

		public void setVariationConverter(final ConfigurableConverter<VariationModel, VariationDto, String> variationConverter)
		{
			this.variationConverter = variationConverter;
		}
	}

	public static class SegmentTriggerPopulator implements Populator<SegmentTriggerModel, SegmentTriggerDto>
	{
		ConfigurableConverter<SegmentModel, SegmentDto, String> segmentConverter;

		@Override
		public void populate(final SegmentTriggerModel source, final SegmentTriggerDto target) throws ConversionException
		{
			target.segment = segmentConverter.convert(source.segment, Collections.singleton("FROM_TRIGGER"));
		}

		public void setSegmentConverter(final ConfigurableConverter<SegmentModel, SegmentDto, String> segmentConverter)
		{
			this.segmentConverter = segmentConverter;
		}
	}

	public static class ClassificationTriggerPopulator implements Populator<ClassificationTriggerModel, ClassificationTriggerDto>
	{
		ConfigurableConverter<ClassificationModel, ClassificationDto, String> classificationConverter;

		@Override
		public void populate(final ClassificationTriggerModel source, final ClassificationTriggerDto target)
				throws ConversionException
		{
			target.classsification = classificationConverter.convert(source.classsification, Collections.singleton("FROM_TRIGGER"));
		}

		public void setClassificationConverter(
				final ConfigurableConverter<ClassificationModel, ClassificationDto, String> classificationConverter)
		{
			this.classificationConverter = classificationConverter;
		}

	}
}
