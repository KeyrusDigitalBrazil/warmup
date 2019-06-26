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
package com.hybris.backoffice.bulkedit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hybris.backoffice.attributechooser.Attribute;
import com.hybris.backoffice.attributechooser.AttributeChooserForm;
import com.hybris.cockpitng.type.ObjectValueService;
import com.hybris.cockpitng.validation.LocalizationAwareValidationHandler;
import com.hybris.cockpitng.validation.LocalizedQualifier;
import com.hybris.cockpitng.validation.ValidationContext;
import com.hybris.cockpitng.validation.ValidationHandler;
import com.hybris.cockpitng.validation.impl.DefaultValidationInfo;
import com.hybris.cockpitng.validation.model.ValidationInfo;
import com.hybris.cockpitng.validation.model.ValidationSeverity;


@RunWith(MockitoJUnitRunner.class)
public class DefaultBulkEditValidationHelperTest
{
	@Mock
	private ObjectValueService objectValueService;
	@Mock
	private LocalizationAwareValidationHandler localizationAwareValidationHandler;
	@Mock
	private CommonI18NService commonI18NService;
	@InjectMocks
	private DefaultBulkEditValidationHelper helper;
	private BulkEditForm bulkEditForm;
	@Mock
	private ItemModel templateObject;
	private Set<Attribute> chooserAttributes;
	@Mock
	private ValidationContext validationContext;

	@Before
	public void setUp()
	{
		chooserAttributes = new HashSet<>();
		final AttributeChooserForm chooserForm = new AttributeChooserForm();
		chooserForm.setChosenAttributes(chooserAttributes);
		bulkEditForm = new BulkEditForm();
		bulkEditForm.setAttributesForm(chooserForm);
		bulkEditForm.setTemplateObject(templateObject);
	}

	@Test
	public void checkAttributesWithValueValidatable()
	{
		//given
		final HashMap<Locale, String> localizedMap = new HashMap<>();
		localizedMap.put(Locale.ENGLISH, "a");
		mockAttributeWithValue("notEmptyLocalizedMap", localizedMap);
		final HashMap<String, String> mapValue = new HashMap<>();
		mapValue.put("a", "a");
		mockAttributeWithValue("notEmptyMap", mapValue);
		mockAttributeWithValue("notEmptyCollection", Lists.newArrayList(1));
		mockAttributeWithValue("notEmpty", 1);
		mockAttributeWithValue("emptyLocalizedMap", new HashMap<>());
		mockAttributeWithValue("emptyMap", new HashMap<>());
		mockAttributeWithValue("emptyCollection", new ArrayList<>());
		mockAttributeWithValue("empty", null);
		//when
		final Set<String> validatableProperties = helper.getValidatableProperties(bulkEditForm);
		//then
		assertThat(validatableProperties).containsOnly("notEmptyLocalizedMap", "notEmptyMap", "notEmptyCollection", "notEmpty");
	}

	@Test
	public void checkAttributeToClearIsValidatable()
	{
		//given
		mockAttributeWithValue("empty", null);
		bulkEditForm.addQualifierToClear("empty");
		//when
		final Set<String> validatableProperties = helper.getValidatableProperties(bulkEditForm);
		//then
		assertThat(validatableProperties).containsOnly("empty");
	}

	@Test
	public void testValidationProxyValidatesAttributesWithValue()
	{
		//given
		final HashMap<Locale, String> localizedMap = new HashMap<>();
		localizedMap.put(Locale.ENGLISH, "a");
		mockAttributeWithValue("notEmptyLocalizedMap", localizedMap);
		final HashMap<String, String> mapValue = new HashMap<>();
		mapValue.put("a", "a");
		mockAttributeWithValue("notEmptyMap", mapValue);
		mockAttributeWithValue("notEmptyCollection", Lists.newArrayList(1));
		mockAttributeWithValue("notEmpty", 1);
		mockAttributeWithValue("emptyLocalizedMap", new HashMap<>());
		mockAttributeWithValue("emptyMap", new HashMap<>());
		mockAttributeWithValue("emptyCollection", new ArrayList<>());
		mockAttributeWithValue("empty", null);

		//when
		final ValidationHandler proxyValidationHandler = helper.createProxyValidationHandler(bulkEditForm);
		proxyValidationHandler.validate(templateObject, Lists.newArrayList("notEmptyLocalizedMap", "notEmptyMap",
				"notEmptyCollection", "notEmpty", "emptyLocalizedMap", "emptyMap", "emptyCollection", "empty"), validationContext);

		//then
		verify(localizationAwareValidationHandler).validate(eq(templateObject),
				argThat(new ArgumentMatcher<Collection<LocalizedQualifier>>()
				{
					@Override
					public boolean matches(final Object o)
					{
						final ArrayList<LocalizedQualifier> correctQualifiers = Lists.newArrayList(
								new LocalizedQualifier("notEmptyLocalizedMap"), new LocalizedQualifier("notEmptyMap"),
								new LocalizedQualifier("notEmptyCollection"), new LocalizedQualifier("notEmpty"));
						return ((Collection) o).size() == correctQualifiers.size() && ((Collection) o).containsAll(correctQualifiers);
					}
				}), same(validationContext));
	}

	private void mockAttributeWithValue(final String qualifier, final Object value)
	{
		chooserAttributes.add(new Attribute(qualifier, qualifier, false));
		when(objectValueService.getValue(qualifier, templateObject)).thenReturn(value);
	}

	@Test
	public void shouldValidateAllAttributes()
	{
		//given
		final ProductModel product = mock(ProductModel.class);
		bulkEditForm.setItemsToEdit(Lists.newArrayList(product));

		mockAttributeWithValue("myAttribute", 1);

		bulkEditForm.setValidateAllAttributes(true);

		final DefaultValidationInfo validationError = new DefaultValidationInfo();
		validationError.setValidationSeverity(ValidationSeverity.ERROR);
		when(localizationAwareValidationHandler.validate(product)).thenReturn(Lists.newArrayList(validationError));

		//when
		final Map<Object, List<ValidationInfo>> result = helper.validateModifiedItems(bulkEditForm, ValidationSeverity.WARN);

		//then
		assertThat(result).isNotEmpty();
		assertThat(result.get(product)).isNotEmpty();
		assertThat(result.get(product)).containsOnly(validationError);
	}

	@Test
	public void shouldValidateSelectedAttributes()
	{
		//given
		final ProductModel product = mock(ProductModel.class);
		bulkEditForm.setItemsToEdit(Lists.newArrayList(product));

		mockAttributeWithValue("myAttribute", 1);

		final DefaultValidationInfo validationError = new DefaultValidationInfo();
		validationError.setValidationSeverity(ValidationSeverity.ERROR);
		when(localizationAwareValidationHandler.validate(eq(product), (Collection<LocalizedQualifier>) any(List.class),
				any(ValidationContext.class))).thenReturn(Lists.newArrayList(validationError));

		//when
		final Map<Object, List<ValidationInfo>> result = helper.validateModifiedItems(bulkEditForm, ValidationSeverity.WARN);

		//then
		assertThat(result).isNotEmpty();
		assertThat(result.get(product)).isNotEmpty();
		assertThat(result.get(product)).containsOnly(validationError);
	}

	@Test
	public void shouldGetValidatablePropertiesWithLocales()
	{
		// given
		final Attribute firstAttribute = mock(Attribute.class);
		given(firstAttribute.getQualifier()).willReturn("firstAttribute");

		final Attribute secondAttributeSubAttribute = mock(Attribute.class);
		given(secondAttributeSubAttribute.getIsoCode()).willReturn("en");

		final Attribute secondAttribute = mock(Attribute.class);
		given(secondAttribute.getQualifier()).willReturn("secondAttribute");
		given(secondAttribute.getSubAttributes()).willReturn(Sets.newHashSet(secondAttributeSubAttribute));

		final Attribute thirdAttribute = mock(Attribute.class);
		given(thirdAttribute.getQualifier()).willReturn("thirdAttribute");

		given(commonI18NService.getLocaleForIsoCode("en")).willReturn(Locale.ENGLISH);

		given(objectValueService.getValue("firstAttribute", bulkEditForm.getTemplateObject())).willReturn("firstAttributeValue");
		given(objectValueService.getValue("secondAttribute", bulkEditForm.getTemplateObject()))
				.willReturn(ImmutableMap.of("key", "value"));
		given(objectValueService.getValue("thirdAttribute", bulkEditForm.getTemplateObject())).willReturn(null);

		final AttributeChooserForm attributeChooserForm = mock(AttributeChooserForm.class);
		given(attributeChooserForm.getChosenAttributes())
				.willReturn(Sets.newHashSet(firstAttribute, secondAttribute, thirdAttribute));

		bulkEditForm.setAttributesForm(attributeChooserForm);

		// when
		final Collection<LocalizedQualifier> localizedQualifiers = helper.getValidatablePropertiesWithLocales(bulkEditForm);

		// then:
		// as first attribute is not null but does not have sub-attributes for locales
		assertThat(localizedQualifiers).contains(new LocalizedQualifier("firstAttribute"));
		// as second attribute is not null and has sub-attribute for English
		assertThat(localizedQualifiers).contains(new LocalizedQualifier("secondAttribute", Collections.singleton(Locale.ENGLISH)));
		// as third attribute is null
		assertThat(localizedQualifiers).doesNotContain(new LocalizedQualifier("thirdAttribute"));
	}
}
