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
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.platform.audit.view.impl.ReportView;
import de.hybris.platform.auditreport.model.AuditReportTemplateModel;
import de.hybris.platform.auditreport.service.ReportConversionData;
import de.hybris.platform.auditreport.service.ReportGenerationException;
import de.hybris.platform.auditreport.service.ReportViewConverterStrategy;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.impl.DefaultSessionService;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.Writer;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;



@RunWith(MockitoJUnitRunner.class)
public class DefaultReportViewConverterStrategyTest
{
	@Spy
	@InjectMocks
	private DefaultReportViewConverterStrategy converterStrategy;

	@Mock
	private ReportViewConverterStrategy textConverterStrategy;
	@Mock
	private RendererService rendererService;
	@Mock
	private I18NService i18NService;

	@Test
	public void shouldConvertWhenIncludeTextIsTrue()
	{
		// given
		final Stream<ReportView> reports = Stream.empty();
		final Map<String, Object> context = new HashMap<>();

		final ReportConversionData textData = new ReportConversionData("fileName", new ByteArrayInputStream("fileName".getBytes()));
		final List<ReportConversionData> textDataCollection = Collections.singletonList(textData);
		when(textConverterStrategy.convert(reports, context)).thenReturn(textDataCollection);

		final AuditReportTemplateModel template = mock(AuditReportTemplateModel.class);
		context.put(AbstractTemplateViewConverterStrategy.CTX_TEMPLATE, template);
		when(template.getIncludeText()).thenReturn(Boolean.TRUE);

		final ReportConversionData htmlData = mock(ReportConversionData.class);
		doReturn(htmlData).when(converterStrategy).convertUsingTemplate(eq(template), anyMap());

		// when
		final List<ReportConversionData> result = converterStrategy.convert(reports, context);

		// then
		assertThat(result).isNotNull();
		assertThat(result).containsExactly(htmlData);
		verify(converterStrategy).createConversionContext(context);
		verify(converterStrategy).getTextConverterStrategy();
		verify(textConverterStrategy).convert(reports, context);
	}

	@Test
	public void shouldConvertWhenIncludeTextFileIsFalse()
	{
		// given
		final Stream<ReportView> reports = Stream.empty();
		final Map<String, Object> context = new HashMap<>();
		final AuditReportTemplateModel template = mock(AuditReportTemplateModel.class);
		context.put(AbstractTemplateViewConverterStrategy.CTX_TEMPLATE, template);

		final ReportConversionData textData = mock(ReportConversionData.class);
		final List<ReportConversionData> textDataCollection = Collections.singletonList(textData);
		when(textConverterStrategy.convert(reports, context)).thenReturn(textDataCollection);

		final ReportConversionData htmlData = mock(ReportConversionData.class);
		doReturn(htmlData).when(converterStrategy).convertUsingTemplate(eq(template), anyMap());

		// when
		final List<ReportConversionData> result = converterStrategy.convert(reports, context);

		// then
		assertThat(result).isNotNull();
		assertThat(result).containsExactly(htmlData);
		verify(converterStrategy).createConversionContext(context);
		verifyZeroInteractions(textConverterStrategy);
	}

	@Test
	public void shouldPrepareVelocityContextWhenCurrentUserIsSet()
	{
		// given
		final Map<String, Object> context = new HashMap<>();

		final UserModel user = mockUser("James Smith", 1L);
		context.put(AbstractTemplateViewConverterStrategy.CTX_CURRENT_USER, user);

		final UserModel rootItem = mockUser("John Doe", 2L);
		context.put(AbstractTemplateViewConverterStrategy.CTX_ROOT_ITEM, rootItem);

		context.put(AbstractTemplateViewConverterStrategy.CTX_REPORT_ID, "Report ID");

		// when
		final Map<String, Object> result = converterStrategy.prepareAdditionalContext(context);

		// then
		assertThat(result).isNotNull();
		assertThat(result.keySet()).hasSize(7);
		assertThat(result.get("generatedTimestamp")).isInstanceOf(ZonedDateTime.class);
		verify(user).getName();
		assertThat(result.get("generatedBy")).isSameAs(user.getName());
		assertThat(result.get("generatedFor")).isSameAs(rootItem.getName());
	}

	@Test
	public void shouldConvertUsingTemplate()
	{
		final HashMap<String, Object> conversionContext = new HashMap<>();
		RendererTemplateModel rendererTemplate = mock(RendererTemplateModel.class);
		when(converterStrategy.getSessionService()).thenReturn(new DefaultSessionService() {
			@Override
			public Object executeInLocalView(final SessionExecutionBody body) {
				return body.execute();
			}
		});

		final ReportConversionData conversionData = converterStrategy.convertUsingTemplate(rendererTemplate, conversionContext);

		assertThat(conversionData).isNotNull();
		assertThat(conversionData.getStream()).isNotNull();
		verify(converterStrategy).registerRequiredCustomDirectives();
		verify(rendererService).render(eq(rendererTemplate), eq(conversionContext), any(Writer.class));
	}

	@Test(expected = ReportGenerationException.class)
	public void shouldThrowReportGenerationExceptiononIOException()
	{
		final HashMap<String, Object> conversionContext = new HashMap<>();
		RendererTemplateModel rendererTemplate = mock(RendererTemplateModel.class);
		when(converterStrategy.getSessionService()).thenReturn(new DefaultSessionService() {
			@Override
			public Object executeInLocalView(final SessionExecutionBody body) {
				return body.execute();
			}
		});

		doThrow(FileNotFoundException.class).when(rendererService).render(any(), any(), any());

		converterStrategy.convertUsingTemplate(rendererTemplate, conversionContext);
	}

	private UserModel mockUser(final String username, final long pk)
	{
		final UserModel user = mock(UserModel.class);
		when(user.getName()).thenReturn(username);
		when(user.getPk()).thenReturn(PK.fromLong(pk));
		return user;
	}
}
