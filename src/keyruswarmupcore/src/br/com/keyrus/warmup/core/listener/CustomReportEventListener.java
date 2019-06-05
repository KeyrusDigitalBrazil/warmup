package br.com.keyrus.warmup.core.listener;

import br.com.keyrus.warmup.core.event.CustomReportEvent;
import br.com.keyrus.warmup.core.report.service.CustomReportService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

public class CustomReportEventListener extends AbstractEventListener<CustomReportEvent> {

    private CustomReportService customReportService;

    @Override
    protected void onEvent(CustomReportEvent event) {
        customReportService.createCustomReport(event.getReportSource(), event.getReportStatus(), event.getMessage());
    }

    public void setCustomReportService(CustomReportService customReportService) {
        this.customReportService = customReportService;
    }
}
