package frc.robot.subsystems;

import edu.wpi.first.wpilibj.I2C;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.Subsystem;

public class LiftLEDS extends Subsystem {

    I2C arduino;

    public LiftLEDS() {
        super();

        arduino = new I2C(I2C.Port.kMXP, 1);

    }

    @Override
    public void initDefaultCommand() {

    }

    public Command blue() {
        return new InstantCommand("lift leds blue", this, () -> {
            arduino.writeBulk(new byte[] { 'b' });
        });
    }

}
