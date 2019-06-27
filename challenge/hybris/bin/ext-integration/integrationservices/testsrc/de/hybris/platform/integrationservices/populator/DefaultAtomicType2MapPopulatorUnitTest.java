/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.integrationservices.populator;

import static de.hybris.platform.integrationservices.model.BaseMockAttributeDescriptorModelBuilder.attributeDescriptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Maps;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAtomicType2MapPopulatorUnitTest
{
	private static final String ATTR_NAME = "codex";
	private static final String QUALIFIER = "qualifier";
	@InjectMocks
	private DefaultAtomicType2MapPopulator populator;
	@Mock
	private ItemModel itemModel;
	@Mock
	private IntegrationObjectItemModel integrationObjectItemModel;
	@Mock
	private IntegrationObjectItemAttributeModel integrationObjectItemAttributeModel;
	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private ComposedTypeModel composedTypeModel;
	@Mock
	private ModelService modelService;

	private Locale locale = Locale.getDefault();

	@Before
	public void setup()
	{
		when(integrationObjectItemModel.getAttributes()).thenReturn(Collections.singleton(integrationObjectItemAttributeModel));
		when(integrationObjectItemModel.getType()).thenReturn(composedTypeModel);
		when(integrationObjectItemAttributeModel.getAttributeDescriptor()).thenReturn(attributeDescriptor);
		when(integrationObjectItemAttributeModel.getAttributeName()).thenReturn(ATTR_NAME);
		when(composedTypeModel.getCode()).thenReturn("Product");
		when(attributeDescriptor.getQualifier()).thenReturn(QUALIFIER);
		when(attributeDescriptor.getAttributeType()).thenReturn(new AtomicTypeModel());
		when(itemModel.getItemtype()).thenReturn("Product");
	}

	@After
	public void tearDown()
	{
		Locale.setDefault(locale);
	}

	@Test
	public void shouldPopulateToMap()
	{
		givenPropertyIsString();
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat(map).containsOnly(entry(ATTR_NAME, "test"));
	}

	@Test
	public void shouldNotPopulate_whenNoAttributeValue()
	{
		givenPropertyIsString();
		when(modelService.getAttributeValue(itemModel, QUALIFIER)).thenReturn(null);
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat(map).isEmpty();
	}

	@Test
	public void shouldIllegalArgumentException()
	{
		givenPropertyIsString();
		final Map<String, Object> map = Maps.newHashMap();
		assertThatThrownBy(() -> populator.populate(new ItemToMapConversionContext(), map))
					.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotPopulate()
	{
		givenPropertyIsString();
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		when(attributeDescriptor.getAttributeType()).thenReturn(new ComposedTypeModel());
		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat(map).isEmpty();
	}

	@Test
	public void shouldNotApplicable()
	{
		final AttributeDescriptorModel attributeDescriptorModel = attributeDescriptor()
				.withType(ComposedTypeModel.class).build();
		assertThat(populator.isApplicable(attributeDescriptorModel)).isFalse();
	}

	@Test
	public void shouldPopulateDate()
	{
		final Date date = givenPropertyIsDate();
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat(map).containsOnly(entry(ATTR_NAME, "/Date("+date.getTime()+")/"));
	}

	@Test
	public void shouldPopulateDouble()
	{
		givenPropertyIsDouble();
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat(map).containsOnly(entry(ATTR_NAME, 1.0D));
	}

	@Test
	public void shouldPopulateInt()
	{
		givenPropertyIsInt();
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat(map).containsOnly(entry(ATTR_NAME, 1));
	}

	@Test
	public void shouldPopulateLong()
	{
		givenPropertyIsLong();
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat(map).containsOnly(entry(ATTR_NAME, "1"));
	}

	@Test
	public void shouldPopulateBigDecimal()
	{
		givenPropertyIsBigDecimal();
		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat(map).containsOnly(entry(ATTR_NAME, "123.46E3"));
	}

	@Test
	public void shouldPopulateBigDecimalDifferentLocale()
	{
		givenPropertyIsBigDecimal();
		Locale.setDefault(Locale.GERMAN);  // 12.55 is written 12,55

		final ItemToMapConversionContext itemToMapConversionContext = giveConversionContext();
		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(itemToMapConversionContext, map);

		assertThat(map).containsOnly(entry(ATTR_NAME, "123.46E3"));
	}

	private ItemToMapConversionContext giveConversionContext()
	{
		final ItemToMapConversionContext context = new ItemToMapConversionContext();
		context.setItemModel(itemModel);
		context.setIntegrationObjectItemModel(integrationObjectItemModel);
		return context;
	}

	private void givenPropertyIsString()
	{
		when(modelService.getAttributeValue(itemModel, QUALIFIER)).thenReturn("test");
	}

	private void givenPropertyIsDouble()
	{
		when(modelService.getAttributeValue(itemModel, QUALIFIER)).thenReturn(1.0D);
	}

	private void givenPropertyIsInt()
	{
		when(modelService.getAttributeValue(itemModel, QUALIFIER)).thenReturn(1);
	}

	private void givenPropertyIsLong()
	{
		when(modelService.getAttributeValue(itemModel, QUALIFIER)).thenReturn(1L);
	}

	private Date givenPropertyIsDate()
	{
		final Date date = new Date();
		when(modelService.getAttributeValue(itemModel, QUALIFIER)).thenReturn(date);
		return date;
	}

	private void givenPropertyIsBigDecimal()
	{
		when(modelService.getAttributeValue(itemModel, QUALIFIER)).thenReturn(BigDecimal.valueOf(123456));
	}
}
