package br.com.keyrus.warmup.core.service;

import de.hybris.platform.core.model.media.MediaModel;

import java.io.File;

public interface CustomMediaService {
    MediaModel createMediaModel(String name, File file);
}
