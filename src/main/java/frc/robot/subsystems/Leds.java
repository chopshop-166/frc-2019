package frc.robot.subsystems;

import com.chopshop166.chopshoplib.outputs.DigitalOutputDutyCycle;
import com.mach.LightDrive.Color;
import com.mach.LightDrive.LightDriveCAN;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Leds extends Subsystem {
    LightDriveCAN ldrive_can;
    public final static Color seafoam = new Color(150, 238, 150);
    public final static Color amber = new Color(255, 191, 0);
    public final static Color harlequin = new Color(43, 255, 0);
    public final static Color fuschia = new Color(255, 0, 255);
    private final static int LEFT_BANK = 1;
    private final static int RIGHT_BANK = 2;
    private final static int VISION_BANK = 3;

    public Leds() {
        super();

        ldrive_can = new LightDriveCAN();
        ldrive_can.SetColor(LEFT_BANK, Color.BLUE, 1.0);
        ldrive_can.SetColor(RIGHT_BANK, Color.BLUE, 1.0);

        ldrive_can.Update();
    }

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(setTeamColor(1, 2));
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
                Color color;

                if (isBlueTeam()) {
                    color = Color.BLUE;

                } else {
                    color = Color.RED;
                }
                for (Integer currentBank : banks) {
                    ldrive_can.SetColor(currentBank, color, 1.0);

                }
                ldrive_can.Update();
            }

            @Override
            protected boolean isFinished() {
                return false;
            }
        };
    }

    public Command turnOnGreen(Integer... banks) {
        return new Command("Green On", this) {

            @Override
            protected void execute() {
                for (Integer currentBank : banks) {
                    ldrive_can.SetColor(currentBank, Color.GREEN, 1.0);
                }
                ldrive_can.Update();
            }

            @Override
            protected boolean isFinished() {
                return false;
            }
        };
    }

    public Command turnOnRed(Integer... banks) {
        return new Command("Red On", this) {

            @Override
            protected void execute() {
                for (Integer currentBank : banks) {
                    ldrive_can.SetColor(currentBank, Color.RED, 1.0);
                }
                ldrive_can.Update();
            }

            @Override
            protected boolean isFinished() {
                return false;
            }
        };
    }

    public Command turnOnBlue(Integer... banks) {
        return new Command("Blue On", this) {

            @Override
            protected void execute() {
                for (Integer currentBank : banks) {
                    ldrive_can.SetColor(currentBank, Color.BLUE, 1.0);
                }
                ldrive_can.Update();
            }

            @Override
            protected boolean isFinished() {
                return false;
            }
        };
    }

    public Command killAllLights(Integer... banks) {
        return new Command("Lights Off", this) {

            @Override
            protected void execute() {
                for (Integer currentBank : banks) {
                    ldrive_can.SetColor(currentBank, Color.OFF, 1.0);
                }
                ldrive_can.Update();
            }

            @Override
            protected boolean isFinished() {
                return false;
            }
        };
    }

    public Command blinkLights(Color aColor, int frequency, Integer... banks) {
        return new Command("Blink Lights", this) {
            int counter = 0;
            boolean lightsOn = true;
            Color color;

            @Override

            protected void initialize() {
                counter = 0;
                lightsOn = true;
            }

            @Override
            protected void execute() {
                if ((counter % frequency) == 0) {
                    if (lightsOn == true) {
                        color = Color.OFF;
                        lightsOn = false;
                    } else {
                        color = aColor;
                        lightsOn = true;
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
                return false;
            }
        };
    }

    public Command justBreathe(DigitalOutputDutyCycle color, int frequency) {
        return new Command("Breathe", this) {
            boolean isDutyCycleIncreasing = true;
            double period;
            final double executePeriod = 20 * 0.001; // Approx how often execute is called
            final double dutyCycleChangePerPeriod = 2.0;
            double changeAmount;

            @Override
            protected void initialize() {
                period = (1.0 / frequency);
                changeAmount = dutyCycleChangePerPeriod / ((period / executePeriod));
                color.enablePWM(0);
                isDutyCycleIncreasing = true;
            }

            @Override
            protected void execute() {
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
                return false;
            }

            @Override
            protected void end() {
            }

            @Override
            protected void interrupted() {
                end();
            }
        };
    }

    public Command turnOnVisionLights() {
        return new InstantCommand("Vision Light On", this, () -> {
            ldrive_can.SetColor(VISION_BANK, Color.GREEN, 1);
        });
    }

    public Command turnOffVisionLights() {
        return new InstantCommand("Vision Light Off", this, () -> {
            ldrive_can.SetColor(VISION_BANK, Color.OFF, 1);
        });
    }

    public Command blinkVisionLights(int frequency) {
        return new Command("Blink Vision Lights", this) {
            int counter = 0;
            boolean lightsOn = true;

            @Override
            protected void initialize() {
                counter = 0;
                ldrive_can.SetColor(VISION_BANK, Color.GREEN, 1.0);
                lightsOn = true;
            }

            @Override
            protected void execute() {
                if (counter % frequency == 0) {
                    if (lightsOn == true) {
                        ldrive_can.SetColor(VISION_BANK, Color.OFF, 1.0);
                        lightsOn = false;
                    } else {
                        ldrive_can.SetColor(VISION_BANK, Color.GREEN, 1.0);
                        lightsOn = false;
                    }
                }
                ldrive_can.Update();
                counter++;
            }

            @Override
            protected boolean isFinished() {
                return false;
            }
        };
    }
}
