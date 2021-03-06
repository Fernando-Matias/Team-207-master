package org.usfirst.frc.team294.robot.commands;

import java.util.function.Supplier;

import org.omg.PortableServer.ImplicitActivationPolicyOperations;
import org.usfirst.frc.team294.robot.Robot;
import org.usfirst.frc.team294.robot.RobotMap;
import org.usfirst.frc.team294.utilities.ToleranceChecker;
import org.usfirst.frc.team294.utilities.VelocityChecker;
import org.usfirst.frc.team294.utilities.ProfileGenerator;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Used for when you want to drive straight at some specified angle, uses motion
 * profiling
 */
public class DriveStraightDistanceProfile extends Command {

	private boolean driveUsingDashboardParams = false;
	private double distErr = 0;
	private double intDistErr = 0;
	private double targetDistance, currentDistance;
	private ToleranceChecker tolCheck;
	private boolean success;
	private double distSpeedControl;

	private final double kPdist = .08, kDdist = .3, kIdist = 0.00, kFdist = 0;// .006; // not used
	// private final double kPdist = .05, kDdist = 0, kIdist = 0.00, kFdist = .009;
	// // not used
	// old kPdist = .05, .2 old kDdist = .37 old kFdist = .008

	private double prevDistErr;
	private double currentAngle;
	private double angleErr;
	private double intErr = 0;
	private double prevAngleErr;
	private double absPrevMPVel = 0;
	private double MPCurrentDistance;

	private double kPangle = .06;
	private double kIangle = .002;
	private double kDangle = .1;
	private final VelocityChecker velCheck = new VelocityChecker(0.3); // 0.4

	private boolean useVisionForAngle = false;
	private double curve;
	private double minSpeed = .1;
	private double angleBase;
	private ProfileGenerator trapezoid;
	private double MPSpeed, MPAccel;
	private double prevDistanceInches;
	
	private Command shiftLow = new Shift(false);
	private Command shiftHigh = new Shift(true); 

	/**
	 * Drive straight using a motion profile and default speed (80 in/sec) and
	 * default acceleration (80 in/sec^2).
	 * 
	 * @param distanceTravel
	 *            Distance to travel, in inches
	 * @param angleBase
	 *            Absolute angle for direction of travel, in degrees. 0 = away from
	 *            drivers, -90 = left (relative to drive stations), +90 = right
	 *            (relative to drive stations)
	 */
	public DriveStraightDistanceProfile(double distanceTravel, double angleBase) {
		this(distanceTravel, angleBase, 80, 80);
	}

	/**
	 * Drive straight using a motion profile.
	 * 
	 * @param distanceTravel
	 *            Distance to travel, in inches
	 * @param angleBase
	 *            Absolute angle for direction of travel, in degrees. 
	 *            <li> 0 = away from drivers, -90 = left (relative to drive stations), +90 = right
	 *            (relative to drive stations)
	 *            <li>***Special Case #1*** 9999 = go straight using current robot heading
	 *            <li>***Special Case #1*** 9998 = go towards cube using vision camera
	 * @param MPSpeed
	 *            Speed, in in/sec
	 * @param MPAccel
	 *            Acceleration, in in/sec^2
	 */
	public DriveStraightDistanceProfile(double distanceTravel, double angleBase, double MPSpeed, double MPAccel) {
		requires(Robot.driveTrain);
		driveUsingDashboardParams = false;
		this.targetDistance = distanceTravel;
		this.angleBase = angleBase;
		this.MPSpeed = MPSpeed;
		this.MPAccel = MPAccel;
	}

	/**
	 * Drive straight using a motion profile, using parameters from ShuffleBoard
	 * (DSDP_Distance_inches, DSDP_AngleBase, DSDP_Speed_ips, DSDP_Accel_ips2)
	 */
	public DriveStraightDistanceProfile() {
		requires(Robot.driveTrain);
		driveUsingDashboardParams = true;
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		// distanceTravel = SmartDashboard.getNumber("DistToTravelDSDG", 60);

		// Reset shifter
		shiftLow.start();
		
		if (driveUsingDashboardParams) {
			Robot.driveTrain.zeroGyroRotation();
			targetDistance = SmartDashboard.getNumber("DSDP_Distance_inches", 0);
			angleBase = SmartDashboard.getNumber("DSDP_AngleBase", 0);
			MPSpeed = SmartDashboard.getNumber("DSDP_Speed_ips", 80);
			MPAccel = SmartDashboard.getNumber("DSDP_Accel_ips2", 80);
		}
		
		// Special case:  If angle = 9999, then drive straight on current heading
		if (angleBase == 9999) {
			angleBase = Robot.driveTrain.getGyroRotation();
		}

		// Special case:  If angle = 9998, then drive using vision towards cube
		useVisionForAngle = (angleBase==9998);
		
		Robot.log.writeLogEcho("DriveStraightdistanceProfile,started,distance inches," + 
				targetDistance + ",angle," + angleBase + ",speed," + MPSpeed + ",accel," + MPAccel);

		distErr = 0;
		prevDistErr = 0;
		intDistErr = 0;
		absPrevMPVel = 0;
		angleErr = 0;
		success = false;
		distSpeedControl = 0;
		tolCheck = new ToleranceChecker(3, 5);
		velCheck.clearHistory();
		Robot.driveTrain.zeroLeftEncoder();
		Robot.driveTrain.zeroRightEncoder();
		trapezoid = new ProfileGenerator(0.0, targetDistance, 0, MPSpeed, MPAccel);
		// angleBase = Robot.driveTrain.getGyroRotation();
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		final double currentDistanceInches = Robot.driveTrain.getAverageEncoderInches();
		this.currentDistance = currentDistanceInches;
		trapezoid.updateProfileCalcs();
		MPCurrentDistance = trapezoid.getCurrentPosition();
		distErr = MPCurrentDistance - currentDistanceInches;
		intDistErr = intDistErr + intDistErr * 0.02;
		success = tolCheck.success(Math.abs(targetDistance - currentDistanceInches));

		
		
		if (!success) {
			//Auto Shift
			double absMPVel = Math.abs(trapezoid.getCurrentVelocity());
			if (absMPVel >= 80 && absPrevMPVel < 80)
			{
				shiftHigh.start();
			}else if(absMPVel < 80 && absPrevMPVel >= 80)
			{
				shiftLow.start();
			}
			absPrevMPVel = absMPVel;
			distSpeedControl = distErr * kPdist + ((distErr - prevDistErr) * kDdist)
					+ kIdist * intDistErr + (kFdist * trapezoid.getCurrentVelocity());

			if (distSpeedControl > 0) {
				distSpeedControl = (distSpeedControl < minSpeed) ? minSpeed : distSpeedControl;
			} else {
				distSpeedControl = (distSpeedControl > -minSpeed) ? -minSpeed : distSpeedControl;
			}

			// If using vision to drive towards cube, then dynamically set angleBase (dest angle)
			if (useVisionForAngle) {
				angleBase = Robot.driveTrain.getGyroRotation() + Robot.visionData.getCubeAngleRelative();
			}
			
			currentAngle = Robot.driveTrain.getGyroRotation();
			angleErr = angleBase - currentAngle;
			angleErr = (angleErr > 180) ? angleErr - 360 : angleErr;
			intErr = intErr + angleErr * 0.02;
			double dErr = angleErr - prevAngleErr;
			prevAngleErr = angleErr;
			curve = angleErr * kPangle + intErr * kIangle + dErr * kDangle;
			curve = (curve > 0.5) ? 0.5 : curve;
			curve = (curve < -0.5) ? -0.5 : curve;
			// curve = (targetDistance - currentDistanceInches >= 0) ? curve : -curve; //
			// Swap curve correction when in reverse

			Robot.driveTrain.driveAtCurve(distSpeedControl, curve);
			velCheck.addValue(currentDistanceInches - prevDistanceInches);
			prevDistErr = distErr;

			double diffInches = currentDistanceInches - prevDistanceInches;

			Robot.driveTrain
					.addFieldPositionX(diffInches * Math.cos(Math.toRadians(Robot.driveTrain.getGyroRotation())));
			Robot.driveTrain
					.addFieldPositionY(diffInches * Math.sin(Math.toRadians(Robot.driveTrain.getGyroRotation())));

			prevDistanceInches = currentDistanceInches;
		}

		Robot.log.writeLog("DriveStraightDistanceProfile,currentDistance," + currentDistanceInches + ",MPCurrentDistance," + MPCurrentDistance 
				+ ",distSpeedControl," + distSpeedControl + ",MPVelocity," 
				+ trapezoid.getCurrentVelocity() + ",tolCheckerValue," + tolCheck.success()
				+ ",velCheckAverage," + velCheck.getAverage()
				+ ",currentAngle," + currentAngle + ",targetAngle," + angleBase + ",curve," + curve); //DSD Profile Specific logging

		SmartDashboard.putNumber("Distance Calculated", MPCurrentDistance);
		SmartDashboard.putNumber("Distance Error", targetDistance - currentDistanceInches);
		SmartDashboard.putNumber("Actual Distance", currentDistance);
		SmartDashboard.putNumber("Actual Percent Power", distSpeedControl);
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		if (success) {
			SmartDashboard.putNumber("fDistance Calculated", MPCurrentDistance);
			SmartDashboard.putNumber("fSet Distance", targetDistance);
			SmartDashboard.putNumber("fActual Distance", currentDistance);
		}
		return (Math.abs(velCheck.getAverage()) < 0.05) || success; // was 0.2, but initial ramp-up triggered at 0.15 and the profile didn't run
	}

	// Called once after isFinished returns true
	protected void end() {
		// velCheck.dumpArray();
		Robot.log.writeLogEcho("DriveStraightDistanceProfile,ended,distError," + (targetDistance - currentDistance)
				+ ",velCheck," + velCheck.getAverage());
		Robot.driveTrain.driveAtCurve(0, 0);
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		Robot.log.writeLogEcho("DriveStraightdistanceProfile,interrupted");
		end();
	}
}
