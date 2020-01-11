package frc.robot.maps;

import com.chopshop166.chopshoplib.outputs.SendableSpeedController;
import com.chopshop166.chopshoplib.sensors.IEncoder;
import com.chopshop166.chopshoplib.sensors.Lidar;
import com.chopshop166.chopshoplib.sensors.MockEncoder;
import com.chopshop166.chopshoplib.sensors.PIDGyro;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import frc.robot.RobotMap;

public class SimMap implements RobotMap {

    @Override
    public LiftMap getLiftMap() {
        return new LiftMap() {

            @Override
            public DigitalInput getUpperLimit() {
                return new DigitalInput(9);
            }

            @Override
            public SendableSpeedController getMotor() {
                return SendableSpeedController.wrap(new PWMTalonSRX(1));
            }

            @Override
            public DigitalInput getLowerLimit() {
                return new DigitalInput(8);
            }

            @Override
            public IEncoder getHeightEncoder() {
                return new MockEncoder();
            }

            @Override
            public DoubleSolenoid getBrake() {
                return new DoubleSolenoid(0, 1);
            }

            @Override
            public DoubleSolenoid getArmsPiston() {
                return new DoubleSolenoid(2, 3);
            }
        };
    }

    @Override
    public ManipulatorMap getManipulatorMap() {
        return new ManipulatorMap() {

            @Override
            public SendableSpeedController getrollersMotor() {
                PWMTalonSRX cargoMotorController = new PWMTalonSRX(10);
                cargoMotorController.setInverted(true);
                return SendableSpeedController.wrap(cargoMotorController);
            }

            @Override
            public DigitalInput getintakePositionLimitSwitch() {
                return new DigitalInput(5);
            }

            @Override
            public DigitalInput getfoldedBackLimitSwitch() {
                return new DigitalInput(6);
            }

            @Override
            public DoubleSolenoid getbeaksPiston() {
                return new DoubleSolenoid(7, 6);
            }

            @Override
            public DigitalInput getGamepieceLimitSwitch() {
                return new DigitalInput(4);
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
                return SendableSpeedController.wrap(new PWMTalonSRX(2));
            }

            @Override
            public SendableSpeedController getLeft() {
                return SendableSpeedController.wrap(new PWMTalonSRX(3));
            }

            @Override
            public PIDGyro getGyro() {
                return PIDGyro.wrap(new AnalogGyro(0));
            }

            @Override
            public Encoder getLeftEncoder() {
                Encoder leftEncoder = new Encoder(0, 1);
                leftEncoder.setDistancePerPulse(Math.PI * 6 / 128);
                return leftEncoder;
            }

            @Override
            public Encoder getRightEncoder() {
                Encoder rightEncoder = new Encoder(2, 3);
                rightEncoder.setDistancePerPulse(Math.PI * 6 / 128);
                return rightEncoder;
            }

            @Override
            public DoubleSolenoid getClimbPiston() {
                return new DoubleSolenoid(4, 5);
            }
        };
    }
}