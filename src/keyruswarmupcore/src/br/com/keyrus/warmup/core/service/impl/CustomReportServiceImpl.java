package br.com.keyrus.warmup.core.service.impl;

import br.com.keyrus.warmup.core.enums.ReportSource;
import br.com.keyrus.warmup.core.enums.ReportStatus;
import br.com.keyrus.warmup.core.model.CustomReportModel;
import br.com.keyrus.warmup.core.service.CustomReportService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import org.apache.log4j.Logger;

public class CustomReportServiceImpl extends AbstractBusinessService implements CustomReportService {

    private static final Logger LOGGER = Logger.getLogger(CustomReportServiceImpl.class);
    @Override
    public CustomReportModel createCustomReport(ReportSource source, ReportStatus status, String message) {
        if (status.equals(ReportStatus.OK)) {
            LOGGER.info("creating custom report " +source + " " + status + " " + message);
        }else{
            LOGGER.error("creating custom report " +source + " " + status + " " + message);
        }
        CustomReportModel report = getModelService().create(CustomReportModel.class);
        report.setSource(source);
        report.setStatus(status);
        report.setMessage(message);
        getModelService().save(report);
        return report;
    }
}
