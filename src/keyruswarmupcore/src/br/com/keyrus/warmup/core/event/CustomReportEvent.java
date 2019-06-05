package br.com.keyrus.warmup.core.event;

import br.com.keyrus.warmup.core.enums.ReportSource;
import br.com.keyrus.warmup.core.enums.ReportStatus;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

public class CustomReportEvent extends AbstractEvent {

    private static final long serialVersionUID = 1L;

    private ReportSource reportSource;
    private ReportStatus reportStatus;
    private String message;

    public CustomReportEvent() {
        // EMPTY CONSTRUCTOR;
    }

    public CustomReportEvent(ReportSource reportSource, ReportStatus reportStatus, String message) {
        this.reportSource = reportSource;
        this.reportStatus = reportStatus;
        this.message = message;
    }

    public ReportSource getReportSource() {
        return reportSource;
    }

    public void setReportSource(ReportSource reportSource) {
        this.reportSource = reportSource;
    }

    public ReportStatus getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(ReportStatus reportStatus) {
        this.reportStatus = reportStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
