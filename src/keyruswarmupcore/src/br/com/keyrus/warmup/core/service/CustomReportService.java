package br.com.keyrus.warmup.core.service;

import br.com.keyrus.warmup.core.enums.ReportSource;
import br.com.keyrus.warmup.core.enums.ReportStatus;
import br.com.keyrus.warmup.core.model.CustomReportModel;

public interface CustomReportService {

    CustomReportModel createCustomReport(ReportSource source, ReportStatus status, String message);
}
