// RobotBuilder Version: 2.0BB
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc330.commands.commandgroups;

import edu.wpi.first.wpilibj.command.BBCommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

import org.usfirst.frc330.commands.*;
import org.usfirst.frc330.commands.CoordinatedMove;
import org.usfirst.frc330.commands.RollerOff;
import org.usfirst.frc330.commands.SetLiftPosition;
import org.usfirst.frc330.constants.ArmConst;
import org.usfirst.frc330.constants.HandConst;
import org.usfirst.frc330.constants.LiftConst;
import org.usfirst.frc330.subsystems.*;

/**
 *
 */
public class SensorlessPickupUp extends BBCommandGroup {


    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=PARAMETERS
    public SensorlessPickupUp() {
    	
    	addSequential(new CloseClaw());
    	addSequential(new WaitCommand(0.8));
    	addSequential(new RollerOff());
    	
    	addParallel(new SetLiftPosition(LiftConst.defensePosition));
    	addSequential(new CoordinatedMove(ArmConst.Defense, HandConst.Defense));
 
    } 
}