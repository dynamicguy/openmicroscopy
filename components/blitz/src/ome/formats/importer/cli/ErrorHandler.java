/*
 *   $Id$
 *
 *   Copyright (C) 2008-2013 Glencoe Software, Inc. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */

package ome.formats.importer.cli;

import static ome.formats.importer.ImportEvent.*;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import ome.formats.importer.IObservable;
import ome.formats.importer.IObserver;
import ome.formats.importer.ImportConfig;
import ome.formats.importer.ImportEvent;
import ome.formats.importer.ImportLibrary;
import ome.formats.importer.ImportCandidates.SCANNING;
import ome.formats.importer.util.ErrorContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link IObserver} based on the gui ErrorHandler code which collects all
 * exceptions during
 * {@link ImportLibrary#importCandidates(ome.formats.importer.ImportConfig, ome.formats.importer.ImportCandidates)}
 * and after the import is finished offers to submit them via the feedback
 * system.
 *
 * @since Beta4.1
 */
public class ErrorHandler extends ome.formats.importer.util.ErrorHandler {

    private final static Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    public ErrorHandler(ImportConfig config) {
        super(config);
    }

    /**
     * Returns <code>true</code> if the error is generated by a plate,
     * <code>false</code> otherwise.
     *
     * @param reader The reader to handle.
     * @param path The absolute path to the file.
     * @return See above.
     */
    private boolean isPlate(String reader, String path) {
        boolean plate = false;
        try {
            Class<?> c = Class.forName(reader);
            FormatReader instance = (FormatReader) c.newInstance();
            instance.setId(path);
            String[] domains = instance.getDomains();
            for (int k = 0; k < domains.length; k++) {
                log.info(domains[k]);
                if (domains[k].equals(FormatTools.HCS_DOMAIN)) {
                    plate = true;
                    break;
                }
            }
        } catch (Exception e) {
            log.info("Cannot determine domain: "+e.toString());
        }
       
        return plate;
    }

    public void onUpdate(IObservable importLibrary, ImportEvent event) {

        if (event instanceof IMPORT_DONE) {
            log.info("Number of errors: " + errors.size());
        }

        else if (event instanceof SCANNING) {
            log.debug(event.toLog());
        }

        else if (event instanceof ImportEvent.DEBUG_SEND) {

            boolean plate = false;
            for (ErrorContainer error : errors) {
                error.setEmail(config.email.get());
                error.setComment("Sent from CLI");
                if (!plate) {
                    plate = isPlate(error.getFileFormat(),
                            error.getSelectedFile().getAbsolutePath());
                }
            }
            if (errors.size() > 0) {
                // Note: it wasn't the intent to have these variables set
                // here. This requires that subclasses know to call
                // super.onUpdate(). To prevent that, one could make this method
                // final and have an onOnUpdate, etc.
                sendFiles = ((ImportEvent.DEBUG_SEND) event).sendFiles;
                sendLogs = ((ImportEvent.DEBUG_SEND) event).sendLogs;
                if (plate) {
                    log.info("To submit HCS data, please e-mail us.");
                    sendFiles = false;
                }
                log.info("Sending error report "+ "(" + errors.size() + ")...");
                sendErrors();
                if (sendFiles) {
                    if (sendLogs)
                        log.info("Sent files and log file.");
                    else log.info("Sent files.");
                } else {
                    if (sendLogs) {
                        log.info("Sent log file.");
                    }
                }
            }

        }

    }

}
