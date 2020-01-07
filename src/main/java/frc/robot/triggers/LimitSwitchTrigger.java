package frc.robot.triggers;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.button.Trigger;;

public class LimitSwitchTrigger extends Trigger {
    private DigitalInput limitSwitch;

    public LimitSwitchTrigger(DigitalInput limitSwitch) {
        this.limitSwitch = limitSwitch;
    }

    @Override
    public boolean get() {
        return !limitSwitch.get();
    }

}