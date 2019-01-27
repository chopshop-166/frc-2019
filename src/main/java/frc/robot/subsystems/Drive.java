package frc.robot.subsystems;

import com.chopshop166.chopshoplib.commands.CommandChain;
import com.chopshop166.chopshoplib.outputs.SendableSpeedController;
import com.chopshop166.chopshoplib.sensors.Lidar;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class Drive extends Subsystem {

    private SendableSpeedController left;
    private SendableSpeedController right;
    private DoubleSolenoid climbPiston;
    private Lidar lidar;
    private Encoder leftEncoder;
    private Encoder rightEncoder;
    private Gyro gyro;
    private DifferentialDrive drive = new DifferentialDrive(left, right);
    // TODO find path for the cameras

    public Drive(
    final RobotMap.DriveMap map)
    { // NOPMD
        super();
        // Take values that the subsystem needs from the map, and store them in the
        // class
        left = map.getLeft();
        right = map.getRight();
        climbPiston = map.getClimbPiston();
        lidar = map.getLidar();
        leftEncoder = map.getLeftEncoder();
        rightEncoder = map.getRightEncoder();
        gyro = map.getGyro();
    }
    // TODO put numbers here

    double gyroCorrection;

    PIDSource gyroSource = new PIDSource(){
    
        @Override
        public void setPIDSourceType(PIDSourceType pidSource) {
            
        }

    @Override public double pidGet(){return gyro.getAngle();}

    @Override public PIDSourceType getPIDSourceType(){return null;}};

    PIDController gyroDrivePID = new PIDController(.01, .0009, 0.0, 0.0, gyroSource, (double value) -> {
        gyroCorrection = value;
    });

    double sandstormSpeed = .2;

    @Override
    public void initDefaultCommand() {

        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    }

    public Command driveNormal() {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("driveNormal", this) {
            @Override
            protected void initialize() {
                // Called just before this Command runs the first time
            }

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
                drive.arcadeDrive(
                        Robot.driveController.getTriggerAxis(Hand.kRight)
                                - Robot.driveController.getTriggerAxis(Hand.kLeft),
                        Robot.driveController.getX(Hand.kLeft));
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                return false;
            }

            @Override
            protected void end() {
                // Called once after isFinished returns true
            }
        };
    }

    public Command goXDistance(double distance) {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("GoXDistance", this) {
            @Override
            protected void initialize() {
                gyroDrivePID.reset();
                gyroDrivePID.setSetpoint(gyro.getAngle());
                gyroDrivePID.enable();
                leftEncoder.reset();
                rightEncoder.reset();
            }

            @Override
            protected void execute() {
                drive.arcadeDrive(sandstormSpeed, gyroCorrection);
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                if (leftEncoder.get() + rightEncoder.get() / 2 > distance)
                    return true;
                else
                    return false;
            }

            @Override
            protected void end() {
                gyroDrivePID.disable();
            }
        };
    }

    public Command turnXDegrees(double degrees) {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("turnXDegrees", this) {
            @Override
            protected void initialize() {
                gyroDrivePID.reset();
                gyroDrivePID.setSetpoint(degrees);
                gyroDrivePID.enable();
            }

            @Override
            protected void execute() {
                drive.arcadeDrive(0, gyroCorrection);
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                if (gyro.getAngle() == degrees)
                    return true;
                else
                    return false;
            }

            @Override
            protected void end() {
                gyroDrivePID.disable();
            }
        };
    }

    public Command extendPiston() {
        return new InstantCommand("Extend Piston", this, () -> {
            climbPiston.set(Value.kForward);
        });
    }

    public Command retractPiston() {
        return new InstantCommand("Retract Piston", this, () -> {
            climbPiston.set(Value.kReverse);
        });
    }

    public Command score() {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("Score", this) {
            @Override
            protected void initialize() {
                // Called just before this Command runs the first time
            }

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                return false;
            }

            @Override
            protected void end() {
                // Called once after isFinished returns true
            }

            // Called when another command which requires one or more of the same
            // subsystems is scheduled to run
            @Override
            protected void interrupted() {
                end();
            }
        };
    }

    public Command downOffDrop() {
        return new CommandChain("Down off Drop").then(goXDistance(1)).then(extendPiston()).then(goXDistance(1))
                .then(retractPiston()).then(goXDistance(1));

    }
}
