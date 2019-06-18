package br.com.keyrus.warmup.core.service.impl;

import br.com.keyrus.warmup.core.dao.StampDAO;
import br.com.keyrus.warmup.core.enums.ReportSource;
import br.com.keyrus.warmup.core.enums.ReportStatus;
import br.com.keyrus.warmup.core.event.CustomReportEvent;
import br.com.keyrus.warmup.core.model.StampModel;
import br.com.keyrus.warmup.core.service.CustomMediaService;
import br.com.keyrus.warmup.core.service.StampService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class StampServiceImpl extends AbstractBusinessService implements StampService {

    private static final Logger LOGGER = Logger.getLogger(StampServiceImpl.class);
    @Value("${stamp.max.product:2}")
    private Integer maxStamps;


    @Value("${stamp.folder.read:${HYBRIS_BIN_DIR}/../interfaces/stamps/success/}")
    private String folderSuccessPath;
    @Value("${stamp.folder.read:${HYBRIS_BIN_DIR}/../interfaces/stamps/error/}")
    private String folderErrorPath;

    private FlexibleSearchService flexibleSearchService;
    private CustomMediaService mediaService;
    private EventService eventService;
    private StampDAO stampDAO;

    @Override
    public void createStamp(File file){
        LOGGER.info("creating Stamp " + file.getName());
        String message = "";
        try {
            StringTokenizer nameParts = new StringTokenizer(file.getName(), "#");
            String name = nextValue(nameParts);
            String priority = nextValue(nameParts);
            priority = priority.substring(0, priority.indexOf("."));
            MediaModel media = mediaService.createMediaModel(name, file);
            StampModel stamp = createStampModel(name, priority, media);

            Path moveFile = Files.move(file.toPath(), Paths.get(folderSuccessPath + file.getName()));
            LOGGER.info("File moved to " + moveFile.toString());
            message = "the stamp " + name + " was imported.";
            publishEvent(ReportSource.STAMP_IMPORT, ReportStatus.OK, message);
        }catch (Exception e){
            try {
                Path moveFile = Files.move(file.toPath(), Paths.get(folderErrorPath + file.getName()));
                LOGGER.info("File moved to " + moveFile.toString());
            }catch (Exception ex) {
            }
            message = "the file name " + file.getName() + " was not imported because " + e.getMessage();
            publishEvent(ReportSource.STAMP_IMPORT, ReportStatus.NOT_OK, message);
        }
    }
    @Override
    public List<StampModel> listProductStamps(ProductModel product){
        List<StampModel> stamps = stampDAO.listStampByProduct(product);
        return stamps.stream()
                .sorted(Comparator.comparingInt(StampModel::getPriority))
                .limit(maxStamps)
                .collect(Collectors.toList());
    }

    private void publishEvent(ReportSource source, ReportStatus status, String message){
        CustomReportEvent customReportEvent = new CustomReportEvent();
        customReportEvent.setReportSource(source);
        customReportEvent.setReportStatus(status);
        customReportEvent.setMessage(message);
        eventService.publishEvent(customReportEvent);
    }

    private String nextValue(StringTokenizer nameParts){
        if(nameParts.hasMoreElements()){
            return nameParts.nextToken();
        }
         throw new RuntimeException("Invalid file name.");
    }

    private StampModel createStampModel(String code, String priority, MediaModel media){
        StampModel stamp = new StampModel();
        stamp.setCode(code);
        List<StampModel> results = flexibleSearchService.getModelsByExample(stamp);
        if(results == null || results.isEmpty()){
            LOGGER.info("Creating new stamp " + code);
            stamp = getModelService().create(StampModel.class);
            stamp.setCode(code);
            stamp.setPriority(Integer.valueOf(priority));
            getModelService().save(stamp);
        }else{
            stamp = results.get(0);
        }
        return stamp;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public CustomMediaService getMediaService() {
        return mediaService;
    }

    public void setMediaService(CustomMediaService mediaService) {
        this.mediaService = mediaService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    public StampDAO getStampDAO() {
        return stampDAO;
    }

    public void setStampDAO(StampDAO stampDAO) {
        this.stampDAO = stampDAO;
    }
}
