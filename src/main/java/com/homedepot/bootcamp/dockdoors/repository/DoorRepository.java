package com.homedepot.bootcamp.dockdoors.repository;

import com.homedepot.bootcamp.dockdoors.model.Door;


import java.util.List;

public interface DoorRepository {

    public List<Door> getAllDoors();

    public Door getDoor(String doorId);

    public Door createDoor(Door door);



}
