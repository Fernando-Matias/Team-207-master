package org.usfirst.frc.team3310.robot.commands;

import org.usfirst.frc.team3310.robot.Robot;
import org.usfirst.frc.team3310.robot.subsystems.Drive.DriveSpeedShiftState;

import edu.wpi.first.wpilibj.command.Command;

public class DriveSpeedShiftSpeedControl extends Command
{
	private DriveSpeedShiftState state;
	
	public DriveSpeedShiftSpeedControl(DriveSpeedShiftState state) {
		requires(Robot.drive);
		this.state = state;
	}

	@Override
	protected void initialize() {
		Robot.drive.setShiftState(state);
	}

	@Override
	protected void execute() {
		
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
		
	}

	@Override
	protected void interrupted() {
			
	}
}