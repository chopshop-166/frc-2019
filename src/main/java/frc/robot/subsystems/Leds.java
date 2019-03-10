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
        setDefaultCommand(setTeamColor(Arrays.asList(1, 2)));
    }

    private boolean isBlueTeam() {
        Alliance team = DriverStation.getInstance().getAlliance();
        if (team == DriverStation.Alliance.Blue) {
            return true;
        } else {
            return false;
        }
    }

    public Command setTeamColor(List<Integer> v) {

        return new Command("Set team color", this) {

            protected void initialize() {
                // Called just before this Command runs the first time
                if (isBlueTeam()) {
                    Iterator<Integer> it = v.iterator();
                    while (it.hasNext()) {
                        ldrive_can.SetColor(it.next(), Color.BLUE, 1.0);
                    }
                } else {
                    Iterator<Integer> it = v.iterator();
                    while (it.hasNext()) {
                        ldrive_can.SetColor(it.next(), Color.RED, 1.0);
                    }
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

    public Command turnOnGreen(List v) {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("Turn on the color known as verde", this) {

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
                Iterator<Integer> it = v.iterator();
                while (it.hasNext()) {
                    ldrive_can.SetColor(it.next(), Color.GREEN, 1.0);
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

    public Command turnOnRed(List v) {
        return new Command("Turn on the color known as rojo", this) {

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
                Iterator<Integer> it = v.iterator();
                while (it.hasNext()) {
                    ldrive_can.SetColor(it.next(), Color.RED, 1.0);
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

    public Command turnOnBlue(List v) {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("Turn on the color known as azul", this) {

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
                Iterator<Integer> it = v.iterator();
                while (it.hasNext()) {
                    ldrive_can.SetColor(it.next(), Color.BLUE, 1.0);
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

    public Command killAllLights(List v) {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("Turn on the color known as Azul", this) {

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
                Iterator<Integer> it = v.iterator();
                while (it.hasNext()) {
                    ldrive_can.SetColor(it.next(), Color.OFF, 1.0);
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
    public Command blinkLights(List banks, Color color, int frequency) {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("Makes the lights go blink", this) {
            int counter = 0;
            boolean lightsOn = true;
            @Override
            
            protected void initialize(){
                counter = 0;
                Iterator<Integer> it = banks.iterator();
                while (it.hasNext()) {
                    ldrive_can.SetColor(it.next(), color, 1.0);
                }
                lightsOn = true;
            }
            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
                if(counter %  frequency == 0){
                    if(lightsOn == true){
                        Iterator<Integer> it = banks.iterator();
                        while (it.hasNext()) {
                            ldrive_can.SetColor(it.next(), Color.OFF, 1.0);
                        }
                        lightsOn = false;
                    } else {
                        Iterator<Integer> it = banks.iterator();
                        while (it.hasNext()) {
                            ldrive_can.SetColor(it.next(), color, 1.0);
                        }
                        lightsOn = false;
                    }
                    
                }
                
                ldrive_can.Update();
                counter ++;

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
}
