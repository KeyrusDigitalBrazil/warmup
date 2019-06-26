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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.platform.audit.view.impl.ReportView;
import de.hybris.platform.auditreport.service.ReportGenerationException;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AbstractTemplateViewConverterStrategyTest
{

	@Spy
	private AbstractTemplateViewConverterStrategy strategy;

	@Test
	public void convertWithArrayShouldCallTheStreamAPI()
	{
		//given
		final HashMap<String, Object> context = new HashMap<>();
		final Stream<ReportView> reports = Stream.empty();

		doReturn(null).when(strategy).convert(any(Stream.class), any());

		//when
		strategy.convert(reports, context);

		//then
		final ArgumentCaptor<Stream> captor = ArgumentCaptor.forClass(Stream.class);
		verify(strategy).convert(captor.capture(), eq(context));
	}

	@Test
	public void convertWithStreamArgumentShouldCallTheOldAPI()
	{
		//given
		final HashMap<String, Object> context = new HashMap<>();
		final ArrayList<ReportView> reports = new ArrayList<>();
		final RendererTemplateModel template = mock(RendererTemplateModel.class);

		//when
		strategy.convert(reports.stream(), template, context);

		//then
		verify(strategy).convert(any(List.class), eq(template), eq(context));
	}

	@Test(expected = ReportGenerationException.class)
	public void convertWithNoRendererTemplateShouldFail()
	{
		//given
		final HashMap<String, Object> context = new HashMap<>();
		final ArrayList<ReportView> reports = new ArrayList<>();

		//when
		strategy.convert(reports.stream(), context);
	}

	@Test
	public void convertWithRendererTemplateShouldPassIt()
	{
		//given
		final HashMap<String, Object> context = new HashMap<>();
		final ArrayList<ReportView> reports = new ArrayList<>();
		final RendererTemplateModel template = mock(RendererTemplateModel.class);
		context.put(AbstractTemplateViewConverterStrategy.CTX_TEMPLATE, template);
		final Stream<ReportView> stream = reports.stream();

		//when
		strategy.convert(stream, context);

		//then
		verify(strategy).convert(stream, template, context);
	}

}
