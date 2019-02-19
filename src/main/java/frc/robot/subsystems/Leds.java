package frc.robot.subsystems;

import com.chopshop166.chopshoplib.outputs.DigitalOutputDutyCycle;
import com.mach.LightDrive.*;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class Leds extends Subsystem {
    LightDriveCAN ldrive_can;

    public Leds(final RobotMap map) { // NOPMD
        super();
        // Take values that the subsystem needs from the map, and store them in the
        // class

        LightDriveCAN ldrive_can = new LightDriveCAN();

    }

    @Override
    public void initDefaultCommand() {

    }

    private boolean isBlueTeam() {
        Alliance team = DriverStation.getInstance().getAlliance();
        if (team == DriverStation.Alliance.Blue) {
            return true;
        } else {
            return false;
        }
    }

    public Command setTeamColor(boolean turnOn) {
        return new InstantCommand("turn on team color", this, () -> {
            if (isBlueTeam()) {
                ldrive_can.SetColor(1, Color.BLUE, (float) 0.8);

            } else {
                ldrive_can.SetColor(3, Color.RED, (float) 0.8);
            }
            ldrive_can.Update();
        });
    }

    public Command turnOnGreen(boolean turnOn) {
        return new InstantCommand("turn on verde", this, () -> {
            ldrive_can.SetColor(2, Color.GREEN, (float) 0.8);

            ldrive_can.Update();
        });
    }

    public Command turnOnRed(boolean turnOn) {
        return new InstantCommand("turn on red", this, () -> {
            ldrive_can.SetColor(2, Color.RED, (float) 0.8);

            ldrive_can.Update();
        });
    }

    public Command turnOnBlue(boolean turnOn) {
        return new InstantCommand("turn on azul", this, () -> {
            ldrive_can.SetColor(2, Color.GREEN, (float) 0.8);

            ldrive_can.Update();
        });
    }

    public Command killDaLights(boolean turnOn) {
        return new InstantCommand("mata los lightos", this, () -> {
            ldrive_can.SetColor(2, Color.OFF, (float) 0.8);

            ldrive_can.Update();
        });
    }

    public Command justBreathe(DigitalOutputDutyCycle color, int frequency) {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("Fade", this) {
            boolean isDutyCycleIncreasing = true;
            double period;
            final double executePeriod = 20 * 0.001; // Approx how often execute is called
            final double dutyCycleChangePerPeriod = 2.0;
            double changeAmount;

            @Override
            protected void initialize() {
                // Called just before this Command runs the first time
                period = (1.0 / frequency);
                changeAmount = dutyCycleChangePerPeriod / ((period / executePeriod));
                color.enablePWM(0);
                isDutyCycleIncreasing = true;
            }

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
                if (isDutyCycleIncreasing == true) {
                    color.updateDutyCycle(color.getPWMRate() + changeAmount);
                } else {
                    color.updateDutyCycle(color.getPWMRate() - changeAmount);
                }
                if ((color.getPWMRate() >= 1) || (color.getPWMRate() <= 0)) {
                    isDutyCycleIncreasing = !isDutyCycleIncreasing;
                }

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
}
