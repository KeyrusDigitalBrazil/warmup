package br.com.keyrus.warmup.core.media.service;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;

import java.io.File;
import java.io.FileNotFoundException;

public interface KeyrusMediaService extends MediaService {

    MediaModel createMediaModel(final String name, final File file) throws FileNotFoundException;

}
