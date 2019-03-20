package frc.robot.maps;

import com.chopshop166.chopshoplib.outputs.MockSpeedController;
import com.chopshop166.chopshoplib.outputs.SendableSpeedController;
import com.chopshop166.chopshoplib.sensors.Lidar;
import com.chopshop166.chopshoplib.sensors.MockPotentiometer;
import com.chopshop166.chopshoplib.sensors.PIDGyro;
import com.chopshop166.chopshoplib.sensors.SparkMaxCounter;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import frc.robot.RobotMap;

public class Tempest implements RobotMap {

    @Override
    public LiftMap getLiftMap() {
        return new LiftMap() {

            @Override
            public DigitalInput getUpperLimit() {
                return new DigitalInput(4);
            }

            @Override
            public CANSparkMax getMotor() {
                // return new MockSpeedController();
                return null;
            }

            @Override
            public DigitalInput getLowerLimit() {
                return new DigitalInput(0);
            }

            @Override
            public SparkMaxCounter getHeightEncoder() {
                return null;
            }

            @Override
            public DoubleSolenoid getBrake() {
                return new DoubleSolenoid(2, 7);
            }
        };
    }

    @Override
    public ManipulatorMap getManipulatorMap() {
        return new ManipulatorMap() {

            @Override
            public SendableSpeedController getrollersMotor() {
                return new MockSpeedController();
            }

            @Override
            public DigitalInput getintakePositionLimitSwitch() {
                return new DigitalInput(1);
            }

            @Override
            public DigitalInput getfoldedBackLimitSwitch() {
                return new DigitalInput(2);
            }

            @Override
            public DoubleSolenoid getbeaksPiston() {
                return new DoubleSolenoid(2, 3);
            }

            @Override
            public DigitalInput getGamepieceLimitSwitch() {
                return new DigitalInput(3);
            }

            @Override
            public DoubleSolenoid getArmsPiston() {
                return new DoubleSolenoid(0, 1);
            }
        };
    }

    @Override
    public DriveMap getDriveMap() {
        return new DriveMap() {

            @Override
            public Lidar getLidar() {
                return new Lidar(Port.kOnboard, 0x10);
            }

            @Override
            public SendableSpeedController getRight() {
                SpeedControllerGroup right = new SpeedControllerGroup(new WPI_TalonSRX(4), new WPI_TalonSRX(1));
                return SendableSpeedController.wrap(right);
            }

            @Override
            public SendableSpeedController getLeft() {
                SpeedControllerGroup left = new SpeedControllerGroup(new WPI_TalonSRX(2), new WPI_TalonSRX(3));
                return SendableSpeedController.wrap(left);
            }

            @Override
            public PIDGyro getGyro() {
                return PIDGyro.mock();
            }

            @Override
            public Encoder getLeftEncoder() {
                return new Encoder(6, 7);
            }

            @Override
            public DoubleSolenoid getClimbPiston() {
                return new DoubleSolenoid(4, 5);
            }

            @Override
            public Encoder getRightEncoder() {
                return new Encoder(9, 8);
            }
        };
    }
}