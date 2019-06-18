package br.com.keyrus.warmup.core.event;

import br.com.keyrus.warmup.core.service.CustomReportService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import org.apache.log4j.Logger;

public class CustomReportListener extends AbstractEventListener<CustomReportEvent> {

    private static final Logger LOGGER = Logger.getLogger(CustomReportListener.class);

    private CustomReportService customReportService;

    @Override
    protected void onEvent(CustomReportEvent customReportEvent) {
        LOGGER.info("Received event " + customReportEvent);
        if(customReportEvent != null) {
            getCustomReportService().createCustomReport(customReportEvent.getReportSource(),
                    customReportEvent.getReportStatus(),
                    customReportEvent.getMessage());
        }
    }


    public CustomReportService getCustomReportService() {
        return customReportService;
    }

    public void setCustomReportService(CustomReportService customReportService) {
        this.customReportService = customReportService;
    }
}
