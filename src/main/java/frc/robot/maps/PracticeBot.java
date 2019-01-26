package frc.robot.maps;

import com.chopshop166.chopshoplib.outputs.SendableSpeedController;
import com.chopshop166.chopshoplib.sensors.Lidar;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import frc.robot.RobotMap;

public class PracticeBot implements RobotMap {

    @Override
    public DriveMap getDriveMap() {
        return new DriveMap(){
        
            @Override
            public SendableSpeedController getRightMotors() {
                return new SendableSpeedController.wrap(new WPI_TalonSRX(1));
            }
        
            @Override
            public Lidar getLidar() {
                return new Lidar(Port.kOnboard, 0x10);
            }
        
            @Override
            public SendableSpeedController getLeftMotors() {
                return new SendableSpeedController.wrap(new WPI_TalonSRX(2));
            }
        
            @Override
            public Gyro getGyro() {
                return new AnalogGyro(3);
            }
        
            @Override
            public Encoder getEncoder() {
                return new ;
            }
        
            @Override
            public DoubleSolenoid getClimbPiston() {
                return new DoubleSolenoid(4, 5);
            }
        };
    }
    // Fill in any methods from the interface here.
}