package com.homedepot.bootcamp.dockdoors.rest;


import com.homedepot.bootcamp.dockdoors.model.Door;
import com.homedepot.bootcamp.dockdoors.repository.DoorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api")
@CrossOrigin("*")
public class DoorController {

    @Autowired
    DoorRepository doorRepository;

    public DoorController(DoorRepository doorRepository) {
        this.doorRepository = doorRepository;
    }

    @RequestMapping(value = "/getAllDoors", method = RequestMethod.GET)
    public ResponseEntity<?> getDoors() {
        try {
            return new ResponseEntity<>(doorRepository.getAllDoors(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }


    }

    @RequestMapping(value = "/getADoor", method = RequestMethod.GET)
    public ResponseEntity<?> read(@RequestParam(value = "doorId" , required=false) String doorId) {
        try {
            return new ResponseEntity<>(doorRepository.getDoor(doorId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }


    }

    @RequestMapping(value = "/addDoor", method = RequestMethod.POST)
    public ResponseEntity<?> insert(@RequestBody Door door) {
        try {
            Door responseDoor = doorRepository.createDoor(door);
            if(responseDoor!=null)
            return new ResponseEntity<>(responseDoor, HttpStatus.OK);
            else
            {
                return new ResponseEntity<>("Data Error", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }


    }

}
