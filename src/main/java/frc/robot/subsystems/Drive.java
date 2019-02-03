package frc.robot.subsystems;

import com.chopshop166.chopshoplib.commands.CommandChain;
import com.chopshop166.chopshoplib.outputs.SendableSpeedController;
import com.chopshop166.chopshoplib.sensors.Lidar;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEntry;
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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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

    public Drive(final RobotMap.DriveMap map) { // NOPMD
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

    PIDSource gyroSource = new PIDSource() {

        @Override
        public void setPIDSourceType(PIDSourceType pidSource) {

        }

        @Override
        public double pidGet() {
            return gyro.getAngle();
        }

        @Override
        public PIDSourceType getPIDSourceType() {
            return null;
        }
    };

    PIDController gyroDrivePID = new PIDController(.01, .0009, 0.0, 0.0, gyroSource, (double value) -> {
        gyroCorrection = value;
    });

    double sandstormSpeed = .2;

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(driveNormal());
    }

    public Command driveNormal() {
        return new Command("driveNormal", this) {

            @Override
            protected void execute() {
                drive.arcadeDrive(
                        Robot.driveController.getTriggerAxis(Hand.kRight)
                                - Robot.driveController.getTriggerAxis(Hand.kLeft),
                        Robot.driveController.getX(Hand.kLeft));
            }

            @Override
            protected boolean isFinished() {
                return false;
            }
        };
    }

    public Command goXDistance(double distance) {
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
                if ((leftEncoder.get() + rightEncoder.get()) / 2 > distance)
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
                return gyroDrivePID.onTarget();
            }

            @Override
            protected void end() {
                gyroDrivePID.disable();
            }
        };
    }

    public Command align() {
        return new Command("turnXDegrees", this) {
            @Override
            protected void initialize() {
                NetworkTableInstance inst = NetworkTableInstance.getDefault();
                NetworkTable table = inst.getTable("Vision Correction Table");
                NetworkTableEntry xEntry;
                xEntry = table.getEntry("Vision Correction");
                gyroDrivePID.reset();
                gyroDrivePID.setSetpoint(xEntry.getDouble(0));
                gyroDrivePID.enable();

            }

            @Override
            protected void execute() {
                drive.arcadeDrive(0, gyroCorrection);
            }

            @Override
            protected boolean isFinished() {
                return gyroDrivePID.onTarget();
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

    public Command downOffDrop() {
        return new CommandChain("Down off Drop").then(goXDistance(1)).then(extendPiston()).then(goXDistance(1))
                .then(retractPiston()).then(goXDistance(1));

    }
}
