package br.com.keyrus.warmup.core.stamp.service.impl;

import br.com.keyrus.warmup.core.model.StampModel;
import br.com.keyrus.warmup.core.stamp.dao.StampDAO;
import br.com.keyrus.warmup.core.stamp.service.StampService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;

public class DefaultStampService extends AbstractBusinessService implements StampService {

    public static final Logger LOG = Logger.getLogger(DefaultStampService.class);

    public static final Integer STAMPS_LIMIT = 2;

    private FlexibleSearchService flexibleSearchService;
    private StampDAO stampDAO;

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

    @Override
    public List<StampModel> getBestProductStamps(ProductModel product) {

        final List<StampModel> stamps = stampDAO.findProductStamps(product);

        if (CollectionUtils.isEmpty(stamps) || stamps.get(0) == null)
            return null;

        stamps.sort(comparing(StampModel::getPriority));

        return stamps.stream().limit(STAMPS_LIMIT).collect(toList());
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public void setStampDAO(StampDAO stampDAO) {
        this.stampDAO = stampDAO;
    }
}
