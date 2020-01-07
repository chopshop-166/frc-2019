package frc.robot.subsystems;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LiftLEDS extends SubsystemBase {

    I2C arduino;

    public LiftLEDS() {
        super();

        arduino = new I2C(I2C.Port.kMXP, 1);

    }

    public InstantCommand blue() {
        return new InstantCommand(() -> {
            arduino.writeBulk(new byte[] { 'b' });
        }, this);
    }

}
