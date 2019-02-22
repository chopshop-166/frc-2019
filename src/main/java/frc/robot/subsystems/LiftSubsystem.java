package frc.robot.subsystems;

import com.chopshop166.chopshoplib.sensors.SparkMaxCounter;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.PIDCommand;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class LiftSubsystem extends Subsystem {
    private CANSparkMax motor;
    private DoubleSolenoid brake;
    private SparkMaxCounter heightEncoder;
    private DigitalInput lowerLimit;
    private DigitalInput upperLimit;

    public LiftSubsystem(final RobotMap.LiftMap map) {
        super();
        motor = map.getMotor();
        brake = map.getBrake();
        heightEncoder = new SparkMaxCounter(motor.getEncoder());
        lowerLimit = map.getLowerLimit();
        upperLimit = map.getUpperLimit();
        addChildren();
        registeredCommands();
    }

    public void addChildren() {
        addChild(motor);
        addChild(brake);
        addChild(heightEncoder);
        addChild(lowerLimit);
        addChild(upperLimit);
    }

    private void registeredCommands() {
        SmartDashboard.putData("Loading Station", autoMoveLift(Heights.kLoadingStation));
        SmartDashboard.putData("Loading Station", autoMoveLift(Heights.kRocketCargoMid));
    }

    public enum Heights {
        // Loading Station
        kLoadingStation(19.0),
        // Low rocket cargo
        kRocketCargoLow(27.5),
        // Middle rocket hatch
        kRocketHatchMid(47.0),
        // Middle rocket cargo
        kRocketCargoMid(55.5),
        // Top rocket hatch
        kRocketHatchHigh(63.0),
        // Top rocket cargo
        kRocketCargoHigh(83.5),
        // floor load
        kFloorLoad(0.0),
        // cargo ship cargo
        kCargoShipCargo(39.75),
        // Height needed to flip over
        kLiftFlipHeight(25);

        private double value;

        Heights(final double value) {
            this.value = value;
        }

        public double get() {
            return value;
        }
    }

    private final static double AUTO_LIFT_SPEED_UP = 0.5;
    private final static double AUTO_LIFT_SPEED_DOWN = -0.05;

    @Override
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(moveLift());
    }

    protected boolean isAtUpperLimit() {
        return !upperLimit.get();
    }

    protected boolean isAtLowerLimit() {
        return !lowerLimit.get();
    }

    public Command engageBrake() {
        return new InstantCommand("Engage Brake", this, () -> {
            brake.set(Value.kForward);
        });
    }

    public Command disengageBrake() {
        return new InstantCommand("Disengage Brake", this, () -> {
            brake.set(Value.kReverse);
        });
    }

    public Command autoMoveLift(Heights target) {
        return new PIDCommand("Auto Move Lift", 0, 0, 0, 0, this) {
            @Override
            protected void initialize() {
                brake.set(Value.kReverse);

                setSetpoint(target.value);

            }

            @Override
            protected void usePIDOutput(final double heightCorrection) {
                double liftSpeed = heightCorrection;
                if (isAtUpperLimit() && liftSpeed > 0) {
                    liftSpeed = 0;
                }
                if (isAtLowerLimit() && liftSpeed < 0) {
                    liftSpeed = 0;
                }
                motor.set(liftSpeed);
            }

            @Override
            protected void end() {
                brake.set(Value.kForward);
                motor.set(0);
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
                SmartDashboard.putNumber("Lift Thing", 5);
                // needs to go 25 arbitrary units up to flip
                double liftSpeed;
                liftSpeed = -Robot.xBoxCoPilot.getY(Hand.kRight);

                if (Math.abs(liftSpeed) <= .1) {
                    liftSpeed = 0;
                }

                // SmartDashboard.putNumber("Lift Faults", motor.getFaults());
                SmartDashboard.putNumber("Lift Before", liftSpeed);
                // Called repeatedly when this Command is scheduled to run
                if (isAtUpperLimit() && (liftSpeed > 0)) {
                    liftSpeed = 0;
                }
                if (isAtLowerLimit() && liftSpeed < 0) {
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

    public Command goToHeight(Heights height) {
        // The command is named "Go to a Specific Height" and requires this subsystem.
        return new Command("Go to a Specific Height", this) {
            @Override
            protected void execute() {

                double currentHeight = heightEncoder.getDistance();
                brake.set(Value.kReverse);

                if (currentHeight < height.get() && isAtUpperLimit()) {
                    motor.set(0.0);
                } else if (currentHeight > height.get() && isAtLowerLimit()) {
                    motor.set(0.0);
                } else if (currentHeight < height.get()) {
                    motor.set(AUTO_LIFT_SPEED_UP);
                } else {
                    motor.set(AUTO_LIFT_SPEED_DOWN);
                }
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                double currentHeight = heightEncoder.getDistance();
                if (Math.abs(height.get() - currentHeight) < 1.0) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            protected void end() {
                // Called once after isFinished returns true
                brake.set(Value.kForward);
                motor.set(0.0);
            }
        };
    }
}
