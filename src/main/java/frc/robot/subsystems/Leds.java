package frc.robot.subsystems;

import com.chopshop166.chopshoplib.outputs.DigitalOutputDutyCycle;
import com.mach.LightDrive.*;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;


public class Leds extends Subsystem {
    LightDriveCAN ldrive_can = new LightDriveCAN();

    public Leds(final RobotMap map) { // NOPMD
        super();
        // Take values that the subsystem needs from the map, and store them in the
        // class
        
        
        LightDriveCAN ldrive_can = new LightDriveCAN();

    }
   
    @Override
    public void initDefaultCommand() {
        LightDriveCAN ldrive_can = new LightDriveCAN();

    }
    private boolean isBlueTeam() {
        Alliance team = DriverStation.getInstance().getAlliance();
        if (team == DriverStation.Alliance.Blue) {
            return true;
        } else {
            return false;
        }
    }
    private void setTeamColor(boolean turnOn) {
        if (isBlueTeam()) {
            ldrive_can.SetColor(1, Color.BLUE, (float) 0.8);
        } else {
            ldrive_can.SetColor(3, Color.RED, (float) 0.8);

            }
        }
    
    public Command turnOnGreen() {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("Turn on the color known as verde", this) {

           
            protected void initialize() {
                // Called just before this Command runs the first time
                
            }

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
                ldrive_can.SetColor(2, Color.GREEN, (float) 0.8);
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
            }

            // Called when another command which requires one or more of the same
            // subsystems is scheduled to run
            @Override
            protected void interrupted() {
                end();
            }
        };
    }
    public Command turnOnRed() {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("Turn on the color known as rojo", this) {

           
            protected void initialize() {
                // Called just before this Command runs the first time
                
            }

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
                ldrive_can.SetColor(3, Color.RED, (float) 0.8);
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
            }

            // Called when another command which requires one or more of the same
            // subsystems is scheduled to run
            @Override
            protected void interrupted() {
                end();
            }
        };
    }
    public Command turnOnBlue() {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("Turn on the color known as Azul", this) {

           
            protected void initialize() {
                // Called just before this Command runs the first time
                
            }

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
                ldrive_can.SetColor(1, Color.BLUE, (float) 0.8);
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
            }

            // Called when another command which requires one or more of the same
            // subsystems is scheduled to run
            @Override
            protected void interrupted() {
                end();
            }
        };
    }
    public Command killAllLights() {
        // The command is named "Sample Command" and requires this subsystem.
        return new Command("Turn on the color known as Azul", this) {

           
            protected void initialize() {
                // Called just before this Command runs the first time
                
            }

            @Override
            protected void execute() {
                // Called repeatedly when this Command is scheduled to run
                ldrive_can.SetColor(1, Color.OFF);
                


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
