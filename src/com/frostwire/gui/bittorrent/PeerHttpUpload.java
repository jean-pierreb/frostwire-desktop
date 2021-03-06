/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml)
 * Copyright (c) 2011-2014, FrostWire(R). All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.frostwire.gui.bittorrent;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.frostwire.core.FileDescriptor;
import com.frostwire.transfers.TransferItem;
import com.frostwire.transfers.TransferState;
import com.frostwire.transfers.UploadTransfer;

/**
 * @author gubatron
 * @author aldenml
 *
 */
public class PeerHttpUpload implements UploadTransfer {

    private static final int STATUS_UPLOADING = 1;
    private static final int STATUS_COMPLETE = 2;
    private static final int STATUS_CANCELLED = 3;

    private static final int SPEED_AVERAGE_CALCULATION_INTERVAL_MILLISECONDS = 1000;

    /*
     * Quick and not so elegant solution to count upload speed.
     * The reason for this is that we need a huge refactor to handle
     * "transfers" in a generic way
     */
    private static final List<PeerHttpUpload> uploads = Collections.synchronizedList(new LinkedList<PeerHttpUpload>());

    //private final TransferManager manager;
    private final FileDescriptor fd;
    private final Date dateCreated;

    private int status;
    public long bytesSent;
    public long averageSpeed; // in bytes

    // variables to keep the upload rate of this transfer
    private long speedMarkTimestamp;
    private long totalSentSinceLastSpeedStamp;

    public PeerHttpUpload(/*TransferManager manager,*/FileDescriptor fd) {
        //this.manager = manager;
        this.fd = fd;
        this.dateCreated = new Date();

        status = STATUS_UPLOADING;

        uploads.add(this);
    }

    public FileDescriptor getFD() {
        return fd;
    }

    public String getDisplayName() {
        return fd.title;
    }

    @Override
    public String getName() {
        return getDisplayName();
    }

    public int getProgress() {
        return isComplete() ? 100 : (int) ((bytesSent * 100) / fd.fileSize);
    }

    public long getSize() {
        return fd.fileSize;
    }

    public Date getCreated() {
        return dateCreated;
    }

    public long getBytesReceived() {
        return 0;
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public long getDownloadSpeed() {
        return 0;
    }

    public long getUploadSpeed() {
        return isComplete() ? 0 : averageSpeed;
    }

    public long getETA() {
        long speed = getUploadSpeed();
        return speed > 0 ? (fd.fileSize - getBytesSent()) / speed : Long.MAX_VALUE;
    }

    public boolean isComplete() {
        return bytesSent == fd.fileSize;
    }

    public boolean isUploading() {
        return status == STATUS_UPLOADING;
    }

    public List<TransferItem> getItems() {
        return Collections.emptyList();
    }

    public void remove() {
        if (status != STATUS_COMPLETE) {
            status = STATUS_CANCELLED;
        }
        uploads.remove(this);
    }

    public void addBytesSent(int n) {
        bytesSent += n;
        updateAverageUploadSpeed();
    }

    public void complete() {
        status = STATUS_COMPLETE;
        remove();
    }

    public boolean isCanceled() {
        return status == STATUS_CANCELLED;
    }

    public TransferState getState() {
        switch (status) {
        case STATUS_UPLOADING:
            return (getUploadSpeed() < 102400) ? TransferState.STREAMING  : TransferState.UPLOADING;
        case STATUS_COMPLETE:
            return TransferState.COMPLETE;
        case STATUS_CANCELLED:
            return TransferState.CANCELED;
        default:
            return TransferState.UNKNOWN;
        }
    }

    private void updateAverageUploadSpeed() {
        long now = System.currentTimeMillis();

        if (now - speedMarkTimestamp > SPEED_AVERAGE_CALCULATION_INTERVAL_MILLISECONDS) {
            averageSpeed = ((bytesSent - totalSentSinceLastSpeedStamp) * 1000) / (now - speedMarkTimestamp);
            speedMarkTimestamp = now;
            totalSentSinceLastSpeedStamp = bytesSent;
        }
    }

    public static long getUploadsBandwidth() {
        try {
            if (uploads.size() == 0) {
                return 0;
            }

            long bandwidth = 0;

            synchronized (uploads) {
                for (PeerHttpUpload u : uploads) {
                    bandwidth += u.getUploadSpeed();
                }
            }

            return bandwidth;
        } catch (Throwable e) {
            // just in case, synchronization and collections errors, remove it in the future
            return 0;
        }
    }

    @Override
    public File getSavePath() {
        return null;
    }
}
