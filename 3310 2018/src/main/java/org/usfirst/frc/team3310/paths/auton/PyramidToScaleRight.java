package org.usfirst.frc.team3310.paths.auton;

import java.util.ArrayList;

import org.usfirst.frc.team3310.paths.PathBuilder;
import org.usfirst.frc.team3310.paths.PathBuilder.Waypoint;
import org.usfirst.frc.team3310.paths.PathContainer;
import org.usfirst.frc.team3310.utility.control.Path;
import org.usfirst.frc.team3310.utility.math.RigidTransform2d;
import org.usfirst.frc.team3310.utility.math.Rotation2d;
import org.usfirst.frc.team3310.utility.math.Translation2d;


public class PyramidToScaleRight implements PathContainer {
    
    @Override
    public Path buildPath() {
        ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(100,165,0,60));
        sWaypoints.add(new Waypoint(70,165,20,60));
        sWaypoints.add(new Waypoint(80,72,45,60,  "shiftHi"));
        sWaypoints.add(new Waypoint(230,50,0,130,  "raiseElevator"));
        sWaypoints.add(new Waypoint(400,40,0,130));

        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }
    
    @Override
    public RigidTransform2d getStartPose() {
        return new RigidTransform2d(new Translation2d(100, 165), Rotation2d.fromDegrees(0)); 
    }

    @Override
    public boolean isReversed() {
        return true; 
    }
}