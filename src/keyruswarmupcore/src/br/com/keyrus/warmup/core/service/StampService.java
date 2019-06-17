package br.com.keyrus.warmup.core.service;

import br.com.keyrus.warmup.core.model.StampModel;

import java.io.File;
import java.io.FileNotFoundException;

public interface StampService {

        void createStamp(final File file);
}
