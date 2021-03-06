package frc.robot;

public class Utils {
	public static double ensureRange(double v, double min, double max) {
		return Math.min(Math.max(v, min), max);
	}
	
	public static double negPowTwo(double v) {
		return (v != 0) ? Math.pow(v, 2)*(Math.abs(v)/v) : 0; 
	}
	
	public static double keepRange(double v){
		if (v > 1.0){
			return 1.0;
		} else {
			return v;
		}	
	}

	public static double addAngle(double ang1, double ang2){
		return Math.acos(Math.cos(ang1)*Math.cos(ang2));
	}
	public static double distFrom(double tx, double ty){
		return Constants.CAMERA_HEIGHT*Math.tan(addAngle(tx,Math.min(ty+Constants.CAMERA_ANGLE,1.57)));

	}
	public static double degToRad(double deg){
		return deg*Math.PI/180.0;
	}

	public static double inchesToEncs(double inches){
		//Based on wheel circumfrence, perform calculation for inches.
		return inches;
	}

	public static double expoDeadzone(double input, double deadzone, double exponent){
		//http://www.mimirgames.com/articles/games/joystick-input-and-using-deadbands/
		if(Math.abs(input)<=deadzone)
			return 0;
		double deadzoned = (input - Math.signum(input) * deadzone)/(1-deadzone);
		//System.out.println(deadzoned);
		double expoed = Math.pow(Math.abs(deadzoned), exponent) * Math.signum(deadzoned);

		return expoed;
	}


}