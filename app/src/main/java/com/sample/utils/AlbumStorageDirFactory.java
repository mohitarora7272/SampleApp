package com.sample.utils;

import java.io.File;

/**
 * @author by Mohit Arora on 27/12/17.
 */

public abstract class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}
