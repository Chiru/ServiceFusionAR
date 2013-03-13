package fi.cie.chiru.servicefusionar.sensors;

public interface OrientationListener
{
	void newStatus(int status, final String info);
	void newResult(final String sensor, final float[] result);
	void newResult(final String sensor, final float[] result, final String format);
	void newLocation(double latitude, double longitude);
	void newOrientation(double angle);
	void isTilt(Boolean bool);
	void isCalibrated(final String sensor, Boolean bool);
}
