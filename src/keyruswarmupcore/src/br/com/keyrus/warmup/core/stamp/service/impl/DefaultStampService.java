package br.com.keyrus.warmup.core.stamp.service.impl;

import br.com.keyrus.warmup.core.model.StampModel;
import br.com.keyrus.warmup.core.stamp.service.StampService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.apache.log4j.Logger;

public class DefaultStampService extends AbstractBusinessService implements StampService {

    public static final Logger LOG = Logger.getLogger(DefaultStampService.class);

    private FlexibleSearchService flexibleSearchService;

    @Override
    public StampModel createStamp(final String code, final MediaModel media, final Integer priority) {

        StampModel stampModel;

        try {

            stampModel = new StampModel();
            stampModel.setCode(code);

            stampModel = flexibleSearchService.getModelByExample(stampModel);

        } catch (final ModelNotFoundException e) {

            LOG.debug("Stamp not found for code '" + code + "'!", e);

            stampModel = getModelService().create(StampModel.class);
            stampModel.setCode(code);
        }

        stampModel.setMedia(media);
        stampModel.setPriority(priority);

        getModelService().save(stampModel);

        return stampModel;
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
