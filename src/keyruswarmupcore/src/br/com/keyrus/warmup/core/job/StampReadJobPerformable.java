package br.com.keyrus.warmup.core.job;

import br.com.keyrus.warmup.core.service.StampService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;


public class StampReadJobPerformable extends AbstractJobPerformable<CronJobModel> {

    @Value("${stamp.folder.read:${HYBRIS_BIN_DIR}/../interfaces/stamps}")
    private String folderPath;

    @Value("#{'${stamp.extensions:png,jpg,jpeg}'.split(',')}")
    private List<String> validExtensions;

    private StampService stampService;

    @Override
    public PerformResult perform(CronJobModel cronJobModel) {

        try (Stream<Path> paths = Files.list(Paths.get(folderPath))) {
            paths.filter(Files::isRegularFile)
                    .filter(file -> validExtensions.contains(FilenameUtils.getExtension(file.toFile().getName())))
                    .forEach(file -> stampService.createStamp(file.toFile()));
        } catch (IOException e) {
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
        }

        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    public StampService getStampService() {
        return stampService;
    }

    public void setStampService(StampService stampService) {
        this.stampService = stampService;
    }

}
