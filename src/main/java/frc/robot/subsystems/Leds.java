package frc.robot.subsystems;

import java.util.List;
import java.util.Arrays;
import java.util.Iterator;

import com.chopshop166.chopshoplib.outputs.DigitalOutputDutyCycle;
import com.mach.LightDrive.Color;
import com.mach.LightDrive.LightDriveCAN;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Leds extends Subsystem {
    LightDriveCAN ldrive_can;
    Color seafoam = new Color(150, 238, 150);
    Color amber = new Color(255, 191, 0);
    Color harlequin = new Color(43, 255, 0);
    Color fuschia = new Color(255, 0, 255);
    private final static int leftBank = 1;
    private final static int rightBank = 2;

    public Leds() { // NOPMD
        super();
        // Take values that the subsystem needs from the map, and store them in the
        // class

        ldrive_can = new LightDriveCAN();
        ldrive_can.SetColor(leftBank, Color.BLUE, 1.0);
        ldrive_can.SetColor(rightBank, Color.BLUE, 1.0);

        ldrive_can.Update();
    }

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(setTeamColor());
    }

    private boolean isBlueTeam() {
        Alliance team = DriverStation.getInstance().getAlliance();
        if (team == DriverStation.Alliance.Blue) {
            return true;
        } else {
            return false;
        }
    }

    public Command setTeamColor(Integer... banks) {

        return new Command("Set team color", this) {

            protected void initialize() {
                // Called just before this Command runs the first time
                Color color;

                if (isBlueTeam()) {
                    color = Color.BLUE;

                } else {
                    color = Color.RED;
                }
                for (Integer currentBank : banks) {
                    ldrive_can.SetColor(currentBank, Color.OFF, 1.0);

                }
                ldrive_can.Update();
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                return false;
            }

            @Override
            protected void end() {
                // Called once after isFinished returns true
                // ldrive_can.SetColor(1, Colors.OFF, 0);
                // ldrive_can.SetColor(1, Color.OFF, 0);
                // ldrive_can.Update();
            }

            // Called when another command which requires one or more of the same
            // subsystems is scheduled to run
            @Override
            protected void interrupted() {
                end();
            }

        };
    }

    public Command turnOnGreen(Integer... banks) {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("Turn on the color known as verde", this) {

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
                for (Integer currentBank : banks) {
                    ldrive_can.SetColor(currentBank, Color.GREEN, 1.0);

                }
                ldrive_can.Update();
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                return false;
            }
        };
    }

    public Command turnOnRed(Integer... banks) {
        return new Command("Turn on the color known as rojo", this) {

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
                for (Integer currentBank : banks) {
                    ldrive_can.SetColor(currentBank, Color.BLUE, 1.0);

                }
                ldrive_can.Update();

            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                return false;
            }
        };
    }

    public Command turnOnBlue(Integer... banks) {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("Turn on the color known as azul", this) {

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
                for (Integer currentBank : banks) {
                    ldrive_can.SetColor(currentBank, Color.BLUE, 1.0);

                }
                ldrive_can.Update();

            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                return false;
            }
        };
    }

    public Command killAllLights(Integer... banks) {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("Turn on the color known as Azul", this) {

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
                for (Integer currentBank : banks) {
                    ldrive_can.SetColor(currentBank, Color.OFF, 1.0);

                }
                ldrive_can.Update();
            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                return false;
            }
        };
    }

    public Command blinkLights(Color color, int frequency, Integer... banks) {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("Makes the lights go blink", this) {
            int counter = 0;
            boolean lightsOn = true;
            Color color;

            @Override

            protected void initialize() {
                counter = 0;
                // for (Integer currentBank : banks) {
                // ldrive_can.SetColor(currentBank, color, 1.0);

                // }
                lightsOn = true;
                // Color color;
            }

            @Override
            protected void execute() {
                // Color color;

                // Called repeatedly when this Command is scheduled to run
                if (counter % frequency == 0) {
                    if (lightsOn == true) {
                        color = Color.BLUE;
                        lightsOn = false;
                    } else {
                        color = Color.OFF;
                        lightsOn = false;
                    }

                }
                for (Integer currentBank : banks) {
                    ldrive_can.SetColor(currentBank, color, 1.0);
                }

                ldrive_can.Update();
                counter++;

            }

            @Override
            protected boolean isFinished() {
                // Make this return true when this Command no longer needs to run execute()
                return false;
            }
        };
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

    public Command turnOnVisionLights () {
        return new InstantCommand ("turn on the color known as vision", this, () - > {
            ldrive_can.SetColor(3, Color.GREEN, 1);
        });
    }
    
    public Command turnOffVisionLights () {
        return new InstantCommand ("turn off the color known as vision", this, () - > {
            ldrive_can.SetColor(3, Color.OFF, 1);
        });
    }
    
     public Command blinkVisionLights(int frequency) {
        return new Command("Blink Vision Lights", this) {
            int counter = 0;
            boolean lightsOn = true;

            @Override
            protected void initialize(){
                counter = 0;
                ldrive_can.SetColor(3, Color.GREEN, 1.0);
                lightsOn = true;
            }
            @Override
            protected void execute() {
                if(counter %  frequency == 0){
                    if(lightsOn == true){
                        ldrive_can.SetColor(3, Color.OFF, 1.0);
                        lightsOn = false;
                    } else {
                        ldrive_can.SetColor(3, Color.GREEN, 1.0);
                        lightsOn = false;
                    }
                    
                }
                
                ldrive_can.Update();
                counter ++;

            }

            @Override
            protected boolean isFinished() {
                return false;
            }
        };
    }

}
