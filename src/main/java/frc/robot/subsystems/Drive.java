package frc.robot.subsystems;

import com.chopshop166.chopshoplib.commands.CommandChain;
import com.chopshop166.chopshoplib.outputs.SendableSpeedController;
import com.chopshop166.chopshoplib.sensors.PIDGyro;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.XboxController;
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

    private final double slowTurnSpeed = 0.475;

    private final double driveDeadband = 0.05;

    double gyroCorrection;
    PIDController gyroDrivePID;

    double sandstormSpeed = .2;

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(demoDrive());
    }

    public Command driveNormal() {
        return new Command("driveNormal", this) {

            @Override
            protected void execute() {
                XboxController c = Robot.driveController;
                double triggerSpeed = 0;
                double thumbstickSpeed = 0;
                triggerSpeed = -c.getTriggerAxis(Hand.kRight) + c.getTriggerAxis(Hand.kLeft);
                thumbstickSpeed = c.getX(Hand.kLeft);
                if (SmartDashboard.getBoolean("isSpeedLimitHeight", false)) {
                    triggerSpeed = Math.max(Math.min(triggerSpeed, .5), -.5);
                    thumbstickSpeed = Math.max(Math.min(thumbstickSpeed, .75), -.75);
                }
                drive.arcadeDrive(triggerSpeed, thumbstickSpeed);
            }

            @Override
            protected boolean isFinished() {
                return false;
            }
        };
    }

    public Command driveBackwards() {
        return new Command("Drive Backwards", this) {

            @Override
            protected void execute() {
                XboxController c = Robot.driveController;
                double triggerSpeed = 0;
                double thumbstickSpeed = 0;
                triggerSpeed = c.getTriggerAxis(Hand.kRight) - c.getTriggerAxis(Hand.kLeft);
                thumbstickSpeed = c.getX(Hand.kLeft);
                if (SmartDashboard.getBoolean("isSpeedLimitHeight", false)) {
                    triggerSpeed = Math.max(Math.min(triggerSpeed, .5), -.5);
                    thumbstickSpeed = Math.max(Math.min(thumbstickSpeed, .75), -.75);
                }
                drive.arcadeDrive(triggerSpeed, thumbstickSpeed);
            }

            @Override
            protected boolean isFinished() {
                return false;
            }
        };
    }

    public Command demoDrive() {
        return new Command("demoDrive", this) {

            @Override
            protected void execute() {
                XboxController c = Robot.driveController;
                double triggerSpeed = 0;
                double thumbstickSpeed = 0;
                triggerSpeed = -c.getTriggerAxis(Hand.kRight) + c.getTriggerAxis(Hand.kLeft);
                triggerSpeed /= 2;

                thumbstickSpeed = c.getX(Hand.kLeft);
                thumbstickSpeed *= .75;

                if (SmartDashboard.getBoolean("isSpeedLimitHeight", false)) {
                    triggerSpeed = Math.max(Math.min(triggerSpeed, .5), -.5);
                    thumbstickSpeed = Math.max(Math.min(thumbstickSpeed, .75), -.75);
                }
                drive.arcadeDrive(triggerSpeed, thumbstickSpeed);
            }

            @Override
            protected boolean isFinished() {
                return false;
            }
        };
    }

    public Command copilotDrive() {
        return new Command("copilotDrive", this) {

            @Override
            protected void execute() {
                XboxController c = Robot.xBoxCoPilot;
                double forwardSpeed = 0;
                double turnSpeed = 0;
                forwardSpeed = c.getY(Hand.kLeft);
                turnSpeed = c.getX(Hand.kLeft);
                if (SmartDashboard.getBoolean("isSpeedLimitHeight", false)) {
                    forwardSpeed = Math.max(Math.min(forwardSpeed, .5), -.5);
                    turnSpeed = Math.max(Math.min(turnSpeed, .75), -.75);
                }
                drive.arcadeDrive(forwardSpeed, turnSpeed);
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
            }
        };
    }

    public Command goXDistanceBackward(double distance) {
        return new Command("GoXDistance", this) {
            @Override
            protected void initialize() {
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
            }
        };
    }

    public Command align() {
        return new Command("align", this) {
            double visionCorrectionFactor = table.getEntry("Vision Correction").getDouble(0);
            boolean visionConfirmation = table.getEntry("Vision Found").getBoolean(false);
            double visionTurnSpeed;

            @Override
            protected void execute() {
                visionCorrectionFactor = table.getEntry("Vision Correction").getDouble(0);
                visionConfirmation = table.getEntry("Vision Found").getBoolean(false);

                if ((visionCorrectionFactor > driveDeadband) && visionConfirmation)
                    visionTurnSpeed = 0.3;
                else if ((visionCorrectionFactor < -driveDeadband) && visionConfirmation)
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
        return new PIDCommand("Vision PID", 1.8, 0.065, 0.0, this) {
            PIDController visionPIDController;
            NetworkTableEntry visionFound;
            NetworkTableEntry visionCorrection;

            @Override
            protected void initialize() {
                visionPIDController = getPIDController();
                visionPIDController.setAbsoluteTolerance(0.05);
                visionFound = table.getEntry("Vision Found");
                visionCorrection = table.getEntry("Vision Correction");

            }

            @Override
            protected boolean isFinished() {
                return visionPIDController.onTarget();
            }

            @Override
            protected double returnPIDInput() {
                if (visionFound.getBoolean(false) == true) {
                    return visionCorrection.getDouble(0);
                } else {
                    visionPIDController.reset();
                    return 0;
                }
            }

            @Override
            protected void usePIDOutput(double visionOutput) {
                drive.arcadeDrive(-.45, -visionOutput);
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
