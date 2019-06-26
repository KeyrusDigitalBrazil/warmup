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
package com.hybris.backoffice.widgets.stats.productstats;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.chart.Chart;
import org.zkoss.chart.Charts;
import org.zkoss.chart.Color;
import org.zkoss.chart.Exporting;
import org.zkoss.chart.Point;
import org.zkoss.chart.Series;
import org.zkoss.chart.Tooltip;
import org.zkoss.chart.options3D.Options3D;
import org.zkoss.chart.plotOptions.BarPlotOptions;
import org.zkoss.chart.plotOptions.ColumnPlotOptions;
import org.zkoss.chart.plotOptions.DataLabels;
import org.zkoss.chart.plotOptions.PiePlotOptions;
import org.zkoss.chart.plotOptions.PlotOptions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;

import com.google.common.collect.ImmutableMap;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;


@NullSafeWidget
@RunWith(MockitoJUnitRunner.class)
public class ProductStatsControllerTest extends AbstractWidgetUnitTest<ProductStatsController>
{
	@Spy
	@InjectMocks
	private ProductStatsController controller;

	@Mock
	private Component component;

	@Mock
	private Label productsTotalCountValue;

	@Mock
	private Label productsUnapprovedCountValue;

	@Mock
	private Label productsCheckCountValue;

	@Mock
	private Label productsApprovedCountValue;

	@Mock
	private Charts charts;

	@Mock
	private Chart chart;

	@Mock
	private Tooltip tooltip;

	@Mock
	private Exporting exporting;

	@Mock
	private PlotOptions plotOptions;

	@Mock
	private PiePlotOptions piePlotOptions;

	@Mock
	private BarPlotOptions barPlotOptions;

	@Mock
	private ColumnPlotOptions columnPlotOptions;

	@Mock
	private DataLabels dataLabels;

	@Mock
	private Options3D options3D;

	@Mock
	private Series series;

	@Mock
	private BackofficeProductCounter backofficeProductCounter;

	@Override
	protected ProductStatsController getWidgetController()
	{
		return controller;
	}

	@Before
	public void setUp()
	{
		when(controller.getProductsTotalCountValue()).thenReturn(productsTotalCountValue);
		when(controller.getProductsUnapprovedCountValue()).thenReturn(productsUnapprovedCountValue);
		when(controller.getProductsCheckCountValue()).thenReturn(productsCheckCountValue);
		when(controller.getProductsApprovedCountValue()).thenReturn(productsApprovedCountValue);
		when(controller.getCharts()).thenReturn(charts);
		when(controller.getCharts().getTooltip()).thenReturn(tooltip);
		when(controller.getCharts().getExporting()).thenReturn(exporting);
		when(controller.getCharts().getChart()).thenReturn(chart);
		when(controller.getCharts().getPlotOptions()).thenReturn(plotOptions);
		when(controller.getCharts().getPlotOptions().getPie()).thenReturn(piePlotOptions);
		when(controller.getCharts().getPlotOptions().getPie().getDataLabels()).thenReturn(dataLabels);
		when(controller.getCharts().getOptions3D()).thenReturn(options3D);
		when(controller.getCharts().getSeries()).thenReturn(series);
	}

	@Test
	public void shouldInitializeWidget()
	{
		// given
		when(controller.getCharts().getType()).thenReturn(Charts.PIE);
		when(controller.getBackofficeProductCounter().countProducts()).thenReturn(0L);
		when(controller.getBackofficeProductCounter().countProducts(any(ArticleApprovalStatus.class))).thenReturn(0L);

		// when
		controller.initialize(component);

		// then
		verify(controller).initializeProductsCounts();
		verify(controller).initializeChartConfiguration();
		verify(controller).initializeChartSeries();
	}

	@Test
	public void shouldInitializeProductsCounts()
	{
		// when
		controller.initializeProductsCounts();

		// then
		verify(controller.getProductsTotalCountValue()).setValue(any());
		verify(controller.getProductsUnapprovedCountValue()).setValue(any());
		verify(controller.getProductsCheckCountValue()).setValue(any());
		verify(controller.getProductsApprovedCountValue()).setValue(any());
		verify(controller).countProducts();
		verify(controller, times(3)).countProducts(any(ArticleApprovalStatus.class));
	}

	@Test
	public void shouldInitializeChartConfiguration()
	{
		// given
		when(controller.getCharts().getType()).thenReturn(Charts.PIE);

		// when
		controller.initializeChartConfiguration();

		// then
		verify(controller.getCharts()).setTitle(anyString());
		verify(controller.getCharts().getTooltip()).setEnabled(anyBoolean());
		verify(controller.getCharts().getTooltip()).setPointFormat(anyString());
		verify(controller.getCharts().getExporting()).setEnabled(anyBoolean());
		verify(controller).initializeChartOptions();
		verify(controller).initializePlotOptions();
	}

	@Test
	public void shouldInitializeChartOptions()
	{
		// when
		controller.initializeChartOptions();

		// then
		verify(controller.getCharts().getChart()).setType(anyString());
		verify(controller.getCharts().getChart()).setPlotBorderWidth(anyInt());
		verify(controller.getCharts().getChart()).setPlotShadow(anyBoolean());
	}

	@Test
	public void shouldInitializePlotOptionsForPie()
	{
		// given
		when(controller.getCharts().getType()).thenReturn(Charts.PIE);

		// when
		controller.initializePlotOptions();

		// then
		verify(controller).initializePiePlotOptions(any(PiePlotOptions.class));
	}

	@Test
	public void shouldInitializePlotOptionsForBar()
	{
		// given
		when(controller.getCharts().getType()).thenReturn(Charts.BAR);
		when(controller.getCharts().getPlotOptions().getBar()).thenReturn(barPlotOptions);
		when(barPlotOptions.getDataLabels()).thenReturn(dataLabels);

		// when
		controller.initializePlotOptions();

		// then
		verify(controller).initializeColumnPlotOptions(any(BarPlotOptions.class));
	}


	@Test
	public void shouldInitializePlotOptionsForColumn()
	{
		// given
		when(controller.getCharts().getType()).thenReturn(Charts.COLUMN);
		when(controller.getCharts().getPlotOptions().getColumn()).thenReturn(columnPlotOptions);
		when(columnPlotOptions.getDataLabels()).thenReturn(dataLabels);

		// when
		controller.initializePlotOptions();

		// then
		verify(controller).initializeColumnPlotOptions(any(ColumnPlotOptions.class));
	}

	@Test
	public void shouldWarnAboutUnsupportedTypeDuringInitializationOfPlotOptions()
	{
		// given
		final String unsupportedChartType = Charts.BUBBLE;
		when(controller.getCharts().getType()).thenReturn(unsupportedChartType);

		// when
		controller.initializePlotOptions();

		// then
		verify(controller).warnAboutUnsupportedChartType();
	}

	@Test
	public void shouldInitializePiePlotOptions()
	{
		// when
		controller.initializePiePlotOptions(piePlotOptions);

		// then
		verify(piePlotOptions).setAllowPointSelect(anyBoolean());
		verify(piePlotOptions).setCursor(anyString());
		verify(piePlotOptions).setInnerSize(anyString());
		verify(piePlotOptions).setShowInLegend(anyBoolean());
		verify(piePlotOptions).setDepth(anyInt());
		verify(piePlotOptions).setColors(anyList());
		verify(controller).initializeDataLabels(anyDataLabels());
		verify(controller).initializeOptions3d();
	}

	@Test
	public void shouldInitializeColumnPlotOptions()
	{
		// given
		when(columnPlotOptions.getDataLabels()).thenReturn(dataLabels);

		// when
		controller.initializeColumnPlotOptions(columnPlotOptions);

		// then
		verify(columnPlotOptions).setAllowPointSelect(anyBoolean());
		verify(columnPlotOptions).setCursor(anyString());
		verify(columnPlotOptions).setShowInLegend(anyBoolean());
		verify(columnPlotOptions).setDepth(anyInt());
		verify(columnPlotOptions).setColors(anyList());
		verify(controller).initializeDataLabels(anyDataLabels());
		verify(controller).initializeOptions3d();
	}

	private DataLabels anyDataLabels()
	{
		return any(DataLabels.class);
	}

	@Test
	public void initializeDataLabels()
	{
		// when
		controller.initializeDataLabels(dataLabels);

		// then
		verify(dataLabels).setEnabled(anyBoolean());
		verify(dataLabels).setFormat(anyString());
	}

	@Test
	public void shouldCreateChartColors()
	{
		// when
		final List<Color> chartColors = controller.createChartColors();

		// then
		assertThat(chartColors.size()).isEqualTo(3);
		verify(controller, times(3)).getWidgetSettingString(anyString());
	}

	@Test
	public void shouldInitializeOptions3d()
	{
		// when
		controller.initializeOptions3d();

		// then
		verify(controller.getCharts().getOptions3D()).setEnabled(anyBoolean());
		verify(controller.getCharts().getOptions3D()).setAlpha(anyInt());
		verify(controller.getCharts().getOptions3D()).setBeta(anyInt());
	}

	@Test
	public void shouldInitializeChartSeries()
	{
		// when
		controller.initializeChartSeries();

		// then
		verify(controller).createChartPoints();
	}

	@Test
	public void shouldCreateChartPoints()
	{
		// given
		final Map<ArticleApprovalStatus, Number> percentage = ImmutableMap.of( //
				ArticleApprovalStatus.UNAPPROVED, 20L, //
				ArticleApprovalStatus.CHECK, 10L, //
				ArticleApprovalStatus.APPROVED, 70L //
		);

		when(backofficeProductCounter.countProducts()).thenReturn(100L);
		percentage.forEach((status, number) -> when(backofficeProductCounter.countProducts(status)).thenReturn(number.longValue()));
		final int[] index =
		{ 0 };

		// when
		final List<Point> points = controller.createChartPoints();

		// then
		assertThat(points.size()).isEqualTo(percentage.size());

		percentage.forEach((status, number) -> {
			assertThat(points.get(index[0]).getName()).isEqualTo(status.name());
			assertThat(points.get(index[0]).getY()).isEqualTo(number.doubleValue());
			index[0]++;
		});
	}
}
