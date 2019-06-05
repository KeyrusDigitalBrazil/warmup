package br.com.keyrus.warmup.core.job;

import br.com.keyrus.warmup.core.media.service.KeyrusMediaService;
import br.com.keyrus.warmup.core.stamp.service.StampService;
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

    private KeyrusMediaService mediaService;
    private StampService stampService;

    @Override
    public PerformResult perform(final CronJobModel cronJobModel) {

        final File stampFolder = getFolder("stamp.import.cronjob.folder");

        try {

            final List<File> stamps = Arrays.asList(stampFolder.listFiles()).stream()
                    .filter(stamp -> VALID_MEDIA_EXTENSIONS.contains(getExtension(stamp.getName())))
                    .collect(Collectors.toList());

            for (final File stamp : stamps) {

                try {
                    stampService.createStamp(getStampName(stamp), mediaService.createMediaModel(getStampName(stamp), stamp), getStampPriority(stamp));
                    LOG.info("Stamp with code '" + stamp.getName() + "' created with success!");
                } catch (final Exception e) {
                    LOG.error("Error trying to create media and stamp for file with code '" + stamp.getName() + "'!", e);
                }
            }

            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);

        } catch (final Exception e) {
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
        }
    }

    public void setMediaService(KeyrusMediaService mediaService) {
        this.mediaService = mediaService;
    }

    public void setStampService(StampService stampService) {
        this.stampService = stampService;
    }
}
