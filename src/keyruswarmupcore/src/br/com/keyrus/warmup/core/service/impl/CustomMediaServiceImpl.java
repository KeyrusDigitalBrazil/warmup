package br.com.keyrus.warmup.core.service.impl;

import br.com.keyrus.warmup.core.service.CustomMediaService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.impl.DefaultMediaService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class CustomMediaServiceImpl extends DefaultMediaService implements CustomMediaService {

    private static final Logger LOGGER = Logger.getLogger(CustomMediaServiceImpl.class);

    private CatalogVersionService catalogVersionService;
    private FlexibleSearchService flexibleSearchService;

    @Override
    public MediaModel createMediaModel(String name, File file) {
        MediaModel media = new MediaModel();
        media.setCode(name);
        List<MediaModel> results = flexibleSearchService.getModelsByExample(media);
        if(results == null || results.isEmpty()){
            LOGGER.info("Creating new media " + name);
            media = getModelService().create(MediaModel.class);
            media.setCode(name);
            media.setRealFileName(name);
            media.setCatalogVersion(catalogVersionService.getCatalogVersion("electronicsProductCatalog", "Online"));
            media.setMediaFormat(getFormat("desktop"));
            media.setMime("image/jpeg");
            media.setFolder(getFolder("images"));
            getModelService().save(media);
        }else{
            media = results.get(0);
        }
        try {
            setStreamForMedia(media, new FileInputStream(file));
        }catch (IOException e){
            throw  new RuntimeException(e);
        }
        return media;
    }

    public CatalogVersionService getCatalogVersionService() {
        return catalogVersionService;
    }

    public void setCatalogVersionService(CatalogVersionService catalogVersionService) {
        this.catalogVersionService = catalogVersionService;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    @Override
    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
