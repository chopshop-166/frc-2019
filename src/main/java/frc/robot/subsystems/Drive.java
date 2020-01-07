package frc.robot.subsystems;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

import com.chopshop166.chopshoplib.outputs.SendableSpeedController;
import com.chopshop166.chopshoplib.sensors.PIDGyro;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class Drive extends SubsystemBase {

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
        gyroDrivePID = new PIDController(.01, .0009, 0.0, 0.0);
        drive = new DifferentialDrive(left, right);
        addChildren();

        inst = NetworkTableInstance.getDefault();
        table = inst.getTable("Vision Correction Table");

        SmartDashboard.putData("VISIONNNNN", visionPID());

        setDefaultCommand(demoDrive());
    }

    private void addChildren() {
        SendableRegistry.addChild(this, leftEncoder);
        SendableRegistry.addChild(this, rightEncoder);
        SendableRegistry.addChild(this, drive);
        SendableRegistry.addChild(this, gyro);
    }

    private final double slowTurnSpeed = 0.475;

    private final double driveDeadband = 0.05;

    double gyroCorrection;
    PIDController gyroDrivePID;

    double sandstormSpeed = .2;

    public CommandBase driveNormal() {
        return new RunCommand(() -> {
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
        });
    }

    public CommandBase driveBackwards() {
        return new RunCommand(() -> {
            XboxController c = Robot.driveController;
            double triggerSpeed = c.getTriggerAxis(Hand.kRight) - c.getTriggerAxis(Hand.kLeft);
            double thumbstickSpeed = c.getX(Hand.kLeft);
            if (SmartDashboard.getBoolean("isSpeedLimitHeight", false)) {
                triggerSpeed = Math.max(Math.min(triggerSpeed, .5), -.5);
                thumbstickSpeed = Math.max(Math.min(thumbstickSpeed, .75), -.75);
            }
            drive.arcadeDrive(triggerSpeed, thumbstickSpeed);
        }, this);
    }

    public CommandBase demoDrive() {
        return new RunCommand(() -> {
            XboxController c = Robot.driveController;
            double thumbstickSpeed = 0;
            double triggerSpeed = -c.getTriggerAxis(Hand.kRight) + c.getTriggerAxis(Hand.kLeft);
            triggerSpeed /= 2;

            thumbstickSpeed = c.getX(Hand.kLeft);
            thumbstickSpeed *= .75;

            if (SmartDashboard.getBoolean("isSpeedLimitHeight", false)) {
                triggerSpeed = Math.max(Math.min(triggerSpeed, .5), -.5);
                thumbstickSpeed = Math.max(Math.min(thumbstickSpeed, .75), -.75);
            }
            drive.arcadeDrive(triggerSpeed, thumbstickSpeed);
        }, this);
    }

    public CommandBase copilotDrive() {
        return new RunCommand(() -> {
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
        }, this);
    }

    public CommandBase leftSlowTurn() {
        return new FunctionalCommand(() -> {
        }, () -> {
            drive.arcadeDrive(0, -slowTurnSpeed);
        }, (Boolean interrupted) -> {
            drive.arcadeDrive(0, 0);
        }, () -> false, this);
    }

    public CommandBase rightSlowTurn() {
        return new FunctionalCommand(() -> {
        }, () -> {
            drive.arcadeDrive(0, slowTurnSpeed);
        }, (Boolean interrupted) -> {
            drive.arcadeDrive(0, 0);
        }, () -> false, this);
    }

    public CommandBase goXDistanceForward(double distance) {
        return new CommandBase() {
            {
                addRequirements(Drive.this);
            }

            @Override
            public void initialize() {
                leftEncoder.reset();
                rightEncoder.reset();
            }

            @Override
            public void execute() {
                drive.arcadeDrive(sandstormSpeed, gyroCorrection);
            }

            @Override
            public boolean isFinished() {
                return ((leftEncoder.get() + rightEncoder.get()) / 2 > distance);
            }
        };
    }

    public CommandBase goXDistanceBackward(double distance) {
        return new CommandBase() {
            {
                addRequirements(Drive.this);
            }

            @Override
            public void initialize() {
                leftEncoder.reset();
                rightEncoder.reset();
            }

            @Override
            public void execute() {
                drive.arcadeDrive(-sandstormSpeed, gyroCorrection);
            }

            @Override
            public boolean isFinished() {
                return (Math.abs(leftEncoder.get() + rightEncoder.get()) / 2 > distance);
            }
        };
    }

    public CommandBase align() {
        return new CommandBase() {
            {
                addRequirements(Drive.this);
            }

            @Override
            public void execute() {
                double visionCorrectionFactor = table.getEntry("Vision Correction").getDouble(0);
                boolean visionConfirmation = table.getEntry("Vision Found").getBoolean(false);

                double visionTurnSpeed;

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
            public boolean isFinished() {
                return false;
            }

            @Override
            public void end(boolean interrupted) {
                drive.stopMotor();
            }
        };
    }

    public CommandBase visionPID() {
        PIDController controller = new PIDController(1.8, 0.065, 0.0);
        controller.setTolerance(0.05);
        NetworkTableEntry visionFound = table.getEntry("Vision Found");
        NetworkTableEntry visionCorrection = table.getEntry("Vision Correction");

        DoubleSupplier measurement = () -> {
            if (visionFound.getBoolean(false) == true) {
                return visionCorrection.getDouble(0);
            } else {
                controller.reset();
                return 0.0;
            }
        };

        DoubleConsumer useOutput = (double output) -> {
            drive.arcadeDrive(-.45, -output);
        };

        return new PIDCommand(controller, measurement, 0.0, useOutput, this) {
            @Override
            public boolean isFinished() {
                return controller.atSetpoint();
            }
        };
    }

    public CommandBase extendPiston() {
        return new InstantCommand(() -> {
            climbPiston.set(Value.kForward);
        }, this);
    }

    public CommandBase retractPiston() {
        return new InstantCommand(() -> {
            climbPiston.set(Value.kReverse);
        }, this);
    }

    public CommandBase downOffDrop() {
        return goXDistanceForward(1).andThen(extendPiston()).andThen(goXDistanceForward(1)).andThen(retractPiston())
                .andThen(goXDistanceForward(1));
    }
}
