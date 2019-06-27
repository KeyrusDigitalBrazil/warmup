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
package com.hybris.backoffice.excel.imp.wizard.renderer;

import static com.hybris.backoffice.excel.imp.wizard.renderer.ExcelImportRenderer.SCLASS_EXCEL_IMPORT_WIZARD_DROP_UPLOAD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.lang.Strings;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zkmax.zul.Dropupload;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;

import com.google.common.collect.Sets;
import com.hybris.backoffice.excel.ExcelConstants;
import com.hybris.backoffice.excel.imp.ExcelValidator;
import com.hybris.backoffice.excel.imp.wizard.ExcelImportWizardForm;
import com.hybris.cockpitng.config.jaxb.wizard.ViewType;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.editor.defaultfileupload.FileUploadResult;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class ExcelImportRendererTest
{
	private final Component parent = new Div();

	@Mock
	private WidgetInstanceManager widgetInstanceManager;

	@Mock
	private ExcelValidator excelValidator;

	@Spy
	private ExcelImportRenderer renderer;

	@Before
	public void setUp()
	{
		final FileUploadResult fileUploadResult = new FileUploadResult();
		fileUploadResult.setName("name");
		fileUploadResult.setFormat("format");
		fileUploadResult.setData(new byte[1024]);
		final ExcelImportWizardForm excelImportWizardForm = new ExcelImportWizardForm();
		excelImportWizardForm.setFileUploadResult(Sets.newHashSet(fileUploadResult));
		final WidgetModel widgetModel = mock(WidgetModel.class);
		given(widgetModel.getValue(ExcelConstants.EXCEL_FORM_PROPERTY, ExcelImportWizardForm.class))
				.willReturn(excelImportWizardForm);
		given(widgetInstanceManager.getModel()).willReturn(widgetModel);

		renderer.setExcelValidator(excelValidator);
		doReturn(Strings.EMPTY).when(renderer).getUpload();
		doReturn(ExcelImportRenderer.DEFAULT_MAX_FILE_SIZE).when(renderer).getMaxFileSize();

		final ViewType viewType = mock(ViewType.class);
		final DataType dataType = mock(DataType.class);

		CockpitTestUtil.mockZkEnvironment();
		doNothing().when(renderer).attachmentsListChanged(any(),any());
		renderer.render(parent, viewType, new HashMap<>(), dataType, widgetInstanceManager);
	}

	@Test
	public void shouldDroppedFileBeAddedToTheModel()
	{
		// given
		final Dropupload dropupload = (Dropupload) parent.query("." + SCLASS_EXCEL_IMPORT_WIZARD_DROP_UPLOAD);
		final Event event = new UploadEvent(Events.ON_UPLOAD, dropupload, new Media[]
		{ new AMedia("file", "format", "", new byte[1024]) });

		// when
		CockpitTestUtil.simulateEvent(dropupload, event);

		// then
		verify(renderer).addFileResult(any(), any(), any(), any());
	}

	@Test
	public void shouldClickingOnRemoveButtonRemovePlaceholderAndFileFromTheModel()
	{
		// given
		final Button button = (Button) parent.query(".yw-excel-import-wizard-attachments-btn");
		final Groupbox attachmentsPlaceholder = CockpitTestUtil.find(parent, Groupbox.class).get();
		final int attachmentComponentsSize = attachmentsPlaceholder.getChildren().size();
		final int modelSize = renderer.getCurrentExcelModel(widgetInstanceManager).getFileUploadResult().size();

		// when
		CockpitTestUtil.simulateEvent(button, Events.ON_CLICK, null);

		// then
		assertThat(attachmentComponentsSize).isEqualTo(attachmentsPlaceholder.getChildren().size() + 1);
		assertThat(modelSize).isEqualTo(renderer.getCurrentExcelModel(widgetInstanceManager).getFileUploadResult().size() + 1);
	}

}
