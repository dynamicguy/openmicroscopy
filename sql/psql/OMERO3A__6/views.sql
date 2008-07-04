--
-- Copyright 2008 Glencoe Software, Inc. All rights reserved.
-- Use is subject to license terms supplied in LICENSE.txt
--

-- This file was generated by dsl/resources/ome/dsl/views.vm
-- and can be used to overwrite the generated Map<Long, Long> tables
-- with functional views.

BEGIN;

  DROP TABLE count_Plate_screenLinks_by_owner;

  CREATE OR REPLACE VIEW count_Plate_screenLinks_by_owner (Plate_id, owner_id, count) AS select child, owner_id, count(*)
    FROM ScreenPlateLink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Plate_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Plate_annotationLinks_by_owner (Plate_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM PlateAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Pixels_pixelsFileMaps_by_owner;

  CREATE OR REPLACE VIEW count_Pixels_pixelsFileMaps_by_owner (Pixels_id, owner_id, count) AS select child, owner_id, count(*)
    FROM PixelsOriginalFileMap GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Pixels_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Pixels_annotationLinks_by_owner (Pixels_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM PixelsAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_ExperimenterGroup_groupExperimenterMap_by_owner;

  CREATE OR REPLACE VIEW count_ExperimenterGroup_groupExperimenterMap_by_owner (ExperimenterGroup_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM GroupExperimenterMap GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Screen_plateLinks_by_owner;

  CREATE OR REPLACE VIEW count_Screen_plateLinks_by_owner (Screen_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ScreenPlateLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Screen_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Screen_annotationLinks_by_owner (Screen_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ScreenAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_OriginalFile_pixelsFileMaps_by_owner;

  CREATE OR REPLACE VIEW count_OriginalFile_pixelsFileMaps_by_owner (OriginalFile_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM PixelsOriginalFileMap GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_OriginalFile_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_OriginalFile_annotationLinks_by_owner (OriginalFile_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM OriginalFileAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Annotation_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Annotation_annotationLinks_by_owner (Annotation_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM AnnotationAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Project_datasetLinks_by_owner;

  CREATE OR REPLACE VIEW count_Project_datasetLinks_by_owner (Project_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ProjectDatasetLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Project_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Project_annotationLinks_by_owner (Project_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ProjectAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_RoiLink_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_RoiLink_annotationLinks_by_owner (RoiLink_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM RoiLinkAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_ScreenAcquisition_wellSampleLinks_by_owner;

  CREATE OR REPLACE VIEW count_ScreenAcquisition_wellSampleLinks_by_owner (ScreenAcquisition_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ScreenAcquisitionWellSampleLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_ScreenAcquisition_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_ScreenAcquisition_annotationLinks_by_owner (ScreenAcquisition_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ScreenAcquisitionAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_CategoryGroup_categoryLinks_by_owner;

  CREATE OR REPLACE VIEW count_CategoryGroup_categoryLinks_by_owner (CategoryGroup_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM CategoryGroupCategoryLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_WellSample_imageLinks_by_owner;

  CREATE OR REPLACE VIEW count_WellSample_imageLinks_by_owner (WellSample_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM WellSampleImageLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_WellSample_screenAcquisitionLinks_by_owner;

  CREATE OR REPLACE VIEW count_WellSample_screenAcquisitionLinks_by_owner (WellSample_id, owner_id, count) AS select child, owner_id, count(*)
    FROM ScreenAcquisitionWellSampleLink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_WellSample_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_WellSample_annotationLinks_by_owner (WellSample_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM WellSampleAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Job_originalFileLinks_by_owner;

  CREATE OR REPLACE VIEW count_Job_originalFileLinks_by_owner (Job_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM JobOriginalFileLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Well_reagentLinks_by_owner;

  CREATE OR REPLACE VIEW count_Well_reagentLinks_by_owner (Well_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM WellReagentLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Well_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Well_annotationLinks_by_owner (Well_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM WellAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Category_categoryGroupLinks_by_owner;

  CREATE OR REPLACE VIEW count_Category_categoryGroupLinks_by_owner (Category_id, owner_id, count) AS select child, owner_id, count(*)
    FROM CategoryGroupCategoryLink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Category_imageLinks_by_owner;

  CREATE OR REPLACE VIEW count_Category_imageLinks_by_owner (Category_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM CategoryImageLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Experimenter_groupExperimenterMap_by_owner;

  CREATE OR REPLACE VIEW count_Experimenter_groupExperimenterMap_by_owner (Experimenter_id, owner_id, count) AS select child, owner_id, count(*)
    FROM GroupExperimenterMap GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Experimenter_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Experimenter_annotationLinks_by_owner (Experimenter_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ExperimenterAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Image_categoryLinks_by_owner;

  CREATE OR REPLACE VIEW count_Image_categoryLinks_by_owner (Image_id, owner_id, count) AS select child, owner_id, count(*)
    FROM CategoryImageLink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Image_datasetLinks_by_owner;

  CREATE OR REPLACE VIEW count_Image_datasetLinks_by_owner (Image_id, owner_id, count) AS select child, owner_id, count(*)
    FROM DatasetImageLink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Image_sampleLinks_by_owner;

  CREATE OR REPLACE VIEW count_Image_sampleLinks_by_owner (Image_id, owner_id, count) AS select child, owner_id, count(*)
    FROM WellSampleImageLink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Image_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Image_annotationLinks_by_owner (Image_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ImageAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Channel_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Channel_annotationLinks_by_owner (Channel_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ChannelAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_PlaneInfo_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_PlaneInfo_annotationLinks_by_owner (PlaneInfo_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM PlaneInfoAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Reagent_wellLinks_by_owner;

  CREATE OR REPLACE VIEW count_Reagent_wellLinks_by_owner (Reagent_id, owner_id, count) AS select child, owner_id, count(*)
    FROM WellReagentLink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Reagent_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Reagent_annotationLinks_by_owner (Reagent_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM ReagentAnnotationLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Dataset_projectLinks_by_owner;

  CREATE OR REPLACE VIEW count_Dataset_projectLinks_by_owner (Dataset_id, owner_id, count) AS select child, owner_id, count(*)
    FROM ProjectDatasetLink GROUP BY child, owner_id ORDER BY child;

  DROP TABLE count_Dataset_imageLinks_by_owner;

  CREATE OR REPLACE VIEW count_Dataset_imageLinks_by_owner (Dataset_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM DatasetImageLink GROUP BY parent, owner_id ORDER BY parent;

  DROP TABLE count_Dataset_annotationLinks_by_owner;

  CREATE OR REPLACE VIEW count_Dataset_annotationLinks_by_owner (Dataset_id, owner_id, count) AS select parent, owner_id, count(*)
    FROM DatasetAnnotationLink GROUP BY parent, owner_id ORDER BY parent;


COMMIT;
