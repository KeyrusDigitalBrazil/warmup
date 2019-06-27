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
package de.hybris.platform.apiregistryservices.jmx;

import org.springframework.integration.support.management.Statistics;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedMetric;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.support.MetricType;

/**
 * Mbean wrapper for {@link org.springframework.integration.channel.QueueChannel}
 * Copy of {@link org.springframework.integration.support.management.MessageChannelMetrics} with one addiction
 */
@ManagedResource(
      description = "Gives an overview of spring-integration queues"
)
public interface QueueInfoBean
{
    @ManagedAttribute(
            description = "Overview of beanName",
            persistPeriod = 1
    )
    String getBeanName();

    @ManagedMetric(
            metricType = MetricType.GAUGE,
            displayName = "QueueChannel Queue Size"
    )
    int getQueueSize();

    @ManagedMetric(
            metricType = MetricType.GAUGE,
            displayName = "QueueChannel Remaining Capacity"
    )
    int getRemainingCapacity();

    @ManagedMetric(
            metricType = MetricType.COUNTER,
            displayName = "MessageChannel Send Count"
    )
    long getSendCountLong();

    @ManagedMetric(
            metricType = MetricType.COUNTER,
            displayName = "MessageChannel Send Error Count"
    )
    int getSendErrorCount();

    @ManagedMetric(
            metricType = MetricType.COUNTER,
            displayName = "MessageChannel Send Error Count"
    )
    long getSendErrorCountLong();

    @ManagedMetric(
            metricType = MetricType.GAUGE,
            displayName = "Channel Time Since Last Send in Milliseconds"
    )
    double getTimeSinceLastSend();

    @ManagedMetric(
            metricType = MetricType.GAUGE,
            displayName = "Channel Send Rate per Second"
    )
    double getMeanSendRate();

    @ManagedMetric(
            metricType = MetricType.GAUGE,
            displayName = "Channel Error Rate per Second"
    )
    double getMeanErrorRate();

    @ManagedMetric(
            metricType = MetricType.GAUGE,
            displayName = "Mean Channel Error Ratio per Minute"
    )
    double getMeanErrorRatio();

    @ManagedMetric(
            metricType = MetricType.GAUGE,
            displayName = "Channel Send Mean Duration in Milliseconds"
    )
    double getMeanSendDuration();

    @ManagedMetric(
            metricType = MetricType.GAUGE,
            displayName = "Channel Send Min Duration in Milliseconds"
    )
    double getMinSendDuration();

    @ManagedMetric(
            metricType = MetricType.GAUGE,
            displayName = "Channel Send Max Duration in Milliseconds"
    )
    double getMaxSendDuration();

    @ManagedMetric(
            metricType = MetricType.GAUGE,
            displayName = "Channel Send Standard Deviation Duration in Milliseconds"
    )
    double getStandardDeviationSendDuration();

    Statistics getSendDuration();

    Statistics getSendRate();

    Statistics getErrorRate();
}
