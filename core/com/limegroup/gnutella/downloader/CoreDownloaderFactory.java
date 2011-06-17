package com.limegroup.gnutella.downloader;

import java.io.File;

import org.limewire.io.InvalidDataException;

import com.limegroup.gnutella.GUID;
import com.limegroup.gnutella.RemoteFileDesc;
import com.limegroup.gnutella.SaveLocationException;
import com.limegroup.gnutella.browser.MagnetOptions;
import com.limegroup.gnutella.version.DownloadInformation;

/**
 * Constructs all kinds of {@link CoreDownloader CoreDownloaders}.<p>
 * 
 * This handles creating downloads from data as well as from mementos
 * of prior downloads.
 */
public interface CoreDownloaderFactory {

    public ManagedDownloader createManagedDownloader(RemoteFileDesc[] files,
            GUID originalQueryGUID, File saveDirectory, String fileName, boolean overwrite)
            throws SaveLocationException;

    public MagnetDownloader createMagnetDownloader(MagnetOptions magnet, boolean overwrite,
            File saveDir, String fileName) throws SaveLocationException;

    public InNetworkDownloader createInNetworkDownloader(DownloadInformation info, File dir,
            long startTime) throws SaveLocationException;

    public ResumeDownloader createResumeDownloader(File incompleteFile, String name, long size)
            throws SaveLocationException;
}
