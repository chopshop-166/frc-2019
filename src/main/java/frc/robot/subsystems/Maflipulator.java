package frc.robot.subsystems;

import com.chopshop166.chopshoplib.outputs.SendableSpeedController;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.PIDCommand;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class Maflipulator extends Subsystem {

    public enum MaflipulatorSide {
        kFront, kBack;
    }

    //private final static double FRONT_LOWER_ANGLE = 0.94;
    private final static double FRONT_SCORING_ANGLE = 0.88;
    private final static double FLIP_TO_FRONT_POSITION = FRONT_SCORING_ANGLE;
    private final static double FRONT_UPPER_ANGLE = 0.73;
    private final static double FRONT_LOWER_ANGLE = FRONT_SCORING_ANGLE;

    //private final static double BACK_LOWER_ANGLE = .14;
    private final static double BACK_SCORING_ANGLE = 0.19;
    private final static double FLIP_TO_BACK_POSITION = BACK_SCORING_ANGLE;
    private final static double BACK_UPPER_ANGLE = .42;
    private final static double BACK_LOWER_ANGLE = BACK_SCORING_ANGLE;

    private final static double VERTICAL_ANGLE = .55;

    private final static double FLIP_MOTOR_SPEED = 1;
    
    private final static double FLIP_RAISING_SPEED = .75;
    private final static double FLIP_DROPPING_SPEED = .2;

    private final static double DEADBAND = .1;

    private MaflipulatorSide currentPosition;

    private SendableSpeedController flipMotor;
    private Potentiometer anglePot;

    double angleCorrection;
    PIDController anglePID;

    public Maflipulator(final RobotMap.MaflipulatorMap map) {
        super();
        flipMotor = map.getFlipMotor();
        anglePot = map.getMaflipulatorPot();

        anglePID = new PIDController(.01, .0009, 0.0, 0.0, anglePot, (double value) -> {
            angleCorrection = value;
        });

        if (anglePot.get() > VERTICAL_ANGLE)
            currentPosition = MaflipulatorSide.kFront;
        else
            currentPosition = MaflipulatorSide.kBack;

        addChildren();
    }

    public void addChildren() {
        addChild(flipMotor);
        addChild(anglePot);
    }

    @Override
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(manualRotate());
    }

    protected double restrict(double flipSpeed) {
        if (currentPosition == MaflipulatorSide.kFront) {
            if (flipSpeed > 0 && anglePot.get() <= FRONT_UPPER_ANGLE) {
                flipSpeed = 0;
            } else if (flipSpeed < 0 && anglePot.get() >= FRONT_LOWER_ANGLE) {
                flipSpeed = 0;
            }
        } else {
            if (flipSpeed < 0 && anglePot.get() >= BACK_UPPER_ANGLE) {
                flipSpeed = 0;
            } else if (flipSpeed > 0 && anglePot.get() <= BACK_LOWER_ANGLE) {
                flipSpeed = 0;
            }
        }
        SmartDashboard.putNumber("Pot Angle", anglePot.get());
        return flipSpeed;

    }

    public Command manualRotate() {
        // The command is named "Manual Rotate" and requires this subsystem.
        return new Command("Manual Rotate", this) {

            @Override
            protected void execute() {
                double flipSpeed = Robot.xBoxCoPilot.getY(Hand.kLeft) * FLIP_MOTOR_SPEED;
                flipSpeed *= Math.abs(flipSpeed);
                flipSpeed = restrict(flipSpeed);
                if (Math.abs(flipSpeed) < DEADBAND) {
                    flipSpeed = 0;
                }
                flipMotor.set(flipSpeed);
                SmartDashboard.putString("Side", currentPosition.toString());
                SmartDashboard.putNumber("Flip Speed", flipSpeed);
            }

            @Override
            protected boolean isFinished() {
                return false;
            }

        };
    }

    public Command Flip() {
        return new ConditionalCommand("Flip", moveToPosition(FLIP_TO_FRONT_POSITION),
                moveToPosition(FLIP_TO_BACK_POSITION)) {
            @Override
            protected boolean condition() {
                return currentPosition == MaflipulatorSide.kFront;
            }
        };
    }

    public Command crappyFlip() {
        return new Command("Crappy Flip", this) {
            @Override
            protected void execute() {

                if (currentPosition == MaflipulatorSide.kFront) {
                    if (anglePot.get() <= VERTICAL_ANGLE) {
                        flipMotor.set(FLIP_DROPPING_SPEED);
                    } else {
                        flipMotor.set(FLIP_RAISING_SPEED);
                    }
                } else {
                    if (anglePot.get() >= VERTICAL_ANGLE) {
                        flipMotor.set(-FLIP_DROPPING_SPEED);
                    } else {
                        flipMotor.set(-FLIP_RAISING_SPEED);
                    }
                }
            }

            @Override
            protected boolean isFinished() {

                if (anglePot.get() >= FRONT_SCORING_ANGLE && currentPosition == MaflipulatorSide.kBack) {
                    return true;
                }

                if (anglePot.get() <= BACK_SCORING_ANGLE && currentPosition == MaflipulatorSide.kFront) {
                    return true;
                }

                return false;
            }

            @Override
            protected void end() {
                flipMotor.set(0);
                if (currentPosition == MaflipulatorSide.kFront) {
                    currentPosition = MaflipulatorSide.kBack;
                } else {
                    currentPosition = MaflipulatorSide.kFront;
                }
            }
        };
    }

    public Command moveToPosition(double targetPosition) {
        return new PIDCommand("Move to Position", .01, 0.0, 0.0, this) {

            @Override
            protected void initialize() {
                anglePID.reset();
                anglePID.setSetpoint(targetPosition);
                anglePID.enable();
            }

            @Override
            protected boolean isFinished() {
                return anglePID.onTarget() && Math.abs(angleCorrection) < 0.1;
            }

            @Override
            protected void end() {
                flipMotor.set(0);
            }

            @Override
            protected double returnPIDInput() {
                return anglePot.pidGet();
            }

            @Override
            protected void usePIDOutput(double output) {
                double flipSpeed = angleCorrection;
                flipSpeed = restrict(flipSpeed);
                flipMotor.set(flipSpeed);
            }
        };
    }

    public Command goToScoringPosition() {
        return new ConditionalCommand("Go To Scoring Position", moveToPosition(FRONT_SCORING_ANGLE),
                moveToPosition(BACK_SCORING_ANGLE)) {
            @Override
            protected boolean condition() {
                return currentPosition == MaflipulatorSide.kFront;
            }
        };
    }

    public Command PIDPickupPosition() {
        return new ConditionalCommand("Pickup Position", moveToPosition(FRONT_LOWER_ANGLE),
                moveToPosition(BACK_LOWER_ANGLE)) {
            @Override
            protected boolean condition() {
                return currentPosition == MaflipulatorSide.kFront;
            }

        };
    }
}
