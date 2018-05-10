package com.homedepot.bootcamp.dockdoors.repository.spanner;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.homedepot.bootcamp.dockdoors.model.Door;
import com.homedepot.bootcamp.dockdoors.repository.DoorRepository;
import com.homedepot.bootcamp.dockdoors.util.DBSpannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Repository
public class DoorRepositorySpannerImpl implements DoorRepository{


    private DBSpannerService dbSpannerService;


    public DoorRepositorySpannerImpl(DBSpannerService dbSpannerService) {
        this.dbSpannerService = dbSpannerService;
    }


    @Override
    public List<Door> getAllDoors() {
        List<Door> doors = new ArrayList<>();

        try {
            String sqlQuery = "SELECT  * FROM door";

            ResultSet rs = dbSpannerService.getDBConnection().singleUse().executeQuery(Statement.of(sqlQuery));

            while (rs.next()) {
                Door door = new Door();
                door.setDoorId(rs.getString("door_id"));
                door.setDoorName(rs.getString("name"));

                doors.add(door);
            }
            return doors;

        } catch (Exception e) {
            System.out.println("Error : "+e);
            return null;
        }
    }

    @Override
    public Door getDoor(String doorId) {
        Door door = new Door();
        try {
            String sqlQuery = "SELECT  * FROM door where door_id='"+doorId+"'";

            ResultSet rs = dbSpannerService.getDBConnection().singleUse().executeQuery(Statement.of(sqlQuery));

            while (rs.next()) {
                door.setDoorId(rs.getString("door_id"));
                door.setDoorName(rs.getString("name"));
            }
            return door;

        } catch (Exception e) {
            System.out.println("Error : "+e);
            return null;
        }
    }

    @Override
    public Door createDoor(Door door) {

        try {
            final UUID id = UUID.randomUUID();
            System.out.println("doorId "+id);
            String doorId = id.toString();
            System.out.println("doorId "+doorId);
           dbSpannerService.getDBConnection().write(new ArrayList(
                   Collections.singleton(Mutation.newInsertBuilder("door")
                           .set("door_id").to(doorId)
                           .set("name").to(door.getDoorName())
                           .build())
            ));

           return new Door(doorId,door.getDoorName());

        } catch (Exception e) {
            System.out.println("Error : "+e);
            return null;
        }
    }

}
