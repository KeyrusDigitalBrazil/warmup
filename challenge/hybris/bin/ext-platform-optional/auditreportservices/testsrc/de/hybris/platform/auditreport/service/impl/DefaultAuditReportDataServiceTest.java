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
package de.hybris.platform.auditreport.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.audit.TypeAuditReportConfig;
import de.hybris.platform.audit.view.AuditViewService;
import de.hybris.platform.audit.view.impl.ReportView;
import de.hybris.platform.auditreport.model.AuditReportDataModel;
import de.hybris.platform.auditreport.service.CreateAuditReportParams;
import de.hybris.platform.auditreport.service.ReportConversionData;
import de.hybris.platform.auditreport.service.ReportGenerationException;
import de.hybris.platform.auditreport.service.ReportViewConverterStrategy;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.audit.AuditReportConfigModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.ModelInitializationException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultAuditReportDataServiceTest
{
	public static final String CONFIG_NAME_PARAM = "config";
	public static final String REPORT_ID = "identifier";
	public static final String CONFIG_NAME = "configName";

	@Spy
	@InjectMocks
	private DefaultAuditReportDataService reportService;

	@Mock
	private AuditViewService auditViewService;
	@Mock
	private MediaService mediaService;
	@Mock
	private ModelService modelService;
	@Mock
	private UserService userService;
	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void init()
	{
		final SearchResult searchResult = mock(SearchResult.class);
		when(searchResult.getCount()).thenReturn(1);
		when(searchResult.getResult()).thenReturn(Collections.singletonList(mock(AuditReportConfigModel.class)));
		when(flexibleSearchService.search(any(String.class))).thenReturn(searchResult);
		when(reportService.getReportViewConverterStrategies()).thenReturn(Collections.emptyList());
	}

	@Test(expected = ReportGenerationException.class)
	public void testCreateWhenSaveReportThrowsException()
	{
		// given
		final ItemModel item = mock(ItemModel.class);
		doReturn(PK.fromLong(1234L)).when(item).getPk();
		doThrow(IOException.class).when(reportService).saveReport(eq(item), any(), any(), any(InputStream.class));

		// when
		reportService.createReport(new CreateAuditReportParams(item, CONFIG_NAME_PARAM, REPORT_ID, false, null, null));
	}

	@Test
	public void testCreateWhenSaveReportFinishedSuccessfully()
	{
		// given
		final ItemModel item = mock(ItemModel.class);
		doReturn(PK.fromLong(1234L)).when(item).getPk();
		when(modelService.create(AuditReportDataModel.class)).thenReturn(mock(AuditReportDataModel.class));

		// when
		reportService.createReport(new CreateAuditReportParams(item, CONFIG_NAME_PARAM, REPORT_ID, false, null, null));

		// then
		verify(reportService).saveReport(eq(item), any(), any(), any(InputStream.class));
	}


	@Test
	public void testCreateMapWithFilesLegacyCode()
	{
		// given
		final Stream<ReportView> reports = mock(Stream.class);

		final ReportViewConverterStrategy converterStrategy1 = mock(ReportViewConverterStrategy.class);
		final InputStream jsonBytes = new ByteArrayInputStream(new byte[]
		{ 'j' });
		final ReportConversionData reportConversionData1 = new ReportConversionData("report.json", jsonBytes);
		final InputStream xmlBytes = new ByteArrayInputStream(new byte[]
		{ 'x' });

		final ReportConversionData reportConversionData2 = new ReportConversionData("report.xml", xmlBytes);
		final List<ReportConversionData> reportConversionResult1 = Arrays.asList(reportConversionData1, reportConversionData2);
		when(converterStrategy1.convert(any(Stream.class), any())).thenReturn(reportConversionResult1);

		final ReportViewConverterStrategy converterStrategy2 = mock(ReportViewConverterStrategy.class);
		when(converterStrategy2.convert(any(Stream.class), any())).thenReturn(Collections.emptyList());

		when(reportService.getReportViewConverterStrategies()).thenReturn(Arrays.asList(converterStrategy1, converterStrategy2));

		final UserModel user = mock(UserModel.class);
		when(userService.getCurrentUser()).thenReturn(user);

		// when
		final Map<String, InputStream> result = reportService.evaluateStrategiesToStreams(reports, new HashMap<>());

		// then
		assertThat(result).isNotNull();
		assertThat(result).containsEntry("report.json", jsonBytes);
		assertThat(result).containsEntry("report.xml", xmlBytes);

		final ArgumentCaptor<Stream<ReportView>> reportsCaptor = ArgumentCaptor.forClass((Class) Stream.class);
		final ArgumentCaptor<Map<String, Object>> contextCaptor = ArgumentCaptor.forClass((Class) Map.class);
		verify(converterStrategy1).convert(reportsCaptor.capture(), contextCaptor.capture());
		assertThat(reportsCaptor.getValue()).isSameAs(reports);

		final Map<String, Object> context = reportService.populateReportGenerationContext(null, CONFIG_NAME_PARAM, "name", null);

		assertThat(context).containsEntry(AbstractTemplateViewConverterStrategy.CTX_CURRENT_USER, user);
	}

	@Test
	public void testCreateReportsViews()
	{
		// given
		final ItemModel item = mock(ItemModel.class);
		final PK itemPk = PK.fromLong(1L);
		when(item.getPk()).thenReturn(itemPk);

		final ReportView report1 = mock(ReportView.class);
		final ReportView report2 = mock(ReportView.class);

		when(auditViewService.getViewOn(any())).thenReturn(Stream.of(report1, report2));

		// when
		final Stream<ReportView> result = reportService.createReportsViewsStream(item, CONFIG_NAME_PARAM, false, null);

		// then
		assertThat(result).containsExactly(report1, report2);

		final ArgumentCaptor<TypeAuditReportConfig> configCaptor = ArgumentCaptor.forClass(TypeAuditReportConfig.class);
		verify(auditViewService).getViewOn(configCaptor.capture());
		assertThat(configCaptor.getValue().getRootTypePk()).isSameAs(itemPk);
	}

	@Test(expected = ModelInitializationException.class)
	public void testSaveReportWhenCannotCreateNewReport()
	{
		// given
		final ItemModel rootItem = mock(ItemModel.class);
		final String reportId = "123";
		final ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);

		when(modelService.create(AuditReportDataModel.class)).thenThrow(ModelInitializationException.class);

		// when
		reportService.saveReport(rootItem, reportId, CONFIG_NAME, input);
	}

	@Test(expected = ModelSavingException.class)
	public void testSaveReportWhenCannotSaveReport()
	{
		// given
		final ItemModel rootItem = mock(ItemModel.class);
		final String reportId = "456";
		final ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);

		final AuditReportDataModel report = mock(AuditReportDataModel.class);
		when(modelService.create(AuditReportDataModel.class)).thenReturn(report);

		doThrow(ModelSavingException.class).when(modelService).save(report);

		// when
		reportService.saveReport(rootItem, reportId, CONFIG_NAME, input);
	}

	@Test
	public void testSaveReportWhenSaveFinishedSuccessfully() throws IOException
	{
		// given
		final String reportId = "789";
		final byte[] content =
		{ 'c', 'o', 'n', 't', 'e', 'n', 't' };
		final InputStream contentStream = new ByteArrayInputStream(content);

		final AuditReportDataModel report = mock(AuditReportDataModel.class);
		when(modelService.create(AuditReportDataModel.class)).thenReturn(report);

		final String folderName = "auditreports";
		when(reportService.getReportFolderName()).thenReturn(folderName);
		final MediaFolderModel mediaFolder = mock(MediaFolderModel.class);
		when(mediaService.getFolder(folderName)).thenReturn(mediaFolder);

		final String reportFileName = "reportFileName";
		reportService.setReportFileName(reportFileName);

		// when
		final ItemModel rootItem = mock(ItemModel.class);
		final AuditReportDataModel result = reportService.saveReport(rootItem, reportId, CONFIG_NAME, contentStream);

		// then
		assertThat(result).isSameAs(report);
		verify(report).setCode(reportId);
		verify(report).setFolder(mediaFolder);
		verify(reportService).getReportFileName();

		final ArgumentCaptor<AuditReportDataModel> reportCaptor = ArgumentCaptor.forClass(AuditReportDataModel.class);
		final ArgumentCaptor<InputStream> contentCaptor = ArgumentCaptor.forClass(InputStream.class);
		final ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> mimeTypeCaptor = ArgumentCaptor.forClass(String.class);
		verify(mediaService).setStreamForMedia(reportCaptor.capture(), contentCaptor.capture(), fileNameCaptor.capture(),
				mimeTypeCaptor.capture());
		assertThat(reportCaptor.getValue()).isSameAs(report);

		final byte[] savedContent = new byte[content.length];
		final InputStream mediaContent = contentCaptor.getValue();
		assertThat(mediaContent.read(savedContent)).isEqualTo(content.length);
		assertThat(savedContent).containsExactly(content);
		assertThat(mediaContent.read(savedContent)).isEqualTo(-1);
		assertThat(fileNameCaptor.getValue()).isEqualTo(reportFileName + ".zip");
		assertThat(mimeTypeCaptor.getValue()).isEqualTo("application/zip");
	}

	@Test
	public void testCreateReportFileNameWhenNameIsNotSet()
	{
		// when
		final String result = reportService.createReportFileName();

		// then
		assertThat(result).isEqualTo(DefaultAuditReportDataService.DEFAULT_FILE_NAME);
	}

	@Test
	public void testCreateReportFileNameWhenNameIsSetAndHasZipExtension()
	{
		// given
		final String reportFileName = "file.zip";
		reportService.setReportFileName(reportFileName);

		// when
		final String result = reportService.createReportFileName();

		// then
		assertThat(result).isEqualTo(reportFileName);
	}

	@Test
	public void testCreateReportFileNameWhenNameIsSetAndHasNotZipExtension()
	{
		// given
		final String reportFileName = "file.txt";
		reportService.setReportFileName(reportFileName);

		// when
		final String result = reportService.createReportFileName();

		// then
		assertThat(result).isEqualTo(reportFileName + ".zip");
	}

	@Test
	public void testDeleteReportsForItem()
	{
		final List<String> list = Arrays.asList("one", "two");
		when(flexibleSearchService.search(any(String.class))).thenReturn(new SearchResultImpl(list, 2, 2, 0));


		reportService.deleteReportsForItem(mock(ItemModel.class));

		final ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);
		verify(modelService).removeAll(captor.capture());
		assertThat(captor.getValue()).hasSize(list.size());
		assertThat(captor.getValue()).containsExactly(list.toArray());
	}
}
