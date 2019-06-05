package br.com.keyrus.warmup.core.stamp.service;

import br.com.keyrus.warmup.core.model.StampModel;
import de.hybris.platform.core.model.media.MediaModel;

public interface StampService {

    StampModel createStamp(final String code, final MediaModel media, final Integer priority);

}
