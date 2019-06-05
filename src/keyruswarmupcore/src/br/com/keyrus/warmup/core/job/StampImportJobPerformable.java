package br.com.keyrus.warmup.core.job;

import br.com.keyrus.warmup.core.enums.ReportSource;
import br.com.keyrus.warmup.core.enums.ReportStatus;
import br.com.keyrus.warmup.core.media.service.KeyrusMediaService;
import br.com.keyrus.warmup.core.report.service.CustomReportService;
import br.com.keyrus.warmup.core.stamp.service.StampService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.keyrus.warmup.core.constants.KeyruswarmupCoreConstants.VALID_MEDIA_EXTENSIONS;
import static br.com.keyrus.warmup.core.constants.KeyruswarmupCoreConstants.getFolder;
import static br.com.keyrus.warmup.core.util.StampUtils.getStampName;
import static br.com.keyrus.warmup.core.util.StampUtils.getStampPriority;
import static org.apache.commons.io.FilenameUtils.getExtension;

public class StampImportJobPerformable extends AbstractJobPerformable<CronJobModel> {

    public static final Logger LOG = Logger.getLogger(StampImportJobPerformable.class);

    private CustomReportService customReportService;
    private KeyrusMediaService mediaService;
    private StampService stampService;

    @Override
    public PerformResult perform(final CronJobModel cronJobModel) {

        final File stampFolder = getFolder("stamp.import.cronjob.folder");
        final File successFolder = getFolder("stamp.import.cronjob.folder.success");
        final File errorFolder = getFolder("stamp.import.cronjob.folder.error");

        try {

            final List<File> stamps = Arrays.asList(stampFolder.listFiles()).stream()
                    .filter(stamp -> VALID_MEDIA_EXTENSIONS.contains(getExtension(stamp.getName())))
                    .collect(Collectors.toList());

            for (final File stamp : stamps) {

                try {

                    final MediaModel media = mediaService.createMediaModel(getStampName(stamp), stamp);

                    stampService.createStamp(getStampName(stamp), media, getStampPriority(stamp));

                    final String message = "Stamp with code '" + stamp.getName() + "' created with success! ";

                    customReportService.createCustomReport(ReportSource.STAMP_IMPORT, ReportStatus.OK, message);
                    LOG.info(message);

                    stamp.renameTo(new File(successFolder.getPath() + "/" +  stamp.getName()));

                } catch (final Exception e) {

                    final String message = "Error trying to create media and stamp for file with code '" + stamp.getName() + "'! ";

                    stamp.renameTo(new File(errorFolder.getPath() + "/" + stamp.getName()));

                    customReportService.createCustomReport(ReportSource.STAMP_IMPORT, ReportStatus.NOT_OK, message + e.getMessage());
                    LOG.error(message, e);
                }
            }

            LOG.info("Stamp import job executed with success!");

            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);

        } catch (final Exception e) {

            final String message = "Problem trying to execute stamp import job!";

            customReportService.createCustomReport(ReportSource.STAMP_IMPORT, ReportStatus.NOT_OK, message + e.getMessage());
            LOG.error(message, e);

            return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
        }
    }

    public void setMediaService(KeyrusMediaService mediaService) {
        this.mediaService = mediaService;
    }

    public void setStampService(StampService stampService) {
        this.stampService = stampService;
    }

    public void setCustomReportService(CustomReportService customReportService) {
        this.customReportService = customReportService;
    }
}
