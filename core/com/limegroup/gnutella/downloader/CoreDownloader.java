package com.limegroup.gnutella.downloader;

import java.io.File;

import com.limegroup.gnutella.Downloader;
import com.limegroup.gnutella.GUID;
import com.limegroup.gnutella.URN;

/**
 * Extends the {@link Downloader} interface to provide more functionality,
 * allowing the download to be more tightly managed.
 */
public interface CoreDownloader extends Downloader {

    /**
     * Sets the inactive priority of this download.
     */
    public void setInactivePriority(int priority);
    
    public GUID getQueryGUID();

    /**
     * Starts the download.
     */
    public void startDownload();

    /**
     * @return whether the download is still alive and cannot be
     * restarted.
     */
    public boolean isAlive();

    /**
     * @return whether it makes sense to restart this download.
     */
    public boolean shouldBeRestarted();

    /**
     * @return whether the download should be removed from 
     * the waiting list.
     */
    public boolean shouldBeRemoved();

    /**
     * Handles state changes and other operations while
     * inactive.
     */
    public void handleInactivity();

    public boolean isQueuable();

    /**
     * Cleans up any resources before this downloader 
     * is completely disposed.
     */
    public void finish();

    public boolean conflicts(URN urn, long fileSize, File... files);

    public boolean conflictsWithIncompleteFile(File incomplete);

    public boolean conflictsSaveFile(File saveFile);

    public void initialize();

    /**
     * Returns the type of download
     */
    public DownloaderType getDownloadType();
    
    public void setOverwrite(boolean override);
}