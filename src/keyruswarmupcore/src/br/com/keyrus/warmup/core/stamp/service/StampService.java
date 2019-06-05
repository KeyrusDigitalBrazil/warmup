package br.com.keyrus.warmup.core.stamp.service;

import br.com.keyrus.warmup.core.model.StampModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;

public interface StampService {

    StampModel createStamp(final String code, final MediaModel media, final Integer priority);

    List<StampModel> getBestProductStamps(final ProductModel product);

}
