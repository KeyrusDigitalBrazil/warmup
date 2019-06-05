package br.com.keyrus.warmup.core.media.service.impl;

import br.com.keyrus.warmup.core.media.service.KeyrusMediaService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.media.impl.DefaultMediaService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static br.com.keyrus.warmup.core.constants.KeyruswarmupCoreConstants.*;
import static br.com.keyrus.warmup.core.util.StampUtils.validateStamp;


public class DefaultKeyrusMediaService extends DefaultMediaService implements KeyrusMediaService {

    public static final Logger LOG = Logger.getLogger(DefaultKeyrusMediaService.class);

    private CatalogVersionService catalogVersionService;
    private FlexibleSearchService flexibleSearchService;

    @Override
    public MediaModel createMediaModel(final String name, final File file) throws FileNotFoundException {

        validateStamp(file);

        MediaModel media;

        try {

            media = new MediaModel();
            media.setCode(name);

            media = flexibleSearchService.getModelByExample(media);

        } catch (final ModelNotFoundException e) {

            LOG.debug("Media not found for code '" + name + "'!", e);

            media = getModelService().create(MediaModel.class);

            media.setCode(name);
            media.setRealFileName(name);
            media.setCatalogVersion(catalogVersionService.getCatalogVersion(DEFAULT_PRODUCT_CATALOG_NAME, DEFAULT_PRODUCT_CATALOG_VERSION));
            media.setMediaFormat(getFormat(DEFAULT_MEDIA_FORMAT));
            media.setMime(DEFAULT_MEDIA_MIME_TYPE);
            media.setFolder(getFolder(DEFAULT_MEDIA_FOLDER));

            getModelService().save(media);
        }

        setStreamForMedia(media, new FileInputStream(file));

        return media;
    }

    public void setCatalogVersionService(CatalogVersionService catalogVersionService) {
        this.catalogVersionService = catalogVersionService;
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
