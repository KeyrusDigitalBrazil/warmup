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
package com.hybris.backoffice.excel.export.wizard;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.internal.i18n.LocalizationService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.hybris.backoffice.attributechooser.Attribute;
import com.hybris.backoffice.excel.ExcelConstants;
import com.hybris.backoffice.excel.data.ExcelExportParams;
import com.hybris.backoffice.excel.data.SelectedAttribute;
import com.hybris.backoffice.excel.exporting.ExcelExportPreProcessor;
import com.hybris.backoffice.excel.exporting.ExcelExportService;
import com.hybris.backoffice.excel.exporting.ExcelExportWorkbookPostProcessor;
import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.search.data.pageable.Pageable;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


@RunWith(MockitoJUnitRunner.class)
public class ExcelExportHandlerTest
{
	public static final String TEST_FILE_NAME = "TestFileName";

	@Mock
	private ExcelExportService excelExportService;
	@Mock
	private NotificationService notificationService;
	@Mock
	private TypeService typeService;
	@Mock
	private FlowActionHandlerAdapter adapter;
	@Mock
	private LocalizationService localizationService;
	@Mock
	private ExcelExportWorkbookPostProcessor excelExportWorkbookPostProcessor;
	@Mock
	private Workbook exportedWorkBook;
	@Mock
	ExcelExportPreProcessor excelExportPreProcessor;
	@InjectMocks
	@Spy
	private ExcelExportHandler handler;

	private ExcelExportWizardForm form;
	private WidgetInstanceManager wim;

	@Before
	public void setUp()
	{
		wim = CockpitTestUtil.mockWidgetInstanceManager();
		form = new ExcelExportWizardForm();
		form.setTypeCode(ProductModel._TYPECODE);
		wim.getModel().put(ExcelConstants.EXCEL_FORM_PROPERTY, form);
		when(adapter.getWidgetInstanceManager()).thenReturn(wim);
		doNothing().when(handler).triggerDownloading(any(Workbook.class), anyString());
		doReturn(TEST_FILE_NAME).when(handler).getFilename(form);
		when(excelExportService.exportData(anyString(), anyList())).thenReturn(exportedWorkBook);
		when(excelExportService.exportData(anyList(), anyList())).thenReturn(exportedWorkBook);
		doReturn(200).when(handler).getExportAttributesMaxCount();
	}

	@Test
	public void shouldTriggerTypeExportWhenPageableEmpty()
	{
		// given
		final List<SelectedAttribute> attributesToExport = mockSelectedAttributes(ProductModel._TYPECODE, ProductModel.CODE,
				ProductModel.NAME);

		final Pageable pageable = mockPageable(Collections.emptyList(), ProductModel._TYPECODE);
		form.setPageable(pageable);
		form.getAttributesForm().setChosenAttributes(toAttributes(attributesToExport));
		final ExcelExportParams expectedExcelExportParams = new ExcelExportParams(pageable.getAllResults(), attributesToExport,
				Collections.emptyList());

		given(excelExportPreProcessor.process(any(ExcelExportParams.class))).willReturn(expectedExcelExportParams);

		// when
		handler.perform(new CustomType(), adapter, Collections.emptyMap());

		// then
		verify(excelExportService).exportData(ProductModel._TYPECODE, attributesToExport);
		verify(handler).triggerDownloading(exportedWorkBook, TEST_FILE_NAME);
		verify(adapter).done();
	}

	@Test
	public void shouldTriggerExportWhenPageableIsNotEmpty()
	{
		// given
		final List<SelectedAttribute> attributesToExport = mockSelectedAttributes(ProductModel._TYPECODE, ProductModel.CODE,
				ProductModel.NAME);
		final Pageable pageable = mockPageable(Lists.newArrayList(1, 2), ProductModel._TYPECODE);
		form.setPageable(pageable);
		form.getAttributesForm().setChosenAttributes(toAttributes(attributesToExport));
		final ExcelExportParams expectedExcelExportParams = new ExcelExportParams(pageable.getAllResults(), attributesToExport,
				Collections.emptyList());
		given(excelExportPreProcessor.process(any(ExcelExportParams.class))).willReturn(expectedExcelExportParams);

		// when
		handler.perform(new CustomType(), adapter, Collections.emptyMap());

		// then
		verify(excelExportService).exportData(pageable.getAllResults(), attributesToExport);
		verify(handler).triggerDownloading(exportedWorkBook, TEST_FILE_NAME);
		verify(adapter).done();
	}

	@Test
	public void shouldNotTriggerDownloadWhenExportedWorkbookIsEmpty()
	{
		// given
		final List<SelectedAttribute> attributesToExport = mockSelectedAttributes(ProductModel._TYPECODE, ProductModel.CODE,
				ProductModel.NAME);
		final Pageable pageable = mockPageable(Lists.newArrayList(1, 2), ProductModel._TYPECODE);
		form.setPageable(pageable);
		form.getAttributesForm().setChosenAttributes(toAttributes(attributesToExport));
		final ExcelExportParams expectedExcelExportParams = new ExcelExportParams(pageable.getAllResults(), attributesToExport,
				Collections.emptyList());

		given(excelExportService.exportData(anyListOf(ItemModel.class), anyListOf(SelectedAttribute.class))).willReturn(null);
		given(excelExportPreProcessor.process(any(ExcelExportParams.class))).willReturn(expectedExcelExportParams);

		// when
		handler.perform(new CustomType(), adapter, Collections.emptyMap());

		// then
		verify(excelExportService).exportData(pageable.getAllResults(), attributesToExport);
		verify(handler, never()).triggerDownloading(exportedWorkBook, TEST_FILE_NAME);
		verify(adapter, never()).done();
	}

	@Test
	public void shouldNotTriggerDownloadWhenTooManyAttributesAreSelected()
	{
		final List<SelectedAttribute> attributesToExport = mockSelectedAttributes(ProductModel._TYPECODE, ProductModel.CODE,
				ProductModel.NAME);
		final Pageable pageable = mockPageable(Lists.newArrayList(1, 2), ProductModel._TYPECODE);
		form.setPageable(pageable);
		form.getAttributesForm().setChosenAttributes(toAttributes(attributesToExport));
		final int exportAttributesMaxCount = 1;
		doReturn(exportAttributesMaxCount).when(handler).getExportAttributesMaxCount();

		handler.perform(new CustomType(), adapter, Collections.emptyMap());

		verify(notificationService).notifyUser(ExcelConstants.NOTIFICATION_SOURCE_EXCEL_EXPORT,
				ExcelConstants.NOTIFICATION_EVENT_TYPE_ATTRIBUTES_MAX_COUNT_EXCEEDED, NotificationEvent.Level.FAILURE,
				exportAttributesMaxCount, attributesToExport.size());
		verify(excelExportService, never()).exportData(anyList(), any());
		verify(excelExportService, never()).exportData(anyString(), any());
		verify(handler, never()).triggerDownloading(exportedWorkBook, TEST_FILE_NAME);
		verify(adapter, never()).done();
	}

	@Test
	public void shouldNotTriggerDownloadWhenSelectedAttributesAreEmptyAndActionIsNotRelatedToTemplate()
	{
		// given
		form.getAttributesForm().setChosenAttributes(Collections.emptySet());
		form.setExportTemplate(false);

		// when
		handler.perform(new CustomType(), adapter, Collections.emptyMap());

		// then
		verify(notificationService).notifyUser(ExcelConstants.NOTIFICATION_SOURCE_EXCEL_EXPORT,
				ExcelConstants.NOTIFICATION_EVENT_TYPE_MISSING_ATTRIBUTES, NotificationEvent.Level.FAILURE);
		verify(excelExportService, never()).exportTemplate(any());
		verify(excelExportService, never()).exportData(anyList(), any());
		verify(excelExportService, never()).exportData(anyString(), any());
		verify(handler, never()).triggerDownloading(exportedWorkBook, TEST_FILE_NAME);
		verify(adapter, never()).done();
	}

	@Test
	public void shouldAdditionalAttributesNotBeExtractedWhenClassificationIsNotIncluded()
	{
		// given
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelExportHandler.PARAM_EXCEL_INCLUDE_CLASSIFICATION, Boolean.FALSE.toString());
		form.getAttributesForm().setChosenAttributes(Collections.emptySet());
		form.setExportTemplate(true);
		doReturn(-1).when(handler).getExportAttributesMaxCount();

		// when
		handler.perform(new CustomType(), adapter, params);

		// then
		then(handler).should(never()).getAdditionalAttributes(form);
	}

	@Test
	public void shouldAdditionalAttributesBeExtractedWhenClassificationIsIncluded()
	{
		// given
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelExportHandler.PARAM_EXCEL_INCLUDE_CLASSIFICATION, Boolean.TRUE.toString());
		form.getAttributesForm().setChosenAttributes(Collections.emptySet());
		form.setExportTemplate(true);
		doReturn(-1).when(handler).getExportAttributesMaxCount();

		// when
		handler.perform(new CustomType(), adapter, params);

		// then
		then(handler).should().getAdditionalAttributes(form);
	}

	@Test
	public void shouldTriggerExportTemplateWhenActionIsRelatedToTemplate()
	{
		// given
		final List<SelectedAttribute> attributesToExport = mockSelectedAttributes(ProductModel._TYPECODE, ProductModel.CODE,
				ProductModel.NAME);

		form.setExportTemplate(true);
		final Pageable pageable = mockPageable(Lists.newArrayList(1, 2), ProductModel._TYPECODE);
		form.setPageable(pageable);
		final ExcelExportParams expectedExcelExportParams = new ExcelExportParams(pageable.getAllResults(), attributesToExport,
				Collections.emptyList());

		given(excelExportService.exportData(anyListOf(ItemModel.class), anyListOf(SelectedAttribute.class))).willReturn(null);
		given(excelExportPreProcessor.process(any(ExcelExportParams.class))).willReturn(expectedExcelExportParams);


		// when
		handler.perform(new CustomType(), adapter, Collections.emptyMap());

		// then
		then(excelExportService).should().exportTemplate(any());
		verify(excelExportService).exportTemplate(any());
		verify(excelExportService, never()).exportData(anyList(), any());
		verify(excelExportService, never()).exportData(anyString(), any());
	}

	private Pageable mockPageable(final List<Object> items, final String typeCode)
	{
		final Pageable pageable = mock(Pageable.class);
		when(pageable.getAllResults()).thenReturn(items);
		when(pageable.getTypeCode()).thenReturn(typeCode);
		when(pageable.getTotalCount()).thenReturn(items != null ? items.size() : 0);
		return pageable;
	}

	private Set<Attribute> toAttributes(final List<SelectedAttribute> attributesToExport)
	{
		return attributesToExport.stream().map(ad -> new Attribute(ad.getQualifier(), ad.getQualifier(), false))
				.collect(Collectors.toSet());
	}

	private List<SelectedAttribute> mockSelectedAttributes(final String typeCode, final String... qualifiers)
	{
		final List<SelectedAttribute> attributes = new ArrayList<>();
		final Set<AttributeDescriptorModel> descriptors = new HashSet<>();
		for (final String qualifier : qualifiers)
		{

			final AttributeDescriptorModel ad = mock(AttributeDescriptorModel.class);
			when(ad.getQualifier()).thenReturn(qualifier);
			descriptors.add(ad);
			final SelectedAttribute sa = new SelectedAttribute(ad);
			attributes.add(sa);

		}

		when(typeService.getAttributesForModifiers(eq(typeCode), any())).thenReturn(descriptors);
		return attributes;
	}
}
