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
package com.hybris.backoffice.excel.export.wizard.renderer;

import static com.hybris.backoffice.excel.ExcelConstants.EXCEL_FORM_PROPERTY;
import static com.hybris.backoffice.excel.ExcelConstants.NOTIFICATION_EVENT_TYPE_MISSING_FORM;
import static com.hybris.backoffice.excel.ExcelConstants.NOTIFICATION_SOURCE_EXCEL_EXPORT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.hybris.backoffice.attributechooser.Attribute;
import com.hybris.backoffice.attributechooser.AttributeChooserForm;
import com.hybris.backoffice.excel.template.filter.ExcelFilter;
import com.hybris.backoffice.excel.translators.ExcelTranslatorRegistry;
import com.hybris.backoffice.renderer.attributeschooser.AbstractAttributesExportRendererTest;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level;
import com.hybris.cockpitng.labels.LabelService;


@RunWith(MockitoJUnitRunner.class)
public class ExcelExportRendererTest extends AbstractAttributesExportRendererTest
{
	@Mock
	private ExcelFilter<AttributeDescriptorModel> requiredFilters;
	@Mock
	private ExcelFilter<AttributeDescriptorModel> supportedFilters;
	@Mock
	private TypeService typeService;
	@Mock
	private LabelService labelService;
	@Mock
	private ExcelTranslatorRegistry excelTranslatorRegistry;

	@Mock
	private AttributeDescriptorModel code;
	@Mock
	private AttributeDescriptorModel name;

	@InjectMocks
	private ExcelExportRenderer renderer;


	@Override
	@Before
	public void setUp()
	{
		super.setUp();

		when(requiredFilters.test(any())).thenReturn(true);
		when(supportedFilters.test(any())).thenReturn(true);
		when(code.getQualifier()).thenReturn("code");
		when(code.getReadable()).thenReturn(true);
		when(code.getWritable()).thenReturn(true);
		when(name.getQualifier()).thenReturn("name");
		when(name.getReadable()).thenReturn(true);
		when(name.getWritable()).thenReturn(true);
		when(name.getLocalized()).thenReturn(true);

		when(labelService.getObjectLabel(any())).thenReturn("label");

		final Set<AttributeDescriptorModel> attributes = Sets.newHashSet(code, name);
		when(typeService.getAttributesForModifiers(any(), any())).thenReturn(attributes);
		when(typeService.getAttributeDescriptor(TYPE_NAME, "code")).thenReturn(code);
		when(typeService.getAttributeDescriptor(TYPE_NAME, "name")).thenReturn(name);

		when(excelTranslatorRegistry.canHandle(any())).thenReturn(true);
		when(permissionFacade.canReadProperty(any(), any())).thenReturn(true);
	}

	@Test
	public void shouldCreateLocalizedChildren()
	{
		// when
		renderer.render(parent, null, params, null, wim);

		// then
		final AttributeChooserForm form = captureAttributesChooserForm();

		final Set<Attribute> chosenAttributes = form.getChosenAttributes();
		assertThat(chosenAttributes).hasSize(2);
		assertThat(chosenAttributes.stream().map(Attribute::getQualifier)).containsOnly(code.getQualifier(), name.getQualifier());

		final Optional<Attribute> chosenName = chosenAttributes.stream()
				.filter(attr -> attr.getQualifier().equals(this.name.getQualifier())).findAny();
		assertThat(chosenName.get().getSubAttributes()).hasSize(1);
		assertThat(chosenName.get().getSubAttributes().iterator().next().getIsoCode()).isEqualTo(english.getIsocode());

		final Set<Attribute> availableAttributes = form.getAvailableAttributes();
		assertThat(availableAttributes).hasSize(1);
		assertThat(availableAttributes.stream().map(Attribute::getQualifier)).containsOnly(name.getQualifier());

		final Optional<Attribute> availableName = availableAttributes.stream()
				.filter(attr -> attr.getQualifier().equals(this.name.getQualifier())).findAny();
		assertThat(availableName.get().getSubAttributes()).hasSize(1);
		assertThat(availableName.get().getSubAttributes().iterator().next().getIsoCode()).isEqualTo(german.getIsocode());
	}

	@Test
	public void shouldNotifyUserAboutMissingForm()
	{
		// given
		wim.getModel().setValue(EXCEL_FORM_PROPERTY, null);

		// when
		renderer.render(null, null, new HashMap<>(), null, wim);

		// then
		verify(notificationService).notifyUser(NOTIFICATION_SOURCE_EXCEL_EXPORT, NOTIFICATION_EVENT_TYPE_MISSING_FORM,
				Level.FAILURE);
	}
}
