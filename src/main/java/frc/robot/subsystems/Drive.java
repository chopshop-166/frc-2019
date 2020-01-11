package frc.robot.subsystems;

import static edu.wpi.first.wpilibj2.command.CommandGroupBase.sequence;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

import com.chopshop166.chopshoplib.RobotUtils;
import com.chopshop166.chopshoplib.outputs.SendableSpeedController;
import com.chopshop166.chopshoplib.sensors.PIDGyro;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
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

    public Drive(final RobotMap.DriveMap map) {
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

    private void safeArcadeDrive(double forwardSpeed, double turnSpeed) {
        if (SmartDashboard.getBoolean("isSpeedLimitHeight", false)) {
            forwardSpeed = RobotUtils.clamp(-0.5, 0.5, forwardSpeed);
            turnSpeed = RobotUtils.clamp(-0.75, 0.75, turnSpeed);
        }
        drive.arcadeDrive(forwardSpeed, turnSpeed);
    }

    public RunCommand driveNormal(DoubleSupplier forward, DoubleSupplier turn) {
        return new RunCommand(() -> {
            double triggerSpeed = -forward.getAsDouble();
            double thumbstickSpeed = turn.getAsDouble();
            safeArcadeDrive(triggerSpeed, thumbstickSpeed);
        }, this);
    }

    public RunCommand driveBackwards(DoubleSupplier forward, DoubleSupplier turn) {
        return new RunCommand(() -> {
            double triggerSpeed = forward.getAsDouble();
            double thumbstickSpeed = turn.getAsDouble();
            safeArcadeDrive(triggerSpeed, thumbstickSpeed);
        }, this);
    }

    public RunCommand demoDrive(DoubleSupplier forward, DoubleSupplier turn) {
        return new RunCommand(() -> {
            double triggerSpeed = -forward.getAsDouble() / 2;
            double thumbstickSpeed = turn.getAsDouble() * 0.75;
            safeArcadeDrive(triggerSpeed, thumbstickSpeed);
        }, this);
    }

    public RunCommand copilotDrive(DoubleSupplier forward, DoubleSupplier turn) {
        return new RunCommand(() -> {
            double forwardSpeed = forward.getAsDouble();
            double turnSpeed = turn.getAsDouble();
            safeArcadeDrive(forwardSpeed, turnSpeed);
        }, this);
    }

    public FunctionalCommand leftSlowTurn() {
        return new FunctionalCommand(() -> {
        }, () -> {
            drive.arcadeDrive(0, -slowTurnSpeed);
        }, this::stopCommand, () -> false, this);
    }

    public FunctionalCommand rightSlowTurn() {
        return new FunctionalCommand(() -> {
        }, () -> {
            drive.arcadeDrive(0, slowTurnSpeed);
        }, this::stopCommand, () -> false, this);
    }

    public FunctionalCommand goXDistanceForward(double distance) {
        return new FunctionalCommand(() -> {
            leftEncoder.reset();
            rightEncoder.reset();
        }, () -> {
            drive.arcadeDrive(sandstormSpeed, gyroCorrection);
        }, (Boolean interrupted) -> {
        }, () -> ((leftEncoder.get() + rightEncoder.get()) / 2 > distance), this);
    }

    public CommandBase goXDistanceBackward(double distance) {
        return new FunctionalCommand(() -> {
            leftEncoder.reset();
            rightEncoder.reset();
        }, () -> {
            drive.arcadeDrive(sandstormSpeed, gyroCorrection);
        }, (Boolean interrupted) -> {
        }, () -> (Math.abs(leftEncoder.get() + rightEncoder.get()) / 2 > distance), this);
    }

    public FunctionalCommand align(DoubleSupplier forward) {
        return new FunctionalCommand(() -> {
        }, () -> {
            double visionCorrectionFactor = table.getEntry("Vision Correction").getDouble(0);
            boolean visionConfirmation = table.getEntry("Vision Found").getBoolean(false);

            double visionTurnSpeed;

            if ((visionCorrectionFactor > driveDeadband) && visionConfirmation)
                visionTurnSpeed = 0.3;
            else if ((visionCorrectionFactor < -driveDeadband) && visionConfirmation)
                visionTurnSpeed = -0.3;
            else
                visionTurnSpeed = 0;

            drive.arcadeDrive(forward.getAsDouble(), visionTurnSpeed);
        }, this::stopCommand, () -> false, this);
    }

    public PIDCommand visionPID() {
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

    public InstantCommand extendPiston() {
        return new InstantCommand(() -> {
            climbPiston.set(Value.kForward);
        }, this);
    }

    public InstantCommand retractPiston() {
        return new InstantCommand(() -> {
            climbPiston.set(Value.kReverse);
        }, this);
    }

    public CommandGroupBase downOffDrop() {
        return sequence(goXDistanceForward(1), extendPiston(), goXDistanceForward(1), retractPiston(),
                goXDistanceForward(1));
    }

    private void stopCommand(Boolean interrupted) {
        drive.stopMotor();
    }
}
