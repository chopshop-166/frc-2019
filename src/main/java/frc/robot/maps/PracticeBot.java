package frc.robot.maps;

import com.chopshop166.chopshoplib.outputs.MockSpeedController;
import com.chopshop166.chopshoplib.outputs.SendableSpeedController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
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
                return new AnalogPotentiometer(1);
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
                return SendableSpeedController.wrap(new WPI_TalonSRX(2));
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
}