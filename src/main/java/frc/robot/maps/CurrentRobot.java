package frc.robot.maps;

import com.chopshop166.chopshoplib.outputs.MockSpeedController;
import com.chopshop166.chopshoplib.outputs.SendableSpeedController;
import com.chopshop166.chopshoplib.sensors.Lidar;
import com.chopshop166.chopshoplib.sensors.PIDGyro;
import com.chopshop166.chopshoplib.sensors.SparkMaxCounter;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import frc.robot.RobotMap;

public class CurrentRobot implements RobotMap {

    @Override
    public LiftMap getLiftMap() {
        return new LiftMap() {
            CANSparkMax liftMotor = new CANSparkMax(15, MotorType.kBrushless);

            @Override
            public DigitalInput getUpperLimit() {
                return new DigitalInput(9);
            }

            @Override
            public CANSparkMax getMotor() {
                liftMotor.setInverted(true);
                return liftMotor;
            }

            @Override
            public DigitalInput getLowerLimit() {
                return new DigitalInput(8);
            }

            @Override
            public SparkMaxCounter getHeightEncoder() {
                return new SparkMaxCounter(liftMotor.getEncoder());
            }

            @Override
            public DoubleSolenoid getBrake() {
                return new DoubleSolenoid(0, 1);
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
                return new DigitalInput(5);
            }

            @Override
            public DigitalInput getfoldedBackLimitSwitch() {
                return new DigitalInput(6);
            }

            @Override
            public DoubleSolenoid getbeaksPiston() {
                return new DoubleSolenoid(6, 7);
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
                return SendableSpeedController
                        .wrap(new SpeedControllerGroup(new WPI_VictorSPX(3), new WPI_VictorSPX(4)));
            }

            @Override
            public SendableSpeedController getLeft() {
                return SendableSpeedController
                        .wrap(new SpeedControllerGroup(new WPI_VictorSPX(6), new WPI_VictorSPX(5)));
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

    @Override
    public MaflipulatorMap getMaflipulatorMap() {
        return new MaflipulatorMap() {

            @Override
            public SendableSpeedController getFlipMotor() {
                return SendableSpeedController.wrap(new WPI_TalonSRX(10));
            }

            @Override
            public Potentiometer getMaflipulatorPot() {
                AnalogPotentiometer potentiometer = new AnalogPotentiometer(3);
                return potentiometer;

            }
        };
    }
}