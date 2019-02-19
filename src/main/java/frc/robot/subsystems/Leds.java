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

}
