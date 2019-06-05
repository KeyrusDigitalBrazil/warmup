package br.com.keyrus.warmup.core.report.service.impl;

import br.com.keyrus.warmup.core.enums.ReportSource;
import br.com.keyrus.warmup.core.enums.ReportStatus;
import br.com.keyrus.warmup.core.model.CustomReportModel;
import br.com.keyrus.warmup.core.report.service.CustomReportService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;

public class DefaultCustomReportService extends AbstractBusinessService implements CustomReportService {

    @Override
    public CustomReportModel createCustomReport(ReportSource source, ReportStatus status) {
        return createCustomReport(source, status, null);
    }

    @Override
    public CustomReportModel createCustomReport(ReportSource source, ReportStatus status, String message) {

        final CustomReportModel report = getModelService().create(CustomReportModel.class);

        report.setSource(source);
        report.setStatus(status);
        report.setMessage(message);

        getModelService().save(report);

        return report;
    }
}
