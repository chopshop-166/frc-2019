package frc.robot.subsystems;

import com.chopshop166.chopshoplib.outputs.SendableSpeedController;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class LiftSubsystem extends Subsystem {

    private SendableSpeedController motor;
    private DoubleSolenoid brake;
    private SendableSpeedController armMotor;
    private Encoder heightEncoder;
    private DigitalInput lowerLimit;
    private DigitalInput upperLimit;
    private Potentiometer manipAngle;

    public LiftSubsystem(final RobotMap.LiftMap map) {
        super();
        motor = map.getMotor();
        brake = map.getBrake();
        armMotor = map.getArmMotor();
        heightEncoder = map.getHeightEncoder();
        lowerLimit = map.getLowerLimit();
        upperLimit = map.getUpperLimit();
        manipAngle = map.getManipAngle();
    }

    enum Heights {
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
        kCargoShipCargo(39.75);

        private double value;

        Heights(final double value) {
            this.value = value;
        }

        public double get() {
            return value;
        }
    }

    @Override
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
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

    public Command moveLift() {
        // The command is named "Move Lift" and requires this subsystem.
        return new Command("Move Lift", this) {
            @Override
            protected void execute() {
                double liftSpeed;
                liftSpeed = Robot.xBoxCoPilot.getY(Hand.kRight);
                // Called repeatedly when this Command is scheduled to run
                if ((upperLimit.get()) && (liftSpeed > 0)) {
                    liftSpeed = 0;
                }
                if ((lowerLimit.get()) && (liftSpeed < 0)) {
                    liftSpeed = 0;
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

    public Command moveArm() {
        // The command is named "Move Arm" and requires this subsystem.
        return new Command("Move Arm", this) {
            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
                armMotor.set(Robot.xBoxCoPilot.getY(Hand.kLeft));
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                return false;
            }
        };
    }

    public Command flipOpp() {
        // The command is named "Lift to Opposite" and requires this subsystem.
        return new Command("Flip to Opposite", this) {
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

    public Command homePos() {
        return goToHeight(Heights.kFloorLoad);
    }

    public Command goToHeight(Heights height) {
        // The command is named "Go to a Specific Height" and requires this subsystem.
        return new Command("Go to a Specific Height", this) {
            @Override
            protected void execute() {
                double currentHeight = heightEncoder.getDistance();
               
                if ((currentHeight < height.get()) && upperLimit.get()) {
                    motor.set(0.0);
                } else if ((currentHeight > height.get()) && lowerLimit.get()) {
                    motor.set(0.0);
                } else if (currentHeight < height.get()) {
                    motor.set(0.3);
                }else {
                    motor.set(-0.3);
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
                motor.set(0.0);
            }
        };
    }
}
