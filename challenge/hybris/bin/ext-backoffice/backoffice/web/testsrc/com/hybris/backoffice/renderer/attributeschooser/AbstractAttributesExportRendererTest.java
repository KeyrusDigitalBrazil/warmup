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
package com.hybris.backoffice.renderer.attributeschooser;

import static com.hybris.backoffice.excel.ExcelConstants.EXCEL_FORM_PROPERTY;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.c2l.LanguageModel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Ignore;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;

import com.google.common.collect.Lists;
import com.hybris.backoffice.attributechooser.AttributeChooserForm;
import com.hybris.backoffice.attributechooser.AttributesChooserConfig;
import com.hybris.backoffice.excel.export.wizard.ExcelExportWizardForm;
import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.search.data.pageable.Pageable;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;



/**
 * Utility superclass for unit tests of implementations of {@link AbstractAttributesExportRenderer}.
 */
@Ignore
public class AbstractAttributesExportRendererTest
{
	public static final String TYPE_NAME = "Product";

	protected Component parent;
	protected Map<String, String> params;
	protected ExcelExportWizardForm form;
	protected WidgetInstanceManager wim;

	@Mock
	protected Pageable pageable;
	@Mock
	protected LanguageModel english;
	@Mock
	protected LanguageModel german;

	@Mock
	protected PermissionFacade permissionFacade;
	@Mock
	protected CockpitLocaleService cockpitLocaleService;
	@Mock
	protected NotificationService notificationService;
	@Mock
	protected WidgetComponentRenderer<Component, AttributesChooserConfig, AttributeChooserForm> attributesChooserRenderer;


	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();

		parent = new Div();
		params = new HashMap<>();
		form = new ExcelExportWizardForm();
		form.setPageable(pageable);
		wim = CockpitTestUtil.mockWidgetInstanceManager();
		wim.getModel().setValue(EXCEL_FORM_PROPERTY, form);
		when(pageable.getTypeCode()).thenReturn(TYPE_NAME);

		// locales
		when(english.getActive()).thenReturn(true);
		when(english.getIsocode()).thenReturn("en");
		when(german.getActive()).thenReturn(true);
		when(german.getIsocode()).thenReturn("de");

		// initialize mocks with locales
		final Set<Locale> locales = Lists.newArrayList(english, german).stream()//
				.map(lang -> Locale.forLanguageTag(lang.getIsocode()))//
				.collect(Collectors.toSet());
		when(permissionFacade.getAllReadableLocalesForCurrentUser()).thenReturn(locales);
		final Locale currentLocale = Locale.forLanguageTag(english.getIsocode());
		when(cockpitLocaleService.getCurrentLocale()).thenReturn(currentLocale);
	}


	protected AttributeChooserForm captureAttributesChooserForm()
	{
		final ArgumentCaptor<AttributeChooserForm> captor = ArgumentCaptor.forClass(AttributeChooserForm.class);
		verify(attributesChooserRenderer).render(eq(parent), any(), captor.capture(), any(), eq(wim));
		return captor.getValue();
	}

}
