package frc.robot;

import com.chopshop166.chopshoplib.outputs.SendableSpeedController;
import com.chopshop166.chopshoplib.sensors.Lidar;
import com.chopshop166.chopshoplib.sensors.PIDGyro;
import com.chopshop166.chopshoplib.sensors.SparkMaxCounter;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;

/**
 * The RobotMap is an interface that contains the instructions on how to make a
 * particular robot. This is useful for situations of practice robot vs real
 * robot, where port numbers might differ.
 */
public interface RobotMap {
    // In the interface, create a function that returns a sensor interface or
    // SendableSpeedController.
    // In the map implementations, fill in that function with the specific instance.
    LiftMap getLiftMap();

    public interface LiftMap {

        CANSparkMax getMotor();

        DoubleSolenoid getBrake();

        SparkMaxCounter getHeightEncoder();

        DigitalInput getLowerLimit();

        DigitalInput getUpperLimit();
    }

    ManipulatorMap getManipulatorMap();

    interface ManipulatorMap {

        SendableSpeedController getrollersMotor();

        DoubleSolenoid getbeaksPiston();

        DoubleSolenoid getArmsPiston();

        DigitalInput getGamepieceLimitSwitch();

        DigitalInput getfoldedBackLimitSwitch();

        DigitalInput getintakePositionLimitSwitch();
    }

    DriveMap getDriveMap();

    public interface DriveMap {
        SendableSpeedController getLeft();

        SendableSpeedController getRight();

        DoubleSolenoid getClimbPiston();

        Lidar getLidar();

        Encoder getLeftEncoder();

        Encoder getRightEncoder();

        PIDGyro getGyro();
    }
}
