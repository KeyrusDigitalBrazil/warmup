package br.com.keyrus.warmup.core.report.service;

import br.com.keyrus.warmup.core.enums.ReportSource;
import br.com.keyrus.warmup.core.enums.ReportStatus;
import br.com.keyrus.warmup.core.model.CustomReportModel;

public interface CustomReportService {

    CustomReportModel createCustomReport(final ReportSource source, final ReportStatus status);

    CustomReportModel createCustomReport(final ReportSource source, final ReportStatus status, final String message);
}
