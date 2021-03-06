package org.usfirst.frc.team3310.paths.auton;

import java.util.ArrayList;

import org.usfirst.frc.team3310.paths.PathBuilder;
import org.usfirst.frc.team3310.paths.PathBuilder.Waypoint;
import org.usfirst.frc.team3310.paths.PathContainer;
import org.usfirst.frc.team3310.utility.control.Path;
import org.usfirst.frc.team3310.utility.math.RigidTransform2d;
import org.usfirst.frc.team3310.utility.math.Rotation2d;
import org.usfirst.frc.team3310.utility.math.Translation2d;


public class SwitchLeftToNullZone implements PathContainer {
    
    @Override
    public Path buildPath() {
        ArrayList<Waypoint> sWaypoints = new ArrayList<Waypoint>();
        sWaypoints.add(new Waypoint(72,246,0,40));
        sWaypoints.add(new Waypoint(97,250,10,40));
        sWaypoints.add(new Waypoint(132,280,20,60));
        sWaypoints.add(new Waypoint(242,270,10,60));
        sWaypoints.add(new Waypoint(242,250,0,60));

        return PathBuilder.buildPathFromWaypoints(sWaypoints);
    }
    
    @Override
    public RigidTransform2d getStartPose() {
        return new RigidTransform2d(new Translation2d(72, 246), Rotation2d.fromDegrees(0.0)); 
    }

    @Override
    public boolean isReversed() {
        return false; 
    }
}