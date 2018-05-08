package com.homedepot.bootcamp.dockdoors.util;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import org.springframework.stereotype.Service;



@Service
public class DBSpannerService {

    private static String project="bootcamp-102288",instance="homedepot",database="casestudy";


        public DatabaseClient getDBConnection() {
        DatabaseClient dBClient = null;
        try {
            SpannerOptions options = SpannerOptions.newBuilder().build();
            Spanner spanner = options.getService();
            dBClient = spanner.getDatabaseClient(DatabaseId.of(project, instance, database));
        } catch(Exception e) {
            System.out.println("Error"+e);
        }
        return dBClient;
    }




}
