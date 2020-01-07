package frc.robot.subsystems;

import com.chopshop166.chopshoplib.sensors.SparkMaxEncoder;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;
import frc.robot.RobotMap;

//when arms piston is extended, the arms are locked in starting position

public class LiftSubsystem extends SubsystemBase {
    private DoubleSolenoid armsPiston;
    private CANSparkMax motor;
    private DoubleSolenoid brake;
    private SparkMaxEncoder heightEncoder;
    private DigitalInput lowerLimit;
    private DigitalInput upperLimit;
    NetworkTableInstance inst;
    NetworkTable table;

    public LiftSubsystem(final RobotMap.LiftMap map) {
        super();
        armsPiston = map.getArmsPiston();
        motor = map.getMotor();
        brake = map.getBrake();
        heightEncoder = new SparkMaxEncoder(motor.getEncoder());
        lowerLimit = map.getLowerLimit();
        upperLimit = map.getUpperLimit();
        armsPiston.set(Value.kForward);
        addChildren();
        registeredCommands();
        setDefaultCommand(moveLift());
    }

    public void addChildren() {
        SendableRegistry.addChild(this, armsPiston);
        SendableRegistry.addChild(this, motor);
        SendableRegistry.addChild(this, brake);
        SendableRegistry.addChild(this, heightEncoder);
        SendableRegistry.addChild(this, lowerLimit);
        SendableRegistry.addChild(this, upperLimit);
    }

    private void registeredCommands() {
        SmartDashboard.putData("Loading Station", goToHeight(Heights.kLoadingStation));
        SmartDashboard.putData("Rocket Hatch Mid", goToHeight(Heights.kRocketHatchMid));
        SmartDashboard.putData("Rocket Hatch High", goToHeight(Heights.kRocketHatchHigh));

        SmartDashboard.putData("Floor Load Cargo", goToHeight(Heights.kFloorLoad));
        SmartDashboard.putData("Rocket Cargo Load", goToHeight(Heights.kRocketCargoLow));
        SmartDashboard.putData("Rocket Cargo Middle", goToHeight(Heights.kRocketCargoMid));
        SmartDashboard.putData("Rocket Cargo High", goToHeight(Heights.kRocketCargoHigh));
        SmartDashboard.putData("Cargo Ship Cargo", goToHeight(Heights.kCargoShipCargo));

        SmartDashboard.putData("Floor Load Cargo Auto", goToHeight(Heights.kFloorLoad));
        SmartDashboard.putData("Rocket Cargo Load Auto", goToHeight(Heights.kRocketCargoLow));
        SmartDashboard.putData("Rocket Cargo Middle Auto", goToHeight(Heights.kRocketCargoMid));
        SmartDashboard.putData("Rocket Cargo High Auto", goToHeight(Heights.kRocketCargoHigh));
        SmartDashboard.putData("Cargo Ship Cargo Auto", goToHeight(Heights.kCargoShipCargo));

        SmartDashboard.putData("Loading Station Auto", autoMoveLift(Heights.kLoadingStation));
        SmartDashboard.putData("Rocket Hatch Mid Auto", autoMoveLift(Heights.kRocketHatchMid));
        SmartDashboard.putData("Rocket Hatch High Auto", autoMoveLift(Heights.kRocketHatchHigh));

        SmartDashboard.putData("Lock Arms", lockArms());

    }

    public enum Heights {
        // Loading Station 19"
        kLoadingStation(5.8),
        // Low rocket cargo 27.5"
        kRocketCargoLow(46),
        // Middle rocket hatch 47"
        kRocketHatchMid(36.1),
        // Middle rocket cargo 55.5"
        kRocketCargoMid(100),
        // Top rocket hatch 75" (MAX HEIGHT)
        kRocketHatchHigh(72),
        // Top rocket cargo 83.5"
        kRocketCargoHigh(0),
        // floor load 0"
        kFloorLoad(0.0),
        // cargo ship cargo 39.75"
        kCargoShipCargo(0),
        // Height limit for speed limiting
        kSpeedLimitHeight(40);

        private double value;

        Heights(final double value) {
            this.value = value;
        }

        public double get() {
            return value;
        }
    }

    protected void restrictedMotorSet(double liftSpeed) {
        if (armsPiston.get() == Value.kForward) {
            liftSpeed = 0;
        }
        if (liftSpeed > 0 && !upperLimit.get()) {
            liftSpeed = 0;
        }
        if (liftSpeed < 0 && !lowerLimit.get()) {
            liftSpeed = 0;
            heightEncoder.reset();
        }
        if (Math.abs(liftSpeed) <= 0.05) {
            brake.set(Value.kForward);
        } else {
            brake.set(Value.kReverse);
        }
        motor.set(liftSpeed);
    }

    private final static double AUTO_LIFT_SPEED_UP = 0.5;
    private final static double AUTO_LIFT_SPEED_DOWN = -0.4;

    public InstantCommand lockArms() {
        return new InstantCommand(() -> {
            armsPiston.set(Value.kForward);
        }, this);
    }

    public InstantCommand deployArms() {
        return new InstantCommand(() -> {
            armsPiston.set(Value.kReverse);
        }, this);
    }

    @Override
    public void periodic() {
        SmartDashboard.putBoolean("isSpeedLimitHeight",
                (heightEncoder.getDistance() > Heights.kSpeedLimitHeight.value));
    }

    protected boolean isAtUpperLimit() {
        return !upperLimit.get();
    }

    protected boolean isAtLowerLimit() {
        return !lowerLimit.get();
    }

    public InstantCommand engageBrake() {
        return new InstantCommand(() -> {
            brake.set(Value.kForward);
        }, this);
    }

    public InstantCommand disengageBrake() {
        return new InstantCommand(() -> {
            brake.set(Value.kReverse);
        }, this);
    }

    public PIDCommand autoMoveLift(Heights target) {
        return new PIDCommand("Auto Move Lift", .0016, 0.0002, 0, 0, this) {
            @Override
            protected void initialize() {
                brake.set(Value.kReverse);
                setSetpoint(target.value);
                getPIDController().setAbsoluteTolerance(4);
            }

            @Override
            protected void usePIDOutput(final double heightCorrection) {
                restrictedMotorSet(heightCorrection);
            }

            @Override
            protected void end() {
                restrictedMotorSet(0);
            }

            @Override
            protected double returnPIDInput() {
                return heightEncoder.get();
            }

            @Override
            protected boolean isFinished() {
                return getPIDController().onTarget();
            }
        };
    }

    public Command moveLift() {
        // The command is named "Move Lift" and requires this subsystem.
        return new Command("Move Lift", this) {
            @Override
            protected void execute() {
                SmartDashboard.putNumber("Lift Height", heightEncoder.getDistance());
                double liftSpeed = Robot.xBoxCoPilot.getTriggerAxis(Hand.kRight)
                        - Robot.xBoxCoPilot.getTriggerAxis(Hand.kLeft);
                if (Math.abs(liftSpeed) <= .1) {
                    liftSpeed = 0;
                }
                restrictedMotorSet(liftSpeed);
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                return false;
            }
        };
    }

    public Command homePos() {
        return goToHeight(Heights.kFloorLoad);
    }

    public Command goToHeight(Heights target) {
        // The command is named "Go to a Specific Height" and requires this subsystem.
        return new Command("Go to a Specific Height", this) {
            @Override
            protected void execute() {
                double currentHeight = heightEncoder.getDistance();
                SmartDashboard.putNumber("Lift Height", currentHeight);
                if (currentHeight < target.get()) {
                    restrictedMotorSet(AUTO_LIFT_SPEED_UP);
                } else {
                    restrictedMotorSet(AUTO_LIFT_SPEED_DOWN);
                }
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                double currentHeight = heightEncoder.getDistance();
                if (Math.abs(target.get() - currentHeight) < 1.0 || (target.get() > currentHeight && !upperLimit.get())
                        || (target.get() < currentHeight && !lowerLimit.get())) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            protected void end() {
                // Called once after isFinished returns true
                restrictedMotorSet(0);
            }
        };
    }

    public Command goToAtLeast(Heights target) {
        // The command is named "Go to a Specific Height" and requires this subsystem.
        return new Command("Go at Least to a Specific Height", this) {
            @Override
            protected void execute() {
                restrictedMotorSet(AUTO_LIFT_SPEED_UP);
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                double currentHeight = heightEncoder.getDistance();
                if (currentHeight > target.get()) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            protected void end() {
                restrictedMotorSet(0);
            }
        };
    }
}
