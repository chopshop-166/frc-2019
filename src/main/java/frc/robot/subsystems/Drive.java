package frc.robot.subsystems;

import com.chopshop166.chopshoplib.commands.CommandChain;
import com.chopshop166.chopshoplib.outputs.SendableSpeedController;
import com.chopshop166.chopshoplib.sensors.Lidar;
import com.chopshop166.chopshoplib.sensors.PIDGyro;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.PIDCommand;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
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
    private PIDGyro gyro;
    private DifferentialDrive drive;
    NetworkTableInstance inst;
    NetworkTable table;

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
        gyroDrivePID = new PIDController(.01, .0009, 0.0, 0.0, gyro, (double value) -> {
            gyroCorrection = value;
        });
        drive = new DifferentialDrive(left, right);
        addChildren();

        inst = NetworkTableInstance.getDefault();
        table = inst.getTable("Vision Correction Table");

        SmartDashboard.putData("VISIONNNNN", visionPID());
    }

    private void addChildren() {
        addChild(leftEncoder);
        addChild(rightEncoder);
        addChild(drive);
        addChild(gyro);
    }

    private final double visionCorrectionMultiplier = 2;
    private final double visionCorrectionSpeed = 0.2;
    private final double visionCorrectionRange = 0.1;

    private final double slowTurnSpeed = 0.5;

    private final double driveDeadband = 0.05;

    double gyroCorrection;
    PIDController gyroDrivePID;

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
                        +Robot.driveController.getTriggerAxis(Hand.kRight)
                                - Robot.driveController.getTriggerAxis(Hand.kLeft),
                        Robot.driveController.getX(Hand.kLeft));
            }

            @Override
            protected boolean isFinished() {
                return false;
            }
        };
    }

    public Command leftSlowTurn() {
        return new Command("Left Slow Turn", this) {

            @Override
            protected void execute() {
                drive.arcadeDrive(0, -slowTurnSpeed);
            }

            @Override
            protected boolean isFinished() {
                return false;
            }

            @Override
            protected void end() {
                drive.arcadeDrive(0, 0);
            }
        };
    }

    public Command rightSlowTurn() {
        return new Command("Right Slow Turn", this) {

            @Override
            protected void execute() {
                drive.arcadeDrive(0, slowTurnSpeed);
            }

            @Override
            protected boolean isFinished() {
                return false;
            }

            @Override
            protected void end() {
                drive.arcadeDrive(0, 0);
            }
        };
    }

    public Command goXDistanceForward(double distance) {
        return new Command("GoXDistance", this) {
            @Override
            protected void initialize() {
                // gyroDrivePID.reset();
                // gyroDrivePID.setSetpoint(gyro.getAngle());
                // gyroDrivePID.enable();
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
                // gyroDrivePID.disable();
            }
        };
    }

    public Command goXDistanceBackward(double distance) {
        return new Command("GoXDistance", this) {
            @Override
            protected void initialize() {
                // gyroDrivePID.reset();
                // gyroDrivePID.setSetpoint(gyro.getAngle());
                // gyroDrivePID.enable();
                leftEncoder.reset();
                rightEncoder.reset();
            }

            @Override
            protected void execute() {
                drive.arcadeDrive(-sandstormSpeed, gyroCorrection);
            }

            @Override
            protected boolean isFinished() {
                if ((Math.abs(leftEncoder.get() + rightEncoder.get()) / 2 > distance))
                    return true;
                else
                    return false;
            }

            @Override
            protected void end() {
                // gyroDrivePID.disable();
            }
        };
    }

    // public Command turnXDegrees(double degrees) {
    // return new Command("turnXDegrees", this) {
    // @Override
    // protected void initialize() {
    // // gyroDrivePID.reset();
    // // gyroDrivePID.setSetpoint(degrees);
    // // gyroDrivePID.enable();
    // }

    // @Override
    // protected void execute() {
    // drive.arcadeDrive(0, gyroCorrection);
    // }

    // // @Override
    // // protected boolean isFinished() {
    // // return gyroDrivePID.onTarget();
    // // }

    // // @Override
    // // protected void end() {
    // // gyroDrivePID.disable();
    // // }
    // };
    // }

    public Command align() {
        return new Command("align", this) {
            double visionCorrectionFactor = table.getEntry("Vision Correction").getDouble(0);
            boolean visionConfirmation = table.getEntry("Vision Found").getBoolean(false);
            double visionTurnSpeed;

            @Override
            protected void execute() {
                visionCorrectionFactor = table.getEntry("Vision Correction").getDouble(0);
                visionConfirmation = table.getEntry("Vision Found").getBoolean(false);

                // drive.arcadeDrive(0, visionCorrectionMultiplier * visionCorrectionFactor);
                if ((visionCorrectionFactor > driveDeadband) && (visionConfirmation))
                    visionTurnSpeed = 0.3;
                else if ((visionCorrectionFactor < -driveDeadband) && (visionConfirmation))
                    visionTurnSpeed = -0.3;
                else
                    visionTurnSpeed = 0;

                drive.arcadeDrive(Robot.driveController.getTriggerAxis(Hand.kRight)
                        - Robot.driveController.getTriggerAxis(Hand.kLeft), visionTurnSpeed);
            }

            @Override
            protected boolean isFinished() {
                return false;
            }

            @Override
            protected void end() {
                drive.stopMotor();
            }
        };
    }

    public Command visionPID() {
        return new PIDCommand("Vision PID", .7, 0.05, 0.0, this) {
            PIDController visionPIDController;

            @Override
            protected void initialize() {
                visionPIDController = getPIDController();
                visionPIDController.setAbsoluteTolerance(0.05);

            }

            @Override
            protected boolean isFinished() {
                return visionPIDController.onTarget();
            }

            @Override
            protected double returnPIDInput() {
                if (table.getEntry("Vision Found").getBoolean(false) == true) {
                    return table.getEntry("Vision Correction").getDouble(0);
                } else {
                    visionPIDController.reset();
                    return 0;
                }
            }

            @Override
            protected void usePIDOutput(double visionOutput) {
                drive.arcadeDrive(-.5, -visionOutput);
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
        CommandChain retValue = new CommandChain("Down off Drop");
        retValue.then(goXDistanceForward(1)).then(extendPiston()).then(goXDistanceForward(1)).then(retractPiston())
                .then(goXDistanceForward(1));
        return retValue;
    }
}
