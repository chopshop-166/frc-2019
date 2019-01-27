package frc.robot.maps;

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
        return new LiftMap(){
        
            @Override
            public DigitalInput getUpperLimit() {
                return new DigitalInput(4);
            }
        
            @Override
            public SendableSpeedController getMotor() {
                return SendableSpeedController.wrap(new WPI_TalonSRX(3));
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
                return new Encoder(3,5);
            }
        
            @Override
            public DoubleSolenoid getBrake() {
                return new DoubleSolenoid(2, 7);
            }
        
            @Override
            public SendableSpeedController getArmMotor() {
                return SendableSpeedController.wrap(new WPI_TalonSRX(2));
            }
        };
    }
    // Fill in any methods from the interface here.
}