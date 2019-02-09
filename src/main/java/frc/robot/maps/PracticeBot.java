package frc.robot.maps;

import com.chopshop166.chopshoplib.outputs.MockSpeedController;
import com.chopshop166.chopshoplib.outputs.SendableSpeedController;
import com.chopshop166.chopshoplib.sensors.Lidar;
import com.chopshop166.chopshoplib.sensors.MockPotentiometer;
import com.chopshop166.chopshoplib.sensors.PIDGyro;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import frc.robot.RobotMap;

public class PracticeBot implements RobotMap {

    @Override
    public LiftMap getLiftMap() {
        return new LiftMap() {

            @Override
            public DigitalInput getUpperLimit() {
                return new DigitalInput(4);
            }

            @Override
            public SendableSpeedController getMotor() {
                return new MockSpeedController();
            }

            @Override
            public Potentiometer getManipAngle() {
                return new MockPotentiometer();
            }

            @Override
            public DigitalInput getLowerLimit() {
                return new DigitalInput(0);
            }

            @Override
            public Encoder getHeightEncoder() {
                return new Encoder(3, 5);
            }

            @Override
            public DoubleSolenoid getBrake() {
                return new DoubleSolenoid(2, 7);
            }

            @Override
            public SendableSpeedController getArmMotor() {
                return new MockSpeedController();
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
            public SendableSpeedController getpivotPointsMotor() {
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
                return new MockSpeedController();
            }

            @Override
            public SendableSpeedController getLeft() {
                return new MockSpeedController();
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
                return new Encoder(3, 4);
            }
        };
    }

    public MaflipulatorMap getMaflipulatorMap() {
        return new MaflipulatorMap() {

            @Override
            public SendableSpeedController getFlipMotor() {
                return new MockSpeedController();
            }

            @Override
            public Potentiometer getMaflipulatorPot() {
                return new MockPotentiometer();
            }
        };
    }
}