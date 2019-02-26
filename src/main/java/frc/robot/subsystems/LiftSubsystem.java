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
        SmartDashboard.putData("Loading Station", autoMoveLift(Heights.kRocketHatchMid));
        SmartDashboard.putData("Loading Station", autoMoveLift(Heights.kRocketHatchHigh));

    }

    public enum Heights {
        // Loading Station 19"
        kLoadingStation(7.2),
        // Low rocket cargo 27.5"
        kRocketCargoLow(13.4),
        // Middle rocket hatch 47"
        kRocketHatchMid(45.1),
        // Middle rocket cargo 55"
        kRocketCargoMid(63.4),
        // Top rocket hatch 75" (MAX HEIGHT)
        kRocketHatchHigh(92.1),
        // Top rocket cargo 83.5"
        kRocketCargoHigh(0),
        // floor load
        kFloorLoad(0.0),
        // cargo ship cargo 39.75"
        kCargoShipCargo(0),
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

    protected void restrictedMotorSet(double liftSpeed) {
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
                double liftSpeed = -Robot.xBoxCoPilot.getY(Hand.kRight);
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
                if (Math.abs(target.get() - currentHeight) < 1.0
                        || (target.get() > currentHeight && upperLimit.get())) {
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
