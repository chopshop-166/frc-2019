package frc.robot.maps;

import com.chopshop166.chopshoplib.outputs.SendableSpeedController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedController;
import frc.robot.RobotMap;
import frc.robot.subsystems.Manipulator;

public class PracticeBot implements RobotMap {

    @Override
    public ManipulatorMap getManipulatorMap() {
        return new ManipulatorMap(){
        
            @Override
            public SendableSpeedController getrollersMotor() {
                return SendableSpeedController.wrap(new WPI_TalonSRX(2));
            }
        
            @Override
            public SendableSpeedController getpivotPointsMotor() {
                return SendableSpeedController.wrap(new WPI_TalonSRX(3));
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
            public DigitalInput getbackPlateLimitSw() {
                return new DigitalInput(3);
            }
        };
    }
    // Fill in any methods from the interface here.
}